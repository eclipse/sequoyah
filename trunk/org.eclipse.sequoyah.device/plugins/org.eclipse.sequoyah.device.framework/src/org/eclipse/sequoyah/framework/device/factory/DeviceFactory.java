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
package org.eclipse.tml.framework.device.factory;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.tml.common.utilities.PluginUtils;
import org.eclipse.tml.common.utilities.exception.TmLExceptionHandler;
import org.eclipse.tml.framework.device.DevicePlugin;
import org.eclipse.tml.framework.device.exception.DeviceExceptionHandler;
import org.eclipse.tml.framework.device.exception.DeviceExceptionStatus;
import org.eclipse.tml.framework.device.internal.model.MobileDevice;
import org.eclipse.tml.framework.device.model.IDevice;
import org.eclipse.tml.framework.device.model.handler.IDeviceHandler;

public class DeviceFactory {
	private static final String ELEMENT_DEVICE = "device";
	private static final String ATR_ID = "id";
	private static final String ATR_NAME = "name";
	private static final String ATR_ICON = "icon";
	private static final String ATR_DESCRIPTION = "description";
	private static final String ATR_PROVIDER = "provider";
	private static final String ATR_COPYRIGHT = "copyright";
	private static final String ATR_VERSION = "version";
	private static final String ATR_HANDLER = "handler";
	
	
	@SuppressWarnings("deprecation")
	public static IDevice createDevice(String deviceId) {

		IExtension fromPlugin =  PluginUtils.getExtension(DevicePlugin.DEVICE_ID, deviceId);
	
		IDevice device = new MobileDevice(PluginUtils.getPluginAttribute(fromPlugin, ELEMENT_DEVICE, ATR_ID));
		device.setName(PluginUtils.getPluginAttribute(fromPlugin, ELEMENT_DEVICE, ATR_NAME));
		String iconName = PluginUtils.getPluginAttribute(fromPlugin, ELEMENT_DEVICE, ATR_ICON);		
		ImageDescriptor image = null;
			try {
			image = DevicePlugin.getPluginImage(fromPlugin.getDeclaringPluginDescriptor().getPlugin().getBundle(), iconName);
		} catch (Throwable t) {
			TmLExceptionHandler.showException(DeviceExceptionHandler.exception(DeviceExceptionStatus.CODE_ERROR_HANDLER_NOT_INSTANCED));
		}
		device.setImage(image);
		device.setDescription(PluginUtils.getPluginAttribute(fromPlugin, ELEMENT_DEVICE, ATR_DESCRIPTION));
		device.setProvider(PluginUtils.getPluginAttribute(fromPlugin, ELEMENT_DEVICE, ATR_PROVIDER));
		device.setCopyright(PluginUtils.getPluginAttribute(fromPlugin, ELEMENT_DEVICE,ATR_COPYRIGHT));
		device.setVersion(PluginUtils.getPluginAttribute(fromPlugin, ELEMENT_DEVICE, ATR_VERSION));
		device.setDefaultProperties(DevicePlugin.DEFAULT_PROPERTIES);
		try {
			device.setHandler((IDeviceHandler)PluginUtils.getExecutableAttribute(fromPlugin, ELEMENT_DEVICE, ATR_HANDLER));
		} catch (CoreException e) {
			TmLExceptionHandler.showException(DeviceExceptionHandler.exception(DeviceExceptionStatus.CODE_ERROR_HANDLER_NOT_INSTANCED));
		}
		return device;
	}
	
}
