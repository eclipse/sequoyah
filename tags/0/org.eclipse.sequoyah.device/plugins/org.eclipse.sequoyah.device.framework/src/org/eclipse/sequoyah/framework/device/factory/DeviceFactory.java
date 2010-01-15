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
 * Yu-Fen Kuo (MontaVista)  - [236476] - provide a generic device type
 * 
 ********************************************************************************/
package org.eclipse.tml.framework.device.factory;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.tml.common.utilities.BasePlugin;
import org.eclipse.tml.common.utilities.PluginUtils;
import org.eclipse.tml.common.utilities.exception.ExceptionHandler;
import org.eclipse.tml.framework.device.DevicePlugin;
import org.eclipse.tml.framework.device.exception.DeviceExceptionHandler;
import org.eclipse.tml.framework.device.exception.DeviceExceptionStatus;
import org.eclipse.tml.framework.device.internal.model.MobileDeviceType;
import org.eclipse.tml.framework.device.model.IDeviceType;
import org.eclipse.tml.framework.device.model.handler.IDeviceHandler;

@Deprecated
public class DeviceFactory {
	private static final String ELEMENT_DEVICE = "device"; //$NON-NLS-1$
	private static final String ATR_ID = "id"; //$NON-NLS-1$
	private static final String ATR_LABEL = "label"; //$NON-NLS-1$
	private static final String ATR_NAME = "name"; //$NON-NLS-1$
	private static final String ATR_ICON = "icon"; //$NON-NLS-1$
	private static final String ATR_DESCRIPTION = "description"; //$NON-NLS-1$
	private static final String ATR_PROVIDER = "provider"; //$NON-NLS-1$
	private static final String ATR_COPYRIGHT = "copyright"; //$NON-NLS-1$
	private static final String ATR_VERSION = "version"; //$NON-NLS-1$
	private static final String ATR_HANDLER = "handler"; //$NON-NLS-1$
	
	public static IDeviceType createDevice(String deviceId) {

		IExtension fromPlugin =  PluginUtils.getExtension(DevicePlugin.DEVICE_TYPES_EXTENSION_POINT_ID, deviceId);

		String id = PluginUtils.getPluginAttribute(fromPlugin, ELEMENT_DEVICE, ATR_ID);
		String label = PluginUtils.getPluginAttribute(fromPlugin, ELEMENT_DEVICE, ATR_LABEL);
		
		IDeviceType device = new MobileDeviceType(id,label);
		device.setBundleName(PluginUtils.getPluginAttribute(fromPlugin, ELEMENT_DEVICE, ATR_NAME));
		String iconName = PluginUtils.getPluginAttribute(fromPlugin, ELEMENT_DEVICE, ATR_ICON);		
		ImageDescriptor image = null;
			try {
			image = BasePlugin.getPluginImage(fromPlugin.getDeclaringPluginDescriptor().getPlugin().getBundle(), iconName);
		} catch (Throwable t) {
			ExceptionHandler.showException(DeviceExceptionHandler.exception(DeviceExceptionStatus.CODE_ERROR_HANDLER_NOT_INSTANCED));
		}
		device.setProperties(DevicePlugin.DEFAULT_PROPERTIES);
		try {
			device.setHandler((IDeviceHandler)PluginUtils.getExecutableAttribute(fromPlugin, ELEMENT_DEVICE, ATR_HANDLER));
		} catch (CoreException e) {
			ExceptionHandler.showException(DeviceExceptionHandler.exception(DeviceExceptionStatus.CODE_ERROR_HANDLER_NOT_INSTANCED));
		}
		return device;
	}
	
}
