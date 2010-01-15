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

package org.eclipse.tml.common.utilities.exception;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.tml.common.utilities.UtilitiesResources;

public class TmLExceptionStatus extends AbstractExceptionStatus {
	public static final int CODE_ERROR_RESOURCE_NOT_AVAILABLE = 1001;
	

	public TmLExceptionStatus(IStatus status) {
		super(status);
	}
	
	public TmLExceptionStatus(int severity, String pluginId, int code, String message, Throwable exception) {
		super(severity, pluginId, code, message, exception);
	}

	public TmLExceptionStatus(int code,String pluginId){
		super(code,pluginId,null,null);
	}

	public TmLExceptionStatus(int code,String pluginId,Throwable exception) {
		super(code,pluginId,exception);
	}
	
	public TmLExceptionStatus(int code,String pluginId,Object[] args,Throwable exception) {
		super(code,pluginId,args,exception);
		
	}
	
	@Override
	public ExceptionMessage getEmulatorMessage(int code) {
		ExceptionMessage message = null;
		switch (code) {
			case CODE_ERROR_RESOURCE_NOT_AVAILABLE: message = new ExceptionMessage(IStatus.ERROR,UtilitiesResources.TML_Resource_Not_Available);break;			
			default: message = new ExceptionMessage(IStatus.ERROR,UtilitiesResources.TML_Error); break;		        
		}			
		return message;
	}

}
