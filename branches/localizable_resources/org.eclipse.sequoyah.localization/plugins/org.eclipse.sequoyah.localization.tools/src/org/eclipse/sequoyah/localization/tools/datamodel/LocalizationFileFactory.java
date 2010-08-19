/********************************************************************************
 * Copyright (c) 2010 Motorola Mobility, Inc.
 * All rights reserved. This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Marcel Augusto Gorri (Eldorado) - Bug [323036] - Add support to other localizable resources
 *  
 ********************************************************************************/
package org.eclipse.sequoyah.localization.tools.datamodel;

/**
 * Factory for creating the different types of LocalizationFile (named: String,
 * Image, Sound, Video). It is also a singleton.
 * 
 */
public class LocalizationFileFactory {

	/*
	 * Private instance of this factory for singleton purposes.
	 */
	private static LocalizationFileFactory localizationFileFactory;

	/**
	 * Default constructor (private since it is a singleton).
	 */
	private LocalizationFileFactory() {
	}

	/**
	 * This method provides a single instance of this factory for whoever needs
	 * to use it.
	 * 
	 * @return unique instance of this factory
	 */
	public static LocalizationFileFactory getInstance() {
		if (localizationFileFactory == null) {
			synchronized (LocalizationFileFactory.class) {
				if (localizationFileFactory == null) {
					localizationFileFactory = new LocalizationFileFactory();
				}
			}
		}
		return localizationFileFactory;
	}

	/**
	 * Method responsible for creating the different types of LocalizationFile
	 * based on the type attribute of the LocalizationFileBean received as
	 * parameter.
	 * 
	 * @param bean
	 *            Bean containing all information necessary for the creation of
	 *            a LocalizationFile.
	 * @return LocalizationFile created if the parameter received is not null.
	 */
	public LocalizationFile createLocalizationFile(LocalizationFileBean bean) {
		LocalizationFile locFile = null;
		if (bean != null) {
			switch (bean.getType()) {
			case ILocalizationFileType.STRING:
				locFile = StringLocalizationFile.create(bean);
			case ILocalizationFileType.IMAGE:
				locFile = ImageLocalizationFile.create(bean);
			case ILocalizationFileType.SOUND:
				locFile = SoundLocalizationFile.create(bean);
			case ILocalizationFileType.VIDEO:
				locFile = VideoLocalizationFile.create(bean);
			}
		}
		return locFile;
	}
}
