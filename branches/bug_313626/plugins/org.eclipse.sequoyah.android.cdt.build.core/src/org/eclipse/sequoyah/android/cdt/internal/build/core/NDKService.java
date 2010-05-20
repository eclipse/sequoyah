/**
 * 
 */
package org.eclipse.sequoyah.android.cdt.internal.build.core;

import java.io.File;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.sequoyah.android.cdt.build.core.INDKService;

/**
 * Implementation of the NDK service.
 */
public class NDKService implements INDKService {

	private static final String NDK_LOCATION = "ndkLocation";
	
	public File getNDKLocation() {
		IEclipsePreferences prefs = new InstanceScope().getNode(Activator.PLUGIN_ID);
		String loc = prefs.get(NDK_LOCATION, null);
		return loc != null ? new File(loc) : null;
	}

	public void setNDKLocation(File location) {
		IEclipsePreferences prefs = new InstanceScope().getNode(Activator.PLUGIN_ID);
		prefs.put(NDK_LOCATION, location.getAbsolutePath());
	}

}
