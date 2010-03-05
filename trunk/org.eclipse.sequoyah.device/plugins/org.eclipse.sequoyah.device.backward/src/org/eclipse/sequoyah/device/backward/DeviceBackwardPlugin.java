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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;

import org.eclipse.sequoyah.device.common.utilities.BasePlugin;
import org.eclipse.sequoyah.device.framework.DevicePlugin;
import org.eclipse.sequoyah.device.framework.factory.InstanceRegistry;
import org.eclipse.sequoyah.device.framework.manager.persistence.DeviceXmlReader;
import org.eclipse.sequoyah.device.framework.model.IInstance;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class DeviceBackwardPlugin extends AbstractUIPlugin implements IStartup {

	// The plug-in ID
	public static final String PLUGIN_ID = Messages.DeviceBackwardPlugin_0;

	// The shared instance
	private static DeviceBackwardPlugin plugin;

	private final String PROJECT_NAME = "sequoyah"; //$NON-NLS-1$

	private final String OLD_PROJECT_NAME = "tml"; //$NON-NLS-1$

	private final String DEVICE_XML_INFO_SUFFIX = ".info"; //$NON-NLS-1$

	private final String BACKWARD_MARK = "backward"; //$NON-NLS-1$

	private final String OLD_DEVICE_PLUGIN_ID = "org.eclipse.tml.framework.device"; //$NON-NLS-1$

	/*
	 * Device XML location
	 */
	File deviceXmlLocation = null;

	File deviceXmlFile = null;

	File oldDeviceXmlFile = null;

	File oldDeviceXmlFileInfo = null;

	/**
	 * The constructor
	 */
	public DeviceBackwardPlugin() {

		this.deviceXmlLocation = DevicePlugin.getDeviceXmlLocation();
		this.deviceXmlFile = new File(deviceXmlLocation.getAbsolutePath()
				+ File.separator + DevicePlugin.getDeviceXmlFileName());
		String oldDeviceXmlFilePath = deviceXmlFile.getAbsolutePath()
				.replaceAll(DevicePlugin.PLUGIN_ID, OLD_DEVICE_PLUGIN_ID)
				.replaceAll(PROJECT_NAME, OLD_PROJECT_NAME);
		this.oldDeviceXmlFile = new File(oldDeviceXmlFilePath);
		this.oldDeviceXmlFileInfo = new File(oldDeviceXmlFilePath
				+ DEVICE_XML_INFO_SUFFIX);

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

		if (!getBackwardStatus()) {
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
	 * Check if backward procedure has already been executed
	 * 
	 * @return true if backward procedure has already been executed, false
	 *         otherwise
	 */
	private boolean getBackwardStatus() {

		boolean result = false;

		try {
			if (oldDeviceXmlFileInfo.exists()) {

				FileReader oldDeviceXmlFileReader = new FileReader(
						oldDeviceXmlFileInfo);
				BufferedReader buffReader = new BufferedReader(
						oldDeviceXmlFileReader);
				String line;
				while ((line = buffReader.readLine()) != null) {
					if (line.startsWith(BACKWARD_MARK)) {
						result = true;
					}
				}

				oldDeviceXmlFileReader.close();
				buffReader.close();

			}
		} catch (Exception e) {
			BasePlugin.logError(Messages.DeviceBackwardPlugin_1);
		}

		return result;
	}

	/**
	 * Add backward status information
	 */
	private void addBackwardStatus() {

		FileWriter deviceXmlFileWriter;
		try {
			deviceXmlFileWriter = new FileWriter(oldDeviceXmlFileInfo);
			deviceXmlFileWriter.write(BACKWARD_MARK + "=" //$NON-NLS-1$
					+ new Date().toString());
			deviceXmlFileWriter.close();
		} catch (IOException e) {
			BasePlugin.logError(Messages.DeviceBackwardPlugin_3);
		}

	}

	/**
	 * Move old TmL devices XML file to new Sequoyah location
	 */
	private void moveDeviceInstancesData() {

		if (oldDeviceXmlFile.exists()) {

			try {

				Collection<IInstance> oldInstances = DeviceXmlReader
						.loadInstances(oldDeviceXmlFile);

				InstanceRegistry instanceRegistry = InstanceRegistry
						.getInstance();
				for (IInstance instance : oldInstances) {
					if (instanceRegistry.getInstancesByName(instance.getName())
							.size() == 0) {
						instanceRegistry.addInstance(instance);
					}
				}

				addBackwardStatus();

			} catch (Exception e) {
				BasePlugin.logError(Messages.DeviceBackwardPlugin_4);
			}
		}

	}

	public void earlyStartup() {
		// do nothing
	}
}
