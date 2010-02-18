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
package org.eclipse.sequoyah.localization.stringeditor.datatype;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.sequoyah.localization.stringeditor.StringEditorPlugin;

/**
 * This class represents a row of the editor
 */
public class RowInfo
{

    /*
     * This row key
     */
    private String key;

    /*
     * This row cells
     */
    private final Map<String, CellInfo> cells;

    /*
     * Array info
     */
    boolean isArray = false;

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
    public RowInfo(String key, boolean isArray, Map<String, CellInfo> cells)
    {
        this.key = key;
        this.isArray = isArray;
        this.cells = cells != null ? cells : new HashMap<String, CellInfo>();
        this.rowStatus = new MultiStatus(StringEditorPlugin.PLUGIN_ID, 0, null, null);
    }

    /**
     * Add a cell to this row
     * 
     * @param columnID
     * @param value
     */
    public void addCell(String columnID, CellInfo value)
    {
        cells.put(columnID, value);
    }

    /**
     * Remove a cell of this row.
     * 
     * @param columnID
     */
    public void removeCell(String columnID)
    {
        cells.remove(columnID);
    }

    /**
     * get this row key
     * 
     * @return
     */
    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    /**
     * get this row cells
     * 
     * @return the cells
     */
    public Map<String, CellInfo> getCells()
    {
        return cells;
    }

    public void addStatus(IStatus status)
    {
        this.rowStatus.merge(status);
    }

    public void cleanStatus()
    {
        this.rowStatus = new MultiStatus(StringEditorPlugin.PLUGIN_ID, 0, null, null);
    }

    public MultiStatus getStatus()
    {
        return rowStatus;
    }

    public boolean isArray()
    {
        return isArray;
    }

    public void setArray(boolean isArray)
    {
        this.isArray = isArray;
    }

}
