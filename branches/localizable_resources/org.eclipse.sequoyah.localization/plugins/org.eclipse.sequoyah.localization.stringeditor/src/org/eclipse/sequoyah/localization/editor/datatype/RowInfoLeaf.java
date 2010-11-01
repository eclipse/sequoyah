package org.eclipse.sequoyah.localization.editor.datatype;

import java.util.HashMap;
import java.util.Map;

public class RowInfoLeaf extends RowInfo {

	/*
	 * This row cells (key is columnId)
	 */
	private final Map<String, CellInfo> cells;

	private final RowInfo parent;

	private Integer position;

	public RowInfoLeaf(String key, RowInfo parent, Integer index,
			Map<String, CellInfo> cells) {
		super(key);
		this.parent = parent;
		this.cells = cells != null ? cells : new HashMap<String, CellInfo>();
		if (parent != null) {
			this.position = index;
			parent.addChild(this, index);
		}
	}

	/**
	 * Add a cell to this row
	 * 
	 * @param columnID
	 * @param value
	 */
	public void addCell(String columnID, CellInfo value) {
		cells.put(columnID, value);
	}

	/**
	 * Remove a cell of this row.
	 * 
	 * @param columnID
	 */
	public void removeCell(String columnID) {
		cells.remove(columnID);
	}

	/**
	 * get this row cells
	 * 
	 * @return the cells
	 */
	public Map<String, CellInfo> getCells() {
		return cells;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public RowInfo getParent() {
		return parent;
	}

	@Override
	public String toString() {
		return "RowInfoLeaf [cells=" + cells + ", parent=" + parent
				+ ", position=" + position + "]";
	}

}
