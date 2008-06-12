/********************************************************************************
 * Copyright (c) 2007-2008 Motorola Inc and others.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Fabio Fantato (Motorola)
 * 
 * Contributors:
 * Otávio Luiz Ferranti (Eldorado Research Institute) - bug#221733 - Code cleanup.
 ********************************************************************************/
package org.eclipse.tml.framework.status;

import org.eclipse.core.runtime.IExtension;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.tml.common.utilities.PluginUtils;
import org.eclipse.tml.common.utilities.exception.TmLExceptionHandler;
import org.eclipse.tml.framework.device.DevicePlugin;
import org.eclipse.tml.framework.device.exception.DeviceExceptionHandler;
import org.eclipse.tml.framework.device.exception.DeviceExceptionStatus;


public class StatusFactory {
	private static final String ELEMENT_STATUS = "status";
	private static final String ATR_ID = "id";
	private static final String ATR_NAME = "name";
	private static final String ATR_IMAGE = "image";
	
	@SuppressWarnings("deprecation")
	public static IStatus createStatus(String statusId) {
		IExtension fromPlugin =  PluginUtils.getExtension(DevicePlugin.STATUS_ID, statusId);
		String id = PluginUtils.getPluginAttribute(fromPlugin, ELEMENT_STATUS, ATR_ID);
		String name = PluginUtils.getPluginAttribute(fromPlugin, ELEMENT_STATUS, ATR_NAME);
		IStatus status = new MobileStatus(id,name);
		String imageName = PluginUtils.getPluginAttribute(fromPlugin, ELEMENT_STATUS, ATR_IMAGE);		
		ImageDescriptor image = null;
			try {
			image = DevicePlugin.getPluginImage(fromPlugin.getDeclaringPluginDescriptor().getPlugin().getBundle(), imageName);
		} catch (Throwable t) {
			TmLExceptionHandler.showException(DeviceExceptionHandler.exception(DeviceExceptionStatus.CODE_ERROR_HANDLER_NOT_INSTANCED));
		}
		status.setImage(image);
		return status;
	}
	
	public static IStatus createStatus(IExtension fromPlugin) {
		String id = PluginUtils.getPluginAttribute(fromPlugin, ELEMENT_STATUS, ATR_ID);
		return createStatus(id);		
	}
	
}
