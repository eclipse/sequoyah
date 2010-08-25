/********************************************************************************
 * Copyright (c) 2009-2010 Motorola Mobility, Inc.
 * All rights reserved. All rights reserved. This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Vinicius Hernandes (Motorola)
 * Matheus Tait Lima (Eldorado)
 * 
 * Contributors:
 * Marcelo Marzola Bossoni (Eldorado) -  Bug [289146] - Performance and Usability Issues
 * Marcelo Marzola Bossoni (Eldorado) - Bug (289236) - Editor Permitting create 2 columns with same id
 * Vinicius Rigoni Hernandes (Eldorado) - Bug [289885] - Localization Editor doesn't recognize external file changes
 * Matheus Tait Lima (Eldorado) - Adapting to accept automatic translation
 * Marcel Gorri (Eldorado) -  Add method to retrieve ISO639 lang ID
 * Paulo Faria (Eldorado) - Add method to retrieve formatted (bold, underline, italics) string
 * Paulo Faria (Eldorado) - Add methods for not to lose comments on save
 * Fabricio Violin (Eldorado) - Bug [317065] - Localization file initialization bug 
 * Daniel Drigo Pastore, Marcel Augusto Gorri (Eldorado) - Bug 312971 - Localization Editor does not accept < and > characters
 * Marcel Augusto Gorri (Eldorado) - Bug 323036 - Add support to other Localizable Resources
 * 
 ********************************************************************************/
package org.eclipse.sequoyah.localization.android;

import static org.w3c.dom.Node.COMMENT_NODE;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.sequoyah.device.common.utilities.BasePlugin;
import org.eclipse.sequoyah.device.common.utilities.FileUtil;
import org.eclipse.sequoyah.device.common.utilities.exception.SequoyahException;
import org.eclipse.sequoyah.device.common.utilities.exception.SequoyahExceptionStatus;
import org.eclipse.sequoyah.localization.android.AndroidLocaleAttribute.AndroidLocaleAttributes;
import org.eclipse.sequoyah.localization.android.datamodel.AndroidStringLocalizationFile;
import org.eclipse.sequoyah.localization.android.i18n.Messages;
import org.eclipse.sequoyah.localization.stringeditor.datatype.ColumnInfo;
import org.eclipse.sequoyah.localization.stringeditor.datatype.RowInfo;
import org.eclipse.sequoyah.localization.stringeditor.datatype.TranslationInfo;
import org.eclipse.sequoyah.localization.tools.datamodel.LocaleAttribute;
import org.eclipse.sequoyah.localization.tools.datamodel.LocaleInfo;
import org.eclipse.sequoyah.localization.tools.datamodel.LocalizationFile;
import org.eclipse.sequoyah.localization.tools.datamodel.LocalizationFileBean;
import org.eclipse.sequoyah.localization.tools.datamodel.LocalizationFileFactory;
import org.eclipse.sequoyah.localization.tools.datamodel.StringLocalizationFile;
import org.eclipse.sequoyah.localization.tools.datamodel.node.NodeComment;
import org.eclipse.sequoyah.localization.tools.datamodel.node.StringArray;
import org.eclipse.sequoyah.localization.tools.datamodel.node.StringNode;
import org.eclipse.sequoyah.localization.tools.extensions.classes.ILocalizationSchema;
import org.eclipse.sequoyah.localization.tools.extensions.implementation.generic.NewRowInputDialog;
import org.eclipse.sequoyah.localization.tools.extensions.implementation.generic.TranslateColumnInputDialog;
import org.eclipse.sequoyah.localization.tools.extensions.implementation.generic.TranslateColumnsInputDialog;
import org.eclipse.sequoyah.localization.tools.managers.LocalizationManager;
import org.eclipse.sequoyah.localization.tools.managers.ProjectLocalizationManager;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PlatformUI;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSParser;
import org.w3c.dom.ls.LSParserFilter;

/**
 * The Android Localization Schema provides the localization schema for Android
 * projects.
 */
public class AndroidLocalizationSchema extends ILocalizationSchema {

	/*
	 * Android resources info
	 */
	private final String RESOURCES_FOLDER = "res"; //$NON-NLS-1$

	private final String PREFERED_LANGUAGES_XML_PATH = "resource/prefered_languages.xml"; //$NON-NLS-1$

	private final String DEFAULT_LOCALE_TOOLTIP = Messages.AndroidLocalizationSchema_Default_Andr_Localization_File_Tooltip;

	public static final String LOCALIZATION_FILES_FOLDER = "values"; //$NON-NLS-1$

	public static final String LOCALIZATION_FILE_NAME = "strings.xml"; //$NON-NLS-1$

	private final String FILE_EXTENSION = "xml"; //$NON-NLS-1$

	private final String LF_REGULAR_EXPRESSION = RESOURCES_FOLDER + "/" //$NON-NLS-1$
			+ LOCALIZATION_FILES_FOLDER + ".*" + "/" + LOCALIZATION_FILE_NAME; //$NON-NLS-1$ //$NON-NLS-2$

	/*
	 * Android localization file tags and attributes
	 */
	private final String XML_RESOURCES_TAG = "resources"; //$NON-NLS-1$

	private final String XML_STRING_TAG = "string"; //$NON-NLS-1$

	private final String XML_STRING_ARRAY_TAG = "string-array"; //$NON-NLS-1$

	private final String XML_STRING_ARRAY_ITEM_TAG = "item"; //$NON-NLS-1$

	private final String XML_STRING_ATTR_NAME = "name"; //$NON-NLS-1$

	private final String NEW_COLUMN_TITLE = Messages.AndroidNewColumnProvider_NewColumnTitle;

	private final String NEW_TRANSLATE_COLUMN_TITLE = Messages.AndroidTranslatedColumnProvider_NewColumnTitle;

	private final String TRANSLATE_CELLS_TITLE = Messages.AndroidTranslateCells_DialogTitle;

	private final String NEW_ROW_TITLE = Messages.AndroidNewRow_DialogTitle;

	private final String NEW_COLUMN_DESCRIPTION = Messages.AndroidNewColumnProvider_NewColumnDescription;

	private final String NEW_COLUMN_TEXT = AndroidLocalizationSchema.LOCALIZATION_FILES_FOLDER;

	private final String NEW_COLUMN_INVALID_ID = Messages.AndroidNewColumnProvider_InvalidNewColumID;

	private String MANDATORY_ID = AndroidLocalizationSchema.LOCALIZATION_FILES_FOLDER;

	private final String QUALIFIER_SEP = "-"; //$NON-NLS-1$

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.sequoyah.localization.tools.extensions.classes.
	 * ILocalizationSchema#getPreferedLanguages()
	 */
	@Override
	public List<String> getPreferedLanguages() {

		List<String> langIDs = new ArrayList<String>();

		// Define XML path
		InputStream xmlStream;
		try {

			xmlStream = AndroidLocalizationPlugin.getDefault().getBundle()
					.getEntry(PREFERED_LANGUAGES_XML_PATH).openStream();

			// Load XML
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(xmlStream);

			/*
			 * Iterate through Startup Groups
			 */
			Element rootNode = document.getDocumentElement();
			NodeList languages = rootNode.getElementsByTagName("language"); //$NON-NLS-1$
			for (int i = 0; i < languages.getLength(); i++) {

				Element language = (Element) languages.item(i);
				langIDs.add(language.getAttributeNode("id").getNodeValue()); //$NON-NLS-1$
			}

		} catch (Exception e) {

			BasePlugin
					.logError("Could not load prefered languages for Android Localization Schema"); //$NON-NLS-1$

		}

		return langIDs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.sequoyah.localization.tools.extensions.classes.
	 * ILocalizationSchema#getIDforLanguage(java.lang.String)
	 */
	@Override
	public String getIDforLanguage(String langID) {
		String columnID = LOCALIZATION_FILES_FOLDER;

		if (langID.contains("-")) { //$NON-NLS-1$
			String[] langParts = langID.split("-"); //$NON-NLS-1$
			columnID += "-" + langParts[0] + "-" + "r" + langParts[1]; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} else {
			columnID += "-" + langID; //$NON-NLS-1$
		}

		return columnID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.sequoyah.localization.tools.extensions.classes.
	 * ILocalizationSchema #isValueValid(java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public IStatus isValueValid(String localeID, String key, String value) {
		Status result = new Status(IStatus.OK,
				AndroidLocalizationPlugin.PLUGIN_ID, ""); //$NON-NLS-1$

		if (localeID.toLowerCase().equals(MANDATORY_ID.toLowerCase())) {

			if ((value == null)) {
				result = new Status(IStatus.ERROR,
						AndroidLocalizationPlugin.PLUGIN_ID,
						Messages.EmptyKey_Discouraged);
			}

			if ((value == null) || (value.length() == 0)) {
				result = new Status(IStatus.WARNING,
						AndroidLocalizationPlugin.PLUGIN_ID,
						Messages.EmptyKey_Discouraged);
			}
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.sequoyah.localization.tools.extensions.classes.
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

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.sequoyah.localization.tools.extensions.classes.
	 * ILocalizationSchema#promptRowName(org.eclipse.core.resources.IProject)
	 */
	@Override
	public RowInfo[] promptRowName(final IProject iProject) {
		RowInfo[] rowInfo = null;

		NewRowInputDialog dialog = new NewRowInputDialog(PlatformUI
				.getWorkbench().getActiveWorkbenchWindow().getShell(),
				iProject, NEW_ROW_TITLE);

		if (dialog.open() == IDialogConstants.OK_ID) {
			rowInfo = new RowInfo[dialog.getNumEntries()];
			String key = dialog.getKey();
			boolean isArray = dialog.isArray();
			for (int i = 0; i < dialog.getNumEntries(); i++) {
				rowInfo[i] = new RowInfo(key, isArray, null);
			}
		}

		return rowInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.sequoyah.localization.tools.extensions.classes.
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

			newColumn = new TranslationInfo(dialog.getValue(), dialog
					.getValue(), null, true, dialog.getFromLanguage(), dialog
					.getToLanguage(), null, dialog.getTranslator());
		}

		return newColumn;
	}

	@Override
	public TranslationInfo[] promptTranslatedCollumnsName(
			final IProject project, String selectedColumn,
			String[] selectedKeys, String[] selectedCells, TableColumn[] columns) {
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
					newColumns[count] = new TranslationInfo(destColumn
							.getText(), destColumn.getText(), null, true,
							dialog.getFromLanguage(), destColumn.getLang(),
							selectedCell, dialog.getTranslator());
					newColumns[count].setFromKey(selectedKey);
					newColumns[count].setToColumn(destColumn.getText());
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

		if (value.startsWith(LOCALIZATION_FILES_FOLDER)) {

			AndroidLocalizationSchema schema = new AndroidLocalizationSchema();
			String id = value.replace(LOCALIZATION_FILES_FOLDER, ""); //$NON-NLS-1$
			LocaleInfo info = schema.getLocaleInfoFromID(id);
			ProjectLocalizationManager manager = null;
			try {
				manager = LocalizationManager.getInstance()
						.getProjectLocalizationManager(project, false);
			} catch (IOException e) {
			}

			if ((info.getLocaleAttributes().size() > 0)
					|| (value
							.equalsIgnoreCase(AndroidLocalizationSchema.LOCALIZATION_FILES_FOLDER))) {
				StringLocalizationFile file = manager.getLocalizationProject()
						.getLocalizationFile(info);
				if (file != null && !file.isToBeDeleted()) {
					result = Messages.AndroidNewColumnProvider_Dialog_FileAlreadyExists;
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
	 * @seeorg.eclipse.sequoyah.localization.tools.extensions.classes.
	 * ILocalizationSchema #getEditorName()
	 */
	@Override
	public String getEditorName() {
		return Messages.AndroidStringEditorInput_EditorTooltip;
	}

	/**
	 * Create an Android string localization file. It's a XML which has the following
	 * format:
	 * 
	 * <?xml version="1.0" encoding="utf-8"?> <resources> <string
	 * name="KEY">VALUE</string> ... </resources>
	 * 
	 * @see org.eclipse.sequoyah.localization.tools.extensions.classes.ILocalizationSchema
	 *      #createStringFile(org.eclipse.sequoyah.localization.tools.datamodel.LocaleInfo)
	 */
	public void createStringFile(LocalizationFile localizationFile)
			throws SequoyahException{

		try {
			String filePath = localizationFile.getFile().getFullPath()
					.toOSString();

			if (!localizationFile.getFile().exists()) {
				localizationFile.getFile().getLocation();
				IPath fileToSave = null;
				if (localizationFile.getLocalizationProject() != null) {
					fileToSave = new Path(localizationFile
							.getLocalizationProject().getProject()
							.getLocation()
							+ filePath);
				} else {
					fileToSave = localizationFile.getFile().getLocation();
				}

				fileToSave.removeLastSegments(1).toFile().mkdirs();
				fileToSave.toFile().createNewFile();

				if (localizationFile.getLocalizationProject() != null) {
					IFile iFile = localizationFile.getLocalizationProject()
							.getProject().getFile(new Path(filePath));
					localizationFile.setFile(iFile);
				}
			}

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.newDocument();

			/*
			 * Create XML nodes
			 */
			Element resources = document.createElement(XML_RESOURCES_TAG);

			// Simple entries
			for (StringNode stringNode : ((StringLocalizationFile) localizationFile).getStringNodes()) {
				addSingleEntry(document, resources, stringNode);
			}

			// Arrays
			for (StringArray stringArray : ((StringLocalizationFile) localizationFile).getStringArrays()) {
				addArrayEntry(document, resources, stringArray);
			}

			document.appendChild(resources);

			saveXMLDocument(localizationFile.getFile().getLocation().toFile(),
					document);
			((AndroidStringLocalizationFile) localizationFile)
					.setSavedXMLDocument(document);
			localizationFile.getFile().getProject().refreshLocal(
					IResource.DEPTH_INFINITE, new NullProgressMonitor());
			// loadAllFiles(localizationFile.getLocalizationProject().getProject());

		} catch (Exception e) {
			throw new SequoyahException();
		}

	}

	/**
	 * Adds array entry into XML Android Localization file
	 * 
	 * @param document
	 * @param resources
	 * @param stringArray
	 */
	private void addArrayEntry(Document document, Element resources,
			StringArray stringArray) {
		Element array = document.createElement(XML_STRING_ARRAY_TAG);
		array.setAttribute(XML_STRING_ATTR_NAME, stringArray.getKey());
		for (StringNode stringNode : stringArray.getValues()) {
			createArrayItem(document, array, stringNode);
		}
		resources.appendChild(array);
	}

	private void createArrayItem(Document document, Element array,
			StringNode stringNode) {
		Element arrayItem = document.createElement(XML_STRING_ARRAY_ITEM_TAG);
		arrayItem.appendChild(document.createTextNode(stringNode.getValue()));
		array.appendChild(arrayItem);

		createOrUpdateComment(document, stringNode, arrayItem);
	}

	/**
	 * Adds single entry into XML Android Localization file
	 * 
	 * @param document
	 * @param resources
	 * @param stringNode
	 */
	private void addSingleEntry(Document document, Element resources,
			StringNode stringNode) {
		if (!stringNode.isArray()) {
			Element string = document.createElement(XML_STRING_TAG);
			string.setAttribute(XML_STRING_ATTR_NAME, stringNode.getKey());
			string.appendChild(document.createTextNode(stringNode.getValue()));

			createOrUpdateComment(document, stringNode, string);

			resources.appendChild(string);
		}
	}

	private void createOrUpdateComment(Document document,
			StringNode stringNode, Element string) {
		NodeComment nodeComment = stringNode.getNodeComment();
		if (nodeComment != null) {
			if (nodeComment.getComment() != null) {
				if (nodeComment.getComment().length() > 0) {
					Comment comment = document.createComment(nodeComment
							.getComment());
					string.appendChild(comment);
				}
			}
		}
	}

	/**
	 * Saves XML file into the file system
	 * 
	 * @param file
	 * @param document
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerConfigurationException
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 * @throws TransformerException
	 */
	private void saveXMLDocument(File file, Document document)
			throws TransformerFactoryConfigurationError,
			TransformerConfigurationException, FileNotFoundException,
			UnsupportedEncodingException, TransformerException {

		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		transformerFactory.setAttribute("indent-number", 4); //$NON-NLS-1$
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.METHOD, "xml"); //$NON-NLS-1$
		transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$

		FileOutputStream fileOutputStream = null;

		/*
		 * At this point, localization file has the correct file
		 */
		fileOutputStream = new FileOutputStream(file);

		try {
			StreamResult result = new StreamResult(new OutputStreamWriter(
					fileOutputStream, "UTF-8")); //$NON-NLS-1$

			DOMSource source = new DOMSource(document);

			transformer.transform(source, result);
		} finally {
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					// do nothing
				}
			}
		}

		unescapeEntity(file);

	}

	private String getXMLAsString(Document document) {
		String resultString = null;
		try {
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			transformerFactory.setAttribute("indent-number", 4); //$NON-NLS-1$
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.METHOD, "xml"); //$NON-NLS-1$
			transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$

			/*
			 * At this point, localization file has the correct file
			 */
			StringWriter writer = new StringWriter();

			StreamResult result = new StreamResult(writer); //$NON-NLS-1$

			DOMSource source = new DOMSource(document);

			transformer.transform(source, result);

			resultString = writer.toString();

			resultString = unescapeEntity(resultString);
		} catch (Exception e) {

		}

		return resultString;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.sequoyah.localization.tools.extensions.classes.
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
					+ ((AndroidLocaleAttribute) localeAttribute)
							.getDisplayName()
					+ ": " //$NON-NLS-1$
					+ ((AndroidLocaleAttribute) localeAttribute)
							.getDisplayValue();
		}

		if (result.length() == 0) {
			result = DEFAULT_LOCALE_TOOLTIP;
		}
		return result;
	}

	private String unescapeEntity(String content) {
		String returnContent = content;
		returnContent = returnContent.replaceAll("&lt;", //$NON-NLS-1$
				"<"); //$NON-NLS-1$
		returnContent = returnContent.replaceAll("&gt;", //$NON-NLS-1$
				">"); //$NON-NLS-1$
		returnContent = returnContent.replaceAll("&#13;", ""); //$NON-NLS-2$ //$NON-NLS-1$
		return returnContent;
	}

	/**
	 * Unescape some entities (such as &lt;) replacing it for the character form
	 * (such as <) in a file. This entities are automatically created by the
	 * Transform object, but we need the character form in the localization
	 * file.
	 * 
	 * @param the
	 *            text file whose entities are to be unescaped
	 */
	private void unescapeEntity(File file1) {
		File file2 = null;
		DataOutputStream dataOutput = null;
		FileInputStream inputStream = null;
		FileOutputStream fileOutputStream = null;
		BufferedReader reader = null;

		try {
			file2 = File.createTempFile("temp_localization", //$NON-NLS-1$
					"dom"); //$NON-NLS-1$
			FileUtil.copyFile(file1, file2);
			inputStream = new FileInputStream(file2);
			fileOutputStream = new FileOutputStream(file1);
			dataOutput = new DataOutputStream(fileOutputStream);
			reader = new BufferedReader(new InputStreamReader(inputStream,
					"UTF-8")); //$NON-NLS-1$
			String line = null;

			if (reader != null) {
				line = reader.readLine();
				while (line != null) {
					line = unescapeEntity(line);
					dataOutput.write(line.getBytes("UTF-8")); //$NON-NLS-1$
					String eol = System.getProperty("line.separator"); //$NON-NLS-1$
					dataOutput.write(eol.getBytes("UTF-8")); //$NON-NLS-1$
					line = reader.readLine();
				}
			}
		} catch (Exception e) {
			BasePlugin.logError("Error translating file.", e); //$NON-NLS-1$
		} finally {

			try {
				if (inputStream != null) {
					inputStream.close();
					inputStream = null;
				}
				if (fileOutputStream != null) {
					fileOutputStream.close();
					fileOutputStream = null;
				}
				if (reader != null) {
					reader.close();
					reader = null;
				}
			} catch (IOException e) {
				BasePlugin.logError(
						"Error cleaning up objects after translation", e); //$NON-NLS-1$
			}

			if (dataOutput != null) {
				try {
					dataOutput.close();
				} catch (IOException e) {
					BasePlugin.logError("Error closing file after translation", //$NON-NLS-1$
							e);
				}
				dataOutput = null;
			}

			if (file2 != null) {
				if (!file2.delete()) {
					file2.deleteOnExit();
				}
			}
			file1 = null;
		}
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

		localeAttributes.add(new AndroidLocaleAttribute(new Integer(123),
				AndroidLocaleAttributes.COUNTRY_CODE.ordinal()));
		localeAttributes.add(new AndroidLocaleAttribute(new Integer(000),
				AndroidLocaleAttributes.NETWORK_CODE.ordinal()));
		localeAttributes.add(new AndroidLocaleAttribute(null,
				AndroidLocaleAttributes.LANGUAGE.ordinal()));
		localeAttributes.add(new AndroidLocaleAttribute(null,
				AndroidLocaleAttributes.REGION.ordinal()));
		localeAttributes.add(new AndroidLocaleAttribute(null,
				AndroidLocaleAttributes.SCREEN_SIZE.ordinal()));
		localeAttributes.add(new AndroidLocaleAttribute(null,
				AndroidLocaleAttributes.SCREEN_ORIENTATION.ordinal()));
		localeAttributes.add(new AndroidLocaleAttribute(new Integer(12),
				AndroidLocaleAttributes.PIXEL_DENSITY.ordinal()));
		localeAttributes.add(new AndroidLocaleAttribute(null,
				AndroidLocaleAttributes.TOUCH_TYPE.ordinal()));
		localeAttributes.add(new AndroidLocaleAttribute(null,
				AndroidLocaleAttributes.KEYBOARD_STATE.ordinal()));
		localeAttributes.add(new AndroidLocaleAttribute(null,
				AndroidLocaleAttributes.TEXT_INPUT_METHOD.ordinal()));
		localeAttributes.add(new AndroidLocaleAttribute(null,
				AndroidLocaleAttributes.NAVIGATION_METHOD.ordinal()));
		localeAttributes.add(new AndroidLocaleAttribute(new Dimension(1, 1),
				AndroidLocaleAttributes.SCREEN_DIMENSION.ordinal()));

		return localeAttributes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.sequoyah.localization.tools.extensions.classes.
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
	 * @seeorg.eclipse.sequoyah.localization.tools.extensions.classes.
	 * ILocalizationSchema
	 * #getLocalizationFiles(org.eclipse.core.resources.IProject)
	 */
	@Override
	public Map<LocaleInfo, IFile> getLocalizationFiles(IProject project) {

		Map<LocaleInfo, IFile> localizationFiles = new LinkedHashMap<LocaleInfo, IFile>();
		boolean hasDefault = false;
		try {

			IResource resourcesFolder = project.findMember(RESOURCES_FOLDER);
			if ((resourcesFolder != null)
					&& (resourcesFolder instanceof IFolder)) {

				IResource[] folders = ((IFolder) resourcesFolder).members();
				if (folders != null) {
					/*
					 * Iterate in folders inside the resources folder
					 */
					for (IResource folder : folders) {
						if (folder.getName().startsWith(
								LOCALIZATION_FILES_FOLDER)) {
							IResource[] files = ((IFolder) folder).members();
							for (IResource file : files) {

								if ((file instanceof IFile)
										&& (isLocalizationFile((IFile) file))) {
									localizationFiles.put(
											getLocaleInfoFromPath(file
													.getProjectRelativePath()),
											(IFile) file);
									if (folder.getName().equals(
											LOCALIZATION_FILES_FOLDER)) {
										hasDefault = true;
									}
								}
							}
						}
					}
				}
			}
			// if a default file (typically values/strings.xml does not exists,
			// create it
			if (!hasDefault) {
				if (resourcesFolder instanceof IFolder) {
					IFolder folder = (IFolder) resourcesFolder;
					final IFolder valuesFolder = folder
							.getFolder(LOCALIZATION_FILES_FOLDER);
					try {
						if (!valuesFolder.exists()) {

							// try to create the folder
							PlatformUI.getWorkbench().getProgressService().run(
									false, false, new IRunnableWithProgress() {

										public void run(IProgressMonitor monitor)
												throws InvocationTargetException,
												InterruptedException {
											try {
												valuesFolder.create(true, true,
														monitor);
											} catch (CoreException e) {
												// do nothing
											}

										}
									});
						}
						// check if folder was created
						// create the default file
						if (valuesFolder.exists()) {
							IFile valuesFile = valuesFolder
									.getFile(LOCALIZATION_FILE_NAME);
							localizationFiles.put(
									getLocaleInfoFromPath(valuesFile
											.getProjectRelativePath()),
									valuesFile);
						}

					} catch (Exception e) {
						// do nothing, just exit
					}

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
	 * @seeorg.eclipse.sequoyah.localization.tools.extensions.classes.
	 * ILocalizationSchema #isLocalizationFile(org.eclipse.core.resources.IFile)
	 */
	@Override
	public boolean isLocalizationFile(IFile file) {

		boolean result = false;
		if (file != null) {
			if (file.getProjectRelativePath().toString().matches(
					LF_REGULAR_EXPRESSION)) {

				result = true;
			}
		}
		return result;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.sequoyah.localization.tools.extensions.classes.
	 * ILocalizationSchema #loadAllFiles()
	 */
	@Override
	public Map<LocaleInfo, LocalizationFile> loadAllFiles(IProject project)
			throws IOException {

		Map<LocaleInfo, LocalizationFile> filesMap = new LinkedHashMap<LocaleInfo, LocalizationFile>();

		Map<LocaleInfo, IFile> localizationFiles = getLocalizationFiles(project);

		for (Map.Entry<LocaleInfo, IFile> entry : localizationFiles.entrySet()) {
			filesMap.put(entry.getKey(), loadFile(entry.getValue()));
		}

		return filesMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.sequoyah.localization.tools.extensions.classes.
	 * ILocalizationSchema #loadFile(org.eclipse.core.resources.IFile)
	 */
	@Override
	public LocalizationFile loadFile(IFile file) throws IOException {
		LocalizationFile localizationFile = null;
		LocaleInfo localeInfo = getLocaleInfoFromPath(file.getFullPath());
		LocalizationFileBean bean = new LocalizationFileBean("", file, localeInfo, null, null);

		if (!file.exists()) {
			LocalizationFile tempFile = new LocalizationFile(bean);
			try {
				createStringFile(tempFile);
			} catch (SequoyahException e) {
				// do nothing
			}
		}

		try {
			InputStream inputStream = new FileInputStream(file.getLocation().toFile());
			DOMImplementation dimp = DOMImplementationRegistry.newInstance()
					.getDOMImplementation("XML 3.0"); //$NON-NLS-1$
			DOMImplementationLS dimpls = (DOMImplementationLS) dimp.getFeature("LS", "3.0"); //$NON-NLS-1$ //$NON-NLS-2$
			LSInput lsi = dimpls.createLSInput();
			LSParser lsp = dimpls.createLSParser(DOMImplementationLS.MODE_SYNCHRONOUS, 
					"http://www.w3.org/2001/XMLSchema"); //$NON-NLS-1$
			LSParserFilter filter = new LocalizationXMLParserFilter();
			lsp.setFilter(filter);
			lsi.setEncoding("UTF-8"); //$NON-NLS-1$
			lsi.setByteStream(inputStream);
			Document document = lsp.parse(lsi);
			
			localizationFile = new AndroidStringLocalizationFile(file, localeInfo,
					new ArrayList<StringNode>(), new ArrayList<StringArray>());
			updateLocalizationFileContent(localizationFile, document);

		} catch (Exception e) {
			throw new IOException(
					Messages.AndroidLocalizationSchema_Exception_CouldNotLoadFile
							+ file.getName() + ". " + e.getMessage()); //$NON-NLS-1$
		}

		return localizationFile;
	}

	/**
	 * Extracts the text with the right formatting from DOM XML representation
	 * 
	 * @param valueText
	 *            string to return, in the initial recursion set it as ""
	 * @param firstChildNode
	 *            node to start iterating over siblings
	 */
	private void getStringByNodes(StringBuffer valueText, Node firstChildNode) {
		Node auxNode = firstChildNode;
		while (auxNode != null) {
			// according to Android documentation it allows only <b>, <i>, <u>
			// formatting. However, we preserve any tags the user has included
			if (auxNode.getNodeName() != null
					&& (auxNode.getNodeType() == Node.ELEMENT_NODE)) {
				NamedNodeMap nodeAttributes = auxNode.getAttributes();
				String nodeAttributesText = ""; //$NON-NLS-1$
				for (int i = 0; i < nodeAttributes.getLength(); i++) {
					Node nodeAttribute = nodeAttributes.item(i);
					nodeAttributesText += " " + nodeAttribute.toString(); //$NON-NLS-1$
				}

				// case: sibling with formatting node
				valueText.append("<" + auxNode.getNodeName() //$NON-NLS-1$
						+ nodeAttributesText + ">"); //$NON-NLS-1$
				if (auxNode.hasChildNodes()) {
					// recursion (step): sibling has internal formatting nodes
					getStringByNodes(valueText, auxNode.getFirstChild());
				} else {
					// recursion (base case): only simple text inside sibling
					if (auxNode.getNodeType() != Node.COMMENT_NODE) {
						valueText.append(auxNode.getTextContent());
					}
				}
				valueText.append("</" //$NON-NLS-1$
						+ auxNode.getNodeName() + ">"); //$NON-NLS-1$
			} else {
				// recursion (base case): simple text in the sibling
				if (auxNode.getNodeType() != Node.COMMENT_NODE) {
					valueText.append(auxNode.getTextContent());
				}
			}
			auxNode = auxNode.getNextSibling();
		}
	}

	/**
	 * Update the XML representation of the file
	 * 
	 * @param localizationFile
	 * @param document
	 * @throws SequoyahException
	 */
	private void updateFile(StringLocalizationFile localizationFile, Document document)
			throws SequoyahException {

		AndroidStringLocalizationFile androidLocalizationFile = (AndroidStringLocalizationFile) localizationFile;
		if (document == null) {
			// file not created yet, do it
			createStringFile(androidLocalizationFile);
			document = androidLocalizationFile.getSavedXMLDocument();
		} else {
			// file already created, update
			// update nodes on XML Document according to LocalizationFile model
			Map<String, StringNode> singleStringsToUpdateOrAdd = new HashMap<String, StringNode>();
			for (StringNode stringNode : localizationFile.getStringNodes()) {
				if (!stringNode.isArray()) {
					singleStringsToUpdateOrAdd.put(stringNode.getKey(),
							stringNode);
				}
			}
			Map<String, StringArray> arrayStringsToUpdateOrAdd = new HashMap<String, StringArray>();
			for (StringArray stringArray : localizationFile.getStringArrays()) {
				arrayStringsToUpdateOrAdd
						.put(stringArray.getKey(), stringArray);
			}

			NodeList resourcesList = document.getElementsByTagName("resources"); //$NON-NLS-1$
			// if there is no resource tag, add at least one
			if (resourcesList.getLength() == 0) {
				Element resources = document.createElement(XML_RESOURCES_TAG);
				document.appendChild(resources);
				resourcesList = document.getElementsByTagName("resources"); //$NON-NLS-1$
			}
			for (int i = 0; i < resourcesList.getLength(); i++) {
				Element resource = (Element) resourcesList.item(i);
				// remove nodes
				visitToRemoveDOMChildren(
						document,
						resource.getFirstChild(),
						"name", //$NON-NLS-1$
						androidLocalizationFile.getSingleEntryToRemove(),
						androidLocalizationFile.getArrayEntryToRemove(),
						androidLocalizationFile.getArrayItemsToRemove());
				// update nodes
				visitToUpdateDOMChildren(document, resource.getFirstChild(),
						"name", //$NON-NLS-1$
						singleStringsToUpdateOrAdd, arrayStringsToUpdateOrAdd);
				// add new nodes (append in the end of file)
				visitToAddDOMChildren(document, singleStringsToUpdateOrAdd,
						arrayStringsToUpdateOrAdd, resource);
			}

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.sequoyah.localization.tools.extensions.classes.
	 * ILocalizationSchema #
	 * updateFile(org.eclipse.sequoyah.localization.tools.datamodel
	 * .LocalizationFile)
	 */
	@Override
	public void updateFile(LocalizationFile localizationFile)
			throws SequoyahException {
		AndroidStringLocalizationFile androidLocalizationFile = (AndroidStringLocalizationFile) localizationFile;
		Document document = androidLocalizationFile.getSavedXMLDocument();
		updateFile(androidLocalizationFile, document);
		if (document == null) {
			document = androidLocalizationFile.getSavedXMLDocument();
		}
		// save modified document
		try {
			saveXMLDocument(localizationFile.getFile().getLocation().toFile(),
					document);
		} catch (Exception e) {
			SequoyahException sqE = new SequoyahException();
			sqE.setStackTrace(e.getStackTrace());
			throw sqE;
		}
		localizationFile.setDirty(false);
	}

	private void visitToAddDOMChildren(Document document,
			Map<String, StringNode> singleStringsToUpdateOrAdd,
			Map<String, StringArray> arrayStringsToUpdateOrAdd, Element resource) {
		for (Map.Entry<String, StringNode> singleEntry : singleStringsToUpdateOrAdd
				.entrySet()) {
			StringNode stringNode = singleEntry.getValue();
			addSingleEntry(document, resource, stringNode);
		}
		for (Map.Entry<String, StringArray> arrayEntry : arrayStringsToUpdateOrAdd
				.entrySet()) {
			StringArray stringArray = arrayEntry.getValue();
			addArrayEntry(document, resource, stringArray);
		}
	}

	/**
	 */
	public void visitToRemoveDOMChildren(Document document, Node visitingNode,
			String attrName, Map<String, StringNode> singleStringsToRemove,
			Map<String, StringArray> arrayStringsToRemove,
			Map<String, StringNode> arrayItemsToRemove) {
		while (visitingNode != null) {
			Node nextNodeToVisit = visitingNode.getNextSibling();
			if (visitingNode.getNodeType() == Node.ELEMENT_NODE) {
				Attr attribute = getAttribute((Element) visitingNode, attrName);
				// check LocalizationFile to get new value for attribute
				if (attribute != null) {
					String keyName = attribute.getValue();
					// try to find as single string entry
					StringNode foundStringNode = singleStringsToRemove
							.get(keyName);
					if (foundStringNode != null) {
						// remove single entry
						visitingNode.getParentNode().removeChild(visitingNode);
					} else {
						// not found single string - try to find as entire array
						// entry
						StringArray foundStringArray = arrayStringsToRemove
								.get(keyName);
						if (foundStringArray != null) {
							// remove array entry
							visitingNode.getParentNode().removeChild(
									visitingNode);
						} else {
							// not found entire array entry - try to find as
							// item
							// inside array
							NodeList items = visitingNode.getChildNodes();
							int itemsIndex = 0;
							while (itemsIndex < items.getLength()) {
								Object obj = items.item(itemsIndex);
								if (obj instanceof Element) {
									Element item = (Element) obj;
									DecimalFormat formatter = new DecimalFormat(
											"000"); //$NON-NLS-1$
									String virtualKey = keyName + "_" //$NON-NLS-1$
											+ formatter.format(itemsIndex);
									StringNode arrayItem = arrayItemsToRemove
											.get(virtualKey);
									if (arrayItem != null) {
										// remove array item
										item.getParentNode().removeChild(item);
									}
								}
								itemsIndex++;
							}
						}
					}
				}
			}
			visitingNode = nextNodeToVisit;
		}
	}

	/**
	 * 
	 * Visit all child in the DOM tree, as an attribute is found, it gets the
	 * corresponding item on LocalizationFile and updates the values
	 * 
	 * @param visitingNode
	 * @param attrName
	 * @param singleStringsToUpdateOrAdd
	 *            map with single items to update/add (in the end of visit, only
	 *            add items should remain)
	 * @param arrayStringsToUpdateOrAdd
	 *            map with array item to update/add (in the end of visit, only
	 *            add item should
	 */
	public void visitToUpdateDOMChildren(Document document, Node visitingNode,
			String attrName,
			Map<String, StringNode> singleStringsToUpdateOrAdd,
			Map<String, StringArray> arrayStringsToUpdateOrAdd) {
		while (visitingNode != null) {
			if (visitingNode.getNodeType() == Node.ELEMENT_NODE) {
				Attr attribute = getAttribute((Element) visitingNode, attrName);
				if (attribute != null) {
					// check LocalizationFile to get new value for attribute
					String keyName = attribute.getValue();
					// try to find as single string entry
					StringNode foundStringNode = singleStringsToUpdateOrAdd
							.get(keyName);
					if (foundStringNode != null) {
						// update single entry
						String newSingleEntryValue = foundStringNode.getValue();
						visitingNode.setTextContent(newSingleEntryValue);
						createOrUpdateComment(document, foundStringNode,
								(Element) visitingNode);
						// remove item from map (it was already updated and it
						// does
						// not need to add in the end)
						singleStringsToUpdateOrAdd.remove(keyName);
					} else {
						// not found single string - try to find as array entry
						StringArray foundStringArray = arrayStringsToUpdateOrAdd
								.get(keyName);
						if (foundStringArray != null) {
							NodeList items = visitingNode.getChildNodes();
							List<StringNode> nodes = foundStringArray
									.getValues();
							int itemsIndex = 0;
							int nodesIndex = 0;
							while (itemsIndex < items.getLength()
									&& nodesIndex < nodes.size()) {
								Object obj = items.item(itemsIndex);
								if (obj instanceof Element) {
									Element item = (Element) obj;
									StringNode nodeItem = nodes.get(nodesIndex);
									// update array entry
									String newArrayItemValue = nodeItem
											.getValue();
									item.setTextContent(newArrayItemValue);
									createOrUpdateComment(document, nodeItem,
											item);
									// remove item from map (it was already
									// updated
									// and it does not need to add in the end)
									arrayStringsToUpdateOrAdd.remove(keyName);
									nodesIndex++;
								}
								itemsIndex++;
							}
							while (nodesIndex < nodes.size()) {
								// add new items into exiting array
								if (visitingNode instanceof Element) {
									Element arrayElement = (Element) visitingNode;
									StringNode stringNode = nodes
											.get(nodesIndex);
									createArrayItem(document, arrayElement,
											stringNode);
									nodesIndex++;
								}
							}

						}
					}
				}
			}
			visitingNode = visitingNode.getNextSibling();
		}
	}

	private boolean isKnownNode(Node visitingNode) {
		return visitingNode.getNodeName().equals(XML_STRING_TAG)
				|| visitingNode.getNodeName().equals(XML_STRING_ARRAY_TAG);
	}

	/**
	 * Get Attribute with the given name
	 * 
	 * @param el
	 *            visiting element
	 * @param attrName
	 *            attribute name to search
	 * @return attribute
	 */
	public static Attr getAttribute(Element el, String attrName) {
		Attr attr = el.getAttributeNode(attrName);
		return attr;
	}

	/**
	 * Given a localization file path, returns the language information
	 * (attributes) of this localization file according to the file name.
	 * 
	 * @return LanguageInfo object with the attributes of this language
	 */
	private LocaleInfo getLocaleInfoFromPath(IPath path) {

		IPath folder = path;
		folder = path.removeLastSegments(1);
		String folderName = folder.lastSegment();
		String id = folderName.replace(LOCALIZATION_FILES_FOLDER, ""); //$NON-NLS-1$

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
			result = RESOURCES_FOLDER + File.separator
					+ LOCALIZATION_FILES_FOLDER + QUALIFIER_SEP
					+ getLocaleID(lang) + File.separator
					+ LOCALIZATION_FILE_NAME;
		} else {
			// It is a default location file (no language qualifier)
			result = RESOURCES_FOLDER + File.separator
					+ LOCALIZATION_FILES_FOLDER + File.separator
					+ LOCALIZATION_FILE_NAME;

		}
		return result;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.sequoyah.localization.tools.extensions.classes.
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
			if (((AndroidLocaleAttribute) localeAttribute).isSet()) {
				if (localeID.length() != 0) {
					localeID = localeID + QUALIFIER_SEP;
				}
				localeID = localeID + localeAttribute.getFolderValue();
			}
		}
		return localeID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.sequoyah.localization.tools.extensions.classes.
	 * ILocalizationSchema #getLocaleInfoFromID(java.lang.String)
	 */
	@Override
	public LocaleInfo getLocaleInfoFromID(String ID) {

		LocaleInfo result = new LocaleInfo();

		String[] segments = ID.split(QUALIFIER_SEP);
		int lastQualifier = -1;

		List<LocaleAttribute> localeAttributes = new ArrayList<LocaleAttribute>();

		for (int i = 1; i < segments.length; i++) {

			if (segments[i].equals("")) { //$NON-NLS-1$
				// Do nothiing
			} else if (isCountryCodeSegment(segments[i])
					&& (lastQualifier < AndroidLocaleAttributes.COUNTRY_CODE
							.ordinal())) {
				lastQualifier = AndroidLocaleAttributes.COUNTRY_CODE.ordinal();
				localeAttributes.add(new AndroidLocaleAttribute(segments[i],
						AndroidLocaleAttributes.COUNTRY_CODE.ordinal()));
			} else if (isNetworkCodeSegment(segments[i])
					&& (lastQualifier < AndroidLocaleAttributes.NETWORK_CODE
							.ordinal())) {
				lastQualifier = AndroidLocaleAttributes.NETWORK_CODE.ordinal();
				localeAttributes.add(new AndroidLocaleAttribute(segments[i],
						AndroidLocaleAttributes.NETWORK_CODE.ordinal()));
			} else if (isLanguageSegment(segments[i])
					&& (lastQualifier < AndroidLocaleAttributes.LANGUAGE
							.ordinal())) {
				lastQualifier = AndroidLocaleAttributes.LANGUAGE.ordinal();
				localeAttributes.add(new AndroidLocaleAttribute(segments[i],
						AndroidLocaleAttributes.LANGUAGE.ordinal()));
			} else if (isRegionSegment(segments[i])
					&& (lastQualifier < AndroidLocaleAttributes.REGION
							.ordinal())) {
				lastQualifier = AndroidLocaleAttributes.REGION.ordinal();
				localeAttributes.add(new AndroidLocaleAttribute(segments[i],
						AndroidLocaleAttributes.REGION.ordinal()));
			} else if (isScreenSizeSegment(segments[i])
					&& lastQualifier < AndroidLocaleAttributes.SCREEN_SIZE
							.ordinal()) {
				lastQualifier = AndroidLocaleAttributes.SCREEN_SIZE.ordinal();
				localeAttributes.add(new AndroidLocaleAttribute(segments[i],
						AndroidLocaleAttributes.SCREEN_SIZE.ordinal()));
			} else if (isOrientationSegment(segments[i])
					&& (lastQualifier < AndroidLocaleAttributes.SCREEN_ORIENTATION
							.ordinal())) {
				lastQualifier = AndroidLocaleAttributes.SCREEN_ORIENTATION
						.ordinal();
				localeAttributes.add(new AndroidLocaleAttribute(segments[i],
						AndroidLocaleAttributes.SCREEN_ORIENTATION.ordinal()));
			} else if (isPixelDensitySegment(segments[i])
					&& (lastQualifier < AndroidLocaleAttributes.PIXEL_DENSITY
							.ordinal())) {
				lastQualifier = AndroidLocaleAttributes.PIXEL_DENSITY.ordinal();
				localeAttributes.add(new AndroidLocaleAttribute(segments[i],
						AndroidLocaleAttributes.PIXEL_DENSITY.ordinal()));
			} else if (isTouchTypeSegment(segments[i])
					&& (lastQualifier < AndroidLocaleAttributes.TOUCH_TYPE
							.ordinal())) {
				lastQualifier = AndroidLocaleAttributes.TOUCH_TYPE.ordinal();
				localeAttributes.add(new AndroidLocaleAttribute(segments[i],
						AndroidLocaleAttributes.TOUCH_TYPE.ordinal()));
			} else if (isKeyboardStateSegment(segments[i])
					&& (lastQualifier < AndroidLocaleAttributes.KEYBOARD_STATE
							.ordinal())) {
				lastQualifier = AndroidLocaleAttributes.KEYBOARD_STATE
						.ordinal();
				localeAttributes.add(new AndroidLocaleAttribute(segments[i],
						AndroidLocaleAttributes.KEYBOARD_STATE.ordinal()));
			} else if (isTextInputSegment(segments[i])
					&& (lastQualifier < AndroidLocaleAttributes.TEXT_INPUT_METHOD
							.ordinal())) {
				lastQualifier = AndroidLocaleAttributes.TEXT_INPUT_METHOD
						.ordinal();
				localeAttributes.add(new AndroidLocaleAttribute(segments[i],
						AndroidLocaleAttributes.TEXT_INPUT_METHOD.ordinal()));
			} else if (isNavigationSegment(segments[i])
					&& (lastQualifier < AndroidLocaleAttributes.NAVIGATION_METHOD
							.ordinal())) {
				lastQualifier = AndroidLocaleAttributes.NAVIGATION_METHOD
						.ordinal();
				localeAttributes.add(new AndroidLocaleAttribute(segments[i],
						AndroidLocaleAttributes.NAVIGATION_METHOD.ordinal()));
			} else if (isDimensionSegment(segments[i])
					&& (lastQualifier < AndroidLocaleAttributes.SCREEN_DIMENSION
							.ordinal())) {
				lastQualifier = AndroidLocaleAttributes.SCREEN_DIMENSION
						.ordinal();
				localeAttributes.add(new AndroidLocaleAttribute(segments[i],
						AndroidLocaleAttributes.SCREEN_DIMENSION.ordinal()));
			} else if (isAPIVersionSegment(segments[i])
					&& (lastQualifier < AndroidLocaleAttributes.API_VERSION
							.ordinal())) {
				lastQualifier = AndroidLocaleAttributes.API_VERSION.ordinal();
				localeAttributes.add(new AndroidLocaleAttribute(segments[i],
						AndroidLocaleAttributes.API_VERSION.ordinal()));
			} else {
				localeAttributes = new ArrayList<LocaleAttribute>();
			}

		}

		result.setLocaleAttributes(localeAttributes);

		return result;
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

	private boolean isNetworkCodeSegment(String value) {
		return value.startsWith("mnc"); //$NON-NLS-1$

	}

	private boolean isLanguageSegment(String value) {
		return (value.length() == 2);

	}

	private boolean isRegionSegment(String value) {
		return ((value.startsWith("r") && (value.length() == 3))); //$NON-NLS-1$

	}

	private boolean isOrientationSegment(String value) {
		return ((value.equalsIgnoreCase("port") //$NON-NLS-1$
				|| value.equalsIgnoreCase("land") || value //$NON-NLS-1$
				.equalsIgnoreCase("square"))); //$NON-NLS-1$

	}

	private boolean isPixelDensitySegment(String value) {
		return (value.endsWith("dpi")); //$NON-NLS-1$

	}

	private boolean isTouchTypeSegment(String value) {
		return ((value.equalsIgnoreCase("notouch") //$NON-NLS-1$
				|| value.equalsIgnoreCase("stylus") || value //$NON-NLS-1$
				.equalsIgnoreCase("finger"))); //$NON-NLS-1$
	}

	private boolean isKeyboardStateSegment(String value) {
		return ((value.equalsIgnoreCase("keysexposed") || value //$NON-NLS-1$
				.equalsIgnoreCase("keyshidden"))); //$NON-NLS-1$

	}

	private boolean isTextInputSegment(String value) {
		return ((value.equalsIgnoreCase("nokeys") //$NON-NLS-1$
				|| value.equalsIgnoreCase("qwerty") || value //$NON-NLS-1$
				.equalsIgnoreCase("12key"))); //$NON-NLS-1$

	}

	private boolean isNavigationSegment(String value) {
		return ((value.equalsIgnoreCase("dpad") //$NON-NLS-1$
				|| value.equalsIgnoreCase("trackball") //$NON-NLS-1$
				|| value.equalsIgnoreCase("wheel") || value //$NON-NLS-1$
				.equalsIgnoreCase("nonav"))); //$NON-NLS-1$

	}

	private boolean isDimensionSegment(String value) {
		boolean result = false;
		if (value.contains("x")) { //$NON-NLS-1$
			String[] ints = value.split("x"); //$NON-NLS-1$
			if (ints.length == 2) {
				result = true;
			}
		}
		return result;

	}

	private boolean isCountryCodeSegment(String value) {
		return value.startsWith("mcc"); //$NON-NLS-1$

	}

	private boolean isScreenSizeSegment(String value) {
		return value.equalsIgnoreCase("large") //$NON-NLS-1$
				|| value.equalsIgnoreCase("normal") //$NON-NLS-1$
				|| value.equalsIgnoreCase("small"); //$NON-NLS-1$
	}

	private boolean isAPIVersionSegment(String value) {
		return value.startsWith("v"); //$NON-NLS-1$
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
		return LocalizationFileFactory.getInstance().createLocalizationFile(bean);
	}

	@Override
	public String getLocalizationFileContent(LocalizationFile locFile) {
		String text = null;
		if (locFile instanceof AndroidStringLocalizationFile) {
			AndroidStringLocalizationFile localizationFile = (AndroidStringLocalizationFile) locFile;
			try {
				updateFile(localizationFile, localizationFile
						.getSavedXMLDocument());
			} catch (SequoyahException e) {

			}
			text = getXMLAsString(localizationFile.getSavedXMLDocument());
		}
		return text;
	}

	private void updateLocalizationFileContent(
			StringLocalizationFile localizationFile, Document document) {
		List<StringNode> stringNodes = new ArrayList<StringNode>();
		List<StringArray> stringArrays = new ArrayList<StringArray>();

		/*
		 * Get string nodes
		 */
		NodeList stringNodeList = document.getElementsByTagName(XML_STRING_TAG);

		String key = null;
		String value = null;
		for (int i = 0; i < stringNodeList.getLength(); i++) {
			Element stringNode = (Element) stringNodeList.item(i);
			key = stringNode.getAttributeNode(XML_STRING_ATTR_NAME)
					.getNodeValue();
			String comment = null;
			if (stringNode.hasChildNodes()) {
				NodeList childs = stringNode.getChildNodes();
				for (int j = 0; j < childs.getLength(); j++) {
					Node childN = childs.item(j);
					if (childN.getNodeType() == COMMENT_NODE) {
						comment = childN.getNodeValue();
					}
				}

			}
			// get formatted text from single (non-array) item
			Node auxNode = stringNode.getFirstChild();
			StringBuffer valueText = new StringBuffer();
			getStringByNodes(valueText, auxNode);
			value = valueText.toString();

			stringNode.toString();
			StringNode stringNodeObj = new StringNode(key, value);
			if (comment != null) {
				NodeComment nodeComment = new NodeComment();
				nodeComment.setComment(comment);
				stringNodeObj.setNodeComment(nodeComment);
			}
			stringNodes.add(stringNodeObj);
		}

		/*
		 * Get array nodes
		 */
		NodeList arrayNodeList = document
				.getElementsByTagName(XML_STRING_ARRAY_TAG);
		String arrayKey = null;
		String arrayValue = null;
		for (int i = 0; i < arrayNodeList.getLength(); i++) {
			Element arrayNode = (Element) arrayNodeList.item(i);
			arrayKey = arrayNode.getAttributeNode(XML_STRING_ATTR_NAME)
					.getNodeValue();
			StringArray stringArray = new StringArray(arrayKey);
			if (arrayNode.hasChildNodes()) {
				NodeList arrayItems = arrayNode
						.getElementsByTagName(XML_STRING_ARRAY_ITEM_TAG);
				for (int j = 0; j < arrayItems.getLength(); j++) {
					Node childN = arrayItems.item(j);

					// get formatted text from array item
					Node auxNode = childN.getFirstChild();
					StringBuffer valueText = new StringBuffer();
					getStringByNodes(valueText, auxNode);
					arrayValue = valueText.toString();

					StringNode newNode = stringArray.addValue(arrayValue);

					// comments
					String comment = null;
					if (childN.hasChildNodes()) {
						NodeList childs = childN.getChildNodes();
						for (int k = 0; k < childs.getLength(); k++) {
							Node commentNode = childs.item(k);
							if (commentNode.getNodeType() == COMMENT_NODE) {
								comment = commentNode.getNodeValue();
							}
						}

					}

					if (comment != null) {
						NodeComment nodeComment = new NodeComment();
						nodeComment.setComment(comment);
						newNode.setNodeComment(nodeComment);
					}

				}

			}
			stringArrays.add(stringArray);
		}

		localizationFile.setStringNodes(stringNodes);
		localizationFile.setStringArrays(stringArrays);

		((AndroidStringLocalizationFile) localizationFile)
				.setSavedXMLDocument(document);
	}

	@Override
	public void updateLocalizationFileContent(
			LocalizationFile localizationFile, String content)
			throws SequoyahException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			ByteArrayInputStream byteInputStream = new ByteArrayInputStream(
					content.getBytes("UTF-8")); //$NON-NLS-1$
			Document document = builder.parse(byteInputStream);
			updateLocalizationFileContent((StringLocalizationFile) localizationFile, document);
		} catch (Exception e) {
			SequoyahExceptionStatus status = new SequoyahExceptionStatus(
					IStatus.ERROR, AndroidLocalizationPlugin.PLUGIN_ID, 0,
					Messages.AndroidLocalizationSchema_6
							+ localizationFile.getFile().getFullPath(), e);
			throw new SequoyahException(status);
		}

	}
}
