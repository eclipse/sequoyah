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


public interface IStatusTransition {

	public String getEndId(); 	
	public void setEndId(String id); 	
	
	public String getStartId(); 	
	public void setStartId(String id); 	
	
	public String getHaltId(); 	
	public void setHaltId(String id); 	
	
		IStatusHandler getHandler();

	void setHandler(IStatusHandler handler);
	public String toString();
}
