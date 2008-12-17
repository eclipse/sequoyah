/********************************************************************************
 * Copyright (c) 2007-2008 Motorola Inc and others. All rights reserved.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributor:
 * Fabio Fantato (Motorola)
 *
 * Contributors:
 * Otavio Luiz Ferranti (Eldorado Research Institute) - bug#221733 - Enhancing instance wizard
 * Fabio Fantato (Eldorado Research Institute) - [221733] Persistence and New wizard for manage Device Instances
 ********************************************************************************/
package org.eclipse.tml.device.qemuarm.wizard;

import org.eclipse.tml.framework.device.wizard.ui.AbstractWizardCustomizer;

/**
 * This class allow user redefine the pages and customize the wizard
 * @author Fabio Fantato
 */
public class DefaultWizardCustomizer extends AbstractWizardCustomizer {

	/**
	 * Constructor - Create a factory and defines the pages that will be customized
	 */
	public DefaultWizardCustomizer(){
		super(false,false,false);
	}
}
