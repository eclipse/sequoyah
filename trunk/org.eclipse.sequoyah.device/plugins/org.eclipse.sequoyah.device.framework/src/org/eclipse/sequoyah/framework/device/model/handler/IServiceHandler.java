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
import org.eclipse.tml.framework.device.model.IInstance;
import org.eclipse.tml.framework.device.model.IService;
import org.eclipse.tml.framework.status.IStatusTransition;

public interface IServiceHandler extends Cloneable {
	public void setParent(IServiceHandler handler);
	public IServiceHandler getParent();
	public void run(IInstance instance) throws TmLException;
	public void updatingService(IInstance instance);	
	public void setService(IService service);		
	public IService getService();
	public Object clone();
	public IServiceHandler newInstance();
}

