/**
 * 
 */
package org.eclipse.sequoyah.android.cdt.internal.build.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

/**
 * @author dschaefer
 *
 */
public class AddNativeProjectPage extends WizardPage {

	private final IProject project;
	
	private Text libraryName;
	private Text sourceFolderName;
	private Text outputFolderName;
	
	private static final String SOURCE_FOLDER_NAME = AddNativeProjectPage.class.getName() + ".sourceFolderName";
	private static final String OUTPUT_FOLDER_NAME = AddNativeProjectPage.class.getName() + ".outputFolderName";
	
	private static final String DEFAULT_SOURCE_FOLDER_NAME = "native";
	private static final String DEFAULT_OUTPUT_FOLDER_NAME = "obj";
	
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
		
		addLibraryName(comp);
		addSourceFolderName(comp);
		addOutputFolderName(comp);
		
		setControl(comp);
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
	
	private void addSourceFolderName(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		group.setLayout(layout);
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		group.setText("Source folder");
		
		IDialogSettings settings = getWizard().getDialogSettings();
		String name = settings.get(SOURCE_FOLDER_NAME);
		if (name == null)
			name = DEFAULT_SOURCE_FOLDER_NAME;
		
		sourceFolderName = new Text(group, SWT.BORDER);
		sourceFolderName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		sourceFolderName.setText(name);
	}
	
	public String getSourceFolderName() {
		return sourceFolderName.getText();
	}

	private void addOutputFolderName(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		group.setLayout(layout);
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		group.setText("Build output folder");
		
		IDialogSettings settings = getWizard().getDialogSettings();
		String name = settings.get(OUTPUT_FOLDER_NAME);
		if (name == null)
			name = DEFAULT_OUTPUT_FOLDER_NAME;
		
		outputFolderName = new Text(group, SWT.BORDER);
		outputFolderName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		outputFolderName.setText(name);
	}
	
	public String getOutputFolderName() {
		return outputFolderName.getText();
	}

	public void saveSettings() {
		IDialogSettings settings = getWizard().getDialogSettings();
		settings.put(SOURCE_FOLDER_NAME, sourceFolderName.getText());
		settings.put(OUTPUT_FOLDER_NAME, outputFolderName.getText());
	}
}
