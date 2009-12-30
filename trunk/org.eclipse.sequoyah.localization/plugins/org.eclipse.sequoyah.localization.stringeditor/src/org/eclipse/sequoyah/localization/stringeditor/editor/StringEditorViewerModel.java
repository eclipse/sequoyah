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
 * Marcelo Marzola Bossoni (Eldorado) - Bug [289146] - Performance and Usability Issues
 *  * Vinicius Rigoni Hernandes (Eldorado) - Bug [289885] - Localization Editor doesn't recognize external file changes
 ********************************************************************************/
package org.eclipse.tml.localization.stringeditor.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.tml.localization.stringeditor.StringEditorPlugin;
import org.eclipse.tml.localization.stringeditor.datatype.CellInfo;
import org.eclipse.tml.localization.stringeditor.datatype.ColumnInfo;
import org.eclipse.tml.localization.stringeditor.datatype.IModelChangedListener;
import org.eclipse.tml.localization.stringeditor.datatype.RowInfo;
import org.eclipse.tml.localization.stringeditor.providers.ICellValidator;

public class StringEditorViewerModel {

	/**
	 * List with all columns
	 */
	private final List<ColumnInfo> columns;

	/**
	 * A Map to quick access the columns by ID
	 */
	private final Map<String, ColumnInfo> columnsMap;

	/**
	 * A Map to quick access rows by key
	 */
	private final Map<String, RowInfo> rowsMap;

	/*
	 * A cell validator
	 */
	private final ICellValidator validator;

	/*
	 * Listeners
	 */
	private final List<IModelChangedListener> listeners;

	public StringEditorViewerModel(List<ColumnInfo> infos,
			ICellValidator cellValidator) {
		columns = infos;
		columnsMap = new HashMap<String, ColumnInfo>();
		rowsMap = new HashMap<String, RowInfo>();
		listeners = new ArrayList<IModelChangedListener>();
		validator = cellValidator;
		initMaps();
		doInitialValidation();
	}

	private void doInitialValidation() {
		for (RowInfo row : rowsMap.values()) {
			validateRow(row.getKey());
		}

	}

	/**
	 * Init the maps to keep thing on track
	 */
	private void initMaps() {

		for (ColumnInfo column : columns) {
			columnsMap.put(column.getId(), column);
			for (String cellKey : column.getCells().keySet()) {
				if (rowsMap.get(cellKey) == null) {
					rowsMap.put(cellKey, new RowInfo(cellKey, null));
				}
				RowInfo rowInfo = rowsMap.get(cellKey);
				rowInfo.addCell(column.getId(), column.getCells().get(cellKey));
			}

		}
		notifyListeners();

	}

	/**
	 * Get the row with the following key
	 * 
	 * @param key
	 * @return the row
	 */
	public RowInfo getRow(String key) {
		return rowsMap.get(key);
	}

	/**
	 * Get the column with the following ID
	 * 
	 * @param ID
	 * @return the column
	 */
	public ColumnInfo getColumn(String ID) {
		return columnsMap.get(ID);
	}

	/**
	 * Get all rows
	 * 
	 * @return map with all rows and their keys
	 */
	public Map<String, RowInfo> getRows() {
		return rowsMap;
	}

	/**
	 * Get all column
	 * 
	 * @return list with all columns
	 */
	public List<ColumnInfo> getColumns() {
		return columns;

	}

	/**
	 * Add a new column
	 * 
	 * @param info
	 */
	public void addColumn(ColumnInfo info) {

		columns.add(info);
		columnsMap.put(info.getId(), info);
		Map<String, CellInfo> cells = info.getCells();
		for (String key : cells.keySet()) {
			RowInfo row = rowsMap.get(key);
			if (row == null) {
				row = new RowInfo(key, null);
				rowsMap.put(key, row);
			}
			row.addCell(info.getId(), info.getCells().get(key));
			validateRow(row);
		}
		notifyListeners();
	}

	/**
	 * Add a new Row.
	 * 
	 * @param info
	 */
	public void addRow(RowInfo info) {
		Map<String, CellInfo> cells = info.getCells();
		rowsMap.put(info.getKey(), info);
		for (String column : cells.keySet()) {
			ColumnInfo columnInfo = columnsMap.get(column);
			if (columnInfo == null) {
				columnInfo = new ColumnInfo(column, column, null, true);
				columnsMap.put(column, columnInfo);
				columns.add(columnInfo);
			}
			columnInfo.addCell(info.getKey(), cells.get(column));
		}
		validateRow(info);
		notifyListeners();
	}

	/**
	 * Add a new cell to a certain column and with the given key
	 * 
	 * @param info
	 *            the cell
	 * @param key
	 *            the key
	 * @param column
	 *            the column
	 */
	public void addCell(CellInfo info, String key, String column) {
		columnsMap.get(column).addCell(key, info);
		rowsMap.get(key).addCell(column, info);
		validateRow(key);
		notifyListeners();
	}

	/**
	 * Remove the cell with some key within some column
	 * 
	 * @param key
	 * @param column
	 */
	public void removeCell(String key, String column) {
		rowsMap.get(key).removeCell(column);
		columnsMap.get(column).removeCell(key);
		notifyListeners();
	}

	public void removeColumn(String column) {
		columnsMap.remove(column);
		List<ColumnInfo> orig = new ArrayList<ColumnInfo>(columns);
		for (ColumnInfo info : orig) {
			if (info.getId().equals(column)) {
				columns.remove(info);
			}
		}

		Iterator<RowInfo> it = rowsMap.values().iterator();
		while (it.hasNext()) {
			RowInfo row = it.next();
			row.removeCell(column);
		}

		notifyListeners();
	}

	public void removeRow(String key) {
		rowsMap.remove(key);

		Iterator<ColumnInfo> it = columnsMap.values().iterator();
		while (it.hasNext()) {
			ColumnInfo col = it.next();
			col.removeCell(key);
		}

		notifyListeners();
	}

	public void addListener(IModelChangedListener listener) {
		listeners.add(listener);
	}

	public void removeListener(IModelChangedListener listener) {
		listeners.remove(listener);
	}

	private void notifyListeners() {
		for (IModelChangedListener listener : listeners) {
			listener.modelChanged(this);
		}
	}

	/**
	 * Clean dirty state of cells
	 * 
	 * @return changed lines
	 */
	public List<RowInfo> save() {
		List<RowInfo> changed = new ArrayList<RowInfo>();
		for (RowInfo info : rowsMap.values()) {
			for (CellInfo cell : info.getCells().values()) {
				if (cell != null && cell.isDirty()) {
					cell.setDirty(false);
					changed.add(info);
				}
			}
		}
		return changed;
	}

	public List<ColumnInfo> getColumnsChanged() {
		Set<ColumnInfo> changed = new HashSet<ColumnInfo>();
		for (RowInfo info : rowsMap.values()) {
			for (Map.Entry<String, CellInfo> cellEntry : info.getCells()
					.entrySet()) {
				CellInfo cell = cellEntry.getValue();
				if (cell != null && cell.isDirty()) {
					changed.add(columnsMap.get(cellEntry.getKey()));
				}
			}
		}
		return (new ArrayList<ColumnInfo>(changed));
	}

	/**
	 * Validate all cells of this row, even if it has null value
	 * 
	 * @param key
	 *            : the row key
	 */
	public void validateRow(String key) {
		RowInfo row = rowsMap.get(key);
		validateRow(row);
	}

	public void validateRow(RowInfo row) {
		row.cleanStatus();
		for (ColumnInfo column : columns) {
			CellInfo cell = row.getCells().get(column.getId());
			IStatus cellStatus = validator.isCellValid(column.getId(), row
					.getKey(), cell != null ? cell.getValue() : null);
			if (!cellStatus.isOK()) {
				row.addStatus(cellStatus);
			}
		}
	}

	public IStatus getStatus() {
		MultiStatus status = null;
		for (RowInfo row : rowsMap.values()) {
			if (!row.getStatus().isOK()) {
				if (status == null) {
					status = new MultiStatus(StringEditorPlugin.PLUGIN_ID, 0,
							null, null);
				}
				status.merge(row.getStatus());
			}
		}
		return status != null ? status : Status.OK_STATUS;
	}
}
