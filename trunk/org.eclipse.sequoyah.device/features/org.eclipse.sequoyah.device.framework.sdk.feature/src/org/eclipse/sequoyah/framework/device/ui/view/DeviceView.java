/********************************************************************************
 * Copyright (c) 2008 Motorola Inc and Others. All rights reserverd.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Fabio Fantato (Eldorado Research Institute)
 * [244810] Migrating Device View and Instance View to a separate plugin
 * 
 * Contributors:
 * Yu-Fen Kuo (MontaVista)  - [236476] - provide a generic device type
 ********************************************************************************/
package org.eclipse.tml.framework.device.ui.view;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.tml.framework.device.factory.DeviceTypeRegistry;
import org.eclipse.tml.framework.device.model.IDeviceTypeRegistry;
import org.eclipse.tml.framework.device.ui.view.provider.DeviceContentProvider;
import org.eclipse.tml.framework.device.ui.view.provider.DeviceLabelProvider;
import org.eclipse.ui.part.ViewPart;


/**
 * Insert the type's description here.
 * @see ViewPart
 */
public class DeviceView extends ViewPart {
	protected TreeViewer treeViewer;
	protected Text text;
	protected DeviceLabelProvider labelProvider;
	
	protected Action onlyBoardGamesAction, atLeatThreeItems;
	protected Action booksBoxesGamesAction, noArticleAction;
	protected Action addBookAction, removeAction;
	protected ViewerFilter onlyBoardGamesFilter, atLeastThreeFilter;
	protected ViewerSorter booksBoxesGamesSorter, noArticleSorter;
	
	protected IDeviceTypeRegistry root;
	
	/**
	 * The constructor.
	 */
	public DeviceView() {
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
		treeViewer.setContentProvider(new DeviceContentProvider());
		labelProvider = new DeviceLabelProvider();
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
		//createFiltersAndSorters();
		//createActions();
		//createMenus();
		//createToolbar();
		//hookListeners();
		
		treeViewer.setInput(getInitalInput());
		treeViewer.expandAll();
	}
	
	protected void createFiltersAndSorters() {
		//
	}

	protected void hookListeners() {
		//
	}
	
	protected void createActions() {
		//
	}
	
	/** Add a new book to the selected moving box.
	 * If a moving box is not selected, use the selected
	 * obect's moving box. 
	 * 
	 * If nothing is selected add to the root. */
	protected void addNewBook() {
		//
	}

	/** Remove the selected domain object(s).
	 * If multiple objects are selected remove all of them.
	 * 
	 * If nothing is selected do nothing. */
	protected void removeSelected() {
			//
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
		IMenuManager filterSubmenu = new MenuManager("Filters"); //$NON-NLS-1$
		rootMenuManager.add(filterSubmenu);
		filterSubmenu.add(onlyBoardGamesAction);
		filterSubmenu.add(atLeatThreeItems);
		
		IMenuManager sortSubmenu = new MenuManager("Sort By"); //$NON-NLS-1$
		rootMenuManager.add(sortSubmenu);
		sortSubmenu.add(booksBoxesGamesAction);
		sortSubmenu.add(noArticleAction);
	}
	
	
	
	protected void updateSorter(Action action) {
		//			
	}
	
	/* Multiple filters can be enabled at a time. */
	protected void updateFilter(Action action) {
		//
	}
	
	protected void createToolbar() {
		//
	}
	
	
	public IDeviceTypeRegistry getInitalInput() {
		return DeviceTypeRegistry.getInstance();
	}

	/*
	 * @see IWorkbenchPart#setFocus()
	 */
	public void setFocus() {}

}
