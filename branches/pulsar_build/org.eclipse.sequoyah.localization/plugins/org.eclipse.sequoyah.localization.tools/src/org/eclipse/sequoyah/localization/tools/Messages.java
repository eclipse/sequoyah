package org.eclipse.sequoyah.localization.tools;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.sequoyah.localization.tools.messages"; //$NON-NLS-1$
	public static String LocalizationToolsPlugin_0;
	public static String LocalizationToolsPlugin_1;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
