/********************************************************************************
 * Copyright (c) 2007-2008 Motorola Inc and others.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Fabio Fantato (Motorola)
 * 
 * Contributors:
 * Otavio Luiz Ferranti (Eldorado Research Institute) - bug#221733 - Code cleanup
 * Fabio Rigo (Eldorado Research Institute) - bug 244052 - The dirtyChanged method is being called out of UI thread
 ********************************************************************************/
package org.eclipse.tml.framework.device.factory;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.tml.framework.device.DevicePlugin;
import org.eclipse.tml.framework.device.events.InstanceEvent;
import org.eclipse.tml.framework.device.events.InstanceEventManager;
import org.eclipse.tml.framework.device.model.IInstance;
import org.eclipse.tml.framework.device.model.IInstanceRegistry;

/**
 * Stores the device instances and provides basic query methods.
 * @author Fabio Fantato
 */
public class InstanceRegistry implements IInstanceRegistry {
	private List<IInstance> instances;
	private static InstanceRegistry _instance;

	/**
	* Constructor - Stores the device instances and provides basic query methods.
	*/
	private InstanceRegistry(){
		instances = new ArrayList<IInstance>();
	}


	/**
	 * Singleton method.
	 * @return An InstanceRegistry instance.
	 */
	public static InstanceRegistry getInstance(){
		if (_instance==null) {
			_instance = new InstanceRegistry();
		}
		return _instance;
	}

	public ImageDescriptor getImage(){
		return DevicePlugin.getDefault().getImageDescriptor(DevicePlugin.ICON_DEVICE);
	}

	/**
	 * Retrieves the list of registered instances.
	 * @return A list of instances.
	 */
	public List<IInstance> getInstances() {
		return instances;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.tml.framework.device.model.IInstanceRegistry#setInstances(java.util.List)
	 */
	public void setInstances(List<IInstance> instance) {
		this.instances = instance;
	}

	/**
	 * Add an instance to the registry.
	 * @param instance - The instance to be added.
	 */
	public void addInstance(IInstance instance){
		this.instances.add(instance);
		InstanceEventManager.getInstance().fireInstanceLoaded(new InstanceEvent(instance));
	}

	/**
	 * Removes an instance from the registry.
	 * @param instance - The instance to be removed.
	 */
	public void removeInstance(IInstance instance){
		this.instances.remove(instance);
		InstanceEventManager.getInstance().fireInstanceUnloaded(new InstanceEvent(instance));
	}

	/**
	 * Removes all instances from the registry.
	 */
	public void clear(){
		this.instances.clear();
	}
}
