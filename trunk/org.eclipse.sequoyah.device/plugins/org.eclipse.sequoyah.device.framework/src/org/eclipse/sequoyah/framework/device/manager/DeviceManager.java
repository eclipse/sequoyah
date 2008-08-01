/********************************************************************************
 * Copyright (c) 2007 Motorola Inc.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Fabio Fantato (Eldorado Research Institute)
 * 
 * Contributors:
 * Fabio Fantato (Eldorado Research Institute) - [242109] IDevice.getServices() returns a null reference
 ********************************************************************************/

package org.eclipse.tml.framework.device.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IExtension;
import org.eclipse.tml.common.utilities.PluginUtils;
import org.eclipse.tml.common.utilities.exception.TmLException;
import org.eclipse.tml.framework.device.DevicePlugin;
import org.eclipse.tml.framework.device.factory.DeviceFactory;
import org.eclipse.tml.framework.device.factory.DeviceRegistry;
import org.eclipse.tml.framework.device.factory.ServiceFactory;
import org.eclipse.tml.framework.device.model.IDevice;
import org.eclipse.tml.framework.device.model.IInstance;
import org.eclipse.tml.framework.device.model.IService;
import org.eclipse.tml.framework.status.StatusManager;

public class DeviceManager {
	private static DeviceManager _instance;
	private Map<String,IDevice> devices;
	private Map<IInstance,IDevice> devicesForInstance;
	private Map<String,List<IService>> servicesForDevice;

	private DeviceManager(){
			devices = new HashMap<String,IDevice>();
			servicesForDevice = new HashMap<String,List<IService>>();
			devicesForInstance = new HashMap<IInstance,IDevice>();		
			loadDevices();		
	}
	
	public static DeviceManager getInstance() {
		if (_instance==null) {
			_instance = new DeviceManager();
		}
		return _instance;
	}
		
	public void loadDevices(){
		devices.clear();
		servicesForDevice.clear();
		Collection<String> devicesIds = PluginUtils.getInstalledPlugins(DevicePlugin.DEVICE_ID);
		StatusManager.getInstance();
		DeviceRegistry.getInstance().clear();
		for (String deviceId:devicesIds){
			IDevice device = DeviceFactory.createDevice(deviceId);
			device.setServices(loadServices(deviceId));			
			DeviceRegistry.getInstance().addDevice(device);
			devices.put(deviceId,device);
			servicesForDevice.put(deviceId, device.getServices());	
		}
	};
	
	public List<IService> loadServices(String deviceId){
		List<IService> services = new ArrayList<IService>();		
		Collection<IExtension> servs = PluginUtils.getInstalledExtensions(DevicePlugin.SERVICE_DEF_ID);
		for (IExtension service:servs){
			if (service.getUniqueIdentifier().equals(deviceId)) {
				try {
					services.add(ServiceFactory.createService(service));
				} catch (TmLException t){
					// ignore missing service
				}
			} 
		}
		return services;
	};
	
	public IDevice getDevice(String deviceId) {
		return devices.get(deviceId);
	}
	
	public IDevice getDevice(IInstance instance) {
		IDevice device = devicesForInstance.get(instance);
		if (device==null) {
			device = devices.get(instance.getDevice());
			if (device!=null) {
				device = (IDevice)device.clone();
				device.setServices(loadServices(instance.getDevice()));							
				device.setParent(instance);
				devicesForInstance.put(instance, device);
			}
		}
		return device;
	}
		
	public List<IService> getServices(IDevice device){
		return servicesForDevice.get(device.getId());		
	}
	
	public List<IService> getServices(String deviceId){
		return servicesForDevice.get(deviceId);		
	}
	
	
	public void listDevices(){
		for(IDevice device:devices.values()){
			DevicePlugin.logInfo(device.toString());
			List<IService> services = servicesForDevice.get(device.getId());
			for(IService service:services){
				DevicePlugin.logInfo("-->"+service.toString());
			}
		}
	};
	
}
