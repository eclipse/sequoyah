/**
 * 
 */
package org.eclipse.sequoyah.android.cdt.internal.build.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * @author dschaefer
 *
 */
public class AddNativeWizard extends Wizard {
	private final IWorkbenchWindow window;
	private final IProject project; 
	
	private AddNativeProjectPage projectPage;
	
	public AddNativeWizard(IWorkbenchWindow window, IProject project) {
		this.window = window;
		this.project = project;
		
		setWindowTitle(Messages.AddNativeWizard_native_wizard_title);
		setDialogSettings(UIPlugin.getDefault().getDialogSettings());
	}
	
	@Override
	public void addPages() {
		projectPage = new AddNativeProjectPage(project.getName(), false);
		addPage(projectPage);
	}
	
	@Override
	public boolean canFinish() {
		return projectPage.isNDKLocationValid() && projectPage.isLibraryNameValid();
	}
	
	@Override
	public boolean performFinish() {
				
		return projectPage.performFinish(window, project);
	}

}
