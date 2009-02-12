/********************************************************************************
 * Copyright (c) 2009 Motorola Inc and others.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Daniel Barboza Franco (Eldorado Research Institute) - Bug [247179] - Choice of service buttons orientation on Instance Mgt View should be persisted
 * 
 * Contributors:
 * name (company) - description.
 ********************************************************************************/
  
package org.eclipse.tml.framework.device.ui;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

	public PreferenceInitializer() {
	}

	@Override
	public void initializeDefaultPreferences() {

    	PlatformUI.getPreferenceStore().setDefault(DeviceUIPlugin.SERVICE_BUTTONS_ORIENTATION_PREFERENCE , SWT.HORIZONTAL);
    	PlatformUI.getPreferenceStore().setDefault(DeviceUIPlugin.FILTER_SERVICE_BY_AVAILABILITY_PREFERENCE , false);

	}

}
