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
package org.eclipse.sequoyah.localization.stringeditor.editor;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.sequoyah.localization.stringeditor.datatype.CellInfo;
import org.eclipse.sequoyah.localization.stringeditor.datatype.RowInfo;
import org.eclipse.sequoyah.localization.stringeditor.i18n.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * This class provides a label for each column
 */
public class StringEditorColumnLabelProvider extends ColumnLabelProvider {

	private final String column;

	private final StringEditorPart editor;

	private final Color searchColor = new Color(Display.getDefault(), 255, 200,
			200);

	/**
	 * Creates a new label provider
	 * 
	 * @param column
	 *            the columnID to get the info
	 */
	public StringEditorColumnLabelProvider(String column,
			StringEditorPart editor) {
		this.column = column;
		this.editor = editor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		if (column.equalsIgnoreCase(Messages.StringEditorPart_KeyLabel)) {
			return super.getText(((RowInfo) element).getKey());
		}
		return super.getText(((RowInfo) element).getCells().get(column));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.CellLabelProvider#getToolTipText(java.lang.
	 * Object)
	 */
	@Override
	public String getToolTipText(Object element) {
		String comment = null;
		RowInfo row = (RowInfo) element;
		if (column.equalsIgnoreCase(Messages.StringEditorPart_KeyLabel)) {
			if (!row.getStatus().isOK()) {
				StringBuilder builder = new StringBuilder();
				for (IStatus child : row.getStatus().getChildren()) {
					builder.append(child.getMessage());
					builder.append(Messages.StringEditorColumnLabelProvider_0);
				}
				comment = builder.toString();
			}

		} else if (editor.getShowCellComments()) {
			CellInfo cell = row.getCells().get(column);
			if (cell != null && cell.getValue() != null
					&& cell.getValue().trim().length() > 0) {
				comment = cell.getComment() != null ? cell.getComment() : ""; //$NON-NLS-1$
			}
		}
		return comment;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ColumnLabelProvider#getBackground(java.lang
	 * .Object)
	 */
	@Override
	public Color getBackground(Object element) {
		Color c = null;
		if (column.equals(Messages.StringEditorPart_KeyLabel)) {
			c = Display.getDefault().getSystemColor(
					SWT.COLOR_WIDGET_LIGHT_SHADOW);
		} else {
			CellInfo cell = ((RowInfo) element).getCells().get(column);
			String searchText = editor.getSearchText();
			if (cell != null) {
				if (searchText.length() > 0
						&& cell.toString().toLowerCase().contains(
								searchText.toLowerCase())) {
					c = searchColor;
				} else if (editor.getHighlightChanges() && cell.isDirty()) {
					c = Display.getDefault().getSystemColor(
							SWT.COLOR_INFO_BACKGROUND);
				}
			}
		}
		return c;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ColumnLabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage(Object element) {
		Image statusImage = null;
		if (column.equalsIgnoreCase(Messages.StringEditorPart_KeyLabel)) {
			RowInfo row = ((RowInfo) element);
			switch (row.getStatus().getSeverity()) {
			case IStatus.ERROR:
				statusImage = editor.getErrorImage();
				break;
			case IStatus.WARNING:
				statusImage = editor.getWarningImage();
				break;
			default:
				statusImage = editor.getOKImage();
				break;
			}
		}

		return statusImage;
	}

}
