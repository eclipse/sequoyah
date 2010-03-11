package org.eclipse.sequoyah.device.framework;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.sequoyah.device.framework.messages"; //$NON-NLS-1$
	public static String DevicePlugin_0;
	public static String DevicePlugin_1;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
