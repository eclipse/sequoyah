/********************************************************************************
 * Copyright (c) 2007-2008 Motorola Inc and others.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Fabio Fantato (Motorola)
 * 
 * Contributors:
 * Otavio Luiz Ferranti (Eldorado Research Institute) - [bug221733]Device handlers
 *                                         shall be able to create device instances.
 * Yu-Fen Kuo (MontaVista) - [bug 236476] Provide a generic device type
 ********************************************************************************/

package org.eclipse.tml.device.qemu.handler;

import org.eclipse.tml.device.qemu.launcher.QEmuLauncher;
import org.eclipse.tml.framework.device.internal.model.MobileInstance;
import org.eclipse.tml.framework.device.model.IDeviceLauncher;
import org.eclipse.tml.framework.device.model.IInstance;
import org.eclipse.tml.framework.device.model.handler.IDeviceHandler;

public class QEmuGenericDeviceHandler implements IDeviceHandler {
	public IInstance createDeviceInstance(String id) {
		return new MobileInstance(id);
	}

	public IDeviceLauncher createDeviceLauncher(IInstance instance) {
		return new QEmuLauncher(instance);
	}
}
