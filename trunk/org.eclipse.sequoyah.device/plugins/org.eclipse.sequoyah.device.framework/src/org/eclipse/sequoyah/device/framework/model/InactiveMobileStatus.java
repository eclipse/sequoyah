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

package org.eclipse.sequoyah.device.framework.model;

import org.eclipse.sequoyah.device.framework.internal.model.MobileStatus;



public class InactiveMobileStatus extends MobileStatus {

	public InactiveMobileStatus(){
		super(eStatus.INACTIVE);
	}
	
}
