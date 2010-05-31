/**
 * 
 */
package org.eclipse.sequoyah.android.cdt.internal.build.ui;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.sequoyah.android.cdt.build.core.INDKService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

/**
 * @author dschaefer
 *
 */
public class AddNativeProjectPage extends WizardPage {

	@Override
	public boolean canFlipToNextPage() {
		
		return false;
	}
	
	private String libName = ""; //$NON-NLS-1$
	
	private Link setNDKLocationLink;
	
	private Text location;
	private Text libraryText;
	boolean isNewProjectWizardPage = false;
	
	public AddNativeProjectPage(String libraryName, boolean isNewProjectWizardPage) {
		this(isNewProjectWizardPage);
		this.libName = libraryName;
	}
	
	public AddNativeProjectPage(boolean isNewProjectWizardPage) {
		super("native_page"); //$NON-NLS-1$
		setTitle(Messages.AddNativeProjectPage_native_page_title);
		setDescription(Messages.AddNativeProjectPage_native_page_description);
 		

		this.isNewProjectWizardPage = isNewProjectWizardPage;
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
		layout.numColumns = 1;
		group.setLayout(layout);
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		group.setText(Messages.AddNativeProjectPage_ndk_location_group_text);

		location = new Text(group, SWT.BORDER);
		location.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		location.setEditable(false);
		
		if(!isNDKLocationValid())
		{
			setErrorMessage(Messages.AddNativeProjectPage_ndk_invalid_path_msg);
		}
		
		String path = PlatformUI.getPreferenceStore().getString(UIPlugin.NDK_LOCATION_PREFERENCE);
		if(path != null)
		{
			location.setText(path);
		}		setNDKLocationLink = new Link(group, SWT.NONE);
		GridData gridData = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
        setNDKLocationLink.setLayoutData(gridData);
        setNDKLocationLink.setText(Messages.AddNativeProjectPage_ndk_preference_link_text);
        setNDKLocationLink.setEnabled(true);
        setNDKLocationLink.addListener(SWT.Selection, new Listener()
        {
			public void handleEvent(Event event) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
                {
                    public void run()
                    {
                    	PreferenceManager prefManager = PlatformUI.getWorkbench().getPreferenceManager();
                        PreferenceDialog dialog = new PreferenceDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                                        .getShell(), prefManager);
                        dialog.setSelectedNode("org.eclipse.sequoyah.android.cdt.internal.build.ui.preferencepage"); //$NON-NLS-1$
                        
                        dialog.create();
                        dialog.setBlockOnOpen(true);
                        dialog.open();
                        //blocks until user clicks ok
                        String path = PlatformUI.getPreferenceStore().getString(UIPlugin.NDK_LOCATION_PREFERENCE);
                        location.setText(path);
                        
                        if(!isNDKLocationValid())
                		{
                        	setErrorMessage(Messages.AddNativeProjectPage_ndk_invalid_path_msg); //$NON-NLS-1$
                		}
                        else if(!isLibraryNameValid())
                        {
                        	setErrorMessage(Messages.AddNativeProjectPage_empty_library_name_msg);
                        }
                        else
                        {
                        	setErrorMessage(null);
                        }
                        getWizard().getContainer().updateButtons();
                    }
                });
			}
        });
 	}
	
	public String getNDKLocation() {
		return location.getText();
	}
	
	private void addLibraryName(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		group.setLayout(layout);
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		group.setText(Messages.AddNativeProjectPage_library_name_group_text);
		
		libraryText = new Text(group, SWT.BORDER);
		libraryText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		libraryText.setText(libName);
		
		if(!isLibraryNameValid())
		{
			setErrorMessage(Messages.AddNativeProjectPage_empty_library_name_msg); //$NON-NLS-1$
		}
		libraryText.addListener(SWT.Modify, new Listener()
		{
			public void handleEvent(Event event) 
			{
				if(!isLibraryNameValid())
                {
                	setErrorMessage(Messages.AddNativeProjectPage_empty_library_name_msg); //$NON-NLS-1$
                }
				else if(!isNDKLocationValid())
        		{
                	setErrorMessage(Messages.AddNativeProjectPage_ndk_invalid_path_msg); //$NON-NLS-1$
        		}
                else 
                {
                	setErrorMessage(null);
                }
				getWizard().getContainer().updateButtons();
			}
		});
	}

	public String getLibraryName() {
		return libraryText.getText();
	}

	//returns null. There is no next page
	@Override
	public IWizardPage getNextPage() 
	{
		return null;
	}

	//true when valid ndk path is set and library name specified
	@Override
	public boolean isPageComplete() {
		boolean isComplete = false;
		
		if(isNDKLocationValid() && isLibraryNameValid())
		{
			isComplete = true;
		}
		
		return isComplete;
	}
	
	public boolean isNDKLocationValid()
	{
		boolean isValid = true;
		String path = PlatformUI.getPreferenceStore().getString(UIPlugin.NDK_LOCATION_PREFERENCE);
		
		if(path.length() == 0 || !NDKPreferencePage.validateNDKDirectory(path))
		{
			isValid = false;
		}
		return isValid;
	}
	
	public boolean isLibraryNameValid()
	{
		boolean isValid = false;
		String libName = libraryText.getText();
		if(libName.trim().length() > 0)
		{
			isValid = true;
		}
		return isValid;
	}
	
	public void initializeLibraryField(String libName)
	{
		libraryText.setText(libName);
		libraryText.update();
	}
	
	public boolean performFinish(IWorkbenchWindow window, IProject project)
	{
		boolean success = true;
		try
		{
			// Switch to the C perspective
			window.getWorkbench().showPerspective("org.eclipse.cdt.ui.CPerspective", window); //$NON-NLS-1 //$NON-NLS-1$

			// Grab the data from the pages
			final String libraryName = getLibraryName();

			// Save the NDK location
			INDKService ndkService = Activator.getService(INDKService.class);
			ndkService.setNDKLocation(getNDKLocation());

			// Add the native support
			ndkService.addNativeSupport(project, libraryName);
			
    		project.setPersistentProperty(UIPlugin.libName, libraryName);
		} 
		catch (WorkbenchException e) 
		{
			Activator.getDefault().getLog().log(e.getStatus());
			success = false;
		}
		return success;
	}
	
}
