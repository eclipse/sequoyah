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
 * 
 ********************************************************************************/
package org.eclipse.sequoyah.localization.tools.datamodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.sequoyah.localization.tools.datamodel.node.ArrayStringNode;
import org.eclipse.sequoyah.localization.tools.datamodel.node.StringNode;

/**
 * This class represents a real localization file for strings
 */
public class StringLocalizationFile extends LocalizationFile {

	/*
	 * The list of StringNodes which are part of the file
	 */
	private List<StringNode> stringNodes = new ArrayList<StringNode>();

	/*
	 * The list of StringArrays which are part of the file
	 */
	private List<ArrayStringNode> stringArrayNodes = new ArrayList<ArrayStringNode>();

	/*
	 * String nodes indexed by key
	 */
	private Map<String, StringNode> stringNodesMap = new HashMap<String, StringNode>();

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
	public StringLocalizationFile (){
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
	public List<ArrayStringNode> getStringArrays() {
		return stringArrayNodes;
	}

	/**
	 * Get the StringNodes which represents a specific key. If there is no node
	 * for this key, a new node is created and returned
	 * 
	 * @param key
	 *            the StringNode key attribute
	 * @param isArray 
	 * @return the StringNode which represents the key passed as a parameter
	 */
	public StringNode getStringNodeByKey(String key, Boolean isArray) {
		//case 1: try to find as string item
		StringNode result = stringNodesMap.get(key);
		
		// TODO: split this method in 2,  this one should only return existent nodes and the other should create nodes.
		// If a node does not exist, it should not be created because in some points we can't be sure if a node
		// is an array or not.
		if (result == null) {
			//case 2: not found as string item, try to find as array item
			//remove underscore to get key
			int indexOfUnderscore = key.indexOf("_"); 
			if (indexOfUnderscore>=0){
				String arrayPrefix = key.substring(0, indexOfUnderscore);
				//get index
				String arrayIndexStr = key.substring(indexOfUnderscore+1); 
				Integer arrayIndex = Integer.valueOf(arrayIndexStr);
				if (result==null && isArray==null){
					int id = stringArrayNodes.indexOf(new ArrayStringNode(arrayPrefix));
					if (id>=0){
						ArrayStringNode arrayNode = stringArrayNodes.get(id);				
						result = arrayNode.getValues().get(arrayIndex);						
					}
				}
			}
			if (result==null){
				//case 3: not found as array nor string => create new node
				StringNode newNode = (isArray) ? new ArrayStringNode(key) : new StringNode(key, ""); //$NON-NLS-1$
				//StringNode newNode =  new StringNode(key, ""); //$NON-NLS-1$
				newNode.setLocalizationFile(this);
				result = this.addStringNode(newNode);
			}
		}
		result.setLocalizationFile(this);
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
	public void setStringNodes(List<StringNode> stringNodes){

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
	 * @param stringArrayNodes
	 *            the list of StringArrays which are part of the file
	 */
	public void setStringArrayNodes(List<ArrayStringNode> stringArrayNodes) {
		if (stringArrayNodes != null) {
			this.stringArrayNodes.clear(); //clears actual content do add new one
			
			/*
			 * Removed this for a while because we have a separated list of
			 * arrayNodes. With this code they were being replicated
			for (ArrayStringNode stringArrayNode : stringArrayNodes) {
				List<StringNode> stringNodes = stringArrayNode.getValues();
				for (StringNode stringNode : stringNodes) {
					this.stringNodesMap.put(stringNode.getKey(), stringNode);
					stringNode.setLocalizationFile(this);
				}
				this.stringNodes.addAll(stringNodes);
			}
			*/
			this.stringArrayNodes.addAll(stringArrayNodes);
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

		newStringNode.setLocalizationFile(this);
		stringNodes.add(newStringNode);
		stringNodesMap.put(newStringNode.getKey(), newStringNode);

		this.setDirty(true);

		return newStringNode;
	}
	
	/**
	 * Add a new ArrayStringNode to the list
	 */
	public ArrayStringNode addStringArraynode(ArrayStringNode arrayStringNode){
		ArrayStringNode newArrayStringNode;
		ArrayStringNode arrStringNode = findStringArray(arrayStringNode.getKey());
		int position = -1;
		if (ArrayStringNode.isArrayItem(arrayStringNode.getKey())) {
			position = ArrayStringNode.findItemPosition(arrayStringNode.getKey());
		}
		return newArrayStringNode = (ArrayStringNode) arrStringNode.addValue(arrayStringNode.getValue(),
				((position != -1) ? position : null));
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
		for (ArrayStringNode sArray : this.stringArrayNodes) {
			if (sArray.getKey().equals(key)) {
				stringArrayNode = sArray;
				break;
			}
		}
		if (stringArrayNode == null) {
			stringArrayNode = new ArrayStringNode(key);
			this.stringArrayNodes.add(stringArrayNode);
		}
		return stringArrayNode;
	}

}
