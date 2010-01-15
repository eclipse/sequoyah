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

package org.eclipse.tml.service.stop.exception;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.tml.common.utilities.exception.AbstractExceptionStatus;
import org.eclipse.tml.common.utilities.exception.ExceptionMessage;
import org.eclipse.tml.service.stop.StopServiceResources;

public class StopServiceExceptionStatus extends AbstractExceptionStatus {
	public static final int CODE_ERROR_RESOURCE_NOT_AVAILABLE = 2101;
	

	public StopServiceExceptionStatus(IStatus status) {
		super(status);
	}
	
	public StopServiceExceptionStatus(int severity, String pluginId, int code, String message, Throwable exception) {
		super(severity, pluginId, code, message, exception);
	}

	public StopServiceExceptionStatus(int code,String pluginId){
		super(code,pluginId,null,null);
	}

	public StopServiceExceptionStatus(int code,String pluginId,Throwable exception) {
		super(code,pluginId,exception);
	}
	
	public StopServiceExceptionStatus(int code,String pluginId,Object[] args,Throwable exception) {
		super(code,pluginId,args,exception);
		
	}
	
	@Override
	public ExceptionMessage getEmulatorMessage(int code) {
		ExceptionMessage message = null;
		switch (code) {
			case CODE_ERROR_RESOURCE_NOT_AVAILABLE: message = new ExceptionMessage(IStatus.ERROR,StopServiceResources.TML_Resource_Not_Available);break;			
			default: message = new ExceptionMessage(IStatus.ERROR,StopServiceResources.TML_Error); break;		        
		}			
		return message;
	}

}
