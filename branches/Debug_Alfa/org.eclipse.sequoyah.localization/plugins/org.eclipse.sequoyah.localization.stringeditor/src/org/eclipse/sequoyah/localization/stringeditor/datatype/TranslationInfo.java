/********************************************************************************
 * Copyright (c) 2009 Motorola Inc.
 * All rights reserved. This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Matheus Tait Lima (Eldorado)
 * 
 * Contributors:
 * name (company) - description.
 ********************************************************************************/
package org.eclipse.sequoyah.localization.stringeditor.datatype;

import java.util.Map;

/**
 * This class represents a column of the editor, with extra information about
 * translation
 */
public class TranslationInfo extends ColumnInfo
{

    public static int TYPE_COLUMN = 0;

    public static int TYPE_CELL = 0;

    private int type;

    private String toLang = "";

    private String toColumn = "";

    private String fromLang = "";

    private String fromWord = null;

    private String fromKey = "";

    private String translator = "";

    private String toWord = "";

    /**
     * The constructor
     */
    public TranslationInfo(String id, String tooltip, Map<String, CellInfo> cells, boolean canRemove)
    {
        super(id, tooltip, cells, canRemove);
    }

    /**
     * The constructor
     */
    public TranslationInfo(String id, String tooltip, Map<String, CellInfo> cells,
            boolean canRemove, String from_lang, String to_lang, String fromWord, String translator)
    {
        super(id, tooltip, cells, canRemove);
        this.fromLang = from_lang;
        this.toLang = to_lang;
        this.fromWord = fromWord;
        this.translator = translator;
    }

    /**
     * @return
     */
    public String getTranslator()
    {
        return translator;
    }

    /**
     * @param translator
     */
    public void setTranslator(String translator)
    {
        this.translator = translator;
    }

    public static int getTYPE_COLUMN()
    {
        return TYPE_COLUMN;
    }

    public static void setTYPE_COLUMN(int tYPECOLUMN)
    {
        TYPE_COLUMN = tYPECOLUMN;
    }

    public static int getTYPE_CELL()
    {
        return TYPE_CELL;
    }

    public static void setTYPE_CELL(int tYPECELL)
    {
        TYPE_CELL = tYPECELL;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public String getToLang()
    {
        return toLang;
    }

    public void setToLang(String toLang)
    {
        this.toLang = toLang;
    }

    public String getFromLang()
    {
        return fromLang;
    }

    public void setFromLang(String fromLang)
    {
        this.fromLang = fromLang;
    }

    public String getFromWord()
    {
        return fromWord;
    }

    public void setFromWord(String fromWord)
    {
        this.fromWord = fromWord;
    }

    public String getFromKey()
    {
        return fromKey;
    }

    public void setFromKey(String fromKey)
    {
        this.fromKey = fromKey;
    }

    public String getToWord()
    {
        return toWord;
    }

    public void setToWord(String toWord)
    {
        this.toWord = toWord;
    }

    public String getToColumn()
    {
        return toColumn;
    }

    public void setToColumn(String toColumn)
    {
        this.toColumn = toColumn;
    }

}
