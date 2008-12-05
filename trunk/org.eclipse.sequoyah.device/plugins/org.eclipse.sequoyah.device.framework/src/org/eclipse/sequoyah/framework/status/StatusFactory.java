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
 * Fabio Rigo (Eldorado) - [245111] Disable the "Delete" option in popup if the instance is not prepared for deletion
 ********************************************************************************/
package org.eclipse.tml.framework.status;

import org.eclipse.core.runtime.IExtension;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.tml.common.utilities.BasePlugin;
import org.eclipse.tml.common.utilities.PluginUtils;
import org.eclipse.tml.common.utilities.exception.ExceptionHandler;
import org.eclipse.tml.framework.device.DevicePlugin;
import org.eclipse.tml.framework.device.exception.DeviceExceptionHandler;
import org.eclipse.tml.framework.device.exception.DeviceExceptionStatus;


public class StatusFactory {
	private static final String ELEMENT_STATUS = "status"; //$NON-NLS-1$
	private static final String ATR_ID = "id"; //$NON-NLS-1$
	private static final String ATR_NAME = "name"; //$NON-NLS-1$
	private static final String ATR_IMAGE = "image"; //$NON-NLS-1$
	private static final String ATR_CANDELETE = "canDeleteInstance"; //$NON-NLS-1$
	
	@SuppressWarnings("deprecation")
	public static IStatus createStatus(String statusId) {
		IExtension fromPlugin =  PluginUtils.getExtension(DevicePlugin.STATUS_ID, statusId);
		String id = PluginUtils.getPluginAttribute(fromPlugin, ELEMENT_STATUS, ATR_ID);
		String name = PluginUtils.getPluginAttribute(fromPlugin, ELEMENT_STATUS, ATR_NAME);
		boolean canDelete = Boolean.parseBoolean(PluginUtils.getPluginAttribute(fromPlugin, ELEMENT_STATUS, ATR_CANDELETE));
		IStatus status = new MobileStatus(id,name);
		status.setCanDeleteInstance(canDelete);
		String imageName = PluginUtils.getPluginAttribute(fromPlugin, ELEMENT_STATUS, ATR_IMAGE);		
		ImageDescriptor image = null;
			try {
			image = BasePlugin.getPluginImage(fromPlugin.getDeclaringPluginDescriptor().getPlugin().getBundle(), imageName);
		} catch (Throwable t) {
			ExceptionHandler.showException(DeviceExceptionHandler.exception(DeviceExceptionStatus.CODE_ERROR_HANDLER_NOT_INSTANCED));
		}
		status.setImage(image);
		return status;
	}
	
	public static IStatus createStatus(IExtension fromPlugin) {
		String id = PluginUtils.getPluginAttribute(fromPlugin, ELEMENT_STATUS, ATR_ID);
		return createStatus(id);		
	}
	
}
