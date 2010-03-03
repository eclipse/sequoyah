/**
 * Copyright (c) 2009 Motorola.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Diego Sandin (Motorola) - Initial Version
 */
package org.eclipse.mtj.tfm.sign.core.exception;

/**
 * @author Diego Sandin
 * @since 1.0
 */
public class SignException extends Exception {

    /**
     */
    private static final long serialVersionUID = 869810851382398797L;

    /**
     * Creates a new instance of SignException.
     */
    public SignException() {
    }

    /**
     * Creates a new instance of SignException.
     * 
     * @param message
     */
    public SignException(String message) {
        super(message);
    }

    /**
     * Creates a new instance of SignException.
     * 
     * @param cause
     */
    public SignException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new instance of SignException.
     * 
     * @param message
     * @param cause
     */
    public SignException(String message, Throwable cause) {
        super(message, cause);
    }
}
