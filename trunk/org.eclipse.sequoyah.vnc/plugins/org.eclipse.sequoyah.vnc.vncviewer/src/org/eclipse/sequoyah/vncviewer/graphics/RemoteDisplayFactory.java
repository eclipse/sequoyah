/********************************************************************************
 * Copyright (c) 2007 Motorola Inc. All rights reserved.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributor:
 * Daniel Franco (Motorola)
 *
 * Contributors:
 * Eugene Melekhov (Montavista) - Bug [227793] - Implementation of the several encodings, performance enhancement etc
 ********************************************************************************/

package org.eclipse.tml.vncviewer.graphics;

import java.util.Properties;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.tml.vncviewer.config.EclipsePropertiesFileHandler;
import org.eclipse.tml.vncviewer.config.IPropertiesFileHandler;
import org.eclipse.tml.vncviewer.config.VNCConfiguration;
import org.eclipse.tml.vncviewer.graphics.swt.img.SWTRemoteDisplayImg;


public class RemoteDisplayFactory {

	
	private static String DEFAULT_PROPERTIES_FILE = "resources/vnc_viewer.conf";
	
	/**
	 * This factory returns an IRemoteDisplay instance.
	 * 
	 * @param display the String that represents the desired instance. 
	 * @param parent the Composite to be used as the GUI components parent.
	 * @return the instantiated IRemoteDisplay
	 */
	public static IRemoteDisplay getDisplay(String display, Composite parent){
	
		
		IPropertiesFileHandler propertiesFileHandler = new EclipsePropertiesFileHandler();
		VNCConfiguration configurator = new VNCConfiguration(DEFAULT_PROPERTIES_FILE, propertiesFileHandler);
		
		Properties config = configurator.getConfigurationProperties();
	
		if (display.equals("SWTDisplay")){
//			return new SWTRemoteDisplayImgData(parent, config, propertiesFileHandler);
			return new SWTRemoteDisplayImg(parent, config, propertiesFileHandler);
		}
	
		return null;
	}
	
	
}
