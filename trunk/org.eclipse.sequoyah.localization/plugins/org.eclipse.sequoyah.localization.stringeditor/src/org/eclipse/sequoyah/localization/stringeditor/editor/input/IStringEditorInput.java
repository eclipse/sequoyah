/********************************************************************************
 * Copyright (c) 2009 Motorola Inc.
 * All rights reserved. This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Marcelo Marzola Bossoni (Eldorado)
 * Matheus Tait Lima (Eldorado)
 * 
 * Contributors:
 * Vinicius Rigoni Hernandes (Eldorado) - Bug [289885] - Localization Editor doesn't recognize external file changes
 * Marcelo Marzola Bossoni (Eldorado) - Bug [294445] - Localization Editor remains opened when project is deleted
 ********************************************************************************/
package org.eclipse.tml.localization.stringeditor.editor.input;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.tml.common.utilities.exception.TmLException;
import org.eclipse.tml.localization.stringeditor.datatype.CellInfo;
import org.eclipse.tml.localization.stringeditor.datatype.ColumnInfo;
import org.eclipse.tml.localization.stringeditor.datatype.RowInfo;
import org.eclipse.ui.IEditorInput;

public abstract class IStringEditorInput implements IEditorInput {

	private List<IInputChangeListener> inputChangeListeners = new ArrayList<IInputChangeListener>();

	public abstract void init(IProject project) throws Exception;

	/**
	 * Return the Editor Title
	 * 
	 * @return the Editor Title
	 */
	public abstract String getTitle();

	/**
	 * Get the columns IDs to be shown inside this editor
	 * 
	 * @return a list with all available columns.
	 */
	public abstract List<ColumnInfo> getColumns();

	/**
	 * Translate from a source column ID to a destination ID
	 * 
	 * @param srcColumnID
	 * @param destColumnID
	 * @return true if the translation was succefull
	 */
	public abstract boolean translate(String srcColumnID, String destColumnID);

	/**
	 * Add a new column ID to the editor input
	 */
	public abstract void addColumn(String ID);

	/**
	 * Remove the follow column ID of the input
	 * 
	 * @param id
	 *            the column id
	 */
	public abstract void removeColumn(String id);

	/**
	 * Set a translation value to the following key in the following language
	 * 
	 * @param columnID
	 *            the column id
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 * @throws TmLException
	 */
	public abstract void setValue(String columnID, String key, String value)
			throws TmLException;

	/**
	 * Get the value of a certain key at following column
	 * 
	 * @param columnID
	 *            the column ID
	 * @param key
	 *            the key
	 * @return the value of the key in the column
	 */
	public abstract CellInfo getValue(String columnID, String key);

	/**
	 * Get the values of a column
	 * 
	 * @param columnID
	 *            the column ID
	 * @return a map of key values for the column
	 */
	public abstract Map<String, CellInfo> getValues(String columnID);

	/**
	 * 
	 * @param columnID
	 *            the column ID
	 * @return a list with all available keys inside of that
	 */
	public abstract List<CellInfo> getAvailableKeysForColumn(String columnID);

	/**
	 * Check if this input can be saved
	 * 
	 * @return true if can be saved, false otherwise
	 */
	public abstract boolean canSave();

	/**
	 * Save this editor input
	 * 
	 * @return true if successfully saved, false otherwise
	 */
	public abstract boolean save();

	/**
	 * Check if this input has unsaved changes
	 * 
	 * @return true if input has unsaved changes, false otherwise
	 */
	public abstract boolean isDirty();

	/**
	 * revert all input to a saved state
	 * 
	 * @return true if operation succeded, false otherwise
	 */
	public abstract boolean revert();

	/**
	 * Revert the following columnID to a saved state
	 * 
	 * @param columnID
	 * @return true if ok, false otherwise
	 * @throws IOException
	 */
	public abstract boolean revert(String columnID) throws IOException;

	/**
	 * 
	 * @return true if columns can be reverted individually
	 */
	public abstract boolean canRevertByColumn();

	/**
	 * Remove the row with the following key from all columns
	 * 
	 * @param key
	 */
	public abstract void removeRow(String key);

	/**
	 * Remove the cell with the following key from the following column
	 * 
	 * @param key
	 */
	public abstract void removeCell(String key, String columnID);

	/**
	 * Add a new row based on the following {@link RowInfo}
	 * 
	 * @param row
	 */
	public abstract void addRow(RowInfo row);

	/**
	 * dispose this editor input. This method is called when editor is being
	 * disposed
	 */
	public abstract void dispose();

	/**
	 * Set the cell tooltip
	 * 
	 * @param columnID
	 * @param key
	 * @param tooltip
	 * @throws TmLException
	 */
	public abstract void setCellTooltip(String columnID, String key,
			String tooltip) throws TmLException;

	/**
	 * Do a Lightwight validation of the entire input This validation shall not
	 * took much time to avoid lack of editor performance Preferentially,
	 * validate top level errors, not doing a "bitwise verification"
	 * 
	 * @return the IStatus object of the validation results
	 */
	public abstract IStatus validate();

	/**
	 * Check if this Input can handle the file passed as argument
	 * 
	 * @param file
	 *            the file to be handled
	 * @return true if can handle the file, false otherwise
	 */
	public abstract boolean canHandle(IFile file);

	/**
	 * Add a listener that will be notified when there is a change in the editor
	 * input
	 * 
	 * @param inputChangeListener
	 *            IInputChangeListener object
	 */
	public void addInputChangeListener(IInputChangeListener inputChangeListener) {
		if (!inputChangeListeners.contains(inputChangeListener)) {
			inputChangeListeners.add(inputChangeListener);
		}
	}

	/**
	 * Remove a listener from the list of listeners which are notified when
	 * there is a change in the editor input
	 * 
	 * @param inputChangeListener
	 *            IInputChangeListener object
	 */
	public void removeInputChangeListener(
			IInputChangeListener inputChangeListener) {
		inputChangeListeners.remove(inputChangeListener);
	}

	/**
	 * Notify a change in the editor input
	 * 
	 * @param columnID
	 *            the column that changed
	 */
	public void notifyInputChange(String columnID) {
		for (IInputChangeListener inputChangeListeener : inputChangeListeners) {
			inputChangeListeener.columnChanged(columnID);
		}
	}

	public void notifyProjectRemoved() {
		for (IInputChangeListener inputChangeListeener : inputChangeListeners) {
			inputChangeListeener.projectRemoved();
		}
	}

}
