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
 * This interface holds constants that identify each type of LocalizationFile.
 * 
 */
public interface ILocalizationFileType {

	/**
	 * Localization file refers to a text.
	 */
	public static final int STRING = 0;
	
	/**
	 * Localization file refers to an image.
	 */
	public static final int IMAGE = 1;

	/**
	 * Localization file refers to a sound.
	 */
	public static final int SOUND = 2;

	/**
	 * Localization file refers to a video.
	 */
	public static final int VIDEO = 3;

}
