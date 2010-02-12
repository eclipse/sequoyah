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

	private static String COMBO_SEPARATOR = "-";

	// Images
	private static Image warningImage = new Image(Display.getDefault(),
			PlatformUI.getWorkbench().getSharedImages().getImage(
					ISharedImages.IMG_OBJS_WARN_TSK).getImageData().scaledTo(
					16, 16));

	private static Image okImage = new Image(Display.getDefault(),
			LocalizationToolsPlugin.imageDescriptorFromPlugin(
					LocalizationToolsPlugin.PLUGIN_ID, "icons/obj16_ok.png")
					.getImageData());

	static {
		availableLanguages.put("AFRIKAANS", AFRIKAANS);
		availableLanguages.put("ALBANIAN", ALBANIAN);
		availableLanguages.put("AMHARIC", AMHARIC);
		availableLanguages.put("ARABIC", ARABIC);
		availableLanguages.put("ARMENIAN", ARMENIAN);
		availableLanguages.put("AZERBAIJANI", AZERBAIJANI);
		availableLanguages.put("BASQUE", BASQUE);
		availableLanguages.put("BELARUSIAN", BELARUSIAN);
		availableLanguages.put("BENGALI", BENGALI);
		availableLanguages.put("BIHARI", BIHARI);
		availableLanguages.put("BULGARIAN", BULGARIAN);
		availableLanguages.put("BURMESE", BURMESE);
		availableLanguages.put("CATALAN", CATALAN);
		availableLanguages.put("CHEROKEE", CHEROKEE);
		availableLanguages.put("CHINESE", CHINESE);
		availableLanguages.put("CHINESE_SIMPLIFIED", CHINESE_SIMPLIFIED);
		availableLanguages.put("CHINESE_TRADITIONAL", CHINESE_TRADITIONAL);
		availableLanguages.put("CROATIAN", CROATIAN);
		availableLanguages.put("CZECH", CZECH);
		availableLanguages.put("DANISH", DANISH);
		availableLanguages.put("DHIVEHI", DHIVEHI);
		availableLanguages.put("DUTCH", DUTCH);
		availableLanguages.put("ENGLISH", ENGLISH);
		availableLanguages.put("ESPERANTO", ESPERANTO);
		availableLanguages.put("ESTONIAN", ESTONIAN);
		availableLanguages.put("FILIPINO", FILIPINO);
		availableLanguages.put("FINNISH", FINNISH);
		availableLanguages.put("FRENCH", FRENCH);
		availableLanguages.put("GALACIAN", GALACIAN);
		availableLanguages.put("GEORGIAN", GEORGIAN);
		availableLanguages.put("GERMAN", GERMAN);
		availableLanguages.put("GREEK", GREEK);
		availableLanguages.put("GUARANI", GUARANI);
		availableLanguages.put("GUJARATI", GUJARATI);
		availableLanguages.put("HEBREW", HEBREW);
		availableLanguages.put("HINDI", HINDI);
		availableLanguages.put("HUNGARIAN", HUNGARIAN);
		availableLanguages.put("ICELANDIC", ICELANDIC);
		availableLanguages.put("INDONESIAN", INDONESIAN);
		availableLanguages.put("INUKTITUT", INUKTITUT);
		availableLanguages.put("IRISH", IRISH);
		availableLanguages.put("ITALIAN", ITALIAN);
		availableLanguages.put("JAPANESE", JAPANESE);
		availableLanguages.put("KANNADA", KANNADA);
		availableLanguages.put("KAZAKH", KAZAKH);
		availableLanguages.put("KHMER", KHMER);
		availableLanguages.put("KOREAN", KOREAN);
		availableLanguages.put("KURDISH", KURDISH);
		availableLanguages.put("KYRGYZ", KYRGYZ);
		availableLanguages.put("LAOTHIAN", LAOTHIAN);
		availableLanguages.put("LATVIAN", LATVIAN);
		availableLanguages.put("LITHUANIAN", LITHUANIAN);
		availableLanguages.put("MACEDONIAN", MACEDONIAN);
		availableLanguages.put("MALAY", MALAY);
		availableLanguages.put("MALAYALAM", MALAYALAM);
		availableLanguages.put("MALTESE", MALTESE);
		availableLanguages.put("MARATHI", MARATHI);
		availableLanguages.put("MONGOLIAN", MONGOLIAN);
		availableLanguages.put("NEPALI", NEPALI);
		availableLanguages.put("NORWEGIAN", NORWEGIAN);
		availableLanguages.put("ORIYA", ORIYA);
		availableLanguages.put("PASHTO", PASHTO);
		availableLanguages.put("PERSIAN", PERSIAN);
		availableLanguages.put("POLISH", POLISH);
		availableLanguages.put("PORTUGUESE", PORTUGUESE);
		availableLanguages.put("PUNJABI", PUNJABI);
		availableLanguages.put("ROMANIAN", ROMANIAN);
		availableLanguages.put("RUSSIAN", RUSSIAN);
		availableLanguages.put("SANSKRIT", SANSKRIT);
		availableLanguages.put("SERBIAN", SERBIAN);
		availableLanguages.put("SINDHI", SINDHI);
		availableLanguages.put("SINHALESE", SINHALESE);
		availableLanguages.put("SLOVAK", SLOVAK);
		availableLanguages.put("SLOVENIAN", SLOVENIAN);
		availableLanguages.put("SPANISH", SPANISH);
		availableLanguages.put("SWAHILI", SWAHILI);
		availableLanguages.put("SWEDISH", SWEDISH);
		availableLanguages.put("TAJIK", TAJIK);
		availableLanguages.put("TAMIL", TAMIL);
		availableLanguages.put("TAGALOG", TAGALOG);
		availableLanguages.put("TELUGU", TELUGU);
		availableLanguages.put("THAI", THAI);
		availableLanguages.put("TIBETAN", TIBETAN);
		availableLanguages.put("TURKISH", TURKISH);
		availableLanguages.put("UKRANIAN", UKRANIAN);
		availableLanguages.put("URDU", URDU);
		availableLanguages.put("UZBEK", UZBEK);
		availableLanguages.put("UIGHUR", UIGHUR);
		availableLanguages.put("VIETNAMESE", VIETNAMESE);
		availableLanguages.put("WELSH", WELSH);
		availableLanguages.put("YIDDISH", YIDDISH);
		availableLanguages.put("VIETNAMESE", VIETNAMESE);
		availableLanguages.put("WELSH", WELSH);
		availableLanguages.put("YIDDISH", YIDDISH);
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
				: "");

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
