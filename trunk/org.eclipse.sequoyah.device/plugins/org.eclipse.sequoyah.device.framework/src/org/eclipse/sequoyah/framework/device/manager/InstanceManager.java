/********************************************************************************
 * Copyright (c) 2007-2009 Motorola Inc and others.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Fabio Fantato (Motorola)
 * 
 * Contributors:
 * Fabio Fantato (Motorola) - bug#221733 - code revisited
 * Otavio Luiz Ferranti (Eldorado Research Institute) - bug#221733 - Adding data persistence
 * Yu-Fen Kuo (MontaVista) - try to replace jdom dependencies with eclipse default xml parsers.
 * Fabio Rigo (Eldorado) - [245114] Enhance persistence policies
 * Yu-Fen Kuo (MontaVista)  - [236476] - provide a generic device type
 * Daniel Barboza Franco (Eldorado Research Institute) - Bug [221739] - Improvements to State machine implementation
 * Daniel Barboza Franco (Eldorado Research Institute) - Bug [252261] - Internal class MobileInstance providing functionalities
 * Fabio Fantato (Instituto Eldorado) - [263188] - Create new examples to support tutorial presentation
 * Fabio Fantato (Instituto Eldorado) - [243494] Change the reference implementation to work on Galileo
 * Daniel Barboza Franco (Eldorado Research Institute) - Bug [269716] - InstanceDeleted event is been fired when a new Instance is created
 ********************************************************************************/
package org.eclipse.tml.framework.device.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.tml.common.utilities.PluginUtils;
import org.eclipse.tml.common.utilities.exception.ExceptionHandler;
import org.eclipse.tml.common.utilities.exception.TmLException;
import org.eclipse.tml.framework.device.DevicePlugin;
import org.eclipse.tml.framework.device.events.InstanceEvent;
import org.eclipse.tml.framework.device.events.InstanceEventManager;
import org.eclipse.tml.framework.device.exception.DeviceExceptionHandler;
import org.eclipse.tml.framework.device.exception.DeviceExceptionStatus;
import org.eclipse.tml.framework.device.factory.InstanceRegistry;
import org.eclipse.tml.framework.device.manager.persistence.DeviceXmlReader;
import org.eclipse.tml.framework.device.manager.persistence.DeviceXmlWriter;
import org.eclipse.tml.framework.device.manager.persistence.TmLDevice;
import org.eclipse.tml.framework.device.model.AbstractMobileInstance;
import org.eclipse.tml.framework.device.model.IDeviceLauncher;
import org.eclipse.tml.framework.device.model.IDeviceType;
import org.eclipse.tml.framework.device.model.IInstance;
import org.eclipse.tml.framework.device.model.IInstanceBuilder;
import org.eclipse.tml.framework.device.model.handler.IDeviceHandler;
import org.eclipse.tml.framework.device.statemachine.StateMachineHandler;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * Manages the device instances
 * 
 * @author Fabio Fantato
 */
public class InstanceManager {

	private static final String ELEMENT_DEVICE = "deviceType"; //$NON-NLS-1$
	private static final String ATTR_HANDLER = "handler"; //$NON-NLS-1$

	private static InstanceManager _instance;
	private IInstance currentInstance;

	// member field to store list of devices defined in tml_devices.xml or
	// derives from instances
	private Map<String, TmLDevice> devices;

	/**
	 * Constructor - Manages the device instances
	 */
	private InstanceManager() {
		DeviceXmlReader.loadInstances(this);
		if (devices == null)
		{
		    devices = new HashMap<String, TmLDevice>();
		}
	    IWorkbench workbench = DevicePlugin.getDefault().getWorkbench();
	    workbench.addWindowListener(new WindowListener());
	}

	/**
	 * Singleton member with creates and returns the instance
	 * 
	 * @return The current available instance
	 */
	public static InstanceManager getInstance() {
		if (_instance == null) {
			_instance = new InstanceManager();
		}
		return _instance;
	}

	private class WindowListener implements IWindowListener {

        public void windowClosed(IWorkbenchWindow window) {
            DeviceXmlWriter.saveInstances(devices);
        }

        public void windowOpened(IWorkbenchWindow window) {

        }

        public void windowDeactivated(IWorkbenchWindow window) {

        }

        public void windowActivated(IWorkbenchWindow window) {

        }
    }
	
	/**
	 * Sets the current instance. The current instance information is used be
	 * the InstanceView class.
	 * 
	 * @param instance -
	 *            The current instance.
	 */
	public void setInstance(IInstance instance) {
		this.currentInstance = instance;
	}

	/**
	 * Retrieves the currently selected instance.
	 * 
	 * @return The current instance.
	 */
	public IInstance getCurrentInstance() {
		return this.currentInstance;
	}

	
	
	public IDeviceLauncher createLauncher(IInstance instance) throws TmLException {

		IDeviceHandler deviceHandler = null;
		IDeviceLauncher launcher = null;
		try {
			IExtension fromPlugin = PluginUtils.getExtension(
					DevicePlugin.DEVICE_TYPES_EXTENSION_POINT_ID, instance.getDeviceTypeId());
			deviceHandler = (IDeviceHandler) PluginUtils
					.getExecutableAttribute(fromPlugin, ELEMENT_DEVICE,
							ATTR_HANDLER);
			launcher = deviceHandler.createDeviceLauncher(instance);
		} catch (CoreException ce) {
			ExceptionHandler
					.showException(DeviceExceptionHandler
							.exception(DeviceExceptionStatus.CODE_ERROR_HANDLER_NOT_INSTANCED));
		}
		
		return launcher;
	}
	
	/**
	 * Creates a new instance.
	 * 
	 * @param name -
	 *            Instance name.
	 * @param deviceId -
	 *            The instance device id.
	 * @param status -
	 *            The instance status.
	 * @param properties -
	 *            The instance properties.
	 * @return The created instance.
	 * @throws TmLException
	 */
	public IInstance createInstance(String name, String deviceId,
			String status, Properties properties) throws TmLException {

		IDeviceHandler deviceHandler = null;
		IInstance instance = null;
		try {
			IExtension fromPlugin = PluginUtils.getExtension(DevicePlugin.DEVICE_TYPES_EXTENSION_POINT_ID, deviceId);
			deviceHandler = (IDeviceHandler) PluginUtils
					.getExecutableAttribute(fromPlugin, ELEMENT_DEVICE,
							ATTR_HANDLER);
			// getExecutable(DevicePlugin.DEVICE_ID, deviceId);
			instance = deviceHandler.createDeviceInstance(name + deviceId);
			instance.setDeviceTypeId(deviceId);
			instance.setName(name);
			((AbstractMobileInstance) instance).setStateMachineHandler(new StateMachineHandler(instance));
			instance.setStatus(status);
			instance.setProperties((Properties) properties.clone());
			
		} catch (CoreException ce) {
			ExceptionHandler
					.showException(DeviceExceptionHandler
							.exception(DeviceExceptionStatus.CODE_ERROR_HANDLER_NOT_INSTANCED));
		}
		InstanceEventManager.getInstance().fireInstanceCreated(new InstanceEvent(instance));
		return instance;
	}
	
	public void deleteInstance(IInstance instance) {
        if (currentInstance == instance) {
            currentInstance = null;
        }
        InstanceRegistry registry = InstanceRegistry.getInstance();
        registry.removeInstance(instance); 
        DeviceXmlWriter.saveInstances(devices);
        InstanceEventManager.getInstance().fireInstanceDeleted(new InstanceEvent(instance));
    }
	
	/**
	 * Creates an instance, sets it as the currently selected and adds it to the
	 * instance registry.
	 * 
	 * @param device -
	 *            The instance device id.
	 * @param projectBuilder
	 * @param monitor
	 */
	public void createProject(IDeviceType device, IInstanceBuilder projectBuilder,
			IProgressMonitor monitor) {
		try {
			IInstance inst = createInstance(projectBuilder.getProjectName(),
					device.getId(), DevicePlugin.TML_STATUS_OFF, projectBuilder
							.getProperties());
			if (currentInstance == null) {
				currentInstance = inst;
			}
			InstanceRegistry registry = InstanceRegistry.getInstance();
			registry.addInstance(inst);
	
	        DeviceXmlWriter.saveInstances(devices);
		} catch (TmLException te) {
			ExceptionHandler
					.showException(DeviceExceptionHandler
							.exception(DeviceExceptionStatus.CODE_ERROR_HANDLER_NOT_INSTANCED));
		}
	}

	/**
	 * Retrieves all instances with a specified matching name
	 * 
	 * @param name -
	 *            The instance name to be queried
	 * @return A list of IInstance objects of name matching instances
	 */
	public List<IInstance> getInstancesByname(String name) {
		InstanceRegistry registry = InstanceRegistry.getInstance();

		List<IInstance> instanceList = registry.getInstances();
		List<IInstance> returnValue = new ArrayList<IInstance>();

		Iterator<IInstance> it = instanceList.iterator();
		while (it.hasNext()) {
			IInstance inst = it.next();
			if (inst.getName().equals(name)) {
				returnValue.add(inst);
			}
		}
		return returnValue;
	}	
	
	public void setDevicesMap(Map<String, TmLDevice> devices)
	{
	    this.devices = devices;
	}
}
