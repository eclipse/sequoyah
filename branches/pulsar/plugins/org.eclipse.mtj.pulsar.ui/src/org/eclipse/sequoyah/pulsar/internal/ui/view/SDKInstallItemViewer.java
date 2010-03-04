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
 *     David Marques (Motorola) - Fixing appearance.
 *     David Marques (Motorola) - Refactoring to use label provider.
 *     David Marques (Motorola) - Loading item viewer asynchronously.
 */
package org.eclipse.sequoyah.pulsar.internal.ui.view;

import java.net.URL;

import org.eclipse.equinox.internal.provisional.p2.metadata.IInstallableUnit;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.sequoyah.pulsar.core.Activator;
import org.eclipse.sequoyah.pulsar.internal.core.SDK;
import org.eclipse.sequoyah.pulsar.internal.provisional.core.ISDK;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyDialogAction;

/**
 * SDKInstallItemViewer extends the {@link Composite} class in
 * order to build an UI block for displaying information about
 * content to be installed from within an {@link SDKInstallView}.
 * 
 * @author David Marques
 */
@SuppressWarnings("restriction")
public class SDKInstallItemViewer extends Composite {

    private class IUSelectionProvider implements ISelectionProvider {

        private IInstallableUnit iu;

        public IUSelectionProvider(IInstallableUnit iu) {
            this.iu = iu;
        }

        public void addSelectionChangedListener(
                ISelectionChangedListener listener) {
        }

        public ISelection getSelection() {
            return new StructuredSelection(this.iu);
        }

        public void removeSelectionChangedListener(
                ISelectionChangedListener listener) {
        }

        public void setSelection(ISelection selection) {
        }
    }
    private class ShellProvider implements IShellProvider {

        private Shell shell;

        public ShellProvider(Shell shell) {
            this.shell = shell;
        }

        public Shell getShell() {
            return this.shell;
        }
    }

    private Text description;
    private Label iconLabel;
    private Object input;
    private ISDKInstallItemLabelProvider labelProvider;

    private Link moreLink;

    private Link siteLink;

    /**
     * Creates a SDKInstallItemViewer instance child of
     * the specified parent {@link Composite} instance.
     * 
     * @param parent parent composite.
     */
    public SDKInstallItemViewer(Composite parent) {
        super(parent, SWT.NONE);

        GridLayout layout = new GridLayout(0x01, true);
        super.setLayout(layout);

        this.setBackground(new Color(Display.getDefault(), 0xFF, 0xFF, 0xFF));
        this.createControls(this);
    }

    /**
     * Sets the input {@link Object} to be displayed into the
     * viewer.
     * 
     * @param input input object.
     */
    public void setInput(Object input) {
        this.input = input;
        if (this.input != null) {
            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    refresh();
                }
            });
        }
    }

    /**
     * Sets the {@link ISDKInstallItemLabelProvider} instance to
     * provide content data to be displayed on the view.
     * 
     * @param provider content provider.
     */
    public void setLabelProvider(ISDKInstallItemLabelProvider provider) {
        this.labelProvider = provider;
    }

    /**
     * This method does nothing, since the internal
     * layout of the block is managed internally.
     */
    public void setLayout(Layout layout) {
    }

    /**
     * Creates the children controls of this block.
     * 
     * @param parent parent composite.
     */
    private void createControls(Composite parent) {
        GridLayout layout = null;

        iconLabel = new Label(parent, SWT.BORDER_SOLID);
        iconLabel.setBackground(parent.getBackground());
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
        gridData.minimumHeight = 50;
        gridData.heightHint = 50;
        iconLabel.setLayoutData(gridData);

        Group group = new Group(parent, SWT.NONE);
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        group.setBackground(parent.getBackground());
        group.setText("Description");
        layout = new GridLayout(0x01, true);
        layout.marginHeight = 0x00;
        layout.marginWidth = 0x00;
        group.setLayout(layout);

        description = new Text(group, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        description.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        description.setText("Information not available...");
        description.setBackground(parent.getBackground());
        description.setEditable(false);

        Composite c1 = new Composite(parent, SWT.NONE);
        c1.setBackground(parent.getBackground());
        c1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        layout = new GridLayout(2, false);
        layout.marginHeight = 0x00;
        layout.marginWidth = 0x00;
        c1.setLayout(layout);

        siteLink = new Link(c1, SWT.NONE);
        siteLink.setBackground(parent.getBackground());
        siteLink.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, true));
        siteLink.setText("<a href=\"\">Web Site...</a>");
        siteLink.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
            }

            public void widgetSelected(SelectionEvent e) {
                openBrowser();
            }
        });

        moreLink = new Link(c1, SWT.NONE);
        moreLink.setBackground(parent.getBackground());
        moreLink.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true));
        moreLink.setText("<a href=\"\">More...</a>");
        moreLink.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
            }

            public void widgetSelected(SelectionEvent e) {
                if (input instanceof SDK) {
                    openSDKProperties(((SDK) input));
                }
            }

            private void openSDKProperties(SDK sdk) {
                PropertyDialogAction dialog = new PropertyDialogAction(
                        new ShellProvider(getShell()), new IUSelectionProvider(
                                sdk.getInstallableUnit()));
                dialog.run();
            }
        });
    }

    /**
     * Updates the viewer content.
     */
    private void refresh() {
        if (this.labelProvider != null) {
            this.iconLabel.setImage(this.labelProvider.getImage(this.input));

            String description = this.labelProvider.getDescription(this.input);
            if (description != null && description.trim().length() > 0) {
                this.description.setText(description);
            } else {
                this.description.setText("Information not available...");
            }

            String site = this.labelProvider.getSite(this.input);
            if (site != null) {
                this.siteLink.setEnabled(true);
                this.siteLink.setToolTipText(site);
            } else {
                this.siteLink.setEnabled(false);
            }

            if (this.input instanceof ISDK) {
                this.moreLink.setEnabled(true);
            } else {
                this.moreLink.setEnabled(false);
            }
        }
    }

    /**
     * Opens a browser instance.
     */
    protected void openBrowser() {
        String site = labelProvider.getSite(this.input);
        if (site != null) {
            try {
                Activator.openWebBrowser(new URL(site));
            } catch (Exception exc) {
                Activator.logError(exc.getMessage(), exc);
            }
        }
    }

}
