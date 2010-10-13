/********************************************************************************
 * Copyright (c) 2009-2010 Motorola Mobility, Inc.
 * All rights reserved. This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Daniel Barboza Franco (Eldorado) - Bug [326793] - Improvements on the String Arrays handling 
 * 
 ********************************************************************************/

package org.eclipse.sequoyah.localization.editor.datatype;

import java.util.Map;

public class RowInfoArray extends RowInfo {

	public RowInfoArray(String key, Map<String, CellInfo> cells) {
		super(key, cells);
		setArray(true);
	}

}
