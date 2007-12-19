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

package org.eclipse.tml.device.qemuarm.handler;

import java.io.IOException;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.tml.common.utilities.exception.TmLException;
import org.eclipse.tml.device.qemuarm.QEmuARMLauncher;
import org.eclipse.tml.device.qemuarm.QEmuARMPlugin;
import org.eclipse.tml.device.qemuarm.exception.QEmuARMDeviceExceptionHandler;
import org.eclipse.tml.device.qemuarm.exception.QEmuARMDeviceExceptionStatus;
import org.eclipse.tml.framework.device.model.IInstance;
import org.eclipse.tml.framework.status.IStatusTransition;
import org.eclipse.tml.framework.status.StatusHandler;
import org.eclipse.tml.service.start.launcher.DeviceLauncherManager;
import org.eclipse.tml.service.start.launcher.IDeviceLauncher;

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
			throw QEmuARMDeviceExceptionHandler.exception(QEmuARMDeviceExceptionStatus.CODE_ERROR_DEFAULT);
		}
		QEmuARMPlugin.logInfo(transition.toString());
	}
}
