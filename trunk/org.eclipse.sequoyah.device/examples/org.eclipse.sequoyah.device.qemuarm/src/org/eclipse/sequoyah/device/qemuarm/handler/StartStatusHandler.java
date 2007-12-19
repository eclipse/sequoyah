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

import org.eclipse.debug.core.ILaunch;
import org.eclipse.tml.common.utilities.exception.TmLException;
import org.eclipse.tml.common.utilities.exception.TmLExceptionHandler;
import org.eclipse.tml.device.qemuarm.QEmuARMLauncher;
import org.eclipse.tml.device.qemuarm.QEmuARMPlugin;
import org.eclipse.tml.device.qemuarm.exception.QEmuARMDeviceExceptionHandler;
import org.eclipse.tml.device.qemuarm.exception.QEmuARMDeviceExceptionStatus;
import org.eclipse.tml.framework.device.model.IInstance;
import org.eclipse.tml.framework.status.IStatusTransition;
import org.eclipse.tml.framework.status.StatusHandler;
import org.eclipse.tml.service.start.launcher.DeviceLauncherManager;
import org.eclipse.tml.service.start.launcher.IDeviceLauncher;

public class StartStatusHandler extends StatusHandler {

	public StartStatusHandler() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute(IStatusTransition transition,IInstance instance) throws TmLException {
		try {
			IDeviceLauncher launcher = new QEmuARMLauncher(instance);
			ILaunch launch = DeviceLauncherManager.launch(launcher,instance.getName());
			instance.setPID(launcher.getPID());
			QEmuARMPlugin.logInfo(transition.toString());
		} catch (Throwable t) {
			throw QEmuARMDeviceExceptionHandler.exception(QEmuARMDeviceExceptionStatus.CODE_ERROR_DEFAULT);
		}
	}
}
