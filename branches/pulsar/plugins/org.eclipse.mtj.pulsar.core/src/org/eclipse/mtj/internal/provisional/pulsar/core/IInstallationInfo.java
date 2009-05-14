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

import java.net.URI;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * IInstallationInfo interface defines methods to be
 * implemented by data sources that contain installation
 * information.
 * 
 * @author David Marques
 */
public interface IInstallationInfo {

	/**
	 * Gets an {@link ImageDescriptor} for the installation
	 * unit in order to be displayed on the UI side.
	 * 
	 * @return an {@link ImageDescriptor} instance.
	 */
	public ImageDescriptor getImageDescriptor();

	/**
	 * Gets the installation unit description.
	 * 
	 * @return string description.
	 */
	public StringBuffer getDescription();
	
	/**
	 * Gets the installation unit reference website.
	 * Can be documentation website, provider website,
	 * etc.
	 * 
	 * @return an the web site {@link URI}.
	 */
	public URI getWebSiteURI();
	
}
