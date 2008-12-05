/********************************************************************************
 * Copyright (c) 2007-2008 Motorola Inc. All rights reserved.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributor:
 * Fabio Fantato (Motorola)
 *
 * Contributors:
 * Fabio Fantato (Motorola) - bug#221733 - code revisited
 * Otávio Ferranti (Motorola) - bug#221733 - Enhancing the device instance wizard
 * Julia Martinez Perdigueiro (Eldorado Research Institute) - [244856] - Instance View usability should be improved
 * Yu-Fen Kuo (MontaVista)  - [236476] - provide a generic device type
 ********************************************************************************/

package org.eclipse.tml.framework.device.wizard.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.tml.common.utilities.BasePlugin;
import org.eclipse.tml.framework.device.DeviceUtils;
import org.eclipse.tml.framework.device.manager.InstanceManager;
import org.eclipse.tml.framework.device.model.IDeviceType;
import org.eclipse.tml.framework.device.model.IInstanceBuilder;
import org.eclipse.tml.framework.device.wizard.DeviceWizardConstants;
import org.eclipse.tml.framework.device.wizard.DeviceWizardPlugin;
import org.eclipse.tml.framework.device.wizard.model.DefaultInstanceBuilder;
import org.eclipse.tml.framework.device.wizard.model.DeviceWizardBean;
import org.eclipse.tml.framework.device.wizard.model.DeviceWizardExtensionManager;
import org.eclipse.tml.framework.device.wizard.model.IWizardProjectPage;
import org.eclipse.tml.framework.device.wizard.model.IWizardPropertyPage;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Define an abstract class to provide new wizard emulator instance features.
 * This class should be extended in the emulator plugin
 * 
 * @author Fabio Fantato
 * 
 */
public abstract class AbstractNewEmulatorInstanceWizard extends Wizard implements INewWizard
{
    private IStructuredSelection selection;

    private IWorkbench workbench;

    private DeviceWizardBean bean;

    private IDeviceType device;

    private String wizardId;

    private String pluginId;

    public AbstractNewEmulatorInstanceWizard(String pluginId, String deviceId, String wizardId)
    {
        this.wizardId = wizardId;
        this.pluginId = pluginId;
        device = DeviceUtils.getDeviceTypeById(deviceId);
        BasePlugin.logInfo("Device for Wizard:" + device.getBundleName()); //$NON-NLS-1$

        // load necessary information on object creation
        initializeBean();
        initializeWizardSettings();
        initializeDialogSettings();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
     *      org.eclipse.jface.viewers.IStructuredSelection)
     */
    public void init(IWorkbench workbench, IStructuredSelection selection)
    {
        BasePlugin.logInfo("New Device Instance Wizard started"); //$NON-NLS-1$
        this.workbench = workbench;
        this.selection = selection;
    }

    /**
     * Initialize Wizard Setting reading extension point in the emulator plugin
     * and loading the properties from plugin.xml
     */
    private void initializeWizardSettings()
    {
        BasePlugin.logInfo(getExtensionId());
        if (bean != null)
        {
            setNeedsProgressMonitor(bean.needsProgressMonitor());
            setForcePreviousAndNextButtons(bean.forcePreviousAndNextButtons());
            setDefaultPageImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(pluginId,
                    bean.getImage()));
            setWindowTitle(bean.getTitle());
        }
    }

    /**
     * Initialize instance bean
     */
    private void initializeBean()
    {
        this.bean =
                DeviceWizardExtensionManager.getInstance().getDeviceWizardBean(getExtensionId());
    }

    /**
     * initialize dialog settings
     */
    private void initializeDialogSettings()
    {
        IDialogSettings settings = DeviceWizardPlugin.getDefault().getDialogSettings();
        String sectionName = this.getClass().getName();
        if (settings.getSection(sectionName) == null)
        {
            settings.addNewSection(sectionName);
        }
        setDialogSettings(settings.getSection(sectionName));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#dispose()
     */
    @Override
    public void dispose()
    {
        this.workbench = null;
        this.selection = null;
        this.bean = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    @Override
    public void addPages()
    {
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
    private void addPage(WizardPage page, String title, String description)
    {
        page.setTitle(title);
        page.setDescription(description);
        addPage(page);
    }

    /**
     * Add a project page
     */
    @SuppressWarnings("unchecked")
    public void addProjectPage()
    {
        addPage(bean.getProjectPage(), bean.getProjectTitle(), bean.getProjectDescription());
    }

    /**
     * Add a properties page
     */
    @SuppressWarnings("unchecked")
    public void addPropertiesPage()
    {
        addPage(bean.getPropertyPage(), bean.getPropertyTitle(), bean.getPropertyDescription());
    }

    /**
     * Add other page
     */
    @SuppressWarnings("unchecked")
    public void addOtherPage()
    {
        if (bean.hasOtherPage())
        {
            addPage(bean.getOtherPage(), bean.getOtherTitle(), bean.getOtherDescription());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#getNextPage(org.eclipse.jface.wizard.IWizardPage)
     */
    @Override
    public IWizardPage getNextPage(IWizardPage page)
    {

        IWizardPage nextPage = null;
        if ((nextPage == null) && (page != null))
        {
            nextPage = super.getNextPage(page);
        }
        if (nextPage != null)
        {
            nextPage.setPreviousPage(page);
        }
        if (page.getName().equals(DeviceWizardConstants.PAGE_PROJECT))
        {
            nextPage = getPage(DeviceWizardConstants.PAGE_PROPERTY);
        }
        return nextPage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    @Override
    public boolean performFinish()
    {
        boolean ok = false;
        try
        {
            final IInstanceBuilder projectBuilder = getProjectBuilder();
            WorkspaceModifyOperation op = new WorkspaceModifyOperation()
            {
                @Override
                protected void execute(IProgressMonitor monitor)
                {
                    BasePlugin.logInfo("Instance creation for Wizard:" + getExtensionId()); //$NON-NLS-1$
                    InstanceManager.getInstance().createProject(getDevice(), projectBuilder,
                            monitor);
                }
            };
            getContainer().run(false, true, op);
            ok = true;
        }
        catch (InvocationTargetException x)
        {
            BasePlugin.logError(x.getMessage(), x);
        }
        catch (InterruptedException x)
        {
            BasePlugin.logError(x.getMessage(), x);
        }
        return ok;
    }

    /**
     * Get local workbench
     * 
     * @return
     */
    public IWorkbench getWorkbench()
    {
        return this.workbench;
    }

    /**
     * Gets the current selection
     * 
     * @return
     */
    public IStructuredSelection getSelection()
    {
        return this.selection;
    }

    public IWizardProjectPage getDefaultProjectPage()
    {
        return (IWizardProjectPage) getPage(DeviceWizardConstants.PAGE_PROJECT);
    }

    public IInstanceBuilder getProjectBuilder()
    {
        return new DefaultInstanceBuilder(getDefaultProjectPage(), getProperties());
    }

    public IDeviceType getDevice()
    {
        return device;
    }

    public String getExtensionId()
    {
        return this.wizardId;
    }

    public Properties getProperties()
    {
        return ((IWizardPropertyPage) getPage(DeviceWizardConstants.PAGE_PROPERTY)).getProperties();
    }

}
