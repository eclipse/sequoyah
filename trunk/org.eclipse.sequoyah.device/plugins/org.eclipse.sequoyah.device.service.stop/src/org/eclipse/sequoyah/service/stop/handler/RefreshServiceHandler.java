/********************************************************************************
 * Copyright (c) 2007 Motorola Inc.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Fabio Fantato (Motorola)
 * 
 * Contributors:
 * Fabio Rigo (Eldorado) - Bug [244066] - The services are being run at one of the UI threads
 ********************************************************************************/

package org.eclipse.tml.service.stop.handler;

import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.tml.framework.device.model.IInstance;
import org.eclipse.tml.framework.device.model.handler.IServiceHandler;
import org.eclipse.tml.framework.device.model.handler.ServiceHandler;
import org.eclipse.tml.service.stop.StopServicePlugin;
import org.eclipse.tml.service.stop.StopServiceResources;

public class RefreshServiceHandler extends ServiceHandler  {

	public IStatus runService(IInstance instance, Map<Object , Object> arguments,  IProgressMonitor monitor) {
		StopServicePlugin.logInfo(StopServiceResources.TML_Refresh_Service+"->"+instance.getName());
		return Status.OK_STATUS;
	}
	
	public IStatus updatingService(IInstance instance, IProgressMonitor monitor) {
		StopServicePlugin.logInfo(StopServiceResources.TML_Refresh_Service_Update+"->"+instance.getName());
		return Status.OK_STATUS;
	}
	
	@Override
	public IServiceHandler newInstance() {		
		return new RefreshServiceHandler();
	}
}
