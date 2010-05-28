/**
 * 
 */
package org.eclipse.sequoyah.android.cdt.build.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.sequoyah.android.cdt.internal.build.core.Activator;

/**
 * Service for getting information about the Android NDK.
 */
public interface INDKService
{

    // Property IDs

    /**
     * Library name property
     */
    public QualifiedName libName = new QualifiedName(Activator.PLUGIN_ID, "libName");

    String getNDKLocation();

    void setNDKLocation(String location);

    void addNativeSupport(IProject project, String libraryName);

}
