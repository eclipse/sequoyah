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
 * Yu-Fen Kuo (MontaVista)  - [236476] - provide a generic device type
 ********************************************************************************/
package org.eclipse.tml.framework.device.ui.view.provider;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.tml.framework.device.DevicePlugin;
import org.eclipse.tml.framework.device.model.IDeviceType;
import org.eclipse.tml.framework.device.model.IDeviceTypeRegistry;
import org.eclipse.tml.framework.device.model.IService;
import org.eclipse.tml.framework.status.IStatusTransition;

public class DeviceLabelProvider extends LabelProvider {
	private Map<ImageDescriptor, Image> imageCache = new HashMap<ImageDescriptor, Image>(
			11);

	/*
	 * @see ILabelProvider#getImage(Object)
	 */
	public Image getImage(Object element) {
		ImageDescriptor descriptor = null;
		if (element instanceof IDeviceTypeRegistry) {
			descriptor = ((IDeviceTypeRegistry) element).getImage();
		} else if (element instanceof IDeviceType) {
			return ((IDeviceType) element).getImage();
		} else if (element instanceof IService) {
			descriptor = ((IService) element).getImage();
		} else if (element instanceof IStatusTransition) {
			descriptor = DevicePlugin.getDefault().getImageDescriptor(
					DevicePlugin.ICON_BOOK);
		} else {
			throw unknownElement(element);
		}

		// obtain the cached image corresponding to the descriptor
		Image image = (Image) imageCache.get(descriptor);
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
		if (element instanceof IDeviceTypeRegistry) {
			return "Device Registry"; //$NON-NLS-1$
		} else if (element instanceof IDeviceType) {
			if (((IDeviceType) element).getLabel() == null) {
				return "Device"; //$NON-NLS-1$
			} else {
				return ((IDeviceType) element).getLabel() + "(" //$NON-NLS-1$
						+ ((IDeviceType) element).getId() + ")"; //$NON-NLS-1$
			}
		} else if (element instanceof IService) {
			return ((IService) element).getName();
		} else if (element instanceof IStatusTransition) {
			return ((IStatusTransition) element).toString();
		} else {
			throw unknownElement(element);
		}
	}

	public void dispose() {
		for (Iterator<Image> i = imageCache.values().iterator(); i.hasNext();) {
			((Image) i.next()).dispose();
		}
		imageCache.clear();
	}

	protected RuntimeException unknownElement(Object element) {
		return new RuntimeException("Unknown type of element in tree of type " //$NON-NLS-1$
				+ element.getClass().getName());
	}

}
