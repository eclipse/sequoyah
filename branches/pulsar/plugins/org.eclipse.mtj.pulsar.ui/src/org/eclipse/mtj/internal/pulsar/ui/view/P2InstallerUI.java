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
 *
 */

package org.eclipse.mtj.internal.pulsar.ui.view;

import java.net.URI;
import java.text.MessageFormat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.equinox.internal.p2.console.ProvisioningHelper;
import org.eclipse.equinox.internal.p2.metadata.InstallableUnit;
import org.eclipse.equinox.internal.p2.ui.PlanAnalyzer;
import org.eclipse.equinox.internal.p2.ui.ProvUIMessages;
import org.eclipse.equinox.internal.provisional.p2.core.ProvisionException;
import org.eclipse.equinox.internal.provisional.p2.director.ProfileChangeRequest;
import org.eclipse.equinox.internal.provisional.p2.director.ProvisioningPlan;
import org.eclipse.equinox.internal.provisional.p2.engine.IProfile;
import org.eclipse.equinox.internal.provisional.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.internal.provisional.p2.metadata.query.InstallableUnitQuery;
import org.eclipse.equinox.internal.provisional.p2.query.Collector;
import org.eclipse.equinox.internal.provisional.p2.query.Query;
import org.eclipse.equinox.internal.provisional.p2.ui.IProvHelpContextIds;
import org.eclipse.equinox.internal.provisional.p2.ui.ProvisioningOperationRunner;
import org.eclipse.equinox.internal.provisional.p2.ui.QueryableMetadataRepositoryManager;
import org.eclipse.equinox.internal.provisional.p2.ui.dialogs.PreselectedIUInstallWizard;
import org.eclipse.equinox.internal.provisional.p2.ui.operations.PlannerResolutionOperation;
import org.eclipse.equinox.internal.provisional.p2.ui.policy.Policy;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mtj.internal.provisional.pulsar.core.IInstallerUI;
import org.eclipse.mtj.internal.provisional.pulsar.core.ISDK;
import org.eclipse.mtj.internal.provisional.pulsar.core.ISDK.EType;
import org.eclipse.mtj.internal.pulsar.core.P2Utils;
import org.eclipse.mtj.internal.pulsar.core.SDK;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * A class implementing IInstallerUI with P2 install wizard
 */
public class P2InstallerUI implements IInstallerUI {

	private static P2InstallerUI instance;

	private P2InstallerUI() {
	}
	
	public static IInstallerUI getInstance() {
		if (instance == null)
			instance = new P2InstallerUI();
		
		return instance;
	}

	public void runInstaller(final Shell parentShell, ISDK sdk) throws CoreException {
		SDK sdkImpl = (SDK) sdk.getAdapter(SDK.class);
		IInstallableUnit iu = sdkImpl.getInstallableUnit();
        Query query = new InstallableUnitQuery(iu.getId(), iu.getVersion());
		Collector installableUnits = ProvisioningHelper.getInstallableUnits(getMetadataURI(sdkImpl), query, null);
		final InstallableUnit[] ius = (InstallableUnit[]) installableUnits.toArray(InstallableUnit.class);
		IProfile profile = createProfile(parentShell, sdk);
		if (profile == null)
			return;
		final URI artifactsUri = sdkImpl.getRepository().getArtifactsURI();
		ProvisioningHelper.addArtifactRepository(artifactsUri);
		ProfileChangeRequest changeRequest = new ProfileChangeRequest(profile);
		changeRequest.addInstallableUnits(ius);
		final String id = profile.getProfileId();
		final PlannerResolutionOperation operation = 
			new PlannerResolutionOperation(ProvUIMessages.ProfileModificationAction_ResolutionOperationLabel, 
					id,	changeRequest, null, PlanAnalyzer.getProfileChangeAlteredStatus(), true);
		Job job = ProvisioningOperationRunner.schedule(operation, StatusManager.SHOW);
		job.addJobChangeListener(new JobChangeAdapter() {
			public void done(IJobChangeEvent event) {
				final ProvisioningPlan plan = operation.getProvisioningPlan();
				if (plan != null) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							Policy policy = getPolicy();
							IWizard wizard = new PreselectedIUInstallWizard(policy, id, ius, operation, 
									new QueryableMetadataRepositoryManager(policy.getQueryContext(), false));
							WizardDialog dialog = new WizardDialog(parentShell, wizard);
							dialog.create();
							PlatformUI.getWorkbench().getHelpSystem().setHelp(dialog.getShell(), IProvHelpContextIds.INSTALL_WIZARD);
							
							int result = dialog.open();
							if (result != Dialog.OK) {
								P2Utils.deleteProfile(id);
							}
							ProvisioningHelper.removeArtifactRepository(artifactsUri);
						}
					});
				}
			}
		});
	}
	
	private URI getMetadataURI(SDK sdk) {
		return sdk.getRepository().getMetadataURI();
	}

	private IProfile createProfile(Shell parentShell, ISDK sdk) throws ProvisionException {
		IPath installFolder = null;
		if (sdk.getType().equals(EType.ZIP_ARCHIVE)) {
			installFolder = getInstallFolderFromUser(parentShell, sdk);
			if (installFolder == null)
				return null;
		}
		return P2Utils.createProfileForSDK(sdk, installFolder);
	}

	private IPath getInstallFolderFromUser(Shell parentShell, ISDK sdk) {
		DirectoryDialog directoryDialog = new DirectoryDialog(parentShell);
		directoryDialog.setMessage(MessageFormat.format(
				"Please choose where to unzip the archive for {0}:", sdk.getName()));
		String dirPathStr = directoryDialog.open();
		if (dirPathStr != null)
			return new Path(dirPathStr);
		
		return null;
	}

	private Policy getPolicy() {
		return new Policy();
	}

}
