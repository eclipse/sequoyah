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
 * name (company) - description.
 ********************************************************************************/
package org.eclipse.tml.framework.device.additional;

import org.osgi.framework.BundleContext;
import org.eclipse.tml.common.utilities.BasePlugin;

public class DeviceAddPlugin extends BasePlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.tml.framework.device.ui"; //$NON-NLS-1$
	public static final String ICON_DEVICE= "ICON_DEVICE"; //$NON-NLS-1$
	public static final String ICON_SERVICE = "ICON_SERVICE"; //$NON-NLS-1$
	public static final String ICON_MOVING = "ICON_MOVING"; //$NON-NLS-1$
	public static final String ICON_BOOK = "ICON_BOOK"; //$NON-NLS-1$
	public static final String ICON_GAMEBOARD = "ICON_GAMEBOARD"; //$NON-NLS-1$
	public static final String ICON_NEW_BOOK = "ICON_NEW_BOOK"; //$NON-NLS-1$
	public static final String ICON_REMOVE = "ICON_REMOVE"; //$NON-NLS-1$
	public static final String ICON_START = "ICON_START"; //$NON-NLS-1$
	public static final String ICON_STOP = "ICON_STOP"; //$NON-NLS-1$
	public static final String ICON_REFRESH = "ICON_REFRESH"; //$NON-NLS-1$
	public static final String ICON_INACTIVE = "ICON_INACTIVE"; //$NON-NLS-1$
	public static final String ICON_HORIZONTAL = "ICON_HORIZONTAL"; //$NON-NLS-1$
	public static final String ICON_VERTICAL = "ICON_VERTICAL"; //$NON-NLS-1$
	
	// The shared instance
	private static DeviceAddPlugin plugin;
	
	/**
	 * The constructor
	 */
	public DeviceAddPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static DeviceAddPlugin getDefault() {
		return plugin;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.tml.common.utilities.BasePlugin#initializeImageRegistry()
	 */
	@Override
	protected void initializeImageRegistry() {
		String path = getIconPath();
		putImageInRegistry(ICON_DEVICE, path+"full/obj16/device.gif"); //$NON-NLS-1$	
		putImageInRegistry(ICON_SERVICE, path+"full/obj16/service.gif"); //$NON-NLS-1$
		putImageInRegistry(ICON_START, path+"full/obj16/start.gif"); //$NON-NLS-1$
		putImageInRegistry(ICON_STOP, path+"full/obj16/stop.gif"); //$NON-NLS-1$
		putImageInRegistry(ICON_REFRESH, path+"full/obj16/refresh.gif"); //$NON-NLS-1$
		putImageInRegistry(ICON_INACTIVE, path+"full/obj16/inactive.gif"); //$NON-NLS-1$
		putImageInRegistry(ICON_MOVING, path+"movingBox.gif"); //$NON-NLS-1$
		putImageInRegistry(ICON_BOOK, path+"book.gif"); //$NON-NLS-1$
		putImageInRegistry(ICON_GAMEBOARD, path+"gameboard.gif"); //$NON-NLS-1$
		putImageInRegistry(ICON_NEW_BOOK, path+"newbook.gif"); //$NON-NLS-1$
		putImageInRegistry(ICON_REMOVE, path+"remove.gif"); //$NON-NLS-1$
		putImageInRegistry(ICON_HORIZONTAL, path+"full/obj16/horizontal.gif"); //$NON-NLS-1$
		putImageInRegistry(ICON_VERTICAL, path+"full/obj16/vertical.gif"); //$NON-NLS-1$
	}
}
