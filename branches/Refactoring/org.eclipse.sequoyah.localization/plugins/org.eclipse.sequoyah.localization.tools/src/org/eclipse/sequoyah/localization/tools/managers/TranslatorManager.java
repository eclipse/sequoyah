/********************************************************************************
 * Copyright (c) 2009 Motorola Inc.
 * All rights reserved. This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Vinicius Hernandes (Motorola)
 * 
 * Contributors:
 * name (company) - description.
 ********************************************************************************/
package org.eclipse.tml.localization.tools.managers;

import java.util.List;

import org.eclipse.tml.localization.tools.datamodel.TranslationResult;
import org.eclipse.tml.localization.tools.extensions.classes.ITranslator;
import org.eclipse.tml.localization.tools.extensions.providers.TranslatorProvider;

/**
 * 
 */
public class TranslatorManager {

	private List<ITranslator> translators;

	private PreferencesManager preferencesManager;

	private TranslatorProvider translatorProvider;

	/**
	 * @param string
	 * @param fromLanguage
	 * @param toLanguage
	 * @return
	 */
	public TranslationResult translate(String string, String fromLanguage,
			String toLanguage) {
		return null;
	}

	/**
	 * @param strings
	 * @param fromLanguage
	 * @param toLanguage
	 * @return
	 */
	public List<TranslationResult> translateAll(List<String> strings,
			String fromLanguage, String toLanguage) {
		return null;
	}

	/**
	 * @param translator
	 * @param string
	 * @param fromLanguage
	 * @param toLanguage
	 * @return
	 */
	public TranslationResult translate(ITranslator translator, String string,
			String fromLanguage, String toLanguage) {
		return null;
	}

	/**
	 * @param translator
	 * @param strings
	 * @param fromLanguage
	 * @param toLanguage
	 * @return
	 */
	public List<TranslationResult> translateAll(ITranslator translator,
			List<String> strings, String fromLanguage, String toLanguage) {
		return null;
	}

	/**
	 * @return
	 */
	public List<ITranslator> getTranslators() {
		return translators;
	}

	/**
	 * @param name
	 * @return
	 */
	public ITranslator getTranslatorByName(String name) {
		return null;
	}

}
