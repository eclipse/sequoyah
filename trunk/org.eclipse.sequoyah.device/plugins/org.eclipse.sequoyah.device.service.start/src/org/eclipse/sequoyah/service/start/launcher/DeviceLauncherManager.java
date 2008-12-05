/********************************************************************************
 * Copyright (c) 2007 Motorola Inc. All rights reserved.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributor:
 * Fabio Fantato (Motorola)
 *
 * Contributors:
 * Fabio Fantato (Eldorado Research Institute) - bug []
 ********************************************************************************/

package org.eclipse.tml.service.start.launcher;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.tml.common.utilities.BasePlugin;
import org.eclipse.tml.common.utilities.exception.AbstractExceptionStatus;
import org.eclipse.tml.common.utilities.exception.TmLException;
import org.eclipse.tml.service.start.exception.StartServiceExceptionHandler;
import org.eclipse.ui.externaltools.internal.model.IExternalToolConstants;

/**
 * Provide the default configuration to launch a new emulator based on External
 * Programs Launcher from eclipse. If the emulator needs to start using a
 * different way, this class should be rewrite by emulator plugin
 * 
 * @author Fabio Fantato
 * 
 */

public class DeviceLauncherManager {

    public static final String LAUNCHER_ID = "org.eclipse.tml.service.start.launcher"; //$NON-NLS-1$
    public static final String ATTR_LAUNCH_CONFIGURATION_BUILD_SCOPE = "org.eclipse.ui.externaltools.ATTR_LAUNCH_CONFIGURATION_BUILD_SCOPE"; //$NON-NLS-1$
    public static final String ATTR_LAUNCH_CONFIGURATION_BUILD_SCOPE_VALUE = "${none}"; //$NON-NLS-1$
    
	static {
	}

	/**
	 * Launch emulator
	 * 
	 * @param launcher
	 *            is the specific launcher for emulator required
	 * @param project
	 *            is the current project selected
	 * @param host
	 *            is a string connection
	 * @return a launcher to control that emulator instance
	 */
	public static ILaunch launch(IDeviceLauncher launcher,String name) {
		ILaunch launch = null;
		try {
			BasePlugin.logInfo("launching "+name); //$NON-NLS-1$
			ILaunchManager mgr = DebugPlugin.getDefault().getLaunchManager();
			ILaunchConfigurationType type = mgr.getLaunchConfigurationType(LAUNCHER_ID);
			ILaunchConfigurationWorkingCopy copy;
			copy = type.newInstance(null, name);
			copy.setAttribute(ATTR_LAUNCH_CONFIGURATION_BUILD_SCOPE,ATTR_LAUNCH_CONFIGURATION_BUILD_SCOPE_VALUE);
			copy.setAttribute(IExternalToolConstants.ATTR_LOCATION, launcher.getLocation());
			copy.setAttribute(IExternalToolConstants.ATTR_TOOL_ARGUMENTS,launcher.getToolArguments());
			copy.setAttribute(IExternalToolConstants.ATTR_WORKING_DIRECTORY,launcher.getWorkingDirectory());			
			ILaunchConfiguration config = copy.doSave();
			
			File file = new File(launcher.getFileId());
			if (file.exists()) file.delete();
			launch = config.launch(ILaunchManager.DEBUG_MODE, null);
			launcher.setPID(readPID(launcher.getFileId()));
		} catch (Throwable e) {
			BasePlugin.logError("emulator could not be launched", e); //$NON-NLS-1$
		}
		return launch;
	}

	
	
	@SuppressWarnings("deprecation")
	private static int readPID(String filename) throws TmLException {
		int pid = 0;
		File file = new File(filename);
		FileInputStream fis = null;
	    BufferedInputStream bis = null;
	    DataInputStream dis = null;
	    int count=0;
	    while (!file.exists()&&(count<50)) {
	    	try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   
			count++;			
	    }
	    if (count>=50) {
	    	throw StartServiceExceptionHandler.exception(AbstractExceptionStatus.CODE_ERROR_USER);
	    }
	    try {
	      fis = new FileInputStream(file);
	      bis = new BufferedInputStream(fis);
	      dis = new DataInputStream(bis);
	      while (dis.available() != 0) {
	    	  pid = Integer.valueOf(dis.readLine());
	      }
	      fis.close();
	      bis.close();
	      dis.close();	      
	    } catch (FileNotFoundException e) {
	      e.printStackTrace();
	    } catch (IOException e) {
	      e.printStackTrace();
	    } finally {
	    	file.delete();
	    }
		return pid;
	}
	
	
}
