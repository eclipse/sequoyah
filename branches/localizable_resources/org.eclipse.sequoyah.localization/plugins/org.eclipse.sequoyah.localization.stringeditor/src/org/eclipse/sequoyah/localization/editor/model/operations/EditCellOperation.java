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
 * Marcel Gorri (Eldorado) - Bug [326793] - Improvements on the string arrays handling
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
import org.eclipse.sequoyah.localization.editor.datatype.RowInfo;
import org.eclipse.sequoyah.localization.editor.datatype.RowInfoLeaf;
import org.eclipse.sequoyah.localization.editor.model.StringEditorPart;

/**
 * The operation of editing a specific editor cell.
 */
public class EditCellOperation extends EditorOperation {

	private final String key;

	private final String column;

	private final CellInfo oldValue;

	private final CellInfo newValue;

	private final RowInfo rowInfo;

	/**
	 * Creates a new EditCellOperation.
	 * 
	 * @param info
	 *            - the row related to the cell.
	 * @param column
	 *            - the column related to the cell.
	 * @param oldValue
	 *            - the cell old value.
	 * @param newValue
	 *            - the cell new value.
	 * @param editor
	 *            - the editor Object.
	 * @param rowInfo
	 *            - row info associated to the cell
	 * 
	 */
	public EditCellOperation(String key, String column, CellInfo oldValue,
			CellInfo newValue, StringEditorPart editor, RowInfo info) {
		super(Messages.EditCellOperation_0, editor);
		this.key = key;
		this.column = column;
		this.oldValue = oldValue;
		this.newValue = newValue;
		this.rowInfo = info;
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
				if (rowInfo instanceof RowInfoLeaf) {
					RowInfoLeaf leaf = (RowInfoLeaf) rowInfo;
					if (leaf.getParent() == null) {
						getEditorInput().setValue(column, key,
								newValue.getValue());
					} else {
						getModel().addCell(newValue, key, column,
								leaf.getPosition());
						getEditorInput().setValue(column, key,
								newValue.getValue(), leaf.getPosition());
					}
				} else {
					getEditorInput().setValue(column, key, newValue.getValue());
				}
			}
		} catch (SequoyahException e) {
			BasePlugin.logError("Error editing cell value: (" + column //$NON-NLS-1$
					+ ", " + key //$NON-NLS-1$
					+ ") =" + newValue != null ? newValue //$NON-NLS-1$
					.getValue() : null, e);
		}
		getEditor().fireDirtyPropertyChanged();
		getEditor().getEditorViewer().update(this.rowInfo, null);
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
				// TODO check if this method is working correctly
				if (rowInfo instanceof RowInfoLeaf) {
					RowInfoLeaf leaf = (RowInfoLeaf) rowInfo;
					getEditorInput().setValue(column, key, newValue.getValue(),
							leaf.getPosition());
				} else {
					getEditorInput().setValue(column, key, oldValue.getValue());
				}
			} else {
				getEditorInput().removeCell(key, column);
			}
		} catch (SequoyahException e) {
			BasePlugin.logError("Error undoing cell edition: (" + column //$NON-NLS-1$
					+ ", " + key //$NON-NLS-1$
					+ ") =" + oldValue != null ? oldValue //$NON-NLS-1$
					.getValue() : null, e);
		}
		getEditor().fireDirtyPropertyChanged();
		getEditor().getEditorViewer().update(this.rowInfo, null);
		return Status.OK_STATUS;
	}

}
