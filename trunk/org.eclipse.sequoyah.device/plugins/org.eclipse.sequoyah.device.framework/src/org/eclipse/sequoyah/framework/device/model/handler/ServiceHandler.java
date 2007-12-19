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
package org.eclipse.tml.framework.device.model.handler;

import org.eclipse.tml.common.utilities.exception.TmLException;
import org.eclipse.tml.framework.device.factory.InstanceRegistry;
import org.eclipse.tml.framework.device.model.IInstance;
import org.eclipse.tml.framework.device.model.IService;
import org.eclipse.tml.framework.status.IStatusTransition;

public abstract class ServiceHandler implements IServiceHandler {
	private IServiceHandler parent;
	private IService service;
	
	public IServiceHandler getParent() {
		return parent;
	}

	public void run(IInstance instance) throws TmLException {
		if (!verifyStatus(instance)) {
			throw new TmLException();
		}
		runService(instance);
		if (parent!=null) {
			parent.updatingService(instance);
		} else {
			this.updatingService(instance);
		}		
		updateStatus(instance);
	}
	
	public void setService(IService service){
		this.service = service;
	};
	
	public IService getService(){
		return service;
	}
		
	public abstract void runService(IInstance instance);
	public abstract void updatingService(IInstance instance);
	public abstract IServiceHandler newInstance();
	
	public void updateStatus(IInstance instance){
		IStatusTransition transition = getService().getStatusTransitions(instance.getStatus());
		instance.setStatus(transition.getHandler().run(transition,instance));
		InstanceRegistry.getInstance().setDirty(true);
	}
	
	public boolean verifyStatus(IInstance instance){
		IStatusTransition transition = getService().getStatusTransitions(instance.getStatus());
		return (transition!=null);		
	}
	
	public void setParent(IServiceHandler handler) {
		this.parent = handler;
	}
	
	public Object clone(){
		IServiceHandler newHandler = newInstance();
		newHandler.setParent(parent);
		newHandler.setService(service);
		return newHandler;
	}
	

}
