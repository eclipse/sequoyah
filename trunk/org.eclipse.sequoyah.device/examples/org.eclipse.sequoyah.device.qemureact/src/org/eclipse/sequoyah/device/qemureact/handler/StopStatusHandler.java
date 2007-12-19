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
 * name (company) - description.
 ********************************************************************************/
package org.eclipse.tml.device.qemureact.handler;

import org.eclipse.tml.common.utilities.exception.TmLException;
import org.eclipse.tml.device.qemureact.QEmuReactPlugin;
import org.eclipse.tml.device.qemureact.exception.QEmuReactDeviceExceptionHandler;
import org.eclipse.tml.device.qemureact.exception.QEmuReactDeviceExceptionStatus;
import org.eclipse.tml.framework.device.model.IInstance;
import org.eclipse.tml.framework.status.IStatusTransition;
import org.eclipse.tml.framework.status.StatusHandler;

public class StopStatusHandler extends StatusHandler {

	public StopStatusHandler() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute(IStatusTransition transition,IInstance instance) throws TmLException {		
		String kill = "taskkill /f /PID "+String.valueOf(instance.getPID());		
		try {
			Process p = Runtime.getRuntime().exec(kill);
		} catch (Throwable t) {
			throw QEmuReactDeviceExceptionHandler.exception(QEmuReactDeviceExceptionStatus.CODE_ERROR_DEFAULT);
		}
		QEmuReactPlugin.logInfo(transition.toString());
	}
}
