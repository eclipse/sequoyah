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

import org.eclipse.tml.framework.device.model.IInstance;

public class LabelStatus {
	private IInstance instance;
	private String status;

	public LabelStatus(IInstance instance,String status){
		this.instance = instance;
		this.status = status;
	};
	
	public IInstance getInstance(){
		return instance;
	}
	
	public String getStatus(){
		return status;
	};
	
}
