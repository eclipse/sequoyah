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

	private Messages() {
		// Do not instantiate
	}

	public static String PreferenceInitializer_0;
	public static String SecurityManagementImplementation_0;
	public static String SecurityManagementImplementation_Could_not_execute;
	public static String SecurityManagementImplementation_Creating_key_alias;
	public static String SecurityManagementImplementation_MTJ_Sun_Keytool;
	public static String SecurityManagementImplementation_Opening_key_store;
	public static String SecurityManagementImplementation_PluginVendor;
	public static String SecurityManagementImplementation_PluginVersion;
	public static String SecurityManagementImplementation_Security_tool_not_configured_correctly;
	public static String SecurityManagementImplementation_Security_tool_using_features;

	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}