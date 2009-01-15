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
 * Fabio Rigo - Bug [221741] - Support to VNC Protocol Extension
 * Eugene Melekhov (Montavista) - Bug [227793] - Implementation of the several encodings, performance enhancement etc
 * Daniel Barboza Franco (Eldorado Research Institute) - Bug [247840] - Mouse click not working
 ********************************************************************************/

package org.eclipse.tml.vncviewer.graphics.swt;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.tml.protocol.lib.ProtocolMessage;
import org.eclipse.tml.vncviewer.config.IPropertiesFileHandler;
import org.eclipse.tml.vncviewer.config.IVNCProperties;


/**
 * This class provides translations from SWT events to VNC specific information.
 */
public class SWTVNCEventTranslator {

	// Number of buttons as defined on the RFB protocol (8 bits for buttons).
	private static final int BUTTONS = 8; 
	
	private boolean shiftPressed = false;
	private boolean buttonPressed[];

	private Hashtable<Integer, Integer> swtToKeysymCodes;

	private Properties configProperties;
	private IPropertiesFileHandler propertiesFileHandler;

	public SWTVNCEventTranslator(Properties configProperties,
			IPropertiesFileHandler propertiesFileHandler) {

		this.configProperties = configProperties;
		this.propertiesFileHandler = propertiesFileHandler;

		buttonPressed = new boolean[BUTTONS];
		
		for(int i=0; i<BUTTONS; i++) {
			buttonPressed[i] = false;
		}
		
		initKeysyms();
	
	}
	
	

	public void initKeysyms(){
	
		String swtkeysymsFile = configProperties.getProperty(IVNCProperties.KEYSYM_SWT_PROPERTIES_FILE);
		String keysymsFile = configProperties.getProperty(IVNCProperties.KEYSYM_PROPERTIES_FILE);
		String swtkeysFile = configProperties.getProperty(IVNCProperties.SWTKEYS_PROPERTIES_FILE);				

		Properties keysymSwt = propertiesFileHandler.loadPropertiesFile(swtkeysymsFile);
		Properties keysyms = propertiesFileHandler.loadPropertiesFile(keysymsFile);
		Properties swtkeys = propertiesFileHandler.loadPropertiesFile(swtkeysFile);
		
		
		swtToKeysymCodes = new Hashtable();

		/* Generates the SWT x Keysym map from files */
		for (Enumeration <Object> swtcodes = keysymSwt.keys() ; swtcodes.hasMoreElements() ;) {
			
			String key = (String) swtcodes.nextElement();
			
			Integer swtCode = new Integer(swtkeys.getProperty(key));
			Integer keysymCode = Integer.decode(keysyms.getProperty(keysymSwt.getProperty(key)));	
			
			swtToKeysymCodes.put(swtCode, keysymCode);
			
		} 
		

	}
	
	
	
	/**
	 * Returns a MouseEvent from a given SWT Event.
	 * @param swtEvent the Event to be translated.
	 */
	public ProtocolMessage getMouseEventMessage(Event swtEvent) {

		// Creates the mouse event message, providing the
		// necessary coordinates
		ProtocolMessage message = new ProtocolMessage(5);
		if (swtEvent.button > 0 ) {
			if (swtEvent.type == SWT.MouseDown) {
				if (!buttonPressed[swtEvent.button - 1]) {
					buttonPressed[swtEvent.button - 1] = true;
				}
			}
			else if (swtEvent.type == SWT.MouseUp){
				if (buttonPressed[swtEvent.button - 1]) {
					buttonPressed[swtEvent.button - 1] = false;
				}
			}
		}
		
		// Create the mask as specified in the RFB protocol
		byte mask = 0;
		for (int i=0; i<BUTTONS; i++) {
			if (buttonPressed[i]) {
				mask = (byte)((byte)mask | (int)Math.pow(2,i));
			}
		}
		
		message.setFieldValue("buttonMask", mask); //$NON-NLS-1$
		message.setFieldValue("x-position", swtEvent.x); //$NON-NLS-1$
		message.setFieldValue("y-position", swtEvent.y); //$NON-NLS-1$

		return message;
	}

	/**
	 * Returns a KeyEvent from a given SWT Event.
	 * @param swtEvent the Event to be translated.
	 */
	public ProtocolMessage getKeyEventMessage(Event swtEvent) {

		int keysym;
		boolean pressed;

		if (swtEvent.keyCode == SWT.SHIFT) {
			shiftPressed = (swtEvent.type == SWT.KeyDown);
		}

		keysym = getKeysym(swtEvent.keyCode);
		pressed = (swtEvent.type == SWT.KeyDown);

		/*
		 * If the shift key is pressed, SWT keeps the original key in the
		 * Event.character field. As the VNC Protocol expects the original key
		 * (shift press was already sent) we have to handle the case when shift
		 * is pressed.
		 */
		ProtocolMessage message = new ProtocolMessage(4);
		message.setFieldValue("downFlag", pressed ? 1 : 0); //$NON-NLS-1$
		if (!shiftPressed) {
			message.setFieldValue("key", keysym); //$NON-NLS-1$
		} else {
			message.setFieldValue("key", (int) swtEvent.character); //$NON-NLS-1$
		}
		return message;

	}

	/**
	 * Returns a keysym from a given keyCode.
	 * @param swtKeyCode the keyCode to be translated. 
	 */
	public int getKeysym(int swtKeyCode) {

		int keysym;

		Integer keysymObj = swtToKeysymCodes.get(new Integer(swtKeyCode));

		if (keysymObj != null) {
			keysym = keysymObj.intValue();
		}
		else {
			keysym = swtKeyCode;
		}
		
		return keysym;
	}

}
