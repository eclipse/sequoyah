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
package org.eclipse.mtj.tfm.internal.sign.smgmt.sun;

import org.eclipse.osgi.util.NLS;

public final class Messages extends NLS {
    
    private static final String BUNDLE_NAME = "org.eclipse.mtj.tfm.internal.sign.smgmt.sun.messages";//$NON-NLS-1$

    public static String PreferenceInitializer_set_location_message;

    public static String SunSecurityManagement_Could_not_execute;
    public static String SunSecurityManagement_Creating_key_alias;
    public static String SunSecurityManagement_defaultErrorMessage;
    public static String SunSecurityManagement_defaultErrorMessage2;
    public static String SunSecurityManagement_Opening_key_store;
    public static String SunSecurityManagement_PluginVendor;
    public static String SunSecurityManagement_PluginVersion;
    public static String SunSecurityManagement_Security_tool_not_configured_correctly;
    public static String SunSecurityManagement_Security_tool_using_features;
    public static String SunSecurityManagement_Sun_Keytool;

    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
        // Do not instantiate
    }
}