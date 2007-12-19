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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class StatusRegistry {
	private Map<String,IStatus> status;
		private static StatusRegistry _instance;

		private StatusRegistry(){		
			status = new HashMap<String,IStatus>();
		}
		
		public static StatusRegistry getInstance(){
			if (_instance==null) {
				_instance = new StatusRegistry();
			}
			return _instance;
		}
						
		public IStatus getStatus(String statusId) {
			return status.get(statusId);
		}

		public Collection<IStatus> getStatus() {
			return status.values();
		}
		
		public void setStatus(Map<String,IStatus> status) {
			this.status = status;
		}
		
		public void addStatus(IStatus status){
			this.status.put(status.getId(),status);
		}

		public void clear(){
			this.status.clear();
		}
}