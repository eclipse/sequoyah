/*******************************************************************************
 * Copyright (c) 2008 MontaVista Software, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Yu-Fen Kuo (MontaVista) - initial API and implementation
 *******************************************************************************/
package org.eclipse.tml.framework.device.ui.wizard;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.graphics.Image;
import org.eclipse.tml.framework.device.manager.InstanceManager;
import org.eclipse.tml.framework.device.model.IInstanceBuilder;
import org.eclipse.tml.framework.device.ui.DeviceUIPlugin;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.progress.IProgressService;

/*
 * new device instance wizard.
 */
public class NewDeviceWizard extends Wizard implements INewWizard {

	public static String DEFAULT_PAGE_ID = "$_default_page_$"; //$NON-NLS-1$
	private String currentDeviceTypeId;
	private DeviceTypeCustomWizardPageHandler customWizardPageHandler = new DeviceTypeCustomWizardPageHandler(
			this);
	private DefaultDeviceTypeWizardPage firstPage;
	private Properties properties;

	public void addPages() {
		firstPage = new DefaultDeviceTypeWizardPage(DEFAULT_PAGE_ID);
		addPage(firstPage);
		super.addPages();
	}

	public boolean performFinish() {
		updatePropertiesFromWizardPages();

		boolean ok = false;
		try {
			final IInstanceBuilder projectBuilder = getProjectBuilder();
			IWorkbench workbench = DeviceUIPlugin.getDefault().getWorkbench();
			IProgressService progressService = workbench.getProgressService();
			final IRunnableWithProgress runnable = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException {
					InstanceManager.getInstance().createProject(
							firstPage.getDeviceType(), projectBuilder, monitor);				
				}
			};
			getContainer().run(false, true, runnable);
			ok = true;
		} catch (InvocationTargetException x) {
			x.printStackTrace();
		} catch (InterruptedException x) {
			x.printStackTrace();
		}
		if (ok)
			return customWizardPageHandler.performFinish();
		return false;

	}

	public IInstanceBuilder getProjectBuilder() {
		return new InstanceBuilder(firstPage.getInstanceName(),
				getProperties());

	}

	public Properties getProperties() {
		return properties;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {

	}

	public String getCurrentDeviceTypeId() {
		return currentDeviceTypeId;
	}

	public void setCurrentDeviceTypeId(String currentDeviceTypeId) {
		this.currentDeviceTypeId = currentDeviceTypeId;
	}

	public IWizardPage getNextPage(IWizardPage page) {
		IWizardPage nextPage = customWizardPageHandler.getNextPage(page);
		addCustomPage(nextPage);
		return nextPage;
	}

	public IWizardPage getPreviousPage(IWizardPage page) {
		return customWizardPageHandler.getPreviousPage(page);
	}

	private void addCustomPage(IWizardPage customPage) {
		if (customPage == null)
			return;
		IWizardPage[] allPages = this.getPages();
		boolean customPageFound = false;
		for (int i = 0; i < allPages.length && !customPageFound; i++) {
			if (allPages[i].equals(customPage))
				customPageFound = true;
		}
		if (!customPageFound) {
			addPage(customPage);
		}
	}

	private void updatePropertiesFromWizardPages() {
		properties = new Properties();
		// properties.putAll(firstPage.getProperties());
		for (IWizardPage page = firstPage; page != null; page = getNextPage(page)) {
			if (page instanceof IInstanceProperties) {
				properties.putAll(((IInstanceProperties) page).getProperties());
			}

		}

	}

	public boolean canFinish() {
		return (firstPage.isPageComplete() && customWizardPageHandler
				.canFinish());
	}

	@Override
	public Image getDefaultPageImage() {
		return DeviceUIPlugin.getDefault().getImageRegistry().get(
				DeviceUIPlugin.IMAGEKEY_NEW_DEVICE_WIZARD);
	}
}
