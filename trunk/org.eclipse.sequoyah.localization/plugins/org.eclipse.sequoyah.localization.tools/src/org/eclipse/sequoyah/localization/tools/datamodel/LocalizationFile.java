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
 *  * Vinicius Rigoni Hernandes (Eldorado) - Bug [289885] - Localization Editor doesn't recognize external file changes
 ********************************************************************************/
package org.eclipse.tml.localization.tools.datamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.tml.localization.tools.persistence.IFilePersistentData;
import org.eclipse.tml.localization.tools.persistence.IPersistentData;
import org.eclipse.tml.localization.tools.persistence.PersistableAttributes;

/**
 * This class represents a real localization file in the project and contains
 * other information about it
 */
public class LocalizationFile implements IFilePersistentData {

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
	public LocalizationFile(IFile file, LocaleInfo localeInfo,
			List<StringNode> stringNodes) {
		this.file = file;
		this.localeInfo = localeInfo;
		setStringNodes(stringNodes);
	}

	/**
	 * Get the LocalizationProject which the LocalizationFile belongs to
	 * 
	 * @return the LocalizationProject which the LocalizationFile belongs to
	 */
	public LocalizationProject getLocalizationProject() {
		return localizationProject;
	}

	/**
	 * Set the LocalizationProject which the LocalizationFile belongs to
	 * 
	 * @param localizationProject
	 *            the LocalizationProject which the LocalizationFile belongs to
	 */
	public void setLocalizationProject(LocalizationProject localizationProject) {
		this.localizationProject = localizationProject;
	}

	/**
	 * Get information about the locale represented by the localization file
	 * 
	 * @return information about the locale represented by the localization file
	 */
	public LocaleInfo getLocaleInfo() {
		return localeInfo;
	}

	/**
	 * Set information about the locale represented by the localization file
	 * 
	 * @param localeInfo
	 *            information about the locale represented by the localization
	 *            file
	 */
	public void setLocaleInfo(LocaleInfo localeInfo) {
		this.localeInfo = localeInfo;
	}

	/**
	 * Get the list of StringNodes which are part of the file
	 * 
	 * @return the list of StringNodes which are part of the file
	 */
	public List<StringNode> getStringNodes() {
		return stringNodes;
	}

	/**
	 * Get the StringNodes which represents a specific key. If there is no node
	 * for this key, a new node is created and returned
	 * 
	 * @param key
	 *            the StringNode key attribute
	 * @return the StringNodes which represents the key passed as a parameter
	 */
	public StringNode getStringNodeByKey(String key) {
		StringNode result = stringNodesMap.get(key);
		if (result == null) {
			StringNode newNode = new StringNode(key, "");
			newNode.setLocalizationFile(this);
			this.addStringNode(newNode);
			stringNodesMap.put(key, newNode);
			result = newNode;
		}
		return result;
	}

	/**
	 * Set the list of StringNodes which are part of the file
	 * 
	 * @param stringNodes
	 *            the list of StringNodes which are part of the file
	 */
	public void setStringNodes(List<StringNode> stringNodes) {
		for (StringNode stringNode : stringNodes) {
			this.stringNodesMap.put(stringNode.getKey(), stringNode);
			stringNode.setLocalizationFile(this);

		}
		this.stringNodes = stringNodes;
	}

	/**
	 * Check whether the data in the model has been modified and differs from
	 * the values saved
	 * 
	 * @return true if the data in the model has been modified and differs from
	 *         the values saved, false otherwise
	 */
	public boolean isDirty() {
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
	public void setDirty(boolean dirty) {
		// propagate the state if dirty = true
		if (dirty) {
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
	public boolean isDirtyMetaExtraData() {
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
	public void setDirtyMetaExtraData(boolean dirtyMetaExtraData) {
		this.dirtyMetaExtraData = dirtyMetaExtraData;
	}

	/**
	 * Set the file that is being represented
	 * 
	 * @param file
	 *            the file that is being represented
	 */
	public void setFile(IFile file) {
		this.file = file;
	}

	/**
	 * Get only the modified StringNodes in this localization file
	 * 
	 * @return the modified StringNodes in this localization file
	 */
	public List<StringNode> getModifiedStringNodes() {
		List<StringNode> modifiedStringNodes = new ArrayList<StringNode>();
		for (StringNode stringNode : stringNodes) {
			if (stringNode.isDirty()) {
				modifiedStringNodes.add(stringNode);
			}
		}
		return modifiedStringNodes;
	}

	/**
	 * Add a new StringNode to the list
	 */
	public void addStringNode(StringNode stringNode) {
		stringNodes.add(stringNode);
		stringNodesMap.put(stringNode.getKey(), stringNode);
		stringNode.setLocalizationFile(this);
		this.setDirty(true);
	}

	/**
	 * Remove a StringNode from the list
	 */
	public void removeStringNode(StringNode stringNode) {
		if (stringNodes.contains(stringNode)) {
			stringNodes.remove(stringNode);
			stringNodesMap.remove(stringNode.getKey());
			this.setDirty(true);
		}
	}

	/**
	 * Return the file that is being represented
	 * 
	 * @see org.eclipse.tml.localization.tools.persistence.IFilePersistentData#getFile()
	 */
	public IFile getFile() {
		return file;
	}

	/**
	 * @see org.eclipse.tml.localization.tools.persistence.IFilePersistentData#getPersistentData()
	 */
	public List<IPersistentData> getPersistentData() {
		List<IPersistentData> persistentData = new ArrayList<IPersistentData>();
		return persistentData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.tml.localization.tools.persistence.IPersistentData#
	 * getPersistableAttributes()
	 */
	public PersistableAttributes getPersistableAttributes() {
		return null;
	}

	/**
	 * Check whether the file shall be deleted or not
	 * 
	 * @return true if the shall be deleted or not, false otherwise
	 */
	public boolean isToBeDeleted() {
		return toBeDeleted;
	}

	/**
	 * Set whether the file shall be deleted or not
	 * 
	 * @param shallBeDeleted
	 *            true if the shall be deleted or not, false otherwise
	 */
	public void setToBeDeleted(boolean toBeDeleted) {
		this.toBeDeleted = toBeDeleted;
	}

	@Override
	public boolean equals(Object obj) {
		boolean result = true;
		LocalizationFile locFile = (LocalizationFile) obj;
		List<StringNode> locFileStringNodes = locFile.getStringNodes();
		if (stringNodes.size() != locFileStringNodes.size()) {
			result = false;
		} else {
			boolean keyEqual, valueEqual;
			for (int i = 0; i < stringNodes.size(); i++) {
				keyEqual = stringNodes.get(i).getKey().equals(
						locFileStringNodes.get(i).getKey());
				valueEqual = stringNodes.get(i).getValue().equals(
						locFileStringNodes.get(i).getValue());
				if ((!keyEqual) || (!valueEqual)) {
					result = false;
					break;
				}
			}
		}
		return result;
	}
}
