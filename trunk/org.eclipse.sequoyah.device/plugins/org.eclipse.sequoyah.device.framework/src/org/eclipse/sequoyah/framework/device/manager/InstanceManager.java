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
package org.eclipse.tml.framework.device.manager;

import java.util.Properties;

import org.eclipse.tml.framework.device.DevicePlugin;
import org.eclipse.tml.framework.device.IPropertyConstants;
import org.eclipse.tml.framework.device.factory.InstanceRegistry;
import org.eclipse.tml.framework.device.internal.model.MobileInstance;
import org.eclipse.tml.framework.device.model.IInstance;

public class InstanceManager {
	private static InstanceManager _instance;
	private IInstance currentInstance;

	private InstanceManager(){
		loadInstances();
	}
	
	public static InstanceManager getInstance() {
		if (_instance==null) {
			_instance = new InstanceManager();
		}
		return _instance;
	}
	
	public void loadInstances(){
		    InstanceRegistry registry = InstanceRegistry.getInstance();			
		    if (!registry.isDirty()) {
		    	registry.clear();
		    	String deviceId1 = "org.eclipse.tml.device.qemuarm.qemuarmDevice";
		    	String deviceId2 = "org.eclipse.tml.device.qemureact.qemureactDevice";
		    	
		    	Properties properties1 = new Properties();
		    	properties1.put(IPropertyConstants.HOST, "127.0.0.1");
		    	properties1.put(IPropertyConstants.DISPLAY, ":0.0");
		    	properties1.put(IPropertyConstants.PORT, "5900");
		    	
		    	Properties properties2 = new Properties();
		    	properties2.put(IPropertyConstants.HOST, "127.0.0.2");
		    	properties2.put(IPropertyConstants.DISPLAY, ":0.0");
		    	properties2.put(IPropertyConstants.PORT, "5900");
		    	
		    	Properties properties3 = new Properties();
		    	properties3.put(IPropertyConstants.HOST, "127.0.0.3");
		    	properties3.put(IPropertyConstants.DISPLAY, ":1.0");
		    	properties3.put(IPropertyConstants.PORT, "5901");
		    	
		    	
		    	Properties properties4 = new Properties();
		    	properties4.put(IPropertyConstants.HOST, "127.0.0.4");
		    	properties4.put(IPropertyConstants.DISPLAY, ":1.0");
		    	properties4.put(IPropertyConstants.PORT, "5901");
		    	
		    	Properties properties5 = new Properties();
		    	properties5.put(IPropertyConstants.HOST, "127.0.0.5");
		    	properties5.put(IPropertyConstants.DISPLAY, ":2.0");
		    	properties5.put(IPropertyConstants.PORT, "5902");
		    	
		    	Properties properties6 = new Properties();
		    	properties6.put(IPropertyConstants.HOST, "127.0.0.6");
		    	properties6.put(IPropertyConstants.DISPLAY, ":2.0");
		    	properties6.put(IPropertyConstants.PORT, "5902");
		    	
		    	IInstance inst = createInstance("Emulator1", deviceId1,DevicePlugin.TML_STATUS_OFF,properties1);
		    	if (currentInstance==null) {
		    		currentInstance = inst;
		    	}
		    	registry.addInstance(inst);
		    	registry.addInstance(createInstance("Emulator2", deviceId2,DevicePlugin.TML_STATUS_OFF,properties2));
		    	registry.addInstance(createInstance("Emulator3", deviceId1,DevicePlugin.TML_STATUS_OFF,properties3));
		    	registry.addInstance(createInstance("Emulator4", deviceId1,DevicePlugin.TML_STATUS_OFF,properties4));
		    	registry.addInstance(createInstance("Emulator5", deviceId2,DevicePlugin.TML_STATUS_OFF,properties5));
		    	registry.addInstance(createInstance("Emulator6", deviceId2,DevicePlugin.TML_STATUS_OFF,properties6));
		    } 		
	};
	
	public void setInstance(IInstance instance){
		this.currentInstance = instance;
	}
	
	public IInstance getCurrentInstance(){
		return this.currentInstance;
	}
		
	public IInstance createInstance(String name,String deviceId,String status,Properties properties){
		IInstance instance = new MobileInstance(name+deviceId);
		instance.setDevice(deviceId);
		instance.setName(name);
		instance.setStatus(status);
		instance.setProperties((Properties)properties.clone());
		return instance;		
	}
		
}
