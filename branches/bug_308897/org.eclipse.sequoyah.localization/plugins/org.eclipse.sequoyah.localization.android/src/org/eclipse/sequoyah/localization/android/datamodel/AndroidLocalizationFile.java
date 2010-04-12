/********************************************************************************
 * Copyright (c) 2010 Motorola Inc.
 * All rights reserved. All rights reserved. This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Matheus Tait Lima (Eldorado)
 * 
 * Contributors:
 * Paulo Faria (Eldorado) - Add methods for not to lose comments on save
 * 
 ********************************************************************************/
package org.eclipse.sequoyah.localization.android.datamodel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.sequoyah.localization.tools.datamodel.LocaleInfo;
import org.eclipse.sequoyah.localization.tools.datamodel.LocalizationFile;
import org.eclipse.sequoyah.localization.tools.datamodel.StringArray;
import org.eclipse.sequoyah.localization.tools.datamodel.StringNode;
import org.w3c.dom.Document;

/**
 * This class represents a real Android localization file in the project and
 * contains information about XML file saved
 * 
 */
public class AndroidLocalizationFile extends LocalizationFile {

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
	private Map<String, StringArray> arrayEntryToRemove = new HashMap<String, StringArray>();

	/**
	 * 
	 * @param file
	 * @param localeInfo
	 * @param stringNodes
	 * @param stringArrays
	 */
	public AndroidLocalizationFile(IFile file, LocaleInfo localeInfo,
			List<StringNode> stringNodes, List<StringArray> stringArrays) {
		super(file, localeInfo, stringNodes, stringArrays);
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
	 * @see org.eclipse.sequoyah.localization.tools.datamodel.LocalizationFile#removeStringNode(org.eclipse.sequoyah.localization.tools.datamodel.StringNode)
	 */
	@Override
	public void removeStringNode(StringNode stringNode) {
		if (getStringNodes().contains(stringNode)) {
			getStringNodes().remove(stringNode);
			getStringNodesMap().remove(stringNode.getKey());
			this.setDirty(true);
			// check if it's is an array
			if (stringNode.isArray()) {
				stringNode.getStringArray().removeValue(stringNode);
				if (stringNode.getStringArray().getValues().size() == 0) {
					this.getStringArrays().remove(stringNode.getStringArray());
					// mark entire array entry to be removed
					arrayEntryToRemove.put(
							stringNode.getStringArray().getKey(), stringNode
									.getStringArray());
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
	public Map<String, StringArray> getArrayEntryToRemove() {
		return arrayEntryToRemove;
	}
}
