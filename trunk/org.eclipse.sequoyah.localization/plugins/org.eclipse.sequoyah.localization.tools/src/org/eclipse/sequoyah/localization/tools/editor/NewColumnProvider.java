/********************************************************************************
 * Copyright (c) 2009 Motorola Inc.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Matheus Tait Lima (Eldorado)
 * 
 * Contributors:
 * name (company) - description.
 ********************************************************************************/
package org.eclipse.tml.localization.tools.editor;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Status;
import org.eclipse.tml.common.utilities.exception.TmLException;
import org.eclipse.tml.common.utilities.exception.TmLExceptionStatus;
import org.eclipse.tml.localization.stringeditor.datatype.ColumnInfo;
import org.eclipse.tml.localization.stringeditor.providers.DefaultOperationProvider;
import org.eclipse.tml.localization.tools.LocalizationToolsPlugin;
import org.eclipse.tml.localization.tools.i18n.Messages;
import org.eclipse.tml.localization.tools.managers.LocalizationManager;
import org.eclipse.tml.localization.tools.managers.ProjectLocalizationManager;

/**
 * Extends the DefaultOperationProvider in order to provide new,
 * empty localization files (for the editor, a new file is a 
 * new column).
 *
 */
public class NewColumnProvider extends DefaultOperationProvider {

	/*
	 * The Project Localization Manager used as a source to get all 
	 * information provided by this class
	 */
	private static ProjectLocalizationManager projectLocalizationManager = null;

	/**
	 * Instantiate the Project Localization Manager
	 * 
	 * @throws TmLException
	 */
	public void init(IProject project) throws TmLException {
		projectLocalizationManager = LocalizationManager.getInstance()
				.getProjectLocalizationManager(project, true);
		if (projectLocalizationManager == null) {

			Status status = new Status(Status.ERROR,
					LocalizationToolsPlugin.PLUGIN_ID,
					Messages.StringEditorInput_ErrorInitializingEditor);
			throw new TmLException(new TmLExceptionStatus(status));
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.tml.localization.stringeditor.providers.DefaultOperationProvider#getNewColumn()
	 */
	@Override
	public ColumnInfo getNewColumn() {
		return projectLocalizationManager.getProjectLocalizationSchema()
				.promptCollumnName();

	}

}
