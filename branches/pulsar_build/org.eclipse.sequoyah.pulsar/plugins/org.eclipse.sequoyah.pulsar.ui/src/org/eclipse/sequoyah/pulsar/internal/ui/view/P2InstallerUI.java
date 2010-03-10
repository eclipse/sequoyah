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
 *  Henrique Magalhaes(Motorola)/
 *  Euclides Neto (Motorola) - Fixed Install and added uninstall. 
 *  David Marques (Motorola) - Adding support for feature installation.
 *  Euclides Neto (Motorola) - Keeping SDK repository on p2.
 *  Henrique Magalhaes(Motorola) - Create method to remove all SDK repositories.
 *  Euclides Neto (Motorola) - Changed the method to refresh instead of remove SDK repositories.
 *  Euclides Neto (Motorola) - Changed the way to add artifacts and metadata repositories for runInstaller().
 */

package org.eclipse.sequoyah.pulsar.internal.ui.view;

import java.net.URI;
import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.equinox.internal.p2.console.ProvisioningHelper;
import org.eclipse.equinox.internal.p2.metadata.InstallableUnit;
import org.eclipse.equinox.internal.p2.operations.PlanAnalyzer;
import org.eclipse.equinox.internal.p2.ui.IProvHelpContextIds;
import org.eclipse.equinox.internal.p2.ui.dialogs.PreselectedIUInstallWizard;
import org.eclipse.equinox.internal.p2.ui.dialogs.ProvisioningWizardDialog;
import org.eclipse.equinox.internal.p2.ui.dialogs.UninstallWizard;
import org.eclipse.equinox.internal.p2.ui.sdk.ProvSDKUIActivator;
import org.eclipse.equinox.internal.provisional.p2.director.ProfileChangeRequest;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.engine.IProvisioningPlan;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.metadata.query.InstallableUnitQuery;
import org.eclipse.equinox.p2.operations.InstallOperation;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.operations.UninstallOperation;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.ui.LoadMetadataRepositoryJob;
import org.eclipse.equinox.p2.ui.Policy;
import org.eclipse.equinox.p2.ui.ProvisioningUI;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.sequoyah.pulsar.internal.core.SDK;
import org.eclipse.sequoyah.pulsar.internal.provisional.core.IInstallerUI;
import org.eclipse.sequoyah.pulsar.internal.provisional.core.ISDK;
import org.eclipse.sequoyah.pulsar.internal.provisional.core.ISDKRepository;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * A class implementing IInstallerUI with P2 install wizard
 */
@SuppressWarnings("restriction")
public class P2InstallerUI implements IInstallerUI {

    private static P2InstallerUI instance;

    private P2InstallerUI() {
    }

    public static IInstallerUI getInstance() {
        if (instance == null)
            instance = new P2InstallerUI();

        return instance;
    }

    public void runInstaller(final Shell parentShell, ISDK sdk)
            throws CoreException {
        SDK sdkImpl = (SDK) sdk.getAdapter(SDK.class);
        final InstallableUnit[] ius = getInstallableUnits(sdkImpl);
        final URI artifactsUri = sdkImpl.getRepository().getArtifactsURI();
        final URI metadataUri = sdkImpl.getRepository().getMetadataURI();
        
        final ProvisioningUI defaultUI = ProvisioningUI.getDefaultUI();
		ProvisioningSession session = defaultUI.getSession();
		session.getArtifactRepositoryManager().addRepository(artifactsUri);
		session.getMetadataRepositoryManager().addRepository(metadataUri);
		
        final InstallOperation operation = new InstallOperation(session, ius);
        Job job = new Job("Preparing") {
			protected IStatus run(IProgressMonitor monitor) {
				IStatus resolveStatus = operation.resolveModal(monitor);
				return resolveStatus;
			}
		};
		job.schedule();
		job.addJobChangeListener(new JobChangeAdapter() {
        	public void done(IJobChangeEvent event) {
        		final IProvisioningPlan plan = operation.getProvisioningPlan();
                if (plan != null) {
                    Display.getDefault().asyncExec(new Runnable() {
                        public void run() {
                        	LoadMetadataRepositoryJob loadMetadaJob = new LoadMetadataRepositoryJob(defaultUI);
                            IWizard wizard = new PreselectedIUInstallWizard(defaultUI, operation, ius, loadMetadaJob);
                            WizardDialog dialog = new WizardDialog(parentShell,
                                    wizard);
                            dialog.create();
                            PlatformUI.getWorkbench().getHelpSystem().setHelp(
                                    dialog.getShell(),
                                    IProvHelpContextIds.INSTALL_WIZARD);
                            dialog.open();
                        }
                    });
                }
        	}
        });
        
    }

    public void runUninstaller(final Shell parentShell, ISDK sdk)
            throws CoreException {
        SDK sdkImpl = (SDK) sdk.getAdapter(SDK.class);
        final InstallableUnit[] ius = getInstallableUnits(sdkImpl);

        final ProvisioningUI defaultUI = ProvisioningUI.getDefaultUI();
		ProvisioningSession session = defaultUI.getSession();
        final UninstallOperation operation = new UninstallOperation(session, ius);

        Job job = new Job("Preparing") {
			protected IStatus run(IProgressMonitor monitor) {
				IStatus resolveStatus = operation.resolveModal(monitor);
				return resolveStatus;
			}
		};
		job.schedule();
        
		job.addJobChangeListener(new JobChangeAdapter() {
			public void done(IJobChangeEvent event) {
				final IProvisioningPlan plan = operation.getProvisioningPlan();
				if (plan != null) {
					if (PlatformUI.isWorkbenchRunning()) {
						PlatformUI.getWorkbench().getDisplay().asyncExec(
								new Runnable() {
									public void run() {
										LoadMetadataRepositoryJob loadMetadaJob = new LoadMetadataRepositoryJob(defaultUI);
										UninstallWizard wizard = new UninstallWizard(
												defaultUI, operation, ius, loadMetadaJob);
										WizardDialog dialog = new ProvisioningWizardDialog(
												parentShell, wizard);
										dialog.create();
										PlatformUI
										.getWorkbench()
										.getHelpSystem()
										.setHelp(
												dialog.getShell(),
												IProvHelpContextIds.UNINSTALL_WIZARD);
										dialog.open();
									}
								});
					}
				}
			}
		});
    }

    public void refreshSDKRepositories(Collection<ISDKRepository> repositories) {
        /*
         * Iterate over repositories in order to get the artifacts and metadata
         * URLs
         */
        int i = 0;
        for (ISDKRepository repository : repositories) {
            ProvisioningHelper.addMetadataRepository(repository.getMetadataURI());
            ProvisioningHelper.addArtifactRepository(repository.getArtifactsURI());
            i++;
        }
    }

    private ProfileChangeRequest getProfileChangeRequest(
            IInstallableUnit[] ius, String targetProfileId, MultiStatus status,
            IProgressMonitor monitor) {
        SubMonitor sub = SubMonitor.convert(monitor, 
                "", 1);
        ProfileChangeRequest request = null;
        try {
            request = ProfileChangeRequest.createByProfileId(targetProfileId);
            request.removeInstallableUnits(ius);

            String key = getPolicy().getVisibleInstalledIUQuery().getId();
            for (int i = 0; i < ius.length; i++)
                request.removeInstallableUnitProfileProperty(ius[i], key);
        } finally {
            sub.done();
        }
        return request;
    }

    private URI getMetadataURI(SDK sdk) {
        return sdk.getRepository().getMetadataURI();
    }

    private InstallableUnit[] getInstallableUnits(SDK sdk) {
        IInstallableUnit iu = sdk.getInstallableUnit();
        InstallableUnitQuery query = new InstallableUnitQuery(iu.getId(), iu.getVersion());
        IQueryResult<IInstallableUnit> installableUnits = ProvisioningHelper.getInstallableUnits(
                getMetadataURI(sdk), query, new NullProgressMonitor());
        return (InstallableUnit[]) installableUnits
                .toArray(InstallableUnit.class);
    }

    private Policy getPolicy() {
        return null /*ProvisioningHelper.get*/;
    }

    private String getProfileID() throws ProvisionException {
        return ProvSDKUIActivator.getDefault().getProvisioningUI().getProfileId();
    }

    private MultiStatus getStatus() {
        return PlanAnalyzer.getProfileChangeAlteredStatus();
    }
}
