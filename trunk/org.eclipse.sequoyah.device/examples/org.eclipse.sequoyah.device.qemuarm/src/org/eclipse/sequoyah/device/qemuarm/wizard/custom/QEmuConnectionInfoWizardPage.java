/*******************************************************************************
 * Copyright (c) 2008 MontaVista Software, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Yu-Fen Kuo (MontaVista) - initial API and implementation
 *******************************************************************************/
package org.eclipse.tml.device.qemuarm.wizard.custom;

import java.util.Properties;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.tml.device.qemuarm.QEmuARMPlugin;
import org.eclipse.tml.framework.device.ui.wizard.IInstanceProperties;

/*
 * wizard page for the new QEmu device type device instance wizard. It allows
 * users to specify the host, port and display info.
 */
public class QEmuConnectionInfoWizardPage extends WizardPage implements
		IInstanceProperties {

	private Text hostText;
	private Text portText;
	private Text displayText;

	protected QEmuConnectionInfoWizardPage(String pageName) {
		super(pageName);
		
	}

	public QEmuConnectionInfoWizardPage() {
		super("$qemu_basic_info$"); //$NON-NLS-1$
		setTitle(QEmuARMPlugin
				.getResourceString("QEmuConnectionInfoWizardPage.title")); //$NON-NLS-1$
		setMessage(QEmuARMPlugin
				.getResourceString("QEmuConnectionInfoWizardPage.message")); //$NON-NLS-1$
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		container.setFont(parent.getFont());

		Label label = new Label(container, SWT.NONE);
		label.setText(QEmuARMPlugin
				.getResourceString("QEmuConnectionInfoWizardPage.Host")); //$NON-NLS-1$
		label.setFont(container.getFont());

		hostText = new Text(container, SWT.BORDER);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		hostText.setLayoutData(gd);
		hostText.setFont(container.getFont());

		hostText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				getWizard().getContainer().updateButtons();

			}

		});

		label = new Label(container, SWT.NONE);
		label.setText(QEmuARMPlugin
				.getResourceString("QEmuConnectionInfoWizardPage.Port")); //$NON-NLS-1$
		label.setFont(container.getFont());

		portText = new Text(container, SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		portText.setLayoutData(gd);
		portText.setFont(container.getFont());

		portText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				getWizard().getContainer().updateButtons();

			}

		});

		label = new Label(container, SWT.NONE);
		label.setText(QEmuARMPlugin
				.getResourceString("QEmuConnectionInfoWizardPage.Display")); //$NON-NLS-1$
		label.setFont(container.getFont());

		displayText = new Text(container, SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		displayText.setLayoutData(gd);
		displayText.setFont(container.getFont());

		displayText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				getWizard().getContainer().updateButtons();

			}

		});
		initializeDefaultValues();
		setControl(container);

	}

	private void initializeDefaultValues() {
		hostText.setText(IPropertyConstants.DEFAULT_HOST);
		portText.setText(IPropertyConstants.DEFAULT_PORT);
		displayText.setText(IPropertyConstants.DEFAULT_DISPLAY);
	}

	@Override
	public boolean isPageComplete() {
		if (hostText == null || hostText.isDisposed())
			return false;
		if (hostText.getText().trim() != "" //$NON-NLS-1$
				&& displayText.getText().trim() != "" //$NON-NLS-1$
				&& portText.getText().trim() != "") //$NON-NLS-1$
			return true;
		return false;
	}

	public void dispose() {
		if (getControl() != null)
			setControl(null);
		super.dispose();
	}

	public Properties getProperties() {
		Properties properties = new Properties();
		properties.put(IPropertyConstants.HOST, hostText.getText());
		properties.put(IPropertyConstants.PORT, portText.getText());
		properties.put(IPropertyConstants.DISPLAY, displayText.getText());
		return properties;
	}
}