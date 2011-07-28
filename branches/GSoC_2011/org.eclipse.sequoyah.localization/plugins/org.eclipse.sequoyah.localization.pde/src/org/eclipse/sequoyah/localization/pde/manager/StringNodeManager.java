/********************************************************************************
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Lucas Tiago de Castro Jesus (GSoC)
 * 
 ********************************************************************************/
package org.eclipse.sequoyah.localization.pde.manager;

import static org.w3c.dom.Node.COMMENT_NODE;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

import org.eclipse.sequoyah.localization.pde.IPDELocalizationSchemaConstants;
import org.eclipse.sequoyah.localization.tools.datamodel.LocalizationFile;
import org.eclipse.sequoyah.localization.tools.datamodel.LocalizationFileBean;
import org.eclipse.sequoyah.localization.tools.datamodel.StringLocalizationFile;
import org.eclipse.sequoyah.localization.tools.datamodel.node.NodeComment;
import org.eclipse.sequoyah.localization.tools.datamodel.node.StringNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Encapsulates the knowledge about manipulating Single String items inside
 * String
 */
public class StringNodeManager extends NodeManager implements
		IPDELocalizationSchemaConstants {

	public StringNodeManager() {

	}

	@Override
	public void loadFile(LocalizationFileBean bean, LocalizationFile locFile) {
		bean.setStringNodes(((StringLocalizationFile) locFile).getStringNodes());
	}

	public void updateLocalizationFileContent(Properties property,
			ArrayList<StringNode> stringNodes) {
		/*
		 * Get string nodes
		 */
		
		String key = null;
		String value = null;
		
		for (Enumeration keyProperties = property.propertyNames(); keyProperties.hasMoreElements();) {
			key = (String) keyProperties.nextElement();
			value = property.getProperty(key);
			
			StringNode stringNodeObj = new StringNode(key, value);
			stringNodes.add(stringNodeObj);
	     }
		/*
		for (int i = 0; i < stringNodeList.getLength(); i++) {
			Element stringNode = (Element) stringNodeList.item(i);
			key = stringNode.getAttributeNode(PDE_STRING_ATTR_NAME)
					.getNodeValue();
			// get formatted text from single (non-array) item
			Node auxNode = stringNode.getFirstChild();
			StringBuffer valueText = new StringBuffer();
			getStringByNodes(valueText, auxNode);
			value = valueText.toString();
			
			stringNode.toString();
			StringNode stringNodeObj = new StringNode(key, value);

			stringNodes.add(stringNodeObj);
		}*/
	}

	public void createFile(Properties property, Element resources,
			LocalizationFile localizationFile) {

		for (StringNode stringNode : ((StringLocalizationFile) localizationFile)
				.getStringNodes()) {
			addSingleEntry(property, resources, stringNode);
		}
	}

	/**
	 * Adds single entry into Properties PDE Localization file
	 * 
	 * @param property
	 * @param resources
	 * @param stringNode
	 */
	public void addSingleEntry(Properties property, Element resources,
			StringNode stringNode) {
		property.setProperty(stringNode.getKey(), stringNode.getValue());
		//Create an element of the type PDE_STRING_TAG = "string"
		//Element string = document.createElement(PDE_STRING_TAG);
		//Set the attribute name = value of the key
		//string.setAttribute(PDE_STRING_ATTR_NAME,stringNode.getKey());
		//string.appendChild(document.createTextNode(stringNode.getValue()));
		//resources.appendChild(string);
	}

	@Override
	public void updateFile(LocalizationFile locFile,
			Map<String, StringNode> singleStringsToUpdateOrAdd) {
		for (StringNode stringNode : ((StringLocalizationFile) locFile)
				.getStringNodes()) {
			singleStringsToUpdateOrAdd.put(stringNode.getKey(), stringNode);
		}
	}
}
