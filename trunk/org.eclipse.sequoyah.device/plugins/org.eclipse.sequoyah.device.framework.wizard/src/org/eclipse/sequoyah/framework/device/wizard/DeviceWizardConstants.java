/********************************************************************************
 * Copyright (c) 2007 Motorola Inc. All rights reserved.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributor:
 * Fabio Fantato (Motorola)
 *
 * Contributors:
 * Fabio Fantato (Motorola) - bug#221733 - code revisited
 ********************************************************************************/

package org.eclipse.tml.framework.device.wizard;

/**
 * Define the common constants used by emulator framework.
 * <p>
 * The purpose of this constants is keep together all definitions for extension
 * points, views, shortcuts, propertys, attributes e several others.
 * 
 * @author Fabio Fantato
 */
public interface DeviceWizardConstants {

	
	/**
	 * Extension ID for New Wizard of Emulator Instances
	 */
	public static final String EXTENSION_INSTANCE_ID = "org.eclipse.tml.device.wizard";


	public static final String PROPERTIES_FILENAME = "instance.properties";
	public static final String PROPERTIES_FILENAME_FULL = "/org/eclipse/tml/device/wizard/resources/instance.properties";

	
	/**
	 * Shortcut to New Folder Wizard
	 */
	public static final String SHORTCUT_FOLDER = "org.eclipse.ui.wizards.new.folder";

	/**
	 * Shortcut to New File Wizard
	 */
	public static final String SHORTCUT_FILE = "org.eclipse.ui.wizards.new.file";

	/**
	 * ID for project page wizard
	 */
	public static final String PAGE_PROJECT = "projectPage";

	/**
	 * ID for property page wizard
	 */
	public static final String PAGE_PROPERTY = "propertyPage";

	/**
	 * ID for other pages wizard
	 */
	public static final String PAGE_OTHER = "otherPage";

	/**
	 * ID for read SETTINGS elements from plugin extensions
	 */
	public static final String SETTINGS = "settings";

	/**
	 * ID for read LAUNCHER elements from plugin extensions
	 */
	public static final String ELEMENT_LAUNCHER = "launcher";

	/**
	 * ID for read PROJECT elements from plugin extensions
	 */
	public static final String ELEMENT_CUSTOMIZER = "customizer";

	
	/**
	 * ID for read PROJECT elements from plugin extensions
	 */
	public static final String ELEMENT_PROJECT = "project";

	/**
	 * ID for read PROPERTY elements from plugin extensions
	 */
	public static final String ELEMENT_PROPERTY = "property";

	/**
	 * ID for read OTHER elements from plugin extensions
	 */
	public static final String ELEMENT_OTHER = "other";

	/**
	 * ID for read xml attribute from plugin extensions
	 */
	public static final String ATB_XML = "xml";
	
	/**
	 * ID for read monitor attribute from plugin extensions
	 */
	public static final String ATB_MONITOR = "needsProgressMonitor";
	
	/**
	 * ID for read forcePreviousAndNextButtons attribute from plugin extensions
	 */
	public static final String ATB_FORCE = "forcePreviousAndNextButtons";
	
	/**
	 * ID for read canFinishEarly attribute from plugin extensions
	 */
	public static final String ATB_FINISH = "canFinishEarly";
	
	/**
	 * ID for read image attribute from plugin extensions
	 */
	public static final String ATB_IMAGE = "image";
	
	/**
	 * ID for read title attribute from plugin extensions
	 */
	public static final String ATB_TITLE = "title";

	/**
	 * ID for read class attribute from plugin extensions
	 */
	public static final String ATB_CLASS = "class";

	/**
	 * ID for read name attribute from plugin extensions
	 */

	public static final String ATB_NAME = "name";

	/**
	 * ID for read description attribute from plugin extensions
	 */
	public static final String ATB_DESCRIPTION = "description";
	
	/**
	 * A null string
	 */
	public static final String STRING_NULL = "";
	/**
	 * 
	 */
	public static final String FOLDER_SEPARATOR = "/";
	/**
	 * 
	 */
	public static final String SLASH = "\\";

}
