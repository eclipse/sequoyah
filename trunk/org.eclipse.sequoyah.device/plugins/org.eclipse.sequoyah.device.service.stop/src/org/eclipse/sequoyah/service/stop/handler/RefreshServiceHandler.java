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

package org.eclipse.tml.service.stop.handler;

import org.eclipse.tml.framework.device.model.IInstance;
import org.eclipse.tml.framework.device.model.handler.IServiceHandler;
import org.eclipse.tml.framework.device.model.handler.ServiceHandler;
import org.eclipse.tml.service.stop.StopServicePlugin;
import org.eclipse.tml.service.stop.StopServiceResources;

public class RefreshServiceHandler extends ServiceHandler  {

	public void runService(IInstance instance) {
		StopServicePlugin.logInfo(StopServiceResources.TML_Refresh_Service+"->"+instance.getName());
	}
	
	public void updatingService(IInstance instance) {
		StopServicePlugin.logInfo(StopServiceResources.TML_Refresh_Service_Update+"->"+instance.getName());
	}
	
	@Override
	public IServiceHandler newInstance() {		
		return new RefreshServiceHandler();
	}
}
