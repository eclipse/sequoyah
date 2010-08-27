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
package org.eclipse.sequoyah.localization.android.manager;

import org.eclipse.sequoyah.localization.tools.datamodel.LocalizationFile;

/**
 * This class deals specifically with localized String / text content. 
 *
 */
public class StringLocalizationFileManager extends ILocalizationFileManager {
	
	/*
	 * Static code to add this manager to factory's hashmap
	 */
	static {
		LocalizationFileManagerFactory.getInstance().addManager(
				StringLocalizationFileManager.class.getName(),
				StringLocalizationFileManager.class);
	}		
	
	/**
	 * Default constructor.
	 */
	public StringLocalizationFileManager() {
	}
	
	/**
	 * StringLocalizationFileManager knows how to create itself.
	 * 
	 * @return StringLocalizationFileManager created 
	 */
	public static ILocalizationFileManager create() {
		StringLocalizationFileManager locFileManager;
		locFileManager = new StringLocalizationFileManager();
		return locFileManager;
	}	

	@Override
	public LocalizationFile loadFile(LocalizationFile locFile) {
		return null;
	}

	@Override
	public void createFile(LocalizationFile locFile) {
		
	}

	@Override
	public void updateFile(LocalizationFile locFile) {
		
	}

}
