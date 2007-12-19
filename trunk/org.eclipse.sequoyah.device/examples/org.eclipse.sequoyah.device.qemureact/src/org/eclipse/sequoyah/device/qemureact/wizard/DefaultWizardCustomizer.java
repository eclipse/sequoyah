/********************************************************************************
 * Copyright (c) 2007 Motorola Inc. All rights reserved.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributor:
 * Fabio Fantato (Motorola)
 *
 * Contributors:
 * {Name} (company) - description of contribution.
 ********************************************************************************/
package org.eclipse.tml.device.qemureact.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.tml.framework.device.wizard.DeviceWizardConstants;
import org.eclipse.tml.framework.device.wizard.ui.AbstractWizardCustomizer;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

/**
 * This class allow user redefine the pages and customize the wizard
 * @author Fabio Fantato
 *
 */
public class DefaultWizardCustomizer extends AbstractWizardCustomizer {

	/**
	 * Create a factory and defines the pages that will be customized
	 */
	public DefaultWizardCustomizer(){
		super(true,false,false);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.tml.emulator.core.wizard.ui.AbstractWizardFactory#getProjectPage()
	 */
	public WizardPage getCustomizedProjectPage() {
		return new WizardNewProjectCreationPage(DeviceWizardConstants.PAGE_PROJECT);
	}
}
