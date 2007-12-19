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
package org.eclipse.tml.framework.device.wizard.model;

import org.eclipse.core.runtime.IPath;
import org.eclipse.tml.framework.device.model.IInstanceBuilder;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

public class DefaultInstanceBuilder implements IInstanceBuilder {
	private WizardNewProjectCreationPage page;
	private String propertiesFile;
	private String propertiesPath;
	
	public DefaultInstanceBuilder(WizardNewProjectCreationPage page,String propertiesFile,String propertiesPath){
		this.page= page;
		this.propertiesFile=propertiesFile;
		this.propertiesPath=propertiesPath;
	}

	public IPath getLocationPath() {
		return page.getLocationPath();
	}

	public String getProjectName() {
		return page.getProjectName();
	}

	public String getPropertiesFile() {
		return propertiesFile;
	}

	public String getPropertiesPath() {
		return propertiesPath;
	}

}
