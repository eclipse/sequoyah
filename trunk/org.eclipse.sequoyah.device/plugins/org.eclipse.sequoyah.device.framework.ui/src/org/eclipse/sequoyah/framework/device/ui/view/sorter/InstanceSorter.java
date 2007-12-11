package org.eclipse.tml.framework.device.ui.view.sorter;

import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.tml.framework.device.model.IInstance;

public class InstanceSorter extends ViewerSorter {

	public int category(Object element) {
		if (element instanceof IInstance) {
			return 0;
		} 
		return 1;
	}

	public int compare(Viewer viewer, Object e1, Object e2) {
		int cat1 = category(e1);
		int cat2 = category(e2);
		if (cat1 != cat2) return cat1 - cat2;
		String name1, name2;
		if (cat1==0) {
			 name1 = ((IInstance)e1).getDevice();
			 name2 = ((IInstance)e2).getDevice();
		} else { 
				name1 = e1.toString();
			    name2 = e2.toString();
		}
		if(name1 == null) name1 = "";
		if(name2 == null) name2 = "";
		return collator.compare(name1, name2);
	}
	
	
}