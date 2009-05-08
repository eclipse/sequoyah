/**
 * Copyright (c) 2009 Nokia Corporation and/or its subsidiary(-ies).
 * All rights reserved.
 * This component and the accompanying materials are made available
 * under the terms of the License "Eclipse Public License v1.0"
 * which accompanies this distribution, and is available
 * at the URL "http://www.eclipse.org/legal/epl-v10.html".
 *
 * Contributors:
 * 	David Dubrow
 *
 */

package org.eclipse.mtj.internal.pulsar.core;

import org.eclipse.osgi.util.NLS;

/**
 *
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mtj.internal.pulsar.core.messages"; //$NON-NLS-1$
	public static String DefaultSDKRepositoryProvider_FileReadError;
	public static String DefaultSDKRepositoryProvider_URLError;
	public static String ExecuteAction_ExecuteError;
	public static String ExecuteAction_MissingArtifactError;
	public static String ExecuteAction_ParamNotSetError;
	public static String ExecuteAction_UndoUnsupportedError;
	public static String SDK_DocumentationURLError;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
