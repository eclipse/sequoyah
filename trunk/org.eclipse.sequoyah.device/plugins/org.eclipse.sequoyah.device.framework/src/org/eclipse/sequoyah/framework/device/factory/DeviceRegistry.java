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
package org.eclipse.tml.framework.device.factory;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.tml.framework.device.DevicePlugin;
import org.eclipse.tml.framework.device.manager.DeviceManager;
import org.eclipse.tml.framework.device.model.IDevice;
import org.eclipse.tml.framework.device.model.IDeviceRegistry;

public class DeviceRegistry implements IDeviceRegistry {
	private List<IDevice> devices;
	private static DeviceRegistry _instance;

	private DeviceRegistry(){		
		devices = new ArrayList<IDevice>();
	}
	
	public static DeviceRegistry getInstance(){
		if (_instance==null) {
			_instance = new DeviceRegistry();
		}
		return _instance;
	}
	
	public ImageDescriptor getImage(){
		return DevicePlugin.getDefault().getImageDescriptor(DevicePlugin.ICON_MOVING);
	}
	
	public List<IDevice> getDevices() {
		return devices;
	}

	public void setDevices(List<IDevice> devices) {
		this.devices = devices;
	}
	
	public void addDevice(IDevice device){
		this.devices.add(device);
	}

	public void clear(){
		this.devices.clear();
	}

}
