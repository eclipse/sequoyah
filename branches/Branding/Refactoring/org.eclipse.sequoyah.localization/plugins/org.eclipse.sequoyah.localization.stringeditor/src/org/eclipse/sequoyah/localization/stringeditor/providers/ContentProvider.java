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
package org.eclipse.tml.localization.stringeditor.providers;

import org.eclipse.tml.localization.stringeditor.editor.input.IStringEditorInput;

/**
 * This class gives editor its input and operation provider
 */
public class ContentProvider {

	private final IStringEditorInput editorInput;

	private final IOperationProvider operationProvider;

	private final ICellValidator cellValidator;

	private final String contextHelpID;

	/**
	 * Internal use content provider class constructor
	 * 
	 * @param input
	 * @param opProvider
	 * @param validator
	 */
	public ContentProvider(IStringEditorInput input,
			IOperationProvider opProvider, ICellValidator validator,
			String contextHelpID) {
		this.editorInput = input;
		this.operationProvider = opProvider;
		this.cellValidator = validator;
		this.contextHelpID = contextHelpID;
	}

	public IStringEditorInput getEditorInput() {
		return editorInput;
	}

	public IOperationProvider getOperationProvider() {
		return operationProvider;
	}

	public ICellValidator getValidator() {
		return cellValidator;
	}

	public String getContextHelpID() {
		return contextHelpID;
	}

}
