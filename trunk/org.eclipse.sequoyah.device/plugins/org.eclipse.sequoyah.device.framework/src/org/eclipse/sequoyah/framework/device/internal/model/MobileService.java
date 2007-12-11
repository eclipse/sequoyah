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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.tml.framework.device.model.IDevice;
import org.eclipse.tml.framework.device.model.IService;
import org.eclipse.tml.framework.device.model.handler.IServiceHandler;

public class MobileService implements IService {
	private String id;
	private String name;
	private ImageDescriptor image;
	private String copyright;
	private String description;
	private String provider;
	private String version;
	private IServiceHandler handler;
	private IDevice parent;
	
	public MobileService(String id){
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	public ImageDescriptor getImage() {
		return image;
	}
	public String getName() {
		return name;
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
	public IServiceHandler getHandler() {
		return handler;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setImage(ImageDescriptor image) {
		this.image = image;
	}
	public void setName(String name) {
		this.name = name;
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
	public void setHandler(IServiceHandler handler) {
		this.handler = handler;
	}

	public Object clone(){
		IService newService = new MobileService(id);
		newService.setName(this.name);
		newService.setImage(this.image);
		newService.setCopyright(this.copyright);
		newService.setDescription(this.description);
		newService.setProvider(this.provider);
		newService.setVersion(this.version);
		newService.setHandler(this.handler);
		newService.setParent(this.parent);
		return newService;
	}
	
	
	
	public String toString(){
		return "[Service: " +
				"id=" + (id==null?"":id) +
				",name=" + (name==null?"":name) +
				",description=" + (description==null?"":description) + 
				",version=" + (version==null?"":version) +
				",provider=" + (provider==null?"":provider) +
				",copyright=" + (copyright==null?"":copyright) +
				",handler=" + (handler==null?"":handler.getClass().getName()) +
				"]";
	}

	public IDevice getParent() {
		return parent;
	}

	public void setParent(IDevice device) {
		this.parent = device;
	}
	
}
