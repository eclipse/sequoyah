/********************************************************************************
 * Copyright (c) 2008 Motorola Inc. All rights reserved.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributors:
 * Otavio Ferranti (Motorola)
 * 
 * Contributors:
 * {Name} (company) - description of contribution.
 ********************************************************************************/

package org.eclipse.tml.framework.device.wizard.ui;

import org.eclipse.tml.framework.device.manager.InstanceManager;
import org.eclipse.tml.framework.device.wizard.DeviceWizardResources;
import org.eclipse.tml.framework.device.wizard.model.IWizardProjectPage;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * Creates a new instance wizard.
 * @author Otavio Ferranti
 *
 */
public class WizardNewProjectPage extends WizardPage implements IWizardProjectPage {

    // constants
    private static final String INSTANCE_NAME = "instance name"; //$NON-NLS-1$
	private static final int SIZING_HOST_FIELD_WIDTH = 250;
    	
	private String initialProjectName = ""; //$NON-NLS-1$
	private String projectName;
    
    private Text projectNameField;
	
   
    /**
     * Constructor - Creates a new project creation wizard page.
     *
     * @param pageName the name of this page
     */
    public WizardNewProjectPage(String pageName) {
        super(pageName);
        setPageComplete(false);
    }

    /**
     * Creates the project name specification controls.
     *
     * @param parent the parent composite
     */
    private final void createPropertyGroup(Composite parent) {
        // project specification group
        Composite propertyGroup = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        propertyGroup.setLayout(layout);
        propertyGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // new project label
        Label projectNameLabel = new Label(propertyGroup, SWT.NONE);
        projectNameLabel.setText(WizardNewProjectPage.INSTANCE_NAME);
        projectNameLabel.setFont(parent.getFont());

        projectNameField = new Text(propertyGroup, SWT.BORDER);
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.widthHint = SIZING_HOST_FIELD_WIDTH;
        projectNameField.setLayoutData(data);
        projectNameField.setFont(parent.getFont());
        
        // Set the initial value first before listener
        // to avoid handling an event during the creation.
        if (initialProjectName != null) {
        	projectNameField.setText(initialProjectName);
		}
        projectNameField.addListener(SWT.Modify, new Listener() {
	            public void handleEvent(Event e) {
	            	//setDisplayForSelection();
	                boolean valid = validatePage();
	                setPageComplete(valid);
	            }
	        });
    }

	/**
	 * Returns whether this page's controls currently all contain valid 
	 * values.
	 *
	 * @return <code>true</code> if all controls are valid, and
	 *   <code>false</code> if at least one is invalid
	 */
    protected boolean validatePage() {
    	InstanceManager manager = InstanceManager.getInstance();
    	String name = projectNameField.getText();
    	String errorMessage = null;
    	boolean retVal = false;
    	
    	if (name != null) {
    		name = name.trim();
    		if (!name.equals("")) { //$NON-NLS-1$
	        	if (manager.getInstancesByname(name).size() == 0) {
	        		retVal = true;
	        	} else {
	        		errorMessage =
	        			DeviceWizardResources.TML_Emulator_Wizard_Project_Description_Duplicated_Error;
	        	}
	        }
     	}
    	
    	this.setErrorMessage(errorMessage);
        return retVal;
    }
    
    /* (non-Javadoc)
     * 
     * Method declared on IDialogPage.
     */
    public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NULL);
        composite.setFont(parent.getFont());

        initializeDialogUnits(parent);

        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        createPropertyGroup(composite);
		
        setPageComplete(validatePage());
        // Show description on opening
        setErrorMessage(null);
        setMessage(null);
        setControl(composite);
    }
    
    /**
     * Retrieves the project name.
     * 
     * @return The project name.
     * @see org.eclipse.tml.framework.device.wizard.model.IWizardProjectPage#getProjectName()
     */
    public String getProjectName() {
        if (projectNameField == null) {
			return initialProjectName;
		}
        projectName = projectNameField.getText().trim();	
		return projectName;
    }
    
    /*
     * see @DialogPage.setVisible(boolean)
     */
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
			projectNameField.setFocus();
		}
    }
}

