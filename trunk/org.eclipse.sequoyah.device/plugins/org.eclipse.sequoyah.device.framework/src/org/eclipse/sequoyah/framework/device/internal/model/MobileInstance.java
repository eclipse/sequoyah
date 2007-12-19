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

package org.eclipse.tml.framework.device.internal.model;

import java.util.Properties;

import org.eclipse.tml.framework.device.model.IInstance;

public class MobileInstance implements IInstance {
	private String id;
	private String name;
	private String deviceId;
	private String status;
	private Properties properties;
	private int pid;
	
	public MobileInstance(String id){
		this.id = id;
		this.pid=0;
	}
	
	public String getId() {
		return id;
	}
	
	public int getPID(){
		return pid;
	}
	
	public void setPID(int pid){
		this.pid=pid;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDevice() {
		return deviceId;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void setName(String name) {		
		this.name = name;
	}
	
	public void setDevice(String deviceId) {
		this.deviceId = deviceId;
	}
	
	public String getStatus(){
		return status;
	};
	
	public void setStatus(String status){
		this.status = status;
	};
		
	public Properties getProperties(){
		return this.properties;
	}
	
	public void setProperties(Properties properties){
		this.properties = properties;
	}
		
	
	public String toString(){
		return "[Device Instance: " +
				"id=" + (id==null?"":id) +
				",name=" + (name==null?"":name) +
				",device=" + (deviceId==null?"":deviceId) +
				",status=" + (status==null?"":status) +
				"]";
	}
	
}
