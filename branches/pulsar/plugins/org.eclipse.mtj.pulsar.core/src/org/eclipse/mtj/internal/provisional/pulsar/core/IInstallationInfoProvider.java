/**
 * Copyright (c) 2009 Motorola.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     David Marques (Motorola) - Initial version
 */
package org.eclipse.mtj.internal.provisional.pulsar.core;

/**
 * IInstallationInfoProvider interface defines methods
 * for any installation unit that may provide installation
 * information to the user.
 *
 * @author David Marques
 */
public interface IInstallationInfoProvider {

	/**
	 * Gets an {@link IInstallationInfo} instance in
	 * order to get information from.
	 * 
	 * @return an {@link IInstallationInfo} instance.
	 */
	public IInstallationInfo getInstallationInfo();
	
}
