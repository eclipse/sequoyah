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
 * Matheus Lima (Eldorado) - Bug [326793] - Fixed array support for the String Localization Editor
 * Paulo Faria (Eldorado) - Bug 326793 - Improvements on the String Arrays handling 
 * Paulo Faria (Eldorado) - Bug [326793] -  Improvements on the String Arrays handling 
 ********************************************************************************/
package org.eclipse.sequoyah.localization.tools.datamodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.sequoyah.localization.editor.datatype.RowInfo;
import org.eclipse.sequoyah.localization.editor.datatype.RowInfoLeaf;
import org.eclipse.sequoyah.localization.tools.datamodel.node.ArrayStringNode;
import org.eclipse.sequoyah.localization.tools.datamodel.node.StringNode;

/**
 * This class represents a real localization file for strings
 */
public class StringLocalizationFile extends LocalizationFile {

	/*
	 * String nodes (single strings and array nodes) indexed by key
	 */
	private final Map<String, StringNode> stringNodesMap = new HashMap<String, StringNode>();

	/*
	 * Static code to add file type to factory's hashmap
	 */
	static {
		LocalizationFileFactory.getInstance().addFileType(
				StringLocalizationFile.class.getName(),
				StringLocalizationFile.class);
	}

	/**
	 * Default constructor.
	 */
	public StringLocalizationFile() {
	}

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
	public StringLocalizationFile(LocalizationFileBean bean) {
		super(bean);
		clearStringNodes();
		setStringNodes(bean.getStringNodes());
		setStringArrayNodes(bean.getStringArrays());
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
			locFile = new StringLocalizationFile(bean);
		} else {
			locFile = null;
		}
		return locFile;
	}

	/**
	 * Get the list of StringNodes (ONLY single strings, not arrays items nor
	 * arrays) which are part of the file
	 * 
	 * @return the list of StringNodes which are part of the file
	 */
	public List<StringNode> getStringNodes() {
		List<StringNode> stringNodes = new ArrayList<StringNode>();
		for (StringNode stringNode : stringNodesMap.values()) {
			if (!(stringNode instanceof ArrayStringNode)) {
				stringNodes.add(stringNode);
			}
		}
		return stringNodes;
	}

	/**
	 * Get the list of StringNodes (ONLY arrays, not arrays items nor single
	 * strings) which are part of the file
	 * 
	 * @return the list of ArrayStringNodes which are part of the file
	 */
	public List<ArrayStringNode> getStringArrays() {
		List<ArrayStringNode> arrayStringNodes = new ArrayList<ArrayStringNode>();
		for (StringNode stringNode : stringNodesMap.values()) {
			if (stringNode instanceof ArrayStringNode) {
				ArrayStringNode arrayNode = (ArrayStringNode) stringNode;
				arrayStringNodes.add(arrayNode);
			}
		}
		return arrayStringNodes;
	}

	/**
	 * Get the list of StringNodes (single strings, and array items, NOT arrays)
	 * which are part of the file
	 * 
	 * @return the list of StringNodes which are part of the file
	 */
	public List<StringNode> getNodesWithTextContent() {
		List<StringNode> stringNodes = new ArrayList<StringNode>();
		for (StringNode stringNode : stringNodesMap.values()) {
			if (!(stringNode instanceof ArrayStringNode)) {
				stringNodes.add(stringNode);
			} else {
				ArrayStringNode array = (ArrayStringNode) stringNode;
				for (StringNode child : array.getValues()) {
					stringNodes.add(child);
				}
			}
		}
		return stringNodes;
	}

	/**
	 * Get the list of StringNodes top levels (arrays or single strings, NOT
	 * array items) which are part of the file
	 * 
	 * @return the list of ArrayStringNodes which are part of the file
	 */
	public List<StringNode> getTopLevelNodes() {
		List<StringNode> topLevelStringNodes = new ArrayList<StringNode>();
		topLevelStringNodes.addAll(stringNodesMap.values());
		return topLevelStringNodes;
	}

	/**
	 * Creates a new node based on the type of the row (single or array)
	 * 
	 * @param rowInfo
	 * @return
	 */
	public StringNode createNode(RowInfo rowInfo) {
		StringNode newNode = null;
		if (rowInfo instanceof RowInfoLeaf) {
			// string or array item
			RowInfoLeaf leaf = (RowInfoLeaf) rowInfo;
			if (leaf.getParent() == null) {
				// string
				newNode = this.addStringNode(new StringNode(rowInfo.getKey(),
						""));
			} else {
				// array item - add parent to add value
				ArrayStringNode arrayNode = (stringNodesMap.containsKey(rowInfo
						.getKey()) ? (ArrayStringNode) stringNodesMap
						.get(rowInfo.getKey()) : new ArrayStringNode(
						rowInfo.getKey()));
				// do not use addStringNode because it is not a top level item
				// (array or string)
				newNode = arrayNode.addValue("", leaf.getPosition());
			}
		} else if (rowInfo instanceof RowInfo) {
			// array
			Map<Integer, RowInfoLeaf> children = rowInfo.getChildren();
			ArrayStringNode arrayNode = new ArrayStringNode(rowInfo.getKey());
			for (int pos = 0; pos < children.size(); pos++) {
				arrayNode.addValue("", pos);
			}
			newNode = this.addStringNode(arrayNode);
		}
		if (newNode != null) {
			newNode.setLocalizationFile(this);
			newNode.setDirty(true);
		}
		return newNode;
	}

	/**
	 * Get the StringNodes (top level nodes, that is single string or array, NOT
	 * array items) which represents a specific key.
	 * 
	 * 
	 * @param key
	 *            the StringNode key attribute
	 * @return the StringNode which represents the key passed as a parameter,
	 *         null if not found
	 */
	public StringNode getStringNodeByKey(String key, Boolean isArra) {
		StringNode result = stringNodesMap.get(key);
		return result;
	}

	public void clearStringNodes() {
		stringNodesMap.clear();
	}

	public boolean containsKey(String key) {
		return stringNodesMap.containsKey(key);
	}

	/**
	 * Set the list of StringNodes which are part of the file.
	 * 
	 * @param stringNodes
	 *            the list of StringNodes which are part of the file
	 */
	public void setStringNodes(List<StringNode> stringNodes) {
		if (stringNodes != null) {
			for (StringNode stringNode : stringNodes) {
				this.stringNodesMap.put(stringNode.getKey(), stringNode);
				stringNode.setLocalizationFile(this);
			}
		}
	}

	/**
	 * Set the list of StringArrays which are part of the file
	 * 
	 * @param stringArrayNodes
	 *            the list of StringArrays which are part of the file
	 */
	public void setStringArrayNodes(List<ArrayStringNode> stringArrayNodes) {
		if (stringArrayNodes != null) {
			for (ArrayStringNode stringArrayNode : stringArrayNodes) {
				List<StringNode> stringNodes = stringArrayNode.getValues();
				for (StringNode stringNode : stringNodes) {
					stringNode.setLocalizationFile(this);
				}
				stringArrayNode.setLocalizationFile(this);
				this.stringNodesMap.put(stringArrayNode.getKey(),
						stringArrayNode);
			}
		}
	}

	public void removeNode(String key) {
		this.stringNodesMap.remove(key);
	}

	/**
	 * Get only the modified StringNodes in this localization file
	 * 
	 * @return the modified StringNodes in this localization file
	 */
	public List<StringNode> getModifiedStringNodes() {
		List<StringNode> modifiedStringNodes = new ArrayList<StringNode>();
		for (StringNode stringNode : getNodesWithTextContent()) {
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

		newStringNode.setLocalizationFile(this);
		stringNodesMap.put(newStringNode.getKey(), newStringNode);

		this.setDirty(true);

		return newStringNode;
	}

	/**
	 * Remove a top level StringNode from the list (array or single string)
	 */
	public void removeStringNode(StringNode stringNode) {
		if (stringNodesMap.containsKey(stringNode.getKey())) {
			stringNodesMap.remove(stringNode.getKey());
			this.setDirty(true);
		}
	}

	/**
	 * Removes a non top level StringNode from the list (array item)
	 * 
	 * @param parent
	 * @param child
	 */
	public void removeStringNode(ArrayStringNode parent, StringNode child) {
		if (stringNodesMap.containsKey(parent.getKey())) {
			parent.removeValue(child);
			this.setDirty(true);
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
			List<StringNode> locFileStringNodes = locFile
					.getNodesWithTextContent();

			// skip blank array items
			List<StringNode> thisStringNodes = removeBlankArrayItems(getNodesWithTextContent());
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
			if (node instanceof ArrayStringNode) {
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
	 * Find and retrieve an String Array. If it doesn't exist, create a new one
	 * 
	 * @param key
	 *            array key
	 * @return StringArray object
	 */
	private ArrayStringNode findStringArray(String key) {
		if (ArrayStringNode.isArrayItem(key)) {
			key = ArrayStringNode.getArrayKeyFromItemKey(key);
		}
		ArrayStringNode stringArrayNode = null;
		for (ArrayStringNode sArray : this.getStringArrays()) {
			if (sArray.getKey().equals(key)) {
				stringArrayNode = sArray;
				break;
			}
		}
		if (stringArrayNode == null) {
			stringArrayNode = new ArrayStringNode(key);
			this.getStringArrays().add(stringArrayNode);
		}
		return stringArrayNode;
	}

}
