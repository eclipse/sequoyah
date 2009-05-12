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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Collection;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.internal.provisional.p2.core.Version;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.mtj.internal.provisional.pulsar.core.ISDK.EType;
import org.eclipse.mtj.internal.pulsar.metadata.generator.engine.IIUDescription;
import org.eclipse.mtj.internal.pulsar.metadata.generator.engine.IRepositoryDescription;
import org.eclipse.mtj.internal.pulsar.metadata.generator.engine.IUDescription;
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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class NewUnitDialog extends StatusDialog {

	private IRepositoryDescription repository;
	private IUDescription unit;
	private ComboViewer typeViewer;
	private Text executableText;
	private Text displayNameText;
	private Text idText;
	private Text versionText;
	private Text categoryText;
	private Text locationText;
	private Button browseButton;
	private Text licenseURLText;
	private Text licenseBodyText;
	private Text copyrightURLText;
	private Text copyrightBodyText;
	
	private enum ETYPE {
		UNZIP, EXEC, UNZIP_AND_EXEC;
	}

	public NewUnitDialog(Shell parent, IRepositoryDescription repository) {
		super(parent);
		this.repository = repository;
		unit = new IUDescription();
	}

	public IIUDescription getUnit() {
		return unit;
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("New SDK Installer");
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		
		final Composite contents = new Composite(composite, SWT.NONE);
		final GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginBottom = 5;
		layout.marginRight = 10;
		layout.marginLeft = 10;
		contents.setLayout(layout);
		contents.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		createLabel(contents, "Artifact file");

		Composite browseComposite = new Composite(contents, SWT.NONE);
		browseComposite.setLayout(new GridLayout(2, false));
		GridDataFactory.fillDefaults().grab(true, false).applyTo(browseComposite);
		
		locationText = createText(browseComposite);
		locationText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validate();
			}
		});
		
		browseButton = new Button(browseComposite, SWT.NONE);
		GridDataFactory.fillDefaults().applyTo(browseButton);
		browseButton.setText("Browse...");
		browseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
				dialog.setFilterPath(repository.getRepoLocation().toOSString());
				dialog.setText("Select The Installer Artifact");
				String path = dialog.open();
				if (path != null) {
					IPath repoLocation = makeCanonical(repository.getRepoLocation());
					IPath artifactLocation = makeCanonical(new Path(path));
					if (!artifactLocation.removeLastSegments(1).equals(repoLocation)) {
						locationText.setText("");
						MessageDialog.openError(getShell(), "Error", "Files must in the same location as the repository");
					}
					else {
						String artifactName = artifactLocation.lastSegment();
						locationText.setText(artifactName);
						if (idText.getText().length() == 0)
							idText.setText(artifactName);
						if (displayNameText.getText().length() == 0)
							displayNameText.setText(artifactName);
						validate();
					}
				}
			}
		});
		
		createLabel(contents, "Type");
		typeViewer = new ComboViewer(contents);
		typeViewer.setContentProvider(new ArrayContentProvider());
		typeViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ETYPE) {
					switch ((ETYPE) element) {
					case UNZIP:
						return "Unzip archive";
					case EXEC:
						return "Run single executable";
					case UNZIP_AND_EXEC:
						return "Unzip and then exectute";
					}
				}
				
				return null;
			}
		});
		typeViewer.setInput(new ETYPE[] { ETYPE.UNZIP, ETYPE.EXEC, ETYPE.UNZIP_AND_EXEC });
		typeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if (!selection.isEmpty()) {
					setExectuableEnabledState((ETYPE) selection.getFirstElement());
					validate();
				}
			}
		});
		Combo combo = typeViewer.getCombo();
		GridDataFactory.fillDefaults().grab(true, false).applyTo(combo);
		combo.select(0);
		
		createLabel(contents, "Executable in archive");
		executableText = createText(contents);
		executableText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validate();
			}
		});

		executableText.setToolTipText("Specify the relative path of the executable to execute after unzipping, e.g., \"setup.exe\"");
		executableText.setEnabled(false);
		
		createLabel(contents, "Display name");
		displayNameText = createText(contents);
		displayNameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validate();
			}
		});
		
		createLabel(contents, "Id");
		idText = createText(contents);
		idText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validate();
			}
		});
		
		createLabel(contents, "Version");
		versionText = createText(contents);
		versionText.setText("1.0.0");
		versionText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validate();
			}
		});
		
		createLabel(contents, "Category name");
		categoryText = createText(contents);
		
		createLabel(contents, "License URL");
		licenseURLText = createText(contents);
		licenseURLText.setToolTipText("The location of a document containing the full license");
		licenseURLText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validate();
			}
		});

		createLabel(contents, "License body");
		licenseBodyText = createMultiText(contents);
		licenseBodyText.setToolTipText("The license body (can hold multiple lines - resize the dialog)");

		createLabel(contents, "Copyright URL");
		copyrightURLText = createText(contents);
		copyrightURLText.setToolTipText("The location of a document containing the copyright notice");
		copyrightURLText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validate();
			}
		});
		
		createLabel(contents, "Copyright body");
		copyrightBodyText = createMultiText(contents);
		copyrightBodyText.setToolTipText("The copyright body (can hold multiple lines - resize the dialog)");
		
		return contents;
	}

	@Override
	public void create() {
		super.create();
		validate();
	}
	
	private boolean validateNotEmpty(Text widget, String widgetName) {
		String value = widget.getText();
		if (value.length() == 0) {
			updateStatus(Activator.makeErrorStatus(
					MessageFormat.format("{0} must have a value", widgetName), null));
			return false;
		}
		return true;
	}

	private void createLabel(Composite parent, String text) {
		Label typeLabel = new Label(parent, SWT.NONE);
		GridDataFactory.defaultsFor(typeLabel).applyTo(typeLabel);
		typeLabel.setText(text);
	}
	
	private Text createText(Composite parent) {
		Text text = new Text(parent, SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(text);
		return text;
	}
	
	private Text createMultiText(Composite parent) {
		Text text = new Text(parent, SWT.BORDER | SWT.MULTI);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(text);
		return text;
	}
	
	private void setExectuableEnabledState(ETYPE typeVal) {
		boolean enabled = typeVal == ETYPE.UNZIP_AND_EXEC;
		if (!enabled)
			executableText.setText("");
		executableText.setEnabled(enabled);
	}
	
	private IPath makeCanonical(IPath path)	{
		try {
			return new Path(path.toFile().getCanonicalPath());
		} catch (IOException e) {
			return path;
		}
	}
	
	private void validate() {
		// version
		String value = versionText.getText();
		try { 
			Version.parseVersion(value);
		} catch (IllegalArgumentException x) {
			updateStatus(Activator.makeErrorStatus(x.getMessage(), null));
			return;
		}
		
		if (!validateNotEmpty(locationText, "Artifact file"))
			return;
		
		if (executableText.isEnabled()) {
			if (!validateNotEmpty(executableText, "Executable"))
				return;
		}
		
		if (!validateNotEmpty(idText, "Id"))
			return;
			
		if (!validateNotEmpty(displayNameText, "Display name"))
			return;
		
		if (!validateUniqueId())
			return;
		
		if (!validateValidURL(licenseURLText, "License URL"))
			return;
		
		if (!validateValidURL(copyrightURLText, "Copyright URL"))
			return;

		updateStatus(Status.OK_STATUS);
	}

	private boolean validateValidURL(Text widget, String widgetName) {
		String value = widget.getText();
		if (value.length() > 0) {
			try {
				new URL(value);
			} catch (MalformedURLException e) {
				updateStatus(Activator.makeErrorStatus(widgetName + ": " + e.getMessage(), null));
				return false;
			}
		}
		return true;
	}

	private boolean validateUniqueId() {
		Collection<IIUDescription> units = repository.getUnitCollection();
		for (IIUDescription iud : units) {
			String id = idText.getText();
			if (iud.getUnitId().equals(id)) {
				updateStatus(Activator.makeErrorStatus(MessageFormat.format(
						"Ids must be unique. \"{0}\" is already used.", id), null));
				return false;
			}
		}
		return true;
	}

	@Override
	protected boolean isResizable() {
		return true;
	}
	
	protected Point getInitialSize() {
		Point size = super.getInitialSize();
		size.x = 400;
		return size;
	}

	@Override
	protected void okPressed() {
		unit.setArtifactType(getType());
		if (executableText.isEnabled())
			unit.setExecutablePath(new Path(executableText.getText()));
		unit.setUnitName(displayNameText.getText());
		String id = idText.getText();
		unit.setUnitId(id);
		Version version = Version.parseVersion(versionText.getText());
		unit.setUnitVersion(version);
		unit.setArtifactVersion(version);
		String category = categoryText.getText();
		if (category.length() > 0)
			unit.setCategoryName(category);
		unit.setArtifactId(locationText.getText());
		String licenseURL = licenseURLText.getText();
		String licenseBody = licenseBodyText.getText();
		if (licenseURL.length() > 0 || licenseBody.length() > 0) {
			try {
				URI uri = null;
				if (licenseURL.length() > 0)
					uri = new URL(licenseURL).toURI();
				unit.setUnitLicense(uri, licenseBody);
			} catch (Exception e) {
				Activator.logError("Could not set unit license", e);
			}
		}
		String copyrightURL = copyrightURLText.getText();
		String copyrightBody = copyrightBodyText.getText();
		if (copyrightURL.length() > 0 || copyrightBody.length() > 0) {
			try {
				URI uri = null;
				if (copyrightURL.length() > 0)
					uri = new URL(licenseURL).toURI();
				unit.setUnitCopyright(uri, copyrightBody);
			} catch (Exception e) {
				Activator.logError("Could not set unit copyright", e);
			}
		}
		unit.setSingleton(false);
		super.okPressed();
	}

	private EType getType() {
		IStructuredSelection selection = (IStructuredSelection) typeViewer.getSelection();
		ETYPE val = (ETYPE) selection.getFirstElement();
		EType type = EType.UNKNOWN;
		if (val == ETYPE.UNZIP || val == ETYPE.UNZIP_AND_EXEC)
			type = EType.ZIP_ARCHIVE;
		else if (val == ETYPE.EXEC)
			type = EType.EXECUTABLE;
		return type;
	}
}
