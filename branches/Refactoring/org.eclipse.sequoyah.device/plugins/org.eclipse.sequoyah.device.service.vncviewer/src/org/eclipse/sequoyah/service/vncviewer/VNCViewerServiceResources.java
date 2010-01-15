/********************************************************************************
 * Copyright (c) 2007 Motorola Inc.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Daniel Franco (Motorola)
 * 
 * Contributors:
 * name (company) - description.
 ********************************************************************************/

package org.eclipse.tml.service.vncviewer;

import org.eclipse.osgi.util.NLS;

/**
 * Resources for externalized Strings of the TmL Emulator Core.
 */
public class VNCViewerServiceResources extends NLS {
	

	private static String BUNDLE_NAME = "org.eclipse.tml.service.vncviewer.VNCViewerServiceResources";//$NON-NLS-1$

	public static String TML_VNCViewer_Service_Plugin_Name;
	public static String TML_Error;
	public static String TML_Resource_Not_Available;
	public static String TML_VNCViewer_Service;
	public static String TML_VNCViewer_Service_Update;
	
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, VNCViewerServiceResources.class);
	}

}
