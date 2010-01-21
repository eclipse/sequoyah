/********************************************************************************
 * Copyright (c) 2009 Motorola Inc.
 * All rights reserved. This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Vinicius Hernandes (Motorola)
 * Matheus Tait Lima (Eldorado)
 * 
 * Contributors:
 * Marcelo Marzola Bossoni (Eldorado) - Bug [289146] - Performance and Usability Issues
 ********************************************************************************/
package org.eclipse.sequoyah.localization.tools.extensions.classes;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.sequoyah.device.common.utilities.exception.SequoyahException;
import org.eclipse.sequoyah.localization.stringeditor.datatype.ColumnInfo;
import org.eclipse.sequoyah.localization.stringeditor.datatype.RowInfo;
import org.eclipse.sequoyah.localization.stringeditor.datatype.TranslationInfo;
import org.eclipse.sequoyah.localization.tools.datamodel.LocaleAttribute;
import org.eclipse.sequoyah.localization.tools.datamodel.LocaleInfo;
import org.eclipse.sequoyah.localization.tools.datamodel.LocalizationFile;
import org.eclipse.swt.widgets.TableColumn;

/**
 * This interface is intended to be implemented by classes
 * which define new localization schemas
 * 
 */
public abstract class ILocalizationSchema
{

    /*
     * The name of the localization schema
     */
    private String name;

    /*
     * The name of the project nature to which this localization schema can be
     * applied
     */
    private String natureName;

    /*
     * The name of the natures that are supersede by the nature / localization
     * schema being defined If a project there are more than one nature and more
     * than one nature has a localization schema, this attribute is used to
     * define which localization schema shall be used for that project
     */
    private List<String> naturePrecedence;

    /**
     * Return the name to be displayed in the localization editor
     * 
     * @return the name to be displayd in the localization editor
     */
    public abstract String getEditorName();

    /**
     * Retrieves a ColumnInfo, which will be use by the editor to add a new
     * column
     * 
     * @param iProject
     * 
     * @return a ColumnInfo, which will be use by the editor to add a new column
     */
    public abstract ColumnInfo promptCollumnName(IProject iProject);

    /**
     * Retrieves a RowInfo, which will be use by the editor to add a new
     * row
     * 
     * @param iProject
     * 
     * @return a RowInfo, which will be use by the editor to add a new row
     */
    public abstract RowInfo[] promptRowName(IProject iProject);

    /**
     * Check if the value if valid for the locale
     * 
     * @param localeID
     *            the locale ID
     * @param key
     *            entry key
     * @param value
     *            entry value
     * @return true if it's valid, false otherwise
     */
    public abstract IStatus isValueValid(String localeID, String key, String value);

    /**
     * Return the extension of the localization files
     * 
     * @return the extension of the localization files
     */
    public abstract List<String> getLocalizationFileExtensions();

    /**
     * Return all localization files in the given project, specifying the
     * language represented by each one
     * 
     * @param project
     *            the project on each the localization files shall be identified
     * @return a map containing all localization files and the language each one
     *         represent
     */
    public abstract Map<LocaleInfo, IFile> getLocalizationFiles(IProject project);

    /**
     * Read the given localization file and return its <key>:<value> pairs,
     * which need to be encapsulated in a LocalizationFile object
     * 
     * @param file
     *            the localization file that must be read
     * @return a LocalizationFile populated with the <key>:<value> pairs of the
     *         given localization file
     * @throws IOException
     */
    public abstract LocalizationFile loadFile(IFile file) throws IOException;

    /**
     * Read all localization files in a project
     * 
     * @param project
     *            the project on each the localization files shall be identified
     *            and loaded
     * @return a map containing all LocalizationFile objects for that project
     *         (one for each localization file) and the language each one
     *         represent
     * @throws IOException
     */
    public abstract Map<LocaleInfo, LocalizationFile> loadAllFiles(IProject project)
            throws IOException;

    /**
     * Create a new localization file according to the rules for this specific
     * localization schema, such as: - where to put the file - how to name it -
     * the internal format of the file (xml, properties, etc) and so one
     * 
     * The file generated is based on the generic LocalizationFile object passed
     * as a parameter
     * 
     * @param localizationFile
     *            an object which has information about the localization file
     *            that shall be created, as well as its content
     * @return true if the file has successfully been created, false otherwise
     */
    public abstract void createFile(LocalizationFile localizationFile) throws SequoyahException;

    /**
     * Update an already existent localization file according to the rules for
     * this specific localization schema
     * 
     * @param localizationFile
     *            an object which has information about the localization file
     *            that shall be updated, as well as its new content
     * @return true if the file has successfully been updated, false otherwise
     */
    public abstract void updateFile(LocalizationFile localizationFile) throws SequoyahException;

    /**
     * Get the list of attributes that makes sense to describe a locale on this
     * localization schema. The list can be compounded by items like: - Language
     * - Country It's not limited to the items above, though.
     * 
     * Each attribute shall be represented by a LanguageAttribute object
     * 
     * @return a list of LocaleAttribute objects, which represent a list of
     *         attributes that makes sense to describe a locale on this
     *         localization schema
     */
    public abstract List<LocaleAttribute> getLocaleAttributes();

    /**
     * Check whether the file passed as a parameter is a localization file or
     * not
     * 
     * It can be used specially when the localization schema uses files with
     * known extensions and not all files with that extension are localization
     * files (it can be determined, for example, looking at the folder the file
     * is located)
     * 
     * @param file
     *            the file that is intended to be verified
     * @return true if the file is a localizatio file, false otherwise
     */
    public abstract boolean isLocalizationFile(IFile file);

    /**
     * Given a LocaleInfo object, return the ID if this locale. The ID formation
     * rules may change from schema to schema
     * 
     * @param localeInfo
     *            the localeInfo
     * @return the ID
     */
    public abstract String getLocaleID(LocaleInfo localeInfo);

    /**
     * Given a Path of a localization file, retrieves the human readable tooltip
     * (if any) to be used in the editor for display purposes.
     * 
     * @param path
     *            the path
     * @return the tooltip
     */
    public abstract String getLocaleToolTip(IPath path);

    /**
     * Given an ID of, retrieves the correspondent LocaleInfo from the set of
     * available locale infos for this localization project
     * 
     * @param path
     *            the path
     * @return the tooltip
     */
    public abstract LocaleInfo getLocaleInfoFromID(String ID);

    /**
     * Get the attribute that contains the ISO 639 code for language
     * It shall return null if this information doesn't exist
     * 
     * @return the name of the attribute that contains the ISO 639 code for language
     */
    public abstract String getISO639LangFromID(String ID);

    /**
     * Get the name of the localization schema
     * 
     * @return the name of the localization schema
     */
    public String getName()
    {
        return name;
    }

    /**
     * Set the name of the localization schema
     * 
     * @param name
     *            the name of the localization schema
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Get the name of the project nature to which this localization schema can
     * be applied
     * 
     * @return the name of the project nature to which this localization schema
     *         can be applied
     */
    public String getNatureName()
    {
        return natureName;
    }

    /**
     * Set the name of the project nature to which this localization schema can
     * be applied
     * 
     * @param natureName
     *            the name of the project nature to which this localization
     *            schema can be applied
     */
    public void setNatureName(String natureName)
    {
        this.natureName = natureName;
    }

    /**
     * Get the name of the natures that are supersede by the nature /
     * localization schema being defined
     * 
     * @return the name of the natures that are supersede by the nature /
     *         localization schema being defined
     */
    public List<String> getNaturePrecedence()
    {
        return naturePrecedence;
    }

    /**
     * Set the name of the natures that are supersede by the nature /
     * localization schema being defined
     * 
     * @param naturePrecedence
     *            the name of the natures that are supersede by the nature /
     *            localization schema being defined
     */
    public void setNaturePrecedence(List<String> naturePrecedence)
    {
        this.naturePrecedence = naturePrecedence;
    }

    /**
     * 
     * Create a path correspondent to a given locale info. Usually the file or
     * folder name will have part of the info somehow.
     * 
     * @param lang
     *            the locale info
     * @return the path
     */
    public abstract String getPathFromLocaleInfo(LocaleInfo lang);

    /**
     * 
     * @return
     */
    public String getDefaultID()
    {
        return null;
    }

    /**
     * Retrieves a ColumnInfo, which will be use by the editor to add a new
     * column. This new column is a translation of an existing one, so
     * this prompt must will also return the from and to languages 
     * 
     * @param iProject
     * 
     * @return a ColumnInfo, which will be use by the editor to add a new column
     */
    public TranslationInfo promptTranslatedCollumnName(IProject project, String selectedColumn)
    {
        // Must be overridden by subclasses if translation of columns is implemented
        return null;
    }

    /**
     * Retrieves a ColumnInfo, which will be use by the editor to add a new
     * column. This new column is a translation of an existing one, so
     * this prompt must will also return the from and to languages 
     * 
     * @param iProject
     * 
     * @return a ColumnInfo, which will be use by the editor to add a new column
     */
    public TranslationInfo[] promptTranslatedCollumnsName(IProject project, String selectedColumn,
            String[] selectedKeys, String[] selectedCells, TableColumn[] columns)
    {
        // Must be overridden by subclasses if translation of cells is implemented
        return null;
    }

}
