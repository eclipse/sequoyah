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

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.sequoyah.pulsar.internal.provisional.core.IInstallationEnvironment;
import org.eclipse.sequoyah.pulsar.internal.provisional.core.IInstallationInfo;
import org.eclipse.sequoyah.pulsar.internal.provisional.core.IInstallationInfoProvider;

/**
 * InstallationEnvironmentFilter extends the {@link ViewerFilter} class 
 * in order to filter {@link IInstallationInfoProvider} instances
 * in order to display only installable for the host OS.
 * 
 * @author David Marques
 */
public class InstallationEnvironmentFilter extends ViewerFilter {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		boolean result = true;
		Object object = ((TreeNode)element).getValue();
		if (object instanceof IInstallationInfoProvider) {
			IInstallationInfoProvider provider = (IInstallationInfoProvider) object;
			IInstallationInfo info = provider.getInstallationInfo();
			if (info != null) {
				IInstallationEnvironment env = info.getTargetEnvironment();
				if (env != null && !env.getTargetOS().equals(IInstallationEnvironment.ALL)) {
					result &= env.getTargetOS().equals(Platform.getOS());
				}
			}
		}
		return result;
	}
}
