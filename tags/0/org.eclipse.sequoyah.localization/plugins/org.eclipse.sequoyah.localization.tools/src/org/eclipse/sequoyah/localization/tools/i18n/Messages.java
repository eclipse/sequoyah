/********************************************************************************
 * Copyright (c) 2009 Motorola Inc.
 * All rights reserved. This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Matheus Tait Lima (Eldorado)
 * 
 * Contributors:
 * name (company) - description.
 ********************************************************************************/
package org.eclipse.tml.localization.tools.i18n;

import org.eclipse.osgi.util.NLS;

/**
 * ID's for all strings that will be shown to the user. Used for localization
 * purposes
 * 
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.tml.localization.tools.i18n.messages"; //$NON-NLS-1$

	public static String StringEditorInput_ErrorInitializingEditor;

	public static String StringEditorInput_ErrorManagerNotInitialized;

	public static String LocaleAttribute_Exception_AllowedTypesNeeded;

	public static String LocaleAttribute_Exception_NameCannotBeNull;

	public static String LocaleAttribute_Exception_NotAllowedMethod;

	public static String LocaleAttribute_Exception_NullAllowedValues;

	public static String LocaleAttribute_Exception_TypeDontExist;

	public static String LocaleAttribute_Exception_ValueNotAllowed;

	public static String LocaleAttribute_Exception_ValueNotAllowed2;

	public static String LocaleAttribute_Exception_ValueNotAllowed3;

	public static String LocaleAttribute_Exception_ValueNotNull;

	public static String LocaleAttribute_Exception_ValueNotNull2;

	public static String Warning_NoDefaultFile;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
