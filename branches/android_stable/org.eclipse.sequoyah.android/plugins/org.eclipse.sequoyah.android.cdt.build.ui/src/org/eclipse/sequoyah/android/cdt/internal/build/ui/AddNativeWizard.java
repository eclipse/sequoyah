/**
 * 
 */
package org.eclipse.sequoyah.android.cdt.internal.build.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.sequoyah.android.cdt.build.core.INDKService;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.WorkbenchException;

/**
 * @author dschaefer
 *
 */
public class AddNativeWizard extends Wizard
{

    private final IWorkbenchWindow window;

    private final IProject project;

    private AddNativeProjectPage projectPage;

    public AddNativeWizard(IWorkbenchWindow window, IProject project)
    {
        this.window = window;
        this.project = project;

        setWindowTitle("Add Android Native Support");
        setDialogSettings(Activator.getDefault().getDialogSettings());
    }

    @Override
    public void addPages()
    {
        projectPage = new AddNativeProjectPage(project);
        addPage(projectPage);
    }

    @Override
    public boolean canFinish()
    {
        return projectPage.isNDKLocationValid();
    }

    @Override
    public boolean performFinish()
    {
        // Switch to the C perspective
        try
        {
            window.getWorkbench().showPerspective("org.eclipse.cdt.ui.CPerspective", window); //$NON-NLS-1
        }
        catch (WorkbenchException e)
        {
            Activator.getDefault().getLog().log(e.getStatus());
        }

        // Grab the data from the pages
        final String libraryName = projectPage.getLibraryName();
        // Save the library name in the project properties.
        try
        {
            project.setPersistentProperty(INDKService.libName, libraryName);
        }
        catch (CoreException e)
        {
            Activator.getDefault().getLog().log(e.getStatus());
        }

        // Save the NDK location
        INDKService ndkService = Activator.getService(INDKService.class);
        ndkService.setNDKLocation(projectPage.getNDKLocation());

        // Add the native support
        ndkService.addNativeSupport(project, libraryName);

        return true;
    }

}
