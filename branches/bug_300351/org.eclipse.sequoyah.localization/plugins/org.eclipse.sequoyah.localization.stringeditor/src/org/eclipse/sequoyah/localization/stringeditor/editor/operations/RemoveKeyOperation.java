/********************************************************************************
 * Copyright (c) 2009-2010 Motorola Inc.
 * All rights reserved. This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Marcelo Marzola Bossoni (Eldorado)
 * Matheus Tait Lima (Eldorado)
 * 
 * Contributors:
 * Daniel Pastore (Eldorado) - [289870] Moving and renaming Tml to Sequoyah
 ********************************************************************************/
package org.eclipse.sequoyah.localization.stringeditor.editor.operations;

import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.sequoyah.localization.stringeditor.datatype.RowInfo;
import org.eclipse.sequoyah.localization.stringeditor.editor.StringEditorPart;

/**
 * The operation of removing a key from the editor.
 */
public class RemoveKeyOperation extends EditorOperation {

	private final List<RowInfo> rows;

	/**
	 * Creates a RemoveKeyOperation.
	 * 
	 * @param label
	 *            - the label for this operation.
	 * @param editor
	 *            - the editor object.
	 * @param row
	 *            - The RowInfo object that identifies the key to be removed.
	 */
	public RemoveKeyOperation(String label, StringEditorPart editor,
			List<RowInfo> rows) {
		super(label, editor);

		this.rows = rows;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {

		return redo(monitor, info);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.operations.AbstractOperation#redo(org.eclipse
	 * .core.runtime.IProgressMonitor, org.eclipse.core.runtime.IAdaptable)
	 */
	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {

		for (RowInfo row : rows) {
			getEditor().removeRow(row.getKey());
		}
		getEditor().getEditorViewer().refresh();
		return Status.OK_STATUS;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.operations.AbstractOperation#undo(org.eclipse
	 * .core.runtime.IProgressMonitor, org.eclipse.core.runtime.IAdaptable)
	 */
	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {

		for (RowInfo row : rows) {
			getEditor().addRow(row);
		}
		getEditor().getEditorViewer().refresh();
		return Status.OK_STATUS;
	}

}
