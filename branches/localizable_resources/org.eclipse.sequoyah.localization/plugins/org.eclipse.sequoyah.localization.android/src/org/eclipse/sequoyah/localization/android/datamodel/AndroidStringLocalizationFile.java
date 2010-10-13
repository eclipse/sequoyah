/********************************************************************************
 * Copyright (c) 2009-2010 Motorola Mobility, Inc.
 * All rights reserved. This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Matheus Tait Lima (Eldorado)
 * 
 * Contributors:
 * Paulo Faria (Eldorado) - Add methods for not to lose comments on save
 * Daniel Pastore (Eldorado) - Bug 323036 - Add support to other Localizable Resources
 * Matheus Lima (Eldorado) - Bug 326793 - Updating data model so the Array Strings is now a new class
 * 
 ********************************************************************************/
package org.eclipse.sequoyah.localization.android.datamodel;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.sequoyah.localization.android.manager.LocalizationFileManagerFactory;
import org.eclipse.sequoyah.localization.android.manager.StringLocalizationFileManager;
import org.eclipse.sequoyah.localization.tools.datamodel.LocalizationFileBean;
import org.eclipse.sequoyah.localization.tools.datamodel.LocalizationFileFactory;
import org.eclipse.sequoyah.localization.tools.datamodel.StringLocalizationFile;
import org.eclipse.sequoyah.localization.tools.datamodel.node.ArrayStringNode;
import org.eclipse.sequoyah.localization.tools.datamodel.node.StringNode;
import org.w3c.dom.Document;

/**
 * This class represents a real Android localization file in the project and
 * contains information about XML file saved
 * 
 */
public class AndroidStringLocalizationFile extends StringLocalizationFile {

	static {
		LocalizationFileFactory.getInstance().addFileType(
				AndroidStringLocalizationFile.class.getName(),
				AndroidStringLocalizationFile.class);
		
		LocalizationFileManagerFactory.getInstance().addManager(
				AndroidStringLocalizationFile.class.getName(),
				StringLocalizationFileManager.class);
	}
	
	/**
	 * Saved XML (it is used not to lose comments on updates)
	 */
	private Document savedXMLDocument = null;

	/**
	 * Kept to remove a single entry to be removed from savedXMLDocument in the
	 * next save action
	 */
	private Map<String, StringNode> singleEntryToRemove = new HashMap<String, StringNode>();

	/**
	 * Kept to remove only one item inside array to be removed from
	 * savedXMLDocument in the next save action
	 */
	private Map<String, StringNode> arrayItemsToRemove = new HashMap<String, StringNode>();

	/**
	 * Kept to remove an entire array to be removed from savedXMLDocument in the
	 * next save action
	 */
	private Map<String, ArrayStringNode> arrayEntryToRemove = new HashMap<String, ArrayStringNode>();

	/**
	 * 
	 * @param file
	 * @param localeInfo
	 * @param stringNodes
	 * @param stringArrays
	 */
	public AndroidStringLocalizationFile(LocalizationFileBean bean){
		super(bean);
	}

	/**
	 * @return the savedXMLDocument
	 */
	public Document getSavedXMLDocument() {
		return savedXMLDocument;
	}

	/**
	 * @param savedXMLDocument
	 *            the savedXMLDocument to set
	 */
	public void setSavedXMLDocument(Document savedXMLDocument) {
		this.savedXMLDocument = savedXMLDocument;
	}

	/**
	 * @see org.eclipse.sequoyah.localization.tools.datamodel.StringLocalizationFile#removeStringNode(org.eclipse.sequoyah.localization.tools.datamodel.node.StringNode)
	 */
	@Override
	public void removeStringNode(StringNode stringNode) {
		if (getStringNodes().contains(stringNode)) {
			getStringNodes().remove(stringNode);
			getStringNodesMap().remove(stringNode.getKey());
			this.setDirty(true);
			// check if it's is an array
			if (stringNode instanceof ArrayStringNode) {
				ArrayStringNode arrayNode = (ArrayStringNode) stringNode ;
				arrayNode.removeValue(stringNode);
				if (arrayNode.getValues().size() == 0) {
					this.getStringArrays().remove(arrayNode);
					// mark entire array entry to be removed
					arrayEntryToRemove.put(
							arrayNode.getKey(), arrayNode);
				} else {
					// mark item array to be removed
					arrayItemsToRemove.put(stringNode.getKey(), stringNode);
				}
			} else {
				// mark single entry to be removed
				singleEntryToRemove.put(stringNode.getKey(), stringNode);
			}
		}
	}

	/**
	 * @return the singleEntryToRemove
	 */
	public Map<String, StringNode> getSingleEntryToRemove() {
		return singleEntryToRemove;
	}

	/**
	 * @return the arrayItemsToRemove
	 */
	public Map<String, StringNode> getArrayItemsToRemove() {
		return arrayItemsToRemove;
	}

	/**
	 * @return the arrayEntryToRemove
	 */
	public Map<String, ArrayStringNode> getArrayEntryToRemove() {
		return arrayEntryToRemove;
	}
}
