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

import java.net.URI;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mtj.internal.provisional.pulsar.core.IInstallationInfo;
import org.eclipse.swt.graphics.Image;

/**
 * SDKInstallItemViewerContentProvider class implements the {@link ISDKInstallItemViewerContentProvider} interface 
 * in order to provide installation information from an {@link IInstallationInfo}
 * class instance.
 *
 * @author David Marques
 */
public class SDKInstallItemViewerContentProvider implements ISDKInstallItemViewerContentProvider {

	private IInstallationInfo info;
	
	/**
	 * Creates a SDKInstallItemViewerContentProvider instance to get
	 * information from the specified {@link IInstallationInfo}
	 * instance.
	 * 
	 * @param info {@link IInstallationInfo} instance.
	 */
	public SDKInstallItemViewerContentProvider(IInstallationInfo info) {
		if (info == null) {
			throw new IllegalArgumentException("Info can not be null.");
		}
		this.info = info;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.mtj.internal.pulsar.ui.view.ISDKInstallViewLabelProvider#getDescription()
	 */
	public String getDescription() {
		String result = null;
		StringBuffer buffer = this.info.getDescription();
		if (buffer != null) {
			result = buffer.toString();
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.mtj.internal.pulsar.ui.view.ISDKInstallViewLabelProvider#getImage()
	 */
	public Image getImage() {
		Image image = null;
		ImageDescriptor descriptor = this.info.getImageDescriptor();
		if (descriptor != null) {
			image = descriptor.createImage();
		}
		return image;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.mtj.internal.pulsar.ui.view.ISDKInstallViewLabelProvider#getSite()
	 */
	public String getSite() {
		String site = null;
		URI uri = this.info.getWebSiteURI();
		if (uri != null) {
			site = uri.toString();
		}
		return site;
	}

}
