/********************************************************************************
 * Copyright (c) 2007 Motorola Incand others.
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

import org.eclipse.tml.common.utilities.BasePlugin;
import org.eclipse.tml.common.utilities.exception.ExceptionStatus;
import org.eclipse.tml.common.utilities.exception.TmLException;
import org.eclipse.tml.device.qemu.exception.QEmuDeviceExceptionHandler;
import org.eclipse.tml.framework.device.model.IInstance;
import org.eclipse.tml.framework.status.IStatusTransition;
import org.eclipse.tml.framework.status.StatusHandler;

public class StopStatusHandler extends StatusHandler {

	public StopStatusHandler() {
	}

	@Override
	public void execute(IStatusTransition transition, IInstance instance)
			throws TmLException {
		String kill = "taskkill /f /PID " + String.valueOf(instance.getPID()); //$NON-NLS-1$
		try {
			Runtime.getRuntime().exec(kill);
		} catch (Throwable t) {
			throw QEmuDeviceExceptionHandler
					.exception(ExceptionStatus.CODE_ERROR_DEFAULT);
		}
		BasePlugin.logInfo(transition.toString());
	}
}
