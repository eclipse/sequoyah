/********************************************************************************
 * Copyright (c) 2007 Motorola Inc.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Fabio Fantato (Motorola)
 * 
 * Contributors:
 * name (company) - description.
 ********************************************************************************/

package org.eclipse.tml.framework.device.model;

import java.util.Properties;

import org.eclipse.core.runtime.IPath;

/**
 * Define properties for project creation
 */
public interface IInstanceBuilder {
	/**
	 * Gets the name of project
	 * @return
	 */
	public String getProjectName();
	/**
	 * Gets the location path
	 * @return
	 */
	public IPath getLocationPath();
	public Properties getProperties();
	public String getProperty(String key);
	
}
