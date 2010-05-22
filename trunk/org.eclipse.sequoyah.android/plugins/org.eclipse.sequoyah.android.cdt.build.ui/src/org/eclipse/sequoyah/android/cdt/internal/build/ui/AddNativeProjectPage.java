/**
 * 
 */
package org.eclipse.sequoyah.android.cdt.internal.build.ui;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.sequoyah.android.cdt.build.core.INDKService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

/**
 * @author dschaefer
 *
 */
public class AddNativeProjectPage extends WizardPage {

	private final IProject project;
	
	private Text location;
	private Text libraryName;
	
	public AddNativeProjectPage(IProject project) {
		super("projectPage");
		setTitle("Project");
		setDescription("Settings for adding native support to the project");
		
		this.project = project;
	}

	public void createControl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		comp.setLayout(layout);
		comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		addNDKLocation(comp);
		addLibraryName(comp);
		
		setControl(comp);
	}

	private void addNDKLocation(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		group.setLayout(layout);
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		group.setText("NDK Location");

		location = new Text(group, SWT.BORDER);
		location.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		location.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validateNDKLocation();
			}
		});
		validateNDKLocation();
		
		String ndkLocDir = Activator.getService(INDKService.class).getNDKLocation();
		if (ndkLocDir != null)
			location.setText(ndkLocDir);
		
		Button browse = new Button(group, SWT.NONE);
		browse.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		browse.setText("Browse");
		browse.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(location.getShell());
				dialog.setMessage("NDK Location");
				String dir = dialog.open();
				if (dir != null)
					location.setText(dir);
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	private void validateNDKLocation() {
		if (isNDKLocationValid())
			setErrorMessage(null);
		else
			setErrorMessage("Invalid Android NDK location");
		
		if (getWizard().getContainer().getCurrentPage() != null)
			getWizard().getContainer().updateButtons();
	}
	
	public boolean isNDKLocationValid() {
		String locStr = location.getText();
		File locFile = new File(locStr);
		return locFile.exists();
	}
	
	public String getNDKLocation() {
		return location.getText();
	}
	
	private void addLibraryName(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		group.setLayout(layout);
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		group.setText("Library name (lib*.so will be added)");
		
		libraryName = new Text(group, SWT.BORDER);
		libraryName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		libraryName.setText(project.getName());
	}

	public String getLibraryName() {
		return libraryName.getText();
	}
	
}
