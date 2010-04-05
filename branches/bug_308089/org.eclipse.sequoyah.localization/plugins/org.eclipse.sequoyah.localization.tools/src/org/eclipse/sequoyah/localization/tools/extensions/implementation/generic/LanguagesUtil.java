/********************************************************************************
 * Copyright (c) 2009 Motorola Inc.
 * All rights reserved. This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Matheus Tait Lima (Eldorado)
 * Vinicius Hernandes (Eldorado)
 * Marcel Gorri (Eldorado)
 * 
 * Contributors:
 * name (company) - description.
 ********************************************************************************/
package org.eclipse.sequoyah.localization.tools.extensions.implementation.generic;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.sequoyah.localization.tools.LocalizationToolsPlugin;
import org.eclipse.sequoyah.localization.tools.extensions.classes.ILocalizationSchema;
import org.eclipse.sequoyah.localization.tools.extensions.classes.ITranslator;
import org.eclipse.sequoyah.localization.tools.i18n.Messages;
import org.eclipse.sequoyah.localization.tools.managers.PreferencesManager;
import org.eclipse.sequoyah.localization.tools.managers.TranslatorManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * Utility class to work with the available languages and their IDs
 * 
 */
public class LanguagesUtil implements TranslatorConstants {

	private static Map<String, String> availableLanguages = new LinkedHashMap<String, String>();

	private static String COMBO_SEPARATOR = Messages.LanguagesUtil_0;

	// Images
	private static Image warningImage = new Image(Display.getDefault(),
			PlatformUI.getWorkbench().getSharedImages().getImage(
					ISharedImages.IMG_OBJS_WARN_TSK).getImageData().scaledTo(
					16, 16));

	private static Image okImage = new Image(Display.getDefault(),
			LocalizationToolsPlugin.imageDescriptorFromPlugin(
					LocalizationToolsPlugin.PLUGIN_ID, Messages.LanguagesUtil_1)
					.getImageData());

	static {
		availableLanguages.put(Messages.LanguagesUtil_2, AFRIKAANS);
		availableLanguages.put(Messages.LanguagesUtil_3, ALBANIAN);
		availableLanguages.put(Messages.LanguagesUtil_4, AMHARIC);
		availableLanguages.put(Messages.LanguagesUtil_5, ARABIC);
		availableLanguages.put(Messages.LanguagesUtil_6, ARMENIAN);
		availableLanguages.put(Messages.LanguagesUtil_7, AZERBAIJANI);
		availableLanguages.put(Messages.LanguagesUtil_8, BASQUE);
		availableLanguages.put(Messages.LanguagesUtil_9, BELARUSIAN);
		availableLanguages.put(Messages.LanguagesUtil_10, BENGALI);
		availableLanguages.put(Messages.LanguagesUtil_11, BIHARI);
		availableLanguages.put(Messages.LanguagesUtil_12, BULGARIAN);
		availableLanguages.put(Messages.LanguagesUtil_13, BURMESE);
		availableLanguages.put(Messages.LanguagesUtil_14, CATALAN);
		availableLanguages.put(Messages.LanguagesUtil_15, CHEROKEE);
		availableLanguages.put(Messages.LanguagesUtil_16, CHINESE);
		availableLanguages.put(Messages.LanguagesUtil_17, CHINESE_SIMPLIFIED);
		availableLanguages.put(Messages.LanguagesUtil_18, CHINESE_TRADITIONAL);
		availableLanguages.put(Messages.LanguagesUtil_19, CROATIAN);
		availableLanguages.put(Messages.LanguagesUtil_20, CZECH);
		availableLanguages.put(Messages.LanguagesUtil_21, DANISH);
		availableLanguages.put(Messages.LanguagesUtil_22, DHIVEHI);
		availableLanguages.put(Messages.LanguagesUtil_23, DUTCH);
		availableLanguages.put(Messages.LanguagesUtil_24, ENGLISH);
		availableLanguages.put(Messages.LanguagesUtil_25, ESPERANTO);
		availableLanguages.put(Messages.LanguagesUtil_26, ESTONIAN);
		availableLanguages.put(Messages.LanguagesUtil_27, FILIPINO);
		availableLanguages.put(Messages.LanguagesUtil_28, FINNISH);
		availableLanguages.put(Messages.LanguagesUtil_29, FRENCH);
		availableLanguages.put(Messages.LanguagesUtil_30, GALACIAN);
		availableLanguages.put(Messages.LanguagesUtil_31, GEORGIAN);
		availableLanguages.put(Messages.LanguagesUtil_32, GERMAN);
		availableLanguages.put(Messages.LanguagesUtil_33, GREEK);
		availableLanguages.put(Messages.LanguagesUtil_34, GUARANI);
		availableLanguages.put(Messages.LanguagesUtil_35, GUJARATI);
		availableLanguages.put(Messages.LanguagesUtil_36, HEBREW);
		availableLanguages.put(Messages.LanguagesUtil_37, HINDI);
		availableLanguages.put(Messages.LanguagesUtil_38, HUNGARIAN);
		availableLanguages.put(Messages.LanguagesUtil_39, ICELANDIC);
		availableLanguages.put(Messages.LanguagesUtil_40, INDONESIAN);
		availableLanguages.put(Messages.LanguagesUtil_41, INUKTITUT);
		availableLanguages.put(Messages.LanguagesUtil_42, IRISH);
		availableLanguages.put(Messages.LanguagesUtil_43, ITALIAN);
		availableLanguages.put(Messages.LanguagesUtil_44, JAPANESE);
		availableLanguages.put(Messages.LanguagesUtil_45, KANNADA);
		availableLanguages.put(Messages.LanguagesUtil_46, KAZAKH);
		availableLanguages.put(Messages.LanguagesUtil_47, KHMER);
		availableLanguages.put(Messages.LanguagesUtil_48, KOREAN);
		availableLanguages.put(Messages.LanguagesUtil_49, KURDISH);
		availableLanguages.put(Messages.LanguagesUtil_50, KYRGYZ);
		availableLanguages.put(Messages.LanguagesUtil_51, LAOTHIAN);
		availableLanguages.put(Messages.LanguagesUtil_52, LATVIAN);
		availableLanguages.put(Messages.LanguagesUtil_53, LITHUANIAN);
		availableLanguages.put(Messages.LanguagesUtil_54, MACEDONIAN);
		availableLanguages.put(Messages.LanguagesUtil_55, MALAY);
		availableLanguages.put(Messages.LanguagesUtil_56, MALAYALAM);
		availableLanguages.put(Messages.LanguagesUtil_57, MALTESE);
		availableLanguages.put(Messages.LanguagesUtil_58, MARATHI);
		availableLanguages.put(Messages.LanguagesUtil_59, MONGOLIAN);
		availableLanguages.put(Messages.LanguagesUtil_60, NEPALI);
		availableLanguages.put(Messages.LanguagesUtil_61, NORWEGIAN);
		availableLanguages.put(Messages.LanguagesUtil_62, ORIYA);
		availableLanguages.put(Messages.LanguagesUtil_63, PASHTO);
		availableLanguages.put(Messages.LanguagesUtil_64, PERSIAN);
		availableLanguages.put(Messages.LanguagesUtil_65, POLISH);
		availableLanguages.put(Messages.LanguagesUtil_66, PORTUGUESE);
		availableLanguages.put(Messages.LanguagesUtil_67, PUNJABI);
		availableLanguages.put(Messages.LanguagesUtil_68, ROMANIAN);
		availableLanguages.put(Messages.LanguagesUtil_69, RUSSIAN);
		availableLanguages.put(Messages.LanguagesUtil_70, SANSKRIT);
		availableLanguages.put(Messages.LanguagesUtil_71, SERBIAN);
		availableLanguages.put(Messages.LanguagesUtil_72, SINDHI);
		availableLanguages.put(Messages.LanguagesUtil_73, SINHALESE);
		availableLanguages.put(Messages.LanguagesUtil_74, SLOVAK);
		availableLanguages.put(Messages.LanguagesUtil_75, SLOVENIAN);
		availableLanguages.put(Messages.LanguagesUtil_76, SPANISH);
		availableLanguages.put(Messages.LanguagesUtil_77, SWAHILI);
		availableLanguages.put(Messages.LanguagesUtil_78, SWEDISH);
		availableLanguages.put(Messages.LanguagesUtil_79, TAJIK);
		availableLanguages.put(Messages.LanguagesUtil_80, TAMIL);
		availableLanguages.put(Messages.LanguagesUtil_81, TAGALOG);
		availableLanguages.put(Messages.LanguagesUtil_82, TELUGU);
		availableLanguages.put(Messages.LanguagesUtil_83, THAI);
		availableLanguages.put(Messages.LanguagesUtil_84, TIBETAN);
		availableLanguages.put(Messages.LanguagesUtil_85, TURKISH);
		availableLanguages.put(Messages.LanguagesUtil_86, UKRANIAN);
		availableLanguages.put(Messages.LanguagesUtil_87, URDU);
		availableLanguages.put(Messages.LanguagesUtil_88, UZBEK);
		availableLanguages.put(Messages.LanguagesUtil_89, UIGHUR);
		availableLanguages.put(Messages.LanguagesUtil_90, VIETNAMESE);
		availableLanguages.put(Messages.LanguagesUtil_91, WELSH);
		availableLanguages.put(Messages.LanguagesUtil_92, YIDDISH);
		availableLanguages.put(Messages.LanguagesUtil_93, VIETNAMESE);
		availableLanguages.put(Messages.LanguagesUtil_94, WELSH);
		availableLanguages.put(Messages.LanguagesUtil_95, YIDDISH);
	}

	/**
	 * Return a set of the supported languages (strings with the language name
	 * in uppercase)
	 */
	public static List<String> getAvailableLanguages() {
		return new ArrayList<String>(availableLanguages.keySet());
	}

	/**
	 * Get preferred languages for certain localization schema
	 * 
	 * @param locSchema
	 *            localization schema
	 * @return a set of the preferred languages (strings with the language name
	 *         in uppercase)
	 */
	public static List<String> getPreferredLanguages(
			ILocalizationSchema locSchema) {

		List<String> languageNames = new ArrayList<String>();
		List<String> preferedLanguagesIds = locSchema.getPreferedLanguages();

		if (preferedLanguagesIds != null) {
			for (String langID : preferedLanguagesIds) {
				languageNames.add(getLanguageName(langID));
			}
		}

		return languageNames;

	}

	/**
	 * Return the separator used in combobox, which represent a non-valid
	 * selection
	 * 
	 * @return separator used in combobox
	 */
	public static String getComboSeparator() {
		return COMBO_SEPARATOR;
	}

	/**
	 * Given a language name (one of the supported languages) return the
	 * language ID.
	 */
	public static String getLanguageID(String languageName) {
		return (String) availableLanguages.get(languageName);
	}

	/**
	 * @param ID
	 * @return
	 */
	public static String getLanguageName(String ID) {
		String langName = null;
		for (Map.Entry<String, String> entry : availableLanguages.entrySet()) {
			if (entry.getValue().equals(ID)) {
				langName = entry.getKey();
				break;
			}
		}
		return langName;
	}

	public static Combo createTranslatorsCombo(Composite parent) {
		Combo translatorsCombo = null;

		List<ITranslator> translators = TranslatorManager.getInstance()
				.getTranslators();
		ITranslator defaultTranslator = PreferencesManager.getInstance()
				.getDefaultTranslator();
		String defaultTranslatorName = ((defaultTranslator != null) ? defaultTranslator
				.getName()
				: ""); //$NON-NLS-1$

		translatorsCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY
				| SWT.SINGLE);

		String[] translatorNames = new String[translators.size()];
		int defaultTranslatorIndex = 0;
		for (int i = 0; i < translators.size(); i++) {
			translatorNames[i] = translators.get(i).getName();
			if (translatorNames[i].equals(defaultTranslatorName)) {
				defaultTranslatorIndex = i;
			}

		}
		translatorsCombo.setItems(translatorNames);
		translatorsCombo.select(defaultTranslatorIndex);

		return translatorsCombo;

	}

	public static Combo createLanguagesCombo(Composite parent,
			String initialSelection, String defaultSelection,
			ILocalizationSchema locSchema) {
		Combo languagesCombo = null;

		List<String> availableLangs = getAvailableLanguages();
		List<String> preferedLangs = getPreferredLanguages(locSchema);

		List<String> allLangs = new ArrayList<String>();
		allLangs.addAll(preferedLangs);
		allLangs.add(COMBO_SEPARATOR);
		allLangs.addAll(availableLangs);

		languagesCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY
				| SWT.SINGLE);

		if (allLangs.size() > 0) {
			languagesCombo.setItems(allLangs
					.toArray(new String[allLangs.size()]));

			String comboSelection = initialSelection;

			if (comboSelection == null) {
				comboSelection = defaultSelection;
			}

			if (comboSelection != null) {
				String langName = getLanguageName(comboSelection);
				int index = 0;
				for (int i = 0; i < languagesCombo.getItems().length; i++) {
					String item = languagesCombo.getItem(i);
					if (item.equals(langName)) {
						index = i;
						break;
					}
				}
				languagesCombo.select(index);
			} else {
				languagesCombo.select(0);
			}
		}

		return languagesCombo;
	}

	/**
	 * Create an OK or WARNING image (16x16) based on language information If
	 * the language has correctly been identified, an OK image is created
	 * Otherwise, the method creates a WARNING image
	 * 
	 * @param parent
	 *            parent composite
	 * @param lang
	 *            the language, or null if it's not recognized
	 */
	public static Label createImageStatus(Composite parent, String lang) {
		Label image = new Label(parent, SWT.NONE);

		changeImageStatus(image, lang);

		return image;
	}

	/**
	 * Change status image according to language information
	 * 
	 * @param image
	 *            the label that contain the image
	 * @param lang
	 *            the language, or null if it's not recognized
	 */
	public static void changeImageStatus(Label image, String lang) {
		if (lang != null) {
			image.setImage(okImage);
			image.setToolTipText(Messages.TranslationDialog_ImageOKTooltip);
		} else {
			image.setImage(warningImage);
			image.setToolTipText(Messages.TranslationDialog_ImageWARNTooltip);
		}
	}

}
