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
 * Fabio Fantato (Eldorado) - Bug [244539] - The plug-in "org.eclipse.tml.service.start" depends on jdt
 ********************************************************************************/

package org.eclipse.tml.device.qemureact.handler;

import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.tml.framework.device.model.IInstance;
import org.eclipse.tml.framework.device.model.handler.IServiceHandler;
import org.eclipse.tml.framework.device.model.handler.ServiceHandler;

public class StopServiceHandler extends ServiceHandler {

	public IStatus runService(IInstance instance, Map<Object , Object> arguments , IProgressMonitor monitor) {
		String kill = "taskkill /f /PID "+String.valueOf(instance.getPID());		 //$NON-NLS-1$
		try {
			Process p = Runtime.getRuntime().exec(kill);
		} catch (Throwable t) {
			return Status.CANCEL_STATUS;
		}
		return Status.OK_STATUS;		
	}

	public IStatus updatingService(IInstance instance, IProgressMonitor monitor) {
		return Status.OK_STATUS;
	}
	
	@Override
	public IServiceHandler newInstance() {		
		return new StopServiceHandler();
	}
}
