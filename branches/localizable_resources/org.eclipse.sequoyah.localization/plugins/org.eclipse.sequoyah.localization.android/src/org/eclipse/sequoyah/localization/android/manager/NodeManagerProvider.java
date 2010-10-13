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

import java.util.ArrayList;

/**
 * 
 *
 */
public class NodeManagerProvider {
	/**
	 * Private instance of this factory for singleton purposes.
	 */
	private volatile static NodeManagerProvider nodeManagerProvider;

	/**
	 * Private map for keeping LocalizationFile instances for creation.
	 */
	private ArrayList<NodeManager> list = new ArrayList<NodeManager>();

	/**
	 * Store NodeManager classes for creation.
	 * 
	 */
	private void addNodeManagers() {
		NodeManager stringNodeManager = new StringNodeManager();
		list.add(stringNodeManager);
		NodeManager arrayStringNodeManager = new ArrayStringNodeManager();
		list.add(arrayStringNodeManager);
	}

	/**
	 * Default constructor (private since it is a singleton).
	 */
	private NodeManagerProvider() {
		addNodeManagers();
	}

	/**
	 * This method provides a single instance of this factory for whoever needs
	 * to use it.
	 * 
	 * @return unique instance of this factory
	 */
	public static NodeManagerProvider getInstance() {
		if (nodeManagerProvider == null) {
			synchronized (NodeManagerProvider.class) {
				if (nodeManagerProvider == null) {
					nodeManagerProvider = new NodeManagerProvider();
				}
			}
		}
		return nodeManagerProvider;
	}

	/**
	 * Method responsible for creating the different types of LocalizationFile
	 * based on the type attribute of the LocalizationFileBean received as
	 * parameter.
	 * 
	 * @param bean
	 *            Bean containing all information necessary for the creation of
	 *            a LocalizationFile.
	 * @return LocalizationFile created if the parameter received is not null.
	 */
	public ArrayList<NodeManager> getNodeManagers() {
		return list;
	}
}