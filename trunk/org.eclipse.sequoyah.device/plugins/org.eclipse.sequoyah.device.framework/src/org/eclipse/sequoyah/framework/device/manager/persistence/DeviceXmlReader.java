/********************************************************************************
 * Copyright (c) 2008, 2009 Motorola Inc and others. All rights reserved
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Fabio Rigo (Eldorado Research Institute)
 * [245114] Enhance persistence policies
 * Mauren Brenner (Eldorado) - Bug [280813] - Support saving instance info outside the workspace
 * Fabio Rigo (Eldorado) - Bug [288006] - Unify features of InstanceManager and InstanceRegistry
 ********************************************************************************/


package org.eclipse.tml.framework.device.manager.persistence;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.tml.common.utilities.exception.ExceptionHandler;
import org.eclipse.tml.common.utilities.exception.TmLException;
import org.eclipse.tml.framework.device.DevicePlugin;
import org.eclipse.tml.framework.device.exception.DeviceExceptionHandler;
import org.eclipse.tml.framework.device.exception.DeviceExceptionStatus;
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
     * 
     * @return A collection containing all loaded instances
     */
    public static Collection<IInstance> loadInstances() {
        File path = DevicePlugin.getDeviceXmlLocation();
        File file = new File(path, TML_DEVICE_DATAFILE);
        Collection<IInstance> loadedInstances = new ArrayList<IInstance>();
        
        if (file.exists()) {
            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            try {
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(file);
                Element rootElement = document.getDocumentElement();
                
                loadedInstances.addAll(parseInstancesList(rootElement));
                
            } catch (IOException ie) {
                ie.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }
        }
        
        return loadedInstances;
    }
    
    private static Collection<IInstance> parseInstancesList(Element rootElement)
    {
    	Collection<IInstance> loadedInstances = new LinkedHashSet<IInstance>();
    	
    	// parse list of instances
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
                                inst = InstanceManager.createInstance(instanceName,
                                        xml_device_id,
                                        DevicePlugin.TML_STATUS_OFF,
                                        prop);
                                loadedInstances.add(inst);
                            } catch (TmLException te) {
                                ExceptionHandler
                                        .showException(DeviceExceptionHandler
                                                .exception(DeviceExceptionStatus.CODE_ERROR_HANDLER_NOT_INSTANCED));
                            }
                        }
                    }
                }
            }
        }
        
        return loadedInstances;
    }
}
