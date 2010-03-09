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

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.sequoyah.pulsar.internal.provisional.core.ISDK;
import org.osgi.framework.Version;

/**
 * VersionLabelProvider class extends the {@link ColumnLabelProvider}
 * class in order to provide {@link Version} strings to a view that
 * supports columns.
 * 
 * @author David Marques
 */
public class VersionLabelProvider extends ColumnLabelProvider {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		String result = null;
		if (element instanceof TreeNode) {
			Object object = ((TreeNode) element).getValue();
			if (object instanceof ISDK) {
				result = ((ISDK) object).getVersion().toString();
			}
		}
		return result;
	}
	
}
