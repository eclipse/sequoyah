package org.eclipse.tml.framework.device.ui.view.provider;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.tml.framework.device.model.IDevice;
import org.eclipse.tml.framework.device.model.IDeviceRegistry;
import org.eclipse.tml.framework.device.model.IService;

public class DeviceContentProvider implements ITreeContentProvider {
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
			removeListenerFrom((IDeviceRegistry)oldInput);
		}
		if(newInput != null) {
			addListenerTo((IDeviceRegistry)newInput);
		}
	}
	
	/** Because the domain model does not have a richer
	 * listener model, recursively remove this listener
	 * from each child box of the given box. */
	protected void removeListenerFrom(IDeviceRegistry box) {
	//	box.removeListener(this);
		//for (Iterator iterator = box.getBoxes().iterator(); iterator.hasNext();) {
			//MovingBox aBox = (MovingBox) iterator.next();
			//removeListenerFrom(aBox);
		//}
	}
	
	/** Because the domain model does not have a richer
	 * listener model, recursively add this listener
	 * to each child box of the given box. */
	protected void addListenerTo(IDeviceRegistry box) {
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
		if(parentElement instanceof IDeviceRegistry) {
			IDeviceRegistry registry = (IDeviceRegistry)parentElement;
			return registry.getDevices().toArray();
		} else if(parentElement instanceof IDevice) {
			IDevice device = (IDevice)parentElement;
			return device.getServices().toArray();
		}
		return EMPTY_ARRAY;
	}
	
	/*
	 * @see ITreeContentProvider#getParent(Object)
	 */
	public Object getParent(Object element) {
		if(element instanceof IService) {
			return ((IService)element).getParent();
		}
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
