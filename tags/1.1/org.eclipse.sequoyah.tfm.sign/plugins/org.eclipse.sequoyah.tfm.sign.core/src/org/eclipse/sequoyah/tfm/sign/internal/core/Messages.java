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
package org.eclipse.sequoyah.tfm.sign.internal.core;

import org.eclipse.osgi.util.NLS;

/**
 * @author Diego Sandin
 * @since 1.0
 */
public class Messages extends NLS {

    private static final String BUNDLE_NAME = "org.eclipse.sequoyah.tfm.sign.internal.core.messages"; //$NON-NLS-1$

    public static String SignExtensionManager_ErrorGettingSignProperties;

    public static String SignExtensionManager_ErrorStoringSignProperties;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }

}
