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
 * Paulo Faria (Eldorado) -  Bug [326793] -  Improvements on the String Arrays handling 
 ********************************************************************************/
package org.eclipse.sequoyah.localization.editor.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.sequoyah.localization.editor.StringEditorPlugin;
import org.eclipse.sequoyah.localization.editor.datatype.CellInfo;
import org.eclipse.sequoyah.localization.editor.datatype.ColumnInfo;
import org.eclipse.sequoyah.localization.editor.datatype.IModelChangedListener;
import org.eclipse.sequoyah.localization.editor.datatype.RowInfo;
import org.eclipse.sequoyah.localization.editor.datatype.RowInfoLeaf;
import org.eclipse.sequoyah.localization.editor.providers.ICellValidator;

public class StringEditorViewerModel {

	private static final int INDEX_NOT_REQUIRED = -1;

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
		rowsMap = new TreeMap<String, RowInfo>();
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
	 * Init the maps to keep things on track
	 */
	private void initMaps() {

		for (ColumnInfo column : columns) {
			columnsMap.put(column.getId(), column);
			for (String cellKey : column.getCells().keySet()) {
				CellInfo cell = column.getCells().get(cellKey);

				if (cell.hasChildren()) {
					// array
					if (rowsMap.get(cellKey) == null) {
						// first column
						RowInfo arrayRow = new RowInfo(cellKey);
						Map<Integer, CellInfo> subCells = cell.getChildren();
						for (Integer subCellIndex : subCells.keySet()) {
							CellInfo subCell = subCells.get(subCellIndex);
							Map<String, CellInfo> cells = new HashMap<String, CellInfo>();
							cells.put(column.getId(), subCell);
							// it does not need to keep object instance because
							// it is inserted on parent (RowInfo arrayRow)
							new RowInfoLeaf(cellKey, arrayRow, subCellIndex,
									cells);
						}
						rowsMap.put(cellKey, arrayRow);
					} else {
						// other columns
						RowInfo arrayRow = rowsMap.get(cellKey);

						Map<Integer, CellInfo> subCells = cell.getChildren();
						Map<Integer, RowInfoLeaf> subRows = arrayRow
								.getChildren();

						for (Integer subCellIndex : subCells.keySet()) {
							CellInfo subCell = subCells.get(subCellIndex);
							if (subRows.containsKey(subCellIndex)) {
								RowInfoLeaf leaf = subRows.get(subCellIndex);
								leaf.addCell(column.getId(), subCell);
							} else {
								// not found subcell at the given index, create
								// row and add sub cell
								Map<String, CellInfo> cells = new HashMap<String, CellInfo>();
								cells.put(column.getId(), subCell);
								new RowInfoLeaf(cellKey, arrayRow,
										subCellIndex, cells);
							}
						}
					}
				} else {
					// string
					if (rowsMap.get(cellKey) == null) {
						// first column
						Map<String, CellInfo> cells = new HashMap<String, CellInfo>();
						cells.put(column.getId(), cell);
						RowInfoLeaf row = new RowInfoLeaf(cellKey, null, null,
								cells);
						rowsMap.put(cellKey, row);
					} else {
						// other columns
						RowInfo rowInfo = rowsMap.get(cellKey);
						if (rowInfo instanceof RowInfoLeaf) {
							((RowInfoLeaf) rowInfo).addCell(column.getId(),
									cell);
						}
					}
				}
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
			CellInfo cell = cells.get(key);

			RowInfo row = rowsMap.get(key);

			if (cell.hasChildren()) {
				// array
				Map<Integer, CellInfo> subCells = cell.getChildren();

				if (row == null) {
					row = new RowInfo(key);
					rowsMap.put(key, row);
					for (Integer subCellIndex : subCells.keySet()) {
						CellInfo subCell = subCells.get(subCellIndex);
						Map<String, CellInfo> cellsForRow = new HashMap<String, CellInfo>();
						cells.put(info.getId(), subCell);
						new RowInfoLeaf(key, row, subCellIndex, cellsForRow);
					}
				} else {
					Map<Integer, RowInfoLeaf> subRows = row.getChildren();

					for (Integer subCellIndex : subCells.keySet()) {
						CellInfo subCell = subCells.get(subCellIndex);
						if (subRows.containsKey(subCellIndex)) {
							RowInfoLeaf leaf = subRows.get(subCellIndex);
							leaf.addCell(info.getId(), subCell);
						} else {
							Map<String, CellInfo> cellsForArray = new HashMap<String, CellInfo>();
							cells.put(info.getId(), subCell);
							new RowInfoLeaf(key, row, subCellIndex,
									cellsForArray);
						}
					}
				}
			} else {
				// string
				if (row == null) {
					Map<String, CellInfo> cellsForArray = new HashMap<String, CellInfo>();
					cells.put(info.getId(), cell);
					row = new RowInfoLeaf(key, null, null, cellsForArray);
					rowsMap.put(key, row);
				} else {
					if (row instanceof RowInfoLeaf) {
						((RowInfoLeaf) row).addCell(info.getId(), cell);
					}
				}
			}

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
		Map<String, CellInfo> cells = null;
		if (info instanceof RowInfoLeaf) {
			RowInfoLeaf leaf = (RowInfoLeaf) info;
			if (leaf.getParent() == null) {
				// string
				cells = leaf.getCells();

				for (String column : columnsMap.keySet()) {
					ColumnInfo columnInfo = columnsMap.get(column);
					if (columnInfo == null) {
						columnInfo = new ColumnInfo(column, column, null, true);
						columnsMap.put(column, columnInfo);
						columns.add(columnInfo);
					}
					CellInfo cell = cells.get(column);
					if (cell == null) {
						cell = new CellInfo(false);
					}
					columnInfo.addCell(info.getKey(), cell);
					rowsMap.put(info.getKey(), info);
				}
			} else {
				// array item
				RowInfo parentRowInfo = leaf.getParent();

				for (String column : columnsMap.keySet()) {
					ColumnInfo columnInfo = columnsMap.get(column);
					if (columnInfo == null) {
						columnInfo = new ColumnInfo(column, column, null, true);
						columnsMap.put(column, columnInfo);
						columns.add(columnInfo);
					}
					CellInfo parentCell = columnInfo.getCells().get(
							parentRowInfo.getKey());
					if (parentCell == null) {
						parentCell = new CellInfo(true);
						columnInfo.addCell(parentRowInfo.getKey(), parentCell);
					}

					CellInfo subcellInfo = leaf.getCells().get(column);
					if (subcellInfo == null) {
						subcellInfo = new CellInfo(null, null);
						leaf.addCell(column, subcellInfo);
					}
					parentCell.addChild(subcellInfo, leaf.getPosition());
				}
			}
		} else {
			// array
			Map<Integer, RowInfoLeaf> subrows = info.getChildren();
			// columnName to parentCell
			Map<String, CellInfo> parentsMap = new LinkedHashMap<String, CellInfo>();

			for (Integer subrowIndex : subrows.keySet()) {
				cells = subrows.get(subrowIndex).getCells();
				for (ColumnInfo cl : columns) {
					String column = cl.getId();
					CellInfo parentCell = parentsMap.get(column);
					if (parentCell == null) {
						// not found => create
						parentCell = new CellInfo(true);
						parentsMap.put(column, parentCell);
					}
					CellInfo cell = cells.get(column);
					if (cell == null) {
						cell = new CellInfo(false);
					}

					parentCell.addChild(cell);
				}
			}

			for (String column : parentsMap.keySet()) {
				ColumnInfo columnInfo = columnsMap.get(column);
				if (columnInfo == null) {
					columnInfo = new ColumnInfo(column, column, null, true);
					columnsMap.put(column, columnInfo);
					columns.add(columnInfo);
				}
				columnInfo.addCell(info.getKey(), parentsMap.get(column));
			}
			rowsMap.put(info.getKey(), info);
		}
		validateRow(info);
		notifyListeners();
	}

	public void addCell(CellInfo info, String key, String column) {
		addCell(info, key, column, INDEX_NOT_REQUIRED);
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
	public void addCell(CellInfo info, String key, String column, Integer index) {
		RowInfo rowInfo = rowsMap.get(key);
		if (rowInfo instanceof RowInfoLeaf) {
			// string or item array
			RowInfoLeaf leaf = (RowInfoLeaf) rowInfo;
			if (leaf.getParent() == null) {
				// string
				columnsMap.get(column).addCell(key, info);
			}
			leaf.addCell(column, info);
		} else {
			// array-item
			if (index >= 0) {
				Map<Integer, RowInfoLeaf> rowChildren = rowInfo.getChildren();

				Map<String, CellInfo> cells = rowChildren.get(index).getCells();
				cells.put(column, info);

				ColumnInfo columnInfo = columnsMap.get(column);
				CellInfo parentCell = columnInfo.getCells().get(key);
				if (parentCell == null) {
					// array does not exist
					parentCell = new CellInfo(true);
					columnInfo.addCell(key, parentCell);
				}
				parentCell.addChild(info, index);
			}
		}
		validateRow(key);
		notifyListeners();
	}

	/**
	 * Remove column from the UI model
	 * 
	 * @param column
	 *            the columnID to remove
	 */
	public void removeColumn(String column) {
		// remove from columns map
		columnsMap.remove(column);
		// remove from columns list
		List<ColumnInfo> orig = new ArrayList<ColumnInfo>(columns);
		for (ColumnInfo info : orig) {
			if (info.getId().equals(column)) {
				columns.remove(info);
			}
		}
		// remove from rows map
		Map<String, RowInfo> rowsMapClone = new HashMap<String, RowInfo>(
				rowsMap);
		Iterator<RowInfo> it = rowsMapClone.values().iterator();
		while (it.hasNext()) {
			RowInfo row = it.next();
			if (row instanceof RowInfoLeaf) {
				RowInfoLeaf leaf = (RowInfoLeaf) row;
				leaf.removeCell(column);
				if (isEmptyRow(row)) {
					// TODO check this
					removeRow(row.getKey());
				}
			}
		}

		notifyListeners();
	}

	private boolean isEmptyRow(RowInfo row) {
		if (row instanceof RowInfoLeaf) {
			return ((RowInfoLeaf) row).getCells().size() == 0;
		} else {
			return false;
		}
	}

	public void removeRow(String key, Integer index) {
		RowInfo row = rowsMap.remove(key);

		if (row != null) {
			Map<Integer, RowInfoLeaf> children = row.getChildren();
			RowInfo newRow = new RowInfo(key);
			rowsMap.put(key, newRow);
			int newCount = 0;
			for (Integer childIndex : children.keySet()) {
				if (index != childIndex) {
					RowInfoLeaf child = children.get(childIndex);
					RowInfoLeaf newChild = new RowInfoLeaf(key, newRow,
							newCount++, child.getCells());
					newChild.addStatus(child.getStatus());
				}
			}
			if (newCount == 0) {
				rowsMap.remove(key);
			}
		}

		Iterator<ColumnInfo> it = columnsMap.values().iterator();
		while (it.hasNext()) {
			ColumnInfo col = it.next();
			CellInfo parent = col.getCells().get(key);
			if (parent != null) {
				Map<Integer, CellInfo> children = parent.getChildren();
				parent.clearChildren();
				if (children != null) {
					int newCount = 0;
					for (Integer childIndex : children.keySet()) {
						if (index != childIndex) {
							parent.addChild(children.get(childIndex),
									newCount++);
						}
					}
					if (newCount == 0) {
						col.removeCell(key);
					}
				} else {
					// this should not happen, but if it does, be safe
					col.removeCell(key);
				}
			}
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
			Collection<RowInfoLeaf> leaves;
			if (info instanceof RowInfoLeaf) {
				leaves = new ArrayList<RowInfoLeaf>(1);
				leaves.add((RowInfoLeaf) info);
			} else {
				leaves = info.getChildren().values();
			}
			for (RowInfoLeaf leaf : leaves) {
				for (CellInfo cell : leaf.getCells().values()) {
					if (cell != null && cell.isDirty()) {
						cell.setDirty(false);
						changed.add(info);
					}
				}
			}
		}
		return changed;
	}

	public List<ColumnInfo> getColumnsChanged() {
		Set<ColumnInfo> changed = new HashSet<ColumnInfo>();
		for (RowInfo info : rowsMap.values()) {
			Collection<RowInfoLeaf> leaves;
			if (info instanceof RowInfoLeaf) {
				leaves = new ArrayList<RowInfoLeaf>(1);
				leaves.add((RowInfoLeaf) info);
			} else {
				leaves = info.getChildren().values();
			}
			for (RowInfoLeaf leaf : leaves) {
				for (Map.Entry<String, CellInfo> cellEntry : leaf.getCells()
						.entrySet()) {
					CellInfo cell = cellEntry.getValue();
					if (cell != null && cell.isDirty()) {
						changed.add(columnsMap.get(cellEntry.getKey()));
					}
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
		if (row instanceof RowInfoLeaf) {
			// string
			RowInfoLeaf leaf = (RowInfoLeaf) row;
			for (ColumnInfo column : columns) {
				CellInfo cell = leaf.getCells().get(column.getId());
				IStatus cellStatus = validator.isCellValid(column.getId(),
						row.getKey(), cell != null ? cell.getValue() : null);
				if (!cellStatus.isOK()) {
					row.addStatus(cellStatus);
				}
			}
		} else {
			// array: validate each child
			Map<Integer, RowInfoLeaf> children = row.getChildren();
			for (RowInfoLeaf child : children.values()) {
				child.cleanStatus();
				for (ColumnInfo column : columns) {
					CellInfo cell = child.getCells().get(column.getId());
					IStatus cellStatus = validator
							.isCellValid(column.getId(), row.getKey(),
									cell != null ? cell.getValue() : null);
					if (!cellStatus.isOK()) {
						child.addStatus(cellStatus);
					}
				}
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
