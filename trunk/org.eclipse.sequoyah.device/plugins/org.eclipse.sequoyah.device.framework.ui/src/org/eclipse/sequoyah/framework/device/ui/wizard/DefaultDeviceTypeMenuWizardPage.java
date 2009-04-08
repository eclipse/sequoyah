/*******************************************************************************
 * Copyright (c) 2008-2009 MontaVista Software, Inc and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Yu-Fen Kuo (MontaVista) - initial API and implementation
 * Yu-Fen Kuo (MontaVista)  - [236476] - provide a generic device type
 * Fabio Fantato (Instituto Eldorado) - [263188] - Create new examples to support tutorial presentation
 * Fabio Fantato (Instituto Eldorado) - [243494] Change the reference implementation to work on Galileo
 * Daniel Barboza Franco (Eldorado Research Institute) - Bug [271682] - Default Wizard Page accepting invalid names
 *******************************************************************************/
package org.eclipse.tml.framework.device.ui.wizard;

import java.util.regex.Pattern;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.tml.framework.device.factory.DeviceTypeRegistry;
import org.eclipse.tml.framework.device.internal.model.MobileDeviceType;
import org.eclipse.tml.framework.device.manager.InstanceManager;
import org.eclipse.tml.framework.device.model.AbstractMobileInstance;
import org.eclipse.tml.framework.device.model.IDeviceType;
import org.eclipse.tml.framework.device.ui.DeviceUIResources;

/**
 * default first page of the new device instance wizard. Allows user to specify
 * the device instance name and the device type
 * 
 */
public class DefaultDeviceTypeMenuWizardPage extends WizardPage {


	private static final String PROPERTY_ICON = "icon"; //$NON-NLS-1$

private Text nameText;
private MobileDeviceType currentDeviceType;
private TreeViewer deviceTypesTreeViewer;


protected DefaultDeviceTypeMenuWizardPage(String pageName, String title,
		ImageDescriptor titleImage) {
	super(pageName, title, titleImage);
}

protected DefaultDeviceTypeMenuWizardPage(String pageName,String currentDeviceTypeId) {
	super(pageName);
	setTitle(DeviceUIResources.TML_Default_Device_Type_Wizard_Page_title); //$NON-NLS-1$

	setMessage(DeviceUIResources.TML_Default_Device_Type_Wizard_Page_message); //$NON-NLS-1$

	for (Object device:DeviceTypeRegistry.getInstance().getDeviceTypes().toArray()) {
		if (((IDeviceType)device).getId().equals(currentDeviceTypeId)) {
			currentDeviceType = ((MobileDeviceType)device);
		}
	};
}


public void createControl(Composite parent) {
	Composite container = new Composite(parent, SWT.NONE);
	container.setLayout(new GridLayout(2, false));
	container.setLayoutData(new GridData(GridData.FILL_BOTH));
	container.setFont(parent.getFont());

	// new name label
	Label label = new Label(container, SWT.NONE);
	label.setText(DeviceUIResources.TML_Default_Device_Type_Wizard_Page_name); //$NON-NLS-1$
	label.setFont(container.getFont());

	nameText = new Text(container, SWT.BORDER);
	GridData gd = new GridData(GridData.FILL_HORIZONTAL);
	nameText.setLayoutData(gd);
	nameText.setFont(container.getFont());

	nameText.addModifyListener(new ModifyListener() {

		public void modifyText(ModifyEvent e) {
			InstanceManager manager = InstanceManager.getInstance();
			String name = nameText.getText();
			String errorMessage = null;

			if (name != null) {
				//name = name.trim();
				if (!name.equals("")) { //$NON-NLS-1$
					if (! (manager.getInstancesByname(name).size() == 0)) {
						errorMessage = DeviceUIResources.TML_Emulator_Wizard_Project_Description_Duplicated_Error;
					}
					else if (!AbstractMobileInstance.validName(name)){
						errorMessage = DeviceUIResources.TML_Instance_Name_Invalid_Error;
					}
				}
			}

			setErrorMessage(errorMessage);
			getWizard().getContainer().updateButtons();

		}

	});

	createDeviceTypesArea(container);

	setControl(container);
}

public boolean isPageComplete() {
	if (getErrorMessage() != null)
		return false;
	if (nameText.getText().trim() != "" && currentDeviceType != null) { //$NON-NLS-1$
		return true;
	}

	return false;
}

private void createDeviceTypesArea(Composite parent) {

	Label label = new Label(parent, SWT.NONE);
	label.setText(DeviceUIResources.TML_Default_Device_Type_Wizard_Page_deviceTypes+" "+currentDeviceType.getLabel()); //$NON-NLS-1$
	label.setFont(parent.getFont());
	GridData gd = new GridData(GridData.FILL_HORIZONTAL);
	gd.horizontalSpan = 1;
	label.setLayoutData(gd);
	IWizard wizard = getWizard();
		if (wizard instanceof NewDeviceMenuWizard) {

			((NewDeviceMenuWizard) wizard).setCurrentDeviceTypeId(currentDeviceType.getId());
		}
}

public String getInstanceName() {
	return nameText.getText().trim();
}

public MobileDeviceType getDeviceType() {
	return currentDeviceType;
}

}
