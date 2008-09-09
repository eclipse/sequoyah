/********************************************************************************
 * Copyright (c) 2008 Motorola Inc and Others. All rights reserverd.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Fabio Fantato (Eldorado Research Institute)
 * [244810] Migrating Device View and Instance View to a separate plugin
 * 
 * Contributors:
 * name (company) - description.
 ********************************************************************************/

package org.eclipse.tml.framework.device.additional;

import org.eclipse.osgi.util.NLS;

/**
 * Resources for externalized Strings of the TmL Emulator Core.
 */
public class DeviceAddResources extends NLS {
	

	private static String BUNDLE_NAME = "org.eclipse.tml.framework.device.additional.DeviceAddResources";//$NON-NLS-1$

	public static String TML_Device_Plugin_Name;
	public static String TML_Error;
	public static String TML_Resource_Not_Available;
	public static String TML_Handler_Not_Instanced;
	public static String TML_STATUS_UNAVAILABLE;
		
	static {
		NLS.initializeMessages(BUNDLE_NAME, DeviceAddResources.class);
	}

}
