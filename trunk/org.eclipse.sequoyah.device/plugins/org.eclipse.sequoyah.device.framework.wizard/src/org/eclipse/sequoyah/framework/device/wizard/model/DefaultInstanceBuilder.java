/********************************************************************************
 * Copyright (c) 2007-2008 Motorola Inc.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Fabio Fantato (Motorola)
 * 
 * Contributors:
 * Fabio Fantato (Motorola) - bug#221736 - new instance wizard
 * Ot�vio Luiz Ferranti (Eldorado Research Institute) - bug#221733 - removing the
 *                              project location field from the default project page
 ********************************************************************************/
package org.eclipse.tml.framework.device.wizard.model;

import java.util.Properties;

import org.eclipse.core.runtime.IPath;
import org.eclipse.tml.framework.device.model.IInstanceBuilder;
import org.eclipse.tml.framework.device.wizard.model.IWizardProjectPage;

/**
 * 
 * @author Fabio Fantato
 *
 */
public class DefaultInstanceBuilder implements IInstanceBuilder {
	private IWizardProjectPage page;
	private Properties properties;
	
	public DefaultInstanceBuilder(IWizardProjectPage page,Properties properties){
		this.page = page;
		this.properties = properties;
	}

	public IPath getLocationPath() {
		return null;
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
