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
 * Marcelo Marzola Bossoni (Eldorado) -  Bug [289146] - Performance and Usability Issues
 ********************************************************************************/
package org.eclipse.sequoyah.localization.editor.datatype;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.sequoyah.localization.editor.StringEditorPlugin;

/**
 * This class represents a row of the editor
 */
public class RowInfo {

	/*
	 * This row key
	 */
	private String key;

	private final TreeMap<Integer, RowInfoLeaf> children = new TreeMap<Integer, RowInfoLeaf>();

	/*
	 * This row status
	 */
	private MultiStatus rowStatus;

	/**
	 * Create a new row with a key and initial cells
	 * 
	 * @param key
	 * @param cells
	 */
	public RowInfo(String key) {
		this.key = key;
		this.rowStatus = new MultiStatus(StringEditorPlugin.PLUGIN_ID, 0, null,
				null);
	}

	/**
	 * get this row key
	 * 
	 * @return
	 */
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void addStatus(IStatus status) {
		this.rowStatus.merge(status);
	}

	public void cleanStatus() {
		this.rowStatus = new MultiStatus(StringEditorPlugin.PLUGIN_ID, 0, null,
				null);
	}

	public MultiStatus getStatus() {
		return rowStatus;
	}

	protected void addChild(RowInfoLeaf child, Integer index) {
		children.put(index, child);
	}

	public Map<Integer, RowInfoLeaf> getChildren() {
		return new LinkedHashMap<Integer, RowInfoLeaf>(children);
	}

	@Override
	public String toString() {
		return "RowInfo [key=" + key + ", children=" + children
				+ ", rowStatus=" + rowStatus + "]";
	}
}
