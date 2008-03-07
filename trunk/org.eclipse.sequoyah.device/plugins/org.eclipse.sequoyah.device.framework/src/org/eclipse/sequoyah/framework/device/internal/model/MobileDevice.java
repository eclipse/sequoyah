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
 * Fabio Fantato (Motorola) - bug#221733 - code revisited
 ********************************************************************************/

package org.eclipse.tml.framework.device.internal.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.tml.framework.device.model.IDevice;
import org.eclipse.tml.framework.device.model.IInstance;
import org.eclipse.tml.framework.device.model.IService;
import org.eclipse.tml.framework.device.model.handler.IDeviceHandler;

public class MobileDevice implements IDevice {
	private String id;
	private String name;
	private ImageDescriptor image;
	private String copyright;
	private String description;
	private String provider;
	private String version;
	private IDeviceHandler handler;
	private List<IService> services;
	private IInstance parent;
	private Properties properties;
	
	public MobileDevice(String id){
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public ImageDescriptor getImage() {
		return image;
	}
	public String getCopyright() {
		return copyright;
	}
	public String getDescription() {
		return description;
	}
	public String getProvider() {
		return provider;
	}
	public String getVersion() {
		return version;
	}
	public IDeviceHandler getHandler() {
		return handler;
	}
	public List<IService> getServices(){
		return services;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setName(String name) {		
		this.name = name;
	}
	public void setImage(ImageDescriptor image) {
		this.image = image;
	}
	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public void setHandler(IDeviceHandler handler) {
		this.handler = handler;
	}
	public void setServices(List<IService> services){
		this.services = services;
		for (IService service:services){
			service.setParent(this);
		}
	}
	public IInstance getParent() {
		return parent;
	}

	public void setParent(IInstance instance) {
		this.parent = instance;
	}
	
	public Object clone(){
		IDevice newDevice = new MobileDevice(id);
		newDevice.setName(this.name);
		newDevice.setImage(this.image);
		newDevice.setCopyright(this.copyright);
		newDevice.setDescription(this.description);
		newDevice.setProvider(this.provider);
		newDevice.setVersion(this.version);
		newDevice.setHandler(this.handler);
		if (this.services!=null) {
			List<IService> cServices = new ArrayList<IService>();
			for (IService service:this.services){
				cServices.add((IService)service.clone());
			}
			newDevice.setServices(cServices);
		}
		return newDevice;
	}
	
	public Properties getDefaultProperties(){
		return this.properties;
	}
	
	public void setDefaultProperties(Properties properties){
		this.properties = properties;
	}
		

	public String toString(){
		return "[Device: " +
				"id=" + (id==null?"":id) +
				",name=" + (name==null?"":name) +
				",description=" + (description==null?"":description) + 
				",version=" + (version==null?"":version) +
				",provider=" + (provider==null?"":provider) +
				",copyright=" + (copyright==null?"":copyright) +
				",handler=" + (handler==null?"":handler.getClass().getName()) +
				"]";
	}
	
}
