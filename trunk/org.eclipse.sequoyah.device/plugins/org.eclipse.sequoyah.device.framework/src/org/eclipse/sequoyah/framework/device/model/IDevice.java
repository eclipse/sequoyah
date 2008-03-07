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

package org.eclipse.tml.framework.device.model;

import java.util.List;
import java.util.Properties;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.tml.framework.device.model.handler.IDeviceHandler;


public interface IDevice {
	String getId();

	void setId(String id);

	ImageDescriptor getImage();
	
	void setImage(ImageDescriptor image);
	
	String getName();

	void setName(String name);

	String getDescription();

	void setDescription(String description);

	String getProvider();

	void setProvider(String provider);

	String getVersion();

	void setVersion(String version);

	String getCopyright();

	void setCopyright(String copyright);

	IDeviceHandler getHandler();

	void setHandler(IDeviceHandler handler);
	
	List<IService> getServices();
	
	void setServices(List<IService> services); 
	
    IInstance getParent();
	
	void setParent(IInstance instance);
	
	Object clone();
	
	public Properties getDefaultProperties();
	
	public void setDefaultProperties(Properties properties);
}
