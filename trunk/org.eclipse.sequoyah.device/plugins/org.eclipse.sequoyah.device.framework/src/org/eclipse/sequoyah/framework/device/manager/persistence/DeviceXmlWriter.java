/********************************************************************************
 * Copyright (c) 2008 Motorola Inc and others. All rights reserved
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Fabio Rigo (Eldorado Research Institute) 
 * [245114] Enhance persistence policies
 * Yu-Fen Kuo (MontaVista)  - [236476] - provide a generic device type
 * Daniel Barboza Franco (Eldorado Research Institute) - Bug [271695] - Support to non-persistent instances of devices
 ********************************************************************************/

package org.eclipse.tml.framework.device.manager.persistence;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
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

import org.eclipse.core.runtime.IPath;
import org.eclipse.tml.framework.device.DevicePlugin;
import org.eclipse.tml.framework.device.factory.DeviceTypeRegistry;
import org.eclipse.tml.framework.device.factory.InstanceRegistry;
import org.eclipse.tml.framework.device.model.IDeviceType;
import org.eclipse.tml.framework.device.model.IInstance;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class DeviceXmlWriter implements IDeviceXmlTags
{
    /**
     * Stores the instance data on a XML file located in the workspace root.
     */
    public static void saveInstances(Map<String, TmLDevice> devices) {

        IPath stateLocationPath = DevicePlugin.getDefault().getStateLocation();
        File path = stateLocationPath.toFile();
        File file = new File(path, TML_DEVICE_DATAFILE);

        Document document = createDocument(file);

        if (document != null) {

            try {
                Element root = document.getDocumentElement();
                Element instancesRoot = createInstancesElement(document);
                root.appendChild(instancesRoot);

                Element devicesRoot = createDevicesElement(document, devices);
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
     * Part of saveInstances
     * 
     * @param file -
     *            The File object representing the XML file.
     * @return The DOM document representing the XML file.
     */
    private static Document createDocument(File file) {

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
                        TML_XML_ROOT, null);

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
    private static Element createDevicesElement(Document document, Map<String, TmLDevice> devices) {
        Element devicesElement = document
                .createElement(TML_XML_DEVICES);
        Element root = document.getDocumentElement();
        root.appendChild(devicesElement);

        updateDevicesFromInstances(devices);
        Iterator<TmLDevice> iterator = devices.values().iterator();
        while (iterator.hasNext()) {
            TmLDevice device = iterator.next();
            Element deviceElement = document
                    .createElement(TML_XML_DEVICE);
            deviceElement.setAttribute(TML_XML_DEVICE_ID,
                    device.getId());
            devicesElement.appendChild(deviceElement);

            Element devicePluginElement = document
                    .createElement(TML_XML_DEVICE_PLUGIN);
            Text pluginNode = document.createTextNode(device.getPlugin());
            devicePluginElement.appendChild(pluginNode);
            devicesElement.appendChild(devicePluginElement);
        }
        return devicesElement;
    }
    
    /**
     * Creates DOM Element which represents all the instances defined. called by
     * saveInstances().
     */
    private static Element createInstancesElement(Document document) {
        Element instancesRoot = document
                .createElement(TML_XML_INSTANCES);
        InstanceRegistry registry = InstanceRegistry.getInstance();
        Iterator<IInstance> iterator = registry.getInstances().iterator();
   
        while (iterator.hasNext()) {
   
        	IInstance iIInst = iterator.next();
            IDeviceType device = DeviceTypeRegistry.getInstance().getDeviceTypeById(iIInst.getDeviceTypeId());

            //check if this instance should be persisted
            if (device.isPersistent()) {
           
	            Element element = document
	                    .createElement(TML_XML_INSTANCE);
	            element.setAttribute(TML_XML_INSTANCE_NAME, iIInst
	                    .getName());
	            String xml_device_id = iIInst.getDeviceTypeId();
	
	            element.setAttribute(TML_XML_INSTANCE_DEVICE_ID,
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

        }
        return instancesRoot;
    }
    
    /**
     * Updates the devices member field so it contains all the devices used by
     * instances. called by createDevicesElement().
     */
    // TO-DO: probably should revisit this later since right now in the code it
    // just uses the device field from
    // IInstance as both the device_id and plugin. Should decide if the devices
    // is useful in the xml.
    private static void updateDevicesFromInstances(Map<String, TmLDevice> devices) {
        InstanceRegistry registry = InstanceRegistry.getInstance();
        Iterator<IInstance> iterator = registry.getInstances().iterator();
        while (iterator.hasNext()) {
            IInstance iIInst = iterator.next();
            String deviceId = iIInst.getDeviceTypeId();
            if (!devices.containsKey(deviceId)) {
                TmLDevice device = new TmLDevice(deviceId, deviceId);
                devices.put(deviceId, device);
            }
        }
    }
}
