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

import org.eclipse.tml.framework.device.factory.InstanceRegistry;
import org.eclipse.tml.framework.device.internal.model.MobileInstance;
import org.eclipse.tml.framework.device.internal.model.MobileStatus;
import org.eclipse.tml.framework.device.model.IInstance;
import org.eclipse.tml.framework.device.model.IStatus;

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
		    registry.clear();
			String deviceId1 = "org.eclipse.tml.device.qemuarm.qemuarmDevice";
			String deviceId2 = "org.eclipse.tml.device.qemureact.qemureactDevice";
			IStatus status1 = new MobileStatus(IStatus.eStatus.STARTED);
			IStatus status2 = new MobileStatus(IStatus.eStatus.STOPPED);
			IStatus status3 = new MobileStatus(IStatus.eStatus.REFRESHING);
		
			IInstance inst = createInstance("Emulator1", deviceId1,status1);
			if (currentInstance==null) {
				currentInstance = inst;
			}
			registry.addInstance(inst);
			registry.addInstance(createInstance("Emulator2", deviceId2,status2));
			registry.addInstance(createInstance("Emulator3", deviceId1,status3));
			registry.addInstance(createInstance("Emulator4", deviceId1,status3));
			registry.addInstance(createInstance("Emulator5", deviceId2,status2));
			registry.addInstance(createInstance("Emulator6", deviceId2,status1));
			
	};
	
	public void setInstance(IInstance instance){
		this.currentInstance = instance;
	}
	
	public IInstance getCurrentInstance(){
		return this.currentInstance;
	}
		
	public IInstance createInstance(String name,String deviceId,IStatus status){
		IInstance instance = new MobileInstance(name+deviceId);
		instance.setDevice(deviceId);
		instance.setName(name);
		instance.setStatus((IStatus)status.clone());
		return instance;		
	}
		
}
