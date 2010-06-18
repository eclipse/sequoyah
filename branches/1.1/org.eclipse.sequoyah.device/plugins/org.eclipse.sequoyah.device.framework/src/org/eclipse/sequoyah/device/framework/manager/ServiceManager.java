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

package org.eclipse.sequoyah.device.framework.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.sequoyah.device.common.utilities.BasePlugin;
import org.eclipse.sequoyah.device.common.utilities.PluginUtils;
import org.eclipse.sequoyah.device.common.utilities.exception.SequoyahException;
import org.eclipse.sequoyah.device.framework.DevicePlugin;
import org.eclipse.sequoyah.device.framework.factory.ServiceFactory;
import org.eclipse.sequoyah.device.framework.model.IService;

public class ServiceManager {
	private static ServiceManager _instance;
	private List<IService> services;
	
	private ServiceManager(){
		services = new ArrayList<IService>();
	}
	
	public static ServiceManager getInstance() {
		if (_instance==null) {
			_instance = new ServiceManager();
		}
		return _instance;
	}
		
	public void loadServices(){
		services.clear();
		Collection<String> servicesIds = PluginUtils.getInstalledPlugins(DevicePlugin.SERVICE_ID);
		for (String serviceId:servicesIds){
			try {
				services.add(ServiceFactory.createService(serviceId));
			} catch (SequoyahException t){
				// ignore missing services
				// TODO log
			}
		}
	};
	
	public void listServices(){
		for(IService service:services){
			BasePlugin.logInfo(service.toString());
		}
	};
	
}
