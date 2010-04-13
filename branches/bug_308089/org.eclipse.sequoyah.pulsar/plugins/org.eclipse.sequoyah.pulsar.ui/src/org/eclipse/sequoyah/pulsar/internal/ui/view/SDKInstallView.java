/**
 * Copyright (c) 2009 Nokia Corporation and/or its subsidiary(-ies).
 * All rights reserved.
 * This component and the accompanying materials are made available
 * under the terms of the License "Eclipse Public License v1.0"
 * which accompanies this distribution, and is available
 * at the URL "http://www.eclipse.org/legal/epl-v10.html".
 *
 * Contributors:
 * 	David Dubrow
 *  David Marques (Motorola) - Refactoring view UI.
 *  David Marques (Motorola) - Refactoring to use label provider.
 *  Euclides Neto (Motorola) - Added refresh functionality and change the install icon.
 *  David Marques (Motorola) - Adding installation environment support.
 *  Euclides Neto (Motorola) - Added details functionality.
 *  Henrique Magalhaes(Motorola)/
 *  Euclides Neto (Motorola) - Added uninstall action.
 *  Henrique Magalhaes (Motorola) - Fixing update repository problem.
 *  Euclides Neto (Motorola) - Changed the method to refresh instead of remove SDK repositories.
 *  Euclides Neto (Motorola) - Adding SDK Category description support.
 */

package org.eclipse.sequoyah.pulsar.internal.ui.view;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.equinox.internal.p2.discovery.Catalog;
import org.eclipse.equinox.internal.p2.discovery.DiscoveryCore;
import org.eclipse.equinox.internal.p2.discovery.compatibility.BundleDiscoveryStrategy;
import org.eclipse.equinox.internal.p2.discovery.compatibility.RemoteBundleDiscoveryStrategy;
import org.eclipse.equinox.internal.p2.ui.discovery.DiscoveryUi;
import org.eclipse.equinox.internal.p2.ui.discovery.wizards.CatalogConfiguration;
import org.eclipse.equinox.internal.p2.ui.discovery.wizards.CatalogViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sequoyah.pulsar.Activator;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.BaseSelectionListenerAction;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.part.ViewPart;

@SuppressWarnings("restriction")
public class SDKInstallView extends ViewPart {

    private class InstallAction extends BaseSelectionListenerAction {

        protected InstallAction() {
            super(Messages.SDKInstallView_InstallActionLabel);
            setToolTipText(Messages.SDKInstallView_InstallActionToolTip);
            setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
                    .getImageDescriptor(ISharedImages.IMG_OBJ_ADD));
        }

        protected boolean updateSelection(IStructuredSelection selection) {
        	return selection != null && !selection.isEmpty();
        }

        @Override
        public void run() {
        	DiscoveryUi.install(viewer.getCheckedItems(), new ProgressMonitorDialog(getSite().getShell()));
        }
    }

    private class UninstallAction extends BaseSelectionListenerAction {

        protected UninstallAction() {
            super(Messages.SDKInstallView_UninstallActionLabel);
            setToolTipText(Messages.SDKInstallView_UninstallActionToolTip);
            setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
                    .getImageDescriptor(ISharedImages.IMG_ETOOL_DELETE));
        }

        protected boolean updateSelection(IStructuredSelection selection) {
        	// TODO return true if there are any installed sdks
        	return false;
        }

        @Override
        public void run() {
        	// TODO implement using ListDialog??
        }
    }

    private class RefreshAction extends Action {

        protected RefreshAction() {
            super(Messages.SDKInstallView_RefreshActionLabel);
            setToolTipText(Messages.SDKInstallView_RefreshActionToolTip);
            setImageDescriptor(getLocalImageDescriptor("icons/refresh_enabled.gif")); //$NON-NLS-1$
            setDisabledImageDescriptor(getLocalImageDescriptor("icons/refresh_disabled.gif")); //$NON-NLS-1$
        }

        @Override
        public void run() {
            refreshSDKs();
        }
    }

    private class DetailsAction extends BaseSelectionListenerAction {

        protected DetailsAction() {
            super(Messages.SDKInstallView_DetailsActionLabel);
            setToolTipText(Messages.SDKInstallView_DetailsActionToolTip);
            setImageDescriptor(getLocalImageDescriptor("icons/details_enabled.gif")); //$NON-NLS-1$
            setDisabledImageDescriptor(getLocalImageDescriptor("icons/details_disabled.gif")); //$NON-NLS-1$
        }

        protected boolean updateSelection(IStructuredSelection selection) {
        	// TODO return true if there are any installed sdks
        	return false;
        }

        @Override
        public void run() {
        	// TODO implement using ListDialog to select sdk and then get details?

        	PreferenceDialog dialog = PreferencesUtil
                    .createPreferenceDialogOn(
                            viewer.getControl().getShell(),
                            "org.eclipse.sequoyah.ui.preferences.deviceManagementPreferencePage", //$NON-NLS-1$
                            new String[] { "org.eclipse.sequoyah.ui.preferences.deviceManagementPreferencePage" }, //$NON-NLS-1$
                            null);
            dialog.open();
        }
    }

    /**
     * Returns a local image descriptor.
     * 
     * @param path The local path to the image.
     * @return a reference to the image descriptor.
     */
    private ImageDescriptor getLocalImageDescriptor(String path) {
        ImageDescriptor image = null;
        URL url = FileLocator.find(Activator.getDefault().getBundle(),
                new Path(path), null);
        if (url != null) {
            image = ImageDescriptor.createFromURL(url);
        }
        return image;
    }

    private CatalogViewer viewer;
    private InstallAction installAction;
    private RefreshAction refreshAction;
    private DetailsAction detailsAction;
    private UninstallAction uninstallAction;

    @Override
    public void createPartControl(Composite parent) {
        GridLayout layout = new GridLayout(2, false);
        layout.marginHeight = 0x00;
        layout.marginWidth = 0x00;
        parent.setLayout(layout);

		viewer = new CatalogViewer(getCatalog(), getSite(), getSite().getWorkbenchWindow(), getConfiguration());
		viewer.createControl(parent);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(viewer.getControl());

        // Updates the view with available SDKs
        refreshSDKs();

        makeActions();
        contributeToActionBars();
        
    }

	private CatalogConfiguration getConfiguration() {
		CatalogConfiguration configuration = new CatalogConfiguration();
		configuration.setShowTagFilter(false);
		return configuration;
	}

	private Catalog getCatalog() {
		Catalog catalog = new Catalog();
		catalog.setEnvironment(DiscoveryCore.createEnvironment());
		catalog.setVerifyUpdateSiteAvailability(false);

		// look for descriptors from installed bundles
		catalog.getDiscoveryStrategies().add(new BundleDiscoveryStrategy());

		RemoteBundleDiscoveryStrategy remoteDiscoveryStrategy = new RemoteBundleDiscoveryStrategy();
		remoteDiscoveryStrategy.setDirectoryUrl("http://download.eclipse.org/sequoyah/pulsar/discovery.xml");
		catalog.getDiscoveryStrategies().add(remoteDiscoveryStrategy);

		return catalog;
	}

	/**
     * Refreshes the SDKs list on Mobile SDKs view.
     */
    protected void refreshSDKs() {
        Job job = new Job(Messages.SDKInstallView_UpdatingInstallersJobTitle) {
            @Override
            public IStatus run(IProgressMonitor monitor) {
                Display.getDefault().asyncExec(new Runnable() {
                    public void run() {
                        viewer.updateCatalog();
                    }
                });
                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }

    private void contributeToActionBars() {
        IActionBars bars = getViewSite().getActionBars();
        fillLocalPullDown(bars.getMenuManager());
        fillLocalToolBar(bars.getToolBarManager());
    }

    private void fillLocalPullDown(IMenuManager manager) {
        manager.add(installAction);
        manager.add(detailsAction);
        manager.add(uninstallAction);
        manager.add(refreshAction);
    }

    private void fillLocalToolBar(IToolBarManager manager) {
        manager.add(installAction);
        manager.add(detailsAction);
        manager.add(uninstallAction);
        manager.add(refreshAction);
    }

    private void makeActions() {
        installAction = new InstallAction();
        installAction.selectionChanged(viewer.getSelection());

        detailsAction = new DetailsAction();

        uninstallAction = new UninstallAction();

        refreshAction = new RefreshAction();

		viewer.addSelectionChangedListener(installAction);
		viewer.addSelectionChangedListener(detailsAction);
		viewer.addSelectionChangedListener(uninstallAction);
    }
    
    public void dispose() {
		viewer.removeSelectionChangedListener(installAction);
		viewer.removeSelectionChangedListener(detailsAction);
		viewer.removeSelectionChangedListener(uninstallAction);
        super.dispose();
    }

    @Override
	public void setFocus() {
	}
}
