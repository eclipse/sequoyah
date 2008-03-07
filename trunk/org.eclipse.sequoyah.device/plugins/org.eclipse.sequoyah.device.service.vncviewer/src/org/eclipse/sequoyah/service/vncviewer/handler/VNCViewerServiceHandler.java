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
 * name (company) - description.
 ********************************************************************************/

package org.eclipse.tml.service.vncviewer.handler;

import org.eclipse.tml.common.utilities.IPropertyConstants;
import org.eclipse.tml.framework.device.model.IInstance;
import org.eclipse.tml.framework.device.model.handler.IServiceHandler;
import org.eclipse.tml.framework.device.model.handler.ServiceHandler;
import org.eclipse.tml.service.vncviewer.VNCViewerServiceResources;
import org.eclipse.tml.service.vncviewer.VNCViewerServicePlugin;
import org.osgi.framework.Bundle;
import org.eclipse.tml.vncviewer.config.EclipsePropertiesFileHandler;
import org.eclipse.tml.vncviewer.exceptions.ProtoClientException;
import org.eclipse.tml.vncviewer.network.IProtoClient;
import org.eclipse.tml.vncviewer.network.VNCKeyEvent;
import org.eclipse.tml.vncviewer.vncviews.views.VNCViewerView;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class VNCViewerServiceHandler extends ServiceHandler {

	public void runService(IInstance instance) {
		VNCViewerServicePlugin.logInfo(VNCViewerServiceResources.TML_VNCViewer_Service+"->"+instance.getName());
	
		//instance.get

		String host = instance.getProperties().getProperty(IPropertyConstants.HOST);
		int port = Integer.parseInt(instance.getProperties().getProperty(IPropertyConstants.PORT));
		String protoVersion = "VNC 3.3";
		
		
		
		try {
			
			if (instance.getStatus().equals("IDLE-VNC")){
				VNCViewerView.stop();
				VNCViewerView.protocol.stopProtocol();
			}; 
			
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("org.eclipse.tml.vncviewer.vncviews.views.VNCViewerView");
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtoClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		if (VNCViewerView.protocol != null) {
			 try {
				VNCViewerView.stop();
				VNCViewerView.protocol.stopProtocol();
			} catch (ProtoClientException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		VNCViewerView.start(host, port, protoVersion);
		
		
		IProtoClient protocol = VNCViewerView.protocol;
		/*
		 * 
		 * 
		 *  The code below is a fix for QEMU-ARM 
		 *  Just for demo purposes
		 * 
		 * ****/
		if ( instance.getDevice().equals("org.eclipse.tml.device.qemuarm.qemuarmDevice")  ) {
		
			try {
				protocol.keyEvent(new VNCKeyEvent(0xFFE3, true));
				protocol.keyEvent(new VNCKeyEvent(0xFFE9, true));
				protocol.keyEvent(new VNCKeyEvent(0x033, true));
				
				protocol.keyEvent(new VNCKeyEvent(0xFFE3, false));
				protocol.keyEvent(new VNCKeyEvent(0xFFE9, false));
				protocol.keyEvent(new VNCKeyEvent(0x033, false));
				
				
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
