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

import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationListener;
import org.eclipse.jface.viewers.ColumnViewerEditorDeactivationEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerCell;

public class StringEditorColumnViewerEditorActivationListener extends
		ColumnViewerEditorActivationListener {

	private final TableViewer viewer;

	public StringEditorColumnViewerEditorActivationListener(TableViewer viewer) {
		this.viewer = viewer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jface.viewers.ColumnViewerEditorActivationListener#
	 * beforeEditorDeactivated
	 * (org.eclipse.jface.viewers.ColumnViewerEditorDeactivationEvent)
	 */
	@Override
	public void beforeEditorDeactivated(
			ColumnViewerEditorDeactivationEvent event) {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jface.viewers.ColumnViewerEditorActivationListener#
	 * beforeEditorActivated
	 * (org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent)
	 */
	@Override
	public void beforeEditorActivated(ColumnViewerEditorActivationEvent event) {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jface.viewers.ColumnViewerEditorActivationListener#
	 * afterEditorDeactivated
	 * (org.eclipse.jface.viewers.ColumnViewerEditorDeactivationEvent)
	 */
	@Override
	public void afterEditorDeactivated(ColumnViewerEditorDeactivationEvent event) {
		ViewerCell cell = (ViewerCell) event.getSource();
		// traverse the viewer without any key press
		if (event.eventType == ColumnViewerEditorDeactivationEvent.EDITOR_SAVED) {
			ViewerCell nieghbor = cell.getNeighbor(ViewerCell.BELOW, false);
			if (nieghbor != null) {
				viewer.setSelection(new StructuredSelection(nieghbor
						.getElement()));
			}
		}
	}

	@Override
	public void afterEditorActivated(ColumnViewerEditorActivationEvent event) {

	}
}
