/********************************************************************************
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Lucas Tiago de Castro Jesus (GSoC)
 * 
 * Contributors:
 * Name (Company) - [Bug #] - Description
 ********************************************************************************/

package org.eclipse.sequoyah.localization.pde.i18n;

import org.eclipse.osgi.util.NLS;

/**
 * ID's for all strings that will be shown to the user. Used for localization
 * purposes
 * 
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.sequoyah.localization.pde.i18n.messages"; //$NON-NLS-1$

	public static String PDELocalizationSchema_6;

	public static String PDELocalizationSchema_Default_PDE_Localization_File_Tooltip;

	public static String PDELocalizationSchema_NewArrayKeyPrefix;

	public static String PDELocalizationSchema_NewStringKeyPrefix;

	public static String StringLocalizationFileManager_Exception_CouldNotLoadFile;

	public static String PDENewColumnProvider_Dialog_FileAlreadyExists;

	public static String PDENewColumnProvider_InvalidNewColumID;

	public static String PDENewColumnProvider_NewColumnDescription;

	public static String PDENewColumnProvider_NewColumnTitle;

	public static String PDEStringEditorInput_EditorTooltip;

	public static String PDETranslatedColumnProvider_NewColumnTitle;

	public static String PDETranslateCells_DialogTitle;

	public static String PDENewRow_DialogTitle;

	// Status message
	public static String EmptyKey_Discouraged;

	// Exception texts
	public static String Unknown_PDE_Type;

	public static String Invalid_PDE_Value;

	public static String Invalid_PDE_Value_Size;

	public static String Invalid_PDE_Key_Name;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
