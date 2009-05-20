/**
 * Copyright (c) 2009 Motorola.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Henrique Magalhaes (Motorola) - Initial version
 */
package org.eclipse.mtj.internal.pulsar.metadata.generator;


import org.eclipse.osgi.util.NLS;

public class Messages extends NLS{
	private static final String BUNDLE_NAME = "org.eclipse.mtj.internal.pulsar.metadata.generator.messages"; //$NON-NLS-1$
	public static String MetadataGeneratorDialog_DialogTitle;
	public static String MetadataGeneratorDialog_DialogMessage;
	public static String MetadataGeneratorDialog_RepositoryNameLabel;
	public static String MetadataGeneratorDialog_NewRepositoryText;
	public static String MetadataGeneratorDialog_MissingRepositoryNameErrorMessage;
	public static String MetadataGeneratorDialog_SaveRepositoryErrorMessage;
	public static String MetadataGeneratorDialog_RepositoryLocationLabel;
	public static String MetadataGeneratorDialog_BrowseButton;
	public static String MetadataGeneratorDialog_AddButton;
	public static String MetadataGeneratorDialog_RemoveButton;
	public static String MetadataGeneratorDialog_SaveButton;
	public static String MetadataGeneratorDialog_UnsavedRepositoryDialogTitle;
	public static String MetadataGeneratorDialog_UnsavedRepositoryDialogMessage;
	public static String MetadataGeneratorDialog_ChooseLocationDialogMessage;
	
	public static String NewUnitDialog_NewSDKInstallerShellTitle;
	public static String NewUnitDialog_ArtifactFileLabel;
	public static String NewUnitDialog_BrowseButton;
	public static String NewUnitDialog_SelectArtifactDialogMessage;
	public static String NewUnitDialog_ErrorDialogTitle;
	public static String NewUnitDialog_RepositoryLocationErrorMessage;
	public static String NewUnitDialog_TypeLabel;
	public static String NewUnitDialog_UnzipArchiveLabelProviderText;
	public static String NewUnitDialog_SingleExecutableLabelProviderText;
	public static String NewUnitDialog_UnzipLabelProviderText;
	public static String NewUnitDialog_ExecutableLabel;
	public static String NewUnitDialog_Executable;
	public static String NewUnitDialog_ExecutableToolTip;
	public static String NewUnitDialog_DisplayNameLabel;
	public static String NewUnitDialog_IDLabel;
	public static String NewUnitDialog_VersionLabel;
	public static String NewUnitDialog_CategoryNameLabel;
	public static String NewUnitDialog_DescriptionLabel;
	public static String NewUnitDialog_LicenseURLLabel;
	public static String NewUnitDialog_LicenseURLToolTip;
	public static String NewUnitDialog_LicenseBodyLabel;
	public static String NewUnitDialog_LicenseBodyToolTip;
	public static String NewUnitDialog_CopyrightURLLabel;
	public static String NewUnitDialog_CopyrightURLToolTip;
	public static String NewUnitDialog_CopyrightBodyLabel;
	public static String NewUnitDialog_CopyrightBodyToolTip;
	public static String NewUnitDialog_SetUnitLicenseError;
	public static String NewUnitDialog_SetUnitCopyrightError;
	public static String NewUnitDialog_UniqueIdError;
	public static String NewUnitDialog_CheckValueError;
	
	public static String GeneratorEngine_ParseRepositoryError;
	public static String GeneratorEngine_NullPath;
	public static String GeneratorEngine_MetadataDoesNotExist;
	public static String GeneratorEngine_ArtifactsDoesNotExist;
	public static String GeneratorEngine_ExecutablePathNotFound;
	public static String GeneratorEngine_CreateMetadataError;
	public static String GeneratorEngine_CreateArtefactsError;
	
	public static String EnvironmentDialog_EnvironmentDialogTitle;
	public static String EnvironmentDialog_ValidValuesLabel;
	public static String EnvironmentDialog_SelectButton;
	public static String EnvironmentDialog_DeselectButton;
	
	
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	private Messages(){
		
	}
}
