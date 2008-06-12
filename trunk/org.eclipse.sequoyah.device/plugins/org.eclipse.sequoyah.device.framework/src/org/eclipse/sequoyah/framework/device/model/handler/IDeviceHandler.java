/********************************************************************************
 * Copyright (c) 2007-2008 Motorola Inc and others.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Fabio Fantato (Motorola)
 * 
 * Contributors:
 * Otávio Luiz Ferranti (Eldorado Research Institute) - bug#221733 - Device handlers
 *                                         shall be able to create device instances.
 ********************************************************************************/
package org.eclipse.tml.framework.device.model.handler;

import org.eclipse.tml.framework.device.model.IInstance;

public interface IDeviceHandler {

	public IInstance createDeviceInstance (String id);
	
}
