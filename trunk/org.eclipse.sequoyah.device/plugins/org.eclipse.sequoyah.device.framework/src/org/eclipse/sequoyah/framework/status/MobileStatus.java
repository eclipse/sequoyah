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

package org.eclipse.tml.framework.status;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.tml.framework.device.DevicePlugin;
import org.eclipse.tml.framework.device.DeviceResources;

public class MobileStatus implements IStatus {
	private String id;
	private String name;
	private ImageDescriptor image;
	private Object parent;
	
	public MobileStatus(){
		this.id = DevicePlugin.TML_STATUS_UNAVAILABLE;
		this.name = DeviceResources.TML_STATUS_UNAVAILABLE;
	};
	
	public MobileStatus(String id,String name){
		this.id = id;
		this.name = name;
	};
	

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public ImageDescriptor getImage() {
		return image;
	}
	
	public void setImage(ImageDescriptor image) {
		this.image = image;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	

	public Object getParent() {
		return parent;
	}

	public void setParent(Object instance) {
		this.parent = instance;
	}
	
	public Object clone(){
		MobileStatus clone = new MobileStatus(this.id,this.name);
		clone.setParent(this.parent);
		clone.setImage(this.image);
		return clone;
	}

	public String toString(){
		return "[Status: id="+(id!=null?id:"")+";name="+(name!=null?name:"")+"]";
	}	

}
