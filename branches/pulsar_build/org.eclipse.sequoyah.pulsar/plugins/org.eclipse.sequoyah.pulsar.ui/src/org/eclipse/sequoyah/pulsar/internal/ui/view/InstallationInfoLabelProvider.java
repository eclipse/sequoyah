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

import java.net.URI;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.sequoyah.pulsar.internal.provisional.core.IInstallationInfo;
import org.eclipse.sequoyah.pulsar.internal.provisional.core.IInstallationInfoProvider;
import org.eclipse.swt.graphics.Image;

/**
 * InstallationInfoLabelProvider class implements {@link ISDKInstallItemLabelProvider}
 * in order to extract information from an {@link IInstallationInfoProvider} instance.
 * 
 * @author David Marques
 */
public class InstallationInfoLabelProvider implements ISDKInstallItemLabelProvider {

	/* (non-Javadoc)
	 * @see org.eclipse.sequoyah.pulsar.internal.ui.view.ISDKInstallItemLabelProvider#getDescription(java.lang.Object)
	 */
	public String getDescription(Object object) {
		String result = null;
		IInstallationInfo info = getInstallationInfo(object);
		if (info != null) {
			StringBuffer buffer = info.getDescription();
			if (buffer != null) {				
				result = buffer.toString();
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.sequoyah.pulsar.internal.ui.view.ISDKInstallItemLabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object object) {
		Image result = null;
		IInstallationInfo info = getInstallationInfo(object);
		if (info != null) {
			ImageDescriptor descriptor = info.getImageDescriptor();
			if (descriptor != null) {
				try {
                    result = descriptor.createImage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.sequoyah.pulsar.internal.ui.view.ISDKInstallItemLabelProvider#getSite(java.lang.Object)
	 */
	public String getSite(Object object) {
		String result = null;
		IInstallationInfo info = getInstallationInfo(object);
		if (info != null) {
			URI uri = info.getWebSiteURI();
			if (uri != null) {				
				result = uri.toString();
			}
		}
		return result;
	}

	/**
	 * Casts the specified {@link Object} into a {@link IInstallationInfo}
	 * object. Returns null if the specified object does not implement the
	 * {@link IInstallationInfo} interface. 
	 * 
	 * @param object target {@link Object} instance.
	 * @return the {@link IInstallationInfo} instance or null.
	 */
	private IInstallationInfo getInstallationInfo(Object object) {
		IInstallationInfo result = null;
		if (object instanceof IInstallationInfoProvider) {
			IInstallationInfoProvider provider = (IInstallationInfoProvider) object;
			result = provider.getInstallationInfo();
		}
		return result;
	}
	
}
