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
 */
package org.eclipse.sequoyah.pulsar.internal.provisional.core;

import java.net.URI;
import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * An SDK repository containing installable SDKs
 * 
 */
public interface ISDKRepository extends IInstallationInfoProvider {

	/**
	 * Returns the list of categories that this SDK repository provides
	 * 
	 * @return List
	 */
	public Collection<ISDK> getSDKs(IProgressMonitor monitor);

	/**
	 * The URI for the metadata of this repository
	 * 
	 * @return URI
	 */
	public URI getMetadataURI();
	
	/**
	 * The URI for the artifacts of this repository
	 * 
	 * @return URI
	 */
	public URI getArtifactsURI();
	
	/**
	 * Returns the image descriptor for this SDK repository (provider logo)
	 * 
	 * @return ImageDescriptor
	 */
	public ImageDescriptor getIconImageDescriptor();
	
	/**
	 * Returns the name of this repository
	 * 
	 * @return String
	 */
	public String getName();

}
