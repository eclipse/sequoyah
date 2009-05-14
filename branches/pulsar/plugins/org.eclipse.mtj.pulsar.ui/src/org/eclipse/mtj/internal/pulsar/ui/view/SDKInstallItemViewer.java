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
 */
package org.eclipse.mtj.internal.pulsar.ui.view;

import java.net.URL;

import org.eclipse.mtj.pulsar.core.Activator;
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
import org.eclipse.swt.widgets.Text;

/**
 * SDKInstallItemViewer extends the {@link Composite} class in
 * order to build an UI block for displaying information about
 * content to be installed from within an {@link SDKInstallView}.
 * 
 * @author David Marques
 */
public class SDKInstallItemViewer extends Composite {

	private ISDKInstallItemViewerContentProvider provider;
	private Label iconLabel;
	private Link  siteLink;
	private Text description;

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
	 * Sets the {@link ISDKInstallItemViewerContentProvider} instance to
	 * provide content to be displayed on the view.
	 * 
	 * @param provider content provider.
	 */
	public void setContentProvider(ISDKInstallItemViewerContentProvider provider) {
		this.provider = provider;
		this.refresh();
	}
	
	/**
	 * This method does nothing, since the internal
	 * layout of the block is managed internally.
	 */
	public void setLayout(Layout layout) {}
	
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
		gridData.heightHint    = 50;
		iconLabel.setLayoutData(gridData);

		Group group = new Group(parent, SWT.NONE);
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		group.setBackground(parent.getBackground());
		group.setText("Description");
		layout = new GridLayout(0x01, true);
		layout.marginHeight = 0x00;
		layout.marginWidth  = 0x00;
		group.setLayout(layout);
		
		description = new Text(group, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		description.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		description.setBackground(parent.getBackground());
		description.setEditable(false);
		
		Composite c1 = new Composite(parent, SWT.NONE);
		c1.setBackground(parent.getBackground());
		c1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		layout = new GridLayout(2, false);
		layout.marginHeight = 0x00;
		layout.marginWidth  = 0x00;
		c1.setLayout(layout);
		
		siteLink = new Link(c1, SWT.NONE);
		siteLink.setBackground(parent.getBackground());
		siteLink.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, true));
		siteLink.setText("<a href=\"\">Web Site...</a>");
		siteLink.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
				openBrowser();
			}
		
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
//		Link moreLink = new Link(c1, SWT.NONE);
//		moreLink.setBackground(parent.getBackground());
//		moreLink.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true));
//		moreLink.setText("<a href=\"\">More...</a>");
	}
	
	/**
	 * Opens a browser instance.
	 */
	protected void openBrowser() {
		String site = provider.getSite();
		if (site != null) {
			try {
				Activator.openWebBrowser(new URL(site));
			} catch (Exception exc) {
				Activator.logError(exc.getMessage(), exc);
			}
		}
	}

	/**
	 * Updates the viewer content.
	 */
	private void refresh() {
		if (this.provider != null) {
			this.iconLabel.setImage(this.provider.getImage());
			
			String description = this.provider.getDescription();
			if (description != null && description.trim().length() > 0) {				
				this.description.setText(description);
			} else {
				this.description.setText("Information not available...");
			}
			
			String site = this.provider.getSite();
			if (site != null) {
				this.siteLink.setVisible(true);
				this.siteLink.setToolTipText(site);
			} else {
				this.siteLink.setVisible(false);
			}
			this.layout(true);
		}
	}
	
}
