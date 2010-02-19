/********************************************************************************
 * Copyright (c) 2010 Motorola Inc.
 * All rights reserved. This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Marcel Gorri (Motorola)
 * 
 * Contributors:
 * name (company) - description.
 ********************************************************************************/
package org.eclipse.sequoyah.device.backward;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.eclipse.sequoyah.device.common.utilities.BasePlugin;
import org.eclipse.sequoyah.device.framework.DevicePlugin;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class DeviceBackwardPlugin extends AbstractUIPlugin implements IStartup {

	private final String DEVICE_BACKWARD_STATUS = "DEVICE_BACKWARD_STATUS";

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.sequoyah.device.backward";

	// The shared instance
	private static DeviceBackwardPlugin plugin;

	private final String PROJECT_NAME = "sequoyah"; //$NON-NLS-1$

	private final String OLD_PROJECT_NAME = "tml"; //$NON-NLS-1$

	private final String OLD_DEVICE_PLUGIN_ID = "org.eclipse.tml.framework.device"; //$NON-NLS-1$

	/**
	 * The constructor
	 */
	public DeviceBackwardPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		boolean deviceBackwardStatus = this.getPreferenceStore().getBoolean(
				DEVICE_BACKWARD_STATUS);
		if (!deviceBackwardStatus) {
			moveDeviceInstancesData();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
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
	public static DeviceBackwardPlugin getDefault() {
		return plugin;
	}

	/**
	 * Move old TmL devices XML file to new Sequoyah location
	 */
	private void moveDeviceInstancesData() {

		File deviceXmlLocation = DevicePlugin.getDeviceXmlLocation();
		File deviceXmlFile = new File(deviceXmlLocation.getAbsolutePath()
				+ File.separator + DevicePlugin.getDeviceXmlFileName());
		File oldDeviceXmlFile = new File(deviceXmlFile.getAbsolutePath()
				.replaceAll(DevicePlugin.PLUGIN_ID, OLD_DEVICE_PLUGIN_ID)
				.replaceAll(PROJECT_NAME, OLD_PROJECT_NAME));

		if (oldDeviceXmlFile.exists()) {

			try {

				FileReader oldDeviceXmlFileReader = new FileReader(
						oldDeviceXmlFile);
				FileWriter deviceXmlFileWriter = new FileWriter(deviceXmlFile);
				int line;

				while ((line = oldDeviceXmlFileReader.read()) != -1) {
					deviceXmlFileWriter.write(line);
				}

				this.getPreferenceStore()
						.setValue(DEVICE_BACKWARD_STATUS, true);

				oldDeviceXmlFileReader.close();
				deviceXmlFileWriter.close();

			} catch (Exception e) {
				BasePlugin.logError("Could not recover TmL devices");
			}
		}

	}

	public void earlyStartup() {
		// do nothing
	}
}
