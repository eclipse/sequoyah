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

package org.eclipse.mtj.internal.pulsar.metadata.generator.ui;

import java.util.Collection;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.mtj.internal.pulsar.metadata.generator.engine.GeneratorEngine;
import org.eclipse.mtj.internal.pulsar.metadata.generator.engine.IIUDescription;
import org.eclipse.mtj.internal.pulsar.metadata.generator.engine.IRepositoryDescription;
import org.eclipse.mtj.internal.pulsar.metadata.generator.engine.RepositoryDescription;
import org.eclipse.mtj.pulsar.core.Activator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class MetadataGeneratorDialog extends TitleAreaDialog {

	private Text nameText;
	private Text locationText;
	private Button browseButton;
	private ListViewer viewer;
	private Button addButton;
	private Button removeButton;
	private Button saveButton;
	private IRepositoryDescription repository;
	private static IPath lastUsedPath;

	public MetadataGeneratorDialog(Shell parentShell) {
		super(parentShell);
		repository = new RepositoryDescription();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		
		setTitle("Create new SDK repository");
		setMessage("Choose a name and location for the repository and ensure the SDK installer artifacts (executables or zip archives) exist relative to that location");

		Composite contents = new Composite(composite, SWT.NONE);
		GridLayout layout = new GridLayout();
		contents.setLayout(layout);
		contents.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label nameLabel = new Label(contents, SWT.NONE);
		GridDataFactory.defaultsFor(nameLabel).applyTo(nameLabel);
		nameLabel.setText("Repository name");
		
		nameText = new Text(contents, SWT.BORDER);
		GridDataFactory.defaultsFor(nameText).grab(true, false).applyTo(nameText);
		nameText.setText("New Repository");
		nameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (nameText.getText().length() == 0)
					setErrorMessage("The repository must have a name");
				else
					setErrorMessage(null);
			}
		});
		
		Label locLabel = new Label(contents, SWT.NONE);
		GridDataFactory.defaultsFor(locLabel).applyTo(locLabel);
		locLabel.setText("Repository location");
		
		Composite browseComposite = new Composite(contents, SWT.NONE);
		GridLayout layoutBrowse = new GridLayout(2, false);
		browseComposite.setLayout(layoutBrowse);
		GridData gdBrowse = new GridData(GridData.FILL_HORIZONTAL);
		browseComposite.setLayoutData(gdBrowse);
		
		locationText = new Text(browseComposite, SWT.BORDER | SWT.READ_ONLY);
		locationText.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, true, false));
		
		browseButton = new Button(browseComposite, SWT.NONE);
		browseButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		browseButton.setText("Browse...");
		browseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				getRepositoryLocation();
				setAddButtonEnabledState();
				setSaveButtonEnabledState();
			}
		});
		
		Composite viewerComposite = new Composite(contents, SWT.NONE);
		GridLayout layoutViewerComp = new GridLayout(2, false);
		viewerComposite.setLayout(layoutViewerComp);
		GridData gdViewerComp = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL);
		viewerComposite.setLayoutData(gdViewerComp);

		viewer = new ListViewer(viewerComposite, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof IIUDescription) {
					IIUDescription iud = (IIUDescription) element;
					StringBuilder sb = new StringBuilder();
					sb.append(iud.getUnitName());
					sb.append(" [");
					sb.append(iud.getUnitId());
					sb.append(":");
					sb.append(iud.getUnitVersion().toString());
					sb.append("]");
					return sb.toString();
				}
				return null;
			}
		});
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				setRemoveButtonEnabledState();
			}
		});
		
		List list = viewer.getList();
		GridDataFactory.defaultsFor(list).grab(true, true).applyTo(list);
		
		Composite buttonsComposite = new Composite(viewerComposite, SWT.NONE);
		buttonsComposite.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));
		buttonsComposite.setLayout(new GridLayout());

		addButton = new Button(buttonsComposite, SWT.NONE);
		addButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		addButton.setText("Add...");
		addButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				showAddNewUnitDialog();
				setSaveButtonEnabledState();
			}
		});
		setAddButtonEnabledState();
		
		removeButton = new Button(buttonsComposite, SWT.NONE);
		removeButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		removeButton.setText("Remove");
		removeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				removeSelectedUnit();
				setSaveButtonEnabledState();
			}
		});
		setRemoveButtonEnabledState();
		
		viewer.setInput(getCurrentUnits());

		browseButton.setFocus();
		
		return contents;
	}

	private void setRemoveButtonEnabledState() {
		removeButton.setEnabled(!viewer.getSelection().isEmpty());
	}

	private void setAddButtonEnabledState() {
		addButton.setEnabled(repository.getRepoLocation() != null);
	}

	private void removeSelectedUnit() {
		IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
		if (!selection.isEmpty()) {
			IIUDescription iud = (IIUDescription) selection.getFirstElement();
			repository.getUnitCollection().remove(iud);
			viewer.setInput(getCurrentUnits());
		}
	}

	private Collection<IIUDescription> getCurrentUnits() {
		return repository.getUnitCollection();
	}

	private void showAddNewUnitDialog() {
		NewUnitDialog dialog = new NewUnitDialog(getShell(), repository);
		int result = dialog.open();
		if (result != Dialog.CANCEL) {
			IIUDescription unit = dialog.getUnit();
			if (unit != null) {
				repository.addIUDescription(unit);
				viewer.setInput(getCurrentUnits());
			}
		}
	}

	private void getRepositoryLocation() {
		if (isDirtyRepository()) {
			boolean result = MessageDialog.openQuestion(getShell(), "Unsaved repository", 
					"Are you sure you want to start a new repository without saving the current one?");
			if (!result)
				return;
		}
		
		DirectoryDialog directoryDialog = new DirectoryDialog(getShell());
		directoryDialog.setMessage("Please choose the location for the new repository");
		if (lastUsedPath != null)
			directoryDialog.setFilterPath(lastUsedPath.toOSString());
		String dirPathStr = directoryDialog.open();
		if (dirPathStr != null) {
			locationText.setText(dirPathStr);
			IPath path = new Path(dirPathStr);
			if (!path.equals(repository.getRepoLocation())) {
				repository.removeAllIUDescriptions();
				viewer.setInput(getCurrentUnits());
			}
			repository.setRepoLocation(path);
			lastUsedPath = path;
		}
	}

	private boolean isDirtyRepository() {
		return repository.getRepoLocation() != null &&
			!repository.getUnitCollection().isEmpty() &&
			nameText.getText().length() > 0;
	}

	protected boolean isResizable() {
		return true;
	}
	
	protected Point getInitialSize() {
		return new Point(500, 350);
	}

	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("New Repository");
	}

	protected void createButtonsForButtonBar(Composite parent) {
		saveButton = createButton(parent, IDialogConstants.OK_ID, "Save", true);
		setSaveButtonEnabledState();
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	private void setSaveButtonEnabledState() {
		saveButton.setEnabled(isDirtyRepository());
	}
	
	@Override
	protected void okPressed() {
		if (!isDirtyRepository())
			return;
		
		try {
			String name = nameText.getText();
			repository.setMetadataRepoName(name);
			repository.setArtifactRepoName(name);
			GeneratorEngine.saveRespository(repository.getRepoLocation(), repository);
		} catch (Exception x) {
			Activator.logError("Could not save repository", x);
		}
		super.okPressed();
	}
}
