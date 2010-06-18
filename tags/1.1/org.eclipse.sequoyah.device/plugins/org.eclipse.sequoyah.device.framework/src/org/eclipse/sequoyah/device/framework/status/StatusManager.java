/********************************************************************************
 * Copyright (c) 2007-2010 Motorola Inc.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Fabio Fantato (Motorola)
 * 
 * Contributors:
 * Daniel Pastore (Eldorado) - [289870] Moving and renaming Tml to Sequoyah
 ********************************************************************************/

package org.eclipse.sequoyah.device.framework.status;

import java.util.Collection;

import org.eclipse.sequoyah.device.common.utilities.BasePlugin;
import org.eclipse.sequoyah.device.common.utilities.PluginUtils;
import org.eclipse.sequoyah.device.framework.DevicePlugin;

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
			BasePlugin.logInfo(status.toString());
		}
	}
	
}
