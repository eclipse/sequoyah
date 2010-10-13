/********************************************************************************
 * Copyright (c) 2009-2010 Motorola Mobility, Inc.
 * All rights reserved. This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Marcelo Marzola Bossoni (Eldorado)
 * Matheus Tait Lima (Eldorado)
 * 
 * Contributors:
 * Daniel Drigo Pastore (Eldorado) - Bug [326793] - Fixed array support for the String Localization Editor
 * 
 ********************************************************************************/
package org.eclipse.sequoyah.localization.editor.model.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.sequoyah.device.common.utilities.BasePlugin;
import org.eclipse.sequoyah.device.common.utilities.exception.SequoyahException;
import org.eclipse.sequoyah.localization.editor.datatype.CellInfo;
import org.eclipse.sequoyah.localization.editor.model.StringEditorPart;
import org.eclipse.sequoyah.localization.editor.model.operations.Messages;

/**
 * The operation of editing a specific editor cell.
 */
public class EditCellOperation extends EditorOperation {

	private final String key;

	private final String column;

	private final CellInfo oldValue;

	private final CellInfo newValue;

	/**
	 * Creates a new EditCellOperation.
	 * 
	 * @param key
	 *            - the key related to the cell.
	 * @param column
	 *            - the column related to the cell.
	 * @param oldValue
	 *            - the cell old value.
	 * @param newValue
	 *            - the cell new value.
	 * @param editor
	 *            - the editor Object.
	 */
	public EditCellOperation(String key, String column, CellInfo oldValue,
			CellInfo newValue, StringEditorPart editor) {
		super(Messages.EditCellOperation_0, editor);
		this.key = key;
		this.column = column;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.operations.AbstractOperation#execute(org.eclipse
	 * .core.runtime.IProgressMonitor, org.eclipse.core.runtime.IAdaptable)
	 */
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

		getModel().addCell(newValue, key, column);
		if (newValue != null && !newValue.isDirty()) {
			newValue.setDirty(true);
		}
		try {
			if (newValue == null
					|| (newValue != null && newValue.getValue() == null)) {
				getEditorInput().removeCell(key, column);
			} else {
				getEditorInput().setValue(column, key, newValue.getValue());
			}
		} catch (SequoyahException e) {
			BasePlugin
					.logError(
							"Error editing cell value: (" + column //$NON-NLS-1$
									+ ", " + key //$NON-NLS-1$
									+ ") =" + newValue != null ? newValue //$NON-NLS-1$
									.getValue()
									: null, e);
		}
		getEditor().fireDirtyPropertyChanged();
		getEditor().getEditorViewer().update(getModel().getRow(key), null);
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
		getModel().addCell(oldValue, key, column);
		try {
			if (oldValue != null) {
				oldValue.setDirty(true);
			}
			if (oldValue != null && oldValue.getValue() != null) {
				getEditorInput().setValue(column, key, oldValue.getValue());
			} else {
				getEditorInput().removeCell(key, column);
			}
		} catch (SequoyahException e) {
			BasePlugin
					.logError(
							"Error undoing cell edition: (" + column //$NON-NLS-1$
									+ ", " + key //$NON-NLS-1$
									+ ") =" + oldValue != null ? oldValue //$NON-NLS-1$
									.getValue()
									: null, e);
		}
		getEditor().fireDirtyPropertyChanged();
		getEditor().getEditorViewer().update(getModel().getRow(key), null);
		return Status.OK_STATUS;
	}

}
