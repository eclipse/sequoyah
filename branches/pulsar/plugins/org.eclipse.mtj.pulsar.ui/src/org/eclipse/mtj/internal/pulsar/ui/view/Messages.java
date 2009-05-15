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
 *  Euclides Neto (Motorola) - Added RefreshAction entries.
 *  Euclides Neto (Motorola) - Added SDKInstallView entries.
 */

package org.eclipse.mtj.internal.pulsar.ui.view;

import org.eclipse.osgi.util.NLS;

/**
 *
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mtj.internal.pulsar.ui.view.messages"; //$NON-NLS-1$
	public static String P2InstallerUI_DirerctoryDialogMessage;
	public static String P2InstallerUI_ResoultionOperationLabel;
	public static String SDKInstallView_DetailsActionLabel;
	public static String SDKInstallView_DetailsActionToolTip;
	public static String SDKInstallView_GettingRepoInfoMessage;
	public static String SDKInstallView_RefreshActionLabel;
	public static String SDKInstallView_RefreshActionToolTip;
	public static String SDKInstallView_InstallActionLabel;
	public static String SDKInstallView_InstallActionToolTip;
	public static String SDKInstallView_InstallError;
	public static String SDKInstallView_InstallersColLabel;
	public static String SDKInstallView_StatusColLabel;
	public static String SDKInstallView_UpdatingInstallersJobTitle;
	public static String StatusLabelProvider_InstalledLabel;
	public static String StatusLabelProvider_UninstalledLabel;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
