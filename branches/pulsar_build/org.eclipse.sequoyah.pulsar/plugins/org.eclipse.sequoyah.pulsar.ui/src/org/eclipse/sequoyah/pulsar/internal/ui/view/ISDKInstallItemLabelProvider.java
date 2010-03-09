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
package org.eclipse.sequoyah.pulsar.internal.ui.view;

import org.eclipse.swt.graphics.Image;

/**
 * ISDKInstallItemLabelProvider interface defines methods to retrieve
 * information to be displayed into an {@link SDKInstallItemViewer}.
 * 
 * @author David Marques
 */
public interface ISDKInstallItemLabelProvider {

	/**
	 * Gets the install unit description.
	 *
	 * @param object target object.
	 * @return description string.
	 */
	public String getDescription(Object object);
	
	/**
	 * Gets an {@link Image} instance
	 * for the installation unit.
	 *
	 * @param object target object.
	 * @return image instance.
	 */
	public Image getImage(Object object);

	/**
	 * Gets the url of the web site.
	 * 
	 * @param object target object.
	 * @return the url string.
	 */
	public String getSite(Object object);
	
}
