/********************************************************************************
 * Copyright (c) 2008 Motorola Inc and Others. All rights reserverd.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Fabio Fantato (Eldorado Research Institute)
 * [244810] Migrating Device View and Instance View to a separate plugin
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
import org.eclipse.tml.framework.device.model.IDeviceRegistry;
import org.eclipse.tml.framework.device.model.IService;
import org.eclipse.tml.framework.status.IStatusTransition;

public class DeviceLabelProvider extends LabelProvider {	
	private Map imageCache = new HashMap(11);
	
	/*
	 * @see ILabelProvider#getImage(Object)
	 */
	public Image getImage(Object element) {
		ImageDescriptor descriptor = null;
		if (element instanceof IDeviceRegistry) {
			descriptor = ((IDeviceRegistry)element).getImage();
		} else if (element instanceof IDevice) {
			descriptor = ((IDevice)element).getImage();
		} else if (element instanceof IService) {
			descriptor = ((IService)element).getImage();
		} else if (element instanceof IStatusTransition) {
			descriptor = DevicePlugin.getDefault().getImageDescriptor(DevicePlugin.ICON_BOOK);
		}else {
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
		if (element instanceof IDeviceRegistry) {
			return "Device Registry";
		} else if (element instanceof IDevice) {
			if(((IDevice)element).getName() == null) {
				return "Device";
			} else {
				return ((IDevice)element).getName()+"("+((IDevice)element).getId()+")";
			}
		} else if (element instanceof IService) {
			return ((IService)element).getName();
		}else if (element instanceof IStatusTransition) {
			return ((IStatusTransition)element).toString();
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
