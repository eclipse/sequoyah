package org.eclipse.sequoyah.localization.android;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.sequoyah.localization.android.messages"; //$NON-NLS-1$
	public static String AndroidLocalizationPlugin_0;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
