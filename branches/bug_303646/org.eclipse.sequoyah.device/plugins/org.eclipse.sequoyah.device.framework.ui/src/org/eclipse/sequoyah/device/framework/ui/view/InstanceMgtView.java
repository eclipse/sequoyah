/********************************************************************************
 * Copyright (c) 2008-2010 Motorola Inc. and Other. All rights reserved
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
 * Daniel Barboza Franco (Eldorado Research Institute) - Bug [280981] - Add suport for selecting instances programatically 
 * Daniel Barboza Franco (Eldorado Research Institute) - Bug [280982] - Add sensitive context help support to InstanceView and New Instance Wizard.
 * Mauren Brenner (Eldorado) - [282428] Notify when instance is null
 * Mauren Brenner (Eldorado) - [282724] Add dispose listener to the top composite
 * Daniel Pastore (Eldorado) - [289870] Moving and renaming Tml to Sequoyah
 * Marcel Gorri (Eldorado) - [303646] Add support for UI styles.
 ********************************************************************************/

package org.eclipse.sequoyah.device.framework.ui.view;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.sequoyah.device.framework.model.IInstance;
import org.eclipse.sequoyah.device.framework.ui.view.model.InstanceSelectionChangeEvent;
import org.eclipse.sequoyah.device.framework.ui.view.model.InstanceSelectionChangeListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class InstanceMgtView extends ViewPart
{   

	private static final Set<InstanceSelectionChangeListener> listeners = new LinkedHashSet<InstanceSelectionChangeListener>();
    private static InstanceServicesComposite instanceServicesComposite = null;
    private static IInstance selectedInstance = null;
    private static InstanceStatusComposite topComposite;
    private static InstanceSelectionChangeListener selectionChangeListener;
    
	
	private static SashForm form;
	private static String contextId;
	private boolean useDropDown;
	private String viewLayout;
	
    public InstanceMgtView()
    {
    }

    public static InstanceServicesComposite getInstanceServicesComposite(){
    	return instanceServicesComposite;
    }
    
    /*
    public static void setHelpContextID (String contextId_) {
    	contextId = contextId_;
    }
    */
    
    public static void setHelp(String contextId) {
    	InstanceMgtView.contextId = contextId;
    	if (form != null) {
    		form.getDisplay().asyncExec(new Runnable () {
				public void run() {
	    			PlatformUI.getWorkbench().getHelpSystem().setHelp(form, InstanceMgtView.contextId );	
				}
    		});
    	}
    }
    
    public void createPartControl(Composite parent)
    {
    	IExtensionPoint extensionPoint = Platform.getExtensionRegistry()
    	.getExtensionPoint("org.eclipse.sequoyah.device.framework.ui.deviceManagerView");
    	IConfigurationElement[] configurationElements = extensionPoint.getConfigurationElements();
    	for(IConfigurationElement configElement : configurationElements) {
    		String attribute = configElement.getAttribute("useDropDown");
    		useDropDown = Boolean.parseBoolean(attribute);
    		viewLayout = configElement.getAttribute("viewLayout");
    	}
    	
    	if(viewLayout.equals("vertical")) {
    		form = new SashForm(parent,SWT.VERTICAL);
    	} else {
    		form = new SashForm(parent,SWT.HORIZONTAL);
    	}
    		
        form.setLayout(new FillLayout());
        
        topComposite = new InstanceStatusComposite(form, getViewSite(), useDropDown);
        topComposite.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e)
            {
                if (selectionChangeListener != null)
                {
                    topComposite.removeInstanceSelectionChangeListener(selectionChangeListener);
                    topComposite.removeDisposeListener(this);
                    selectionChangeListener = null;                    
                }
            }
        });
        
        // TODO: if clause kept just for safety reasons
        if (selectionChangeListener != null) {
            topComposite.removeInstanceSelectionChangeListener(selectionChangeListener);
        }
        
        if(!useDropDown) {
        	final InstanceServicesComposite bottomComposite = new InstanceServicesComposite(form);
        	instanceServicesComposite = bottomComposite;
        	form.setWeights(new int[] {60,40});
        	selectionChangeListener = new InstanceSelectionChangeListener() {
        		public void instanceSelectionChanged(InstanceSelectionChangeEvent event)
        		{
        			bottomComposite.setSelectedInstance(event.getInstance());
        		}
        	};
        	topComposite.addInstanceSelectionChangeListener(selectionChangeListener);
        }
        else {
        	form.setWeights(new int[] {100});
        }

        if (contextId != null) {
        	PlatformUI.getWorkbench().getHelpSystem().setHelp(form, contextId);
        }
    }

    public void setFocus()
    {
    	if (form != null) { 
    		form.setFocus();
    	}

    }

    
    /**
     * Set the current selected instance in this view. 
     * If the view is closed, the information will be recorded for further update.
     * In this case change listeners will be notified only when the node have been selected (After the view was opened).
     * 
     *  @param instance - the instance to be selected.
     */
    public static void setSeletectedInstance (IInstance instance){
    	selectedInstance = instance;
    	
    	if (topComposite != null) {
    		//topComposite.refreshViewer(instance);
    		topComposite.selectInstance(instance);
    	}
    }

	public static IInstance getSelectedInstance() {
		
		if (topComposite != null) {
			return topComposite.getSelectedInstance();
		}
		
		return selectedInstance;
	}
	
	public static void addInstanceSelectionChangeListener(InstanceSelectionChangeListener listener)	{
		listeners.add(listener);
	}
	
	public static void removeInstanceSelectionChangeListener(InstanceSelectionChangeListener listener) {
		listeners.remove(listener);
	}
	
	protected static void notifyInstanceSelectionChangeListeners(IInstance instance)	{
		InstanceSelectionChangeEvent event = new InstanceSelectionChangeEvent(instance);
		for (InstanceSelectionChangeListener listener : listeners) {
			listener.instanceSelectionChanged(event);
		}
	}
	
	@Override
	public void dispose() {
		
		if (topComposite != null) {
			topComposite.removeListener();
		}
		
		super.dispose();
	}
	
}
