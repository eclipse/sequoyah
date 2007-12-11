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

package org.eclipse.tml.framework.device.model;




public interface IInstance {
	public String getId();
	public void setId(String id);
	public String getName();
	public void setName(String name);
	public String getDevice();
	public void setDevice(String deviceId);
	public IStatus getStatus();
	public void setStatus(IStatus status);
}
