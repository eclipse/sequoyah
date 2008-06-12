/********************************************************************************
 * Copyright (c) 2007-2008 Motorola Inc and others.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Fabio Fantato (Motorola)
 * 
 * Contributors:
 * Fabio Fantato (Motorola) - bug#221733 - code revisited
 * Otávio Luiz Ferranti (Eldorado Research Institute) - bug#221733 - Adding data persistence
 * Yu-Fen Kuo (MontaVista) - try to replace jdom dependencies with eclipse default xml parsers.
 ********************************************************************************/
package org.eclipse.tml.framework.device.manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.tml.common.utilities.PluginUtils;
import org.eclipse.tml.common.utilities.exception.TmLException;
import org.eclipse.tml.common.utilities.exception.TmLExceptionHandler;
import org.eclipse.tml.framework.device.DevicePlugin;
import org.eclipse.tml.framework.device.exception.DeviceExceptionHandler;
import org.eclipse.tml.framework.device.exception.DeviceExceptionStatus;
import org.eclipse.tml.framework.device.factory.InstanceRegistry;
import org.eclipse.tml.framework.device.model.IDevice;
import org.eclipse.tml.framework.device.model.IInstance;
import org.eclipse.tml.framework.device.model.IInstanceBuilder;
import org.eclipse.tml.framework.device.model.handler.IDeviceHandler;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 * Manages the device instances
 * 
 * @author Fabio Fantato
 */
public class InstanceManager {

	class Device {
		private String id;
		private String plugin;

		public Device(String id, String plugin) {
			this.id = id;
			this.plugin = plugin;
		}

		public String getId() {
			return id;
		}

		public String getPlugin() {
			return plugin;
		}

		public void setId(String id) {
			this.id = id;
		}

		public void setPlugin(String plugin) {
			this.plugin = plugin;
		}

	}

	private static final String TML_DEVICE_DATAFILE = "tml_devices.xml";
	private static final String TML_XML_DEVICES = "devices";
	private static final String TML_XML_DEVICE = "device";
	private static final String TML_XML_DEVICE_ID = "id";
	private static final String TML_XML_DEVICE_PLUGIN = "plugin";
	private static final String TML_XML_INSTANCES = "instances";
	private static final String TML_XML_INSTANCE = "instance";
	private static final String TML_XML_INSTANCE_NAME = "name";
	private static final String TML_XML_INSTANCE_DEVICE_ID = "device_id";
	private static final String TML_XML_ROOT = "tml";

	private static final String ELEMENT_DEVICE = "device";
	private static final String ATTR_HANDLER = "handler";

	private static InstanceManager _instance;
	private IInstance currentInstance;

	// member field to store list of devices defined in tml_devices.xml or
	// derives from instances
	private Map<String, Device> devices = new HashMap<String, Device>();

	/**
	 * Constructor - Manages the device instances
	 */
	private InstanceManager() {
		loadInstances();
		IWorkbench workbench = DevicePlugin.getDefault().getWorkbench();

		workbench.addWindowListener(new WindowListener(this));
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
		private InstanceManager instanceManager = null;

		private WindowListener(InstanceManager im) {
			instanceManager = im;
		}

		public void windowClosed(IWorkbenchWindow window) {
			instanceManager.saveInstances();
		}

		public void windowOpened(IWorkbenchWindow window) {

		}

		public void windowDeactivated(IWorkbenchWindow window) {

		}

		public void windowActivated(IWorkbenchWindow window) {

		}
	}

	/**
	 * Performs the load from XML operation during the IDE startup.
	 */
	private void loadInstances() {

		InstanceRegistry registry = InstanceRegistry.getInstance();

		File path = DevicePlugin.getWorkspaceRoot().getLocation().toFile();
		File file = new File(path, InstanceManager.TML_DEVICE_DATAFILE);

		if (file.exists()) {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			try {
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document document = builder.parse(file);
				Element rootElement = document.getDocumentElement();

				// parse list of devices
				NodeList devicesNodes = rootElement
						.getElementsByTagName(InstanceManager.TML_XML_DEVICES);
				if (devicesNodes != null && devicesNodes.getLength() == 1) {
					Node node = devicesNodes.item(0);
					if (node.getNodeType() == Node.ELEMENT_NODE) {
						Element devicesElement = (Element) node;
						NodeList deviceNodeList = devicesElement
								.getElementsByTagName(InstanceManager.TML_XML_DEVICE);
						for (int i = 0; deviceNodeList != null
								&& i < deviceNodeList.getLength(); i++) {
							Node devicedNode = deviceNodeList.item(i);
							if (devicedNode.getNodeType() == Node.ELEMENT_NODE) {
								Element deviceElement = (Element) devicedNode;
								String deviceId = deviceElement
										.getAttribute(InstanceManager.TML_XML_DEVICE_ID);
								String plugin = ""; //$NON-NLS-1$
								NodeList devicePluginNodes = deviceElement
										.getElementsByTagName(InstanceManager.TML_XML_DEVICE_PLUGIN);
								if (devicePluginNodes != null
										&& devicePluginNodes.getLength() >= 1) {
									Node devicePluginNode = devicePluginNodes
											.item(0);
									Node firstChild = devicePluginNode
											.getFirstChild();
									if (firstChild != null
											&& firstChild.getNodeType() == Node.TEXT_NODE) {
										plugin = firstChild.getNodeValue();
									}

								}

								Device device = new Device(deviceId, plugin);
								devices.put(deviceId, device);
							}
						}

					}
				}

				// parse list of instances
				NodeList instancesNodes = rootElement
						.getElementsByTagName(InstanceManager.TML_XML_INSTANCES);
				if (instancesNodes != null && instancesNodes.getLength() == 1) {
					Node node = instancesNodes.item(0);
					if (node.getNodeType() == Node.ELEMENT_NODE) {
						Element instancesElement = (Element) node;
						NodeList instanceNodeList = instancesElement
								.getElementsByTagName(InstanceManager.TML_XML_INSTANCE);
						for (int i = 0; instanceNodeList != null
								&& i < instanceNodeList.getLength(); i++) {

							Node childNode = instanceNodeList.item(i);
							if (childNode.getNodeType() == Node.ELEMENT_NODE) {
								Element childElement = (Element) childNode;
								String xml_device_id = childElement
										.getAttribute(InstanceManager.TML_XML_INSTANCE_DEVICE_ID);
								String instanceName = childElement
										.getAttribute(InstanceManager.TML_XML_INSTANCE_NAME);

								Properties prop = new Properties();
								NodeList propList = childElement
										.getChildNodes();

								for (int j = 0; propList != null
										&& j < propList.getLength(); j++) {
									Node propNode = propList.item(j);
									if (propNode.getNodeType() == Node.ELEMENT_NODE) {
										Element propElement = (Element) propNode;
										prop.put(propElement.getTagName(),
												propElement.getTextContent());
									}
								}
								IInstance inst = null;
								if (instanceName != null
										&& !instanceName.equals("") //$NON-NLS-1$
										&& xml_device_id != null
										&& !xml_device_id.equals("")) { //$NON-NLS-1$
									try {
										inst = createInstance(instanceName,
												xml_device_id,
												DevicePlugin.TML_STATUS_OFF,
												prop);
										registry.addInstance(inst);
									} catch (TmLException te) {
										TmLExceptionHandler
												.showException(DeviceExceptionHandler
														.exception(DeviceExceptionStatus.CODE_ERROR_HANDLER_NOT_INSTANCED));
									}
								}

								if (null == currentInstance) {
									currentInstance = inst;
								}

							}

						}
					}

				}

			} catch (IOException ie) {
				ie.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * Part of saveInstances
	 * 
	 * @param file -
	 *            The File object representing the XML file.
	 * @return The DOM document representing the XML file.
	 */
	private Document createDocument(File file) {

		Document document = null;
		if (file != null) {
			try {
				DocumentBuilderFactory factory = DocumentBuilderFactory
						.newInstance();
				factory.setNamespaceAware(true);
				DocumentBuilder builder = factory.newDocumentBuilder();
				DOMImplementation impl = builder.getDOMImplementation();

				// Create the document
				document = impl.createDocument(null,
						InstanceManager.TML_XML_ROOT, null);

			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
		}
		return document;
	}

	/**
	 * Creates DOM Element which represents all the devices used by instances.
	 * called by saveInstances().
	 */
	private Element createDevicesElement(Document document) {
		Element devicesElement = document
				.createElement(InstanceManager.TML_XML_DEVICES);
		Element root = document.getDocumentElement();
		root.appendChild(devicesElement);

		updateDevicesFromInstances();
		Iterator<Device> iterator = devices.values().iterator();
		while (iterator.hasNext()) {
			Device device = iterator.next();
			Element deviceElement = document
					.createElement(InstanceManager.TML_XML_DEVICE);
			deviceElement.setAttribute(InstanceManager.TML_XML_DEVICE_ID,
					device.getId());
			devicesElement.appendChild(deviceElement);

			Element devicePluginElement = document
					.createElement(InstanceManager.TML_XML_DEVICE_PLUGIN);
			Text pluginNode = document.createTextNode(device.getPlugin());
			devicePluginElement.appendChild(pluginNode);
			devicesElement.appendChild(devicePluginElement);
		}
		return devicesElement;
	}

	/**
	 * Updates the devices member field so it contains all the devices used by
	 * instances. called by createDevicesElement().
	 */
	// TO-DO: probably should revisit this later since right now in the code it
	// just uses the device field from
	// IInstance as both the device_id and plugin. Should decide if the devices
	// is useful in the xml.
	private void updateDevicesFromInstances() {
		InstanceRegistry registry = InstanceRegistry.getInstance();
		Iterator<IInstance> iterator = registry.getInstances().iterator();
		while (iterator.hasNext()) {
			IInstance iIInst = iterator.next();
			String deviceId = iIInst.getDevice();
			if (!devices.containsKey(deviceId)) {
				Device device = new Device(deviceId, deviceId);
				devices.put(deviceId, device);
			}
		}
	}

	/**
	 * Creates DOM Element which represents all the instances defined. called by
	 * saveInstances().
	 */
	private Element createInstancesElement(Document document) {
		Element instancesRoot = document
				.createElement(InstanceManager.TML_XML_INSTANCES);
		InstanceRegistry registry = InstanceRegistry.getInstance();
		Iterator<IInstance> iterator = registry.getInstances().iterator();
		while (iterator.hasNext()) {
			IInstance iIInst = iterator.next();
			Element element = document
					.createElement(InstanceManager.TML_XML_INSTANCE);
			element.setAttribute(InstanceManager.TML_XML_INSTANCE_NAME, iIInst
					.getName());
			String xml_device_id = iIInst.getDevice();

			element.setAttribute(InstanceManager.TML_XML_INSTANCE_DEVICE_ID,
					xml_device_id);
			if (element != null)
				instancesRoot.appendChild(element);
			Properties propProp = iIInst.getProperties();

			for (Enumeration<?> e = propProp.keys(); e.hasMoreElements();) {
				String propStr = (String) e.nextElement();
				String propValStr = propProp.getProperty(propStr);

				Element propElement = document.createElement(propStr);
				Text propNode = document.createTextNode(propValStr);
				propElement.appendChild(propNode);
				element.appendChild(propElement);

			}

		}
		return instancesRoot;
	}

	/**
	 * Stores the instance data on a XML file located in the workspace root.
	 */
	private void saveInstances() {

		File path = DevicePlugin.getWorkspaceRoot().getLocation().toFile();
		File file = new File(path, InstanceManager.TML_DEVICE_DATAFILE);

		Document document = createDocument(file);

		if (document != null) {

			try {
				Element root = document.getDocumentElement();
				Element instancesRoot = createInstancesElement(document);
				root.appendChild(instancesRoot);

				Element devicesRoot = createDevicesElement(document);
				root.appendChild(devicesRoot);

				Transformer transformer = TransformerFactory.newInstance()
						.newTransformer();
				transformer.setOutputProperty(OutputKeys.METHOD, "xml"); //$NON-NLS-1$
				transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
				transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8"); //$NON-NLS-1$

				// initialize FileOutputStream with File object to save to file
				FileOutputStream outputStream = new FileOutputStream(file
						.getAbsoluteFile());
				StreamResult result = new StreamResult(outputStream);
				DOMSource source = new DOMSource(document);
				transformer.transform(source, result);
				outputStream.close();

			} catch (IOException ie) {
				ie.printStackTrace();
			} catch (TransformerConfigurationException e) {
				e.printStackTrace();
			} catch (TransformerFactoryConfigurationError e) {
				e.printStackTrace();
			} catch (TransformerException e) {
				e.printStackTrace();
			}
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
			IExtension fromPlugin = PluginUtils.getExtension(
					DevicePlugin.DEVICE_ID, deviceId);
			deviceHandler = (IDeviceHandler) PluginUtils
					.getExecutableAttribute(fromPlugin, ELEMENT_DEVICE,
							ATTR_HANDLER);
			// getExecutable(DevicePlugin.DEVICE_ID, deviceId);
			instance = deviceHandler.createDeviceInstance(name + deviceId);
			instance.setDevice(deviceId);
			instance.setName(name);
			instance.setStatus(status);
			instance.setProperties((Properties) properties.clone());
		} catch (CoreException ce) {
			TmLExceptionHandler
					.showException(DeviceExceptionHandler
							.exception(DeviceExceptionStatus.CODE_ERROR_HANDLER_NOT_INSTANCED));
		}
		return instance;
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
	public void createProject(IDevice device, IInstanceBuilder projectBuilder,
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
			registry.setDirty(true);
		} catch (TmLException te) {
			TmLExceptionHandler
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
}
