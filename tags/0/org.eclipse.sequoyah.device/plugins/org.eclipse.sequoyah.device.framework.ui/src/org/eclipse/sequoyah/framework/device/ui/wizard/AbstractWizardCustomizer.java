/********************************************************************************
 * Copyright (c) 2007-2009 Motorola Inc. All rights reserved.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributor:
 * Fabio Fantato (Motorola)
 *
 * Contributors:
 * Fabio Fantato (Instituto Eldorado) - [263188] - Create new examples to support tutorial presentation
 * Fabio Fantato (Instituto Eldorado) - [243494] Change the reference implementation to work on Galileo
 ********************************************************************************/
package org.eclipse.tml.framework.device.ui.wizard;

import org.eclipse.jface.wizard.WizardPage;

/**
 * The Wizard Factory defines the common methods to support customized wizards for TML Wizard Instances.
 * If the user decides to customize on wizard it is need to implement a concrete class for it and 
 * reference this class using the extension point {@link org.eclipse.tml.emulator.core.wizard}
 * @author Fabio Fantato
 */
public abstract class AbstractWizardCustomizer implements IWizardCustomizer {
	private boolean hasCustomizedProjectPage;
	private boolean hasCustomizedPropertyPage;
	private boolean hasCustomizedOtherPage;
	
	
	public AbstractWizardCustomizer() {
		this.hasCustomizedProjectPage = false;
		this.hasCustomizedPropertyPage = false;
		this.hasCustomizedOtherPage = false;	
	}
	
	
	/**
	 * To create a new WizardFactory its necessary to define which pages will
	 * be customized. Only the pages set up with true should return a valid WizardPage
	 * instance to replace the original implementation.
	 * @param hasCustomizedProjectPage
	 * @param hasCustomizedPropertyPage
	 * @param hasCustomizedOtherPage
	 */
	public AbstractWizardCustomizer(boolean hasCustomizedProjectPage,boolean hasCustomizedPropertyPage,boolean hasCustomizedOtherPage){
		this.hasCustomizedProjectPage = hasCustomizedProjectPage;
		this.hasCustomizedPropertyPage = hasCustomizedPropertyPage;
		this.hasCustomizedOtherPage = hasCustomizedOtherPage;		
	}
	
	
	/**
	 * Redefine the page for project info to be show in wizard. 
	 * To use the default implementation use:
	 * return new WizardNewProjectCreationPage(EmulatorWizardConstants.PAGE_PROJECT);
	 *
	 * It is important to use as pageId to any WizardPage created the constant EmulatorWizardConstants.PAGE_PROJECT
	 * @return
	 */
	public WizardPage getCustomizedProjectPage() {
		//EmulatorWizardConstants.PAGE_PROPERTY
		return null;
	}
	
	
	/**
	 * Redefine the page for properties info to be show in wizard. 
	 *
	 * It is important to use as pageId to any WizardPage created the constant EmulatorWizardConstants.PAGE_PROPERTIES
	 * @return
	 */
	public WizardPage getCustomizedPropertyPage() {
		//EmulatorWizardConstants.PAGE_PROPERTY
		return null;
	}
	
	
	/**
	 * Redefine the page for other info to be show in wizard. 
	 *
	 * It is important to use as pageId to any WizardPage created the constant EmulatorWizardConstants.PAGE_OTHER
	 * @return
	 */
	public WizardPage getCustomizedOtherPage() {
		//EmulatorWizardConstants.PAGE_OTHER
		return null;
	}
	
	
	public boolean hasCustomizedPropertyPage() {
		return this.hasCustomizedPropertyPage;
	}
	
	public boolean hasCustomizedProjectPage() {
		return this.hasCustomizedProjectPage;
	}
	

	public boolean hasCustomizedOtherPage() {
		return this.hasCustomizedOtherPage;
	}

	
}
