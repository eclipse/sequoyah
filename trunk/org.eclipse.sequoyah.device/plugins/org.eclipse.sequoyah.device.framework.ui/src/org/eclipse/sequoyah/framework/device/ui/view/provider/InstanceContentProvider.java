/********************************************************************************
 * Copyright (c) 2007 Motorola Inc.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Fabio Fantato (Motorola)
 * 
 * Contributors:
 * Fabio Fantato (Motorola) - bug#221733 - code revisited
 ********************************************************************************/
package org.eclipse.tml.framework.device.ui.view.provider;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.tml.common.utilities.IPropertyConstants;
import org.eclipse.tml.framework.device.manager.DeviceManager;
import org.eclipse.tml.framework.device.model.IDevice;
import org.eclipse.tml.framework.device.model.IInstance;
import org.eclipse.tml.framework.device.model.IInstanceRegistry;
import org.eclipse.tml.framework.status.LabelStatus;

public class InstanceContentProvider implements ITreeContentProvider {
	private static Object[] EMPTY_ARRAY = new Object[0];
	protected TreeViewer viewer;
	
	/*
	 * @see IContentProvider#dispose()
	 */
	public void dispose() {}

	/*
	 * @see IContentProvider#inputChanged(Viewer, Object, Object)
	 */
	/**
	* Notifies this content provider that the given viewer's input
	* has been switched to a different element.
	* <p>
	* A typical use for this method is registering the content provider as a listener
	* to changes on the new input (using model-specific means), and deregistering the viewer 
	* from the old input. In response to these change notifications, the content provider
	* propagates the changes to the viewer.
	* </p>
	*
	* @param viewer the viewer
	* @param oldInput the old input element, or <code>null</code> if the viewer
	*   did not previously have an input
	* @param newInput the new input element, or <code>null</code> if the viewer
	*   does not have an input
	*/
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (TreeViewer)viewer;
		if(oldInput != null) {
			removeListenerFrom((IInstanceRegistry)oldInput);
		}
		if(newInput != null) {
			addListenerTo((IInstanceRegistry)newInput);
		}		
	}
	
	/** Because the domain model does not have a richer
	 * listener model, recursively remove this listener
	 * from each child box of the given box. */
	protected void removeListenerFrom(IInstanceRegistry box) {
		//box.removeListener(this);
		//for (Iterator iterator = box.getBoxes().iterator(); iterator.hasNext();) {
			//MovingBox aBox = (MovingBox) iterator.next();
			//removeListenerFrom(aBox);
		//}
	}
	
	/** Because the domain model does not have a richer
	 * listener model, recursively add this listener
	 * to each child box of the given box. */
	protected void addListenerTo(IInstanceRegistry box) {
		//box.addListener(this);
		//for (Iterator iterator = box.getBoxes().iterator(); iterator.hasNext();) {
		//	MovingBox aBox = (MovingBox) iterator.next();
		//	addListenerTo(aBox);
		//}
	}
	
	
	/*
	 * @see ITreeContentProvider#getChildren(Object)
	 */
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof IInstanceRegistry) {
			IInstanceRegistry registry = (IInstanceRegistry)parentElement;
			return registry.getInstances().toArray();
		} else if(parentElement instanceof IInstance) {
			IInstance instance = (IInstance)parentElement;
			List child = new LinkedList();
			IDevice device = DeviceManager.getInstance().getDevice(instance);
			if (device==null) {
				//child.add(new InactiveMobileStatus());	
			} else {
				child.add(device);
				child.add(new LabelStatus(instance,instance.getStatus()));
				String properties = "Host="+instance.getProperties().getProperty(IPropertyConstants.HOST)+instance.getProperties().getProperty(IPropertyConstants.DISPLAY);
				child.add(properties);
			}				
			return child.toArray();
		} 
		return EMPTY_ARRAY;
	}
	
	/*
	 * @see ITreeContentProvider#getParent(Object)
	 */
	public Object getParent(Object element) {
		return null;
	}

	/*
	 * @see ITreeContentProvider#hasChildren(Object)
	 */
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

	/*
	 * @see IStructuredContentProvider#getElements(Object)
	 */
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	
}
