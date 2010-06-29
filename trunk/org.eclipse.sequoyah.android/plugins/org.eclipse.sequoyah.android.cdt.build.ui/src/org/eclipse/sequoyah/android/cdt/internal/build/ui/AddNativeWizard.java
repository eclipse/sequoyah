/**
 * 
 */
package org.eclipse.sequoyah.android.cdt.internal.build.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.sequoyah.android.cdt.internal.build.ui.AddNativeProjectPage;

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

        setWindowTitle(Messages.AddNativeWizard_native_wizard_title);
        setNeedsProgressMonitor(true);
        setDialogSettings(UIPlugin.getDefault().getDialogSettings());
        ImageDescriptor img = new ImageDescriptor()
        {
            
            @Override
            public ImageData getImageData()
            {
                ImageData data = new ImageData(getClass().getResourceAsStream("/icons/android_native_64x64.png"));
                return data;
            }
        };
        setDefaultPageImageDescriptor(img);
    }

    @Override
    public void addPages()
    {
        projectPage = new AddNativeProjectPage(project == null ? null : project.getName(), false);
        addPage(projectPage);
    }

    @Override
    public boolean canFinish()
    {
        return projectPage.isPageComplete();
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
            UIPlugin.getDefault().getLog()
                    .log(new Status(IStatus.ERROR, PLUGIN_ID, ex.getMessage(), ex));
            MessageDialog.openError(getShell(), Messages.AddNativeWizard_native_wizard_title,
                    Messages.AddNativeWizard__Message_UnexpectedErrorWhileAddingNativeSupport);
            isOKPerformFinish[0] = false;
        }

        // return performing finish status
        return isOKPerformFinish[0];
    }
}
