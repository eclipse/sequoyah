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
 ********************************************************************************/
package org.eclipse.sequoyah.localization.pde.datamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.sequoyah.localization.pde.manager.LocalizationFileManagerFactory;
import org.eclipse.sequoyah.localization.pde.manager.StringLocalizationFileManager;
import org.eclipse.sequoyah.localization.tools.datamodel.LocalizationFileBean;
import org.eclipse.sequoyah.localization.tools.datamodel.LocalizationFileFactory;
import org.eclipse.sequoyah.localization.tools.datamodel.StringLocalizationFile;
import org.eclipse.sequoyah.localization.tools.datamodel.node.StringArrayItemNode;
import org.eclipse.sequoyah.localization.tools.datamodel.node.StringArrayNode;
import org.eclipse.sequoyah.localization.tools.datamodel.node.StringNode;
import org.w3c.dom.Document;

/**
 * This class represents a real PDE localization file in the project and
 * contains information about properties file saved
 * 
 */
public class PDEStringLocalizationFile extends StringLocalizationFile {

	static {
		LocalizationFileFactory.getInstance().addFileType(
				PDEStringLocalizationFile.class.getName(),
				PDEStringLocalizationFile.class);

		LocalizationFileManagerFactory.getInstance().addManager(
				PDEStringLocalizationFile.class.getName(),
				StringLocalizationFileManager.class);
	}

	/**
	 * Saved Properties (it is used not to lose comments on updates)
	 */
	private Document savedPDEDocument = null;

	/**
	 * Kept to remove a single entry to be removed from savedPDEDocument in the
	 * next save action
	 */
	private Map<String, StringNode> singleEntryToRemove = new HashMap<String, StringNode>();

	/**
	 * 
	 * @param file
	 * @param localeInfo
	 * @param stringNodes
	 * @param stringArrays
	 */
	public PDEStringLocalizationFile(LocalizationFileBean bean) {
		super(bean);
	}

	/**
	 * @return the savedPDEDocument
	 */
	public Document getSavedPDEDocument() {
		return savedPDEDocument;
	}

	/**
	 * @param savedPDEDocument
	 *            the savedPDEDocument to set
	 */
	public void setSavedPDEDocument(Document savedPDEDocument) {
		this.savedPDEDocument = savedPDEDocument;
	}

	/**
	 * Removes item from top level node
	 */
	public void removeStringNode(StringNode stringNode) {
		if (containsKey(stringNode.getKey())) {
			// top level: array or string
			removeNode(stringNode.getKey());
			this.setDirty(true);
			// check if it's is an array
			if (!(stringNode instanceof StringArrayNode)) {
				// mark single entry to be removed
				singleEntryToRemove.put(stringNode.getKey(), stringNode);
			}
		}
	}

	/**
	 * Adds StringNode. If necessary undoes removal mark from StringNode fixing
	 * maps to update savedPDEDocument file
	 * 
	 * @param stringNode
	 */
	public StringNode addStringNode(StringNode stringNode) {
		StringNode node = super.addStringNode(stringNode);
		// check if it's is an array
		if (!(stringNode instanceof StringArrayNode)) {
			if (singleEntryToRemove.containsKey(stringNode.getKey())) {
				singleEntryToRemove.remove(stringNode.getKey());
			}
		}
		return node;
	}

	/**
	 * @return the singleEntryToRemove
	 */
	public Map<String, StringNode> getSingleEntryToRemove() {
		return singleEntryToRemove;
	}
}
