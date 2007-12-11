package org.eclipse.tml.vncviewer.config;

import java.net.URL;
import java.util.Properties;



public interface IPropertiesFileHandler {


	


	
	/**
	 * Load the Properties file given by fileAddress.
	 * 
	 *  @param fileAdress the Properties file address.
	 *  @return the Properties read from the given file.
	 */	
	public Properties loadPropertiesFile(String fileAddress);
	
	
	//
	public boolean savePropertiesFile(URL fileURL, Properties properties) throws Exception;
	
	
	
	
	
}
