/********************************************************************************
 * Copyright (c) 2009-2010 Motorola Inc.
 * All rights reserved. This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Vinicius Hernandes (Motorola)
 * 
 * Contributors:
 * Daniel Pastore (Eldorado) - [289870] Moving and renaming Tml to Sequoyah
 ********************************************************************************/
package org.eclipse.sequoyah.localization.tools.extensions.providers;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.sequoyah.device.common.utilities.BasePlugin;
import org.eclipse.sequoyah.localization.tools.LocalizationToolsPlugin;
import org.eclipse.sequoyah.localization.tools.extensions.classes.ITranslator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 *
 */
public class TranslatorProvider {

	/*
	 * Store all loaded translators
	 */
	private Map<String, ITranslator> translators = null;

	/*
	 * The "localizationSchema" extension point ID
	 */
	private final String EXTENSION_ID = "org.eclipse.sequoyah.localization.tools.translator";

	/*
	 * Fields in "translator" extension point definition
	 */
	private final String EXTENSION_FIELD_NAME = "name";

	private final String EXTENSION_FIELD_CLASS = "class";

	private final String EXTENSION_FIELD_BRANDING_IMG = "brandingImage";

	/*
	 * Singleton instance
	 */
	private static TranslatorProvider instance = null;

	/**
	 * Singleton
	 * 
	 * @return LocalizationSchemaProvider
	 */
	public static TranslatorProvider getInstance() {
		if (instance == null) {
			instance = new TranslatorProvider();
		}
		return instance;
	}

	/**
	 * @return
	 */
	public Map<String, ITranslator> getTranslators() {

		if (translators == null) {

			translators = new HashMap<String, ITranslator>();

			/*
			 * Get extension points defined
			 */
			IConfigurationElement[] config = Platform.getExtensionRegistry()
					.getConfigurationElementsFor(EXTENSION_ID);

			String name;

			/*
			 * Iterate through extension points
			 */
			for (IConfigurationElement configElem : config) {

				try {

					// get information from extension point
					name = configElem.getAttribute(EXTENSION_FIELD_NAME);

					// instantiate the ILocalizationSchema object and set their
					// attributes according to the extension point information
					ITranslator translator = (ITranslator) configElem
							.createExecutableExtension(EXTENSION_FIELD_CLASS);
					translator.setName(name);

					// branding image
					String brandingImgPath = configElem
							.getAttribute(EXTENSION_FIELD_BRANDING_IMG);
					if ((brandingImgPath != null)
							&& (!brandingImgPath.equals(""))) {
						String contributor = configElem.getContributor()
								.getName();
						Image brandingImg = new Image(Display.getDefault(),
								LocalizationToolsPlugin
										.imageDescriptorFromPlugin(contributor,
												brandingImgPath).getImageData());
						translator.setBrandingImg(brandingImg);
					}

					// Add the localization schema to the list
					translators.put(name, translator);

				} catch (Exception e) {
					BasePlugin
							.logError("Error while trying to get translators: "
									+ e.getMessage());
				}
			}
		}

		return translators;
	}

	/**
	 * @param name
	 * @return
	 */
	public ITranslator getTranslatorByName(String name) {

		ITranslator translator = null;
		Map<String, ITranslator> allTranslators = null;

		/*
		 * If the translators have already been loaded, use them Otherwise, call
		 * the method which loads them before using
		 */
		if (translators != null) {
			allTranslators = translators;
		} else {
			allTranslators = getTranslators();
		}

		/*
		 * Check if there are translators defined and search for the one with
		 * the name passed as a parameter
		 */
		if (allTranslators != null) {
			translator = allTranslators.get(name);
		}

		return translator;
	}

}
