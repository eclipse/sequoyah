/********************************************************************************
 * Copyright (c) 2010 Motorola Mobility Inc.
 * All rights reserved. All rights reserved. This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Daniel Drigo Pastore, Marcel Augusto Gorri (Eldorado) - Bug 312971 - Localization Editor does not accept < and > characters
 * 
 ********************************************************************************/
package org.eclipse.sequoyah.localization.android;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ls.LSParserFilter;
import org.w3c.dom.traversal.NodeFilter;

/**
 * The LocalizationXMLParserFilter provides a way to escape correctly '<' and '>' characters
 * when reading and saving xml localization files.
 */
public class LocalizationXMLParserFilter implements LSParserFilter {

	/**
	 * Checks if the content of a node from a DOM Document is from text type, 
	 * and if so, checks if it contains any '<' or '>' characters. In this case, 
	 * escapes correctly those characters by '&lt' and '&gt'. 
	 * 
	 * @param nodeArg
	 *            DOM Document node
	 * @return accept the node
	 */
	public short acceptNode(Node nodeArg) {
		// If the node is of text type, it could contain '<' or '>' that should not be escaped
		// automatically, but replaced by '&lt' or '&gt' instead
		if (nodeArg.getNodeName() == "#text") { //$NON-NLS-1$
			if ((nodeArg.getNodeValue().contains("<"))||(nodeArg.getNodeValue().contains(">"))){ //$NON-NLS-1$ //$NON-NLS-2$
				String content = nodeArg.getNodeValue().replaceAll("<", "&lt;"); //$NON-NLS-1$ //$NON-NLS-2$
				content = content.replaceAll(">", "&gt;"); //$NON-NLS-1$ //$NON-NLS-2$
				nodeArg.setNodeValue(content);
			}
		}
		return FILTER_ACCEPT;
	}

	/**
	 * Tells the LSParser what types of nodes to show to the method 
	 * LSParserFilter.acceptNode.
	 * 
	 * @return show all nodes
	 */
	public int getWhatToShow() {
		return NodeFilter.SHOW_ALL;
	}

	/**
	 * The parser will call this method after each Element start tag has been scanned, but 
	 * before the remainder of the Element is processed. The intent is to allow the element, 
	 * including any children, to be efficiently skipped. 
	 * 
	 * @param elementArg
	 *            The newly encountered element. 
	 * @return true if the Element should be included in the DOM document being built. 
	 */
	public short startElement(Element elementArg) {
		return FILTER_ACCEPT;
	}

}