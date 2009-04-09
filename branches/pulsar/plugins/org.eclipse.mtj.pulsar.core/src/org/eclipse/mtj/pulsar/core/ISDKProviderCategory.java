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

import java.util.List;

/**
 * This entity represents a category that is aavailable in pone specific SDK provider
 * repository
 * 
 * @since 1.0
 *
 */
public interface ISDKProviderCategory {

	/**
	 * Returns the category name
	 * 
	 * @return Category name
	 */
	public String getCategoryName ();
	
	/**
	 * Returns the list of SDKs that are available on one specific SDK category
	 * 
	 * @return List of SDKs of that specific category 
	 */
	public List <ISDK> getSDKs ();
}
