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
package org.eclipse.tml.localization.stringeditor.editor.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.tml.localization.stringeditor.datatype.CellInfo;
import org.eclipse.tml.localization.stringeditor.datatype.ColumnInfo;
import org.eclipse.tml.localization.stringeditor.editor.StringEditorPart;

/**
 * The operation of translating a text from an editor cell.
 */
public class TranslateOperation extends EditorOperation {

	private final String sourceColumnID;

	private final ColumnInfo destinationColumn;

	public TranslateOperation(StringEditorPart editor, String sourceColumnID,
			ColumnInfo newColumnInfo) {
		super("Clone", editor);
		this.sourceColumnID = sourceColumnID;
		this.destinationColumn = newColumnInfo;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		ColumnInfo source = getModel().getColumn(sourceColumnID);
		for (String cellKey : source.getCells().keySet()) {
			CellInfo cell = source.getCells().get(cellKey);
			if (cell != null) {
				destinationColumn.addCell(cellKey, new CellInfo(
						cell.getValue(), cell.getComment()));
			}
		}
		return redo(monitor, info);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		getEditor().addColumn(destinationColumn, -1);

		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		getEditor().removeColumn(destinationColumn.getId());

		return Status.OK_STATUS;
	}

}
