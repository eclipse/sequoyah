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

package org.eclipse.mtj.internal.pulsar.ui.view;

import java.net.URI;
import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.equinox.internal.p2.console.ProvisioningHelper;
import org.eclipse.equinox.internal.p2.metadata.InstallableUnit;
import org.eclipse.equinox.internal.p2.ui.PlanAnalyzer;
import org.eclipse.equinox.internal.p2.ui.ProvUIMessages;
import org.eclipse.equinox.internal.p2.ui.sdk.ProvSDKUIActivator;
import org.eclipse.equinox.internal.provisional.p2.core.ProvisionException;
import org.eclipse.equinox.internal.provisional.p2.director.ProfileChangeRequest;
import org.eclipse.equinox.internal.provisional.p2.director.ProvisioningPlan;
import org.eclipse.equinox.internal.provisional.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.internal.provisional.p2.metadata.query.InstallableUnitQuery;
import org.eclipse.equinox.internal.provisional.p2.query.Collector;
import org.eclipse.equinox.internal.provisional.p2.query.Query;
import org.eclipse.equinox.internal.provisional.p2.ui.IProvHelpContextIds;
import org.eclipse.equinox.internal.provisional.p2.ui.ProvisioningOperationRunner;
import org.eclipse.equinox.internal.provisional.p2.ui.QueryableMetadataRepositoryManager;
import org.eclipse.equinox.internal.provisional.p2.ui.actions.InstallAction;
import org.eclipse.equinox.internal.provisional.p2.ui.dialogs.PreselectedIUInstallWizard;
import org.eclipse.equinox.internal.provisional.p2.ui.dialogs.ProvisioningWizardDialog;
import org.eclipse.equinox.internal.provisional.p2.ui.dialogs.UninstallWizard;
import org.eclipse.equinox.internal.provisional.p2.ui.operations.PlannerResolutionOperation;
import org.eclipse.equinox.internal.provisional.p2.ui.operations.ProvisioningUtil;
import org.eclipse.equinox.internal.provisional.p2.ui.policy.Policy;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mtj.internal.provisional.pulsar.core.IInstallerUI;
import org.eclipse.mtj.internal.provisional.pulsar.core.ISDK;
import org.eclipse.mtj.internal.provisional.pulsar.core.ISDKRepository;
import org.eclipse.mtj.internal.pulsar.core.SDK;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.statushandlers.StatusManager;

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
        ProvisioningUtil.addArtifactRepository(artifactsUri, true);
        ProvisioningUtil.addMetadataRepository(metadataUri, true);
        MultiStatus status = getStatus();
        final String id = getProfileID();
        ProfileChangeRequest changeRequest = InstallAction
                .computeProfileChangeRequest(ius, id, status,
                        new NullProgressMonitor());
        final PlannerResolutionOperation operation = new PlannerResolutionOperation(
                Messages.P2InstallerUI_ResoultionOperationLabel, id,
                changeRequest, null, PlanAnalyzer
                        .getProfileChangeAlteredStatus(), true);

        Job job = ProvisioningOperationRunner.schedule(operation,
                StatusManager.SHOW);
        job.addJobChangeListener(new JobChangeAdapter() {
            public void done(IJobChangeEvent event) {
                final ProvisioningPlan plan = operation.getProvisioningPlan();
                if (plan != null) {
                    Display.getDefault().asyncExec(new Runnable() {
                        public void run() {
                            Policy policy = getPolicy();
                            IWizard wizard = new PreselectedIUInstallWizard(
                                    policy, id, ius, operation,
                                    new QueryableMetadataRepositoryManager(
                                            policy.getQueryContext(), false));
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

        final String id = getProfileID();
        SDK sdkImpl = (SDK) sdk.getAdapter(SDK.class);
        final InstallableUnit[] ius = getInstallableUnits(sdkImpl);
        final MultiStatus additionalStatus = getStatus();
        final ProfileChangeRequest[] request = new ProfileChangeRequest[1];

        BusyIndicator.showWhile(parentShell.getDisplay(), new Runnable() {
            public void run() {
                request[0] = getProfileChangeRequest(ius, id, additionalStatus,
                        new NullProgressMonitor());
            }
        });

        final PlannerResolutionOperation operation = new PlannerResolutionOperation(
                ProvUIMessages.ProfileModificationAction_ResolutionOperationLabel,
                id, request[0], null, additionalStatus, true);

        Job job = ProvisioningOperationRunner.schedule(operation,
                StatusManager.SHOW);
        job.addJobChangeListener(new JobChangeAdapter() {
            public void done(IJobChangeEvent event) {
                final ProvisioningPlan plan = operation.getProvisioningPlan();
                if (plan != null) {
                    if (PlatformUI.isWorkbenchRunning()) {
                        PlatformUI.getWorkbench().getDisplay().asyncExec(
                                new Runnable() {
                                    public void run() {
                                        UninstallWizard wizard = new UninstallWizard(
                                                getPolicy(), id, ius, operation);
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
        URI[] artifactsURIs = new URI[repositories.size()];
        URI[] metadataURIs = new URI[repositories.size()];

        /*
         * Iterate over repositories in order to get the artifacts and metadata
         * URLs
         */
        int i = 0;
        for (ISDKRepository repository : repositories) {
            metadataURIs[i] = repository.getMetadataURI();
            artifactsURIs[i] = repository.getArtifactsURI();
            i++;
        }

        /*
         * The call to ProvisioningUtil.refreshMetadataRepositories() and
         * refreshArtifactRepositories() discards any cached state held by the
         * repository manager and reloads the repository contents.
         */
        try {
            ProvisioningUtil.refreshMetadataRepositories(metadataURIs,
                    new NullProgressMonitor());
            ProvisioningUtil.refreshArtifactRepositories(artifactsURIs,
                    new NullProgressMonitor());
        } catch (ProvisionException e) {
            /*
             * do nothing - the repository can have its URL changed or unknown
             * (first call)
             */
        }
    }

    private ProfileChangeRequest getProfileChangeRequest(
            IInstallableUnit[] ius, String targetProfileId, MultiStatus status,
            IProgressMonitor monitor) {
        SubMonitor sub = SubMonitor.convert(monitor,
                ProvUIMessages.ProfileChangeRequestBuildingRequest, 1);
        ProfileChangeRequest request = null;
        try {
            request = ProfileChangeRequest.createByProfileId(targetProfileId);
            request.removeInstallableUnits(ius);

            String key = getPolicy().getQueryContext()
                    .getVisibleInstalledIUProperty();
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
        Query query = new InstallableUnitQuery(iu.getId(), iu.getVersion());
        Collector installableUnits = ProvisioningHelper.getInstallableUnits(
                getMetadataURI(sdk), query, null);
        return (InstallableUnit[]) installableUnits
                .toArray(InstallableUnit.class);
    }

    private Policy getPolicy() {
        return Policy.getDefault();

    }

    private String getProfileID() throws ProvisionException {
        return ProvSDKUIActivator.getSelfProfileId();
    }

    private MultiStatus getStatus() {
        return PlanAnalyzer.getProfileChangeAlteredStatus();
    }
}
