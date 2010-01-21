/********************************************************************************
 * Copyright (c) 2007 Motorola Inc.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Fabio Fantato (Motorola)
 * 
 * Contributors:
 * name (company) - description.
 ********************************************************************************/
package org.eclipse.sequoyah.device.framework.model.handler;

import org.eclipse.sequoyah.device.common.utilities.BasePlugin;
import org.eclipse.sequoyah.device.common.utilities.exception.SequoyahException;
import org.eclipse.sequoyah.device.framework.model.IInstance;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;


public class ServiceHandlerAction implements Listener {
	private IInstance instance;
	private IServiceHandler serviceHandler;
	private String text;
	
	public ServiceHandlerAction(IInstance instance,IServiceHandler handler){
		this.instance = instance;
		this.serviceHandler = handler;
	}
	
	public ServiceHandlerAction(String text){
		serviceHandler = null;
		this.text = text;
	}
	
	
     public void handleEvent(Event event) {
    	 if (serviceHandler!=null) {  
    		 try {
    			 serviceHandler.run(instance);
    		 } catch (SequoyahException te){
    			 BasePlugin.logError(text+"-"+te.getMessage()); //$NON-NLS-1$
    		 }
    	 }
     }
	
}
