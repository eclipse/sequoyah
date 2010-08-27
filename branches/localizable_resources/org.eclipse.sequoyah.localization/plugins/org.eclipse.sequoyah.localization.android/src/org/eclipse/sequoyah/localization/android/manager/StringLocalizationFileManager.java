/********************************************************************************
 * Copyright (c) 2010 Motorola Mobility, Inc.
 * All rights reserved. This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Marcel Augusto Gorri (Eldorado) - Bug [323036] - Add support to other localizable resources
 * 
 ********************************************************************************/
package org.eclipse.sequoyah.localization.android.manager;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.sequoyah.device.common.utilities.BasePlugin;
import org.eclipse.sequoyah.device.common.utilities.exception.SequoyahException;
import org.eclipse.sequoyah.localization.android.IAndroidLocalizationSchemaConstants;
import org.eclipse.sequoyah.localization.android.LocalizationXMLParserFilter;
import org.eclipse.sequoyah.localization.android.datamodel.AndroidStringLocalizationFile;
import org.eclipse.sequoyah.localization.android.i18n.Messages;
import org.eclipse.sequoyah.localization.tools.datamodel.LocalizationFile;
import org.eclipse.sequoyah.localization.tools.datamodel.LocalizationFileBean;
import org.eclipse.sequoyah.localization.tools.datamodel.LocalizationFileFactory;
import org.eclipse.sequoyah.localization.tools.datamodel.StringLocalizationFile;
import org.eclipse.sequoyah.localization.tools.datamodel.node.NodeComment;
import org.eclipse.sequoyah.localization.tools.datamodel.node.StringArray;
import org.eclipse.sequoyah.localization.tools.datamodel.node.StringNode;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSParser;
import org.w3c.dom.ls.LSParserFilter;

/**
 * This class deals specifically with localized String / text content.
 * 
 */
public class StringLocalizationFileManager extends ILocalizationFileManager implements IAndroidLocalizationSchemaConstants{

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
	 * #loadFile(org.eclipse.sequoyah.localization.tools.datamodel.LocalizationFile)
	 */
	@Override
	public LocalizationFile loadFile(LocalizationFile locFile) {
		if (!locFile.getFile().exists()) {
			LocalizationFileBean bean = new LocalizationFileBean(locFile);
			bean.setStringNodes(((StringLocalizationFile)locFile).getStringNodes());
			bean.setStringArrays(((StringLocalizationFile)locFile).getStringArrays());
			LocalizationFile tempFile = LocalizationFileFactory.getInstance().createLocalizationFile(bean);
			try {
				createFile(tempFile);
			} catch (SequoyahException e) {
				BasePlugin.getLogger().error("Could not create StringLocalizationFile: ", e); //$NON-NLS-1$
			}
		}
		try {					
			InputStream inputStream = new FileInputStream(locFile.getFile().getLocation().toFile());
			DOMImplementation dimp = DOMImplementationRegistry.newInstance()
					.getDOMImplementation("XML 3.0"); //$NON-NLS-1$
			DOMImplementationLS dimpls = (DOMImplementationLS) dimp.getFeature("LS", "3.0"); //$NON-NLS-1$ //$NON-NLS-2$
			LSInput lsi = dimpls.createLSInput();
			LSParser lsp = dimpls.createLSParser(DOMImplementationLS.MODE_SYNCHRONOUS, 
					"http://www.w3.org/2001/XMLSchema"); //$NON-NLS-1$
			LSParserFilter filter = new LocalizationXMLParserFilter();
			lsp.setFilter(filter);
			lsi.setEncoding("UTF-8"); //$NON-NLS-1$
			lsi.setByteStream(inputStream);
			Document document = lsp.parse(lsi);
			
			// TODO 			
			updateLocalizationFileContent(locFile, document);

		} catch (Exception e) {
			throw new IOException(
					Messages.AndroidLocalizationSchema_Exception_CouldNotLoadFile
							+ locFile.getFile().getName() + ". " + e.getMessage()); //$NON-NLS-1$
		}
				
		
		
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.sequoyah.localization.android.manager.ILocalizationFileManager
	 * #createFile(org.eclipse.sequoyah.localization.tools.datamodel.LocalizationFile)
	 */
	@Override
	public void createFile(LocalizationFile localizationFile) {
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

				fileToSave.removeLastSegments(1).toFile().mkdirs();
				fileToSave.toFile().createNewFile();

				if (localizationFile.getLocalizationProject() != null) {
					IFile iFile = localizationFile.getLocalizationProject()
							.getProject().getFile(new Path(filePath));
					localizationFile.setFile(iFile);
				}
			}

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.newDocument();

			/*
			 * Create XML nodes
			 */
			Element resources = document.createElement(XML_RESOURCES_TAG);

			// Simple entries
			for (StringNode stringNode : ((StringLocalizationFile) localizationFile)
					.getStringNodes()) {
				addSingleEntry(document, resources, stringNode);
			}

			// Arrays
			for (StringArray stringArray : ((StringLocalizationFile) localizationFile)
					.getStringArrays()) {
				addArrayEntry(document, resources, stringArray);
			}

			document.appendChild(resources);

			saveXMLDocument(localizationFile.getFile().getLocation().toFile(),
					document);
			((AndroidStringLocalizationFile) localizationFile)
					.setSavedXMLDocument(document);
			localizationFile
					.getFile()
					.getProject()
					.refreshLocal(IResource.DEPTH_INFINITE,
							new NullProgressMonitor());
			// loadAllFiles(localizationFile.getLocalizationProject().getProject());

		} catch (Exception e) {
			throw new SequoyahException();
		}
	}
	
	/**
	 * Adds single entry into XML Android Localization file
	 * 
	 * @param document
	 * @param resources
	 * @param stringNode
	 */
	private void addSingleEntry(Document document, Element resources,
			StringNode stringNode) {
		if (!stringNode.isArray()) {
			Element string = document.createElement(XML_STRING_TAG);
			string.setAttribute(XML_STRING_ATTR_NAME, stringNode.getKey());
			string.appendChild(document.createTextNode(stringNode.getValue()));

			createOrUpdateComment(document, stringNode, string);

			resources.appendChild(string);
		}
	}

	/*
	 * 
	 * @param document
	 * @param stringNode
	 * @param string
	 */
	private void createOrUpdateComment(Document document,
			StringNode stringNode, Element string) {
		NodeComment nodeComment = stringNode.getNodeComment();
		if (nodeComment != null) {
			if (nodeComment.getComment() != null) {
				if (nodeComment.getComment().length() > 0) {
					Comment comment = document.createComment(nodeComment
							.getComment());
					string.appendChild(comment);
				}
			}
		}
	}	
	
	/**
	 * Adds array entry into XML Android Localization file
	 * 
	 * @param document
	 * @param resources
	 * @param stringArray
	 */
	private void addArrayEntry(Document document, Element resources,
			StringArray stringArray) {
		Element array = document.createElement(XML_STRING_ARRAY_TAG);
		array.setAttribute(XML_STRING_ATTR_NAME, stringArray.getKey());
		for (StringNode stringNode : stringArray.getValues()) {
			createArrayItem(document, array, stringNode);
		}
		resources.appendChild(array);
	}
	

	private void createArrayItem(Document document, Element array,
			StringNode stringNode) {
		Element arrayItem = document.createElement(XML_STRING_ARRAY_ITEM_TAG);
		arrayItem.appendChild(document.createTextNode(stringNode.getValue()));
		array.appendChild(arrayItem);

		createOrUpdateComment(document, stringNode, arrayItem);
	}	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.sequoyah.localization.android.manager.ILocalizationFileManager
	 * #updateFile(org.eclipse.sequoyah.localization.tools.datamodel.LocalizationFile)
	 */
	@Override
	public void updateFile(LocalizationFile locFile) {

	}

}
