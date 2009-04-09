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

import org.eclipse.equinox.internal.provisional.p2.metadata.IInstallableUnit;
import org.osgi.framework.Version;

/**
 * Represents one SDK that will be presented on the QuickInstall view
 * 
 * @since 1.0
 */
public interface ISDK {

	/**
	 * Returns the SDK Name
	 * 
	 * @return SDK Name
	 */
	public String getName ();
	
	/**
	 * Returns the SDK Version
	 * 
	 * @return SDK Version
	 */
	public Version getVersion ();
	
	
	/**
	 * Returns the SDK P2 installable unit
	 * 
	 * @return SDK installable unit 
	 */
	public IInstallableUnit getIU ();
}
