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
 * @author wmg040
 *
 */
public class LocalizationFileFactory {

	/**
	 * 
	 */
	private static LocalizationFileFactory localizationFileFactory;

	/**
	 * 
	 */
	private LocalizationFileFactory() {
	}

	/**
	 * @return
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
	 * @param bean
	 * @return
	 */
	public LocalizationFile createLocalizationFile(LocalizationFileBean bean) {
		LocalizationFile locFile = null;
		if (bean != null){
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
