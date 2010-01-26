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
 * Fabio Rigo (Eldorado) - Bug [244066] - The services are being run at one of the UI threads
 ********************************************************************************/
package org.eclipse.sequoyah.device.framework.status;


public interface IStatusTransition {

	public String getEndId(); 	
	public void setEndId(String id); 	
	
	public String getStartId(); 	
	public void setStartId(String id); 	
	
	public String getHaltId(); 	
	public void setHaltId(String id); 	
	
	public String toString();
}
