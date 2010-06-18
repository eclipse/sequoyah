/********************************************************************************
 * Copyright (c) 2010 Motorola Inc.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributor:
 * Daniel Franco (Eldorado) - [308089] SDK Uninstall Dialog
 * Daniel Pastore (Eldorado) - [308089] SDK Uninstall Dialog
 *
 * Contributors:
 ********************************************************************************/

package org.eclipse.sequoyah.pulsar.internal.ui;


import java.util.HashSet;
import java.util.Set;

import org.eclipse.equinox.internal.p2.discovery.model.CatalogItem;
import org.eclipse.equinox.internal.p2.ui.discovery.wizards.DiscoveryResources;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

@SuppressWarnings("restriction")
public class UninstallDialog extends TitleAreaDialog {

	private Object[] catalog;
	private Table table;
	private Set<CatalogItem> selectedItems;
	private Button okButton;
	private Button cancelButton;
	
	public UninstallDialog(Shell parent, Object[] installedItems) {
		super(parent);

		this.catalog = installedItems;
	}
	
	public Set<CatalogItem> getSelected() {
		return selectedItems;
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		
		newShell.setSize(400, 300);
	}
	
	protected Control createDialogArea(Composite parent) {
		parent.getShell().setText(Messages.UninstallDialog_2);
		selectedItems = new HashSet<CatalogItem>();
		
		this.setMessage(Messages.UninstallDialog_3);
		this.setTitle(Messages.UninstallDialog_4);
		this.setHelpAvailable(false);
		
		ScrolledComposite scroll = new ScrolledComposite(parent, SWT.V_SCROLL);
		scroll.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    scroll.setLayout(new FillLayout());
				
		table = new Table (scroll, SWT.BORDER | SWT.CHECK | SWT.MULTI | SWT.RIGHT);

		for (Object o: catalog) {
			CatalogItem citem = (CatalogItem) o;
			TableItem titem = new TableItem (table, SWT.NONE);
			titem.setText (citem.getName());
			titem.setData(citem);
			
			DiscoveryResources resources = new DiscoveryResources(parent.getDisplay());

			if (citem.getIcon() != null) {
				Image img = resources.getIconImage(citem.getSource(), citem.getIcon(), 32, true);
				titem.setImage(img);
			}
			
		}

		table.layout(true);
		table.pack(true);
		
		scroll.setContent(table);
		scroll.setExpandHorizontal(true);
		 
		table.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent e) {
 				if (!(e.detail == SWT.CHECK)) {
 					boolean isChecked;
 					table.deselect(table.indexOf(((TableItem)e.item)));
 					isChecked = ((TableItem)e.item).getChecked();
 					((TableItem)e.item).setChecked(!isChecked);
 				}

	        	if (selectedItems.contains(((TableItem)e.item).getData())) {
	        		selectedItems.remove((CatalogItem)((TableItem)e.item).getData());
	        	}
	        	else {
	        		selectedItems.add((CatalogItem)((TableItem)e.item).getData());
	        	}
	        	validate();	        	
			}
		});
		
		parent.setSize(400, 300);
		scroll.layout();

		return scroll;
	}

	
	private void validate(){
		if (selectedItems.isEmpty()) {
			okButton.setEnabled(false);
		}
		else {
			okButton.setEnabled(true);
		}
	}
	
	protected void createButtonsForButtonBar(Composite parent) {
		okButton = createButton(parent, OK, Messages.UninstallDialog_5, true);
		okButton.setEnabled(false);

		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setReturnCode(OK);
				close();
			}
		});

		cancelButton = createButton(parent, CANCEL, Messages.UninstallDialog_6, false);
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setReturnCode(CANCEL);
				close();
			}
		});
	}
}
