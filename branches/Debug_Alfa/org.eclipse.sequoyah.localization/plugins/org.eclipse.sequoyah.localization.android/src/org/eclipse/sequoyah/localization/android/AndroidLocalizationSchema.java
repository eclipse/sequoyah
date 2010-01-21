/********************************************************************************
 * Copyright (c) 2009 Motorola Inc.
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
 ********************************************************************************/
package org.eclipse.sequoyah.localization.android;

import static org.w3c.dom.Node.COMMENT_NODE;

import java.awt.Dimension;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.sequoyah.device.common.utilities.exception.SequoyahException;
import org.eclipse.sequoyah.localization.android.i18n.Messages;
import org.eclipse.sequoyah.localization.stringeditor.datatype.ColumnInfo;
import org.eclipse.sequoyah.localization.stringeditor.datatype.RowInfo;
import org.eclipse.sequoyah.localization.stringeditor.datatype.TranslationInfo;
import org.eclipse.sequoyah.localization.tools.datamodel.LocaleAttribute;
import org.eclipse.sequoyah.localization.tools.datamodel.LocaleInfo;
import org.eclipse.sequoyah.localization.tools.datamodel.LocalizationFile;
import org.eclipse.sequoyah.localization.tools.datamodel.StringArray;
import org.eclipse.sequoyah.localization.tools.datamodel.StringNode;
import org.eclipse.sequoyah.localization.tools.datamodel.StringNodeComment;
import org.eclipse.sequoyah.localization.tools.extensions.classes.ILocalizationSchema;
import org.eclipse.sequoyah.localization.tools.extensions.implementation.generic.NewRowInputDialog;
import org.eclipse.sequoyah.localization.tools.extensions.implementation.generic.TranslateColumnInputDialog;
import org.eclipse.sequoyah.localization.tools.extensions.implementation.generic.TranslateColumnsInputDialog;
import org.eclipse.sequoyah.localization.tools.managers.LocalizationManager;
import org.eclipse.sequoyah.localization.tools.managers.ProjectLocalizationManager;
import org.eclipse.swt.widgets.TableColumn;
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
public class AndroidLocalizationSchema extends ILocalizationSchema
{

    /*
     * Android resources info
     */
    private final String RESOURCES_FOLDER = "res"; //$NON-NLS-1$

    private final String DEFAULT_LOCALE_TOOLTIP =
            Messages.AndroidLocalizationSchema_Default_Andr_Localization_File_Tooltip;

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

    private final String NEW_TRANSLATE_COLUMN_TITLE =
            Messages.AndroidTranslatedColumnProvider_NewColumnTitle;

    private final String TRANSLATE_CELLS_TITLE = Messages.AndroidTranslateCells_DialogTitle;

    private final String NEW_ROW_TITLE = Messages.AndroidNewRow_DialogTitle;

    private final String NEW_COLUMN_DESCRIPTION =
            Messages.AndroidNewColumnProvider_NewColumnDescription;

    private final String NEW_COLUMN_TEXT = AndroidLocalizationSchema.LOCALIZATION_FILES_FOLDER;

    private final String NEW_COLUMN_INVALID_ID =
            Messages.AndroidNewColumnProvider_InvalidNewColumID;

    private String MANDATORY_ID = AndroidLocalizationSchema.LOCALIZATION_FILES_FOLDER;

    private final String QUALIFIER_SEP = "-";

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.sequoyah.localization.tools.extensions.classes.ILocalizationSchema
     * #isValueValid(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public IStatus isValueValid(String localeID, String key, String value)
    {
        Status result = new Status(IStatus.OK, AndroidLocalizationPlugin.PLUGIN_ID, "");

        if (localeID.toLowerCase().equals(MANDATORY_ID.toLowerCase()))
        {

            if ((value == null))
            {
                result =
                        new Status(IStatus.ERROR, AndroidLocalizationPlugin.PLUGIN_ID,
                                Messages.EmptyKey_Discouraged);
            }

            if ((value == null) || (value.length() == 0))
            {
                result =
                        new Status(IStatus.WARNING, AndroidLocalizationPlugin.PLUGIN_ID,
                                Messages.EmptyKey_Discouraged);
            }
        }

        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.sequoyah.localization.tools.extensions.classes.ILocalizationSchema
     * #promptCollumnName()
     */
    @Override
    public ColumnInfo promptCollumnName(final IProject project)
    {
        ColumnInfo newColumn = null;

        // Ask user for the ID
        InputDialog dialog =
                new InputDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        NEW_COLUMN_TITLE, NEW_COLUMN_DESCRIPTION, NEW_COLUMN_TEXT, //$NON-NLS-2$
                        new IInputValidator()
                        {

                            public String isValid(String newText)
                            {
                                return isValid2(newText, project);
                            };
                        });

        if (dialog.open() == IDialogConstants.OK_ID)
        {
            newColumn = new ColumnInfo(dialog.getValue(), dialog.getValue(), null, true);
        }

        return newColumn;
    }

    /* (non-Javadoc)
     * @see org.eclipse.sequoyah.localization.tools.extensions.classes.ILocalizationSchema#promptRowName(org.eclipse.core.resources.IProject)
     */
    @Override
    public RowInfo[] promptRowName(final IProject iProject)
    {
        RowInfo[] rowInfo = null;

        NewRowInputDialog dialog =
                new NewRowInputDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                        .getShell(), iProject, NEW_ROW_TITLE);

        if (dialog.open() == IDialogConstants.OK_ID)
        {
            rowInfo = new RowInfo[dialog.getNumEntries()];
            String key = dialog.getKey();
            boolean isArray = dialog.isArray();
            for (int i = 0; i < dialog.getNumEntries(); i++)
            {
                rowInfo[i] = new RowInfo(key, isArray, null);
            }
        }

        return rowInfo;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.sequoyah.localization.tools.extensions.classes.ILocalizationSchema
     * #promptTranslatedCollumnName()
     */
    @Override
    public TranslationInfo promptTranslatedCollumnName(final IProject project, String selectedColumn)
    {
        TranslationInfo newColumn = null;

        // Ask user for the ID
        TranslateColumnInputDialog dialog =
                new TranslateColumnInputDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                        .getShell(), project, selectedColumn, NEW_TRANSLATE_COLUMN_TITLE,
                        NEW_COLUMN_DESCRIPTION, NEW_COLUMN_TEXT, new IInputValidator()
                        {

                            public String isValid(String newText)
                            {
                                return isValid2(newText, project);
                            };
                        });

        if (dialog.open() == IDialogConstants.OK_ID)
        {

            newColumn =
                    new TranslationInfo(dialog.getValue(), dialog.getValue(), null, true, dialog
                            .getFromLanguage(), dialog.getToLanguage(), null, dialog
                            .getTranslator());
        }

        return newColumn;
    }

    @Override
    public TranslationInfo[] promptTranslatedCollumnsName(final IProject project,
            String selectedColumn, String[] selectedKeys, String[] selectedCells,
            TableColumn[] columns)
    {
        TranslationInfo[] newColumns = null;

        // Ask user for the ID
        TranslateColumnsInputDialog dialog =
                new TranslateColumnsInputDialog(PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow().getShell(), project, selectedColumn,
                        selectedCells, columns, TRANSLATE_CELLS_TITLE);

        if (dialog.open() == IDialogConstants.OK_ID)
        {
            List<TranslateColumnsInputDialog.DestinationColumn> destinationColumns =
                    dialog.getDestinationColumns();
            newColumns = new TranslationInfo[selectedCells.length * destinationColumns.size()];

            int count = 0;
            for (int i = 0; i < selectedCells.length; i++)
            {
                String selectedCell = selectedCells[i];
                String selectedKey = selectedKeys[i];
                for (int j = 0; j < destinationColumns.size(); j++)
                {
                    TranslateColumnsInputDialog.DestinationColumn destColumn =
                            destinationColumns.get(j);
                    newColumns[count] =
                            new TranslationInfo(destColumn.getText(), destColumn.getText(), null,
                                    true, dialog.getFromLanguage(), destColumn.getLang(),
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
    private String isValid2(String value, IProject project)
    {

        String result = null;

        if (value.startsWith(LOCALIZATION_FILES_FOLDER))
        {

            AndroidLocalizationSchema schema = new AndroidLocalizationSchema();
            String id = value.replace(LOCALIZATION_FILES_FOLDER, ""); //$NON-NLS-1$
            LocaleInfo info = schema.getLocaleInfoFromID(id);
            ProjectLocalizationManager manager =
                    LocalizationManager.getInstance().getProjectLocalizationManager(project, false);

            if ((info.getLocaleAttributes().size() > 0)
                    || (value.equalsIgnoreCase(AndroidLocalizationSchema.LOCALIZATION_FILES_FOLDER)))
            {
                LocalizationFile file = manager.getLocalizationProject().getLocalizationFile(info);
                if (file != null && !file.isToBeDeleted())
                {
                    result = Messages.AndroidNewColumnProvider_Dialog_FileAlreadyExists;
                }
            }
            else
            {
                result = NEW_COLUMN_INVALID_ID;
            }

        }
        else
        {
            result = NEW_COLUMN_INVALID_ID;
        }

        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.sequoyah.localization.tools.extensions.classes.ILocalizationSchema
     * #getEditorName()
     */
    @Override
    public String getEditorName()
    {
        return Messages.AndroidStringEditorInput_EditorTooltip;
    }

    /**
     * Create an Android localization file. It's a XML which has the following
     * format:
     * 
     * <?xml version="1.0" encoding="utf-8"?> <resources> <string
     * name="KEY">VALUE</string> ... </resources>
     * 
     * @see org.eclipse.sequoyah.localization.tools.extensions.classes.ILocalizationSchema
     *      #createFile(org.eclipse.sequoyah.localization.tools.datamodel.LocaleInfo)
     */
    @Override
    public void createFile(LocalizationFile localizationFile) throws SequoyahException
    {

        try
        {
            String filePath = localizationFile.getFile().getFullPath().toOSString();

            if (!localizationFile.getFile().exists())
            {
                localizationFile.getFile().getLocation();
                IPath fileToSave = null;
                if (localizationFile.getLocalizationProject() != null)
                {
                    fileToSave =
                            new Path(localizationFile.getLocalizationProject().getProject()
                                    .getLocation()
                                    + filePath);
                }
                else
                {
                    fileToSave = localizationFile.getFile().getLocation();
                }

                fileToSave.removeLastSegments(1).toFile().mkdirs();
                fileToSave.toFile().createNewFile();
                ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE,
                        new NullProgressMonitor());

                if (localizationFile.getLocalizationProject() != null)
                {
                    IFile iFile =
                            localizationFile.getLocalizationProject().getProject().getFile(
                                    new Path(filePath));
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
            for (StringNode stringNode : localizationFile.getStringNodes())
            {
                if (!stringNode.isArray())
                {
                    Element string = document.createElement(XML_STRING_TAG);
                    string.setAttribute(XML_STRING_ATTR_NAME, stringNode.getKey());
                    string.appendChild(document.createTextNode(stringNode.getValue()));

                    StringNodeComment nodeComment = stringNode.getStringNodeComment();
                    if (nodeComment != null)
                    {
                        if (nodeComment.getComment() != null)
                        {
                            if (nodeComment.getComment().length() > 0)
                            {
                                Comment comment = document.createComment(nodeComment.getComment());
                                string.appendChild(comment);
                            }
                        }
                    }

                    resources.appendChild(string);
                }
            }

            // Arrays
            for (StringArray stringArray : localizationFile.getStringArrays())
            {
                Element array = document.createElement(XML_STRING_ARRAY_TAG);
                array.setAttribute(XML_STRING_ATTR_NAME, stringArray.getKey());
                for (StringNode stringNode : stringArray.getValues())
                {
                    Element arrayItem = document.createElement(XML_STRING_ARRAY_ITEM_TAG);
                    arrayItem.appendChild(document.createTextNode(stringNode.getValue()));
                    array.appendChild(arrayItem);

                    StringNodeComment nodeComment = stringNode.getStringNodeComment();
                    if (nodeComment != null)
                    {
                        if (nodeComment.getComment() != null)
                        {
                            if (nodeComment.getComment().length() > 0)
                            {
                                Comment comment = document.createComment(nodeComment.getComment());
                                arrayItem.appendChild(comment);
                            }
                        }
                    }
                }
                resources.appendChild(array);
            }

            document.appendChild(resources);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", 4); //$NON-NLS-1$
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.METHOD, "xml"); //$NON-NLS-1$
            transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$

            FileOutputStream fileOutputStream = null;

            /*
             * At this point, localization file has the correct file
             */
            fileOutputStream =
                    new FileOutputStream(localizationFile.getFile().getLocation().toFile());

            StreamResult result =
                    new StreamResult(new OutputStreamWriter(fileOutputStream, "UTF-8")); //$NON-NLS-1$

            DOMSource source = new DOMSource(document);

            transformer.transform(source, result);

        }
        catch (Exception e)
        {
            throw new SequoyahException();
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.sequoyah.localization.tools.extensions.classes.ILocalizationSchema
     * #getLocaleToolTip(org.eclipse.core.runtime.Path)
     */
    @Override
    public String getLocaleToolTip(IPath path)
    {
        LocaleInfo locale = getLocaleInfoFromPath(path);
        List<LocaleAttribute> attributes = locale.getLocaleAttributes();
        String result = ""; //$NON-NLS-1$
        for (Iterator<LocaleAttribute> iterator = attributes.iterator(); iterator.hasNext();)
        {
            if (result.length() > 0)
            {
                result = result + "\n "; //$NON-NLS-1$
            }
            LocaleAttribute localeAttribute = iterator.next();
            result = result + ((AndroidLocaleAttribute) localeAttribute).getDisplayName() + ": " //$NON-NLS-1$
                    + ((AndroidLocaleAttribute) localeAttribute).getDisplayValue();
        }

        if (result.length() == 0)
        {
            result = DEFAULT_LOCALE_TOOLTIP;
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.sequoyah.localization.tools.extensions.classes.ILocalizationSchema
     * #getLocaleAttributes()
     */
    @Override
    public List<LocaleAttribute> getLocaleAttributes()
    {
        List<LocaleAttribute> localeAttributes = new ArrayList<LocaleAttribute>();

        localeAttributes.add(new AndroidLocaleAttribute(new Integer(123),
                AndroidLocaleAttribute.INDEX_COUNTRY_CODE));
        localeAttributes.add(new AndroidLocaleAttribute(new Integer(000),
                AndroidLocaleAttribute.INDEX_NETWORK_CODE));
        // TODO: change this values
        localeAttributes
                .add(new AndroidLocaleAttribute(null, AndroidLocaleAttribute.INDEX_LANGUAGE));
        localeAttributes.add(new AndroidLocaleAttribute(null, AndroidLocaleAttribute.INDEX_REGION));
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
     * org.eclipse.sequoyah.localization.tools.extensions.classes.ILocalizationSchema
     * #getLocalizationFileExtensions()
     */
    @Override
    public List<String> getLocalizationFileExtensions()
    {
        List<String> localizationFileExtensions = new ArrayList<String>();
        localizationFileExtensions.add(FILE_EXTENSION);
        return localizationFileExtensions;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.sequoyah.localization.tools.extensions.classes.ILocalizationSchema
     * #getLocalizationFiles(org.eclipse.core.resources.IProject)
     */
    @Override
    public Map<LocaleInfo, IFile> getLocalizationFiles(IProject project)
    {

        Map<LocaleInfo, IFile> localizationFiles = new LinkedHashMap<LocaleInfo, IFile>();
        boolean hasDefault = false;
        try
        {

            IResource resourcesFolder = project.findMember(RESOURCES_FOLDER);
            if ((resourcesFolder != null) && (resourcesFolder instanceof IFolder))
            {

                IResource[] folders = ((IFolder) resourcesFolder).members();
                if (folders != null)
                {
                    /*
                     * Iterate in folders inside the resources folder
                     */
                    for (IResource folder : folders)
                    {
                        if (folder.getName().startsWith(LOCALIZATION_FILES_FOLDER))
                        {
                            IResource[] files = ((IFolder) folder).members();
                            for (IResource file : files)
                            {

                                if ((file instanceof IFile) && (isLocalizationFile((IFile) file)))
                                {
                                    localizationFiles.put(getLocaleInfoFromPath(file
                                            .getProjectRelativePath()), (IFile) file);
                                    if (folder.getName().equals(LOCALIZATION_FILES_FOLDER))
                                    {
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
            if (!hasDefault)
            {
                if (resourcesFolder instanceof IFolder)
                {
                    IFolder folder = (IFolder) resourcesFolder;
                    final IFolder valuesFolder = folder.getFolder(LOCALIZATION_FILES_FOLDER);
                    try
                    {
                        if (!valuesFolder.exists())
                        {

                            // try to create the folder
                            PlatformUI.getWorkbench().getProgressService().run(false, false,
                                    new IRunnableWithProgress()
                                    {

                                        public void run(IProgressMonitor monitor)
                                                throws InvocationTargetException,
                                                InterruptedException
                                        {
                                            try
                                            {
                                                valuesFolder.create(true, true, monitor);
                                            }
                                            catch (CoreException e)
                                            {
                                                // do nothing
                                            }

                                        }
                                    });
                        }
                        // check if folder was created
                        // create the default file
                        if (valuesFolder.exists())
                        {
                            IFile valuesFile = valuesFolder.getFile(LOCALIZATION_FILE_NAME);
                            localizationFiles.put(getLocaleInfoFromPath(valuesFile
                                    .getProjectRelativePath()), valuesFile);
                        }

                    }
                    catch (Exception e)
                    {
                        // do nothing, just exit
                    }

                }
            }
        }
        catch (CoreException e)
        {
            // 
        }
        return localizationFiles;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.sequoyah.localization.tools.extensions.classes.ILocalizationSchema
     * #isLocalizationFile(org.eclipse.core.resources.IFile)
     */
    @Override
    public boolean isLocalizationFile(IFile file)
    {

        boolean result = false;
        if (file != null)
        {
            if (file.getProjectRelativePath().toString().matches(LF_REGULAR_EXPRESSION))
            {

                result = true;
            }
        }
        return result;

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.sequoyah.localization.tools.extensions.classes.ILocalizationSchema
     * #loadAllFiles()
     */
    @Override
    public Map<LocaleInfo, LocalizationFile> loadAllFiles(IProject project) throws IOException
    {

        Map<LocaleInfo, LocalizationFile> filesMap =
                new LinkedHashMap<LocaleInfo, LocalizationFile>();

        Map<LocaleInfo, IFile> localizationFiles = getLocalizationFiles(project);

        for (Map.Entry<LocaleInfo, IFile> entry : localizationFiles.entrySet())
        {
            filesMap.put(entry.getKey(), loadFile(entry.getValue()));
        }

        return filesMap;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.sequoyah.localization.tools.extensions.classes.ILocalizationSchema
     * #loadFile(org.eclipse.core.resources.IFile)
     */
    @Override
    public LocalizationFile loadFile(IFile file) throws IOException
    {

        LocalizationFile localizationFile = null;
        LocaleInfo localeInfo = getLocaleInfoFromPath(file.getFullPath());
        List<StringNode> stringNodes = new ArrayList<StringNode>();
        List<StringArray> stringArrays = new ArrayList<StringArray>();

        if (!file.exists())
        {
            LocalizationFile tempFile = new LocalizationFile(file, localeInfo, stringNodes, null);
            try
            {
                createFile(tempFile);
            }
            catch (SequoyahException e)
            {
                // do nothing
            }
        }

        try
        {

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(file.getLocation().toString()));

            /*
             * Get string nodes
             */
            NodeList stringNodeList = document.getElementsByTagName(XML_STRING_TAG);

            String key = null;
            String value = null;
            for (int i = 0; i < stringNodeList.getLength(); i++)
            {
                Element stringNode = (Element) stringNodeList.item(i);
                key = stringNode.getAttributeNode(XML_STRING_ATTR_NAME).getNodeValue();
                String comment = null;
                if (stringNode.hasChildNodes())
                {
                    NodeList childs = stringNode.getChildNodes();
                    for (int j = 0; j < childs.getLength(); j++)
                    {
                        Node childN = (Node) childs.item(j);
                        if (childN.getNodeType() == COMMENT_NODE)
                        {
                            comment = childN.getNodeValue();
                        }
                    }

                }
                value = stringNode.getTextContent();

                StringNode stringNodeObj = new StringNode(key, value);
                if (comment != null)
                {
                    StringNodeComment nodeComment = new StringNodeComment();
                    nodeComment.setComment(comment);
                    stringNodeObj.setStringNodeComment(nodeComment);
                }
                stringNodes.add(stringNodeObj);
            }

            /*
             * Get array nodes
             */
            NodeList arrayNodeList = document.getElementsByTagName(XML_STRING_ARRAY_TAG);
            String arrayKey = null;
            String arrayValue = null;
            for (int i = 0; i < arrayNodeList.getLength(); i++)
            {
                Element arrayNode = (Element) arrayNodeList.item(i);
                arrayKey = arrayNode.getAttributeNode(XML_STRING_ATTR_NAME).getNodeValue();
                StringArray stringArray = new StringArray(arrayKey);
                if (arrayNode.hasChildNodes())
                {
                    NodeList arrayItems = arrayNode.getElementsByTagName(XML_STRING_ARRAY_ITEM_TAG);
                    for (int j = 0; j < arrayItems.getLength(); j++)
                    {
                        Node childN = (Node) arrayItems.item(j);
                        arrayValue = childN.getTextContent();
                        StringNode newNode = stringArray.addValue(arrayValue);

                        // comments
                        String comment = null;
                        if (childN.hasChildNodes())
                        {
                            NodeList childs = childN.getChildNodes();
                            for (int k = 0; k < childs.getLength(); k++)
                            {
                                Node commentNode = (Node) childs.item(k);
                                if (commentNode.getNodeType() == COMMENT_NODE)
                                {
                                    comment = commentNode.getNodeValue();
                                }
                            }

                        }

                        if (comment != null)
                        {
                            StringNodeComment nodeComment = new StringNodeComment();
                            nodeComment.setComment(comment);
                            newNode.setStringNodeComment(nodeComment);
                        }

                    }

                }
                stringArrays.add(stringArray);
            }

            localizationFile = new LocalizationFile(file, localeInfo, stringNodes, stringArrays);

        }
        catch (Exception e)
        {
            throw new IOException(Messages.AndroidLocalizationSchema_Exception_CouldNotLoadFile
                    + file.getName() + ". " + e.getMessage()); //$NON-NLS-1$
        }

        return localizationFile;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.sequoyah.localization.tools.extensions.classes.ILocalizationSchema
     * #
     * updateFile(org.eclipse.sequoyah.localization.tools.datamodel.LocalizationFile)
     */
    @Override
    public void updateFile(LocalizationFile localizationFile) throws SequoyahException
    {
        createFile(localizationFile);
        localizationFile.setDirty(false);
    }

    /**
     * Given a localization file path, returns the language information
     * (attributes) of this localization file according to the file name.
     * 
     * @return LanguageInfo object with the attributes of this language
     */
    private LocaleInfo getLocaleInfoFromPath(IPath path)
    {

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
    public String getPathFromLocaleInfo(LocaleInfo lang)
    {

        String result;
        if (lang.getLocaleAttributes().size() > 0)
        {
            // There are qualifiers to concatenate in the folder name
            result =
                    RESOURCES_FOLDER + File.separator + LOCALIZATION_FILES_FOLDER + QUALIFIER_SEP
                            + getLocaleID(lang) + File.separator + LOCALIZATION_FILE_NAME;
        }
        else
        {
            // It is a default location file (no language qualifier)
            result =
                    RESOURCES_FOLDER + File.separator + LOCALIZATION_FILES_FOLDER + File.separator
                            + LOCALIZATION_FILE_NAME;

        }
        return result;

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.sequoyah.localization.tools.extensions.classes.ILocalizationSchema
     * #getLocaleID(org.eclipse.sequoyah.localization.tools.datamodel.LocaleInfo)
     */
    @Override
    public String getLocaleID(LocaleInfo localeInfo)
    {
        String localeID = ""; //$NON-NLS-1$
        List<LocaleAttribute> localeAttributes = localeInfo.getLocaleAttributes();
        for (Iterator<LocaleAttribute> iterator = localeAttributes.iterator(); iterator.hasNext();)
        {
            LocaleAttribute localeAttribute = iterator.next();
            if (((AndroidLocaleAttribute) localeAttribute).isSet())
            {
                if (localeID.length() != 0)
                {
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
     * org.eclipse.sequoyah.localization.tools.extensions.classes.ILocalizationSchema
     * #getLocaleInfoFromID(java.lang.String)
     */
    @Override
    public LocaleInfo getLocaleInfoFromID(String ID)
    {

        LocaleInfo result = new LocaleInfo();

        String[] segments = ID.split(QUALIFIER_SEP);
        int lastQualifier = -1;

        List<LocaleAttribute> localeAttributes = new ArrayList<LocaleAttribute>();

        for (int i = 1; i < segments.length; i++)
        {

            if (segments[i].equals(""))
            {
                // Do nothiing
            }
            else if (isCountryCodeSegment(segments[i])
                    && (lastQualifier < AndroidLocaleAttribute.INDEX_COUNTRY_CODE))
            {
                lastQualifier = AndroidLocaleAttribute.INDEX_COUNTRY_CODE;
                localeAttributes.add(new AndroidLocaleAttribute(segments[i],
                        AndroidLocaleAttribute.INDEX_COUNTRY_CODE));
            }
            else if (isNetworkCodeSegment(segments[i])
                    && (lastQualifier < AndroidLocaleAttribute.INDEX_NETWORK_CODE))
            {
                lastQualifier = AndroidLocaleAttribute.INDEX_NETWORK_CODE;
                localeAttributes.add(new AndroidLocaleAttribute(segments[i],
                        AndroidLocaleAttribute.INDEX_NETWORK_CODE));
            }
            else if (isLanguageSegment(segments[i])
                    && (lastQualifier < AndroidLocaleAttribute.INDEX_LANGUAGE))
            {
                lastQualifier = AndroidLocaleAttribute.INDEX_LANGUAGE;
                localeAttributes.add(new AndroidLocaleAttribute(segments[i],
                        AndroidLocaleAttribute.INDEX_LANGUAGE));
            }
            else if (isRegionSegment(segments[i])
                    && (lastQualifier < AndroidLocaleAttribute.INDEX_REGION))
            {
                lastQualifier = AndroidLocaleAttribute.INDEX_REGION;
                localeAttributes.add(new AndroidLocaleAttribute(segments[i],
                        AndroidLocaleAttribute.INDEX_REGION));
            }
            else if (isOrientationSegment(segments[i])
                    && (lastQualifier < AndroidLocaleAttribute.INDEX_SCREEN_ORIENTATION))
            {
                lastQualifier = AndroidLocaleAttribute.INDEX_SCREEN_ORIENTATION;
                localeAttributes.add(new AndroidLocaleAttribute(segments[i],
                        AndroidLocaleAttribute.INDEX_SCREEN_ORIENTATION));
            }
            else if (isPixelDensitySegment(segments[i])
                    && (lastQualifier < AndroidLocaleAttribute.INDEX_PIXEL_DENSITY))
            {
                lastQualifier = AndroidLocaleAttribute.INDEX_PIXEL_DENSITY;
                localeAttributes.add(new AndroidLocaleAttribute(segments[i],
                        AndroidLocaleAttribute.INDEX_PIXEL_DENSITY));
            }
            else if (isTouchTypeSegment(segments[i])
                    && (lastQualifier < AndroidLocaleAttribute.INDEX_TOUCH_TYPE))
            {
                lastQualifier = AndroidLocaleAttribute.INDEX_TOUCH_TYPE;
                localeAttributes.add(new AndroidLocaleAttribute(segments[i],
                        AndroidLocaleAttribute.INDEX_TOUCH_TYPE));
            }
            else if (isKeyboardStateSegment(segments[i])
                    && (lastQualifier < AndroidLocaleAttribute.INDEX_KEYBOARD_STATE))
            {
                lastQualifier = AndroidLocaleAttribute.INDEX_KEYBOARD_STATE;
                localeAttributes.add(new AndroidLocaleAttribute(segments[i],
                        AndroidLocaleAttribute.INDEX_KEYBOARD_STATE));
            }
            else if (isTextInputSegment(segments[i])
                    && (lastQualifier < AndroidLocaleAttribute.INDEX_TEXT_INPUT_METHOD))
            {
                lastQualifier = AndroidLocaleAttribute.INDEX_TEXT_INPUT_METHOD;
                localeAttributes.add(new AndroidLocaleAttribute(segments[i],
                        AndroidLocaleAttribute.INDEX_TEXT_INPUT_METHOD));
            }
            else if (isNavigationSegment(segments[i])
                    && (lastQualifier < AndroidLocaleAttribute.INDEX_NAVIGATION_METHOD))
            {
                lastQualifier = AndroidLocaleAttribute.INDEX_NAVIGATION_METHOD;
                localeAttributes.add(new AndroidLocaleAttribute(segments[i],
                        AndroidLocaleAttribute.INDEX_NAVIGATION_METHOD));
            }
            else if (isDimensionSegment(segments[i])
                    && (lastQualifier < AndroidLocaleAttribute.INDEX_SCREEN_DIMENSION))
            {
                lastQualifier = AndroidLocaleAttribute.INDEX_SCREEN_DIMENSION;
                localeAttributes.add(new AndroidLocaleAttribute(segments[i],
                        AndroidLocaleAttribute.INDEX_SCREEN_DIMENSION));
            }
            else
            {
                localeAttributes = new ArrayList<LocaleAttribute>();
                break;
            }

        }

        result.setLocaleAttributes(localeAttributes);

        return result;
    }

    @Override
    public String getISO639LangFromID(String ID)
    {
        String iso639 = null;

        LocaleInfo localeInfo = getLocaleInfoFromID(ID);
        for (LocaleAttribute locAtt : localeInfo.getLocaleAttributes())
        {
            if (locAtt.getDisplayName().equals("Language"))
            {
                iso639 = locAtt.getFolderValue();
            }
        }

        return iso639;
    }

    @Override
    public String getDefaultID()
    {
        return LOCALIZATION_FILES_FOLDER;
    }

    private boolean isNetworkCodeSegment(String value)
    {
        return value.startsWith("mnc");

    }

    private boolean isLanguageSegment(String value)
    {
        return (value.length() == 2);

    }

    private boolean isRegionSegment(String value)
    {
        return ((value.startsWith("r") && (value.length() == 3)));

    }

    private boolean isOrientationSegment(String value)
    {
        return ((value.equalsIgnoreCase("port") || value.equalsIgnoreCase("land") || value
                .equalsIgnoreCase("square")));

    }

    private boolean isPixelDensitySegment(String value)
    {
        return (value.endsWith("dpi"));

    }

    private boolean isTouchTypeSegment(String value)
    {
        return ((value.equalsIgnoreCase("notouch") || value.equalsIgnoreCase("stylus") || value
                .equalsIgnoreCase("finger")));
    }

    private boolean isKeyboardStateSegment(String value)
    {
        return ((value.equalsIgnoreCase("keysexposed") || value.equalsIgnoreCase("keyshidden")));

    }

    private boolean isTextInputSegment(String value)
    {
        return ((value.equalsIgnoreCase("nokeys") || value.equalsIgnoreCase("qwerty") || value
                .equalsIgnoreCase("12key")));

    }

    private boolean isNavigationSegment(String value)
    {
        return ((value.equalsIgnoreCase("dpad") || value.equalsIgnoreCase("trackball")
                || value.equalsIgnoreCase("wheel") || value.equalsIgnoreCase("nonav")));

    }

    private boolean isDimensionSegment(String value)
    {
        boolean result = false;
        if (value.contains("x"))
        {
            String[] ints = value.split("x");
            if (ints.length == 2)
            {
                result = true;
            }
        }
        return result;

    }

    private boolean isCountryCodeSegment(String value)
    {
        return value.startsWith("mcc");

    }
}
