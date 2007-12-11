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

package org.eclipse.tml.vncviewer.vncviews.views;

import static org.eclipse.tml.vncviewer.VNCViewerPlugin.log;

import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.tml.vncviewer.graphics.RemoteDisplayFactory;
import org.eclipse.tml.vncviewer.graphics.swt.SWTRemoteDisplay;
import org.eclipse.tml.vncviewer.network.IProtoClient;
import org.eclipse.tml.vncviewer.network.ProtocolFactory;
import org.eclipse.ui.part.ViewPart;


/**
 * The VNCViewerView class implements the Eclipse View that contains a VNC viewer. 
 */
public class VNCViewerView extends ViewPart{
	

	private static SWTRemoteDisplay swtDisplay;
	
	private static boolean running=false;

	
	private static IProtoClient protocol;


	public void createPartControl(Composite parent) {
		//swtDisplay = new SWTRemoteDisplay(parent);
		
		swtDisplay = (SWTRemoteDisplay) RemoteDisplayFactory.getDisplay("SWTDisplay", parent);
		running = true;
		
		if (VNCViewerView.protocol != null) {
			try {
				swtDisplay.start(protocol);
			} catch (Exception e) {
				// TODO handle properly
				e.printStackTrace();
			}
		} 
		
	}


	public void setFocus() {
		if (swtDisplay != null) {
			swtDisplay.setFocus();
		}
	}


	public void dispose() {
		
		running = false;
		if (swtDisplay != null) swtDisplay.dispose();
		super.dispose();
	}


	/**
	 * Performs the start action into the VNC Component.
	 */
	synchronized public static void start(String host, int port, String protoVersion){
		
		if ((running) && (swtDisplay != null)) {
			
			if (swtDisplay.isActive()) {
				swtDisplay.stop();
			}
			
			try {
				
				IProtoClient protocol = ProtocolFactory.getProtocol(protoVersion);

				VNCViewerView.protocol = protocol;
				
				protocol.runProtocol(host, port);
				swtDisplay.start(protocol);
				//swtDisplay.start(host, port, protocol);

			} catch (Exception e){
	
				log(VNCViewerView.class).error("Viewer could not be started: " + e.getMessage());
	
				GC gc = new GC(swtDisplay.getCanvas());
	    		gc.fillRectangle(0, 0, swtDisplay.getScreenWidth(), swtDisplay.getScreenHeight());
	    		gc.dispose();		
	    		
			}
		}
 	}

	/**
	 * Performs the stop action into the VNC Component.
	 */
	synchronized public static void stop(){
		
		if ((running) && (swtDisplay != null)) {
			
			if (swtDisplay.isActive()) {
				swtDisplay.stop();
			}
		}
	}
	
}
