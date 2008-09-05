/********************************************************************************
 * Copyright (c) 2008 Motorola Inc. and Other. All rights reserved
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Julia Martinez Perdigueiro (Eldorado Research Institute) 
 * [244805] - Improvements on Instance view  
 *
 * Contributors:
 ********************************************************************************/

package org.eclipse.tml.framework.device.ui.view;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.tml.framework.device.DevicePlugin;
import org.eclipse.tml.framework.device.events.InstanceAdapter;
import org.eclipse.tml.framework.device.events.InstanceEvent;
import org.eclipse.tml.framework.device.events.InstanceEventManager;
import org.eclipse.tml.framework.device.manager.DeviceManager;
import org.eclipse.tml.framework.device.manager.InstanceManager;
import org.eclipse.tml.framework.device.model.IDevice;
import org.eclipse.tml.framework.device.model.IInstance;
import org.eclipse.tml.framework.device.model.IService;
import org.eclipse.tml.framework.device.model.handler.ServiceHandlerAction;
import org.eclipse.tml.framework.device.ui.view.model.InstanceMgtViewComparator;
import org.eclipse.tml.framework.device.ui.view.model.ViewerAbstractNode;
import org.eclipse.tml.framework.device.ui.view.model.ViewerDeviceNode;
import org.eclipse.tml.framework.device.ui.view.model.ViewerInstanceNode;
import org.eclipse.tml.framework.device.ui.view.provider.InstanceMgtViewContentProvider;
import org.eclipse.tml.framework.device.ui.view.provider.InstanceMgtViewLabelProvider;
import org.eclipse.tml.framework.status.IStatus;
import org.eclipse.tml.framework.status.StatusRegistry;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;

public class InstanceStatusComposite extends Composite
{
	class InstanceSelectionChangeEvent
	{
		private final IInstance instance;

		InstanceSelectionChangeEvent()
		{
			this(null);
		}

		InstanceSelectionChangeEvent(IInstance instance)
		{
			this.instance = instance;
		}

		IInstance getInstance()
		{
			return instance;
		}	
	}

	interface InstanceSelectionChangeListener
	{
		void instanceSelectionChanged(InstanceSelectionChangeEvent event);
	}

	private static final String PROPERTY_EDITOR_ID = "org.eclipse.tml.framework.device.ui.editors.InstancePropertyEditorDefault";
	private static final String MENU_DELETE = "Delete";
	private static final String MENU_PROPERTIES = "Properties";
	// TODO for future use
//	private static final String MENU_NEW = "New..."; 

	private final Set<InstanceSelectionChangeListener> listeners = new LinkedHashSet<InstanceSelectionChangeListener>();

	/**
	 * The main viewer of the instance view. 
	 * It is responsible to construct the tree with columns for correct instance visualization 
	 */
	private TreeViewer viewer;

	private IViewSite viewSite;

	public InstanceStatusComposite(Composite parent, IViewSite viewSite)
	{
		super(parent, SWT.NONE);
		this.viewSite = viewSite;
		createContents();

		InstanceEventManager eventMgr = InstanceEventManager.getInstance();
		eventMgr.addInstanceListener(new InstanceAdapter()
		{
		    public void instanceLoaded(InstanceEvent e)
		    {
		        InstanceStatusComposite.this.instanceLoaded(e.getInstance());
		    }

		    public void instanceUnloaded(InstanceEvent e)
		    {
		        InstanceStatusComposite.this.instanceUnloaded(e.getInstance());
		    }

		    public void instanceUpdated(InstanceEvent e)
		    {
		        InstanceStatusComposite.this.instanceUpdated(e.getInstance());
		    }
		});
	}
	
	protected void addInstanceSelectionChangeListener(InstanceSelectionChangeListener listener)
	{
		listeners.add(listener);
	}
	
	protected void removeInstanceSelectionChangeListener(InstanceSelectionChangeListener listener)
	{
		listeners.remove(listener);
	}
	
	private void notifyInstanceSelectionChangeListeners(IInstance instance)
	{
	    InstanceSelectionChangeEvent event = new InstanceSelectionChangeEvent(instance);
		for (InstanceSelectionChangeListener listener : listeners)
		{
			listener.instanceSelectionChanged(event);
		}
	}

	private IInstance getSelectedInstance()
	{
		IInstance instance = null;
		Object lastSelection = getLastSelection();
		
		if (lastSelection instanceof IInstance)
		{
		    instance = (IInstance) lastSelection;
		}
		
		return instance;
	}

	private void createContents()
	{
		setLayout(new FillLayout());
		viewer = new TreeViewer(this, SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
		Tree tree = viewer.getTree();
		TableLayout layout = new TableLayout();
		tree.setLayout(layout);
		tree.setHeaderVisible(true);

		createColumn("Instance name", 3);
		createColumn("Status", 1);

		InstanceMgtViewLabelProvider labelProvider = new InstanceMgtViewLabelProvider();
		viewer.setLabelProvider(labelProvider);

		viewer.setContentProvider(new InstanceMgtViewContentProvider());
		viewer.getTree().setLinesVisible(true);
		// Use the custom comparator in emulator views
		viewer.setComparator(new InstanceMgtViewComparator(labelProvider));
		viewer.setInput(viewSite);
		viewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent event) {
				notifyInstanceSelectionChangeListeners(getSelectedInstance());
			}	
		});

		fillContextMenu();
		refreshViewer(null);
		viewer.expandAll();
	}

	private void createColumn(String columnLabel, int columnWeight)
	{
		Tree tree = viewer.getTree();
		TableLayout layout = (TableLayout) tree.getLayout();
		layout.addColumnData(new ColumnWeightData(columnWeight));
		TreeColumn tc = new TreeColumn(tree, SWT.NONE);
		tc.setMoveable(true);
		tc.setText(columnLabel);
		tc.setResizable(true);

		tc.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				columnSelected(e);
			}
		});
		tc.addControlListener(new ControlAdapter()
		{
			/**
			 * @see org.eclipse.swt.events.ControlListener#controlMoved(ControlEvent)
			 */
			 public void controlMoved(ControlEvent e)
			 {
				 columnMoved(e);
			 }
		});
	}

	private void refreshViewer(IInstance selectedInstance)
	{
	    Collection<String> expandedDevices = getExpandedDevices();
		viewer.refresh();
		expandToNodeValues(expandedDevices, selectedInstance);		

        notifyInstanceSelectionChangeListeners(getSelectedInstance());
	}
	
	private Collection<String> getExpandedDevices()
	{
	    Object[] expandedElements = viewer.getVisibleExpandedElements();

	    Collection<String> devicesCol = new HashSet<String>();

	    for (Object element : expandedElements)
	    {
	        if (element instanceof ViewerDeviceNode)
	        {
	            ViewerDeviceNode node = (ViewerDeviceNode) element;
	            devicesCol.add(node.getDevice().getId());
	        }
	    }
	    
	    return devicesCol;
	}
	
	private void expandToNodeValues(Collection<String> devices, IInstance instance)
	{
        for (TreeItem treeNode : viewer.getTree().getItems())
        {
            Object node = treeNode.getData();
            if (node instanceof ViewerDeviceNode)
            {
                ViewerDeviceNode deviceNode = (ViewerDeviceNode) node;
                if (devices.contains(deviceNode.getDevice().getId()))
                {
                    treeNode.setExpanded(true);
                    viewer.reveal(treeNode);
                    for (ViewerAbstractNode childNode : deviceNode.getChildren())
                    {
                        viewer.reveal(childNode);
                        
                        if ((instance != null) && (instance.equals(((ViewerInstanceNode)childNode).getInstance())))
                        {
                            treeNode.setExpanded(true);
                            viewer.reveal(treeNode);
                            viewer.setSelection(new StructuredSelection(childNode));
                        }
                    }
                }
            }
        }
	}
	
	
	
	private Object getLastSelection()
	{
	    Object lastSelection = null;
	    ISelection selection = viewer.getSelection();
        if (selection instanceof IStructuredSelection)
        {
            IStructuredSelection strSelection = (IStructuredSelection) selection;

            if (strSelection.size() == 1)
            {
                Object firstSelection = strSelection.getFirstElement();

                if (firstSelection instanceof ViewerInstanceNode)
                {
                    ViewerInstanceNode node = (ViewerInstanceNode) firstSelection;
                    lastSelection = node.getInstance();
                }
                else if (firstSelection instanceof ViewerDeviceNode)
                {
                    ViewerDeviceNode node = (ViewerDeviceNode) firstSelection;
                    lastSelection = node.getDevice();
                }
            }
        }
        
        return lastSelection;
	}

	private void columnSelected(SelectionEvent e)
	{
		// When a column is selected by the user, it is used as basis for sorting
		TreeColumn selectedColumn = (TreeColumn) e.widget;
		Tree tree = selectedColumn.getParent();
		int columnIndex = tree.indexOf(selectedColumn);
		InstanceMgtViewComparator comparator = (InstanceMgtViewComparator) viewer.getComparator();
		comparator.setColumnToSort(columnIndex);
		
		if (tree.getSortColumn() == selectedColumn)
		{
			comparator.toggleAscending();
		}

		tree.setSortColumn(selectedColumn);
		if (comparator.isAscending())
		{
			tree.setSortDirection(SWT.UP);
		}
		else
		{
			tree.setSortDirection(SWT.DOWN);
		}
		refreshViewer(getSelectedInstance());
	}

	private void columnMoved(ControlEvent e)
	{
		TreeColumn treeColumn = (TreeColumn) e.getSource();
		Tree tree = treeColumn.getParent();
		int[] order = tree.getColumnOrder();
		InstanceMgtViewLabelProvider provider =
			(InstanceMgtViewLabelProvider) viewer.getLabelProvider();
		int previousFirstColumnIndex = provider.getFirstColumnIndex();

		if (order[0] != previousFirstColumnIndex)
		{
			// This procedure is made only if the movement causes a column to change its
			// position in comparison with other columns. Otherwise, a performance lack
			// is detected.
			provider.setFirstColumnIndex(order[0]);
			refreshViewer(getSelectedInstance());
		}
	}

	private void fillContextMenu()
	{
		final Menu menu = new Menu(viewer.getTree()); 
		viewer.getTree().setMenu(menu); 
		menu.addMenuListener(new MenuAdapter() { 
			public void menuShown(MenuEvent e) { 
				// Get rid of existing menu items 
				MenuItem[] items = menu.getItems(); 
				for (int i = 0; i < items.length; i++) { 
					((MenuItem) items[i]).dispose(); 
				}
				fillMenuContext(menu);              

			}
		}); 
	}

	private void fillMenuContext(Menu menu)
	{
		MenuItem newItem = null;

		ISelection selection = viewer.getSelection();
		if (selection instanceof IStructuredSelection)
		{
			IStructuredSelection strSelection = (IStructuredSelection) selection;
			Object firstSelection = strSelection.getFirstElement(); // TODO support multiple selection

			if (firstSelection instanceof ViewerInstanceNode)
			{
			    ViewerInstanceNode node = (ViewerInstanceNode) firstSelection;
			    if (node.containsInstance())
			    {
			        // menu item "New..."
//			        newItem = new MenuItem(menu, SWT.PUSH);
//			        newItem.setText(MENU_NEW);
			        // TODO create action for new wizards for the device of the selected instance

//			        newItem = new MenuItem(menu, SWT.SEPARATOR);

                    IInstance instance = getSelectedInstance();
                    String statusId = instance.getStatus();
                    IStatus status = StatusRegistry.getInstance().getStatus(statusId);
                    
			        // menu item "Delete"
			        newItem = new MenuItem(menu, SWT.PUSH);
			        newItem.setText(MENU_DELETE);
			        newItem.addListener(SWT.Selection, new MenuDeleteListener());
			        newItem.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_TOOL_DELETE));
			        newItem.setEnabled(status.canDeleteInstance());

			        newItem = new MenuItem(menu, SWT.SEPARATOR);

			        // menu item "Properties"
			        newItem = new MenuItem(menu, SWT.PUSH);
			        newItem.setText(MENU_PROPERTIES);
			        newItem.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(DevicePlugin.ICON_PROPERTY));
			        newItem.addListener(SWT.Selection, new MenuPropertiesListener(instance));
			       
			        newItem = new MenuItem(menu, SWT.SEPARATOR);
			        
			        IDevice device = DeviceManager.getInstance().getDevice(instance);
			        for (IService service:device.getServices()){
			            if (service.isVisible()) {
			                newItem = new MenuItem(menu, SWT.PUSH);     
			                newItem.setImage(service.getImage().createImage());
			                newItem.setEnabled((service.getStatusTransitions(instance.getStatus())!=null));
			                newItem.setText(service.getName());
			                newItem.addListener(SWT.Selection,  new ServiceHandlerAction(instance,service.getHandler()));
			            }
			        }
			    }
			}
			else if (firstSelection instanceof ViewerDeviceNode)
			{
//				// menu item "New..."
//				newItem = new MenuItem(menu, SWT.PUSH);
//				newItem.setText(MENU_NEW);
				// TODO create action for new wizards for the selected device type

			}
		}  
	}

	/** Remove the selected domain object(s).
	 * If multiple objects are selected remove all of them.
	 * 
	 * If nothing is selected do nothing.
	 */
	protected void removeSelected() {
		if (viewer.getSelection().isEmpty()) {
			return;
		}
		
		IInstance instance = getSelectedInstance();

		if (instance != null)
		{
		    InstanceManager.getInstance().deleteInstance(instance);
		}
	}

	/*
	 * Menu handler
	 */
	private class MenuDeleteListener implements Listener {
		public void handleEvent(Event event) {
			removeSelected();
		}
	}

	/*
	 * Menu handler
	 */
	private class MenuPropertiesListener implements Listener {
	    private IInstance instance;
	    
	    public MenuPropertiesListener(IInstance instance)
	    {
	        super();
	        this.instance = instance;
	    }
	    
		public void handleEvent(Event event) {

			Shell shell = new Shell();
			PreferenceDialog dialog = PreferencesUtil.createPropertyDialogOn(
					shell,
					instance,
					PROPERTY_EDITOR_ID,
					new String[] {},
					null);
			dialog.open();
		}
	}
	
	private void instanceLoaded(final IInstance instance)
	{
	    Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                Collection<String> expandedDevices = getExpandedDevices();
                
                viewer.setInput(viewSite);
                viewer.refresh();
                expandedDevices.add(instance.getDevice());
                expandToNodeValues(expandedDevices, instance);
                
                notifyInstanceSelectionChangeListeners(instance);
            }});
	}
	
	private void instanceUnloaded(IInstance instance)
	{

        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                Collection<String> expandedDevices = getExpandedDevices();
                viewer.setInput(viewSite);
                viewer.refresh();
                expandToNodeValues(expandedDevices, null);
            }});
	}
	
	private void instanceUpdated(final IInstance instance)
	{
	    Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                Collection<String> expandedDevices = getExpandedDevices();
                IInstance selectedInstance = getSelectedInstance();
                viewer.setInput(viewSite);
                viewer.refresh();
                expandToNodeValues(expandedDevices, selectedInstance);
                
                if (instance.equals(selectedInstance))
                {
                    notifyInstanceSelectionChangeListeners(selectedInstance);
                }
            }});
	}
}
