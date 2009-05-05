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

package org.eclipse.mtj.internal.pulsar.core.action;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.internal.p2.touchpoint.natives.Messages;
import org.eclipse.equinox.internal.p2.touchpoint.natives.Util;
import org.eclipse.equinox.internal.p2.touchpoint.natives.actions.ActionConstants;
import org.eclipse.equinox.internal.provisional.p2.artifact.repository.IFileArtifactRepository;
import org.eclipse.equinox.internal.provisional.p2.core.ProvisionException;
import org.eclipse.equinox.internal.provisional.p2.engine.ProvisioningAction;
import org.eclipse.equinox.internal.provisional.p2.metadata.IArtifactKey;
import org.eclipse.equinox.internal.provisional.p2.metadata.IInstallableUnit;
import org.eclipse.mtj.pulsar.core.Activator;
import org.eclipse.osgi.util.NLS;

public class ExecuteAction extends ProvisioningAction {

	public static final String ACTION_EXECUTE = "execute"; //$NON-NLS-1$
	private static final String PARM_EXECUTABLE = "executable";

	@SuppressWarnings("unchecked")
	@Override
	public IStatus execute(Map parameters) {
		String executable = (String) parameters.get(PARM_EXECUTABLE);
		if (executable == null)
			return Activator.makeErrorStatus(NLS.bind(Messages.param_not_set, PARM_EXECUTABLE, ACTION_EXECUTE), null);

		IInstallableUnit iu = (IInstallableUnit) parameters.get(ActionConstants.PARM_IU);
		if (executable.equals(ActionConstants.PARM_ARTIFACT)) {
			IArtifactKey artifactKey = iu.getArtifacts()[0];

			IFileArtifactRepository downloadCache;
			try {
				downloadCache = Util.getDownloadCacheRepo();
			} catch (ProvisionException e) {
				return e.getStatus();
			}
			File fileLocation = downloadCache.getArtifactFile(artifactKey);
			if ((fileLocation == null) || !fileLocation.exists())
				return Activator.makeErrorStatus(NLS.bind(Messages.artifact_not_available, artifactKey), null);
			executable = fileLocation.getAbsolutePath();
		}
		try {
			execute(executable, iu);
		} catch (Exception e) {
			return Activator.makeErrorStatus(MessageFormat.format("could not execute \"{0}\"", executable), e);
		}

		return Status.OK_STATUS;
	}

	private void execute(String executable, IInstallableUnit iu) throws IOException, InterruptedException {
		ProcessBuilder processBuilder = new ProcessBuilder(executable);
		Process process = processBuilder.start();
		process.waitFor();
		// TODO do more with error/output streams, put result into iu
	}

	@SuppressWarnings("unchecked")
	@Override
	public IStatus undo(Map parameters) {
		return Activator.makeErrorStatus("undo not supported for exectutables", null);
	}

}
