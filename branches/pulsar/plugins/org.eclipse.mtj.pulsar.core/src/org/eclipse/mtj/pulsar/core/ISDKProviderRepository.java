/**
 * Copyright (c) 2009 Motorola
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Gustavo de Paula (Motorola) - Initial implementation
 */
package org.eclipse.mtj.pulsar.core;

import java.awt.Image;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * This entity represents a SDK provider repository. From it is possible to 
 * query the categories that this SDK provider has
 *  
 * @since 1.0
 */
public interface ISDKProviderRepository {

	/**
	 * Returns the list of categories that this SDK Provider has
	 * 
	 * @return list of categories
	 * @throws CoreException thrown if there is any error to list the categories
	 */
	public List <ISDKProviderCategory> getCategories (IProgressMonitor monitor) throws CoreException;

	/**
	 * Returns the image that is associated with this SDK provider
	 * 
	 * @return SDK provider logo
	 * @throws CoreException thrown if there is any error to read the SDK Provider image
	 */
	public Image getSDKProviderLogo () throws CoreException;
}
