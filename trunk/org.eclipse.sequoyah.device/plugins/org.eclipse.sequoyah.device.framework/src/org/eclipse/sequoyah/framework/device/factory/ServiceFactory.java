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
 * Daniel Barboza Franco - Bug [239970] - Invisible Services
 * Fabio Rigo (Eldorado) - Bug [244066] - The services are being run at one of the UI threads
 ********************************************************************************/
package org.eclipse.tml.framework.device.factory;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
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
import org.eclipse.tml.framework.status.IStatusTransition;
import org.eclipse.tml.framework.status.MobileStatusTransition;

public class ServiceFactory {
	private static final String ELEMENT_SERVICE = "service";
	private static final String ELEMENT_STATUS = "status";
	private static final String ATR_START_ID = "startId";
	private static final String ATR_END_ID = "endId";
	private static final String ATR_HALT_ID = "haltId";
	private static final String ATR_ID = "id";
	private static final String ATR_NAME = "name";
	private static final String ATR_ICON = "icon";
	private static final String ATR_DESCRIPTION = "description";
	private static final String ATR_PROVIDER = "provider";
	private static final String ATR_COPYRIGHT = "copyright";
	private static final String ATR_VERSION = "version";
	private static final String ATR_HANDLER = "handler";
	private static final String ATR_VISIBLE = "visible";
	
	
	@SuppressWarnings("deprecation")
	public static IService createService(IExtension originalPlugin,String serviceId,IServiceHandler handler) throws TmLException {
		IExtension fromPlugin =  PluginUtils.getExtension(DevicePlugin.SERVICE_ID, serviceId);		
		List<IStatusTransition> statusList = new ArrayList<IStatusTransition>();
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
		service.setVisible(new Boolean(PluginUtils.getPluginAttribute(fromPlugin, ELEMENT_SERVICE, ATR_VISIBLE)));
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
		if (originalPlugin!=null) {
			List<IConfigurationElement> statusElementList = PluginUtils.getPluginElementList(originalPlugin, ELEMENT_SERVICE, ELEMENT_STATUS);
			for (IConfigurationElement statusElement:statusElementList){
				String startId = statusElement.getAttribute(ATR_START_ID);
				String endId	= statusElement.getAttribute(ATR_END_ID);
				String haltId	= statusElement.getAttribute(ATR_HALT_ID);
				
				IStatusTransition transition = new MobileStatusTransition(startId,endId,haltId);
				statusList.add(transition);
			}		
		}	
		service.setStatusTransitions(statusList);
		return service;
	}
	
	public static IService createService(String serviceId) throws TmLException {
		return createService(null,serviceId,null);
	}
	
	public static IService createService(IExtension fromPlugin) throws TmLException {
		String id = PluginUtils.getPluginAttribute(fromPlugin, ELEMENT_SERVICE, ATR_ID);
		IServiceHandler handler = null; 
		try {
			handler = (IServiceHandler)PluginUtils.getExecutableAttribute(fromPlugin, ELEMENT_SERVICE, ATR_HANDLER);
		} catch (CoreException e) {
			// empty is a valid value
		}
		return createService(fromPlugin,id,handler);		
	}
	
}
