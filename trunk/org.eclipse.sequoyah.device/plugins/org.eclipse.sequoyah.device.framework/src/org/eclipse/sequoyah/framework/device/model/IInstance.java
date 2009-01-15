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
 * Otavio Luiz Ferranti (Eldorado Research Institute) - bug#221733 - Adding data persistence
 * Yu-Fen Kuo (MontaVista) - bug#236476 - provide a generic device type
 * Daniel Barboza Franco (Eldorado Research Institute) - Bug [221739] - Improvements to State machine implementation
 ********************************************************************************/

package org.eclipse.tml.framework.device.model;

import java.util.Properties;

import org.eclipse.core.runtime.IAdaptable;

/**
 * Interface which defines the requirements of the device instance classes 
 * @author Fabio Fantato
 */
public interface IInstance extends IAdaptable {
	public int getPID();
	public void setPID(int pid);
	public String getId();
	public void setId(String id);
	public String getName();
	public void setName(String name);
	public String getDeviceTypeId();
	public void setDeviceTypeId(String deviceTypeId);
	public String getStatus();
	public void setStatus(String status);
	public Properties getProperties();
	public void setProperties(Properties properties);

}
