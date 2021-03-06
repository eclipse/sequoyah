/********************************************************************************
 * Copyright (c) 2009 Motorola Inc.
 * All rights reserved. This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Vinicius Hernandes (Motorola)
 * 
 * Contributors:
 * Marcelo Marzola Bossoni (Eldorado) - Bug [289146] - Performance and Usability Issues
 ********************************************************************************/
package org.eclipse.sequoyah.localization.tools;

import org.eclipse.sequoyah.localization.tools.managers.LocalizationManager;
import org.eclipse.ui.IStartup;

public class LocalizationToolsStartup implements IStartup {

	public void earlyStartup() {
		LocalizationManager.getInstance().initialize();
	}

}
