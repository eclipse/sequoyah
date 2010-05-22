/**
 * 
 */
package org.eclipse.sequoyah.android.cdt.build.core;

import org.eclipse.core.resources.IProject;


/**
 * Service for getting information about the Android NDK.
 */
public interface INDKService {

	String getNDKLocation();
	
	void setNDKLocation(String location);
	
	void addNativeSupport(IProject project, String libraryName);
	
}
