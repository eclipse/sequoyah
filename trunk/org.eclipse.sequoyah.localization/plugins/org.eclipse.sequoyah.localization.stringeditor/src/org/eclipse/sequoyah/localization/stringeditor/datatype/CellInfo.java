/********************************************************************************
 * Copyright (c) 2009 Motorola Inc.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Marcelo Marzola Bossoni (Eldorado)
 * 
 * Contributors:
 * name (company) - description.
 ********************************************************************************/
package org.eclipse.tml.localization.stringeditor.datatype;

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
		this.value = value;
		this.comment = comment;
		this.dirty = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getValue() != null ? getValue() : "";
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

}
