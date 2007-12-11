/********************************************************************************
 * Copyright (c) 2007 Motorola Inc.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Fabio Fantato (Motorola)
 * 
 * Contributors:
 * name (company) - description.
 ********************************************************************************/
package org.eclipse.tml.framework.device.parser;

public interface XMLConstants {
	public static final String TAG_CONFIG = "config";
	public static final String TAG_PROPERTY = "property";
	public static final String TAG_ID          = "id";
	public static final String TAG_NAME        = "name";
	public static final String TAG_DESCRIPTION = "description";
	public static final String TAG_SCOPE = "scope";
	public static final String TAG_RULE = "rule";
	
	
	public static final String SCOPE_DEVICE   = "DEVICE";
	public static final String SCOPE_SERVICE  = "SERVICE";
	public static final String SCOPE_INSTANCE = "INSTANCE";
	
	public static final String RULE_INVISIBLE = "INVISIBLE";
	public static final String RULE_READ_ONLY = "READ-ONLY";
	public static final String RULE_WRITABLE  = "WRITABLE";
	
	
}
