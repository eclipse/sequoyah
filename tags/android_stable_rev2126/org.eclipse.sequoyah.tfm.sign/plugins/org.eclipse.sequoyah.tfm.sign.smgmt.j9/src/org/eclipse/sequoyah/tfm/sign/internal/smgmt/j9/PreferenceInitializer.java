/**
 * Copyright (c) 2006,2009 IBM Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation         - initial API and implementation
 *     Diego Sandin (Motorola) - Porting code to TFM Sign Framework [Bug 286387]
 */
package org.eclipse.sequoyah.tfm.sign.internal.smgmt.j9;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.sequoyah.tfm.sign.smgmt.j9.J9SmgmtCore;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
     */
    public void initializeDefaultPreferences() {
        IPreferenceStore store = J9SmgmtCore.getDefault().getPreferenceStore();
        store.setDefault(J9SmgmtConstants.SECURITY_TOOL_LOCATION,
                Messages.PreferenceInitializer_set_location_message);
    }

}
