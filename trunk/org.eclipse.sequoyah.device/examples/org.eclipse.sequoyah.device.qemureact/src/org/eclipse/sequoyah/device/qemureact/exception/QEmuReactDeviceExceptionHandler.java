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

package org.eclipse.tml.device.qemureact.exception;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.tml.common.utilities.exception.ExceptionHandler;
import org.eclipse.tml.common.utilities.exception.TmLException;
import org.eclipse.tml.device.qemureact.QEmuReactPlugin;

public class QEmuReactDeviceExceptionHandler extends ExceptionHandler {
	

	public static TmLException exception(IStatus status) {
		return new TmLException(new QEmuReactDeviceExceptionStatus(status));
	}
	
	public static TmLException exception(int severity, int code, String message, Throwable exception) {
		return new TmLException(new QEmuReactDeviceExceptionStatus(severity, QEmuReactPlugin.PLUGIN_ID, code, message, exception));
	}

	public static TmLException exception(int code){
		return new TmLException(new QEmuReactDeviceExceptionStatus(code,QEmuReactPlugin.PLUGIN_ID,null,null));
	}

	public static TmLException exception(int code,Throwable exception) {
		return new TmLException(new QEmuReactDeviceExceptionStatus(code,QEmuReactPlugin.PLUGIN_ID,exception));
	}
	
	public static TmLException exception(int code,Object[] args,Throwable exception) {
		return new TmLException(new QEmuReactDeviceExceptionStatus(code,QEmuReactPlugin.PLUGIN_ID,args,exception));		
	}
	
}
