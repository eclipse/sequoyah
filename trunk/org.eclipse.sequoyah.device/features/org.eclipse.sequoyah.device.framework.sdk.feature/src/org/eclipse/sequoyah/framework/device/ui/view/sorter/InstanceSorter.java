/********************************************************************************
 * Copyright (c) 2007-2008 Motorola Inc and others.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Fabio Fantato (Motorola)
 * 
 * Contributors:
 * Otávio Luiz Ferranti (Eldorado Research Institute) - bug#221733 - Code cleanup.
 * Fabio Fantato (Eldorado Research Institute) - [244810] Migrating Device View and Instance View to a separate plugin
 * Yu-Fen Kuo (MontaVista)  - [236476] - provide a generic device type
 ********************************************************************************/
package org.eclipse.tml.framework.device.ui.view.sorter;


import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.tml.framework.device.model.IInstance;

/**
 * @author Fabio Fantato
 */
public class InstanceSorter extends ViewerSorter {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerComparator#category(java.lang.Object)
	 */
	public int category(Object element) {
		if (element instanceof IInstance) {
			return 0;
		} 
		return 1;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public int compare(Viewer viewer, Object e1, Object e2) {
		int cat1 = category(e1);
		int cat2 = category(e2);
		if (cat1 != cat2) return cat1 - cat2;
		String name1, name2;
		if (cat1==0) {
			 name1 = ((IInstance)e1).getDeviceTypeId();
			 name2 = ((IInstance)e2).getDeviceTypeId();
		} else { 
				name1 = e1.toString();
			    name2 = e2.toString();
		}
		if(name1 == null) name1 = ""; //$NON-NLS-1$
		if(name2 == null) name2 = ""; //$NON-NLS-1$
		return getComparator().compare(name1, name2);
	}
}