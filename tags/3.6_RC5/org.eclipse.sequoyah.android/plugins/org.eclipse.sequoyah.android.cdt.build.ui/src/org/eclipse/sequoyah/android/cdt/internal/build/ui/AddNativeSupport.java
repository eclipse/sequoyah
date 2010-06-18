/*******************************************************************************
 * Copyright (c) 2010 Wind River Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Doug Schaefer (WRS) - Initial API and implementation
 * Carlos Alberto Souto Junior - Further improvements in the Wizard
 *******************************************************************************/
package org.eclipse.sequoyah.android.cdt.internal.build.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * 
 */
public class AddNativeSupport implements IObjectActionDelegate
{

    private IWorkbenchPart targetPart;

    private IProject project;

    public void run(IAction action)
    {
        // Run the wizard
        AddNativeWizard wizard =
                new AddNativeWizard(targetPart.getSite().getWorkbenchWindow(), project);
        WizardDialog dialog = new WizardDialog(targetPart.getSite().getShell(), wizard);
        dialog.open();
    }

    public void selectionChanged(IAction action, ISelection selection)
    {
        if (selection instanceof IStructuredSelection)
        {
            Object selected = ((IStructuredSelection) selection).getFirstElement();
            if (selected instanceof IProject)
            {
                project = (IProject) selected;
            }
            else if (selected instanceof PlatformObject)
            {
                project = (IProject) ((PlatformObject) selected).getAdapter(IProject.class);
            }
            else
            {
                project = null;
            }
        }
    }

    public void setActivePart(IAction action, IWorkbenchPart targetPart)
    {
        this.targetPart = targetPart;
    }

}
