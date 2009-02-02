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

import org.eclipse.tml.common.utilities.exception.TmLException;
import org.eclipse.tml.device.qemu.QEmuPlugin;
import org.eclipse.tml.device.qemu.exception.QEmuDeviceExceptionHandler;
import org.eclipse.tml.device.qemu.exception.QEmuDeviceExceptionStatus;
import org.eclipse.tml.device.qemu.launcher.QEmuLauncher;
import org.eclipse.tml.framework.device.model.IDeviceLauncher;
import org.eclipse.tml.framework.device.model.IInstance;
import org.eclipse.tml.framework.status.IStatusTransition;
import org.eclipse.tml.framework.status.StatusHandler;
import org.eclipse.tml.service.start.launcher.DeviceLauncherManager;

public class StartStatusHandler extends StatusHandler {

	public StartStatusHandler() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute(IStatusTransition transition,IInstance instance) throws TmLException {
		try {
			IDeviceLauncher launcher = new QEmuLauncher(instance);
			DeviceLauncherManager.launch(launcher,instance.getName());
			instance.setPID(launcher.getPID());
			QEmuPlugin.logInfo(transition.toString());
		} catch (Throwable t) {
			throw QEmuDeviceExceptionHandler.exception(QEmuDeviceExceptionStatus.CODE_ERROR_DEFAULT);
		}
	}

}
