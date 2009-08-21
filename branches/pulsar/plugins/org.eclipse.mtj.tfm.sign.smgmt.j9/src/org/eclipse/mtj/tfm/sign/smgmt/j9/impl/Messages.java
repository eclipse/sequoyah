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
package org.eclipse.mtj.tfm.sign.smgmt.j9.impl;

import org.eclipse.osgi.util.NLS;

public final class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.mtj.extension.smgmt.impl.j9.messages";//$NON-NLS-1$

	private Messages() {
		// Do not instantiate
	}

	public static String J9SecurityManager_Could_not_execute;
	public static String J9SecurityManager_Creating_key_alias;
	public static String J9SecurityManager_Description;
	public static String J9SecurityManager_getSecurityManagerException;
	public static String J9SecurityManager_Opening_keystore;
	public static String J9SecurityManager_PluginVendor;
	public static String J9SecurityManager_PluginVersion;
	public static String J9SecurityManager_Specify_directory_here;
	public static String J9SecurityManager_Tool_not_configured_correctly;
	public static String J9SecurityManager_Using_Security_management_features;
	public static String J9SecurityManagerPrefInitilizer_0;
	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}