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
import org.eclipse.tml.common.utilities.UtilitiesPlugin;

public class ExceptionHandler {
	

	public static String logException(TmLException exception) {
		IStatus status = exception.getStatus(); 
		int code = status.getCode();
		String plugin = status.getPlugin();
		String msg = status.getMessage();
		Throwable t = status.getException();
		String logMessage= msg+"(PLUGIN="+plugin+";ERROR="+String.valueOf(code)+")";
		switch(exception.getStatus().getSeverity()){
			case IStatus.INFO:
				UtilitiesPlugin.logInfo(logMessage);
				break;
			case IStatus.WARNING:
				UtilitiesPlugin.logWarning(logMessage);
				break;
			default:
				if (t!=null) {
					UtilitiesPlugin.logError(logMessage, t);
				} else {
					UtilitiesPlugin.logError(logMessage);
				}				
		}
		return logMessage;
	}
	
	public static void showException(TmLException exception) {
		@SuppressWarnings("unused")
		String logMessage = logException(exception);
		@SuppressWarnings("unused")
		Throwable t = exception.getStatus().getException();
		// UI print message
	}
	
}
