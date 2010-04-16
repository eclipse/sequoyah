/**
 * 
 */
package org.eclipse.sequoyah.android.cdt.build.core;

import java.io.File;

/**
 * Service for getting information about the Android NDK.
 */
public interface INDKService {

	File getNDKLocation();
	
	void setNDKLocation(File location);
	
}
