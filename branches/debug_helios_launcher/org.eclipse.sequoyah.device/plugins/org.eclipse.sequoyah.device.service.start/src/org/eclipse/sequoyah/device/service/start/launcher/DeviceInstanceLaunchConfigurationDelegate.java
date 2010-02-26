/********************************************************************************
 * Copyright (c) 2007-2010 Motorola Inc. All rights reserved.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributor:
 * Fabio Fantato (Motorola)
 *
 * Contributors:
 * Daniel Pastore (Eldorado) - [289870] Moving and renaming Tml to Sequoyah
 ********************************************************************************/

package org.eclipse.sequoyah.device.service.start.launcher;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.externaltools.internal.launchConfigurations.ExternalToolsCoreUtil;
import org.eclipse.core.externaltools.internal.launchConfigurations.ProgramLaunchDelegate;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.externaltools.internal.model.IExternalToolConstants;


/**
 * This delegate controls the Emulator Instance Launch Configuration. This class
 * has no initial implementation because the launch use the same behavior of
 * {@link ProgramLaunchDelegate}.
 * 
 * @author Fabio Fantato
 * 
 */

public class DeviceInstanceLaunchConfigurationDelegate extends LaunchConfigurationDelegate{

	private static final String ExternalToolsProgramMessages = null;

	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		String[] arguments = ExternalToolsCoreUtil.getArguments(configuration);
		IPath location = ExternalToolsCoreUtil.getLocation(configuration);
		if (monitor.isCanceled()) {
			return;
		}
		
		int cmdLineLength = 1;
		if (arguments != null) {
			cmdLineLength += arguments.length;
		}
		String[] cmdLine = new String[cmdLineLength];
		cmdLine[0] = location.toOSString();
		if (arguments != null) {
			System.arraycopy(arguments, 0, cmdLine, 1, arguments.length);
		}
		
		File workingDir = ExternalToolsCoreUtil.getWorkingDirectory(configuration).toFile();

		if (monitor.isCanceled()) {
			return;
		}
		
		String[] envp = DebugPlugin.getDefault().getLaunchManager().getEnvironment(configuration);
		
		Process p = DebugPlugin.exec(cmdLine, workingDir, envp);
		IProcess process = null;
		
		// add process type to process attributes
		Map<String, String> processAttributes = new HashMap<String, String>();
		String programName = location.lastSegment();
		String extension = location.getFileExtension();
		if (extension != null) {
			programName = programName.substring(0, programName.length() - (extension.length() + 1));
		}
		programName = programName.toLowerCase();
		processAttributes.put(IProcess.ATTR_PROCESS_TYPE, programName);
		
		if (p != null) {
			monitor.beginTask(NLS.bind("Running...", new String[] {configuration.getName()}), IProgressMonitor.UNKNOWN);
			process = DebugPlugin.newProcess(launch, p, location.toOSString(), processAttributes);
		}
		if (p == null || process == null) {
			if (p != null)
				p.destroy();
			throw new CoreException(new Status(IStatus.ERROR, IExternalToolConstants.PLUGIN_ID, IExternalToolConstants.ERR_INTERNAL_ERROR, "An IProcess could not be created for the launch", null));
		}
		process.setAttribute(IProcess.ATTR_CMDLINE, generateCommandLine(cmdLine));
		
		
	}
	
	private String generateCommandLine(String[] commandLine) {
		if (commandLine.length < 1)
			return ""; //$NON-NLS-1$
		StringBuffer buf= new StringBuffer();
		for (int i= 0; i < commandLine.length; i++) {
			buf.append(' ');
			char[] characters= commandLine[i].toCharArray();
			StringBuffer command= new StringBuffer();
			boolean containsSpace= false;
			for (int j = 0; j < characters.length; j++) {
				char character= characters[j];
				if (character == '\"') {
					command.append('\\');
				} else if (character == ' ') {
					containsSpace = true;
				}
				command.append(character);
			}
			if (containsSpace) {
				buf.append('\"');
				buf.append(command);
				buf.append('\"');
			} else {
				buf.append(command);
			}
		}	
		return buf.toString();
	}
}
