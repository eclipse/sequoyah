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

package org.eclipse.sequoyah.device.framework.wizard.model;

import org.eclipse.jface.wizard.WizardPage;


/**
 * Define a wizard factory interface
 * @author Fabio Fantato
 *
 */
public interface IWizardCustomizer {
	/**
	 * @return
	 */
	public WizardPage getCustomizedProjectPage();
	/**
	 * @return
	 */
	public WizardPage getCustomizedPropertyPage();
	/**
	 * @return
	 */
	public WizardPage getCustomizedOtherPage();
	/**
	 * @return
	 */
	public boolean hasCustomizedProjectPage();
	/**
	 * @return
	 */
	public boolean hasCustomizedPropertyPage();
	/**
	 * 
	 * @return
	 */
	public boolean hasCustomizedOtherPage();
}
