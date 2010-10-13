/********************************************************************************
 * Copyright (c) 2009-2010 Motorola Mobility, Inc.
 * All rights reserved. This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Marcelo Marzola Bossoni (Eldorado)
 * 
 * Contributors:
 * Marcelo Marzola Bossoni (Eldorado) - Bug [289146] - Performance and Usability Issues
 * Matheus Tait Lima (Eldorado) - Adapting localization plugins to accept automatic translations
 * Daniel Drigo Pastore (Eldorado) - Bug [326793] - Fixed array support for the String Localization Editor
 * 
 ********************************************************************************/
package org.eclipse.sequoyah.localization.editor.providers;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.sequoyah.localization.editor.datatype.ColumnInfo;
import org.eclipse.sequoyah.localization.editor.datatype.RowInfo;
import org.eclipse.sequoyah.localization.editor.datatype.TranslationInfo;
import org.eclipse.sequoyah.localization.editor.i18n.Messages;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PlatformUI;

/**
 * This class provides a basic implementation of the {@link IOperationProvider}
 * It only opens a input dialog for user input
 */
public class DefaultOperationProvider implements IOperationProvider {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.sequoyah.localization.editor.providers.IOperationProvider
	 * #init(org.eclipse .core.resources.IProject)
	 */
	public void init(IProject project) throws Exception {
		// nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.sequoyah.localization.editor.providers.IOperationProvider
	 * #getNewColumn()
	 */
	public ColumnInfo getNewColumn() {
		ColumnInfo newColumn = null;
		InputDialog dialog = new InputDialog(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(),
				Messages.DefaultOperationProvider_NewColumnTitle,
				Messages.DefaultOperationProvider_NewColumnDescription,
				Messages.DefaultOperationProvider_NewColumnDefault, //$NON-NLS-2$
				new IInputValidator() {

					public String isValid(String newText) {
						String errorMessage = null;
						if (newText.length() == 0) {
							errorMessage = Messages.DefaultOperationProvider_NewColumnErrorNotEmpty;
						}
						return errorMessage;
					}
				});

		if (dialog.open() == IDialogConstants.OK_ID) {
			newColumn = new ColumnInfo(dialog.getValue(), dialog.getValue(),
					null, true);
		}

		return newColumn;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.sequoyah.localization.editor.providers.IOperationProvider
	 * #getNewRow()
	 */
	public RowInfo[] getNewRow() {
		RowInfo[] newRow = null;
		InputDialog dialog = new InputDialog(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(),
				Messages.DefaultOperationProvider_NewRowTitle,
				Messages.DefaultOperationProvider_NewRowDescription,
				Messages.DefaultOperationProvider_NewRowDefault,
				new IInputValidator() { //$NON-NLS-2$

					public String isValid(String newText) {
						String errorMessage = null;
						if (newText.length() == 0) {
							errorMessage = Messages.DefaultOperationProvider_NewRowErrorNotEmpty;
						}
						return errorMessage;
					}
				});

		if (dialog.open() == IDialogConstants.OK_ID) {
			newRow = new RowInfo[1];
			newRow[0] = new RowInfo(dialog.getValue(), null);
		}

		return newRow;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.sequoyah.localization.editor.providers.IOperationProvider
	 * #getTranslatedColumnInfo()
	 */
	public TranslationInfo getTranslatedColumnInfo(String selectedColumn) {
		// Needs to be implemented by subclasses if automatic translation is
		// required
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.sequoyah.localization.editor.providers.IOperationProvider
	 * #getTranslatedColumnsInfo()
	 */
	public TranslationInfo[] getTranslatedColumnsInfo(String selectedColumn,
			String[] selectedKeys, String[] selectedCells, TableColumn[] columns) {
		// Needs to be implemented by subclasses if automatic translation is
		// required
		return null;
	}
}
