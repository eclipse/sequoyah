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
package org.eclipse.mtj.internal.pulsar.ui.view;

import org.eclipse.swt.graphics.Image;

/**
 * ISDKInstallItemViewerContentProvider interface provides content to be 
 * displayed on a {@link SDKInstallItemViewer} instance.  
 * 
 * @author David Marques
 */
public interface ISDKInstallItemViewerContentProvider {
	
	/**
	 * Gets the install unit description.
	 * 
	 * @return description string.
	 */
	public String getDescription();
	
	/**
	 * Gets the url of the web site.
	 * 
	 * @return the url string.
	 */
	public String getSite();
	
	/**
	 * Gets an {@link Image} instance
	 * for the installation unit.
	 * 
	 * @return image instance.
	 */
	public Image getImage();
	
}
