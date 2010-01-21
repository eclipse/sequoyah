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
 * Matheus Tait Lima (Eldorado) - Adapting to work with automatic translation
 ********************************************************************************/
package org.eclipse.sequoyah.localization.tools.extensions.classes;

import java.util.List;

import org.eclipse.sequoyah.localization.tools.datamodel.TranslationResult;

public abstract class ITranslator {

	/*
	 * The name of the translator
	 */
	private String name;

	
	/**
	 * @param word
	 * @param fromLanguage
	 * @param toLanguage
	 * @return
	 */
	public abstract TranslationResult translate(String word, String fromLanguage,
			String toLanguage) throws Exception;
	
	
	public abstract List<TranslationResult> translateAll(List<String> words, String fromLanguage,
			String toLanguage) throws Exception;
	
	/**
	 * Get the name of the translator
	 * 
	 * @return the name of the translator
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the translator
	 * 
	 * @param name the name of the translator
	 */
	public void setName(String name) {
		this.name = name;
	}
	
}
