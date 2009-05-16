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
package org.eclipse.mtj.internal.pulsar.metadata.generator.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

/**
 * EnvironmentDialog class extends a {@link Dialog} in order
 * to create an environment selection for the installable units.
 * 
 * @author David Marques
 */
public class EnvironmentDialog extends Dialog {

	private CheckboxTableViewer viewer;
	private String[] options;
	private String selections;

	/**
	 * Creates an EnvironmentDialog instance with
	 * the specified parent {@link Shell}.
	 * 
	 * @param parent parent shell.
	 */
	public EnvironmentDialog(Shell parent) {
		super(parent);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	protected void configureShell(Shell newShell) {
		newShell.setText("Environment Options");
		super.configureShell(newShell);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		GridData  gridData  = null;
		
		ScrolledComposite scroll = new ScrolledComposite(composite, SWT.V_SCROLL);
		scroll.setExpandHorizontal(true);
		scroll.setExpandVertical(true);
		scroll.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Composite contents = new Composite(scroll, SWT.NONE);
		contents.setLayout(new GridLayout(0x02, false));
		contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label label = new Label(contents, SWT.NONE);
		label.setText("Valid Values:");
		gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData.horizontalSpan = 2;
		label.setLayoutData(gridData);
		
		Table table = new Table(contents, SWT.BORDER | SWT.CHECK | SWT.MULTI);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.verticalSpan = 3;
		table.setLayoutData(gridData);
		
		viewer = new CheckboxTableViewer(table);
		viewer.setContentProvider(new StringArrayContentProvider());
		viewer.setLabelProvider(new StringLabelProvider());
		viewer.setInput(options);
		
		Button selectAll = new Button(contents, SWT.NONE);
		selectAll.setText("Select All");
		selectAll.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		selectAll.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
				viewer.setAllChecked(true);
			}
		
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		
		Button deselectAll = new Button(contents, SWT.NONE);
		deselectAll.setText("Deselect All");
		deselectAll.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		deselectAll.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
				viewer.setAllChecked(false);
			}
		
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		
		Composite spacer = new Composite(contents, SWT.NONE);
		spacer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		spacer.setVisible(false);
		
		scroll.setContent(contents);
		scroll.setMinSize(contents.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		return composite;
	}

	/**
	 * Sets the options to display.
	 * 
	 * @param options target options.
	 */
	public void setOptions(String[] options) {
		this.options = options;
	}
	
	/**
	 * Gets the selected options.
	 * 
	 * @return comma separated list of
	 * selected options.
	 */
	public String getSelectedOptions() {
		return this.selections;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed() {
		StringBuffer result = new StringBuffer();
		Object[] selected = this.viewer.getCheckedElements();
		for (Object option : selected) {
			if (option instanceof String) {
				if (result.length() > 0) {
					result.append(",").append(option);
				} else {
					result.append(option);
				}
			}
		}
		this.selections = result.toString();
		super.okPressed();
	}
	
	private class StringArrayContentProvider implements IStructuredContentProvider {
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof String[]) {
				return (Object[]) inputElement;
			}
			return null;
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			viewer.refresh();
		}
		
		public void dispose() {}
	}
	
	private class StringLabelProvider extends LabelProvider {
		public String getText(Object element) {
			if (element instanceof String) {
				return (String) element;
			}
			return null;
		}
	}
}
