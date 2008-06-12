/********************************************************************************
 * Copyright (c) 2008 Motorola Inc and others.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Otávio Ferranti (Eldorado Research Institute) - bug#221733 - Adding data persistence
 * 
 * Contributors:
 * name (company) - description.
 ********************************************************************************/

package org.eclipse.tml.framework.device.ui.editors;

import java.util.Enumeration;
import java.util.Properties;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import org.eclipse.ui.dialogs.PropertyPage;

import org.eclipse.tml.framework.device.internal.model.MobileInstance;
import org.eclipse.tml.framework.device.manager.DeviceManager;
import org.eclipse.tml.framework.device.manager.InstanceManager;
import org.eclipse.tml.framework.device.factory.InstanceRegistry;
import org.eclipse.tml.framework.device.ui.DeviceUIResources;

/**
 * Basic device instance property editor
 * @author Otávio Ferranti
 */
public class InstancePropertyEditor extends PropertyPage {

	private static final String LABEL_INSTANCE_NAME = "Instance name: ";
	private static final String LABEL_DEVICE_NAME = "Device Name: ";
	private static final String LABEL_DEVICE_CLASS = "Device class: ";
	private static final String LABEL_PROPERTIES = "Properties: ";
	private static final String COLUMN_NAME_KEY = "Key";
	private static final String COLUMN_NAME_VALUE = "Value";

	private Table table;
	private MobileInstance instance;
	private Text textInstanceName;
	private String initialInstanceName;
	
	/**
	 * Constructor - Creates a device instance property editor
	 */
	public InstancePropertyEditor() {
		super();
	}

	/**
	 * Listener handler.
	 */
	private void onMouseDoubleClick() {
		String key = table.getSelection()[0].getText(0);
		String value = table.getSelection()[0].getText(1);
		
        InputDialog dialog = new InputDialog(this.getShell(),
        		key + " Property Value",
        		"Enter a new value for " + key,
        		value,
        		null);
        if (dialog.open() == Window.OK) {
        	table.getSelection()[0].setText(1, dialog.getValue().trim());
        }
	}
	
    /* (non-Javadoc)
     * Called to verify if this instance name is duplicated.
     * @param instanceName
     * @return
     */
    private boolean validateName(String instanceName) {
    	InstanceManager manager = InstanceManager.getInstance();
    	String errorMessage = null;
    	boolean retVal = false;
    	
    	if (instanceName != null) {
    		instanceName = instanceName.trim();
    		if (!instanceName.equals("")) {
	        	if (manager.getInstancesByname(instanceName).size() == 0 ||
	        		instanceName.equals(initialInstanceName)) {
	        		retVal = true;
	        	} else {
	        		errorMessage = DeviceUIResources.TML_Instance_Name_Duplicated_Error;
	        	}
	        }
     	}
    	this.setErrorMessage(errorMessage);
        return retVal;
    }
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	public Control createContents(Composite parent) {

		String[] columnNames = {InstancePropertyEditor.COLUMN_NAME_KEY,
								InstancePropertyEditor.COLUMN_NAME_VALUE};

        Font font = parent.getFont();
		
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		composite.setLayout(layout);
        composite.setFont(font);

        instance = (MobileInstance) getElement().getAdapter(MobileInstance.class);

        Label label = new Label(composite, SWT.NULL);
        label.setText(InstancePropertyEditor.LABEL_INSTANCE_NAME);
        textInstanceName = new Text(composite, SWT.BORDER);
        initialInstanceName = instance.getName().trim();
        textInstanceName.setText(initialInstanceName);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        textInstanceName.setLayoutData(gridData);
        textInstanceName.addListener(SWT.Modify, new Listener() {
	            public void handleEvent(Event e) {
	                boolean valid = validateName(textInstanceName.getText());
	                setValid(valid);
	            }
        	});

		label = new Label(composite, SWT.NULL );
        label.setText(InstancePropertyEditor.LABEL_DEVICE_NAME);
        label = new Label(composite, SWT.NULL );
        label.setText(DeviceManager.getInstance().getDevice(instance).getName());
        
		label = new Label(composite, SWT.NULL );
        label.setText(InstancePropertyEditor.LABEL_DEVICE_CLASS);
		label = new Label(composite, SWT.NULL );
        label.setText(instance.getDevice());

        label = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL );
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
        label.setLayoutData(gridData);
        
        label = new Label(composite, SWT.NULL);
        label.setText(InstancePropertyEditor.LABEL_PROPERTIES);
        
		this.table = new Table(composite, SWT.FULL_SELECTION);
	
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 2;
		gridData.grabExcessVerticalSpace = true;
		
		table.setLayoutData(gridData);

	    new TableColumn(table, SWT.CENTER).setText(columnNames[0]);
	    new TableColumn(table, SWT.CENTER).setText(columnNames[1]);
	    
		Properties properties = instance.getProperties();
		
		for (Enumeration e = properties.keys();
		 		e.hasMoreElements() ;) {
	        String propertyKey = (String) e.nextElement();
	        String propertyVal = properties.getProperty(propertyKey);

	        TableItem item = new TableItem (table, SWT.NONE);
	        item.setText(new String[] {propertyKey, propertyVal});
		}

		int columns = table.getColumnCount();
	    for (int i = 0; i < columns; i++) {
	        table.getColumn(i).pack();
	    }

	    table.setHeaderVisible(true);
	    table.setLinesVisible(true);

	    table.addMouseListener(new MouseAdapter() {
	    	public void mouseDoubleClick(MouseEvent event) {
	    		onMouseDoubleClick();
	        }
		});
	    
	    noDefaultAndApplyButton();
		return composite;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	public boolean performOk() {
		
		TableItem items[] = table.getItems();
		for (int i = 0; i < items.length; i ++) {
			String key = items[i].getText(0);
			String value = items[i].getText(1).trim();
			instance.getProperties().setProperty(key, value);
		}
		instance.setName(textInstanceName.getText().trim());
		InstanceRegistry.getInstance().setDirty(true);
		return true;
	}
}
