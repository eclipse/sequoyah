package org.eclipse.tml.framework.device.ui.view.provider;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.tml.framework.device.model.IDevice;
import org.eclipse.tml.framework.device.model.IDeviceRegistry;
import org.eclipse.tml.framework.device.model.IService;

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
