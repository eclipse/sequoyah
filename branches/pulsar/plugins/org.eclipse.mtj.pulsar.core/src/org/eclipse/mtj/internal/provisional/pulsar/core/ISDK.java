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
 *     David Dubrow (Nokia)
 *     David Marques (Motorola) - Extending IInstallationInfoProvider.
 *     David Marques (Motorola) - Adding getInstallableUnit and getRepository
 *                                methods.
 */
package org.eclipse.mtj.internal.provisional.pulsar.core;

import java.net.URL;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.equinox.internal.provisional.p2.metadata.IInstallableUnit;
import org.osgi.framework.Version;

/**
 * Represents one SDK that will be presented in the QuickInstall view
 * 
 */
public interface ISDK extends IAdaptable, IInstallationInfoProvider {
	
	/**
	 * States of an SDK
	 */
	enum EState {
		INSTALLED,
		UNINSTALLED
		// possible extend with update state, etc.
	}
	
	/**
	 * Types of SDK installers
	 */
	enum EType {
		ZIP_ARCHIVE,
		EXECUTABLE, 
		UNKNOWN
	}
	
	/**
	 * Return the SDK Name
	 * 
	 * @return SDK Name
	 */
	public String getName();

	/**
	 * Return the SDK Version
	 * 
	 * @return SDK Version
	 */
	public Version getVersion();
	
	/**
	 * Return the type of SDK installer
	 * 
	 * @return EType
	 */
	public EType getType();
	
	/**
	 * Return an optional category name for this SDK (or null).
	 * 
	 * @return String
	 */
	public String getCategory();
	
	/**
	 * Return an optional URL to documentation about this SDK (or null).
	 * 
	 * @return URL
	 */
	public URL getDocumentationURL();

	/**
	 * Return the state of this SDK (e.g., installed, uninstalled, etc.)
	 * 
	 * @return EState
	 */
	public EState getState();
	
	/**
	 * Gets the {@link IInstallableUnit} instance for this
	 * {@link ISDK} instance.
	 * 
	 * @return an {@link IInstallableUnit} instance.
	 */
	public IInstallableUnit getInstallableUnit();
	
	/**
	 * Gets the {@link ISDKRepository} instance that contains
	 * this {@link ISDK} instance.
	 * 
	 * @return an {@link ISDKRepository} instance.
	 */
	public ISDKRepository getRepository();
	
}
