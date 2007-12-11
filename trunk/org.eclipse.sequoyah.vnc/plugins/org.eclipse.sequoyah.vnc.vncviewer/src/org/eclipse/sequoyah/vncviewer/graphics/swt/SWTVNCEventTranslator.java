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
 * {Name} (company) - description of contribution.
 ********************************************************************************/

package org.eclipse.tml.vncviewer.graphics.swt;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.tml.vncviewer.VNCViewerPlugin;
import org.eclipse.tml.vncviewer.config.IVNCProperties;
import org.eclipse.tml.vncviewer.config.VNCConfiguration;
import org.eclipse.tml.vncviewer.network.VNCKeyEvent;
import org.eclipse.tml.vncviewer.network.VNCMouseEvent;
import org.osgi.framework.Bundle;


/**
 * This class provides translations from SWT events to VNC specific information.
 */
public class SWTVNCEventTranslator {
	
	private static boolean shiftPressed = false;
	private static boolean buttonPressed = false;

	
	//private static Hashtable <Integer, Integer> swtKeysymsMap;
	
	/*
	private static Properties keysyms;
	private static Properties swtkeys;
	*/
	private static Hashtable<Integer, Integer> swtToKeysymCodes;
	
	
	/*
	public static void addSWTKeysymAssociation(int swtKey, int keysymValue){
		
		if (swtKeysymsMap == null) {
			swtKeysymsMap = new Hashtable();
		}
		
		swtKeysymsMap.put(new Integer(swtKey), new Integer(keysymValue));
	}
*/
	
	

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
	
	
	
	private static Properties loadDefaultPropertiesFile(String filename) {

		Properties properties = new Properties(); 
		Bundle pluginBundle = VNCViewerPlugin.getDefault().getBundle();
		URL vncViewerConf = pluginBundle.getResource(filename);
		
		try {
			InputStream vncViewerConfStream = vncViewerConf.openStream();
			properties.load(vncViewerConfStream);
		} catch (IOException e) {
			// TODO handle properly
			e.printStackTrace();
		}
		

		return properties;
		
	}
	
	
	
	public static void initKeysyms(){
	
		/*
		addSWTKeysymAssociation(SWT.MouseUp, VNCMouseEvent.MOUSE_UP);
		addSWTKeysymAssociation(SWT.MouseDown, VNCMouseEvent.MOUSE_DOWN);
		addSWTKeysymAssociation(SWT.MouseMove, VNCMouseEvent.MOUSE_MOVE);
		*/
		
		//InputStream inStream;
		
	
		
		//URL url = WorkbenchPlugin.getDefault().getBundle().getResource("C:/temp/SWT_to_keysyms.prop");  //project.getFile(getEditorInput().getName());
		//url.get
		 
		//IFile ifile;
		//ifile.

		//inStream = ifile.getContents();
	 
		
		Properties conf = VNCConfiguration.getConfigurationProperties();
		
		String swtkeysymsFile = conf.getProperty(IVNCProperties.KEYSYM_SWT_PROPERTIES_FILE);
		String keysymsFile = conf.getProperty(IVNCProperties.KEYSYM_PROPERTIES_FILE);
		String swtkeysFile = conf.getProperty(IVNCProperties.SWTKEYS_PROPERTIES_FILE);				

		/*
		Properties keysymSwt = loadPropertiesFile(swtkeysymsFile);
		Properties keysyms = loadPropertiesFile(keysymsFile);
		Properties swtkeys = loadPropertiesFile(swtkeysFile);
		*/

		/*
		Properties keysymSwt = loadDefaultPropertiesFile(filename) SWT_to_keysyms.prop");
		Properties keysyms = loadDefaultPropertiesFile(filename)  C:/temp/keysyms.prop");
		Properties swtkeys = loadDefaultPropertiesFile(filename)  C:/temp/swtkeys.prop");
		*/

		Properties keysymSwt = loadDefaultPropertiesFile(swtkeysymsFile);
		Properties keysyms = loadDefaultPropertiesFile(keysymsFile);
		Properties swtkeys = loadDefaultPropertiesFile(swtkeysFile);

		
		swtToKeysymCodes = new Hashtable();
		
		for (Enumeration <Object> swtcodes = keysymSwt.keys() ; swtcodes.hasMoreElements() ;) {
			
			String key = (String) swtcodes.nextElement();
			
			Integer swtCode = new Integer(swtkeys.getProperty(key));
			
			//Integer keysymCode = new Integer(keysyms.getProperty(keysymSwt.getProperty(key)));
			Integer keysymCode = Integer.decode(keysyms.getProperty(keysymSwt.getProperty(key)));	
			
			swtToKeysymCodes.put(swtCode, keysymCode);
			
		} 
		

	}
	
	
	
	/**
	 * Returns a MouseEvent from a given SWT Event.
	 * @param swtEvent the Event to be translated.
	 */
	public static VNCMouseEvent getMouseEvent(Event swtEvent){
	
		int eventType = -1; 
		
		// button mask
		switch (swtEvent.type) {
		
			case (SWT.MouseUp):
				eventType = VNCMouseEvent.MOUSE_UP;
				buttonPressed = false;
			
				break;

			case (SWT.MouseDown):
				eventType = VNCMouseEvent.MOUSE_DOWN;
				buttonPressed = true;
				
				break;

			case (SWT.MouseMove):
				eventType = VNCMouseEvent.MOUSE_MOVE;
				break;
		
			default:
				break;
					
		}
		
		
		return new VNCMouseEvent(swtEvent.button, eventType, swtEvent.x, swtEvent.y, buttonPressed);
		
				
	}
	
	
	
	/**
	 * Returns a KeyEvent from a given SWT Event.
	 * @param swtEvent the Event to be translated.
	 */
	public static VNCKeyEvent getKeyEvent(Event swtEvent) {

		initKeysyms();
		
		
		
		int keysym;
		boolean pressed;
		
		if (swtEvent.keyCode == SWT.SHIFT) {
			shiftPressed = (swtEvent.type == SWT.KeyDown);
		}
		
		keysym = getKeysym(swtEvent.keyCode);
		pressed = (swtEvent.type == SWT.KeyDown);
		
		
		/* If the shift key is pressed, SWT keeps the original key in the Event.character field.
		 * As the VNC Protocol expects the original key (shift press was already sent) we have to
		 * handle the case when shift is pressed. */
		if (!shiftPressed) {
			return new VNCKeyEvent(keysym, pressed);	
		} 
		else {
			return new VNCKeyEvent((int) swtEvent.character, pressed);
		}
		
	}
	
	
	/**
	 * Returns a keysym from a given keyCode.
	 * @param swtKeyCode the keyCode to be translated. 
	 */
	public static int getKeysym(int swtKeyCode) {
		
		int keysym;
		
		Integer keysymObj = swtToKeysymCodes.get(new Integer(swtKeyCode));
		
		if (keysymObj != null) {
			keysym = keysymObj.intValue();
		}
		else {
			keysym = swtKeyCode;
		}
		
		
		
		//swtKeyCode
		
		
		/*
		switch (swtKeyCode) {
		
			case(SWT.CONTROL): 
				keysym = KeysymConstants.XK_CONTROL_L;
				break;
				
			case(SWT.SHIFT):
				keysym = KeysymConstants.XK_SHIFT_L;
				break;
				
			case(SWT.ALT):
				keysym = KeysymConstants.XK_ALT_L;
				break;

				
			default:
				
				
				keysym = swtKeyCode;
				break;
			
		}
		
		*/
		
		return keysym;
	}
	

}
