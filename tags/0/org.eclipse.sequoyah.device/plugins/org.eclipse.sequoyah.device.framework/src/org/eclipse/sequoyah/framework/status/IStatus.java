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
 * Fabio Rigo (Eldorado) - [245111] Disable the "Delete" option in popup if the instance is not prepared for deletion
 * Daniel Barboza Franco (Eldorado Research Institute) - Bug [246082] - Complement bug #245111 by allowing disable of "Properties" option as well
 ********************************************************************************/

package org.eclipse.tml.framework.status;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * @author wfr004
 *
 */
public interface IStatus {
    
	/**
	 * @return
	 */
	public String getId(); 	
	public void setId(String id); 	
	
	/**
	 * @return
	 */
	public String getName();
	public void setName(String name);
	
	/**
	 * @return
	 */
	public ImageDescriptor getImage();
	public void setImage(ImageDescriptor image);
	
	/**
	 * @return
	 */
	public String toString();
	
	/**
	 * @return
	 */
	public Object getParent();	
	public void setParent(Object instance);
	/**
     * @return
     */
	public boolean canDeleteInstance();
	public void setCanDeleteInstance(boolean canDeleteInstance);

	public boolean canEditProperties();
	public void setCanEditProperties(boolean canEditProperties);

	
	/**
	 * @return
	 */
	public Object clone();
}