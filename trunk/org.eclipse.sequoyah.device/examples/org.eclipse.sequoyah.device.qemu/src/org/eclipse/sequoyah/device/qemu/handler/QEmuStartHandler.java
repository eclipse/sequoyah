/********************************************************************************
 * Copyright (c) 2007 Motorola Inc and others.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Fabio Fantato (Motorola)
 * 
 * Contributors:
 * Yu-Fen Kuo (MontaVista) - [bug 236476] Provide a generic device type
 ********************************************************************************/

package org.eclipse.tml.device.qemu.handler;

import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.tml.device.qemu.QEmuPlugin;
import org.eclipse.tml.framework.device.model.IInstance;
import org.eclipse.tml.framework.device.model.handler.IServiceHandler;
import org.eclipse.tml.framework.device.model.handler.ServiceHandler;


public class QEmuStartHandler extends ServiceHandler {

	public void runService(IInstance instance) {
		QEmuPlugin.logInfo("start service->"+instance.getName()); //$NON-NLS-1$
	}

	public void updatingService(IInstance instance) {
		// no action
	}

	@Override
	public IServiceHandler newInstance() {		
		return new QEmuStartHandler();
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
