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
 * Fabio Fantato (Motorola) - bug#221736 - new instance wizard
 ********************************************************************************/
package org.eclipse.tml.framework.device.wizard.model;

import java.util.Properties;

import org.eclipse.core.runtime.IPath;
import org.eclipse.tml.framework.device.model.IInstanceBuilder;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

public class DefaultInstanceBuilder implements IInstanceBuilder {
	private WizardNewProjectCreationPage page;
	private Properties properties;
	
	public DefaultInstanceBuilder(WizardNewProjectCreationPage page,Properties properties){
		this.page= page;
		this.properties=properties;
	}

	public IPath getLocationPath() {
		return page.getLocationPath();
	}

	public String getProjectName() {
		return page.getProjectName();
	}

	public Properties getProperties() {
		return properties;
	}

	public String getProperty(String key) {
		return properties.getProperty(key);
	}

}
