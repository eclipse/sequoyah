/********************************************************************************
 * Copyright (c) 2008 Motorola Inc.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Fabio Rigo (Eldorado) - Bug [244066] - The services are being run at one of the UI threads 
 * 
 * Contributors:
 ********************************************************************************/

package org.eclipse.tml.service.vncviewer.handler;

import java.io.IOException;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.tml.framework.device.model.IInstance;
import org.eclipse.tml.framework.device.model.handler.IServiceHandler;
import org.eclipse.tml.framework.device.model.handler.ServiceHandler;
import org.eclipse.tml.protocol.PluginProtocolActionDelegate;
import org.eclipse.tml.vncviewer.vncviews.views.VNCViewerView;

public class UnplugVNCViewerServiceHandler extends ServiceHandler
{

    public UnplugVNCViewerServiceHandler()
    {

        //VNCViewerView.stop();
        //VNCViewerView.

    }

    @Override
    public IServiceHandler newInstance()
    {
        return new UnplugVNCViewerServiceHandler();
    }

    @Override
    public IStatus runService(IInstance instance, Map<Object, Object> arguments,
            IProgressMonitor monitor)
    {
        // TODO Auto-generated method stub

        VNCViewerView.stop();

        try
        {
            PluginProtocolActionDelegate.stopProtocol(VNCViewerView.protocol);
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return Status.OK_STATUS;
    }

    @Override
    public IStatus updatingService(IInstance instance, IProgressMonitor monitor)
    {
        // TODO Auto-generated method stub
        return Status.OK_STATUS;
    }

}
