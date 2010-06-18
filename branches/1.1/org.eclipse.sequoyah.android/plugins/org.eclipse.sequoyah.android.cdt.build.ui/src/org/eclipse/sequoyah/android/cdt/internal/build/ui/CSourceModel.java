package org.eclipse.sequoyah.android.cdt.internal.build.ui;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the methods inside a C source class.
 * @author Paulo Renato de Faria
 *
 */
public class CSourceModel
{
    private List<CSourceMethod> methods = new ArrayList<CSourceMethod>();

    public CSourceModel()
    {
        super();
    }

    @Override
    public String toString()
    {
        return "CSourceModel [methods=" + methods + "]";
    }

    public List<CSourceMethod> getMethods()
    {
        return methods;
    }

    /**
     * Populates methods list  using the difference between two C Source Model
     * It will generate a new list letting all items in first model that are not included on second model
     * (i.e. set difference operation)  
     * @param firstModel 
     * @param secondModel
     */
    public void createModelThroughDiff(CSourceModel firstModel, CSourceModel secondModel)
    {
        if ((firstModel != null) && (firstModel.getMethods().size() != 0))
        {
            if ((secondModel != null) && (secondModel.getMethods().size() != 0))
            {
                methods = firstModel.getMethods();
                methods.removeAll(secondModel.getMethods());

            }
            else
            {
                methods = firstModel.getMethods();
            }
        }
        else
        {
            methods =
                    ((secondModel != null) && (secondModel.getMethods().size() != 0) ? secondModel
                            .getMethods() : null);
        }

    }

}
