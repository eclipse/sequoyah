package org.eclipse.tml.framework.device.ui.view;

import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.tml.framework.device.factory.InstanceRegistry;
import org.eclipse.tml.framework.device.manager.DeviceManager;
import org.eclipse.tml.framework.device.manager.InstanceManager;
import org.eclipse.tml.framework.device.model.IDevice;
import org.eclipse.tml.framework.device.model.IInstance;
import org.eclipse.tml.framework.device.model.IInstanceRegistry;
import org.eclipse.tml.framework.device.model.IService;
import org.eclipse.tml.framework.device.model.IStatus;
import org.eclipse.tml.framework.device.model.handler.ServiceHandlerAction;
import org.eclipse.tml.framework.device.ui.view.provider.InstanceContentProvider;
import org.eclipse.tml.framework.device.ui.view.provider.InstanceLabelProvider;
import org.eclipse.tml.framework.device.ui.view.sorter.InstanceSorter;
import org.eclipse.tml.framework.device.ui.view.sorter.StatusSorter;
import org.eclipse.ui.part.ViewPart;


/**
 * Insert the type's description here.
 * @see ViewPart
 */
public class InstanceView extends ViewPart {
	protected TreeViewer treeViewer;
	protected Text text;
	protected InstanceLabelProvider labelProvider;
	
	protected Action onlyBoardGamesAction, atLeatThreeItems;
	protected Action instanceSorterAction,statusSorterAction;
	protected Action addBookAction, removeAction;
	protected ViewerFilter onlyBoardGamesFilter, atLeastThreeFilter;
	protected ViewerSorter instanceSorter,statusSorter;
	
	protected IInstanceRegistry root;
	
	/**
	 * The constructor.
	 */
	public InstanceView() {
	}

	/*
	 * @see IWorkbenchPart#createPartControl(Composite)
	 */
	public void createPartControl(Composite parent) {
		/* Create a grid layout object so the text and treeviewer
		 * are layed out the way I want. */
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.verticalSpacing = 2;
		layout.marginWidth = 0;
		layout.marginHeight = 2;
		parent.setLayout(layout);
		
		/* Create a "label" to display information in. I'm
		 * using a text field instead of a lable so you can
		 * copy-paste out of it. */
		text = new Text(parent, SWT.READ_ONLY | SWT.SINGLE | SWT.BORDER);
		// layout the text field above the treeviewer
		GridData layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.horizontalAlignment = GridData.FILL;
		text.setLayoutData(layoutData);
		
		// Create the tree viewer as a child of the composite parent
		treeViewer = new TreeViewer(parent);
		treeViewer.setContentProvider(new InstanceContentProvider());
		labelProvider = new InstanceLabelProvider();
		treeViewer.setLabelProvider(labelProvider);
		
		treeViewer.setUseHashlookup(true);
		
		// layout the tree viewer below the text field
		layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.horizontalAlignment = GridData.FILL;
		layoutData.verticalAlignment = GridData.FILL;
		treeViewer.getControl().setLayoutData(layoutData);
		
		// Create menu, toolbars, filters, sorters.
		createFiltersAndSorters();
		createActions();
		createMenus();
		createToolbar();
		hookListeners();
		
		treeViewer.setInput(getInitalInput());
		treeViewer.expandAll();
	}
	
	protected void createFiltersAndSorters() {
		instanceSorter = new InstanceSorter();
		statusSorter = new StatusSorter();
	}

	protected void hookListeners() {
		    treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				// if the selection is empty clear the label
				if(event.getSelection().isEmpty()) {
					text.setText("");
					return;
				}
				if(event.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection selection = (IStructuredSelection)event.getSelection();
					StringBuffer toShow = new StringBuffer();
					for (Iterator iterator = selection.iterator(); iterator.hasNext();) {
						Object domain = iterator.next();
						if (domain instanceof IInstance) {							
							String value = labelProvider.getText(domain);
							toShow.append(value);
							toShow.append(", ");
							InstanceManager.getInstance().setInstance((IInstance)domain);
						} else if (domain instanceof IDevice) {
							domain = ((IDevice)domain).getParent();							
							InstanceManager.getInstance().setInstance((IInstance)domain);
						} else if (domain instanceof IStatus) {
							domain = ((IStatus)domain).getParent();
							InstanceManager.getInstance().setInstance((IInstance)domain);
						}
					}
					// remove the trailing comma space pair
					if(toShow.length() > 0) {
						toShow.setLength(toShow.length() - 2);
					}
					text.setText(InstanceManager.getInstance().getCurrentInstance().getName());
				}
			}
		});
	}
	
	protected void createActions() {
//		onlyBoardGamesAction = new Action("Only Board Games") {
//			public void run() {
//				updateFilter(onlyBoardGamesAction);
//			}
//		};
//		onlyBoardGamesAction.setChecked(false);
//		
//		atLeatThreeItems = new Action("Boxes With At Least Three Items") {
//			public void run() {
//				updateFilter(atLeatThreeItems);
//			}
//		};
//		atLeatThreeItems.setChecked(false);
//		
//		booksBoxesGamesAction = new Action("Books, Boxes, Games") {
//			public void run() {
//				updateSorter(booksBoxesGamesAction);
//			}
//		};
//		booksBoxesGamesAction.setChecked(false);
//		
		instanceSorterAction = new Action("Devices") {
			public void run() {
				updateSorter(instanceSorterAction);
			}
		};
		instanceSorterAction.setChecked(false);
		
		statusSorterAction = new Action("Status") {
			public void run() {
				updateSorter(statusSorterAction);
			}
		};
		statusSorterAction.setChecked(false);
		
		
//		
//		addBookAction = new Action("Add Book") {
//			public void run() {
//				addNewBook();
//			}			
//		};
//		addBookAction.setToolTipText("Add a New Book");
//		addBookAction.setImageDescriptor(DevicePlugin.getDefault().getImageDescriptor(DevicePlugin.ICON_NEW_BOOK));
//
//		removeAction = new Action("Delete") {
//			public void run() {
//				removeSelected();
//			}			
//		};
//		removeAction.setToolTipText("Delete");
//		removeAction.setImageDescriptor(DevicePlugin.getDefault().getImageDescriptor(DevicePlugin.ICON_REMOVE));		
	}
	
	/** Add a new book to the selected moving box.
	 * If a moving box is not selected, use the selected
	 * obect's moving box. 
	 * 
	 * If nothing is selected add to the root. */
	protected void addNewBook() {
//		MovingBox receivingBox;
//		if (treeViewer.getSelection().isEmpty()) {
//			receivingBox = root;
//		} else {
//			IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
//			Model selectedDomainObject = (Model) selection.getFirstElement();
//			if (!(selectedDomainObject instanceof MovingBox)) {
//				receivingBox = selectedDomainObject.getParent();
//			} else {
//				receivingBox = (MovingBox) selectedDomainObject;
//			}
//		}
//		receivingBox.add(Book.newBook());
	}

	/** Remove the selected domain object(s).
	 * If multiple objects are selected remove all of them.
	 * 
	 * If nothing is selected do nothing. */
	protected void removeSelected() {
//		if (treeViewer.getSelection().isEmpty()) {
//			return;
//		}
//		IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
//		/* Tell the tree to not redraw until we finish
//		 * removing all the selected children. */
//		treeViewer.getTree().setRedraw(false);
//		for (Iterator iterator = selection.iterator(); iterator.hasNext();) {
//			Model model = (Model) iterator.next();
//			MovingBox parent = model.getParent();
//			parent.remove(model);
//		}
//		treeViewer.getTree().setRedraw(true);
	}
	
	protected void createMenus() {
		IMenuManager rootMenuManager = getViewSite().getActionBars().getMenuManager();
		rootMenuManager.setRemoveAllWhenShown(true);
		rootMenuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager mgr) {
				fillMenu(mgr);
			}
		});
		fillMenu(rootMenuManager);
	}


	protected void fillMenu(IMenuManager rootMenuManager) {
		//IMenuManager filterSubmenu = new MenuManager("Filters");
		//rootMenuManager.add(filterSubmenu);
		//filterSubmenu.add(onlyBoardGamesAction);
		//filterSubmenu.add(atLeatThreeItems);
		
		IMenuManager sortSubmenu = new MenuManager("Sort By");
		rootMenuManager.add(sortSubmenu);
		sortSubmenu.add(instanceSorterAction);
		sortSubmenu.add(statusSorterAction);
		
		
		final Menu menu = new Menu(treeViewer.getTree()); 
		treeViewer.getTree().setMenu(menu); 
	    menu.addMenuListener(new MenuAdapter() { 
	         public void menuShown(MenuEvent e) { 
	            // Get rid of existing menu items 
	            MenuItem[] items = menu.getItems(); 
	            for (int i = 0; i < items.length; i++) { 
	               ((MenuItem) items[i]).dispose(); 
	            } 
	            fillMenuContext(menu,InstanceManager.getInstance().getCurrentInstance());	           
	         } 
	      }); 
	}

	protected void fillMenuContext(Menu menu,IInstance instance) {
        IDevice device = DeviceManager.getInstance().getDevice(instance);
		MenuItem newItem = null;
		
		newItem = new MenuItem(menu, SWT.PUSH);
	    newItem.setText("Properties");
        newItem.addListener(SWT.Selection,  new ServiceHandlerAction("properties"));
        
		newItem = new MenuItem(menu, SWT.SEPARATOR);
		
		
		for (IService service:device.getServices()){
			newItem = new MenuItem(menu, SWT.PUSH);			
	        newItem.setText(service.getName());
	        newItem.addListener(SWT.Selection,  new ServiceHandlerAction(instance,service.getHandler()));
		}
	}
	
	
	
	protected void updateSorter(Action action) {
		if(action == instanceSorterAction) {			
			if(action.isChecked()) {
				treeViewer.setSorter(instanceSorter);
				statusSorterAction.setChecked(!instanceSorterAction.isChecked());
			} else {
				treeViewer.setSorter(null);
			}
		} else if(action == statusSorterAction) {					
			if(action.isChecked()) {
				instanceSorterAction.setChecked(!statusSorterAction.isChecked());
				treeViewer.setSorter(statusSorter);
			} else {
				treeViewer.setSorter(null);
			}
		}
			
	}
	
	/* Multiple filters can be enabled at a time. */
	protected void updateFilter(Action action) {
//		if(action == atLeatThreeItems) {
//			if(action.isChecked()) {
//				treeViewer.addFilter(atLeastThreeFilter);
//			} else {
//				treeViewer.removeFilter(atLeastThreeFilter);
//			}
//		} else if(action == onlyBoardGamesAction) {
//			if(action.isChecked()) {
//				treeViewer.addFilter(onlyBoardGamesFilter);
//			} else {
//				treeViewer.removeFilter(onlyBoardGamesFilter);
//			}
//		}
	}
	
	protected void createToolbar() {
//		IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
//		toolbarManager.add(addBookAction);
//		toolbarManager.add(removeAction);
	}
	
	
	public IInstanceRegistry getInitalInput() {
		InstanceManager.getInstance().loadInstances();
		return InstanceRegistry.getInstance();
	}

	/*
	 * @see IWorkbenchPart#setFocus()
	 */
	public void setFocus() {}

}
