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
 * {Name} (company) - description of contribution.
 ********************************************************************************/

package org.eclipse.tml.framework.device.wizard.ui;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.tml.framework.device.manager.DeviceManager;
import org.eclipse.tml.framework.device.model.IDevice;
import org.eclipse.tml.framework.device.model.IInstanceBuilder;
import org.eclipse.tml.framework.device.wizard.DeviceWizardConstants;
import org.eclipse.tml.framework.device.wizard.DeviceWizardPlugin;
import org.eclipse.tml.framework.device.wizard.model.DefaultInstanceBuilder;
import org.eclipse.tml.framework.device.wizard.model.DeviceWizardBean;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

/**
 * Define an abstract class to provide new wizard emulator instance features.
 * This class should be extended in the emulator plugin
 * 
 * @author Fabio Fantato
 * 
 */
public abstract class AbstractNewEmulatorInstanceWizard extends Wizard implements
		INewWizard {	
	private IStructuredSelection selection;
	private IWorkbench workbench;
	private DeviceWizardBean bean;
	private IDevice device;
	private String pluginId;

	
	
	public AbstractNewEmulatorInstanceWizard(String deviceId,String pluginId){
		this.pluginId = pluginId;
		device = DeviceManager.getInstance().getDevice(pluginId);
		//DeviceWizardPlugin.logInfo("Device for Wizard:"+device.getName());
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
	 *      org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		DeviceWizardPlugin.logInfo("New Device Instance Wizard started");
		this.workbench = workbench;
		this.selection = selection;
		initializeBean();
		initializeWizardSettings();
		initializeDialogSettings();
	}

	/**
	 * Initialize Wizard Setting reading extension point in the emulator plugin
	 * and loading the properties from plugin.xml
	 */
	private void initializeWizardSettings() {
		DeviceWizardPlugin.logInfo(getExtensionId());
		if (bean != null) {
			setNeedsProgressMonitor(bean.needsProgressMonitor());
			setForcePreviousAndNextButtons(bean.forcePreviousAndNextButtons());
			setDefaultPageImageDescriptor(DeviceWizardPlugin.imageDescriptorFromPlugin(getExtensionId(), bean.getImage()));
			setWindowTitle(bean.getTitle());
		}
	}

	/**
	 * Initialize instance bean
	 */
	private void initializeBean() {
		this.bean = new DeviceWizardBean(getExtensionId());
	}

	/**
	 * initialize dialog settings
	 */
	private void initializeDialogSettings() {
		IDialogSettings settings = DeviceWizardPlugin.getDefault().getDialogSettings();
		String sectionName = this.getClass().getName();
		if (settings.getSection(sectionName) == null) {
			settings.addNewSection(sectionName);
		}
		setDialogSettings(settings.getSection(sectionName));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#dispose()
	 */
	public void dispose() {
		this.workbench = null;
		this.selection = null;
		this.bean = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	public void addPages() {
		addProjectPage();
		addPropertiesPage();
		addOtherPage();
	}

	/**
	 * Add one page using className,pageId, title and description
	 * 
	 * @param className
	 *            is the name of the class
	 * @param pageId
	 *            is the page if
	 * @param title
	 *            is a string that represents the pages´s title
	 * @param description
	 *            is a strint that represents the pages´s description
	 */
	@SuppressWarnings("unchecked")
	private void addPage(WizardPage page, String title,
			String description) {
			page.setTitle(title);
			page.setDescription(description);
			addPage(page);
	}

	
	/**
	 * Add a project page
	 */
	@SuppressWarnings("unchecked")
	public void addProjectPage() {
			addPage(bean.getProjectPage(), bean.getProjectTitle(), bean.getProjectDescription());
	}

	/**
	 * Add a properties page
	 */
	@SuppressWarnings("unchecked")
	public void addPropertiesPage() {
		if (bean.hasPropertyPage()) {
			addPage(bean.getPropertyPage(),bean.getPropertyTitle(), bean.getPropertyDescription());
		}
	}

	/**
	 * Add other page
	 */
	@SuppressWarnings("unchecked")
	public void addOtherPage() {
		if (bean.hasOtherPage()) {
			addPage(bean.getOtherPage(),bean.getOtherTitle(), bean.getOtherDescription());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#getNextPage(org.eclipse.jface.wizard.IWizardPage)
	 */
	public IWizardPage getNextPage(IWizardPage page) {
		IWizardPage nextPage = null;
		if (nextPage == null && page != null)
			nextPage = super.getNextPage(page);
		if (nextPage != null)
			nextPage.setPreviousPage(page);
		return nextPage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	public boolean performFinish() {
		boolean ok = false;
		try {
			final IInstanceBuilder projectBuilder = getProjectBuilder(); 
			WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
				protected void execute(IProgressMonitor monitor) {
					//EmulatorProjectManager.getInstance().createProject(getExtensionId(),projectBuilder,monitor);
				}
			};
			getContainer().run(false, true, op);
		ok = true;
	} catch (InvocationTargetException x) {
		DeviceWizardPlugin.logError(x.getMessage(), x);
	} catch (InterruptedException x) {
		DeviceWizardPlugin.logError(x.getMessage(), x);
	}
		return ok;
	}

	/**
	 * Get local workbench
	 * 
	 * @return
	 */
	public IWorkbench getWorkbench() {
		return this.workbench;
	}

	/**
	 * Gets the current selection
	 * 
	 * @return
	 */
	public IStructuredSelection getSelection() {
		return this.selection;
	}
	
	public WizardNewProjectCreationPage getDefaultProjectPage() {
		return (WizardNewProjectCreationPage)getPage(DeviceWizardConstants.PAGE_PROJECT);
	}
	
	

	public IInstanceBuilder getProjectBuilder(){
		return new DefaultInstanceBuilder(getDefaultProjectPage(),getPropertiesFile(),getPropertiesPath());
	}
	
	
	public String getExtensionNatureId() {
		return device.getId();
	}
		
	
	public String getExtensionId() {
		return this.pluginId;
	}

	
	public String getPropertiesFile() {
		return "";
	}
	
	
	public String getPropertiesPath() {
		return "";
		
	}

}
