/********************************************************************************
 * Copyright (c) 2010 Motorola Mobility, Inc.
 * All rights reserved. This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Marcelo Marzola Bossoni (Eldorado)
 * 
 * Contributors:
 * <name> (<company>) - Bug [<bugid>] - <bugDescription>
 ********************************************************************************/
package org.eclipse.sequoyah.localization.editor.model.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.sequoyah.localization.editor.i18n.Messages;
import org.eclipse.sequoyah.localization.editor.model.StringEditorPart;
import org.eclipse.sequoyah.localization.editor.model.operations.RevertColumnToSavedStateOperation;

public class RevertToSavedAction extends Action {
	/**
	 * 
	 */
	private final StringEditorPart stringEditorPart;

	public RevertToSavedAction(StringEditorPart stringEditorPart) {
		super(Messages.StringEditorPart_RevertColumnActionName);
		this.stringEditorPart = stringEditorPart;
		setToolTipText(Messages.StringEditorPart_RevertColumnActionTooltip);
		setEnabled(stringEditorPart.getActiveColumn() != 0);
	}

	@Override
	public void run() {
		String column = stringEditorPart.getEditorViewer().getTree()
				.getColumn(stringEditorPart.getActiveColumn()).getText();
		RevertColumnToSavedStateOperation operation = new RevertColumnToSavedStateOperation(
				Messages.StringEditorPart_RevertColumnActionOperationName,
				stringEditorPart, stringEditorPart.getModel().getColumn(column));
		stringEditorPart.executeOperation(operation);
	}
}