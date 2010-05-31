package org.eclipse.sequoyah.android.cdt.internal.build.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.sequoyah.android.cdt.internal.build.ui.messages"; //$NON-NLS-1$
	public static String AddNativeProjectPage_empty_library_name_msg;
	public static String AddNativeProjectPage_library_name_group_text;
	public static String AddNativeProjectPage_native_page_description;
	public static String AddNativeProjectPage_native_page_title;
	public static String AddNativeProjectPage_ndk_invalid_path_msg;
	public static String AddNativeProjectPage_ndk_location_group_text;
	public static String AddNativeProjectPage_ndk_preference_link_text;
	public static String AddNativeWizard_native_wizard_title;
	public static String NDKPreferencePage_invalid_NDK_path_msg;
	public static String NDKPreferencePage_NDK_location;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}