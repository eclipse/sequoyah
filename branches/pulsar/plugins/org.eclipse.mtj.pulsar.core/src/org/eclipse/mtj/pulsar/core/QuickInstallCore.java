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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.equinox.internal.provisional.p2.metadata.IInstallableUnit;

/**
 * This is a facade to all services that are needed from the quickinstall view
 *  
 * @since 1.0
 */
public class QuickInstallCore {

	private static QuickInstallCore instance = new QuickInstallCore();
	
	
	/**
	 * Returns an instance of the quick instance core services
	 * 
	 * @return quickinstall core services instance
	 */
	public static QuickInstallCore getInstance () {
		return instance;
	}

	/**
	 * Returns a list os SDK providers repositories. This list can then be used to query
	 * all categories and SDKs and then assemble the quickinstall UI
	 * 
	 * This method might connect to the network to update the repositories.
	 * 
	 * @param monitor monitor to track the method execution
	 * @return List of SDK Provider repositories
	 * @throws CoreException thrown if there is any error to read the information
	 */
	public List <ISDKProviderRepository> getSDKProvidersRepositories (IProgressMonitor monitor) throws CoreException {
		return null;
	}

	/**
	 * Ask P2 to install the instalable units selected by the user. This method will
	 * end up calling P2 UI to validate all provisioning process
	 * 
	 * @param uis list of units to install
	 * @throws CoreException thrown if there is any error on the install process
	 */
	public void installSDKs (List <IInstallableUnit> uis) throws CoreException {
		
	}
}
