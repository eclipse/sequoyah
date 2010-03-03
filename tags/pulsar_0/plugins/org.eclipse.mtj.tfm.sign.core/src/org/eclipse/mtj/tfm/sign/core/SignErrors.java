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
package org.eclipse.mtj.tfm.sign.core;

import java.util.ResourceBundle;

/**
 * @author Diego Sandin
 * @since 1.0
 * 
 */
public enum SignErrors {

    /**
     * 
     */
    SECURITY_BAD_KEYSTORE_OR_PASSWORD,

    /**
     * 
     */
    SECURITY_BAD_KEY_TYPE,

    /**
     * 
     */
    SECURITY_MANAGER_NOT_CONFIGURED,

    /**
     * 
     */
    SECURITY_ALIAS_DUPLICATE,

    /**
     * 
     */
    SECURITY_MALFORMED_PASSWORD,

    /**
     * 
     */
    GENERIC_ERROR,

    /**
     * 
     */
    GENERIC_SECURITY_ERROR,
    
    /**
     * 
     */
    GENERIC_PERMISSION_ERROR;

    private static final String BUNDLE_NAME = "org.eclipse.mtj.tfm.internal.sign.core.errors"; //$NON-NLS-1$
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
            .getBundle(BUNDLE_NAME);

    /**
     * This method returns the message associated with a particular error code
     * or warning.
     * 
     * @param error
     * @return
     */
    public static final String getErrorMessage(SignErrors error) {

        String result = "[Error " + error.ordinal() + "]";

        if ((error != GENERIC_ERROR) && (error != GENERIC_SECURITY_ERROR)) {
            try {
                result = RESOURCE_BUNDLE.getString(error.name()).trim();
            } catch (Exception e) {
            }
        }
        return result;
    }
}
