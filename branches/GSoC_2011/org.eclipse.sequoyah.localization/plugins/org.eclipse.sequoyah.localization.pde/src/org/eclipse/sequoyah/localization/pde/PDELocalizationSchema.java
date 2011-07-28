/********************************************************************************
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Lucas Tiago de Castro Jesus (GSoC)
 ********************************************************************************/
package org.eclipse.sequoyah.localization.pde;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.sequoyah.device.common.utilities.BasePlugin;
import org.eclipse.sequoyah.device.common.utilities.exception.SequoyahException;
import org.eclipse.sequoyah.localization.pde.PDELocaleAttribute;
import org.eclipse.sequoyah.localization.editor.datatype.ColumnInfo;
import org.eclipse.sequoyah.localization.editor.datatype.RowInfo;
import org.eclipse.sequoyah.localization.editor.datatype.RowInfoLeaf;
import org.eclipse.sequoyah.localization.editor.datatype.TranslationInfo;
import org.eclipse.sequoyah.localization.pde.PDELocaleAttribute.PDELocaleAttributes;
import org.eclipse.sequoyah.localization.pde.datamodel.PDEStringLocalizationFile;
import org.eclipse.sequoyah.localization.pde.i18n.Messages;
import org.eclipse.sequoyah.localization.pde.manager.ILocalizationFileManager;
import org.eclipse.sequoyah.localization.pde.manager.LocalizationFileManagerFactory;
import org.eclipse.sequoyah.localization.pde.manager.StringLocalizationFileManager;
import org.eclipse.sequoyah.localization.tools.datamodel.LocaleAttribute;
import org.eclipse.sequoyah.localization.tools.datamodel.LocaleInfo;
import org.eclipse.sequoyah.localization.tools.datamodel.LocalizationFile;
import org.eclipse.sequoyah.localization.tools.datamodel.LocalizationFileBean;
import org.eclipse.sequoyah.localization.tools.datamodel.StringLocalizationFile;
import org.eclipse.sequoyah.localization.tools.extensions.classes.ILocalizationSchema;
import org.eclipse.sequoyah.localization.tools.extensions.implementation.generic.TranslateColumnInputDialog;
import org.eclipse.sequoyah.localization.tools.extensions.implementation.generic.TranslateColumnsInputDialog;
import org.eclipse.sequoyah.localization.tools.managers.LocalizationManager;
import org.eclipse.sequoyah.localization.tools.managers.ProjectLocalizationManager;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.PlatformUI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The PDE Localization Schema provides the localization schema for PDE
 * projects.
 */
public class PDELocalizationSchema extends ILocalizationSchema implements
		IPDELocalizationSchemaConstants {

	private static final HashMap<Class, Class> typesMap = new HashMap<Class, Class>();

	static {
		typesMap.put(StringLocalizationFile.class,
				StringLocalizationFileManager.class);
		typesMap.put(PDEStringLocalizationFile.class,
				StringLocalizationFileManager.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.sequoyah.localization.tools.extensions.classes.
	 * ILocalizationSchema#getPreferedLanguages()
	 */
	@Override
	public List<String> getPreferedLanguages() {

		List<String> langIDs = new ArrayList<String>();

		// Define Properties FILE path
		InputStream pdeStream;
		try {

			if (PDELocalizationPlugin.getDefault().getBundle()
					.getEntry(PREFERED_LANGUAGES_PDE_PATH) != null) {

				pdeStream = PDELocalizationPlugin.getDefault().getBundle()
						.getEntry(PREFERED_LANGUAGES_PDE_PATH).openStream();

				
				DocumentBuilderFactory factory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document document = builder.parse(pdeStream);

				/*
				 * Iterate through Startup Groups
				 */
				Element rootNode = document.getDocumentElement();
				NodeList languages = rootNode.getElementsByTagName("language"); //$NON-NLS-1$
				for (int i = 0; i < languages.getLength(); i++) {

					Element language = (Element) languages.item(i);
					langIDs.add(language.getAttributeNode("id").getNodeValue()); //$NON-NLS-1$
				}
			}

		} catch (Exception e) {

			BasePlugin
					.logError("Could not load prefered languages for PDE Localization Schema"); //$NON-NLS-1$

		}

		return langIDs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.sequoyah.localization.tools.extensions.classes.
	 * ILocalizationSchema#getIDforLanguage(java.lang.String)
	 */
	@Override
	public String getIDforLanguage(String langID) {
		String columnID = LOCALIZATION_FILE_NAME;

		if (langID.contains("_")) { //$NON-NLS-1$
			String[] langParts = langID.split("_"); //$NON-NLS-1$
			columnID += "_" + langParts[0] + "_" + langParts[1]; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} else {
			columnID += "_" + langID; //$NON-NLS-1$
		}
		return columnID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.sequoyah.localization.tools.extensions.classes.
	 * ILocalizationSchema #isValueValid(java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public IStatus isValueValid(String localeID, String key, String value) {
		Status result = new Status(IStatus.OK,
				PDELocalizationPlugin.PLUGIN_ID, ""); //$NON-NLS-1$

		if (localeID.toLowerCase().equals(MANDATORY_ID.toLowerCase())) {

			if ((value == null)) {
				result = new Status(IStatus.ERROR,
						PDELocalizationPlugin.PLUGIN_ID,
						Messages.EmptyKey_Discouraged);
			}

			if ((value == null) || (value.length() == 0)) {
				result = new Status(IStatus.WARNING,
						PDELocalizationPlugin.PLUGIN_ID,
						Messages.EmptyKey_Discouraged);
			}
		}

		return result;
	}

	/**
	 * Key is valid on PDE if it is a valid Java identifier
	 * 
	 */
	@Override
	public IStatus isKeyValid(String key) {
		Status result = new Status(IStatus.OK,
				PDELocalizationPlugin.PLUGIN_ID, ""); //$NON-NLS-1$

		char[] keyChars = key.toCharArray();
		for (int i = 0; i < keyChars.length; i++) {
			boolean valid = (i > 0) ? Character
					.isJavaIdentifierPart(keyChars[i]) : Character
					.isJavaIdentifierStart(keyChars[i]);
			if (!valid) {
				result = new Status(IStatus.ERROR,
						PDELocalizationPlugin.PLUGIN_ID, Messages.bind(
								Messages.Invalid_PDE_Key_Name, "'" //$NON-NLS-1$
										+ keyChars[i] + "'")); //$NON-NLS-1$
				break;
			}
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.sequoyah.localization.tools.extensions.classes.
	 * ILocalizationSchema #promptCollumnName()
	 */
	@Override
	public ColumnInfo promptCollumnName(final IProject project) {
		ColumnInfo newColumn = null;

		// Ask user for the ID
		InputDialog dialog = new InputDialog(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(), NEW_COLUMN_TITLE,
				NEW_COLUMN_DESCRIPTION, NEW_COLUMN_TEXT, //$NON-NLS-2$
				new IInputValidator() {

					public String isValid(String newText) {
						return isValid2(newText, project);
					};
				});

		if (dialog.open() == IDialogConstants.OK_ID) {
			newColumn = new ColumnInfo(dialog.getValue(), dialog.getValue(),
					null, true);
		}

		return newColumn;
	}

	@Override
	public RowInfo[] promptSingleRowName(final IProject iProject, int quantity) {
		RowInfo[] rowInfo = new RowInfo[quantity];

		String[] singleStringAutomaticKey = getAutomaticKeys(iProject, false,
				quantity);

		for (int i = 0; i < quantity; i++) {
			RowInfoLeaf row = new RowInfoLeaf(singleStringAutomaticKey[i],
					null, -1, null);
			rowInfo[i] = row;
		}
		return rowInfo;
	}

	@Override
	public RowInfo[] promptArrayRowName(IProject iProject, int quantity) {
		RowInfo[] rowInfo = null;

		String[] arrayAutomaticKey = getAutomaticKeys(iProject, true, quantity);

		rowInfo = new RowInfo[arrayAutomaticKey.length];
		for (int j = 0; j < arrayAutomaticKey.length; j++) {
			RowInfo row = new RowInfo(arrayAutomaticKey[j]);
			int arraySize = 1; // by default array will be created with 1 array
								// item
			for (int i = 0; i < arraySize; i++) {
				row.addChild(
						new RowInfoLeaf(arrayAutomaticKey[j], row, i, null), 0);
			}

			rowInfo[j] = row;
		}
		return rowInfo;
	}

	/**
	 * Returns an automatic key for a single string or an array
	 * 
	 * @param iProject
	 * @param isArray
	 * @return
	 */
	private String[] getAutomaticKeys(final IProject iProject, boolean isArray,
			int quantity) {
		String[] automaticKeys = new String[quantity];
		try {

			// get new string automatic key
			String key = (isArray) ? Messages.PDELocalizationSchema_NewArrayKeyPrefix
					: Messages.PDELocalizationSchema_NewStringKeyPrefix;
			int index = 0;

			ProjectLocalizationManager projLocMgr = LocalizationManager
					.getInstance().getProjectLocalizationManager(iProject,
							false);
			ILocalizationSchema schema = projLocMgr
					.getProjectLocalizationSchema();
			LocaleInfo locale = schema.getLocaleInfoFromID(schema
					.getDefaultID());
			
			if (locale != null) {
				LocalizationFile mainFile = projLocMgr.getLocalizationProject()
						.getLocalizationFile(locale);
				
				for (int i = 0; i < quantity; i++, index++) {
					automaticKeys[i] = key + index;
				
					while (((StringLocalizationFile) mainFile)
							.containsKey(automaticKeys[i])) {
						// automatic key already exists => create a new one
						index++;
						automaticKeys[i] = key + index;
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SequoyahException se) {

		}
		
		return automaticKeys;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.sequoyah.localization.tools.extensions.classes.
	 * ILocalizationSchema #promptTranslatedCollumnName()
	 */
	@Override
	public TranslationInfo promptTranslatedCollumnName(final IProject project,
			String selectedColumn) {
		TranslationInfo newColumn = null;

		// Ask user for the ID
		TranslateColumnInputDialog dialog = new TranslateColumnInputDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				project, selectedColumn, NEW_TRANSLATE_COLUMN_TITLE,
				NEW_COLUMN_DESCRIPTION, NEW_COLUMN_TEXT, new IInputValidator() {

					public String isValid(String newText) {
						return isValid2(newText, project);
					};
				});

		if (dialog.open() == IDialogConstants.OK_ID) {

			newColumn = new TranslationInfo(dialog.getValue(),
					dialog.getValue(), null, true, dialog.getFromLanguage(),
					dialog.getToLanguage(), null, dialog.getTranslator());
		}

		return newColumn;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.sequoyah.localization.tools.extensions.classes.
	 * ILocalizationSchema #promptTranslatedCollumnName()
	 */
	@Override
	public TranslationInfo[] promptTranslatedCollumnsName(
			final IProject project, String selectedColumn,
			String[] selectedKeys, String[] selectedCells, TreeColumn[] columns) {
		TranslationInfo[] newColumns = null;

		// Ask user for the ID
		TranslateColumnsInputDialog dialog = new TranslateColumnsInputDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				project, selectedColumn, selectedCells, columns,
				TRANSLATE_CELLS_TITLE);

		if (dialog.open() == IDialogConstants.OK_ID) {
			List<TranslateColumnsInputDialog.DestinationColumn> destinationColumns = dialog
					.getDestinationColumns();
			newColumns = new TranslationInfo[selectedCells.length
					* destinationColumns.size()];

			int count = 0;
			for (int i = 0; i < selectedCells.length; i++) {
				String selectedCell = selectedCells[i];
				String selectedKey = selectedKeys[i];
				for (int j = 0; j < destinationColumns.size(); j++) {
					TranslateColumnsInputDialog.DestinationColumn destColumn = destinationColumns
							.get(j);
					newColumns[count] = new TranslationInfo(
							destColumn.getText(), destColumn.getText(), null,
							true, dialog.getFromLanguage(),
							destColumn.getLang(), selectedCell,
							dialog.getTranslator());
					newColumns[count].setFromKey(selectedKey);
					newColumns[count].setToColumn(destColumn.getText());
					count++;
				}
			}

		}

		return newColumns;
	}

	@Override
	public TranslationInfo[] promptTranslatedCollumnsName(
			final IProject project, String selectedColumn,
			String[] selectedKeys, String[] selectedCells,
			Integer[] selectedIndexes, TreeColumn[] columns) {
		TranslationInfo[] newColumns = null;

		// Ask user for the ID
		TranslateColumnsInputDialog dialog = new TranslateColumnsInputDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				project, selectedColumn, selectedCells, columns,
				TRANSLATE_CELLS_TITLE);

		if (dialog.open() == IDialogConstants.OK_ID) {
			List<TranslateColumnsInputDialog.DestinationColumn> destinationColumns = dialog
					.getDestinationColumns();
			newColumns = new TranslationInfo[selectedCells.length
					* destinationColumns.size()];

			int count = 0;
			for (int i = 0; i < selectedCells.length; i++) {
				String selectedCell = selectedCells[i];
				String selectedKey = selectedKeys[i];
				for (int j = 0; j < destinationColumns.size(); j++) {
					TranslateColumnsInputDialog.DestinationColumn destColumn = destinationColumns
							.get(j);
					newColumns[count] = new TranslationInfo(
							destColumn.getText(), destColumn.getText(), null,
							true, dialog.getFromLanguage(),
							destColumn.getLang(), selectedCell,
							dialog.getTranslator());
					newColumns[count].setFromKey(selectedKey);
					newColumns[count].setToColumn(destColumn.getText());
					newColumns[count].setIndexKey(selectedIndexes[i]);
					count++;
				}
			}

		}

		return newColumns;
	}

	/**
	 * Checks if the current input is a valid column name for a new column
	 * 
	 * @param value
	 *            the new column name
	 * @return true if it is a valid column name
	 */
	private String isValid2(String value, IProject project) {

		String result = null;

		if (value.startsWith(LOCALIZATION_FILE_NAME)) {

			PDELocalizationSchema schema = new PDELocalizationSchema();
			String id = value.replace(LOCALIZATION_FILE_NAME, ""); //$NON-NLS-1$
			LocaleInfo info = schema.getLocaleInfoFromID(id);
			ProjectLocalizationManager manager = null;
			try {
				manager = LocalizationManager.getInstance()
						.getProjectLocalizationManager(project, false);
			} catch (IOException e) {
			} catch (SequoyahException e) {
			}

			if ((info.getLocaleAttributes().size() > 0)
					|| (value
							.equalsIgnoreCase(PDELocalizationSchema.LOCALIZATION_FILES_FOLDER))) {
				LocalizationFile file = manager.getLocalizationProject()
						.getLocalizationFile(info);
				if ((file != null) && !file.isToBeDeleted()) {
					result = Messages.PDENewColumnProvider_Dialog_FileAlreadyExists;
				}
			} else {
				result = NEW_COLUMN_INVALID_ID;
			}

		} else {
			result = NEW_COLUMN_INVALID_ID;
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.sequoyah.localization.tools.extensions.classes.
	 * ILocalizationSchema #getEditorName()
	 */
	@Override
	public String getEditorName() {
		return Messages.PDEStringEditorInput_EditorTooltip;
	}

	/**
	 * Create an pde string localization file. It's a XML which has the
	 * following format:
	 * 
	 * <?xml version="1.0" encoding="utf-8"?> <resources> <string
	 * name="KEY">VALUE</string> ... </resources>
	 * 
	 * @see org.eclipse.sequoyah.localization.tools.extensions.classes.ILocalizationSchema
	 *      #createStringFile(org.eclipse.sequoyah.localization.tools.datamodel.LocaleInfo)
	 */
	@Override
	public void createLocalizationFile(LocalizationFile localizationFile)
			throws SequoyahException {

		Class aClass = typesMap.get(localizationFile.getClass());
		ILocalizationFileManager manager = LocalizationFileManagerFactory
				.getInstance().createLocalizationFileManager(aClass.getName());
		manager.createFile(localizationFile);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.sequoyah.localization.tools.extensions.classes.
	 * ILocalizationSchema #getLocaleToolTip(org.eclipse.core.runtime.Path)
	 */
	@Override
	public String getLocaleToolTip(IPath path) {
		LocaleInfo locale = getLocaleInfoFromPath(path);
		
		List<LocaleAttribute> attributes = locale.getLocaleAttributes();
		
		String result = ""; //$NON-NLS-1$
		for (Iterator<LocaleAttribute> iterator = attributes.iterator(); iterator
				.hasNext();) {
			if (result.length() > 0) {
				result = result + "\n "; //$NON-NLS-1$
			}
			LocaleAttribute localeAttribute = iterator.next();
			result = result
					+ ((PDELocaleAttribute) localeAttribute)
							.getDisplayName()
					+ ": " //$NON-NLS-1$
					+ ((PDELocaleAttribute) localeAttribute)
							.getDisplayValue();
		}

		if (result.length() == 0) {
			result = DEFAULT_LOCALE_TOOLTIP;
		}
		return result;
	}

	public String getColumnID(IFile file) {
		return file.getFullPath().lastSegment().replace(FILE_EXTENSION,"");
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.sequoyah.localization.tools.extensions.classes.
	 * ILocalizationSchema #getLocaleAttributes()
	 */
	@Override
	public List<LocaleAttribute> getLocaleAttributes() {
		List<LocaleAttribute> localeAttributes = new ArrayList<LocaleAttribute>();

		localeAttributes.add(new PDELocaleAttribute(new Integer(123),
				PDELocaleAttributes.COUNTRY_CODE.ordinal()));

		localeAttributes.add(new PDELocaleAttribute(null,
				PDELocaleAttributes.LANGUAGE.ordinal()));		

		return localeAttributes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.sequoyah.localization.tools.extensions.classes.
	 * ILocalizationSchema #getLocalizationFileExtensions()
	 */
	@Override
	public List<String> getLocalizationFileExtensions() {
		List<String> localizationFileExtensions = new ArrayList<String>();
		localizationFileExtensions.add(FILE_EXTENSION);
		return localizationFileExtensions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.sequoyah.localization.tools.extensions.classes.
	 * ILocalizationSchema
	 * #getLocalizationFiles(org.eclipse.core.resources.IProject)
	 */
	@Override
	public Map<LocaleInfo, IFile> getLocalizationFiles(IProject project) {

		Map<LocaleInfo, IFile> localizationFiles = new LinkedHashMap<LocaleInfo, IFile>();
		boolean hasDefault = false;
		
		try {
			IResource localizationFolder = project.findMember(LOCALIZATION_FILES_FOLDER);
			if ((localizationFolder != null)
					&& (localizationFolder instanceof IFolder)) {
				IResource[] files = ((IFolder) localizationFolder).members();
				for (IResource file : files) {
					if ((file instanceof IFile)
							&& (isLocalizationFile((IFile) file))) {
									// TODO WARNING 2 put LocalizationFiles in
									// the map, not an IFile
						
						localizationFiles.put(
								getLocaleInfoFromPath(file
								.getProjectRelativePath()),
								(IFile) file);
						
						if (localizationFolder.getName().equals(
							LOCALIZATION_FILES_FOLDER) && ((IFolder) localizationFolder).getFile(LOCALIZATION_FILE_NAME+FILE_EXTENSION).exists()) {
							hasDefault = true;
						}
					}
				}
			}
			
			if(!hasDefault){		
				
				final IFolder i18nFolder = project.
					getFolder(LOCALIZATION_FILES_FOLDER);
				try {						
						if (!i18nFolder.exists()) {
							// try to create the folder
							PlatformUI
									.getWorkbench()
									.getProgressService()
									.run(false, false,
											new IRunnableWithProgress() {
												public void run(
														IProgressMonitor monitor)
														throws InvocationTargetException,
														InterruptedException {
													try {
														i18nFolder.create(
																true, true,
																monitor);
													} catch (CoreException e) {
														// do nothing
													}
												}
											});
						}
						// check if folder was created
						// create the default file
					
						if (i18nFolder.exists()) {
							IFile valuesFile = ((IFolder) i18nFolder)
									.getFile(LOCALIZATION_FILE_NAME+FILE_EXTENSION);
					
							localizationFiles.put(
									getLocaleInfoFromPath(valuesFile
											.getProjectRelativePath()),
									valuesFile);
						}
						
					} catch (Exception e) {
						// do nothing, just exit
					}
				}			
		} catch (CoreException e) {
			//
		}
		return localizationFiles;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.sequoyah.localization.tools.extensions.classes.
	 * ILocalizationSchema #isLocalizationFile(org.eclipse.core.resources.IFile)
	 */
	@Override
	public boolean isLocalizationFile(IFile file) {

		boolean result = false;
		if (file != null) {
			// TODO WARNING 1 fix regular expression for each type of
			// localizable resource			
			if (file.getProjectRelativePath().toString()
					.endsWith(FILE_EXTENSION)) {
				result = true;
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.sequoyah.localization.tools.extensions.classes.
	 * ILocalizationSchema #loadAllFiles()
	 */
	@Override
	public Map<LocaleInfo, LocalizationFile> loadAllFiles(IProject project)
			throws SequoyahException {

		Map<LocaleInfo, LocalizationFile> filesMap = new LinkedHashMap<LocaleInfo, LocalizationFile>();
		Map<LocaleInfo, IFile> localizationFiles = getLocalizationFiles(project);
		
		for (Map.Entry<LocaleInfo, IFile> entry : localizationFiles.entrySet()) {
			String fileName = entry.getValue().getName();
			if (fileName.endsWith(FILE_EXTENSION)) {
				// Selecting the StringLocalizationFileManager for resources of
				// type string file
				
				filesMap.put(
						entry.getKey(),
						loadFile(StringLocalizationFileManager.class.getName(),
								entry.getValue()));
			}
		}
		return filesMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.sequoyah.localization.tools.extensions.classes.
	 * ILocalizationSchema #loadFile()
	 */
	@Override
	public LocalizationFile loadFile(String type, IFile file)
			throws SequoyahException {

		ILocalizationFileManager manager = LocalizationFileManagerFactory
				.getInstance().createLocalizationFileManager(type);

		LocalizationFileBean bean = new LocalizationFileBean(
				new LocalizationFile());
		PDEStringLocalizationFile localizationFile = new PDEStringLocalizationFile(
				bean);

		localizationFile
				.setLocaleInfo(getLocaleInfoFromPath(file.getFullPath()));
		localizationFile.setFile(file);

		LocalizationFile locFile = manager.loadFile(localizationFile);

		return locFile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.sequoyah.localization.tools.extensions.classes.
	 * ILocalizationSchema #
	 * updateFile(org.eclipse.sequoyah.localization.tools.datamodel
	 * .LocalizationFile)
	 */
	@Override
	public void updateFile(LocalizationFile localizationFile)
			throws SequoyahException {

		Class aClass = typesMap.get(localizationFile.getClass());
		ILocalizationFileManager manager = LocalizationFileManagerFactory
				.getInstance().createLocalizationFileManager(aClass.getName());
		manager.updateFile(localizationFile);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.sequoyah.localization.tools.extensions.classes.
	 * ILocalizationSchema
	 * #createLocalizationFile(org.eclipse.core.resources.IFile,
	 * org.eclipse.sequoyah.localization.tools.datamodel.LocaleInfo,
	 * java.util.List, java.util.List)
	 */
	@Override
	public LocalizationFile createLocalizationFile(LocalizationFileBean bean) {
		bean.setType(PDEStringLocalizationFile.class.getName());
		PDEStringLocalizationFile f = new PDEStringLocalizationFile(
				bean);
		return f;
	}

	@Override
	public Object getLocalizationFileContent(LocalizationFile locFile) {
		Class aClass = typesMap.get(locFile.getClass());
		ILocalizationFileManager manager = LocalizationFileManagerFactory
				.getInstance().createLocalizationFileManager(aClass.getName());
		return manager.getLocalizationFileContent(locFile);
	}

	@Override
	public void updateLocalizationFileContent(LocalizationFile locFile,
			String content) throws SequoyahException {
		Class aClass = typesMap.get(locFile.getClass());
		ILocalizationFileManager manager = LocalizationFileManagerFactory
				.getInstance().createLocalizationFileManager(aClass.getName());
		manager.updateLocalizationFileContent(locFile, content);
	}

	@Override
	public boolean keyAcceptsBlankSpaces() {
		return false;
	}

	/*
	 * 
	 */
	private boolean isKnownNode(Node visitingNode) {
		return visitingNode.getNodeName().equals(PDE_STRING_TAG);
	}

	/**
	 * Given a localization file path, returns the language information
	 * (attributes) of this localization file according to the file name.
	 * 
	 * @return LanguageInfo object with the attributes of this language
	 */
	private LocaleInfo getLocaleInfoFromPath(IPath path) {

		IPath file = path;
		//folder = path.removeLastSegments(1);
		String fileName = file.lastSegment();
		String id = fileName.replace(LOCALIZATION_FILE_NAME, ""); //$NON-NLS-1$
		id = id.replace(FILE_EXTENSION, ""); //$NON-NLS-1$
		return getLocaleInfoFromID(id);

	}

	/**
	 * @param lang
	 * @return
	 */
	@Override
	public String getPathFromLocaleInfo(LocaleInfo lang) {
		
		String result;
		if (lang.getLocaleAttributes().size() > 0) {
			// There are qualifiers to concatenate in the folder name
			result = LOCALIZATION_FILES_FOLDER + File.separator
					+ LOCALIZATION_FILE_NAME + QUALIFIER_SEP
					+ getLocaleID(lang) + FILE_EXTENSION;
		} else {
			// It is a default location file (no language qualifier)
			result = LOCALIZATION_FILES_FOLDER + File.separator
					+ LOCALIZATION_FILE_NAME + FILE_EXTENSION;

		}
		return result;

	}

	public LocaleInfo getLocaleInfoFromID(String ID) {

		LocaleInfo result = new LocaleInfo();

		String[] segments = ID.split(QUALIFIER_SEP);
		int lastQualifier = -1;

		List<LocaleAttribute> localeAttributes = new ArrayList<LocaleAttribute>();
		//Handle with qualifiers like en_US, pt_BR...
		if(segments.length == 3){
			segments[1] = segments[1] + QUALIFIER_SEP + segments[2];
			segments[2] = "";
		}
		for (int i = 1; i < segments.length; i++) {
			if (segments[i].equals("")) { //$NON-NLS-1$
				// Do nothing
			} else if (isCountryCodeSegment(segments[i])
					&& (lastQualifier < PDELocaleAttributes.COUNTRY_CODE
							.ordinal())) {
				lastQualifier = PDELocaleAttributes.COUNTRY_CODE.ordinal();
				localeAttributes.add(new PDELocaleAttribute(segments[i],
						PDELocaleAttributes.COUNTRY_CODE.ordinal()));
				
			} else if (isLanguageSegment(segments[i])
					&& (lastQualifier < PDELocaleAttributes.LANGUAGE
							.ordinal())) {
				lastQualifier = PDELocaleAttributes.LANGUAGE.ordinal();
				localeAttributes.add(new PDELocaleAttribute(segments[i],
						PDELocaleAttributes.LANGUAGE.ordinal()));
			} else {
				localeAttributes = new ArrayList<LocaleAttribute>();
			}

		}

		result.setLocaleAttributes(localeAttributes);
		return result;
	}
	
	//TODO 
	private boolean isCountryCodeSegment(String value) {
		boolean result = false;
		
		return result;
	}
	
	private boolean isLanguageSegment(String value) {
		return (value.length() == 2 || value.length() == 5);

	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.sequoyah.localization.tools.extensions.classes.
	 * ILocalizationSchema
	 * #getLocaleID(org.eclipse.sequoyah.localization.tools.datamodel
	 * .LocaleInfo)
	 */
	@Override
	public String getLocaleID(LocaleInfo localeInfo) {
		String localeID = ""; //$NON-NLS-1$
		List<LocaleAttribute> localeAttributes = localeInfo
				.getLocaleAttributes();
		for (Iterator<LocaleAttribute> iterator = localeAttributes.iterator(); iterator
				.hasNext();) {
			LocaleAttribute localeAttribute = iterator.next();
			if (((PDELocaleAttribute) localeAttribute).isSet()) {
				if (localeID.length() != 0) {
					localeID = localeID + QUALIFIER_SEP;
				}
				localeID = localeID + localeAttribute.getFolderValue();
			}
		}
		return localeID;
	}


	@Override
	public String getISO639LangFromID(String ID) {
		String iso639 = null;

		LocaleInfo localeInfo = getLocaleInfoFromID(ID);
		for (LocaleAttribute locAtt : localeInfo.getLocaleAttributes()) {
			if (locAtt.getDisplayName().equals("Language")) { //$NON-NLS-1$
				iso639 = locAtt.getFolderValue();
			}
		}

		return iso639;
	}

	@Override
	public String getDefaultID() {
		return LOCALIZATION_FILES_FOLDER;
	}
	
	

	
}