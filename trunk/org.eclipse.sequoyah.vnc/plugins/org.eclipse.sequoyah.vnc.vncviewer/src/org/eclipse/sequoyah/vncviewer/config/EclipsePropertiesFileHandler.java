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
package org.eclipse.tml.vncviewer.config;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.eclipse.tml.vncviewer.VNCViewerPlugin;
import org.osgi.framework.Bundle;






public class EclipsePropertiesFileHandler implements IPropertiesFileHandler {

	

	
	
	public Properties loadPropertiesFile(String fileAddress) {

		Properties properties = new Properties(); 
		Bundle pluginBundle = VNCViewerPlugin.getDefault().getBundle();
		URL vncViewerConf = pluginBundle.getResource(fileAddress);
		
		try {
			InputStream vncViewerConfStream = vncViewerConf.openStream();
			properties.load(vncViewerConfStream);
		} catch (IOException e) {
			// TODO handle properly
			e.printStackTrace();
		}
		
		return properties;
	}
	
	

	public boolean savePropertiesFile(URL fileURL, Properties properties)
			throws Exception {

		return false;
	}



}
