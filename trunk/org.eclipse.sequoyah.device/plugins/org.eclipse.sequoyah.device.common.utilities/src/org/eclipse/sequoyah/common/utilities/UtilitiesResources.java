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

package org.eclipse.tml.common.utilities;

import org.eclipse.osgi.util.NLS;

/**
 * Resources for externalized Strings of the TmL Emulator Core.
 */
public class UtilitiesResources extends NLS {
	

	private static String BUNDLE_NAME = "org.eclipse.tml.common.utilities.UtilitiesResources";//$NON-NLS-1$

	public static String TML_Utilities_Plugin_Name;
	public static String TML_Error;
	public static String TML_Resource_Not_Available;
	
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, UtilitiesResources.class);
	}

}
