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

package org.eclipse.tml.framework.device.factory;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.tml.common.utilities.PluginUtils;
import org.eclipse.tml.common.utilities.exception.TmLException;
import org.eclipse.tml.common.utilities.exception.TmLExceptionHandler;
import org.eclipse.tml.framework.device.DevicePlugin;
import org.eclipse.tml.framework.device.exception.DeviceExceptionHandler;
import org.eclipse.tml.framework.device.exception.DeviceExceptionStatus;
import org.eclipse.tml.framework.device.internal.model.MobileService;
import org.eclipse.tml.framework.device.model.IService;
import org.eclipse.tml.framework.device.model.handler.IServiceHandler;

public class ServiceFactory {
	private static final String ELEMENT_SERVICE = "service";
	private static final String ATR_ID = "id";
	private static final String ATR_NAME = "name";
	private static final String ATR_ICON = "icon";
	private static final String ATR_DESCRIPTION = "description";
	private static final String ATR_PROVIDER = "provider";
	private static final String ATR_COPYRIGHT = "copyright";
	private static final String ATR_VERSION = "version";
	private static final String ATR_HANDLER = "handler";
	
	
	@SuppressWarnings("deprecation")
	public static IService createService(String serviceId,IServiceHandler handler) throws TmLException {
		IExtension fromPlugin =  PluginUtils.getExtension(DevicePlugin.SERVICE_ID, serviceId);
		if (fromPlugin==null) {
			throw new TmLException();
		}		
		IService service = new MobileService(PluginUtils.getPluginAttribute(fromPlugin, ELEMENT_SERVICE, ATR_ID));
		service.setName(PluginUtils.getPluginAttribute(fromPlugin, ELEMENT_SERVICE, ATR_NAME));
		String iconName = PluginUtils.getPluginAttribute(fromPlugin, ELEMENT_SERVICE, ATR_ICON);		
		ImageDescriptor image = null;
			try {
			image = DevicePlugin.getPluginImage(fromPlugin.getDeclaringPluginDescriptor().getPlugin().getBundle(), iconName);
		} catch (Throwable t) {
			TmLExceptionHandler.showException(DeviceExceptionHandler.exception(DeviceExceptionStatus.CODE_ERROR_HANDLER_NOT_INSTANCED));
		}
		service.setImage(image);
		service.setDescription(PluginUtils.getPluginAttribute(fromPlugin, ELEMENT_SERVICE, ATR_DESCRIPTION));
		service.setProvider(PluginUtils.getPluginAttribute(fromPlugin, ELEMENT_SERVICE, ATR_PROVIDER));
		service.setCopyright(PluginUtils.getPluginAttribute(fromPlugin, ELEMENT_SERVICE,ATR_COPYRIGHT));
		service.setVersion(PluginUtils.getPluginAttribute(fromPlugin, ELEMENT_SERVICE, ATR_VERSION));
		try {
			IServiceHandler originalHandler = (IServiceHandler)PluginUtils.getExecutableAttribute(fromPlugin, ELEMENT_SERVICE, ATR_HANDLER);
			if (handler!=null) {
				handler.setParent(originalHandler);
				service.setHandler(handler);
			} else { 
				service.setHandler(originalHandler);	
			}
			
		} catch (CoreException e) {
			TmLExceptionHandler.showException(DeviceExceptionHandler.exception(DeviceExceptionStatus.CODE_ERROR_HANDLER_NOT_INSTANCED));
		}
		return service;
	}
	
	public static IService createService(String serviceId) throws TmLException {
		return createService(serviceId,null);
	}
	
	public static IService createService(IExtension fromPlugin) throws TmLException {
		String id = PluginUtils.getPluginAttribute(fromPlugin, ELEMENT_SERVICE, ATR_ID);
		IServiceHandler handler = null; 
		try {
			handler = (IServiceHandler)PluginUtils.getExecutableAttribute(fromPlugin, ELEMENT_SERVICE, ATR_HANDLER);
		} catch (CoreException e) {
			// empty is a valid value
		}
		return createService(id,handler);		
	}
	
}
