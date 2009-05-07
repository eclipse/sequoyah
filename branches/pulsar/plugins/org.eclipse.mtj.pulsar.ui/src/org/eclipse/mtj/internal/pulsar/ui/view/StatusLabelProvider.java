/**
 * Copyright (c) 2009 Nokia Corporation and/or its subsidiary(-ies).
 * All rights reserved.
 * This component and the accompanying materials are made available
 * under the terms of the License "Eclipse Public License v1.0"
 * which accompanies this distribution, and is available
 * at the URL "http://www.eclipse.org/legal/epl-v10.html".
 *
 * Contributors:
 * 	David Dubrow
 *
 */

package org.eclipse.mtj.internal.pulsar.ui.view;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.mtj.internal.provisional.pulsar.core.ISDK;
import org.eclipse.mtj.internal.provisional.pulsar.core.ISDK.EState;

public class StatusLabelProvider extends ColumnLabelProvider {

	@Override
	public String getText(Object element) {
		Object object = ((TreeNode) element).getValue();
		if (object instanceof ISDK) {
			return getDisplayString(((ISDK) object).getState());
		}
		
		return null;
	}

	private String getDisplayString(EState state) {
		switch (state) {
		case INSTALLED:
			return "Installed";
		case UNINSTALLED:
			return "Uninstalled";

		}
		return null;
	}

}
