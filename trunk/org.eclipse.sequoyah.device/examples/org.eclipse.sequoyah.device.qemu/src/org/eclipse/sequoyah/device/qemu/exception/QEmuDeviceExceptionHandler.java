/********************************************************************************
 * Copyright (c) 2007 Motorola Inc and others.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Fabio Fantato (Motorola)
 * 
 * Contributors:
 * Fabio Fantato (Motorola) - bug#221733 - code revisited
 * Yu-Fen Kuo  (MontaVista) - bug#236476 - provide a generic device type 
 ********************************************************************************/

package org.eclipse.tml.device.qemu.exception;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.tml.common.utilities.exception.ExceptionHandler;
import org.eclipse.tml.common.utilities.exception.TmLException;
import org.eclipse.tml.device.qemu.QEmuPlugin;

public class QEmuDeviceExceptionHandler extends ExceptionHandler {

	public static TmLException exception(IStatus status) {
		return new TmLException(new QEmuDeviceExceptionStatus(status));
	}

	public static TmLException exception(int severity, int code,
			String message, Throwable exception) {
		return new TmLException(new QEmuDeviceExceptionStatus(severity,
				QEmuPlugin.PLUGIN_ID, code, message, exception));
	}

	public static TmLException exception(int code) {
		return new TmLException(new QEmuDeviceExceptionStatus(code,
				QEmuPlugin.PLUGIN_ID, null, null));
	}

	public static TmLException exception(int code, Throwable exception) {
		return new TmLException(new QEmuDeviceExceptionStatus(code,
				QEmuPlugin.PLUGIN_ID, exception));
	}

	public static TmLException exception(int code, Object[] args,
			Throwable exception) {
		return new TmLException(new QEmuDeviceExceptionStatus(code,
				QEmuPlugin.PLUGIN_ID, args, exception));
	}

}
