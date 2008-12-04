/*******************************************************************************
 * Copyright (c) 2008 MontaVista Software, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Yu-Fen Kuo (MontaVista) - initial API and implementation
 *******************************************************************************/
package org.eclipse.tml.framework.device.internal.model;

import java.util.List;
import java.util.Properties;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.tml.framework.device.DevicePlugin;
import org.eclipse.tml.framework.device.model.IDeviceType;
import org.eclipse.tml.framework.device.model.IService;
import org.eclipse.tml.framework.device.model.handler.IDeviceHandler;

public class MobileDeviceType implements IDeviceType {
	private static final String PROPERTY_ICON = "icon"; //$NON-NLS-1$
	private String id;
	private String label;
	private String bundleName;
	private boolean isAbstract = false;
	private String superClass;
	private IDeviceHandler handler;
	private ImageDescriptor image;
	private Properties properties = new Properties();
	private List<IService> services;

	public MobileDeviceType(String id, String label) {
		this.id = id;
		this.label = label;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public void addProperty(String name, String value) {
		this.properties.put(name, value);
	}

	public String getBundleName() {
		return bundleName;
	}

	public void setBundleName(String bundleName) {
		this.bundleName = bundleName;
	}

	public List<IService> getServices() {
		return services;
	}

	public void setServices(List<IService> services) {
		this.services = services;
		for (IService service : services) {
			service.setParent(this);
		}

	}
	
	public IDeviceHandler getHandler() {
		return handler;
	}

	public void setHandler(IDeviceHandler handler) {
		this.handler = handler;
	}
	
	public Image getImage() {
		if (getProperties().containsKey(PROPERTY_ICON)) {
			String path = getProperties().getProperty(PROPERTY_ICON);
			Image image = DevicePlugin.getDefault().getImageFromRegistry(
					getBundleName(), path);
			return image;
	}

		return null;
	}

	public String toString() {
		return "[DeviceType: " + "id=" + (id == null ? "" : id) + ",label=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				+ (label == null ? "" : label) + ",bundleName=" //$NON-NLS-1$ //$NON-NLS-2$
				+ (bundleName == null ? "" : bundleName) + "]"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}

	public String getSuperClass() {
		return superClass;
	}

	public void setSuperClass(String superClass) {
		this.superClass = superClass;
	}

}
