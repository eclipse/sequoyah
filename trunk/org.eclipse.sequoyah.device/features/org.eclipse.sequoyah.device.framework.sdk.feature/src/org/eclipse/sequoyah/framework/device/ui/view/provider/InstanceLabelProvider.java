/********************************************************************************
 * Copyright (c) 2007-2008 Motorola Inc and Others. All rights reserverd.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Fabio Fantato (Eldorado Research Institute)
 * 
 * Contributors:
 * Otávio Luiz Ferranti (Eldorado Research Institute) - bug#221733 - Adding data persistence* Fabio Fantato (Eldorado Research Institute) - [244810] Migrating Device View and Instance View to a separate plugin
 * Fabio Fantato (Eldorado Research Institute) - [244810] Migrating Device View and Instance View to a separate plugin
 *  * Yu-Fen Kuo (MontaVista)  - [236476] - provide a generic device type
 ********************************************************************************/
package org.eclipse.tml.framework.device.ui.view.provider;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.tml.framework.device.DevicePlugin;
import org.eclipse.tml.framework.device.model.IDeviceType;
import org.eclipse.tml.framework.device.model.IInstance;
import org.eclipse.tml.framework.device.model.IInstanceRegistry;
import org.eclipse.tml.framework.status.IStatus;
import org.eclipse.tml.framework.status.LabelStatus;
import org.eclipse.tml.framework.status.StatusRegistry;

/**
 * 
 * @author Fabio Fantato
 *
 */
public class InstanceLabelProvider extends LabelProvider {

	private static final String DEVICE = "Device";
	private static final String INSTANCE_REGISTRY = "Instance registry";
	private static final String NO_DEVICE = "No device";
	private static final String NO_STATUS = "No status";
	private static final String PROPERTIES = "Properties";
	
	private Map imageCache = new HashMap(20);
	
	/*
	 * @see ILabelProvider#getImage(Object)
	 */
	public Image getImage(Object element) {
		ImageDescriptor descriptor = null;
		if (element instanceof IInstanceRegistry) {
			descriptor = ((IInstanceRegistry)element).getImage();
		} else if (element instanceof IInstance) {
			descriptor = DevicePlugin.getDefault().getImageDescriptor(DevicePlugin.ICON_DEVICE);
		} else if (element instanceof LabelStatus) {
			IStatus status = StatusRegistry.getInstance().getStatus(((LabelStatus)element).getStatus());
			descriptor = status.getImage();
		} else if (element instanceof IDeviceType) {
			return ((IDeviceType)element).getImage();
		}else if (element instanceof String || element instanceof Properties) {
			descriptor = DevicePlugin.getDefault().getImageDescriptor(DevicePlugin.ICON_PROPERTY);;
		} else {
			throw unknownElement(element);
		}

		//obtain the cached image corresponding to the descriptor
		Image image = (Image)imageCache.get(descriptor);
		if (image == null) {
			image = descriptor.createImage();
			imageCache.put(descriptor, image);
		}
		return image;
	}

	/*
	 * @see ILabelProvider#getText(Object)
	 */
	public String getText(Object element) {
		if (element instanceof IInstanceRegistry) {
			return InstanceLabelProvider.INSTANCE_REGISTRY;
		} else if (element instanceof IInstance) {			
			String name = ((IInstance)element).getName();			
			if((name == null)) {
				return InstanceLabelProvider.DEVICE;
			} else {
				return name;
			}
		} else if (element instanceof LabelStatus) {
			IStatus status = StatusRegistry.getInstance().getStatus(((LabelStatus)element).getStatus());			
			String name = null;
			if (status != null) {
				name = status.getName();
			}
			if((name == null)) {
				return InstanceLabelProvider.NO_STATUS;
			} else {
				return name;
			}
		} else if (element instanceof IDeviceType) {
			if((element == null)) {
				return InstanceLabelProvider.NO_DEVICE;
			} else {
				return ((IDeviceType)element).getLabel();
			}
		} else if (element instanceof String) {
			return (String)element;
		} else if (element instanceof Properties) {
			return InstanceLabelProvider.PROPERTIES;
		} else {
			throw unknownElement(element);
		}
	}

	public void dispose() {
		for (Iterator i = imageCache.values().iterator(); i.hasNext();) {
			((Image) i.next()).dispose();
		}
		imageCache.clear();
	}

	protected RuntimeException unknownElement(Object element) {
		return new RuntimeException("Unknown type of element in tree of type " + element.getClass().getName());
	}

}
