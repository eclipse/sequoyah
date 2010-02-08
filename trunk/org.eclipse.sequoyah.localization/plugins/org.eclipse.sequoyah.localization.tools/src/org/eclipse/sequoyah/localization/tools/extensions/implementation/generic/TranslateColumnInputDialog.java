/********************************************************************************
 * Copyright (c) 2009-2010 Motorola Inc.
 * All rights reserved. All rights reserved. This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Matheus Tait Lima (Eldorado)
 * Vinicius Rigoni (Eldorado)
 * Marcel Gorri (Eldorado)
 * 
 * Contributors:
 * Daniel Pastore (Eldorado) - [289870] Moving and renaming Tml to Sequoyah 
 ********************************************************************************/
package org.eclipse.sequoyah.localization.tools.extensions.implementation.generic;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.sequoyah.localization.tools.extensions.classes.ILocalizationSchema;
import org.eclipse.sequoyah.localization.tools.i18n.Messages;
import org.eclipse.sequoyah.localization.tools.managers.LocalizationManager;
import org.eclipse.sequoyah.localization.tools.managers.PreferencesManager;
import org.eclipse.sequoyah.localization.tools.managers.TranslatorManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Asks for: - the name of a new language, and - source language, and - target
 * language
 * 
 * These data will be used for translation purposes
 */
public class TranslateColumnInputDialog extends InputDialog {

	private IProject project = null;

	private final String initialValue;

	// Translators
	private String translator;

	private Combo translatorsCombo = null;

	private Label translatorBrandingImage = null;

	// "From" information
	private Combo fromCombo = null;

	private String fromLanguage;

	private Text columnName = null;

	private String selectedColumn = null;

	// "To" information
	private String toLanguage;

	private Combo toCombo = null;

	private Button automaticallyAddLangID;

	/**
	 * The constructor
	 */
	public TranslateColumnInputDialog(Shell parentShell, IProject project,
			String selectedColumn, String dialogTitle, String dialogMessage,
			String initialValue, IInputValidator validator) {
		super(parentShell, dialogTitle, dialogMessage, initialValue, validator);
		this.project = project;
		this.initialValue = initialValue;
		this.selectedColumn = selectedColumn;
	}

	/**
	 * Get the original language
	 * 
	 * @return the original language of the word(s) being translated
	 */
	public String getFromLanguage() {
		return LanguagesUtil.getLanguageID(fromLanguage);
	}

	/**
	 * 
	 * @return
	 */
	public String getToLanguage() {
		return LanguagesUtil.getLanguageID(toLanguage);
	}

	/**
	 * Get the selected translator
	 * 
	 * @return the selected translator name
	 */
	public String getTranslator() {
		return translator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.InputDialog#createDialogArea(org.eclipse.swt
	 * .widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {

		ILocalizationSchema schema = LocalizationManager.getInstance()
				.getLocalizationSchema(project);

		Composite languagesComposite = (Composite) super
				.createDialogArea(parent);
		this.columnName = this.getText();

		ComboListener listener = new ComboListener();

		Group translationDetailsGroup = new Group(languagesComposite,
				SWT.SHADOW_ETCHED_OUT);
		translationDetailsGroup
				.setText(Messages.TranslationDialog_LanguageAreaLabel);
		translationDetailsGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				true, true));
		translationDetailsGroup.setLayout(new GridLayout(3, false));

		/*
		 * Translators
		 */
		Label translatorText = new Label(translationDetailsGroup, SWT.NONE);
		translatorText.setText(Messages.Translator_Text);

		translatorsCombo = LanguagesUtil
				.createTranslatorsCombo((Composite) translationDetailsGroup);
		translatorsCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 2, 1));
		translatorsCombo.addSelectionListener(listener);

		/*
		 * From Area
		 */
		Label fromText = new Label(translationDetailsGroup, SWT.NONE);
		fromText.setText(Messages.TranslationDialog_FromLanguage);

		String selectedLanguage = schema
				.getISO639LangFromID(this.selectedColumn);
		String defaultLanguage = PreferencesManager.getInstance()
				.getProjectPreferencesManager(this.project)
				.getDefaultLanguage();

		fromCombo = LanguagesUtil.createLanguagesCombo(translationDetailsGroup,
				selectedLanguage, defaultLanguage);
		fromCombo
				.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fromCombo.addSelectionListener(listener);

		LanguagesUtil.createImageStatus(translationDetailsGroup,
				((selectedLanguage != null) ? selectedLanguage
						: defaultLanguage));

		/*
		 * To Area
		 */
		Label toText = new Label(translationDetailsGroup, SWT.NONE);
		toText.setText(Messages.TranslationDialog_ToLanguage);

		toCombo = LanguagesUtil.createLanguagesCombo(translationDetailsGroup,
				null, null);
		toCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		toCombo.addSelectionListener(listener);

		LanguagesUtil.createImageStatus(translationDetailsGroup, null);

		Label empty = new Label(translationDetailsGroup, SWT.NONE);

		// Automatic language ID
		automaticallyAddLangID = new Button(translationDetailsGroup, SWT.CHECK);
		automaticallyAddLangID
				.setText("Automatically add language ID to column name");
		automaticallyAddLangID.setSelection(true);

		/*
		 * Translator Branding Area
		 */
		Composite brandingComposite = new Composite(languagesComposite,
				SWT.RIGHT);
		brandingComposite.setLayout(new GridLayout(1, false));
		brandingComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false,
				false));

		translatorBrandingImage = new Label(brandingComposite, SWT.NONE);
		translatorBrandingImage.setLayoutData(new GridData(SWT.FILL, SWT.TOP,
				true, true));

		setInitialValues();

		return languagesComposite;
	}

	/**
	 * Populate attributes with their initial values
	 */
	private void setInitialValues() {
		fromLanguage = fromCombo.getText();
		toLanguage = toCombo.getText();
		translator = translatorsCombo.getText();
		TranslatorManager.getInstance().setTranslatorBranding(translator,
				translatorBrandingImage);
	}

	/**
	 * Selection Listener that will be responsible for monitoring changes in
	 * combo selections (from language, to language and translator)
	 */
	class ComboListener implements SelectionListener {
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}

		public void widgetSelected(SelectionEvent e) {
			if (e.getSource() == fromCombo) {
				fromLanguage = fromCombo.getText();
			} else if (e.getSource() == toCombo) {
				toLanguage = toCombo.getText();
				if (automaticallyAddLangID.getSelection()) {
					columnName.setText(getColumnNameWithLangID(LanguagesUtil
							.getLanguageID(toLanguage)));
				}
			} else if (e.getSource() == translatorsCombo) {
				translator = translatorsCombo.getText();
				TranslatorManager.getInstance().setTranslatorBranding(
						translator, translatorBrandingImage);
			}
		}
	}

	/**
	 * Create a column name with the language ID attached
	 * 
	 * @param langID
	 *            the language ID
	 * @return the column name with the language ID attached
	 */
	private String getColumnNameWithLangID(String langID) {
		String columnName = null;

		columnName = this.initialValue + "-" + langID;

		return columnName;
	}
}
