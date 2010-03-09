/**
 * Copyright (c) 2009 Motorola
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Euclides Neto (Motorola) - Initial implementation.
 */
package org.eclipse.sequoyah.pulsar.internal.provisional.core;

/**
 * Represents one Category that may be presented in the QuickInstall view
 * 
 */
public interface ISDKCategory extends IInstallationInfoProvider {
	
	/**
	 * Returns the name of this category
	 * 
	 * @return String
	 */
	public String getName();
	
	/**
         * Returns the description of this category
         * 
         * @return String
         */
        public String getDescription();

}
