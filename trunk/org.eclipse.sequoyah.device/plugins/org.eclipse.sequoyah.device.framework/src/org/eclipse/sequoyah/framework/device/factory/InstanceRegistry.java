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
import org.eclipse.tml.framework.device.model.IInstance;
import org.eclipse.tml.framework.device.model.IInstanceRegistry;

public class InstanceRegistry implements IInstanceRegistry {
	private List<IInstance> instances;
	private static InstanceRegistry _instance;
	private boolean dirty;
	private List<IInstanceListeners> listeners;

	private InstanceRegistry(){
		dirty=false;
		listeners = new ArrayList<IInstanceListeners>();
		instances = new ArrayList<IInstance>();
	}
	
	public static InstanceRegistry getInstance(){
		if (_instance==null) {
			_instance = new InstanceRegistry();
		}
		return _instance;
	}
	
	public ImageDescriptor getImage(){
		return DevicePlugin.getDefault().getImageDescriptor(DevicePlugin.ICON_DEVICE);
	}
	
	public List<IInstance> getInstances() {
		return instances;
	}

	public void setInstances(List<IInstance> instance) {
		this.instances = instance;
	}
	
	public void addInstance(IInstance instance){
		this.instances.add(instance);
	}
	
	public void removeInstance(IInstance instance){
		this.instances.remove(instance);
	}
	
	public void clear(){
		this.instances.clear();
	}

	public void setDirty(boolean dirty) {		
		this.dirty = dirty;
		if (dirty) {
			notifyDirty();
		}
	}
	
	public boolean isDirty() {		
		return dirty;
	}
	
	public void addListener(IInstanceListeners listener){
		listeners.add(listener);
	}

	public void removeListener(IInstanceListeners listener){
		listeners.remove(listener);
	}

	public void notifyDirty(){
		for (IInstanceListeners listener:listeners){
			listener.dirtyChanged();
		}
	}

}
