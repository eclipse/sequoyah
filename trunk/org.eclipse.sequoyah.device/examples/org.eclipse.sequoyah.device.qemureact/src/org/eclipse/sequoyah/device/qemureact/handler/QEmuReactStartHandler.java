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

package org.eclipse.tml.device.qemureact.handler;

import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.tml.device.qemureact.QEmuReactPlugin;
import org.eclipse.tml.device.qemureact.QEmuReactResources;
import org.eclipse.tml.framework.device.model.IInstance;
import org.eclipse.tml.framework.device.model.handler.IServiceHandler;
import org.eclipse.tml.framework.device.model.handler.ServiceHandler;

public class QEmuReactStartHandler extends ServiceHandler {


	public void runService(IInstance instance) {
		QEmuReactPlugin.logInfo(QEmuReactResources.TML_Start_Service+"->"+instance.getName());
	}

	public void updatingService(IInstance instance) {
		// no action
	}

	@Override
	public IServiceHandler newInstance() {		
		return new QEmuReactStartHandler();
	}

	@Override
	public IStatus runService(IInstance instance,
			Map<Object, Object> arguments, IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus updatingService(IInstance instance, IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		return null;
	}
}
