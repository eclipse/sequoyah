/********************************************************************************
 * Copyright (c) 2010 Motorola Mobility, Inc.
 * All rights reserved. This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Marcel Gorri (Eldorado) - Bug 326793 -  Improvements on the String Arrays handling  
 * Paulo Faria (Eldorado) - Bug [326793] - Starting new LFE workflow improvements (Refactor visitDomXYZ and NodeManagers)
 ********************************************************************************/
package org.eclipse.sequoyah.localization.pde.manager;

import java.util.ArrayList;
import java.util.Map;

import org.eclipse.sequoyah.localization.tools.datamodel.LocalizationFile;
import org.eclipse.sequoyah.localization.tools.datamodel.LocalizationFileBean;
import org.eclipse.sequoyah.localization.tools.datamodel.node.StringNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Encapsulates the knowledge about manipulating String Properties file for PDE
 */
public abstract class NodeManager {

	public abstract void loadFile(LocalizationFileBean bean,
			LocalizationFile locFile);

	public abstract void updateLocalizationFileContent(Document document,
			ArrayList<StringNode> stringNodes);

	public abstract void createFile(Document document, Element resources,
			LocalizationFile localizationFile);


	/**
	 * Extracts the text with the right formatting from DOM properties representation
	 * 
	 * @param valueText
	 *            string to return, in the initial recursion set it as ""
	 * @param firstChildNode
	 *            node to start iterating over siblings
	 */
	protected void getStringByNodes(StringBuffer valueText, Node firstChildNode) {
		
		Node auxNode = firstChildNode;
		while (auxNode != null) {

			if (auxNode.getNodeName() != null
					&& (auxNode.getNodeType() == Node.ELEMENT_NODE)) {
				
				NamedNodeMap nodeAttributes = auxNode.getAttributes();
				String nodeAttributesText = ""; //$NON-NLS-1$
				for (int i = 0; i < nodeAttributes.getLength(); i++) {
					Node nodeAttribute = nodeAttributes.item(i);
					nodeAttributesText += " " + nodeAttribute.toString(); //$NON-NLS-1$
				}

				// case: sibling with formatting node			
				valueText.append(auxNode.getNodeName() + nodeAttributesText); //$NON-NLS-1$
				if (auxNode.hasChildNodes()) {
					// recursion (step): sibling has internal formatting nodes
					getStringByNodes(valueText, auxNode.getFirstChild());
				} else {
					// recursion (base case): only simple text inside sibling
					if (auxNode.getNodeType() != Node.COMMENT_NODE) {
						valueText.append(auxNode.getTextContent());
					}
					
				}
			} else {
				// recursion (base case): simple text in the sibling
				if (auxNode.getNodeType() != Node.COMMENT_NODE) {
					valueText.append(auxNode.getTextContent());
				}
			}
			auxNode = auxNode.getNextSibling();
		}
	}

	public abstract void updateFile(LocalizationFile locFile,Map<String, StringNode> singleStringsToUpdateOrAdd);

}
