package org.eclipse.sequoyah.android.cdt.build.core;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.sequoyah.android.cdt.internal.build.core.Activator;
import org.eclipse.sequoyah.android.cdt.internal.build.core.TemplatedInputStream;

/**
 * Class that provide utility methods
 * @author cwhm38
 */
public class NDKUtils
{
    /**
     * Auxiliary method to set the NDK location in the Sequoyah framework
     * @param location - NDK location
     */
    public static void setNDKLocation(String location)
    {
        INDKService ndkService = Activator.getService(INDKService.class);
        ndkService.setNDKLocation(location);
    }

    /**
     * Auxiliary method to re-generate the Android makefile when needed.
     * @param project - Project that will contain the makefile
     * @param libname - Library name
     */
    public static void generateAndroidMakeFile(final IProject project, final String libName)
    {
        // The operation to do all the dirty work
        IWorkspaceRunnable op = new IWorkspaceRunnable()
        {
            public void run(IProgressMonitor monitor) throws CoreException
            {
                // Create the source folders, if necessary. 
                // Shouldn't be the case, but you never know what the user is up to.
                IFolder sourceFolder = project.getFolder("jni");

                if (!sourceFolder.exists())
                {
                    sourceFolder.create(true, true, monitor);
                }

                InputStream makefileIn = null;
                InputStream templateIn = null;

                try
                {
                    // Generate the Android.mk file
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("lib", libName);

                    URL makefileURL = Activator.getFile(new Path("templates/Android.mk"));
                    makefileIn = makefileURL.openStream();
                    templateIn = new TemplatedInputStream(makefileIn, map);

                    IFile makefile = sourceFolder.getFile("Android.mk");

                    if (makefile.exists())
                    {
                        // Empty file contents because delete does not work properly
                        FileOutputStream outputStream =
                                new FileOutputStream(makefile.getLocation().toFile(), false);
                        outputStream.close();

                        // Append content
                        makefile.appendContents(templateIn, true, false, monitor);
                    }
                    else
                    {
                        // Create new makefile
                        makefile.create(templateIn, true, monitor);
                    }

                    // Refresh parent folder
                    makefile.getParent().refreshLocal(IResource.DEPTH_ONE, monitor);

                }
                catch (IOException e)
                {
                    throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e
                            .getLocalizedMessage(), e));
                }
                finally
                {

                    if (templateIn != null)
                    {
                        try
                        {
                            templateIn.close();
                        }
                        catch (IOException e)
                        {
                            // Do nothing
                        }
                    }
                }

            }
        };

        IWorkspace workspace = ResourcesPlugin.getWorkspace();

        try
        {
            workspace.run(op, null, IWorkspace.AVOID_UPDATE, null);
        }
        catch (CoreException e)
        {
            Activator.getDefault().getLog().log(e.getStatus());
        }
    }
}
