/**
 * 
 */
package org.eclipse.sequoyah.android.cdt.internal.build.ui;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * @author dschaefer
 *
 */
public class AddNativeWizard extends Wizard {
    /**
     * This plug-in Id
     */
    private static final String PLUGIN_ID = "org.eclipse.sequoyah.android.cdt.build.ui"; //$NON-NLS-1$

    private final IWorkbenchWindow window;

    private final IProject project;

    private AddNativeProjectPage projectPage;

    public AddNativeWizard(IWorkbenchWindow window, IProject project)
    {
        this.window = window;
        this.project = project;

        setWindowTitle(Messages.AddNativeWizard_native_wizard_title);
        setNeedsProgressMonitor(true);
        setDialogSettings(UIPlugin.getDefault().getDialogSettings());
    }

    @Override
    public void addPages()
    {
        projectPage = new AddNativeProjectPage(project.getName(), false);
        addPage(projectPage);
    }

    @Override
    public boolean canFinish()
    {
        return projectPage.isNDKLocationValid() && projectPage.isLibraryNameValid();
    }

    @Override
    public boolean performFinish()
    {
        // variable for perform finish
        final boolean[] isOKPerformFinish = new boolean[1];

        // execute finish processes with progress monitor
        try
        {
            getContainer().run(false, false, new IRunnableWithProgress()
            {

                public void run(final IProgressMonitor monitor) throws InvocationTargetException,
                        InterruptedException
                {
                    // get monitor
                    final SubMonitor subMonitor = SubMonitor.convert(monitor, 10);
                    // finish up
                    isOKPerformFinish[0] =
                            projectPage.performFinish(window, project, subMonitor.newChild(10));
                }
            });
        }
        catch (Exception ex)
        {
            // treat error - log, show the error message and set the flag for performing finish
            UIPlugin.getDefault().getLog().log(
                    new Status(IStatus.ERROR, PLUGIN_ID, ex.getMessage(), ex));
            MessageDialog.openError(getShell(), Messages.AddNativeWizard_native_wizard_title,
                    Messages.AddNativeWizard__Message_UnexpectedErrorWhileAddingNativeSupport);
            isOKPerformFinish[0] = false;
        }

        // return performing finish status
        return isOKPerformFinish[0];
    }
}
