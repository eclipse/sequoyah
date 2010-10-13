/********************************************************************************
 * Copyright (c) 2010 Motorola Mobility, Inc.
 * All rights reserved. This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Marcel Gorri (Eldorado) - Bug 326793 -  Improvements on the String Arrays handling  
 * 
 ********************************************************************************/
package org.eclipse.sequoyah.localization.android.manager;

import static org.w3c.dom.Node.COMMENT_NODE;

import java.util.ArrayList;
import java.util.Map;

import org.eclipse.sequoyah.localization.android.IAndroidLocalizationSchemaConstants;
import org.eclipse.sequoyah.localization.tools.datamodel.LocalizationFile;
import org.eclipse.sequoyah.localization.tools.datamodel.LocalizationFileBean;
import org.eclipse.sequoyah.localization.tools.datamodel.StringLocalizationFile;
import org.eclipse.sequoyah.localization.tools.datamodel.node.ArrayStringNode;
import org.eclipse.sequoyah.localization.tools.datamodel.node.NodeComment;
import org.eclipse.sequoyah.localization.tools.datamodel.node.StringNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 *
 */
public class ArrayStringNodeManager extends NodeManager implements
		IAndroidLocalizationSchemaConstants {

	public ArrayStringNodeManager() {

	}

	@Override
	public void loadFile(LocalizationFileBean bean, LocalizationFile locFile) {
		bean.setStringArrays(((StringLocalizationFile) locFile)
				.getStringArrays());
	}

	@Override
	public void updateLocalizationFileContent(Document document,
			ArrayList<StringNode> stringNodes) {
		/*
		 * Get array nodes
		 */
		NodeList arrayNodeList = document
				.getElementsByTagName(XML_STRING_ARRAY_TAG);
		String arrayKey = null;
		String arrayValue = null;
		for (int i = 0; i < arrayNodeList.getLength(); i++) {
			Element arrayNode = (Element) arrayNodeList.item(i);
			arrayKey = arrayNode.getAttributeNode(XML_STRING_ATTR_NAME)
					.getNodeValue();
			ArrayStringNode stringArray = new ArrayStringNode(arrayKey);
			if (arrayNode.hasChildNodes()) {
				NodeList arrayItems = arrayNode
						.getElementsByTagName(XML_STRING_ARRAY_ITEM_TAG);
				for (int j = 0; j < arrayItems.getLength(); j++) {
					Node childN = arrayItems.item(j);

					// get formatted text from array item
					Node auxNode = childN.getFirstChild();
					StringBuffer valueText = new StringBuffer();
					getStringByNodes(valueText, auxNode);
					arrayValue = valueText.toString();

					StringNode newNode = stringArray.addValue(arrayValue);

					// comments
					String comment = null;
					if (childN.hasChildNodes()) {
						NodeList childs = childN.getChildNodes();
						for (int k = 0; k < childs.getLength(); k++) {
							Node commentNode = childs.item(k);
							if (commentNode.getNodeType() == COMMENT_NODE) {
								comment = commentNode.getNodeValue();
							}
						}

					}

					if (comment != null) {
						NodeComment nodeComment = new NodeComment();
						nodeComment.setComment(comment);
						newNode.setNodeComment(nodeComment);
					}

				}

			}
			stringNodes.add(stringArray);
		}
	}

	@Override
	public void createFile(Document document, Element resources,
			LocalizationFile localizationFile) {

		for (ArrayStringNode stringArray : ((StringLocalizationFile) localizationFile)
				.getStringArrays()) {
			addArrayEntry(document, resources, stringArray);
		}

	}

	/**
	 * Adds array entry into XML Android Localization file
	 * 
	 * @param document
	 * @param resources
	 * @param stringArray
	 */
	private void addArrayEntry(Document document, Element resources,
			ArrayStringNode stringArray) {
		Element array = document.createElement(XML_STRING_ARRAY_TAG);
		array.setAttribute(XML_STRING_ATTR_NAME, stringArray.getKey());
		for (StringNode stringNode : stringArray.getValues()) {
			createArrayItem(document, array, stringNode);
		}
		resources.appendChild(array);
	}

	/*
	 * 
	 */
	private void createArrayItem(Document document, Element array,
			StringNode stringNode) {
		Element arrayItem = document.createElement(XML_STRING_ARRAY_ITEM_TAG);
		arrayItem.appendChild(document.createTextNode(stringNode.getValue()));
		array.appendChild(arrayItem);

		createOrUpdateComment(document, stringNode, arrayItem);
	}

	@Override
	public void updateFile(LocalizationFile locFile,
			Map<String, StringNode> arrayStringsToUpdateOrAdd) {
		for (ArrayStringNode stringArray : ((StringLocalizationFile)locFile).getStringArrays()) {
			arrayStringsToUpdateOrAdd
					.put(stringArray.getKey(), stringArray);
		}
	}

	@Override
	public void visitToAddDOMChildren(Document document,
			Map<String, StringNode> stringsToUpdateOrAdd, Element resource) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitToUpdateDOMChildren(Document document, Node visitingNode,
			String attrName, Map<String, StringNode> singleStringsToUpdateOrAdd) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitToRemoveDOMChildren(Document document, Node visitingNode,
			String attrName, Map<String, StringNode> arrayItemsToRemove) {
		// TODO Auto-generated method stub

	}

}
