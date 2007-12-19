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

package org.eclipse.tml.service.start.handler;

import org.eclipse.tml.framework.device.model.IInstance;
import org.eclipse.tml.framework.device.model.handler.IServiceHandler;
import org.eclipse.tml.framework.device.model.handler.ServiceHandler;
import org.eclipse.tml.service.start.StartServicePlugin;
import org.eclipse.tml.service.start.StartServiceResources;

public class StartServiceHandler extends ServiceHandler {

	public void runService(IInstance instance) {
		StartServicePlugin.logInfo(StartServiceResources.TML_Start_Service+"->"+instance.getName());
	}

	public void updatingService(IInstance instance) {
		StartServicePlugin.logInfo(StartServiceResources.TML_Start_Service_Update+"->"+instance.getName());
	}
	
	@Override
	public IServiceHandler newInstance() {		
		return new StartServiceHandler();
	}
}
