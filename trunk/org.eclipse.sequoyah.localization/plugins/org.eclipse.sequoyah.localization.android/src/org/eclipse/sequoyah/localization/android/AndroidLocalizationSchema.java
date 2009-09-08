/********************************************************************************
 * Copyright (c) 2009 Motorola Inc.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Vinicius Hernandes (Motorola)
 * Matheus Tait Lima (Eldorado)
 * 
 * Contributors:
 * name (company) - description.
 ********************************************************************************/
package org.eclipse.tml.localization.android;

import static org.w3c.dom.Node.COMMENT_NODE;

import java.awt.Dimension;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.tml.common.utilities.exception.TmLException;
import org.eclipse.tml.localization.android.i18n.Messages;
import org.eclipse.tml.localization.stringeditor.datatype.ColumnInfo;
import org.eclipse.tml.localization.tools.datamodel.LocaleAttribute;
import org.eclipse.tml.localization.tools.datamodel.LocaleInfo;
import org.eclipse.tml.localization.tools.datamodel.LocalizationFile;
import org.eclipse.tml.localization.tools.datamodel.StringNode;
import org.eclipse.tml.localization.tools.datamodel.StringNodeComment;
import org.eclipse.tml.localization.tools.editor.StringEditorInput;
import org.eclipse.tml.localization.tools.extensions.classes.ILocalizationSchema;
import org.eclipse.tml.localization.tools.managers.ProjectLocalizationManager;
import org.eclipse.ui.PlatformUI;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The Android Localization Schema provides the localization schema for Android
 * projects.
 */
public class AndroidLocalizationSchema extends ILocalizationSchema {

	/*
	 * Android resources info
	 */
	private final String RESOURCES_FOLDER = "res"; //$NON-NLS-1$

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

	private final String XML_STRING_ATTR_NAME = "name"; //$NON-NLS-1$

	private final String NEW_COLUMN_TITLE = Messages.AndroidNewColumnProvider_NewColumnTitle;

	private final String NEW_COLUMN_DESCRIPTION = Messages.AndroidNewColumnProvider_NewColumnDescription;

	private final String NEW_COLUMN_TEXT = AndroidLocalizationSchema.LOCALIZATION_FILES_FOLDER;

	private final String NEW_COLUMN_INVALID_ID = Messages.AndroidNewColumnProvider_InvalidNewColumID;

	private String MANDATORY_ID = AndroidLocalizationSchema.LOCALIZATION_FILES_FOLDER;

	private final String QUALIFIER_SEP = "-";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.tml.localization.tools.extensions.classes.ILocalizationSchema
	 * #isValueValid(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public IStatus isValueValid(String localeID, String key, String value) {
		Status result = new Status(IStatus.OK,
				AndroidLocalizationPlugin.PLUGIN_ID, "");

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
	 * @see
	 * org.eclipse.tml.localization.tools.extensions.classes.ILocalizationSchema
	 * #promptCollumnName()
	 */
	@Override
	public ColumnInfo promptCollumnName() {
		ColumnInfo newColumn = null;

		// Ask user for the ID
		InputDialog dialog = new InputDialog(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(), NEW_COLUMN_TITLE,
				NEW_COLUMN_DESCRIPTION, NEW_COLUMN_TEXT, //$NON-NLS-2$
				new IInputValidator() {

					public String isValid(String newText) {
						return isValid2(newText);
					};
				});

		if (dialog.open() == IDialogConstants.OK_ID) {
			newColumn = new ColumnInfo(dialog.getValue(), dialog.getValue(),
					null, true);
		}

		return newColumn;
	}

	/**
	 * Checks if the current input is a valid column name for a new column
	 * 
	 * @param value
	 *            the new column name
	 * @return true if it is a valid column name
	 */
	private String isValid2(String value) {
		String result = null;
		AndroidLocalizationSchema schema = new AndroidLocalizationSchema();
		String id = value.replace(LOCALIZATION_FILES_FOLDER, ""); //$NON-NLS-1$
		LocaleInfo info = schema.getLocaleInfoFromID(id);
		ProjectLocalizationManager manager = StringEditorInput
				.getProjectLocalizationManager();

		if ((info.getLocaleAttributes().size() > 0)
				|| (value
						.equalsIgnoreCase(AndroidLocalizationSchema.LOCALIZATION_FILES_FOLDER))) {
			LocalizationFile file = manager.getLocalizationProject()
					.getLocalizationFile(info);
			if (file != null) {
				result = Messages.AndroidNewColumnProvider_Dialog_FileAlreadyExists;

			}
		} else {
			result = NEW_COLUMN_INVALID_ID;
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.tml.localization.tools.extensions.classes.ILocalizationSchema
	 * #getEditorName()
	 */
	@Override
	public String getEditorName() {
		return Messages.AndroidStringEditorInput_EditorTooltip;
	}

	/**
	 * Create an Android localization file. It's a XML which has the following
	 * format:
	 * 
	 * <?xml version="1.0" encoding="utf-8"?> <resources> <string
	 * name="KEY">VALUE</string> ... </resources>
	 * 
	 * @see org.eclipse.tml.localization.tools.extensions.classes.ILocalizationSchema
	 *      #createFile(org.eclipse.tml.localization.tools.datamodel.LocaleInfo)
	 */
	@Override
	public void createFile(LocalizationFile localizationFile)
			throws TmLException {

		try {
			String filePath = localizationFile.getFile().getFullPath()
					.toOSString();

			if (!localizationFile.getFile().exists()) {
				localizationFile.getFile().getLocation();
				IPath fileToSave = new Path(localizationFile
						.getLocalizationProject().getProject().getLocation()
						+ filePath);

				fileToSave.removeLastSegments(1).toFile().mkdirs();
				fileToSave.toFile().createNewFile();
				ResourcesPlugin.getWorkspace().getRoot().refreshLocal(
						IResource.DEPTH_INFINITE, new NullProgressMonitor());

				IFile iFile = localizationFile.getLocalizationProject()
						.getProject().getFile(new Path(filePath));
				localizationFile.setFile(iFile);
			}

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.newDocument();

			/*
			 * Create XML nodes
			 */
			Element resources = document.createElement(XML_RESOURCES_TAG);

			for (StringNode stringNode : localizationFile.getStringNodes()) {
				Element string = document.createElement(XML_STRING_TAG);
				string.setAttribute(XML_STRING_ATTR_NAME, stringNode.getKey());
				string.appendChild(document.createTextNode(stringNode
						.getValue()));

				StringNodeComment nodeComment = stringNode
						.getStringNodeComment();
				if (nodeComment != null) {
					if (nodeComment.getComment() != null) {
						if (nodeComment.getComment().length() > 0) {
							Comment comment = document
									.createComment(nodeComment.getComment());
							string.appendChild(comment);
						}
					}
				}

				resources.appendChild(string);
			}
			document.appendChild(resources);

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
			fileOutputStream = new FileOutputStream(localizationFile.getFile()
					.getLocation().toFile());

			StreamResult result = new StreamResult(new OutputStreamWriter(
					fileOutputStream, "UTF-8")); //$NON-NLS-1$

			DOMSource source = new DOMSource(document);

			transformer.transform(source, result);

		} catch (Exception e) {
			throw new TmLException();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.tml.localization.tools.extensions.classes.ILocalizationSchema
	 * #getLocaleToolTip(org.eclipse.core.runtime.Path)
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.tml.localization.tools.extensions.classes.ILocalizationSchema
	 * #getLocaleAttributes()
	 */
	@Override
	public List<LocaleAttribute> getLocaleAttributes() {
		List<LocaleAttribute> localeAttributes = new ArrayList<LocaleAttribute>();

		localeAttributes.add(new AndroidLocaleAttribute(new Integer(123),
				AndroidLocaleAttribute.INDEX_COUNTRY_CODE));
		localeAttributes.add(new AndroidLocaleAttribute(new Integer(000),
				AndroidLocaleAttribute.INDEX_NETWORK_CODE));
		localeAttributes.add(new AndroidLocaleAttribute("pt", //$NON-NLS-1$
				AndroidLocaleAttribute.INDEX_LANGUAGE));
		localeAttributes.add(new AndroidLocaleAttribute("br", //$NON-NLS-1$
				AndroidLocaleAttribute.INDEX_REGION));
		localeAttributes.add(new AndroidLocaleAttribute(null,
				AndroidLocaleAttribute.INDEX_SCREEN_ORIENTATION));
		localeAttributes.add(new AndroidLocaleAttribute(new Integer(12),
				AndroidLocaleAttribute.INDEX_PIXEL_DENSITY));
		localeAttributes.add(new AndroidLocaleAttribute(null,
				AndroidLocaleAttribute.INDEX_TOUCH_TYPE));
		localeAttributes.add(new AndroidLocaleAttribute(null,
				AndroidLocaleAttribute.INDEX_KEYBOARD_STATE));
		localeAttributes.add(new AndroidLocaleAttribute(null,
				AndroidLocaleAttribute.INDEX_TEXT_INPUT_METHOD));
		localeAttributes.add(new AndroidLocaleAttribute(null,
				AndroidLocaleAttribute.INDEX_NAVIGATION_METHOD));
		localeAttributes.add(new AndroidLocaleAttribute(new Dimension(1, 1),
				AndroidLocaleAttribute.INDEX_SCREEN_DIMENSION));

		return localeAttributes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.tml.localization.tools.extensions.classes.ILocalizationSchema
	 * #getLocalizationFileExtensions()
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
	 * @see
	 * org.eclipse.tml.localization.tools.extensions.classes.ILocalizationSchema
	 * #getLocalizationFiles(org.eclipse.core.resources.IProject)
	 */
	@Override
	public Map<LocaleInfo, IFile> getLocalizationFiles(IProject project) {

		Map<LocaleInfo, IFile> localizationFiles = new LinkedHashMap<LocaleInfo, IFile>();

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
								}
							}
						}

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
	 * @see
	 * org.eclipse.tml.localization.tools.extensions.classes.ILocalizationSchema
	 * #isLocalizationFile(org.eclipse.core.resources.IFile)
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
	 * @see
	 * org.eclipse.tml.localization.tools.extensions.classes.ILocalizationSchema
	 * #loadAllFiles()
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
	 * @see
	 * org.eclipse.tml.localization.tools.extensions.classes.ILocalizationSchema
	 * #loadFile(org.eclipse.core.resources.IFile)
	 */
	@Override
	public LocalizationFile loadFile(IFile file) throws IOException {

		LocalizationFile localizationFile = null;
		LocaleInfo localeInfo = getLocaleInfoFromPath(file.getFullPath());
		List<StringNode> stringNodes = new ArrayList<StringNode>();

		try {

			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(new File(file.getLocation()
					.toString()));

			/*
			 * Get string nodes
			 */
			NodeList stringNodeList = document
					.getElementsByTagName(XML_STRING_TAG);

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
						Node childN = (Node) childs.item(j);
						if (childN.getNodeType() == COMMENT_NODE) {
							comment = childN.getNodeValue();
						}
					}

				}
				value = stringNode.getTextContent();

				StringNode stringNodeObj = new StringNode(key, value);
				if (comment != null) {
					StringNodeComment nodeComment = new StringNodeComment();
					nodeComment.setComment(comment);
					stringNodeObj.setStringNodeComment(nodeComment);
				}
				stringNodes.add(stringNodeObj);
			}

			localizationFile = new LocalizationFile(file, localeInfo,
					stringNodes);

		} catch (Exception e) {
			throw new IOException(
					Messages.AndroidLocalizationSchema_Exception_CouldNotLoadFile
							+ file.getName() + ". " + e.getMessage()); //$NON-NLS-1$
		}

		return localizationFile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.tml.localization.tools.extensions.classes.ILocalizationSchema
	 * #
	 * updateFile(org.eclipse.tml.localization.tools.datamodel.LocalizationFile)
	 */
	@Override
	public void updateFile(LocalizationFile localizationFile)
			throws TmLException {
		createFile(localizationFile);
		localizationFile.setDirty(false);
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
	 * @see
	 * org.eclipse.tml.localization.tools.extensions.classes.ILocalizationSchema
	 * #getLocaleID(org.eclipse.tml.localization.tools.datamodel.LocaleInfo)
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
	 * @see
	 * org.eclipse.tml.localization.tools.extensions.classes.ILocalizationSchema
	 * #getLocaleInfoFromID(java.lang.String)
	 */
	@Override
	public LocaleInfo getLocaleInfoFromID(String ID) {

		LocaleInfo result = new LocaleInfo();

		String[] segments = ID.split(QUALIFIER_SEP);
		int lastQualifier = -1;

		List<LocaleAttribute> localeAttributes = new ArrayList<LocaleAttribute>();

		for (int i = 1; i < segments.length; i++) {

			if (segments[i].equals("")) {
				// Do nothiing
			} else if (isCountryCodeSegment(segments[i])
					&& (lastQualifier < AndroidLocaleAttribute.INDEX_COUNTRY_CODE)) {
				lastQualifier = AndroidLocaleAttribute.INDEX_COUNTRY_CODE;
				localeAttributes.add(new AndroidLocaleAttribute(segments[i],
						AndroidLocaleAttribute.INDEX_COUNTRY_CODE));
			} else if (isNetworkCodeSegment(segments[i])
					&& (lastQualifier < AndroidLocaleAttribute.INDEX_NETWORK_CODE)) {
				lastQualifier = AndroidLocaleAttribute.INDEX_NETWORK_CODE;
				localeAttributes.add(new AndroidLocaleAttribute(segments[i],
						AndroidLocaleAttribute.INDEX_NETWORK_CODE));
			} else if (isLanguageSegment(segments[i])
					&& (lastQualifier < AndroidLocaleAttribute.INDEX_LANGUAGE)) {
				lastQualifier = AndroidLocaleAttribute.INDEX_LANGUAGE;
				localeAttributes.add(new AndroidLocaleAttribute(segments[i],
						AndroidLocaleAttribute.INDEX_LANGUAGE));
			} else if (isRegionSegment(segments[i])
					&& (lastQualifier < AndroidLocaleAttribute.INDEX_REGION)) {
				lastQualifier = AndroidLocaleAttribute.INDEX_REGION;
				localeAttributes.add(new AndroidLocaleAttribute(segments[i],
						AndroidLocaleAttribute.INDEX_REGION));
			} else if (isOrientationSegment(segments[i])
					&& (lastQualifier < AndroidLocaleAttribute.INDEX_SCREEN_ORIENTATION)) {
				lastQualifier = AndroidLocaleAttribute.INDEX_SCREEN_ORIENTATION;
				localeAttributes.add(new AndroidLocaleAttribute(segments[i],
						AndroidLocaleAttribute.INDEX_SCREEN_ORIENTATION));
			} else if (isPixelDensitySegment(segments[i])
					&& (lastQualifier < AndroidLocaleAttribute.INDEX_PIXEL_DENSITY)) {
				lastQualifier = AndroidLocaleAttribute.INDEX_PIXEL_DENSITY;
				localeAttributes.add(new AndroidLocaleAttribute(segments[i],
						AndroidLocaleAttribute.INDEX_PIXEL_DENSITY));
			} else if (isTouchTypeSegment(segments[i])
					&& (lastQualifier < AndroidLocaleAttribute.INDEX_TOUCH_TYPE)) {
				lastQualifier = AndroidLocaleAttribute.INDEX_TOUCH_TYPE;
				localeAttributes.add(new AndroidLocaleAttribute(segments[i],
						AndroidLocaleAttribute.INDEX_TOUCH_TYPE));
			} else if (isKeyboardStateSegment(segments[i])
					&& (lastQualifier < AndroidLocaleAttribute.INDEX_KEYBOARD_STATE)) {
				lastQualifier = AndroidLocaleAttribute.INDEX_KEYBOARD_STATE;
				localeAttributes.add(new AndroidLocaleAttribute(segments[i],
						AndroidLocaleAttribute.INDEX_KEYBOARD_STATE));
			} else if (isTextInputSegment(segments[i])
					&& (lastQualifier < AndroidLocaleAttribute.INDEX_TEXT_INPUT_METHOD)) {
				lastQualifier = AndroidLocaleAttribute.INDEX_TEXT_INPUT_METHOD;
				localeAttributes.add(new AndroidLocaleAttribute(segments[i],
						AndroidLocaleAttribute.INDEX_TEXT_INPUT_METHOD));
			} else if (isNavigationSegment(segments[i])
					&& (lastQualifier < AndroidLocaleAttribute.INDEX_NAVIGATION_METHOD)) {
				lastQualifier = AndroidLocaleAttribute.INDEX_NAVIGATION_METHOD;
				localeAttributes.add(new AndroidLocaleAttribute(segments[i],
						AndroidLocaleAttribute.INDEX_NAVIGATION_METHOD));
			} else if (isDimensionSegment(segments[i])
					&& (lastQualifier < AndroidLocaleAttribute.INDEX_SCREEN_DIMENSION)) {
				lastQualifier = AndroidLocaleAttribute.INDEX_SCREEN_DIMENSION;
				localeAttributes.add(new AndroidLocaleAttribute(segments[i],
						AndroidLocaleAttribute.INDEX_SCREEN_DIMENSION));
			} else {
				localeAttributes = new ArrayList<LocaleAttribute>();
				break;
			}

		}

		result.setLocaleAttributes(localeAttributes);

		return result;
	}

	@Override
	public String getDefaultID() {
		return LOCALIZATION_FILES_FOLDER;
	}

	private boolean isNetworkCodeSegment(String value) {
		return value.startsWith("mnc");

	}

	private boolean isLanguageSegment(String value) {
		return (value.length() == 2);

	}

	private boolean isRegionSegment(String value) {
		return ((value.startsWith("r") && (value.length() == 3)));

	}

	private boolean isOrientationSegment(String value) {
		return ((value.equalsIgnoreCase("port")
				|| value.equalsIgnoreCase("land") || value
				.equalsIgnoreCase("square")));

	}

	private boolean isPixelDensitySegment(String value) {
		return (value.endsWith("dpi"));

	}

	private boolean isTouchTypeSegment(String value) {
		return ((value.equalsIgnoreCase("notouch")
				|| value.equalsIgnoreCase("stylus") || value
				.equalsIgnoreCase("finger")));
	}

	private boolean isKeyboardStateSegment(String value) {
		return ((value.equalsIgnoreCase("keysexposed") || value
				.equalsIgnoreCase("keyshidden")));

	}

	private boolean isTextInputSegment(String value) {
		return ((value.equalsIgnoreCase("nokeys")
				|| value.equalsIgnoreCase("qwerty") || value
				.equalsIgnoreCase("12key")));

	}

	private boolean isNavigationSegment(String value) {
		return ((value.equalsIgnoreCase("dpad")
				|| value.equalsIgnoreCase("trackball")
				|| value.equalsIgnoreCase("wheel") || value
				.equalsIgnoreCase("nonav")));

	}

	private boolean isDimensionSegment(String value) {
		boolean result = false;
		if (value.contains("x")) {
			String[] ints = value.split("x");
			if (ints.length == 2) {
				result = true;
			}
		}
		return result;

	}

	private boolean isCountryCodeSegment(String value) {
		return value.startsWith("mcc");

	}
}
