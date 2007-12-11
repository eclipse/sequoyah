/********************************************************************************
 * Copyright (c) 2007 Motorola Inc.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Fabio Fantato (Motorola)
 * 
 * Contributors:
 * name (company) - description.
 ********************************************************************************/

package org.eclipse.tml.framework.device.ui;

import org.eclipse.osgi.util.NLS;

/**
 * Resources for externalized Strings of the TmL Emulator Core.
 */
public class DeviceUIResources extends NLS {
	

	private static String BUNDLE_NAME = "org.eclipse.tml.framework.device.ui.DeviceUIResources";//$NON-NLS-1$

	public static String TML_Device_Plugin_Name;
	public static String TML_Error;
	public static String TML_Resource_Not_Available;
	public static String TML_Handler_Not_Instanced;
		
	static {
		NLS.initializeMessages(BUNDLE_NAME, DeviceUIResources.class);
	}

}
