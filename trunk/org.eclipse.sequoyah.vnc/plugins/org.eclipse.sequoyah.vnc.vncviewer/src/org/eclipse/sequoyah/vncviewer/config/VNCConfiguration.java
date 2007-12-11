package org.eclipse.tml.vncviewer.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.eclipse.tml.vncviewer.VNCViewerPlugin;
import org.osgi.framework.Bundle;



public class VNCConfiguration {
	

	
	//private Properties configurationProperties;

	/*
	public VNCConfiguration(String filename) {
	
		//String file = "C:/temp/vnc_viewer.conf";
		configurationProperties = loadPropertiesFile(filename);
	}
	*/
	
	
	public static Properties getConfigurationProperties()
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
	
	
	
	public static Properties getConfigurationProperties(String filename){
		
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


