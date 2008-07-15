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
 * Daniel Barboza Franco (Motorola) - Bug [233775] - Does not have a way to enter the session password for the vnc connection
 ********************************************************************************/

package org.eclipse.tml.service.vncviewer.handler;

import java.io.IOException;

import org.eclipse.tml.common.utilities.IPropertyConstants;
import org.eclipse.tml.framework.device.model.IInstance;
import org.eclipse.tml.framework.device.model.handler.IServiceHandler;
import org.eclipse.tml.framework.device.model.handler.ServiceHandler;
import org.eclipse.tml.service.vncviewer.VNCViewerServiceResources;
import org.eclipse.tml.service.vncviewer.VNCViewerServicePlugin;
import org.osgi.framework.Bundle;
import org.eclipse.tml.vncviewer.config.EclipsePropertiesFileHandler;
import org.eclipse.tml.protocol.PluginProtocolActionDelegate;
import org.eclipse.tml.protocol.lib.IProtocolImplementer;
import org.eclipse.tml.protocol.lib.ProtocolActionDelegate;
import org.eclipse.tml.protocol.lib.ProtocolMessage;
import org.eclipse.tml.vncviewer.network.VNCKeyEvent;
import org.eclipse.tml.vncviewer.vncviews.views.VNCViewerView;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class VNCViewerServiceHandler extends ServiceHandler {

	public void runService(IInstance instance) {

		VNCViewerServicePlugin.logInfo(VNCViewerServiceResources.TML_VNCViewer_Service+"->"+instance.getName());

		String host = instance.getProperties().getProperty(IPropertyConstants.HOST);
		int port = Integer.parseInt(instance.getProperties().getProperty(IPropertyConstants.PORT));
		String protoVersion = "VNC 3.3";
		String password = instance.getProperties().getProperty(IPropertyConstants.PASSWORD);
		
		try {
			
			if (instance.getStatus().equals("IDLE-VNC")){
				VNCViewerView.stop();
				try {
					PluginProtocolActionDelegate.stopProtocol(VNCViewerView.protocol);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}; 
			
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("org.eclipse.tml.vncviewer.vncviews.views.VNCViewerView");
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		if (VNCViewerView.protocol != null) {
				VNCViewerView.stop();
				try {
					PluginProtocolActionDelegate.stopProtocol(VNCViewerView.protocol);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
		VNCViewerView.start(host, port, protoVersion, password);
		
		
		IProtocolImplementer protocol = VNCViewerView.protocol;
		/*
		 * 
		 * 
		 *  The code below is a fix for QEMU-ARM 
		 *  Just for demo purposes
		 * 
		 * ****/
		if ( instance.getDevice().equals("org.eclipse.tml.device.qemuarm.qemuarmDevice")  ) {
		
			try {
				
				ProtocolMessage qemumsg = new ProtocolMessage(0x04);
				qemumsg.setFieldValue("downFlag", 1);
				qemumsg.setFieldValue("padding", 0);
				
				qemumsg.setFieldValue("key", 0xFFE3);
				ProtocolActionDelegate.sendMessageToServer(VNCViewerView.protocol, qemumsg);
				qemumsg.setFieldValue("key", 0xFFE9);
				ProtocolActionDelegate.sendMessageToServer(VNCViewerView.protocol, qemumsg);
				qemumsg.setFieldValue("key", 0x033);
				ProtocolActionDelegate.sendMessageToServer(VNCViewerView.protocol, qemumsg);

				qemumsg.setFieldValue("downFlag", 0);
				qemumsg.setFieldValue("key", 0xFFE3);
				ProtocolActionDelegate.sendMessageToServer(VNCViewerView.protocol, qemumsg);
				qemumsg.setFieldValue("key", 0xFFE9);
				ProtocolActionDelegate.sendMessageToServer(VNCViewerView.protocol, qemumsg);
				qemumsg.setFieldValue("key", 0x033);
				ProtocolActionDelegate.sendMessageToServer(VNCViewerView.protocol, qemumsg);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}

	public void updatingService(IInstance instance) {
		VNCViewerServicePlugin.logInfo(VNCViewerServiceResources.TML_VNCViewer_Service_Update+"->"+instance.getName());
	}

	
	public IServiceHandler newInstance() {
		return new VNCViewerServiceHandler();
	}
	
}
