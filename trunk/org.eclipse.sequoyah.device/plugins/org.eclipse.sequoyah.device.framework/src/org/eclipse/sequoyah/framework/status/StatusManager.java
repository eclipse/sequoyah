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

package org.eclipse.tml.framework.status;

import java.util.Collection;

import org.eclipse.tml.common.utilities.PluginUtils;
import org.eclipse.tml.framework.device.DevicePlugin;

public class StatusManager {
	private static StatusManager _instance;

	private StatusManager(){
		loadStatus();
	}
	
	public static StatusManager getInstance() {
		if (_instance==null) {
			_instance = new StatusManager();
		}
		return _instance;
	}
		
	public void loadStatus(){
		Collection<String> statusIds = PluginUtils.getInstalledPlugins(DevicePlugin.STATUS_ID);
		StatusRegistry.getInstance().clear();
		for (String statusId:statusIds){
			IStatus status = StatusFactory.createStatus(statusId);
			StatusRegistry.getInstance().addStatus(status);
		}
	};
	
	public void listStatus(){
		for (IStatus status:StatusRegistry.getInstance().getStatus()) {
			DevicePlugin.logInfo(status.toString());
		}
	}
	
}
