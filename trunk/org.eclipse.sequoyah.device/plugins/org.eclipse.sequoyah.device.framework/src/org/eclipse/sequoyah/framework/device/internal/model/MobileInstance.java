/********************************************************************************
 * Copyright (c) 2007-2008 Motorola Inc and others.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Daniel Barboza Franco (Eldorado Research Institute) - Bug [252261] - Internal class MobileInstance providing functionalities
 * 
 * Contributors:
 * Otávio Luiz Ferranti (Eldorado Research Institute) - bug#221733 - Adding data persistence
 * Yu-Fen Kuo (MontaVista) - bug#236476 - Provide a generic device type
 ********************************************************************************/

package org.eclipse.tml.framework.device.internal.model;

import org.eclipse.tml.framework.device.model.AbstractMobileInstance;

/**
 * Basic implementation of IInstance.
 */

public class MobileInstance extends AbstractMobileInstance {
	
	/**
	 * Constructor - Basic implementation of IInstance.
	 */
	public MobileInstance(String id){
		this.id = id;
		this.pid=0;
	}
	
}
