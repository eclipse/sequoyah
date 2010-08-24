/********************************************************************************
 * Copyright (c) 2010 Motorola Mobility, Inc.
 * All rights reserved. This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Daniel Pastore (Eldorado) - Bug 323036 - Add support to other Localizable Resources
 * 
 ********************************************************************************/
package org.eclipse.sequoyah.localization.tools.datamodel;

import org.eclipse.core.resources.IFile;

/**
 * This class represents a real localization file for images
 * 
 */
public class ImageLocalizationFile extends LocalizationFile {

	/*
	 * Static code to add file type to factory's hashmap
	 */
	static {
		LocalizationFileFactory.getInstance().addFileType(
				ImageLocalizationFile.class.getName(),
				ImageLocalizationFile.class);
	}
	
	/**
	 * Default constructor.
	 */
	public ImageLocalizationFile (){
	}
	
	/**
	 * Constructor
	 * 
	 * @param file
	 * @param localeInfo
	 */
	public ImageLocalizationFile(IFile file, LocaleInfo localeInfo) {
		super(file, localeInfo);
	}

	/**
	 * ImageLocalizationFile knows how to create itself.
	 * 
	 * @param bean
	 *            Bean containing all information necessary to create any type
	 *            of LocalizationFile.
	 * @return LocalizationFile created (if the parameter received is not null).
	 */
	public static LocalizationFile create(LocalizationFileBean bean) {
		LocalizationFile locFile;
		if (bean != null) {
			locFile = new ImageLocalizationFile(bean.getFile(),
					bean.getLocale());
		} else {
			locFile = null;
		}
		return locFile;
	}

}
