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
package org.eclipse.tml.framework.device.ui.view.provider;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.tml.framework.device.DevicePlugin;
import org.eclipse.tml.framework.device.model.IDevice;
import org.eclipse.tml.framework.device.model.IInstance;
import org.eclipse.tml.framework.device.model.IInstanceRegistry;
import org.eclipse.tml.framework.status.IStatus;
import org.eclipse.tml.framework.status.LabelStatus;
import org.eclipse.tml.framework.status.StatusRegistry;

public class InstanceLabelProvider extends LabelProvider {	
	private Map imageCache = new HashMap(11);
	
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
		} else if (element instanceof IDevice) {
			descriptor = ((IDevice)element).getImage();
		}else if (element instanceof String) {
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
			return "Instance Registry";
		} else if (element instanceof IInstance) {			
			String name = ((IInstance)element).getName();			
			if((name == null)) {
				return "Device";
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
				return "no status";
			} else {
				return name;
			}
		} else if (element instanceof IDevice) {
			if((element == null)) {
				return "no device";
			} else {
				return ((IDevice)element).getName();
			}
		} else if (element instanceof String) {
			return (String)element;
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
