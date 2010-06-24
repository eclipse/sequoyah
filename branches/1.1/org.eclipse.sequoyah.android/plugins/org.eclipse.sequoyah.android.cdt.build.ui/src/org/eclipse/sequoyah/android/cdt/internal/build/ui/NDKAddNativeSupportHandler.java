package org.eclipse.sequoyah.android.cdt.internal.build.ui;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class NDKAddNativeSupportHandler extends AbstractHandler implements IHandler
{
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        IWorkbench workbench = PlatformUI.getWorkbench();

        if ((workbench != null) && !workbench.isClosing())
        {
            IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();

            if (window != null)
            {
                ISelection selection = window.getSelectionService().getSelection();
                IStructuredSelection structureSelection = null;
                if (selection instanceof IStructuredSelection)
                {
                    structureSelection = (IStructuredSelection) selection;
                }
                else
                {
                    structureSelection = new StructuredSelection();
                }

                Object selectionElement = structureSelection.getFirstElement();

                if (selectionElement == null)
                {
                    WizardDialog dialog =
                            new WizardDialog(window.getShell(), new AddNativeWizard(PlatformUI
                                    .getWorkbench().getActiveWorkbenchWindow(), null));
                    dialog.open();
                }
                else
                {
                    IResource resource = null;
                    if (selectionElement instanceof IResource)
                    {
                        resource = (IResource) selectionElement;
                    }
                    else if (selectionElement instanceof IAdaptable)
                    {
                        try
                        {
                            resource =
                                    (IResource) ((IAdaptable) selectionElement)
                                            .getAdapter(IResource.class);
                        }
                        catch (Exception e)
                        {

                        }
                    }

                    WizardDialog dialog = null;
                    if (resource != null)
                    {

                        dialog =
                                new WizardDialog(window.getShell(), new AddNativeWizard(PlatformUI
                                        .getWorkbench().getActiveWorkbenchWindow(),
                                        resource.getProject()));
                        dialog.open();
                    }
                }

            }
        }
        return workbench;
    }
}
