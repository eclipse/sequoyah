/********************************************************************************
 * Copyright (c) 2007-2008 Motorola Inc and Others. All rights reserved.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributor:
 * Daniel Franco (Eldorado Research Institute)
 *
 * Contributors:
 * Fabio Rigo (Eldorado Research Institute) - [221741] - Support to VNC Protocol Extension
 * Eugene Melekhov (Montavista) - [227793] - Implementation of the several encodings, performance enhancement etc
 * Daniel Barboza Franco (Eldorado Research Institute) - [233775] - Does not have a way to enter the session password for the vnc connection
 * Daniel Barboza Franco (Eldorado Research Institute) - [233062] - Protocol connection port is static.
 * Fabio Rigo (Eldorado Research Institute) -  [238191] - Enhance exception handling
 * Daniel Barboza Franco (Eldorado Research Institute) -  [233064] - Add reconnection mechanism to avoid lose connection with the protocol
 * Fabio Rigo (Eldorado Research Institute) -  [246212] - Enhance encapsulation of protocol implementer
 * Daniel Barboza Franco (Eldorado Research Institute) -  [243167] - Zoom mechanism not working properly 
 * Daniel Barboza Franco (Eldorado Research Institute) - Bug [233121] - There is no support for proxies when connecting the protocol 
 * Daniel Barboza Franco (Eldorado Research Institute) - Bug [246585] - VncViewerService is not working anymore after changes made in ProtocolHandle
 *******************************************************************************/

package org.eclipse.tml.vncviewer.vncviews.views;

import static org.eclipse.tml.vncviewer.VNCViewerPlugin.log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.tml.protocol.PluginProtocolActionDelegate;
import org.eclipse.tml.protocol.lib.ProtocolHandle;
import org.eclipse.tml.vncviewer.graphics.RemoteDisplayFactory;
import org.eclipse.tml.vncviewer.graphics.swt.SWTRemoteDisplay;
import org.eclipse.tml.vncviewer.network.VNCProtocolExceptionHandler;
import org.eclipse.ui.part.ViewPart;

/**
 * The VNCViewerView class implements the Eclipse View that contains a VNC
 * viewer.
 */
public class VNCViewerView extends ViewPart {

	private static final String VIEWER_COULD_NOT_BE_STARTED = "Viewer could not be started: ";

	private static final String SWTDISPLAY = "SWTDisplay";

	private static SWTRemoteDisplay swtDisplay;

	private static boolean running = false;

	public static ProtocolHandle protocolHandle;
	
	private static int zoomFactor = 1;

	public void createPartControl(Composite parent) {

		swtDisplay = (SWTRemoteDisplay) RemoteDisplayFactory.getDisplay(
				SWTDISPLAY, parent);
		running = true;

		if (VNCViewerView.protocolHandle != null) {
			try {
				swtDisplay.start(protocolHandle);
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
		if (swtDisplay != null)
			swtDisplay.dispose();
		super.dispose();
	}

	/**
	 * Performs the start action into the VNC Component.
	 */
	synchronized public static void start(String host, int port,
			String protoVersion, String password, boolean bypassProxy) {

		if ((running) && (swtDisplay != null)) {

			if (swtDisplay.isActive()) {
				swtDisplay.stop();
			}

			try {

				HashMap<String, Object> parameters = new HashMap<String, Object>();
				String protocolId = ProtocolIdTranslator
						.getProtocolId(protoVersion);

				parameters.put("password", password);
				parameters.put("connectionRetries", swtDisplay
						.getConnectionRetries());
				
				parameters.put("bypassProxy", new Boolean(bypassProxy));
				
				VNCViewerView.protocolHandle = PluginProtocolActionDelegate
						.startClientProtocol(protocolId,
								new VNCProtocolExceptionHandler(), host, port,
								parameters);

				swtDisplay.start(protocolHandle);

			} catch (Exception e) {

				log(VNCViewerView.class).error(
						VIEWER_COULD_NOT_BE_STARTED + e.getMessage());

				GC gc = new GC(swtDisplay.getCanvas());
				gc.fillRectangle(0, 0, swtDisplay.getScreenWidth(), swtDisplay
						.getScreenHeight());
				gc.dispose();

			}
		}
	}

	/**
	 * Performs the stop action into the VNC Component.
	 */
	synchronized public static void stop() {

		if ((running) && (swtDisplay != null)) {

			if (swtDisplay.isActive()) {
				swtDisplay.getDisplay().syncExec(new Runnable(){
					public void run() {
						swtDisplay.stop();						
					}
	
				});
				
			}

		}

	}
	
	public static void zoomIn(){
		//double zoom = swtDisplay.getZoomFactor();
		
		double newzoom = 1;
		
		if (zoomFactor == -2) {
			zoomFactor = 1;
		} else {
			zoomFactor++;
		}
			
		if (zoomFactor >= 1) {
			newzoom = zoomFactor;
		}
		else {
			newzoom = ((double)1) / -zoomFactor;
		}
		
		swtDisplay.setZoomFactor(newzoom);
		try {
			swtDisplay.updateRequest(false); //full request
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void zoomOut(){

		double newzoom = 1;
		
		if (zoomFactor == 1) {
			zoomFactor = -2;
		} else {
			zoomFactor--;
		}
			
		if (zoomFactor >= 1) {
			newzoom = zoomFactor;
		}
		else {
			newzoom = ((double)1) / -zoomFactor;
		}
		
		swtDisplay.setZoomFactor(newzoom);
		try {
			swtDisplay.updateRequest(false); //full request
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void stopProtocol() throws IOException {

		if (protocolHandle != null){
			PluginProtocolActionDelegate.stopProtocol(protocolHandle);
		}
	}


}
