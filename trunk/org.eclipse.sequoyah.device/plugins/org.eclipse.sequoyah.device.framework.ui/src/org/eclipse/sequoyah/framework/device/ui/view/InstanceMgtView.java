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
 * Daniel Barboza Franco (Eldorado Research Institute) - Bug [250644] - Instance view keeps enabled buttons while performing a service.
 ********************************************************************************/

package org.eclipse.tml.framework.device.ui.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.tml.framework.device.ui.view.InstanceStatusComposite.InstanceSelectionChangeEvent;
import org.eclipse.tml.framework.device.ui.view.InstanceStatusComposite.InstanceSelectionChangeListener;
import org.eclipse.ui.part.ViewPart;

public class InstanceMgtView extends ViewPart
{   
    public InstanceMgtView()
    {
    }

    private static InstanceServicesComposite instanceServicesComposite = null;
    
    public static InstanceServicesComposite getInstanceServicesComposite(){
    	return instanceServicesComposite;
    }
    
    public void createPartControl(Composite parent)
    {
        SashForm form = new SashForm(parent,SWT.VERTICAL);
        form.setLayout(new FillLayout());
        
        InstanceStatusComposite topComposite = new InstanceStatusComposite(form, getViewSite());
        
        final InstanceServicesComposite bottomComposite = new InstanceServicesComposite(form);
        instanceServicesComposite = bottomComposite;
        
        form.setWeights(new int[] {60,40});
        
        topComposite.addInstanceSelectionChangeListener(new InstanceSelectionChangeListener()
        {
            public void instanceSelectionChanged(InstanceSelectionChangeEvent event)
            {
                bottomComposite.setSelectedInstance(event.getInstance());
            }
        });
    }
    
    public void setFocus()
    {
    }
}
