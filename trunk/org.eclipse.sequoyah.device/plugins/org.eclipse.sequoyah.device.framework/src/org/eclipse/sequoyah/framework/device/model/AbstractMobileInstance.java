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
 * Otavio Luiz Ferranti (Eldorado Research Institute) - bug#221733 - Adding data persistence
 * Daniel Barboza Franco (Eldorado Research Institute) - Bug [221739] - Improvements to State machine implementation
 * Daniel Barboza Franco (Eldorado Research Institute) - Bug [252261] - Internal class MobileInstance providing functionalities
 ********************************************************************************/

package org.eclipse.tml.framework.device.model;

import java.util.Properties;

import org.eclipse.tml.framework.device.statemachine.StateMachineHandler;

public abstract class AbstractMobileInstance implements IInstance {

	protected String id;
	private String name;
	private String deviceId;
	private Properties properties;
	protected int pid;
	private StateMachineHandler stateMachineHandler;

	public AbstractMobileInstance() {
		super();
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
	public int getPID() {
		return pid;
	}

	/**
	 * Sets the instance PID.
	 * @return pid - The instance PID.
	 */
	public void setPID(int pid) {
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
	 * Retieves the device id.
	 * @return The device id.
	 */
	public String getDeviceTypeId() {
		return deviceId;
	}

	/**
	 * Sets the device id.
	 * @param deviceId - The device id.
	 */
	public void setDeviceTypeId(String deviceTypeId) {
		this.deviceId = deviceTypeId;
		
	}
	

	/**
	 * Retrieves the instance status.
	 * @return The instance status.
	 */
	public String getStatus() {
		return getStateMachineHandler().getState();
	}

	/**
	 * Sets the instance status. 
	 * Warning: This method do not check if the transition is valid.
	 * It should be used only in cases that it is not possible to cover 
	 * the transition using the services mechanism. 
	 * @param status - The instance status.
	 */
	public void setStatus(String status) {
		stateMachineHandler.setState(status);
	}

	/**
	 * Retrieves the instance properties.
	 */
	public Properties getProperties() {
		return this.properties;
	}

	/**
	 * Sets the instance properties.
	 * @param properties - The instance properties.
	 */
	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public String toString() {
		String status = stateMachineHandler.getState();
		return "[Device Instance: " + //$NON-NLS-1$
				"id=" + (id==null?"":id) + //$NON-NLS-1$ //$NON-NLS-2$
				",name=" + (name==null?"":name) + //$NON-NLS-1$ //$NON-NLS-2$
				",device=" + (deviceId==null?"":deviceId) + //$NON-NLS-1$ //$NON-NLS-2$
				",status=" + (status==null?"":status) +   //$NON-NLS-1$//$NON-NLS-2$
				"]"; //$NON-NLS-1$
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

	public StateMachineHandler getStateMachineHandler() {
		return this.stateMachineHandler;
	}

	public void setStateMachineHandler(StateMachineHandler stateMachineHandler) {
		this.stateMachineHandler = stateMachineHandler;
	}

}
