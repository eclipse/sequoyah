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

package org.eclipse.tml.framework.device.ui.exception;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.tml.common.utilities.exception.AbstractExceptionStatus;
import org.eclipse.tml.common.utilities.exception.ExceptionMessage;
import org.eclipse.tml.framework.device.ui.DeviceUIResources;

public class DeviceUIExceptionStatus extends AbstractExceptionStatus {
	public static final int CODE_ERROR_RESOURCE_NOT_AVAILABLE = 201;
	public static final int CODE_ERROR_HANDLER_NOT_INSTANCED = 202;
	

	public DeviceUIExceptionStatus(IStatus status) {
		super(status);
	}
	
	public DeviceUIExceptionStatus(int severity, String pluginId, int code, String message, Throwable exception) {
		super(severity, pluginId, code, message, exception);
	}

	public DeviceUIExceptionStatus(int code,String pluginId){
		super(code,pluginId,null,null);
	}

	public DeviceUIExceptionStatus(int code,String pluginId,Throwable exception) {
		super(code,pluginId,exception);
	}
	
	public DeviceUIExceptionStatus(int code,String pluginId,Object[] args,Throwable exception) {
		super(code,pluginId,args,exception);
		
	}
	
	@Override
	public ExceptionMessage getEmulatorMessage(int code) {
		ExceptionMessage message = null;
		switch (code) {
			case CODE_ERROR_RESOURCE_NOT_AVAILABLE: message = new ExceptionMessage(IStatus.ERROR,DeviceUIResources.TML_Resource_Not_Available);break;			
			case CODE_ERROR_HANDLER_NOT_INSTANCED: message = new ExceptionMessage(IStatus.ERROR,DeviceUIResources.TML_Handler_Not_Instanced);break;
			default: message = new ExceptionMessage(IStatus.ERROR,DeviceUIResources.TML_Error); break;		        
		}			
		return message;
	}

}
