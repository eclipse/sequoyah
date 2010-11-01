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
package org.eclipse.sequoyah.localization.editor.datatype;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class represents a Cell of the editor. It can include comments
 */
public class CellInfo {

	/*
	 * The cell value
	 */
	private String value;

	/*
	 * The cell comment (if any)
	 */
	private String comment;

	/*
	 * The dirt state of the cell
	 */
	private boolean dirty;

	private final TreeMap<Integer, CellInfo> children;

	/**
	 * Get the value of this cell
	 * 
	 * @return the value of this cell
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Set this cell value
	 * 
	 * @param value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Get this cell comment
	 * 
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * Set this cell comment
	 * 
	 * @param comment
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * Create a new CellInfo object with a given name and comment
	 * 
	 * @param value
	 * @param comment
	 */
	public CellInfo(String value, String comment) {
		this(false);
		this.value = value;
		this.comment = comment;
		this.dirty = false;
	}

	public CellInfo(CellInfo baseCellInfo) {
		this(baseCellInfo.hasChildren());
		this.value = baseCellInfo.getValue();
		this.comment = baseCellInfo.getComment();
		this.dirty = false;

		if (baseCellInfo.hasChildren()) {
			Map<Integer, CellInfo> baseChildren = baseCellInfo.getChildren();
			for (Integer index : baseChildren.keySet()) {
				CellInfo baseChild = baseChildren.get(index);
				CellInfo child = new CellInfo(baseChild.getValue(),
						baseChild.getComment());
				this.addChild(child, index);
			}
		}
	}

	public CellInfo(boolean hasChildren) {
		if (hasChildren) {
			children = new TreeMap<Integer, CellInfo>();
		} else {
			children = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getValue() != null ? getValue() : ""; //$NON-NLS-1$
	}

	/*
	 * Set the dirty state of this cell
	 */
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public boolean isDirty() {
		return dirty;
	}

	public boolean hasChildren() {
		return children != null;
	}

	public Map<Integer, CellInfo> getChildren() {
		return new LinkedHashMap<Integer, CellInfo>(children);
	}

	public void clearChildren() {
		if (children != null) {
			children.clear();
		}
	}

	public void addChild(CellInfo child) {
		if (children != null) {
			children.put(getNextPositionAvailable(), child);
		}
	}

	public void addChild(CellInfo child, Integer index) {
		if (children != null) {
			children.put(index, child);
		}
	}

	private Integer getNextPositionAvailable() {
		Integer nextPositionAvailable;
		Integer lastKey = (children.size() > 0 ? children.lastKey() : null);

		if (lastKey != null) {
			nextPositionAvailable = lastKey + 1;
		} else {
			nextPositionAvailable = 0;
		}

		return nextPositionAvailable;
	}

}
