/********************************************************************************
 * Copyright (c) 2007 Motorola Inc.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Fabio Fantato (Motorola)
 * 
 * Contributors:
 * name (company) - description.
 ********************************************************************************/

package org.eclipse.tml.device.qemuarm;

import org.eclipse.tml.common.utilities.BasePlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class QEmuARMPlugin extends BasePlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.tml.device.qemuarm";
	public static final String DEVICE_ID = "org.eclipse.tml.device.qemuarm.qemuarmDevice";
	
	public static final String ICON_DEVICE_QEMUARM = "ICON_DEVICE_QEMUARM";
	
	public static final String EMULATOR_NAME = "qemu";
	public static final String EMULATOR_PARAMS = "-kernel integratorcp.zImage -pidfile qemuarm.id -initrd arm_root.img -M integratorcp1026 -vnc ";
	public static final String EMULATOR_BIN = "qemu-arm-vnc.bat";
	public static final String EMULATOR_KILL = "qemu-system-arm.exe";
	public static final String EMULATOR_FILE_ID = "qemuarm.id";

	// The shared instance
	private static QEmuARMPlugin plugin;
	
	/**
	 * The constructor
	 */
	public QEmuARMPlugin() {
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
	public static QEmuARMPlugin getDefault() {
		return plugin;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.tml.common.utilities.BasePlugin#initializeImageRegistry()
	 */
	@Override
	protected void initializeImageRegistry() {
		String path = getIconPath();
		putImageInRegistry(ICON_DEVICE_QEMUARM, path+"full/obj16/qemuarm.gif"); //$NON-NLS-1$	
	}


}
