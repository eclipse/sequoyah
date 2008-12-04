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
 * Otávio Luiz Ferranti (Eldorado Research Institute) - bug#221733 - Adding data persistence
 * Yu-Fen Kuo (MontaVista) - bug#236476 - Provide a generic device type
 ********************************************************************************/

package org.eclipse.tml.framework.device.internal.model;

import java.util.Properties;
import org.eclipse.tml.framework.device.model.IInstance;

/**
 * Basic implementation of IInstance.
 * @author Fabio Fantato
 */
public class MobileInstance implements IInstance {
	private String id;
	private String name;
	private String deviceTypeId;
	private String status;
	private Properties properties;
	private int pid;
	
	/**
	 * Constructor - Basic implementation of IInstance.
	 */
	public MobileInstance(String id){
		this.id = id;
		this.pid=0;
	}
	
	/**
	 * Retrieves the instance id.
	 * @return The instance id.
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Sets the instance id
	 * @param id - The instance id.
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Retrieves the instance PID.
	 * @return The instance PID.
	 */
	public int getPID(){
		return pid;
	}

	/**
	 * Sets the instance PID.
	 * @return pid - The instance PID.
	 */
	public void setPID(int pid){
		this.pid = pid;
	}
	
	/**
	 * Retrieves the instance name.
	 * @return The instance name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the instance name.
	 * @param name - The instance name.
	 */
	public void setName(String name) {		
		this.name = name;
	}
	
	/**
	 * Retrieves the device type id.
	 * @return The device type id.
	 */
	public String getDeviceTypeId() {
		return deviceTypeId;
	}

	/**
	 * Sets the device type id.
	 * @param deviceTypeId - The device type id.
	 */
	public void setDeviceTypeId(String deviceTypeId) {
		this.deviceTypeId = deviceTypeId;
	}
	
	/**
	 * Retrieves the instance status.
	 * @return The instance status.
	 */
	public String getStatus(){
		return status;
	}
	
	/**
	 * Sets the instance status.
	 * @param status - The instance status.
	 */
	public void setStatus(String status){
		this.status = status;
	}

	/**
     * Retrieves the instance properties.
	 */
	public Properties getProperties(){
		return this.properties;
	}
	
	/**
	 * Sets the instance properties.
	 * @param properties - The instance properties.
	 */
	public void setProperties(Properties properties){
		this.properties = properties;
	}
	
	public String toString(){
		return "[Device Instance: " +
				"id=" + (id==null?"":id) +
				",name=" + (name==null?"":name) +
				",deviceTypeId=" + (deviceTypeId==null?"":deviceTypeId) +
				",status=" + (status==null?"":status) +
				"]";
	}

	/** Returns a equivalent object of a different class
	 * @param adapter - The requested object class
	 * @return The requested object if conversion is available
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		if (adapter.equals(this.getClass())) {
			return this;
		}
		return null;
	}
	
}
