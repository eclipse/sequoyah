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
package org.eclipse.sequoyah.localization.editor.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.sequoyah.localization.editor.datatype.RowInfo;
import org.eclipse.sequoyah.localization.editor.datatype.RowInfoLeaf;

/**
 * This class provides a content provider for the editor
 */
public class StringEditorViewerContentProvider implements
		IStructuredContentProvider {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java
	 * .lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		Object[] elements = null;
		if (inputElement instanceof StringEditorViewerModel) {
			StringEditorViewerModel theInput = (StringEditorViewerModel) inputElement;
			Map<String, RowInfo> rows = theInput.getRows();
			List<RowInfo> rowsList = new ArrayList<RowInfo>();
			for (String key : rows.keySet()) {
				RowInfo aRow = rows.get(key);
				rowsList.add(aRow);
				// check if it is an array; if so, add its children
				if (!(aRow instanceof RowInfoLeaf)) {
					Map<Integer, RowInfoLeaf> children = aRow.getChildren();
					for (RowInfoLeaf child : children.values()) {
						rowsList.add(child);
					}
				}
			}
			elements = new Object[rowsList.size()];
			elements = rowsList.toArray(elements);
		}

		return elements;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface
	 * .viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// do nothing
	}
}
