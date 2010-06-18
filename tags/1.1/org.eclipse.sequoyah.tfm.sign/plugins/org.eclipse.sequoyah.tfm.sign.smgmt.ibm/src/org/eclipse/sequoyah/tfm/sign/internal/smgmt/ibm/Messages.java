/**
 * Copyright (c) 2006,2009 IBM Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation         - initial API and implementation
 *     Diego Sandin (Motorola) - Porting code to TFM Sign Framework [Bug 286387]
 */
package org.eclipse.sequoyah.tfm.sign.internal.smgmt.ibm;

import org.eclipse.osgi.util.NLS;

public final class Messages extends NLS {

    private static final String BUNDLE_NAME = "org.eclipse.sequoyah.tfm.sign.internal.smgmt.ibm.messages";//$NON-NLS-1$

    private Messages() {
        // Do not instantiate
    }

    public static String SecurityManagementImpl_6;

    public static String SecurityManagementImpl_Could_not_execute;

    public static String SecurityManagementImpl_Creating_key_alias;

    public static String SecurityManagementImpl_GetSecurityManagmentException;

    public static String SecurityManagementImpl_Opening_key_store;

    public static String SecurityManagementImpl_PluginVendor;

    public static String SecurityManagementImpl_PluginVersion;

    public static String SecurityManagementImpl_Security_manager;

    public static String SecurityManagementImpl_Security_tool_not_configured_correctly;

    public static String SecurityManagementImpl_Specify_home_directory;

    public static String PreferenceInitializer_0;

    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
}