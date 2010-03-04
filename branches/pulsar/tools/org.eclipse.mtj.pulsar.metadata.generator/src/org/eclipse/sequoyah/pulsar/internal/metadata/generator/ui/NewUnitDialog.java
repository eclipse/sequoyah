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
 *  Henrique Magalhaes (Motorola) - Internalization of messages
 *  David Marques (Motorola) - Implementing environment filtering.
 *  Euclides Neto (Motorola) - Adding Category description support.
 */

package org.eclipse.sequoyah.pulsar.internal.metadata.generator.ui;

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
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.sequoyah.pulsar.core.Activator;
import org.eclipse.sequoyah.pulsar.internal.metadata.generator.engine.IIUDescription;
import org.eclipse.sequoyah.pulsar.internal.metadata.generator.engine.IRepositoryDescription;
import org.eclipse.sequoyah.pulsar.internal.metadata.generator.engine.IUDescription;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import sun.security.krb5.internal.crypto.EType;

@SuppressWarnings("restriction")
public class NewUnitDialog extends StatusDialog {

    private EnvironmentDialog envDialog;
    private IRepositoryDescription repository;
    private IUDescription unit;
    private ComboViewer typeViewer;
    private Text executableText;
    private Text displayNameText;
    private Text idText;
    private Text versionText;
    private Text categoryText;
    private Text categoryDescriptionText;
    private Text descriptionText;
    private Text locationText;
    private Button browseButton;
    private Text licenseURLText;
    private Text licenseBodyText;
    private Text copyrightURLText;
    private Text copyrightBodyText;
    private Text osText;
    private Text wsText;
    private Text archText;

    private enum ETYPE {
        UNZIP, EXEC, UNZIP_AND_EXEC;
    }

    public NewUnitDialog(Shell parent, IRepositoryDescription repository) {
        super(parent);
        this.repository = repository;
        unit = new IUDescription();
        envDialog = new EnvironmentDialog(this.getShell());
    }

    public IIUDescription getUnit() {
        return unit;
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(Messages.NewUnitDialog_NewSDKInstallerShellTitle);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);

        ScrolledComposite scroll = new ScrolledComposite(composite,
                SWT.V_SCROLL);
        scroll.setExpandHorizontal(true);
        scroll.setExpandVertical(true);
        scroll.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final Composite contents = new Composite(scroll, SWT.NONE);
        final GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginBottom = 5;
        layout.marginRight = 10;
        layout.marginLeft = 10;
        contents.setLayout(layout);
        contents.setLayoutData(new GridData(GridData.FILL_BOTH));

        createLabel(contents, Messages.NewUnitDialog_ArtifactFileLabel);

        Composite browseComposite = new Composite(contents, SWT.NONE);
        browseComposite.setLayout(new GridLayout(2, false));
        GridDataFactory.fillDefaults().grab(true, false).applyTo(
                browseComposite);

        locationText = createText(browseComposite);
        locationText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                validate();
            }
        });

        browseButton = new Button(browseComposite, SWT.NONE);
        GridDataFactory.fillDefaults().applyTo(browseButton);
        browseButton.setText(Messages.NewUnitDialog_BrowseButton);
        browseButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
                dialog.setFilterPath(repository.getRepoLocation().toOSString());
                dialog
                        .setText(Messages.NewUnitDialog_SelectArtifactDialogMessage);
                String path = dialog.open();
                if (path != null) {
                    IPath repoLocation = makeCanonical(repository
                            .getRepoLocation());
                    IPath artifactLocation = makeCanonical(new Path(path));
                    if (!artifactLocation.removeLastSegments(1).equals(
                            repoLocation)) {
                        locationText.setText(""); //$NON-NLS-1$
                        MessageDialog
                                .openError(
                                        getShell(),
                                        Messages.NewUnitDialog_ErrorDialogTitle,
                                        Messages.NewUnitDialog_RepositoryLocationErrorMessage);
                    } else {
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

        createLabel(contents, Messages.NewUnitDialog_TypeLabel);
        typeViewer = new ComboViewer(contents);
        typeViewer.setContentProvider(new ArrayContentProvider());
        typeViewer.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof ETYPE) {
                    switch ((ETYPE) element) {
                        case UNZIP:
                            return Messages.NewUnitDialog_UnzipArchiveLabelProviderText;
                        case EXEC:
                            return Messages.NewUnitDialog_SingleExecutableLabelProviderText;
                        case UNZIP_AND_EXEC:
                            return Messages.NewUnitDialog_UnzipLabelProviderText;
                    }
                }

                return null;
            }
        });
        typeViewer.setInput(new ETYPE[] { ETYPE.UNZIP, ETYPE.EXEC,
                ETYPE.UNZIP_AND_EXEC });
        typeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection selection = (IStructuredSelection) event
                        .getSelection();
                if (!selection.isEmpty()) {
                    setExectuableEnabledState((ETYPE) selection
                            .getFirstElement());
                    validate();
                }
            }
        });
        Combo combo = typeViewer.getCombo();
        GridDataFactory.fillDefaults().grab(true, false).applyTo(combo);
        combo.select(0);

        createLabel(contents, Messages.NewUnitDialog_ExecutableLabel);
        executableText = createText(contents);
        executableText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                validate();
            }
        });

        executableText.setToolTipText(Messages.NewUnitDialog_ExecutableToolTip);
        executableText.setEnabled(false);

        createLabel(contents, Messages.NewUnitDialog_DisplayNameLabel);
        displayNameText = createText(contents);
        displayNameText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                validate();
            }
        });

        createLabel(contents, Messages.NewUnitDialog_IDLabel);
        idText = createText(contents);
        idText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                validate();
            }
        });

        createLabel(contents, Messages.NewUnitDialog_VersionLabel);
        versionText = createText(contents);
        versionText.setText("1.0.0"); //$NON-NLS-1$
        versionText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                validate();
            }
        });

        createLabel(contents, Messages.NewUnitDialog_CategoryNameLabel);
        categoryText = createText(contents);
        
        createLabel(contents, Messages.NewUnitDialog_CategoryDescriptionLabel);
        categoryDescriptionText = createText(contents);

        createLabel(contents, Messages.NewUnitDialog_DescriptionLabel);
        descriptionText = createMultiText(contents);
        createEmptyCell(contents);
        createEmptyCell(contents);

        createLabel(contents, Messages.NewUnitDialog_LicenseURLLabel);
        licenseURLText = createText(contents);
        licenseURLText.setToolTipText(Messages.NewUnitDialog_LicenseURLToolTip);
        licenseURLText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                validate();
            }
        });

        createLabel(contents, Messages.NewUnitDialog_LicenseBodyLabel);
        licenseBodyText = createMultiText(contents);
        licenseBodyText
                .setToolTipText(Messages.NewUnitDialog_LicenseBodyToolTip);
        createEmptyCell(contents);
        createEmptyCell(contents);

        createLabel(contents, Messages.NewUnitDialog_CopyrightURLLabel);
        copyrightURLText = createText(contents);
        copyrightURLText
                .setToolTipText(Messages.NewUnitDialog_CopyrightURLToolTip);
        copyrightURLText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                validate();
            }
        });

        createLabel(contents, Messages.NewUnitDialog_CopyrightBodyLabel);
        copyrightBodyText = createMultiText(contents);
        copyrightBodyText
                .setToolTipText(Messages.NewUnitDialog_CopyrightBodyToolTip);
        createEmptyCell(contents);
        createEmptyCell(contents);

        createEnvironmentSection(contents);

        scroll.setContent(contents);
        scroll.setMinSize(contents.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        return composite;
    }

    private void createEnvironmentSection(Composite parent) {
        Group group = new Group(parent, SWT.NONE);
        group.setText("Installation Environment");
        group.setLayout(new GridLayout(0x03, false));

        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        gridData.horizontalSpan = 2;
        group.setLayoutData(gridData);

        Label osLabel = new Label(group, SWT.NONE);
        osLabel.setText("Operating System:");
        osLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

        osText = new Text(group, SWT.BORDER);
        osText.setEditable(false);
        osText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        Button osBrowse = new Button(group, SWT.NONE);
        osBrowse.setText("Browse...");
        osBrowse.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
        osBrowse.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                envDialog.setOptions(new String[] { "aix", "hpux", "linux",
                        "macosx", "qnx", "solaris", "win32" });
                if (envDialog.open() == Dialog.OK) {
                    osText.setText(envDialog.getSelectedOptions());
                }
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        Label wsLabel = new Label(group, SWT.NONE);
        wsLabel.setText("Window System:");
        wsLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

        wsText = new Text(group, SWT.BORDER);
        wsText.setEditable(false);
        wsText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        Button wsBrowse = new Button(group, SWT.NONE);
        wsBrowse.setText("Browse...");
        wsBrowse.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
        wsBrowse.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                envDialog.setOptions(new String[] { "carbon", "cocoa", "gtk",
                        "motif", "photon", "win32", "wpf" });
                if (envDialog.open() == Dialog.OK) {
                    wsText.setText(envDialog.getSelectedOptions());
                }
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        Label archLabel = new Label(group, SWT.NONE);
        archLabel.setText("Architecture:");
        archLabel
                .setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

        archText = new Text(group, SWT.BORDER);
        archText.setEditable(false);
        archText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        Button archBrowse = new Button(group, SWT.NONE);
        archBrowse.setText("Browse...");
        archBrowse
                .setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
        archBrowse.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                envDialog.setOptions(new String[] { "ia64", "ia64_32",
                        "PA_RISC", "ppc", "sparc", "x86", "x86_64" });
                if (envDialog.open() == Dialog.OK) {
                    archText.setText(envDialog.getSelectedOptions());
                }
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
    }

    @Override
    public void create() {
        super.create();
        validate();
    }

    private boolean validateNotEmpty(Text widget, String widgetName) {
        String value = widget.getText();
        if (value.length() == 0) {
            updateStatus(Activator.makeErrorStatus(MessageFormat.format(
                    Messages.NewUnitDialog_CheckValueError, widgetName), null));
            return false;
        }
        return true;
    }

    private void createEmptyCell(Composite parent) {
        Label typeLabel = new Label(parent, SWT.NONE);
        GridDataFactory.defaultsFor(typeLabel).applyTo(typeLabel);
        typeLabel.setText(""); //$NON-NLS-1$
        typeLabel.setVisible(false);
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
        Text text = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
        GridDataFactory.fillDefaults().grab(true, true).span(1, 3)
                .applyTo(text);
        return text;
    }

    private void setExectuableEnabledState(ETYPE typeVal) {
        boolean enabled = typeVal == ETYPE.UNZIP_AND_EXEC;
        if (!enabled)
            executableText.setText(""); //$NON-NLS-1$
        executableText.setEnabled(enabled);
    }

    private IPath makeCanonical(IPath path) {
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

        if (!validateNotEmpty(locationText,
                Messages.NewUnitDialog_ArtifactFileLabel))
            return;

        if (executableText.isEnabled()) {
            if (!validateNotEmpty(executableText,
                    Messages.NewUnitDialog_Executable))
                return;
        }

        if (!validateNotEmpty(idText, Messages.NewUnitDialog_IDLabel))
            return;

        if (!validateNotEmpty(displayNameText,
                Messages.NewUnitDialog_DisplayNameLabel))
            return;

        if (!validateUniqueId())
            return;

        if (!validateValidURL(licenseURLText,
                Messages.NewUnitDialog_LicenseURLLabel))
            return;

        if (!validateValidURL(copyrightURLText,
                Messages.NewUnitDialog_CopyrightURLLabel))
            return;

        updateStatus(Status.OK_STATUS);
    }

    private boolean validateValidURL(Text widget, String widgetName) {
        String value = widget.getText();
        if (value.length() > 0) {
            try {
                new URL(value);
            } catch (MalformedURLException e) {
                updateStatus(Activator.makeErrorStatus(widgetName
                        + ": " + e.getMessage(), null)); //$NON-NLS-1$
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
                        Messages.NewUnitDialog_UniqueIdError, id), null));
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
        String categoryDescription = categoryDescriptionText.getText();
        if (categoryDescription.length() > 0)
            unit.setCategoryDescription(categoryDescription);
        String artefactDescription = descriptionText.getText();
        if (artefactDescription.length() > 0)
            unit.setArtifactDescription(artefactDescription);
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
                Activator.logError(Messages.NewUnitDialog_SetUnitLicenseError,
                        e);
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
                Activator.logError(
                        Messages.NewUnitDialog_SetUnitCopyrightError, e);
            }
        }
        unit.setSingleton(false);
        unit.setArch(this.archText.getText());
        unit.setOs(this.osText.getText());
        unit.setWs(this.wsText.getText());
        super.okPressed();
    }

    private EType getType() {
        IStructuredSelection selection = (IStructuredSelection) typeViewer
                .getSelection();
        ETYPE val = (ETYPE) selection.getFirstElement();
        EType type = EType.UNKNOWN;
        if (val == ETYPE.UNZIP || val == ETYPE.UNZIP_AND_EXEC)
            type = EType.ZIP_ARCHIVE;
        else if (val == ETYPE.EXEC)
            type = EType.EXECUTABLE;
        return type;
    }
}
