/********************************************************************************
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
 *  * Vinicius Rigoni Hernandes (Eldorado) - Bug [289885] - Localization Editor doesn't recognize external file changes
 ********************************************************************************/
package org.eclipse.sequoyah.localization.tools.datamodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.sequoyah.localization.tools.persistence.IFilePersistentData;
import org.eclipse.sequoyah.localization.tools.persistence.IPersistentData;
import org.eclipse.sequoyah.localization.tools.persistence.PersistableAttributes;

/**
 * This class represents a real localization file in the project and contains
 * other information about it
 */
public class LocalizationFile implements IFilePersistentData
{

    /*
     * The LocalizationProject which the LocalizationFile belongs to
     */
    private LocalizationProject localizationProject;

    /*
     * A reference to the file being represented
     */
    private IFile file;

    /*
     * The information about the locale represented by the localization file
     */
    private LocaleInfo localeInfo;

    /*
     * The list of StringNodes which are part of the file
     */
    private List<StringNode> stringNodes;

    /*
     * The list of StringArrays which are part of the file
     */
    private List<StringArray> stringArrays;

    /*
     * String nodes indexed by key
     */
    Map<String, StringNode> stringNodesMap = new HashMap<String, StringNode>();

    /*
     * Whether the data in the model has been modified and differs from the
     * values saved
     */
    private boolean dirty = false;

    /*
     * Whether there are changes in the associated meta-data / extra-info or not
     */
    private boolean dirtyMetaExtraData = false;

    /*
     * Whether the file is marked to be deleted or not
     */
    private boolean toBeDeleted = false;

    /**
     * Constructor method
     * 
     * @param file
     *            a reference to the file being represented
     * @param localeInfo
     *            the locale represented by the localization file
     * @param stringNodes
     *            the list of StringNodes which are part of the file
     */
    public LocalizationFile(IFile file, LocaleInfo localeInfo, List<StringNode> stringNodes,
            List<StringArray> stringArrays)
    {
        this.file = file;
        this.localeInfo = localeInfo;
        this.stringNodes = new ArrayList<StringNode>();
        this.stringArrays = new ArrayList<StringArray>();
        setStringNodes(stringNodes);
        setStringArrays(stringArrays);
    }

    /**
     * Get the LocalizationProject which the LocalizationFile belongs to
     * 
     * @return the LocalizationProject which the LocalizationFile belongs to
     */
    public LocalizationProject getLocalizationProject()
    {
        return localizationProject;
    }

    /**
     * Set the LocalizationProject which the LocalizationFile belongs to
     * 
     * @param localizationProject
     *            the LocalizationProject which the LocalizationFile belongs to
     */
    public void setLocalizationProject(LocalizationProject localizationProject)
    {
        this.localizationProject = localizationProject;
    }

    /**
     * Get information about the locale represented by the localization file
     * 
     * @return information about the locale represented by the localization file
     */
    public LocaleInfo getLocaleInfo()
    {
        return localeInfo;
    }

    /**
     * Set information about the locale represented by the localization file
     * 
     * @param localeInfo
     *            information about the locale represented by the localization
     *            file
     */
    public void setLocaleInfo(LocaleInfo localeInfo)
    {
        this.localeInfo = localeInfo;
    }

    /**
     * Get the list of StringNodes which are part of the file
     * 
     * @return the list of StringNodes which are part of the file
     */
    public List<StringNode> getStringNodes()
    {
        return stringNodes;
    }

    /**
     * Get the list of StringNodes which are part of the file
     * 
     * @return the list of StringNodes which are part of the file
     */
    public List<StringArray> getStringArrays()
    {
        return stringArrays;
    }

    /**
     * Get the StringNodes which represents a specific key. If there is no node
     * for this key, a new node is created and returned
     * 
     * @param key
     *            the StringNode key attribute
     * @return the StringNode which represents the key passed as a parameter
     */
    public StringNode getStringNodeByKey(String key)
    {
        // TODO: tirar false
        boolean isArray = false;
        for (StringArray stringArray : this.getLocalizationProject().getAllStringArrays())
        {
            if (stringArray.isPartOfTheArray(key))
            {
                isArray = true;
                break;
            }
        }
        return getStringNodeByKey(key, isArray);
    }

    /**
     * Get the StringNodes which represents a specific key. If there is no node
     * for this key, a new node is created and returned
     * 
     * @param key the StringNode key attribute
     * @param isArray if it's an array or not
     * @return the StringNode which represents the key passed as a parameter
     */
    public StringNode getStringNodeByKey(String key, boolean isArray)
    {
        StringNode result = stringNodesMap.get(key);
        if (result == null)
        {
            StringNode newNode = new StringNode(key, "");
            newNode.setLocalizationFile(this);
            newNode.setArray(isArray);
            result = this.addStringNode(newNode);
        }
        return result;
    }

    /**
     * Set the list of StringNodes which are part of the file
     * 
     * @param stringNodes
     *            the list of StringNodes which are part of the file
     */
    public void setStringNodes(List<StringNode> stringNodes)
    {
        for (StringNode stringNode : stringNodes)
        {
            this.stringNodesMap.put(stringNode.getKey(), stringNode);
            stringNode.setLocalizationFile(this);

        }
        this.stringNodes.addAll(stringNodes);
    }

    /**
     * Set the list of StringArrays which are part of the file
     * 
     * @param stringArrays the list of StringArrays which are part of the file
     */
    public void setStringArrays(List<StringArray> stringArrays)
    {
        if (stringArrays != null)
        {
            for (StringArray stringArray : stringArrays)
            {
                List<StringNode> stringNodes = stringArray.getValues();
                for (StringNode stringNode : stringNodes)
                {
                    this.stringNodesMap.put(stringNode.getKey(), stringNode);
                    stringNode.setLocalizationFile(this);
                    stringNode.setArray(true);
                }
                this.stringNodes.addAll(stringNodes);
            }
            this.stringArrays.addAll(stringArrays);
        }
    }

    /**
	 * @return the stringNodesMap
	 */
	public Map<String, StringNode> getStringNodesMap() {
		return stringNodesMap;
	}

	/**
     * Check whether the data in the model has been modified and differs from
     * the values saved
     * 
     * @return true if the data in the model has been modified and differs from
     *         the values saved, false otherwise
     */
    public boolean isDirty()
    {
        return dirty;
    }

    /**
     * Set whether the data in the model has been modified and differs from the
     * values saved
     * 
     * @param dirty
     *            true if the data in the model has been modified and differs
     *            from the values saved, false otherwise
     */
    public void setDirty(boolean dirty)
    {
        // propagate the state if dirty = true
        if (dirty)
        {
            this.getLocalizationProject().setDirty(dirty);
        }
        this.dirty = dirty;
    }

    /**
     * Check whether there are changes in the associated meta-data / extra-info
     * or not
     * 
     * @return true if there are changes in the associated meta-data /
     *         extra-info, false otherwise
     */
    public boolean isDirtyMetaExtraData()
    {
        return dirtyMetaExtraData;
    }

    /**
     * Set whether there are changes in the associated meta-data / extra-info or
     * not
     * 
     * @param dirtyMetaExtraData
     *            true if there are changes in the associated meta-data /
     *            extra-info, false otherwise
     */
    public void setDirtyMetaExtraData(boolean dirtyMetaExtraData)
    {
        this.dirtyMetaExtraData = dirtyMetaExtraData;
    }

    /**
     * Set the file that is being represented
     * 
     * @param file
     *            the file that is being represented
     */
    public void setFile(IFile file)
    {
        this.file = file;
    }

    /**
     * Get only the modified StringNodes in this localization file
     * 
     * @return the modified StringNodes in this localization file
     */
    public List<StringNode> getModifiedStringNodes()
    {
        List<StringNode> modifiedStringNodes = new ArrayList<StringNode>();
        for (StringNode stringNode : stringNodes)
        {
            if (stringNode.isDirty())
            {
                modifiedStringNodes.add(stringNode);
            }
        }
        return modifiedStringNodes;
    }

    /**
     * Add a new StringNode to the list
     */
    public StringNode addStringNode(StringNode stringNode)
    {
        StringNode newStringNode = stringNode;

        // check if it's is an array
        if (stringNode.isArray())
        {
            StringArray stringArray = findStringArray(stringNode.getKey());
            int position = -1;
            //if (stringNode.getKey().contains("_"))
            if (StringArray.isArrayItem(stringNode.getKey()))
            {
                //position = Integer.parseInt(stringNode.getKey().split("_")[1]);
                position = StringArray.findItemPosition(stringNode.getKey());
            }
            newStringNode =
                    stringArray.addValue(stringNode.getValue(),
                            ((position != -1) ? position : null));
        }

        newStringNode.setLocalizationFile(this);
        stringNodes.add(newStringNode);
        stringNodesMap.put(newStringNode.getKey(), newStringNode);

        this.setDirty(true);

        return newStringNode;
    }

    /**
     * Remove a StringNode from the list
     */
    public void removeStringNode(StringNode stringNode)
    {
        if (stringNodes.contains(stringNode))
        {
            stringNodes.remove(stringNode);
            stringNodesMap.remove(stringNode.getKey());
            this.setDirty(true);
            // check if it's is an array
            if (stringNode.isArray())
            {
                stringNode.getStringArray().removeValue(stringNode);
                if (stringNode.getStringArray().getValues().size() == 0)
                {
                    this.stringArrays.remove(stringNode.getStringArray());
                }
            }
        }
    }

    /**
     * Return the file that is being represented
     * 
     * @see org.eclipse.sequoyah.localization.tools.persistence.IFilePersistentData#getFile()
     */
    public IFile getFile()
    {
        return file;
    }

    /**
     * @see org.eclipse.sequoyah.localization.tools.persistence.IFilePersistentData#getPersistentData()
     */
    public List<IPersistentData> getPersistentData()
    {
        List<IPersistentData> persistentData = new ArrayList<IPersistentData>();
        return persistentData;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.sequoyah.localization.tools.persistence.IPersistentData#
     * getPersistableAttributes()
     */
    public PersistableAttributes getPersistableAttributes()
    {
        return null;
    }

    /**
     * Check whether the file shall be deleted or not
     * 
     * @return true if the shall be deleted or not, false otherwise
     */
    public boolean isToBeDeleted()
    {
        return toBeDeleted;
    }

    /**
     * Set whether the file shall be deleted or not
     * 
     * @param shallBeDeleted
     *            true if the shall be deleted or not, false otherwise
     */
    public void setToBeDeleted(boolean toBeDeleted)
    {
        this.toBeDeleted = toBeDeleted;
    }

    @Override
    public boolean equals(Object obj)
    {
        boolean result = true;
        LocalizationFile locFile = (LocalizationFile) obj;
        List<StringNode> locFileStringNodes = locFile.getStringNodes();

        // skip blank array items
        List<StringNode> thisStringNodes = removeBlankArrayItems(stringNodes);
        List<StringNode> otherStringNodes = removeBlankArrayItems(locFileStringNodes);

        Collections.sort(thisStringNodes);
        Collections.sort(otherStringNodes);

        if ((thisStringNodes.size() != otherStringNodes.size()))
        {
            result = false;
        }
        else
        {
            boolean keyEqual, valueEqual;
            for (int i = 0; i < thisStringNodes.size(); i++)
            {
                keyEqual = thisStringNodes.get(i).getKey().equals(otherStringNodes.get(i).getKey());
                valueEqual =
                        thisStringNodes.get(i).getValue()
                                .equals(otherStringNodes.get(i).getValue());
                if ((!keyEqual) || (!valueEqual))
                {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Remove blank array items
     * 
     * @param nodes all nodes
     * @return only non blank array items
     */
    private List<StringNode> removeBlankArrayItems(List<StringNode> nodes)
    {
        List<StringNode> noBlankArrayItems = new ArrayList<StringNode>();

        for (StringNode node : nodes)
        {
            if (node.isArray())
            {
                if (!node.getValue().equals(""))
                {
                    noBlankArrayItems.add(node);
                }
            }
            else
            {
                noBlankArrayItems.add(node);
            }
        }

        return noBlankArrayItems;
    }

    /**
     * Find and retrieve an String Array
     * If it doesn't exist, create a new one
     * 
     * @param key array key
     * @return StringArray object
     */
    private StringArray findStringArray(String key)
    {
        if (StringArray.isArrayItem(key))
        {
            key = StringArray.getArrayKeyFromItemKey(key);
        }
        StringArray stringArray = null;
        for (StringArray sArray : this.stringArrays)
        {
            if (sArray.getKey().equals(key))
            {
                stringArray = sArray;
                break;
            }
        }
        if (stringArray == null)
        {
            stringArray = new StringArray(key);
            this.stringArrays.add(stringArray);
        }
        return stringArray;
    }

}
