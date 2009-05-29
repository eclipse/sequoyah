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
 *  Euclides Neto (Motorola) - Externalize strings.
 *  Gustavo de Paula (Motorola) - Add change permission in a linux OS  
 */

package org.eclipse.mtj.internal.pulsar.core.action;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.internal.p2.touchpoint.natives.Util;
import org.eclipse.equinox.internal.p2.touchpoint.natives.actions.ActionConstants;
import org.eclipse.equinox.internal.p2.touchpoint.natives.actions.ChmodAction;
import org.eclipse.equinox.internal.p2.touchpoint.natives.actions.UnzipAction;
import org.eclipse.equinox.internal.provisional.p2.engine.IProfile;
import org.eclipse.equinox.internal.provisional.p2.metadata.IInstallableUnit;
import org.eclipse.mtj.internal.pulsar.core.Messages;
import org.eclipse.mtj.pulsar.core.Activator;

public class UnzipAndExecuteAction extends UnzipAction {

	public static final String ACTION_UNZIPANDEXECUTE = "unzipAndExecute"; //$NON-NLS-1$
	private static final String PARM_EXECUTABLE = "executable"; //$NON-NLS-1$

	@SuppressWarnings("unchecked")
	@Override
	public IStatus execute(Map parameters) {
		IStatus status = super.execute(parameters);
		if (!status.isOK())
			return status;
		
		String executable = (String) parameters.get(PARM_EXECUTABLE);
		if (executable == null)
			return Activator.makeErrorStatus(
					MessageFormat.format(
							Messages.ExecuteAction_ParamNotSetError,
							PARM_EXECUTABLE, ACTION_UNZIPANDEXECUTE), null);

		IInstallableUnit iu = (IInstallableUnit) parameters.get(ActionConstants.PARM_IU);
		IProfile profile = (IProfile) parameters.get(ActionConstants.PARM_PROFILE);
		String installFolder = Util.getInstallFolder(profile);
		if (installFolder == null)
			return Activator.makeErrorStatus(Messages.ExecuteAction_ProfileInstallFolderError, null);
		else {
			IPath path = new Path(installFolder);
			path = path.append(executable);
			executable = path.toOSString();
			try {
				execute(executable, iu);
			} catch (Exception e) {
				return Activator.makeErrorStatus(MessageFormat.format(Messages.ExecuteAction_ExecuteError, executable), e);
			}
		}

		return Status.OK_STATUS;
	}

	private void execute(String executable, IInstallableUnit iu) throws IOException, InterruptedException {
		String osName = System.getProperty("os.name" );
		if( osName.equals( "Linux" ) )
        {
            ChmodAction ca = new ChmodAction();
            String dir = executable.substring(0, executable.lastIndexOf('/'));
            String file = executable.substring(executable.lastIndexOf('/')+1, executable.length());
            ca.chmod(dir, file, "777", null);
        }		
		
		ProcessBuilder processBuilder = new ProcessBuilder(executable);
		Process process = processBuilder.start();
		process.waitFor();
		// TODO do more with error/output streams, put result into iu
	}

	@SuppressWarnings("unchecked")
	@Override
	public IStatus undo(Map parameters) {
		return Activator.makeErrorStatus(Messages.ExecuteAction_UndoUnsupportedError, null);
	}

}
