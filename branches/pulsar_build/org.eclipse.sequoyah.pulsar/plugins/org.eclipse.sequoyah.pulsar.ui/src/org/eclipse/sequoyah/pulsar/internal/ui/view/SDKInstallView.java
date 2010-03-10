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
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.equinox.internal.p2.ui.ProvUI;
import org.eclipse.equinox.internal.p2.ui.viewers.StructuredViewerProvisioningListener;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeColumnViewerLabelProvider;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeNodeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.sequoyah.pulsar.Activator;
import org.eclipse.sequoyah.pulsar.internal.provisional.core.IInstallationInfoProvider;
import org.eclipse.sequoyah.pulsar.internal.provisional.core.ISDK;
import org.eclipse.sequoyah.pulsar.internal.provisional.core.ISDKCategory;
import org.eclipse.sequoyah.pulsar.internal.provisional.core.ISDKRepository;
import org.eclipse.sequoyah.pulsar.internal.provisional.core.QuickInstallCore;
import org.eclipse.sequoyah.pulsar.internal.provisional.core.ISDK.EState;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
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
            boolean toReturn = false;
            // Just enable the action if the SDK state is not INSTALLED
            if (getSelectedSDK() != null) {
                toReturn = !getSelectedSDK().getState()
                        .equals(EState.INSTALLED);
            }
            return toReturn;
        }

        @Override
        public void run() {
            installSelectedSDK();
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
            boolean toReturn = false;
            // Just enable the action if the SDK state is INSTALLED
            if (getSelectedSDK() != null) {
                toReturn = getSelectedSDK().getState().equals(EState.INSTALLED);
            }
            return toReturn;
        }

        @Override
        public void run() {
            uninstallSelectedSDK();
        }
    }

    private class RefreshAction extends BaseSelectionListenerAction {

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
            boolean toReturn = false;
            // Just enable the action if the SDK state is INSTALLED
            if (getSelectedSDK() != null) {
                toReturn = getSelectedSDK().getState().equals(EState.INSTALLED);
            }
            return toReturn;
        }

        @Override
        public void run() {
            PreferenceDialog dialog = PreferencesUtil
                    .createPreferenceDialogOn(
                            viewer.getTree().getShell(),
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

    private TreeViewer viewer;
    private InstallAction installAction;
    private RefreshAction refreshAction;
    private DetailsAction detailsAction;
    private UninstallAction uninstallAction;

    private Action doubleClickAction;
    private StructuredViewerProvisioningListener listener;
    private SDKInstallItemViewer itemViewer;

    public SDKInstallView() {
    }

    @Override
    public void createPartControl(Composite parent) {
        GridLayout layout = new GridLayout(2, false);
        layout.marginHeight = 0x00;
        layout.marginWidth = 0x00;
        parent.setLayout(layout);

        viewer = new TreeViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        Tree tree = viewer.getTree();
        tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        TreeViewerColumn installersColumn = new TreeViewerColumn(viewer,
                SWT.LEFT);
        Display display = getViewSite().getShell().getDisplay();
        installersColumn.setLabelProvider(new TreeColumnViewerLabelProvider(
                new InstallersLabelProvider(display)));
        installersColumn.getColumn().setText(
                Messages.SDKInstallView_InstallersColLabel);

        TreeViewerColumn statusColumn = new TreeViewerColumn(viewer, SWT.LEFT);
        statusColumn.setLabelProvider(new StatusLabelProvider());
        statusColumn.getColumn()
                .setText(Messages.SDKInstallView_StatusColLabel);

        TreeViewerColumn versionColumn = new TreeViewerColumn(viewer, SWT.LEFT);
        versionColumn.setLabelProvider(new VersionLabelProvider());
        versionColumn.getColumn().setText(Messages.SDKInstallView_VersionLabel);

        viewer.setContentProvider(new TreeNodeContentProvider());
        viewer.getTree().setHeaderVisible(true);
        viewer.setSorter(new ViewerSorter() {
            @Override
            public int compare(Viewer viewer, Object e1, Object e2) {
                return getTreeNodeDisplayName(e1).compareToIgnoreCase(
                        getTreeNodeDisplayName(e2));
            }
        });
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                installAction.selectionChanged(event);
                detailsAction.selectionChanged(event);
                uninstallAction.selectionChanged(event);
                updateSDKItemViewer();
            }
        });
        viewer.addFilter(new InstallationEnvironmentFilter());

        // Updates the view with available SDKs
        refreshSDKs();

        makeActions();
        hookDoubleClickAction();
        contributeToActionBars();
        addProvisioningListener();
    }

    /**
     * Refreshes the SDKs list on Mobile SDKs view.
     */
    protected void refreshSDKs() {
        Job job = new Job(Messages.SDKInstallView_UpdatingInstallersJobTitle) {
            @Override
            public IStatus run(IProgressMonitor monitor) {
                refreshSDKRepositories();
                final TreeNode[] treeNodes = createTreeNodes(monitor);
                Display.getDefault().asyncExec(new Runnable() {
                    public void run() {
                        viewer.getTree().removeAll();
                        viewer.setInput(treeNodes);
                        viewer.expandAll();
                        packColumns();
                    }
                });
                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }

    /**
     * Removes all sdk repositories.
     */
    private void refreshSDKRepositories() {
        P2InstallerUI installer = (P2InstallerUI) P2InstallerUI.getInstance();
        installer.refreshSDKRepositories(QuickInstallCore.getInstance()
                .getSDKRepositories());
    }

    /**
     * Updates the {@link SDKInstallItemViewer} instance contents.
     */
    private void updateSDKItemViewer() {
        Composite main = this.viewer.getTree().getParent();
        if (main == null) {
            return;
        }

        Object item = this.getSelectedItem();
        if (item != null) {
            if (itemViewer == null || itemViewer.isDisposed()) {
                itemViewer = new SDKInstallItemViewer(main);
                GridData gridData = new GridData(SWT.FILL, SWT.FILL, false,
                        true);
                gridData.minimumWidth = 350;
                gridData.widthHint = 350;
                itemViewer.setLayoutData(gridData);
            }
            ISDKInstallItemLabelProvider labelProvider = getLabelProvider(item);
            itemViewer.setLabelProvider(labelProvider);
            itemViewer.setInput(item);
        } else {
            if (itemViewer != null && !itemViewer.isDisposed()) {
                itemViewer.dispose();
            }
        }
        main.layout(true);
    }

    /**
     * Gets an {@link ISDKInstallItemLabelProvider} instance for the specified
     * {@link Object} in order to display it into the
     * {@link SDKInstallItemViewer}.
     * 
     * @param item target object.
     * @return an {@link ISDKInstallItemLabelProvider} instance.
     */
    private ISDKInstallItemLabelProvider getLabelProvider(Object item) {
        ISDKInstallItemLabelProvider result = null;
        if (item instanceof IInstallationInfoProvider) {
            result = new InstallationInfoLabelProvider();
        }
        return result;
    }

    private TreeNode[] createTreeNodes(IProgressMonitor monitor) {
        Collection<TreeNode> treeNodes = new ArrayList<TreeNode>();
        Collection<ISDKRepository> repositories = QuickInstallCore
                .getInstance().getSDKRepositories();
        monitor.beginTask("", repositories.size()); //$NON-NLS-1$
        monitor.subTask(Messages.SDKInstallView_GettingRepoInfoMessage);
        for (ISDKRepository repository : repositories) {
            treeNodes.add(createRepositoryTreeNode(repository, monitor));
        }
        monitor.done();
        return (TreeNode[]) treeNodes.toArray(new TreeNode[treeNodes.size()]);
    }

    private TreeNode createRepositoryTreeNode(ISDKRepository repository,
            IProgressMonitor monitor) {
        // create a new repository node
        TreeNode repositoryNode = new TreeNode(repository);
        // a map of categories to sdks in that category
        Map<ISDKCategory, Collection<ISDK>> categoryToSDKListMap = new LinkedHashMap<ISDKCategory, Collection<ISDK>>();
        // the current child list for the repository node
        Collection<TreeNode> childList = new ArrayList<TreeNode>();
        // pass through sdks, adding uncategorized sdk and category lists
        Collection<ISDK> sdks = repository.getSDKs(monitor);
        for (ISDK sdk : sdks) {
            ISDKCategory category = sdk.getCategory();
            if (category != null) {
                if (!categoryToSDKListMap.containsKey(category))
                    categoryToSDKListMap.put(category, new ArrayList<ISDK>());
                categoryToSDKListMap.get(category).add(sdk);
            } else {
                addNewTreeNode(childList, repositoryNode, sdk);
            }
        }
        // pass through category lists, adding categorized sdks
        for (ISDKCategory category : categoryToSDKListMap.keySet()) {
            TreeNode categoryNode = addNewTreeNode(childList, repositoryNode,
                    category);
            Collection<ISDK> childSdks = categoryToSDKListMap.get(category);
            Collection<TreeNode> sdkNodes = new ArrayList<TreeNode>();
            for (ISDK sdk : childSdks) {
                addNewTreeNode(sdkNodes, categoryNode, sdk);
            }
            categoryNode.setChildren((TreeNode[]) sdkNodes
                    .toArray(new TreeNode[sdkNodes.size()]));
        }

        repositoryNode.setChildren((TreeNode[]) childList
                .toArray(new TreeNode[childList.size()]));

        monitor.worked(1);
        return repositoryNode;
    }

    private static TreeNode addNewTreeNode(Collection<TreeNode> nodes,
            TreeNode parentNode, Object data) {
        TreeNode node = new TreeNode(data);
        node.setParent(parentNode);
        if (nodes != null)
            nodes.add(node);
        return node;
    }

    private String getTreeNodeDisplayName(Object treeNode) {
        Object element = ((TreeNode) treeNode).getValue();
        if (element instanceof ISDKCategory) {
            return ((ISDKCategory) element).getName();
        } else if (element instanceof ISDK) {
            return ((ISDK) element).getName();
        } else if (element instanceof String) {
            return (String) element;
        }

        return ""; //$NON-NLS-1$
    }

    private void packColumns() {
        TreeColumn[] columns = viewer.getTree().getColumns();
        for (TreeColumn column : columns) {
            column.pack();
        }
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
        installAction.setEnabled(getSelectedSDK() != null);

        detailsAction = new DetailsAction();
        detailsAction.setEnabled(getSelectedSDK() != null);

        uninstallAction = new UninstallAction();
        uninstallAction.setEnabled(getSelectedSDK() != null);

        refreshAction = new RefreshAction();
        refreshAction.setEnabled(true);

        doubleClickAction = new Action() {
            public void run() {
                installSelectedSDK();
            }
        };
    }

    /**
     * Gets the selected item {@link Object}.
     * 
     * @return selected object or null if selection is empty.
     */
    private Object getSelectedItem() {
        Object result = null;

        TreeNode node = getSelectedNode();
        if (node != null) {
            Object object = node.getValue();
            if (object instanceof String) {
                TreeNode root = getRootParentNode(node);
                if (root.getValue() instanceof ISDKRepository) {
                    result = root.getValue();
                }
            } else {
                result = node.getValue();
            }
        }
        return result;
    }

    /**
     * Gets the root {@link TreeNode} for the specified {@link TreeNode}
     * instance.
     * 
     * @param node target node.
     * @return root parent node.
     */
    private TreeNode getRootParentNode(TreeNode node) {
        TreeNode result = node;
        if (node.getParent() != null) {
            result = getRootParentNode(node.getParent());
        }
        return result;
    }

    private ISDK getSelectedSDK() {
        ISDK result = null;
        TreeNode node = getSelectedNode();
        if (node != null) {
            Object object = node.getValue();
            if (object instanceof ISDK) {
                result = (ISDK) object;
            }
        }
        return result;
    }

    /**
     * Gets the current selected {@link TreeNode} instance.
     * 
     * @return {@link TreeNode} instance.
     */
    private TreeNode getSelectedNode() {
        TreeNode result = null;
        ISelection selection = viewer.getSelection();
        if (!selection.isEmpty()) {
            Object selectedElement = ((IStructuredSelection) selection)
                    .getFirstElement();
            if (selectedElement instanceof TreeNode) {
                result = (TreeNode) selectedElement;
            }
        }
        return result;
    }

    protected void installSelectedSDK() {
        ISDK sdk = getSelectedSDK();
        if (sdk != null) {
            try {
                QuickInstallCore.getInstance().installSDK(getSite().getShell(),
                        sdk, P2InstallerUI.getInstance());
            } catch (CoreException e) {
                org.eclipse.sequoyah.pulsar.core.Activator.logError(
                        Messages.SDKInstallView_InstallError, e);
            }
        }
    }

    protected void uninstallSelectedSDK() {
        ISDK sdk = getSelectedSDK();
        if (sdk != null) {
            try {
                QuickInstallCore.getInstance().uninstallSDK(
                        getSite().getShell(), sdk, P2InstallerUI.getInstance());
            } catch (CoreException e) {
                org.eclipse.sequoyah.pulsar.core.Activator.logError(
                        Messages.SDKInstallView_UninstallError, e);
            }
        }
    }

    private void hookDoubleClickAction() {
        viewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                doubleClickAction.run();
            }
        });
    }

    private void addProvisioningListener() {
        listener = new StructuredViewerProvisioningListener(viewer,
                StructuredViewerProvisioningListener.PROV_EVENT_PROFILE) {
            @Override
            protected void profileChanged(String profileId) {
                Display.getDefault().asyncExec(new Runnable() {
                    public void run() {
                        viewer.refresh();
                    }
                });
            }
        };
        ProvUI.addProvisioningListener(listener);
    }

    private void removeProvisioningListener() {
        ProvUI.removeProvisioningListener(listener);
    }

    public void dispose() {
        removeProvisioningListener();
        super.dispose();
    }

    @Override
    public void setFocus() {
    }
}
