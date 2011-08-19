/********************************************************************************
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Lucas Tiago de Castro Jesus (GSoC)
 * 
 * Contributors:
 * Name (Company) - [Bug #] - Description
 ********************************************************************************/
package org.eclipse.sequoyah.localization.pde.manager;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.sequoyah.device.common.utilities.BasePlugin;
import org.eclipse.sequoyah.device.common.utilities.exception.SequoyahException;
import org.eclipse.sequoyah.device.common.utilities.exception.SequoyahExceptionStatus;
import org.eclipse.sequoyah.localization.pde.IPDELocalizationSchemaConstants;
import org.eclipse.sequoyah.localization.pde.PDELocalizationPlugin;
import org.eclipse.sequoyah.localization.pde.datamodel.PDEStringLocalizationFile;
import org.eclipse.sequoyah.localization.pde.i18n.Messages;
import org.eclipse.sequoyah.localization.tools.datamodel.LocalizationFile;
import org.eclipse.sequoyah.localization.tools.datamodel.LocalizationFileBean;
import org.eclipse.sequoyah.localization.tools.datamodel.LocalizationFileFactory;
import org.eclipse.sequoyah.localization.tools.datamodel.StringLocalizationFile;
import org.eclipse.sequoyah.localization.tools.datamodel.node.StringNode;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class deals specifically with localized String / text content.
 * 
 */
public class StringLocalizationFileManager extends ILocalizationFileManager
		implements IPDELocalizationSchemaConstants {

	/*
	 * Static code to add this manager to factory's hashmap
	 */
	static {
		LocalizationFileManagerFactory.getInstance().addManager(
				StringLocalizationFileManager.class.getName(),
				StringLocalizationFileManager.class);
	}

	/**
	 * Default constructor.
	 */
	public StringLocalizationFileManager() {
	}

	/**
	 * StringLocalizationFileManager knows how to create itself.
	 * 
	 * @return StringLocalizationFileManager created
	 */
	public static ILocalizationFileManager create() {
		return new StringLocalizationFileManager();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.sequoyah.localization.android.manager.ILocalizationFileManager
	 * #
	 * loadFile(org.eclipse.sequoyah.localization.tools.datamodel.LocalizationFile
	 * )
	 */
	@Override
	public LocalizationFile loadFile(LocalizationFile locFile)
			throws SequoyahException {
		Properties property = new Properties();
		
		if (!locFile.getFile().exists()) {
			
			LocalizationFileBean bean = new LocalizationFileBean(locFile);
			bean.setType(StringLocalizationFile.class.getName());
			for (NodeManager nodeManager : NodeManagerProvider.getInstance()
					.getNodeManagers()) {
				// according to NodeManagerProvider, the first element is a
				// StringNodeManager and the second is an ArrayStringNodeManager
				
				nodeManager.loadFile(bean, locFile);
			}

			LocalizationFile tempFile = LocalizationFileFactory.getInstance()
					.createLocalizationFile(bean);
			try {
				createFile(tempFile);
			} catch (SequoyahException e) {
				BasePlugin.getLogger().error(
						"Could not create StringLocalizationFile: ", e); //$NON-NLS-1$
			}
		}
	
		try {
			property.load(new FileInputStream(locFile.getFile().getLocation().toString()));
			updateLocalizationFileContent(locFile, property);
		} catch (Exception e) {			
			SequoyahExceptionStatus status = new SequoyahExceptionStatus(
					IStatus.ERROR,
					PDELocalizationPlugin.PLUGIN_ID,
					0,
					Messages.StringLocalizationFileManager_Exception_CouldNotLoadFile
							+ locFile.getFile().getFullPath().toOSString()
							+ ". " + e.getMessage(), e); //$NON-NLS-1$
			throw new SequoyahException(status);
		}
		
		updateLocalizationFileContent(locFile, property);
		
		return locFile;
	}

	/*
	 * 
	 */
	
	private void updateLocalizationFileContent(
			LocalizationFile localizationFile, Properties property) {
		
		
		ArrayList<StringNode> stringNodes = new ArrayList<StringNode>();
		for (NodeManager nodeManager : NodeManagerProvider.getInstance()
				.getNodeManagers()) {
			
			if (nodeManager instanceof StringNodeManager) {
				nodeManager
						.updateLocalizationFileContent(property, stringNodes);
			}
		}

		((PDEStringLocalizationFile) localizationFile)
				.setSavedPDEProperty(property);

		((PDEStringLocalizationFile) localizationFile)
				.clearStringNodes();
		
		((PDEStringLocalizationFile) localizationFile)
				.setStringNodes(stringNodes);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.sequoyah.localization.android.manager.ILocalizationFileManager
	 * #
	 * createFile(org.eclipse.sequoyah.localization.tools.datamodel.LocalizationFile
	 * )
	 */
	@Override
	public void createFile(LocalizationFile localizationFile)
			throws SequoyahException {
		try {
			String filePath = localizationFile.getFile().getFullPath()
					.toOSString();

			if (!localizationFile.getFile().exists()) {
				
				localizationFile.getFile().getLocation();
				
				IPath fileToSave = null;
				if (localizationFile.getLocalizationProject() != null) {
					fileToSave = new Path(localizationFile
							.getLocalizationProject().getProject()
							.getLocation()
							+ filePath);
				} else {
					fileToSave = localizationFile.getFile().getLocation();
				}
				//TODO warning
				//fileToSave.removeLastSegments(1).toFile().mkdirs();
				fileToSave.removeLastSegments(1).toFile().mkdirs();
								
				fileToSave.toFile().createNewFile();

				if (localizationFile.getLocalizationProject() != null) {
					IFile iFile = localizationFile.getLocalizationProject()
							.getProject().getFile(new Path(filePath));
					localizationFile.setFile(iFile);
				}
			}
			
			Properties property = new Properties();
			
			Element resources = null;

			for (NodeManager nodeManager : NodeManagerProvider.getInstance()
					.getNodeManagers()) {
				// according to NodeManagerProvider, the first element is a
				// StringNodeManager and the second is an ArrayStringNodeManager
				nodeManager.createFile(property, resources, localizationFile);
			}
			
			savePDEProperty(localizationFile.getFile().getLocation().toFile(),
					property);
			((PDEStringLocalizationFile) localizationFile)
					.setSavedPDEProperty(property);
			localizationFile
					.getFile()
					.getProject()
					.refreshLocal(IResource.DEPTH_INFINITE,
							new NullProgressMonitor());

			
		} catch (Exception e) {
			throw new SequoyahException();
		}
	}

	/**
	 * Saves PDE file into the file system
	 * 
	 * @param file
	 * @param property
	 * @throws IOException 
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerConfigurationException
	 * @throws TransformerException
	 */
	private void savePDEProperty(File file, Properties property)
			throws  IOException{
				
		BufferedWriter fileOutputStream = null;
		/*
		 * At this point, localization file has the correct file
		 */
		fileOutputStream = new BufferedWriter(new FileWriter(file));		
		
		try {
			//TODO Complete here
			String result = getPropertyAsString(property);
			result.replaceAll("\n",System.getProperty("line.separator"));
			
			fileOutputStream.write(result);
		} finally {
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					// do nothing
				}
			}
		}

	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.sequoyah.localization.android.manager.ILocalizationFileManager
	 * #
	 * updateFile(org.eclipse.sequoyah.localization.tools.datamodel.LocalizationFile
	 * )
	 */
	@Override
	//TODO Quem chama esse updateFile deve ser corrigido para passar o path certo
	public void updateFile(LocalizationFile locFile) throws SequoyahException {
		PDEStringLocalizationFile pdeLocalizationFile = (PDEStringLocalizationFile) locFile;
		
		Properties property = pdeLocalizationFile.getSavedPDEProperty();
		updateFile(pdeLocalizationFile, property);
		if (property == null) {
			property = pdeLocalizationFile.getSavedPDEProperty();
		}
		// save modified document
		try {
			savePDEProperty(locFile.getFile().getLocation().toFile(), property);
		} catch (Exception e) {
			SequoyahException sqE = new SequoyahException();
			sqE.setStackTrace(e.getStackTrace());
			throw sqE;
		}
		locFile.setDirty(false);
	}

	/**
	 * Update the Property representation of the file
	 * 
	 * @param locFile
	 * @param document
	 * @throws SequoyahException
	 */
	
	private void updateFile(LocalizationFile locFile, Properties property)
			throws SequoyahException {
		
		PDEStringLocalizationFile PDELocalizationFile = (PDEStringLocalizationFile) locFile;
		if (property == null) {
			// file not created yet, do it
			
			createFile(PDELocalizationFile);
			property = PDELocalizationFile.getSavedPDEProperty();
		} else {
			// file already created, update
			// update nodes on Property according to LocalizationFile model
			Map<String, StringNode> singleStringsToUpdateOrAdd = new HashMap<String, StringNode>();
			
			for (StringNode stringNode : ((StringLocalizationFile) locFile)
					.getStringNodes()){
				if (stringNode instanceof StringNode){
					singleStringsToUpdateOrAdd.put(stringNode.getKey(),
							stringNode);
				}
			}
			
			visitToUpdateDOMChildren(property, null,"", singleStringsToUpdateOrAdd);
			visitToRemoveDOMChildren(property, null,"", PDELocalizationFile.getSingleEntryToRemove());
			visitToAddDOMChildren(property, singleStringsToUpdateOrAdd, null);
		}
	}

	/**
	 * 
	 * Visit all child in the DOM tree to add an attribute.
	 * 
	 * @param document
	 * @param singleStringsToUpdateOrAdd
	 *            map with single items to update/add (in the end of visit, only
	 *            add items should remain)
	 * @param arrayStringsToUpdateOrAdd
	 *            map with array item to update/add (in the end of visit, only
	 *            add item should
	 * @param resource
	 */
	
	public void visitToAddDOMChildren(Properties property,
			Map<String, StringNode> singleStringsToUpdateOrAdd,
			Element resource) {
		for (Map.Entry<String, StringNode> singleEntry : singleStringsToUpdateOrAdd
				.entrySet()) {
			StringNode stringNode = singleEntry.getValue();
			
			StringNodeManager stMgr = NodeManagerProvider.getInstance()
					.getStringNodeManager();
			stMgr.addSingleEntry(property, resource, stringNode);
		}	
	}

	/**
	 * 
	 * Visit all child in the DOM tree, as an attribute is found, it gets the
	 * corresponding item on LocalizationFile and updates the values
	 * 
	 * @param document
	 * @param visitingNode
	 * @param attrName
	 * @param singleStringsToRemove
	 *            map with single items to remove (in the end of visit, only add
	 *            items should remain)
	 */
	public void visitToRemoveDOMChildren(Document document, Node visitingNode,
			String attrName, Map<String, StringNode> singleStringsToRemove) {
		while (visitingNode != null) {
			Node nextNodeToVisit = visitingNode.getNextSibling();
			if (visitingNode.getNodeType() == Node.ELEMENT_NODE) {
				Attr attribute = getAttribute((Element) visitingNode, attrName);
				// check LocalizationFile to get new value for attribute
				if (attribute != null) {
					String keyName = attribute.getValue();
					// try to find as single string entry
					StringNode foundStringNode = singleStringsToRemove
							.get(keyName);
					if (foundStringNode != null) {
						// remove single entry
						visitingNode.getParentNode().removeChild(visitingNode);
					}					
				}
			}
			visitingNode = nextNodeToVisit;
		}
	}
	
	public void visitToRemoveDOMChildren(Properties property, Node visitingNode,
			String attrName, Map<String, StringNode> singleStringsToRemove) {
		for (Enumeration keyProperties = property.propertyNames(); keyProperties.hasMoreElements();) {
			String key = (String) keyProperties.nextElement();
			StringNode foundStringNode = singleStringsToRemove
				.get(key);
			if (foundStringNode != null) {

				property.remove(key);				
				// remove item from map (it was already updated and it
				// does
				// not need to add in the end)
				singleStringsToRemove.remove(key);
			}
			
	     }	
	}
	
	public void visitToUpdateDOMChildren(Document document, Node visitingNode,
			String attrName,
			Map<String, StringNode> singleStringsToUpdateOrAdd) {
		
		
		while (visitingNode != null) {
			if (visitingNode.getNodeType() == Node.ELEMENT_NODE) {
				Attr attribute = getAttribute((Element) visitingNode, attrName);
				if (attribute != null) {
					// check LocalizationFile to get new value for attribute
					String keyName = attribute.getValue();
					// try to find as single string entry
					StringNode foundStringNode = singleStringsToUpdateOrAdd
							.get(keyName);
					if (foundStringNode != null) {
						// update single entry
						
						String newSingleEntryValue = foundStringNode.getValue();
						visitingNode.setTextContent(newSingleEntryValue);
						StringNodeManager stMgr = NodeManagerProvider
								.getInstance().getStringNodeManager();
						// remove item from map (it was already updated and it
						// does
						// not need to add in the end)
						singleStringsToUpdateOrAdd.remove(keyName);
					}
				}
			}
			visitingNode = visitingNode.getNextSibling();
		}
	}
	
	public void visitToUpdateDOMChildren(Properties property, Node visitingNode,
			String attrName,
			Map<String, StringNode> singleStringsToUpdateOrAdd) {
		
		for (Enumeration keyProperties = property.propertyNames(); keyProperties.hasMoreElements();) {
			String key = (String) keyProperties.nextElement();
			StringNode foundStringNode = singleStringsToUpdateOrAdd
				.get(key);
			if (foundStringNode != null) {
				// update single entry
				
				String newSingleEntryValue = foundStringNode.getValue();
				property.setProperty(key,newSingleEntryValue);				
				// remove item from map (it was already updated and it
				// does
				// not need to add in the end)
				singleStringsToUpdateOrAdd.remove(key);
			}
			
	     }	
	}


	/**
	 * Get Attribute with the given name
	 * 
	 * @param el
	 *            visiting element
	 * @param attrName
	 *            attribute name to search
	 * @return attribute
	 */
	public static Attr getAttribute(Element el, String attrName) {
		Attr attr = el.getAttributeNode(attrName);
		return attr;
	}

	/*
	 * Get PropertieAsString
	 */
	private String getPropertyAsString(Properties property) {
		String resultString = null;
		try {			
			resultString = "";
			for (Enumeration keyProperties = property.propertyNames(); keyProperties.hasMoreElements();) {
				String keyProperty = (String) keyProperties.nextElement();
				String valueProperty = property.getProperty(keyProperty);
				
				valueProperty = valueProperty.replace("\r","");
				resultString += keyProperty + " = " + valueProperty.replace("\n", "\\n\\" + "\n") + "\n";
		     }

		} catch (Exception e) {
			e.printStackTrace();
		}

		return resultString;
	}
	
	/**
	 * Text nodes are removed from document
	 * 
	 * @param document
	 *            Properties document to be edited
	 */
	void removePDEDocumentBlankNodes(Document document) {
		ArrayList<Node> nodesToRemove = new ArrayList<Node>();
		
		NodeList resourceNodeList = document.getElementsByTagName("resources"); //$NON-NLS-1$
		// exactly one resource node
		if (resourceNodeList.getLength() == 1) {
			Node resourceNode = resourceNodeList.item(0);
			NodeList nodeList = resourceNode.getChildNodes();

			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				nodesToRemove.add(node);				
			}
			// remove blank nodes
			for (Node current : nodesToRemove) {
				resourceNode.removeChild(current);
			}
		}
		return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.sequoyah.localization.android.manager.ILocalizationFileManager
	 * # updateLocalizationFileContent()
	 */
	@Override
	public void updateLocalizationFileContent(
			LocalizationFile localizationFile, String content)
			throws SequoyahException {

		Properties property;
		InputStream streamContent;
		try {
				property = new Properties();

				streamContent  = new ByteArrayInputStream(content.getBytes());
				property.load(streamContent);
				
				updateLocalizationFileContent(
					(StringLocalizationFile) localizationFile, property);
			
		} catch (Exception e) {
			SequoyahExceptionStatus status = new SequoyahExceptionStatus(
					IStatus.ERROR, PDELocalizationPlugin.PLUGIN_ID, 0,
					Messages.PDELocalizationSchema_6
							+ localizationFile.getFile().getFullPath(), e);
			throw new SequoyahException(status);
		}
	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.sequoyah.localization.android.manager.ILocalizationFileManager
	 * # getLocalizationFileContent()
	 */
	@Override
	public Object getLocalizationFileContent(LocalizationFile locFile) {
		String text = null;
		
		if (locFile instanceof PDEStringLocalizationFile) {
			PDEStringLocalizationFile localizationFile = (PDEStringLocalizationFile) locFile;

			try {
				updateFile(localizationFile,
						localizationFile.getSavedPDEProperty());
			} catch (SequoyahException e) {
				
			}
			text = getPropertyAsString(localizationFile.getSavedPDEProperty());	
		}
		
		return text;
	}
}