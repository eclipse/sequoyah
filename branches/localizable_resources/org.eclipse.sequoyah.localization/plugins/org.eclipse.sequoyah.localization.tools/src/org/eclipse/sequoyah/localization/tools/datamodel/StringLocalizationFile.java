/********************************************************************************
 * Copyright (c) 2009-2010 Motorola Mobility, Inc.
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
 * Vinicius Rigoni Hernandes (Eldorado) - Bug [289885] - Localization Editor doesn't recognize external file changes
 * Fabricio Violin (Eldorado) - Bug [317065] - Localization file initialization bug 
 * Daniel Pastore (Eldorado) - Bug [323036] - Add support to other localizable resources
 * 
 ********************************************************************************/
package org.eclipse.sequoyah.localization.tools.datamodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.sequoyah.localization.tools.datamodel.node.StringArray;
import org.eclipse.sequoyah.localization.tools.datamodel.node.StringNode;

/**
 * This class represents a real localization file for strings
 */
public class StringLocalizationFile extends LocalizationFile {

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
	public StringLocalizationFile(IFile file, LocaleInfo localeInfo,
			List<StringNode> stringNodes, List<StringArray> stringArrays) {
		super(file, localeInfo);
		this.stringNodes = new ArrayList<StringNode>();
		this.stringArrays = new ArrayList<StringArray>();
		setStringNodes(stringNodes);
		setStringArrays(stringArrays);
	}

	/**
	 * StringLocalizationFile knows how to create itself.
	 * 
	 * @param bean
	 *            Bean containing all information necessary to create any type
	 *            of LocalizationFile.
	 * @return LocalizationFile created (if the parameter received is not null).
	 */
	public static LocalizationFile create(LocalizationFileBean bean) {
		LocalizationFile locFile;
		if (bean != null) {
			locFile = new StringLocalizationFile(bean.getFile(),
					bean.getLocale(), bean.getStringNodes(),
					bean.getStringArrays());
		} else {
			locFile = null;
		}
		return locFile;
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
	 * Get the list of StringNodes which are part of the file
	 * 
	 * @return the list of StringNodes which are part of the file
	 */
	public List<StringArray> getStringArrays() {
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
	public StringNode getStringNodeByKey(String key) {
		boolean isArray = false;
		for (StringArray stringArray : this.getLocalizationProject()
				.getAllStringArrays()) {
			if (stringArray.isPartOfTheArray(key)) {
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
	 * @param key
	 *            the StringNode key attribute
	 * @param isArray
	 *            if it's an array or not
	 * @return the StringNode which represents the key passed as a parameter
	 */
	public StringNode getStringNodeByKey(String key, boolean isArray) {
		StringNode result = stringNodesMap.get(key);
		if (result == null) {
			StringNode newNode = new StringNode(key, ""); //$NON-NLS-1$
			newNode.setLocalizationFile(this);
			newNode.setArray(isArray);
			result = this.addStringNode(newNode);
		}
		return result;
	}

	/**
	 * Set the list of StringNodes which are part of the file. NOTE: it will
	 * clear the StringNodes associated with StringArray. You should call
	 * setStringArrays after this operation.
	 * 
	 * @param stringNodes
	 *            the list of StringNodes which are part of the file
	 */
	public void setStringNodes(List<StringNode> stringNodes) {
		this.stringNodes.clear();
		stringNodesMap.clear();
		if (stringNodes != null) {
			for (StringNode stringNode : stringNodes) {
				this.stringNodesMap.put(stringNode.getKey(), stringNode);
				stringNode.setLocalizationFile(this);

			}
			this.stringNodes.addAll(stringNodes);
		}
	}

	/**
	 * Set the list of StringArrays which are part of the file
	 * 
	 * @param stringArrays
	 *            the list of StringArrays which are part of the file
	 */
	public void setStringArrays(List<StringArray> stringArrays) {
		if (stringArrays != null) {
			this.stringArrays.clear();
			this.stringArrays.clear();
			for (StringArray stringArray : stringArrays) {
				List<StringNode> stringNodes = stringArray.getValues();
				for (StringNode stringNode : stringNodes) {
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
	public StringNode addStringNode(StringNode stringNode) {
		StringNode newStringNode = stringNode;

		// check if it's is an array
		if (stringNode.isArray()) {
			StringArray stringArray = findStringArray(stringNode.getKey());
			int position = -1;
			if (StringArray.isArrayItem(stringNode.getKey())) {
				position = StringArray.findItemPosition(stringNode.getKey());
			}
			newStringNode = stringArray.addValue(stringNode.getValue(),
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
	public void removeStringNode(StringNode stringNode) {
		if (stringNodes.contains(stringNode)) {
			stringNodes.remove(stringNode);
			stringNodesMap.remove(stringNode.getKey());
			this.setDirty(true);
			// check if it's is an array
			if (stringNode.isArray()) {
				stringNode.getStringArray().removeValue(stringNode);
				if (stringNode.getStringArray().getValues().size() == 0) {
					this.stringArrays.remove(stringNode.getStringArray());
				}
			}
		}
	}

	@Override
	public boolean equals(Object obj) {
		boolean result = true;

		// if (!this.getLocaleInfo().equals(
		// ((StringLocalizationFile) obj).getLocaleInfo())) {
		//
		// result = false;
		// }
		if (!super.equals(obj)) {
			result = false;
		} else {
			StringLocalizationFile locFile = (StringLocalizationFile) obj;
			List<StringNode> locFileStringNodes = locFile.getStringNodes();

			// skip blank array items
			List<StringNode> thisStringNodes = removeBlankArrayItems(stringNodes);
			List<StringNode> otherStringNodes = removeBlankArrayItems(locFileStringNodes);

			Collections.sort(thisStringNodes);
			Collections.sort(otherStringNodes);

			if ((thisStringNodes.size() != otherStringNodes.size())) {
				result = false;
			} else {
				boolean keyEqual, valueEqual;
				for (int i = 0; i < thisStringNodes.size(); i++) {
					keyEqual = thisStringNodes.get(i).getKey()
							.equals(otherStringNodes.get(i).getKey());
					String EOL = System.getProperty("line.separator"); //$NON-NLS-1$
					String fromFile = thisStringNodes.get(i).getValue()
							.replaceAll(EOL, "\n"); //$NON-NLS-1$
					valueEqual = fromFile.equals(otherStringNodes.get(i)
							.getValue());
					if ((!keyEqual) || (!valueEqual)) {
						result = false;
						break;
					}
				}
			}
		}
		return result;
	}

	/**
	 * Remove blank array items
	 * 
	 * @param nodes
	 *            all nodes
	 * @return only non blank array items
	 */
	private List<StringNode> removeBlankArrayItems(List<StringNode> nodes) {
		List<StringNode> noBlankArrayItems = new ArrayList<StringNode>();

		for (StringNode node : nodes) {
			if (node.isArray()) {
				if (!node.getValue().equals("")) { //$NON-NLS-1$
					noBlankArrayItems.add(node);
				}
			} else {
				noBlankArrayItems.add(node);
			}
		}
		return noBlankArrayItems;
	}

	/**
	 * Find and retrieve an String Array If it doesn't exist, create a new one
	 * 
	 * @param key
	 *            array key
	 * @return StringArray object
	 */
	private StringArray findStringArray(String key) {
		if (StringArray.isArrayItem(key)) {
			key = StringArray.getArrayKeyFromItemKey(key);
		}
		StringArray stringArray = null;
		for (StringArray sArray : this.stringArrays) {
			if (sArray.getKey().equals(key)) {
				stringArray = sArray;
				break;
			}
		}
		if (stringArray == null) {
			stringArray = new StringArray(key);
			this.stringArrays.add(stringArray);
		}
		return stringArray;
	}

}
