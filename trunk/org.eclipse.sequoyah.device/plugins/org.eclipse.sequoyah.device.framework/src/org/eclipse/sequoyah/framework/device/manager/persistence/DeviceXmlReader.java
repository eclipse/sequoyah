/********************************************************************************
 * Copyright (c) 2008 Motorola Inc and others. All rights reserved
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Fabio Rigo (Eldorado Research Institute) 
 * [245114] Enhance persistence policies
 ********************************************************************************/


package org.eclipse.tml.framework.device.manager.persistence;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.tml.common.utilities.exception.ExceptionHandler;
import org.eclipse.tml.common.utilities.exception.TmLException;
import org.eclipse.tml.framework.device.DevicePlugin;
import org.eclipse.tml.framework.device.exception.DeviceExceptionHandler;
import org.eclipse.tml.framework.device.exception.DeviceExceptionStatus;
import org.eclipse.tml.framework.device.factory.InstanceRegistry;
import org.eclipse.tml.framework.device.manager.InstanceManager;
import org.eclipse.tml.framework.device.model.IInstance;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DeviceXmlReader implements IDeviceXmlTags
{
    /**
     * Performs the load from XML operation during the IDE startup.
     */
    public static void loadInstances(InstanceManager manager) {

        IPath stateLocationPath = DevicePlugin.getDefault().getStateLocation();
        File path = stateLocationPath.toFile();
        File file = new File(path, TML_DEVICE_DATAFILE);

        if (file.exists()) {
            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            try {
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(file);
                Element rootElement = document.getDocumentElement();

                parseDevicesList(rootElement, manager);
                parseInstancesList(rootElement, manager);
                
            } catch (IOException ie) {
                ie.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }
        }
    };
    
    private static void parseDevicesList(Element rootElement, InstanceManager manager)
    {
        Map<String, TmLDevice> devices = new HashMap<String, TmLDevice>();
        manager.setDevicesMap(devices);
        
     // parse list of devices
        NodeList devicesNodes = rootElement
                .getElementsByTagName(TML_XML_DEVICES);
        if (devicesNodes != null && devicesNodes.getLength() == 1) {
            Node node = devicesNodes.item(0);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element devicesElement = (Element) node;
                NodeList deviceNodeList = devicesElement
                        .getElementsByTagName(TML_XML_DEVICE);
                for (int i = 0; deviceNodeList != null
                        && i < deviceNodeList.getLength(); i++) {
                    Node devicedNode = deviceNodeList.item(i);
                    if (devicedNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element deviceElement = (Element) devicedNode;
                        String deviceId = deviceElement
                                .getAttribute(TML_XML_DEVICE_ID);
                        String plugin = ""; //$NON-NLS-1$
                        NodeList devicePluginNodes = deviceElement
                                .getElementsByTagName(TML_XML_DEVICE_PLUGIN);
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

                        TmLDevice device = new TmLDevice(deviceId, plugin);
                        devices.put(deviceId, device);
                    }
                }

            }
        }
    }
    
    private static void parseInstancesList(Element rootElement, InstanceManager manager)
    {
     // parse list of instances
        InstanceRegistry registry = InstanceRegistry.getInstance();
        NodeList instancesNodes = rootElement
                .getElementsByTagName(TML_XML_INSTANCES);
        if (instancesNodes != null && instancesNodes.getLength() == 1) {
            Node node = instancesNodes.item(0);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element instancesElement = (Element) node;
                NodeList instanceNodeList = instancesElement
                        .getElementsByTagName(TML_XML_INSTANCE);
                for (int i = 0; instanceNodeList != null
                        && i < instanceNodeList.getLength(); i++) {

                    Node childNode = instanceNodeList.item(i);
                    if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element childElement = (Element) childNode;
                        String xml_device_id = childElement
                                .getAttribute(TML_XML_INSTANCE_DEVICE_ID);
                        String instanceName = childElement
                                .getAttribute(TML_XML_INSTANCE_NAME);

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
                                inst = manager.createInstance(instanceName,
                                        xml_device_id,
                                        DevicePlugin.TML_STATUS_OFF,
                                        prop);
                                registry.addInstance(inst);
                            } catch (TmLException te) {
                                ExceptionHandler
                                        .showException(DeviceExceptionHandler
                                                .exception(DeviceExceptionStatus.CODE_ERROR_HANDLER_NOT_INSTANCED));
                            }
                        }

                        if (manager.getCurrentInstance() == null) {
                            manager.setInstance(inst);
                        }

                    }

                }
            }

        }
    }
}
