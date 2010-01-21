/********************************************************************************
 * Copyright (c) 2009 Motorola Inc.
 * All rights reserved. This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Marcelo Marzola Bossoni (Eldorado)
 * 
 * Contributors:
 * name (company) - description.
 ********************************************************************************/
package org.eclipse.sequoyah.localization.stringeditor.providers;

import org.eclipse.core.resources.IProject;
import org.eclipse.sequoyah.localization.stringeditor.datatype.ColumnInfo;
import org.eclipse.sequoyah.localization.stringeditor.datatype.RowInfo;

/**
 * 
 * Implementers should have in mind that callers will do:
 * 
 * 
 */

public interface IOperationProvider {

	public void init(IProject project) throws Exception;

	/**
	 * When creating a new column, providers will be asked to provide the ID If
	 * no id is provided, a simple dialog will be opened asking user
	 * 
	 * @return the new column
	 */
	public ColumnInfo getNewColumn();

	/**
	 * When creating a new row, providers will be asked to provide the key of
	 * this row.
	 * 
	 * @return the new row
	 */
	public RowInfo getNewRow();

}
