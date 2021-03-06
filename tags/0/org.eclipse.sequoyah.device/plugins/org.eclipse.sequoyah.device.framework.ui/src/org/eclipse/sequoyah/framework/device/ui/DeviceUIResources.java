/********************************************************************************
 * Copyright (c) 2007 - 2009 Motorola Inc and others.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Fabio Fantato (Motorola)
 * 
 * Contributors:
 * Otavio Luiz Ferranti (Eldorado Research Institute) - bug#221733 - Adding data
 *                                                                    persistence
 * Yu-Fen Kuo (MontaVista) - bug#236476 - provide a generic device type
 * Fabio Fantato (Instituto Eldorado) - [263188] - Create new examples to support tutorial presentation
 * Fabio Fantato (Instituto Eldorado) - [243494] Change the reference implementation to work on Galileo
 * Daniel Barboza Franco (Eldorado Research Institute) - Bug [271682] - Default Wizard Page accepting invalid names
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
	public static String TML_Instance_Name_Duplicated_Error;
	public static String TML_Instance_Name_Invalid_Error;
	public static String TML_Emulator_Wizard_Project_Description_Duplicated_Error;
	public static String TML_Default_Device_Type_Wizard_Page_title;
	public static String TML_Default_Device_Type_Wizard_Page_message;
	public static String TML_Default_Device_Type_Wizard_Page_name;
	public static String TML_Default_Device_Type_Wizard_Page_deviceTypes;
	public static String DefaultDeviceTypeWizardPage_title;
	public static String DefaultDeviceTypeWizardPage_message;
	public static String DefaultDeviceTypeWizardPage_name;
	public static String DefaultDeviceTypeWizardPage_deviceTypes;
	public static String ConnectionInfoWizardPage_title;
	public static String ConnectionInfoWizardPage_message;
	public static String ConnectionInfoWizardPage_Host;
	public static String ConnectionInfoWizardPage_Port;
	public static String ConnectionInfoWizardPage_Display;
	
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, DeviceUIResources.class);
	}

}
