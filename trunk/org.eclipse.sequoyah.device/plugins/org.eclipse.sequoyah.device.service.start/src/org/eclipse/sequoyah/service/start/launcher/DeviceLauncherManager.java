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
 * {Name} (company) - description of contribution.
 ********************************************************************************/

package org.eclipse.tml.service.start.launcher;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.RefreshTab;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.tml.common.utilities.exception.TmLException;
import org.eclipse.tml.service.start.StartServicePlugin;
import org.eclipse.tml.service.start.exception.StartServiceExceptionHandler;
import org.eclipse.tml.service.start.exception.StartServiceExceptionStatus;
import org.eclipse.ui.externaltools.internal.model.IExternalToolConstants;

/**
 * Provide the default configuration to launch a new emulator based on External
 * Programs Launcher from eclipse. If the emulator needs to start using a
 * different way, this class should be rewrite by emulator plugin
 * 
 * @author Fabio Fantato
 * 
 */
@SuppressWarnings( { "unchecked", "restriction" })
public class DeviceLauncherManager {

    public static final String LAUNCHER_ID = "org.eclipse.tml.service.start.launcher";
	public static int ATTR_APPLET_WIDTH_VALUE = 200;
	public static int ATTR_APPLET_HEIGHT_VALUE = 200;
	public static String ATTR_MAIN_TYPE_NAME_VALUE = "";
	public static String MAPPED_RESOURCE_TYPES = "org.eclipse.debug.core.MAPPED_RESOURCE_TYPES";
	@SuppressWarnings("unchecked")
	public static List MAPPED_RESOURCE_TYPES_LIST = new ArrayList<String>();
	public static String MAPPED_RESOURCE_TYPES_VALUE = "4";
	public static String ATTR_JRE_CONTAINER_PATH_VALUE = "org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/jdk1.5.0_11";
	public static String FAVORITE_GROUPS = "org.eclipse.debug.ui.favoriteGroups";
	@SuppressWarnings("unchecked")
	public static List FAVORITE_GROUPS_LIST = new ArrayList<String>();
	public static Map<String, Object> ATTR_APPLET_PARAMETERS_MAP = new HashMap<String, Object>();
	public static String ATTR_APPLET_NAME_VALUE = "";
	public static boolean ATTR_ENVIRONMENT_VARIABLES_VALUE = true;
	public static String HOST_DEFAULT = "127.0.0.1:0.0";
	public static String ATTR_PROJECT_NAME_VALUE = "javaproject";
	public static String ATTR_REFRESH_SCOPE_VALUE = "${project}";
	public static String MAPPED_RESOURCE_PATHS = "org.eclipse.debug.core.MAPPED_RESOURCE_PATHS";
	public static String MAPPED_RESOURCE_PATHS_VALUE = "/javaproject";
	@SuppressWarnings("unchecked")
	public static List MAPPED_RESOURCE_PATHS_LIST = new ArrayList<String>();

	static {
		MAPPED_RESOURCE_TYPES_LIST.add(MAPPED_RESOURCE_TYPES_VALUE);
		FAVORITE_GROUPS_LIST
				.add(IExternalToolConstants.ID_EXTERNAL_TOOLS_LAUNCH_GROUP);
		ATTR_APPLET_PARAMETERS_MAP.put(RefreshTab.ATTR_REFRESH_SCOPE,
				ATTR_REFRESH_SCOPE_VALUE);
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
			ILaunchManager mgr = DebugPlugin.getDefault().getLaunchManager();
			ILaunchConfigurationType type = mgr.getLaunchConfigurationType(LAUNCHER_ID);
			ILaunchConfigurationWorkingCopy copy;
			copy = type.newInstance(null, name);
			copy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_APPLET_WIDTH,ATTR_APPLET_WIDTH_VALUE);
			copy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_APPLET_HEIGHT,	ATTR_APPLET_HEIGHT_VALUE);
			copy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,ATTR_MAIN_TYPE_NAME_VALUE);
			copy.setAttribute(MAPPED_RESOURCE_TYPES,MAPPED_RESOURCE_TYPES_LIST);
			copy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_JRE_CONTAINER_PATH,ATTR_JRE_CONTAINER_PATH_VALUE);
			copy.setAttribute(FAVORITE_GROUPS, FAVORITE_GROUPS_LIST);
			copy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_APPLET_NAME,ATTR_APPLET_NAME_VALUE);
			copy.setAttribute(IExternalToolConstants.ATTR_WORKING_DIRECTORY,launcher.getWorkingDirectory());
			copy.setAttribute(IExternalToolConstants.ATTR_TOOL_ARGUMENTS,launcher.getToolArguments());
			copy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME,ATTR_PROJECT_NAME_VALUE);
			copy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_APPLET_PARAMETERS,ATTR_APPLET_PARAMETERS_MAP);
			copy.setAttribute(IExternalToolConstants.ATTR_LOCATION, launcher.getLocation());
			ILaunchConfiguration config = copy.doSave();
			File file = new File(launcher.getFileId());
			if (file.exists()) file.delete();
			launch = config.launch(ILaunchManager.DEBUG_MODE, null);
			launcher.setPID(readPID(launcher.getFileId()));
		} catch (Throwable e) {
			StartServicePlugin.logError("emulator could not be launched", e);
		}
		return launch;
	}

	
	
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
	    	throw StartServiceExceptionHandler.exception(StartServiceExceptionStatus.CODE_ERROR_USER);
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
