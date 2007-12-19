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
package org.eclipse.tml.framework.status;

import org.eclipse.tml.common.utilities.exception.TmLException;
import org.eclipse.tml.framework.device.model.IInstance;

public abstract class StatusHandler implements IStatusHandler {

	public String run(IStatusTransition transition,IInstance instance) {
		String status = null;
		try {
			execute(transition,instance);
			status = transition.getEndId();			
		} catch (TmLException te) {
			status = transition.getHaltId();	
		}
		return status;
	}

	public abstract void execute(IStatusTransition transition,IInstance instance) throws TmLException; 

}
