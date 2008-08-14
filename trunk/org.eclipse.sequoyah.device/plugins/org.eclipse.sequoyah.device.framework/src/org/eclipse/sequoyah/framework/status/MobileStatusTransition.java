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
package org.eclipse.tml.framework.status;


public class MobileStatusTransition implements IStatusTransition {
	private String endId;
	private String startId;
	private String haltId;
	
	public MobileStatusTransition(String startId,String endId,String haltId) {
		this.startId = startId;
		this.endId = endId;
		this.haltId = haltId;
	};
	
	public String getEndId() {
		return endId;
	}
	
	public void setEndId(String id) {
		this.endId = id;
	}
	
	public String getStartId() {
		return startId;
	}
	
	public void setStartId(String id) {
		this.startId = id;
	}
	
	public String getHaltId() {
		return haltId;
	}
	
	public void setHaltId(String id) {
		this.haltId = id;
	}
		
	public String toString(){
		return "[Transition: start="+(startId!=null?startId:"")+" end="+(endId!=null?endId:"")+" halt="+(haltId!=null?haltId:"")+"]";
	}	
	
}
