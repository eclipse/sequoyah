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

import org.eclipse.tml.device.qemureact.QEmuReactPlugin;
import org.eclipse.tml.framework.device.wizard.ui.AbstractNewEmulatorInstanceWizard;


/**
 * New wizard specific for this emulator
 * @author Fabio Fantato
 *
 */
public class DefaultNewEmulatorInstanceWizard extends AbstractNewEmulatorInstanceWizard {
	
	public DefaultNewEmulatorInstanceWizard(){
		super(QEmuReactPlugin.DEVICE_ID,QEmuReactPlugin.PLUGIN_ID);		
	}
		
}
