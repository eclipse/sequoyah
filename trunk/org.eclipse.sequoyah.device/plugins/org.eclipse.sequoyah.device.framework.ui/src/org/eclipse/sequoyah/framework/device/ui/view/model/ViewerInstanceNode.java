/********************************************************************************
 * Copyright (c) 2008 Motorola Inc. and Other. All rights reserved
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Julia Martinez Perdigueiro (Eldorado Research Institute) 
 * [244805] - Improvements on Instance view  
 *
 * Contributors:
 ********************************************************************************/

package org.eclipse.tml.framework.device.ui.view.model;

import org.eclipse.tml.framework.device.model.IInstance;

public class ViewerInstanceNode extends ViewerAbstractNode
{

    
    private IInstance instance;
    
    public ViewerInstanceNode(ViewerAbstractNode parent, IInstance instance)
    {
        super(parent);
        this.instance = instance;
    }

    public void addChild(ViewerAbstractNode child)
    {
        // Do nothing
    }
    
    public String toString()
    {
        return getInstanceName();
    }
    
    public String getInstanceName()
    {
        String instanceName = ""; //$NON-NLS-1$
        if (instance != null)
        {
            instanceName = instance.getName();
        }
        return instanceName;
    }
    
    public String getInstanceStatus()
    {
        String instanceStatus = ""; //$NON-NLS-1$
        if (instance != null)
        {
            instanceStatus = instance.getStatus();
        }
        return instanceStatus;
    }
    
    public IInstance getInstance()
    {
    	return instance;
    }
    
    public boolean containsInstance()
    {
        return instance != null;
    }
}
