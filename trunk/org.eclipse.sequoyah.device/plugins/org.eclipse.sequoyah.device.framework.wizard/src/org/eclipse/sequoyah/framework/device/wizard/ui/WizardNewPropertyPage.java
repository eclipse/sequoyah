/********************************************************************************
 * Copyright (c) 2007 Motorola Inc. All rights reserved.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributor:
 * Fabio Fantato (Motorola)
 *
 * Contributors:
 * Fabio Fantato (Motorola) - bug#221733 - code revisited
 ********************************************************************************/

package org.eclipse.tml.framework.device.wizard.ui;

import java.util.Properties;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.tml.common.utilities.IPropertyConstants;
import org.eclipse.tml.framework.device.wizard.model.IWizardPropertyPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.ide.IIDEHelpContextIds;
import org.eclipse.ui.internal.ide.dialogs.ProjectContentsLocationArea.IErrorMessageReporter;


public class WizardNewPropertyPage extends WizardPage implements IWizardPropertyPage {


	public Properties getProperties(){
		Properties properties = new Properties();
		properties.setProperty(IPropertyConstants.HOST, getHost());
		properties.setProperty(IPropertyConstants.DISPLAY, getDisplay());
		properties.setProperty(IPropertyConstants.PORT, getPort());
		return properties;
	}
	
	
    private Listener hostModifyListener = new Listener() {
        public void handleEvent(Event e) {
        	//setHostForSelection();
            boolean valid = validatePage();
            setPageComplete(valid);
                
        }
    };
    
    private Listener displayModifyListener = new Listener() {
        public void handleEvent(Event e) {
        	//setDisplayForSelection();
            boolean valid = validatePage();
            setPageComplete(valid);
                
        }
    };
    
    private Listener portModifyListener = new Listener() {
        public void handleEvent(Event e) {
        	//setPortForSelection();
            boolean valid = validatePage();
            setPageComplete(valid);
                
        }
    };

    private String initialHost;
	private String initialDisplay;
	private String initialPort;
    
	private String host;
	private String display;
	private String port;

    // widgets
    Text hostField;
    Text displayField;
    Text portField;
	
    // constants
    private static final int SIZING_HOST_FIELD_WIDTH = 250;
    private static final int SIZING_DISPLAY_FIELD_WIDTH = 50;
    private static final int SIZING_PORT_FIELD_WIDTH = 20;
    
    /**
     * Creates a new project creation wizard page.
     *
     * @param pageName the name of this page
     */
    public WizardNewPropertyPage(String pageName) {
        super(pageName);
        setPageComplete(false);
    }

    /** (non-Javadoc)
     * Method declared on IDialogPage.
     */
    public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NULL);
        composite.setFont(parent.getFont());

        initializeDialogUnits(parent);

        PlatformUI.getWorkbench().getHelpSystem().setHelp(composite,
                IIDEHelpContextIds.NEW_PROJECT_WIZARD_PAGE);

        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        createPropertyGroup(composite);
//        host = new ProjectContentsLocationArea(getErrorReporter(), composite);
//        if(initialProjectFieldValue != null) {
//			locationArea.updateProjectName(initialProjectFieldValue);
//		}

		// Scale the button based on the rest of the dialog
		//setButtonLayoutData(locationArea.getBrowseButton());
		
        setPageComplete(validatePage());
        // Show description on opening
        setErrorMessage(null);
        setMessage(null);
        setControl(composite);
    }
    
    /**
	 * Get an error reporter for the receiver.
	 * @return IErrorMessageReporter
	 */
	private IErrorMessageReporter getErrorReporter() {
		return new IErrorMessageReporter(){
			/* (non-Javadoc)
			 * @see org.eclipse.ui.internal.ide.dialogs.ProjectContentsLocationArea.IErrorMessageReporter#reportError(java.lang.String)
			 */
			public void reportError(String errorMessage) {
				setErrorMessage(errorMessage);
				boolean valid = errorMessage == null;
				if(valid) {
					valid = validatePage();
				}
				
				setPageComplete(valid);
			}
		};
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
        Label hostLabel = new Label(propertyGroup, SWT.NONE);
        hostLabel.setText("Host");
        hostLabel.setFont(parent.getFont());

        hostField = new Text(propertyGroup, SWT.BORDER);
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.widthHint = SIZING_HOST_FIELD_WIDTH;
        hostField.setLayoutData(data);
        hostField.setFont(parent.getFont());
        
        Label displayLabel = new Label(propertyGroup, SWT.NONE);
        displayLabel.setText("Display");
        displayLabel.setFont(parent.getFont());
        
        displayField = new Text(propertyGroup, SWT.BORDER);
        data = new GridData(GridData.FILL_HORIZONTAL);
        data.widthHint = SIZING_DISPLAY_FIELD_WIDTH;
        displayField.setLayoutData(data);
        displayField.setFont(parent.getFont());
    
        Label portLabel = new Label(propertyGroup, SWT.NONE);
        portLabel.setText("Port");
        portLabel.setFont(parent.getFont());
        
        portField = new Text(propertyGroup, SWT.BORDER);
        data = new GridData(GridData.FILL_HORIZONTAL);
        data.widthHint = SIZING_PORT_FIELD_WIDTH;
        portField.setLayoutData(data);
        portField.setFont(parent.getFont());
        

        // Set the initial value first before listener
        // to avoid handling an event during the creation.
        if (initialHost != null) {
			hostField.setText(initialHost);
		}
        hostField.addListener(SWT.Modify, hostModifyListener);
        
        if (initialDisplay != null) {
			displayField.setText(initialDisplay);
		}
        displayField.addListener(SWT.Modify, displayModifyListener);
        
        if (initialPort != null) {
			portField.setText(initialPort);
		}
        portField.addListener(SWT.Modify, portModifyListener);
    }


       
    public String getHost() {
        if (hostField == null) {
			return initialHost;
		}
        host = hostField.getText();	
		return host;
    }

    public void setInitialHost(String host) {
        if (host == null) {
			initialHost = null;
		} else {
            initialHost = host.trim();
            if(hostField.getText() != null) {
				hostField.setText(host.trim());
			}
        }
    }
   
    void setHostForSelection() {
    	displayField.setText(getDisplay());
    }

    public String getDisplay() {
        if (displayField == null) {
			return initialDisplay;
		}
        display = displayField.getText();	
		return display;
    }

    public void setInitialDisplay(String display) {
        if (display == null) {
        	initialDisplay = null;
		} else {
			initialDisplay = display.trim();
            if(displayField.getText() != null) {
            	displayField.setText(display.trim());
			}
        }
    }
    
    void setDisplayForSelection() {
    	displayField.setText(getDisplay());
    }
    
    
    public String getPort() {
        if (portField == null) {
			return initialPort;
		}
        port = portField.getText();	
		return port;
    }

    public void setInitialPort(String port) {
        if (port == null) {
        	initialPort = null;
		} else {
			initialPort = display.trim();
            if(portField.getText() != null) {
            	portField.setText(port.trim());
			}
        }
    }
   
    void setPortForSelection() {
    	portField.setText(getPort());
    }
    
    
  
    /**
     * Returns whether this page's controls currently all contain valid 
     * values.
     *
     * @return <code>true</code> if all controls are valid, and
     *   <code>false</code> if at least one is invalid
     */
    protected boolean validatePage() {
        IWorkspace workspace = IDEWorkbenchPlugin.getPluginWorkspace();

//        String projectFieldContents = getProjectNameFieldValue();
//        if (projectFieldContents.equals("")) { //$NON-NLS-1$
//            setErrorMessage(null);
//            setMessage(IDEWorkbenchMessages.WizardNewProjectCreationPage_projectNameEmpty);
//            return false;
//        }
//
//        IStatus nameStatus = workspace.validateName(projectFieldContents,
//                IResource.PROJECT);
//        if (!nameStatus.isOK()) {
//            setErrorMessage(nameStatus.getMessage());
//            return false;
//        }
//
//        IProject handle = getProjectHandle();
//        if (handle.exists()) {
//            setErrorMessage(IDEWorkbenchMessages.WizardNewProjectCreationPage_projectExistsMessage);
//            return false;
//        }
//        
//        /*
//         * If not using the default value validate the location.
//         */
//        if (!locationArea.isDefault()) {
//        	String validLocationMessage = locationArea.checkValidLocation();
//        	if (validLocationMessage != null) { //there is no destination location given
//        		setErrorMessage(validLocationMessage);
//        		return false;
//        	}
//        }
//
//        setErrorMessage(null);
//        setMessage(null);
        return true;
    }

    /*
     * see @DialogPage.setVisible(boolean)
     */
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
			hostField.setFocus();
		}
    }

    
    public boolean useDefaults() {
    	return (hostField.getText().equals(IPropertyConstants.DEFAULT_HOST))&(portField.getText().equals(IPropertyConstants.DEFAULT_PORT))&(displayField.getText().equals(IPropertyConstants.DEFAULT_DISPLAY));
    }

}
