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

import org.eclipse.tml.framework.device.model.IInstance;

public abstract class ServiceHandler implements IServiceHandler {
	private IServiceHandler parent;
	
	public IServiceHandler getParent() {
		return parent;
	}

	public void run(IInstance instance) {
		runService(instance);
		if (parent!=null) {
			parent.updatingService(instance);
		} else {
			this.updatingService(instance);
		}
	}
		
	public abstract void runService(IInstance instance);
	public abstract void updatingService(IInstance instance);
	
	public void setParent(IServiceHandler handler) {
		this.parent = handler;
	}
	
}
