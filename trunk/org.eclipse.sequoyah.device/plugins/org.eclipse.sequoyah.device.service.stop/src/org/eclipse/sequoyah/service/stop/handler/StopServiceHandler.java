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
import org.eclipse.tml.common.utilities.BasePlugin;
import org.eclipse.tml.framework.device.model.IInstance;
import org.eclipse.tml.framework.device.model.handler.IServiceHandler;
import org.eclipse.tml.framework.device.model.handler.ServiceHandler;
import org.eclipse.tml.service.stop.StopServiceResources;

public class StopServiceHandler extends ServiceHandler {

	public IStatus runService(IInstance instance, Map<Object , Object> arguments , IProgressMonitor monitor) {
		BasePlugin.logInfo(StopServiceResources.TML_Stop_Service+"->"+instance.getName()); //$NON-NLS-1$
		return Status.OK_STATUS;		
	}

	public IStatus updatingService(IInstance instance, IProgressMonitor monitor) {
		BasePlugin.logInfo(StopServiceResources.TML_Stop_Service_Update+"->"+instance.getName()); //$NON-NLS-1$
		return Status.OK_STATUS;
	}
	
	@Override
	public IServiceHandler newInstance() {		
		return new StopServiceHandler();
	}
}
