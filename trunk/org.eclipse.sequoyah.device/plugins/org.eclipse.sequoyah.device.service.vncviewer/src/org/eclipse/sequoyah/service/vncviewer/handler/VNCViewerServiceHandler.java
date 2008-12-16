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
 * Fabio Rigo (Eldorado) - Bug [244066] - The services are being run at one of the UI threads
 * Fabio Rigo (Eldorado Research Institute) - [246212] - Enhance encapsulation of protocol implementer 
 * Daniel Barboza Franco (Eldorado Research Institute) - Bug [233121] - There is no support for proxies when connecting the protocol
 * Daniel Barboza Franco (Eldorado Research Institute) - Bug [246585] - VncViewerService is not working anymore after changes made in ProtocolHandle
 * Yu-Fen Kuo (MontaVista)  - [236476] - provide a generic device type
 ********************************************************************************/

package org.eclipse.tml.service.vncviewer.handler;

import java.io.IOException;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.tml.common.utilities.BasePlugin;
import org.eclipse.tml.common.utilities.IPropertyConstants;
import org.eclipse.tml.framework.device.model.IInstance;
import org.eclipse.tml.framework.device.model.handler.IServiceHandler;
import org.eclipse.tml.framework.device.model.handler.ServiceHandler;
import org.eclipse.tml.protocol.PluginProtocolActionDelegate;
import org.eclipse.tml.protocol.lib.ProtocolHandle;
import org.eclipse.tml.protocol.lib.ProtocolMessage;
import org.eclipse.tml.service.vncviewer.VNCViewerServiceResources;
import org.eclipse.tml.vncviewer.vncviews.views.VNCViewerView;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class VNCViewerServiceHandler extends ServiceHandler
{

    @Override
    public IStatus runService(IInstance instance, Map<Object, Object> arguments,
            IProgressMonitor monitor)
    {

        BasePlugin.logInfo(VNCViewerServiceResources.TML_VNCViewer_Service + "->" //$NON-NLS-1$
                + instance.getName());

        String host = instance.getProperties().getProperty(IPropertyConstants.HOST);
        int port = Integer.parseInt(instance.getProperties().getProperty(IPropertyConstants.PORT));
        String protoVersion = "VNC 3.3"; //$NON-NLS-1$
        String password = instance.getProperties().getProperty(IPropertyConstants.PASSWORD);

        try
        {

            if (instance.getStatus().equals("IDLE-VNC")) //$NON-NLS-1$
            {
                VNCViewerView.stop();
                try
                {
                	ProtocolHandle handle = VNCViewerView.protocolHandle;
                    PluginProtocolActionDelegate.stopProtocol(handle);
                }
                catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            ;

            PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable(){

				public void run() {
					try {
						PlatformUI.getWorkbench().getWorkbenchWindows()[0].getActivePage().showView("org.eclipse.tml.vncviewer.vncviews.views.VNCViewerView"); //$NON-NLS-1$
					} catch (PartInitException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
            	
            });
            
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (VNCViewerView.protocolHandle!= null)
        {
            VNCViewerView.stop();
            try
            {
                PluginProtocolActionDelegate.stopProtocol(VNCViewerView.protocolHandle);
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        VNCViewerView.start(host, port, protoVersion, password, false);

        /*
         *  The code below is a fix for QEMU-ARM which expect 
         *  the sequence ctrl+alt+3 to show it's display. 
         *****/
        if (instance.getDeviceTypeId().equals("org.eclipse.tml.device.qemuarm.qemuarmDevice"))
        {

            try
            {

            	ProtocolHandle handle = VNCViewerView.protocolHandle;
                ProtocolMessage qemumsg = new ProtocolMessage(0x04);
                qemumsg.setFieldValue("downFlag", 1); //$NON-NLS-1$
                qemumsg.setFieldValue("padding", 0); //$NON-NLS-1$

                qemumsg.setFieldValue("key", 0xFFE3); //$NON-NLS-1$
                PluginProtocolActionDelegate.sendMessageToServer(handle, qemumsg);
                qemumsg.setFieldValue("key", 0xFFE9); //$NON-NLS-1$
                PluginProtocolActionDelegate.sendMessageToServer(handle, qemumsg);
                qemumsg.setFieldValue("key", 0x033); //$NON-NLS-1$
                PluginProtocolActionDelegate.sendMessageToServer(handle, qemumsg);

                qemumsg.setFieldValue("downFlag", 0); //$NON-NLS-1$
                qemumsg.setFieldValue("key", 0xFFE3); //$NON-NLS-1$
                PluginProtocolActionDelegate.sendMessageToServer(handle, qemumsg);
                qemumsg.setFieldValue("key", 0xFFE9); //$NON-NLS-1$
                PluginProtocolActionDelegate.sendMessageToServer(handle, qemumsg);
                qemumsg.setFieldValue("key", 0x033); //$NON-NLS-1$
                PluginProtocolActionDelegate.sendMessageToServer(handle, qemumsg);

            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return Status.OK_STATUS;
    }

    @Override
    public IStatus updatingService(IInstance instance, IProgressMonitor monitor)
    {
        BasePlugin.logInfo(VNCViewerServiceResources.TML_VNCViewer_Service_Update
                + "->" + instance.getName()); //$NON-NLS-1$
        return Status.OK_STATUS;
    }

    @Override
    public IServiceHandler newInstance()
    {
        return new VNCViewerServiceHandler();
    }

}
