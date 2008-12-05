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
import org.eclipse.debug.core.ILaunch;
import org.eclipse.tml.common.utilities.BasePlugin;
import org.eclipse.tml.device.qemureact.QEmuReactLauncher;
import org.eclipse.tml.framework.device.model.IInstance;
import org.eclipse.tml.framework.device.model.handler.IServiceHandler;
import org.eclipse.tml.framework.device.model.handler.ServiceHandler;
import org.eclipse.tml.service.start.StartServiceResources;
import org.eclipse.tml.service.start.launcher.DeviceLauncherManager;
import org.eclipse.tml.service.start.launcher.IDeviceLauncher;

public class StartServiceHandler extends ServiceHandler {

	public IStatus runService(IInstance instance, Map<Object, Object> arguments, IProgressMonitor monitor) {
		BasePlugin.logInfo(StartServiceResources.TML_Start_Service+" Over ->"+instance.getName()); //$NON-NLS-1$
		try {
			IDeviceLauncher launcher = new QEmuReactLauncher(instance);
			ILaunch launch = DeviceLauncherManager.launch(launcher,instance.getName());
			instance.setPID(launcher.getPID());
		} catch (Throwable t) {
			return Status.CANCEL_STATUS;
		}
		return Status.OK_STATUS;
	}

	public IStatus updatingService(IInstance instance, IProgressMonitor monitor) {
		BasePlugin.logInfo(StartServiceResources.TML_Start_Service_Update+"->"+instance.getName()); //$NON-NLS-1$
		return Status.OK_STATUS;
	}
	
	@Override
	public IServiceHandler newInstance() {		
		return new StartServiceHandler();
	}
}
