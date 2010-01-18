/********************************************************************************
 * Copyright (c) 2009 Motorola Inc. All rights reserved.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributor:
 * Fabio Fantato (Motorola)
 *
 * Contributors:
 * Fabio Fantato (Instituto Eldorado) - [263188] - Create new examples to support tutorial presentation
 * Fabio Fantato (Instituto Eldorado) - [243494] Change the reference implementation to work on Galileo
 ********************************************************************************/

package org.eclipse.tml.framework.device.ui.wizard;

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