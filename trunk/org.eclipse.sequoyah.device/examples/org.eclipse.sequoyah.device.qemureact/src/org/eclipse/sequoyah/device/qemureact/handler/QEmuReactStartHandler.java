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

import org.eclipse.tml.device.qemureact.QEmuReactPlugin;
import org.eclipse.tml.device.qemureact.QEmuReactResources;
import org.eclipse.tml.framework.device.model.IInstance;
import org.eclipse.tml.framework.device.model.handler.ServiceHandler;

public class QEmuReactStartHandler extends ServiceHandler {

	@Override
	public void runService(IInstance instance) {
		QEmuReactPlugin.logInfo(QEmuReactResources.TML_Start_Service+"->"+instance.getName());
	}

	public void updatingService(IInstance instance) {
		// no action
	}
}
