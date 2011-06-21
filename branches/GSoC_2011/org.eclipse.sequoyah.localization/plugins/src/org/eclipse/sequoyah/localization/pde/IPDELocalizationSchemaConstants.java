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
package org.eclipse.sequoyah.localization.pde;

import org.eclipse.sequoyah.localization.pde.PDELocalizationSchema;
import org.eclipse.sequoyah.localization.pde.i18n.Messages;

/**
 * Interface for sharing constants used in different places.
 * 
 */
public interface IPDELocalizationSchemaConstants {

	/*
	 * PDE resources info
	 */

	//O que seria esse resource?
	//Eu acho que não precisa
	public String RESOURCES_FOLDER    = "i18n";	
	public String PREFERED_LANGUAGES_PDE_PATH = "resource/prefered_languages.xml"; //$NON-NLS-1$

	public String DEFAULT_LOCALE_TOOLTIP = Messages.PDELocalizationSchema_Default_Andr_Localization_File_Tooltip;

	public static final String LOCALIZATION_FILES_FOLDER = "i18n"; //$NON-NLS-1$
	//Pq coloca um nome padrao?
	public static final String LOCALIZATION_FILE_NAME = "plugin.properties"; //$NON-NLS-1$
	
	public String FILE_EXTENSION = "properties"; //$NON-NLS-1$
	
	//Acho que nao precisa
	public String LF_REGULAR_EXPRESSION = LOCALIZATION_FILES_FOLDER + ".*" + "/" + LOCALIZATION_FILE_NAME; //$NON-NLS-1$ //$NON-NLS-2$

	public String PDE_RESOURCES_TAG    = ""; //$NON-NLS-1$
	
	//Acho que nao tem para projeto eclipse
	public String PDE_STRING_ATTR_NAME = "name"; //$NON-NLS-1$
	public String PDE_STRING_TAG       = ""; //$NON-NLS-1$
	
	public String NEW_COLUMN_TITLE = Messages.PDENewColumnProvider_NewColumnTitle;

	public String NEW_TRANSLATE_COLUMN_TITLE = Messages.PDETranslatedColumnProvider_NewColumnTitle;

	public String TRANSLATE_CELLS_TITLE = Messages.PDETranslateCells_DialogTitle;

	public String NEW_ROW_TITLE = Messages.PDENewRow_DialogTitle;

	public String NEW_COLUMN_DESCRIPTION = Messages.PDENewColumnProvider_NewColumnDescription;

	public String NEW_COLUMN_TEXT = PDELocalizationSchema.LOCALIZATION_FILES_FOLDER;

	public String NEW_COLUMN_INVALID_ID = Messages.PDENewColumnProvider_InvalidNewColumID;

	public String MANDATORY_ID = PDELocalizationSchema.LOCALIZATION_FILES_FOLDER;

	//O que é isso? Qualifier?
	public String QUALIFIER_SEP = "-"; //$NON-NLS-1$

	

}
