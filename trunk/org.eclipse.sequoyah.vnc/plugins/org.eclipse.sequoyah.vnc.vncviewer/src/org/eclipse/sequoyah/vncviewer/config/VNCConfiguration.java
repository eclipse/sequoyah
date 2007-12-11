package org.eclipse.tml.vncviewer.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.eclipse.tml.vncviewer.VNCViewerPlugin;
import org.osgi.framework.Bundle;



public class VNCConfiguration {
	

	
	private Properties configurationProperties;
	private IPropertiesFileHandler propertiesFileHandler;

	/*
	public VNCConfiguration(String filename) {
	
		//String file = "C:/temp/vnc_viewer.conf";
		configurationProperties = loadPropertiesFile(filename);
	}
	*/
	
	/*
	public Properties getDefaultConfigurationProperties()
	{
		Properties properties = new Properties(); 
		Bundle pluginBundle = VNCViewerPlugin.getDefault().getBundle();
		URL vncViewerConf = pluginBundle.getResource("resources/vnc_viewer.conf");
		
		try {
			InputStream vncViewerConfStream = vncViewerConf.openStream();
			properties.load(vncViewerConfStream);
		} catch (IOException e) {
			// TODO handle properly
			e.printStackTrace();
		}
		
		return properties;
	}
	*/

	
	public VNCConfiguration(String filename, IPropertiesFileHandler propertiesFileHandler) {
		
		//String file = "C:/temp/vnc_viewer.conf";
		//configurationProperties = loadPropertiesFile(filename);
		
		this.propertiesFileHandler = propertiesFileHandler;
		configurationProperties = propertiesFileHandler.loadPropertiesFile(filename);
	}

	
	
	public Properties getConfigurationProperties() {
		
		return configurationProperties;
	}
	
	
	
	/*
	public Properties getConfigurationProperties(String filename){
		
		
		return loadPropertiesFile(filename);
	}
	
	
	private static Properties loadPropertiesFile(String filename) {

		Properties properties = new Properties(); 
		File propFile = new File (filename);
		
		try {
			properties.load( new FileInputStream(propFile));
			
		} catch (IOException e) {
			//TODO handle properly
			e.printStackTrace();
		}

		return properties;
		
	}
	*/
	
	private static void checkProperties(){
		
	}



/*
	public void addVncProperty(String key, String value) {
		this.vncProperties.put(key, value);
	}




	public String getVncProperty(String key) {
		return this.vncProperties.getProperty(key);
	}
	*/
	

}


