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
package org.eclipse.sequoyah.localization.stringeditor.editor.operations;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.sequoyah.localization.stringeditor.datatype.RowInfo;
import org.eclipse.sequoyah.localization.stringeditor.editor.StringEditorPart;

/**
 * The operation of adding a new key (row) to the editor.
 */
public class AddKeysOperation extends EditorOperation
{
    private List<AddKeyOperation> addKeyOperations = new ArrayList<AddKeyOperation>();

    public AddKeysOperation(String label, StringEditorPart editor, RowInfo[] rows)
    {
        super(label, editor);
        for (int i = 0; i < rows.length; i++)
        {
            this.addKeyOperations.add(new AddKeyOperation(label, editor, rows[i]));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.core.commands.operations.AbstractOperation#execute(org.eclipse
     * .core.runtime.IProgressMonitor, org.eclipse.core.runtime.IAdaptable)
     */
    @Override
    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException
    {
        return redo(monitor, info);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.core.commands.operations.AbstractOperation#redo(org.eclipse
     * .core.runtime.IProgressMonitor, org.eclipse.core.runtime.IAdaptable)
     */
    @Override
    public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException
    {
        for (AddKeyOperation addKeyOperation : this.addKeyOperations)
        {
            addKeyOperation.redo(monitor, info);
        }
        return Status.OK_STATUS;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.core.commands.operations.AbstractOperation#undo(org.eclipse
     * .core.runtime.IProgressMonitor, org.eclipse.core.runtime.IAdaptable)
     */
    @Override
    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException
    {
        for (AddKeyOperation addKeyOperation : this.addKeyOperations)
        {
            addKeyOperation.undo(monitor, info);
        }
        return Status.OK_STATUS;
    }

}
