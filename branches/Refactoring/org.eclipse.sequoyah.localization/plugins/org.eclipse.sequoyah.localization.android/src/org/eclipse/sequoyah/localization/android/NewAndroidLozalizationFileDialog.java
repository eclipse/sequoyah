/********************************************************************************
 * Copyright (c) 2009 Motorola Inc. All rights reserved.
 * All rights reserved. All rights reserved. This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributor:
 * Matheus Tait Lima (Eldorado)
 *
 * Contributors:
 * Marcelo Marzola Bossoni (Eldorado) -  Bug [289146] - Performance and Usability Issues
 ********************************************************************************/

package org.eclipse.tml.localization.android;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class NewAndroidLozalizationFileDialog extends Dialog {

	private static final String DIALOG_TITLE = "New Android Localization File";
	private static final String COUNTRY_LABEL = "Country Code (3 digits):";
	private static final String LANGUAGE_LABEL = "Language:";
	private Text countryCode;
	private Text language;
	private Button bypassProxyButton;

	private ModifyListener listener = new ModifyListener() {
		public void modifyText(ModifyEvent e) {
			validate();
		}
	};

	public NewAndroidLozalizationFileDialog(Shell parent) {
		super(parent);
	}

	protected Point getInitialSize() {
		return new Point(240, 290);
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(DIALOG_TITLE);
	}

	protected Control createDialogArea(Composite parent) {

		Composite external = createDefaultComposite(parent, 1, 17);
		Composite fields = createDefaultComposite(external, 2, 0);

		int width, height, cols;

		cols = 20;

		Label countryLabel = new Label(fields, SWT.RIGHT);
		countryCode = new Text(fields, parent.getStyle() | SWT.BORDER);

		Label languageLabel = new Label(fields, SWT.RIGHT);
		language = new Text(fields, parent.getStyle() | SWT.BORDER);

		GC gc = new GC(countryCode.getDisplay());
		width = gc.getFontMetrics().getAverageCharWidth() * cols;
		height = gc.getFontMetrics().getHeight();

		GridData gridData = new GridData();
		gridData.heightHint = height;
		gridData.widthHint = width;

		countryLabel.setText(COUNTRY_LABEL); //$NON-NLS-1$
		countryCode.setLayoutData(gridData);
		countryCode.setSize(countryCode.computeSize(width, height));

		languageLabel.setText(LANGUAGE_LABEL); //$NON-NLS-1$
		language.setSize(language.computeSize(width, height));
		language.setLayoutData(gridData);

		createCombo(external);

		Composite bypassComposite = new Composite(external, SWT.NULL);
		GridData gdata = new GridData(SWT.FILL, SWT.FILL, true, false);
		bypassComposite.setLayoutData(gdata);
		GridLayout glayout = new GridLayout();

		bypassComposite.setLayout(glayout);

		bypassProxyButton = new Button(bypassComposite, SWT.CHECK);
		Point p = bypassProxyButton.getLocation();
		bypassProxyButton.setLocation(p.x, p.y + 100);

		bypassProxyButton.setText("Create ID manually");

		countryCode.addModifyListener(listener);
		language.addModifyListener(listener);

		return external;

	}

	protected void okPressed() {

		String host = countryCode.getText();
		MessageDialog.openInformation(new Shell(), "Ok pressed", "Yeah baby"
				+ host);
		super.okPressed();
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true).setEnabled(false);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false).setEnabled(true);
	}

	@Override
	protected void cancelPressed() {
		super.cancelPressed();
	}

	/**
	 * @param parent
	 * @param columns
	 * @return
	 */
	private Composite createDefaultComposite(Composite parent, int columns,
			int left) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();

		if (columns > 0) {
			layout.numColumns = columns;
		}

		layout.marginLeft = left + 5;
		layout.marginTop = 5;
		// layout.marginBottom = 15;

		composite.setLayout(layout);

		GridData data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		composite.setLayoutData(data);

		return composite;
	}

	/**
	 * 
	 * Creates the widgets that will be used to manipulate the devices and
	 * configuration at the view
	 * 
	 * @param composite
	 *            The parent composite of the device widgets
	 */
	private void createCombo(Composite composite)

	{

		Composite comboComposite = new Composite(composite, SWT.NULL);

		GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);

		comboComposite.setLayoutData(data);
		GridLayout gridLayout = new GridLayout();

		comboComposite.setLayout(gridLayout);

		Label configListLabel = new Label(comboComposite, SWT.NONE);
		configListLabel.setText("Orientation:");

		Combo orientationCombo = new Combo(comboComposite, SWT.READ_ONLY);
		data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		orientationCombo.setLayoutData(data);

		orientationCombo.add("None");
		orientationCombo.add("Landscape");
		orientationCombo.add("Portrait");
		orientationCombo.select(1);

	}

	private void validate() {
		if (countryCode != null && language != null) {
			NewAndroidLozalizationFileDialog.this.getButton(
					IDialogConstants.OK_ID).setEnabled(
					countryCode.getText().length() > 0
							&& language.getText().length() > 0);
		}
	}

}
