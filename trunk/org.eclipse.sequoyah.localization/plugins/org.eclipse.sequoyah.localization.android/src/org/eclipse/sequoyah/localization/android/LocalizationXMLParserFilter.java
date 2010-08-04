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

public class LocalizationXMLParserFilter implements LSParserFilter {

	public short acceptNode(Node nodeArg) {
		// If the node is of text type, it could contain < or > that should not be escaped
		if (nodeArg.getNodeName() == "#text") { //$NON-NLS-1$
			if (nodeArg.getNodeValue().contains("<")){ //$NON-NLS-1$
				String content = nodeArg.getNodeValue().replaceAll("<", "&lt;"); //$NON-NLS-1$ //$NON-NLS-2$
				content = content.replaceAll(">", "&gt;"); //$NON-NLS-1$ //$NON-NLS-2$
				nodeArg.setNodeValue(content);
			}
		}
		return FILTER_ACCEPT;
	}

	public int getWhatToShow() {
		return NodeFilter.SHOW_ALL;
	}

	public short startElement(Element elementArg) {
		return FILTER_ACCEPT;
	}

}