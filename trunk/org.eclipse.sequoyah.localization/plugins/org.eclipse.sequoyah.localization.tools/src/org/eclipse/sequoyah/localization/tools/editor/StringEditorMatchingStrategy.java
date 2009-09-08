/********************************************************************************
 * Copyright (c) 2009 Motorola Inc.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Marcelo Bossoni (Motorola)
 * 
 * Contributors:
 * name (company) - description.
 ********************************************************************************/
package org.eclipse.tml.localization.tools.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.tml.common.utilities.BasePlugin;
import org.eclipse.tml.localization.stringeditor.editor.StringEditorPart;
import org.eclipse.tml.localization.tools.extensions.classes.ILocalizationSchema;
import org.eclipse.tml.localization.tools.managers.LocalizationManager;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorMatchingStrategy;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPart;

public class StringEditorMatchingStrategy implements IEditorMatchingStrategy
{
    /*
     * (non-Javadoc)
     * 
     * @seeorg.eclipse.ui.IEditorMatchingStrategy#matches(org.eclipse.ui.
     * IEditorReference, org.eclipse.ui.IEditorInput)
     */
    public boolean matches(IEditorReference editorRef, IEditorInput input)
    {
        boolean matches = false;
        IFile inputFile = ((IFileEditorInput) input).getFile();
        IProject p = inputFile.getProject();
        ILocalizationSchema localizationSchema =
                LocalizationManager.getInstance().getLocalizationSchema(p);
        if (localizationSchema != null)
        {
            IWorkbenchPart part = editorRef.getPart(false);
            if (part instanceof StringEditorPart)
            {
                StringEditorPart stringEditor = (StringEditorPart) part;
                if (stringEditor.getAssociatedProject().equals(p)
                        && localizationSchema.isLocalizationFile(inputFile))
                {
                    matches = true;
                    BasePlugin.logInfo("Editor already opened. Setting focus to editor");
                }
            }
        }
        return matches;
    }
}
