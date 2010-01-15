/********************************************************************************
 * Copyright (c) 2007 Motorola Inc.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Fabio Fantato (Motorola)
 * 
 * Contributors:
 * Fabio Rigo (Eldorado) - Bug [244066] - The services are being run at one of the UI threads
 * Yu-Fen Kuo (MontaVista)  - [236476] - provide a generic device type
 * Daniel Barboza Franco (Eldorado Research Institute) - Bug [221739] - Improvements to State machine implementation
 * Daniel Barboza Franco (Eldorado Research Institute) - Bug [252261] - Internal class MobileInstance providing functionalities
 ********************************************************************************/
package org.eclipse.tml.framework.device.model.handler;

import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.tml.common.utilities.exception.TmLException;
import org.eclipse.tml.framework.device.model.AbstractMobileInstance;
import org.eclipse.tml.framework.device.model.IInstance;
import org.eclipse.tml.framework.device.model.IService;
import org.eclipse.tml.framework.status.IStatusTransition;
import org.eclipse.ui.PlatformUI;

public abstract class ServiceHandler implements IServiceHandler
{
    private IServiceHandler parent;

    private IService service;

    public IServiceHandler getParent()
    {
        return parent;
    }

    public void run(IInstance instance) throws TmLException
    {
     
    	/*
    	if (!verifyStatus(instance))
        {
            throw new TmLException();
        }

        createJob(instance, null);*/
    	
    	run(instance, null);
    }

    public void run(IInstance instance, Map<Object, Object> arguments) throws TmLException
    {
        if (!verifyStatus(instance))
        {
            throw new TmLException();
        }

        createJob(instance, arguments);
    }

    public IStatus run(IInstance instance, Map<Object, Object> arguments, IProgressMonitor monitor)
            throws TmLException
    {
        if (!verifyStatus(instance))
        {
            throw new TmLException();
        }

        String jobName = (service != null ? service.getName() : ""); //$NON-NLS-1$
        return doRun(instance, arguments, jobName, monitor);
    }

    private void createJob(final IInstance instance, final Map<Object, Object> arguments)
    {
        final String jobName = (service != null ? service.getName() : ""); //$NON-NLS-1$
        final Job serviceJob = new Job(jobName)
        {
            @Override
            public IStatus run(IProgressMonitor monitor)
            {	
            	IStatus status = null;
            	try {
            		status = doRun(instance, arguments, jobName, monitor);
				} catch (TmLException e) {
					e.printStackTrace();
				}
				
				return status;
            }
        };

        Display.getDefault().asyncExec(new Runnable()
        {
            public void run()
            {
                PlatformUI.getWorkbench().getProgressService().showInDialog(null, serviceJob);
            }
        });
        serviceJob.schedule();
    }

    private IStatus doRun(final IInstance instance, final Map<Object, Object> arguments,
            String jobName, IProgressMonitor monitor) throws TmLException
    {
    	
    	return ((AbstractMobileInstance)instance).getStateMachineHandler().runService(this, instance, arguments, jobName, monitor);
    	
    }

    public void setService(IService service)
    {
        this.service = service;
    };

    public IService getService()
    {
        return service;
    }

    public abstract IStatus runService(IInstance instance, Map<Object, Object> arguments,
            IProgressMonitor monitor);

    public abstract IStatus updatingService(IInstance instance, IProgressMonitor monitor);

    public abstract IServiceHandler newInstance();

    public void updatingService(IInstance instance)
    {
        // empty default implementation
    }

    public boolean verifyStatus(IInstance instance)
    {
        IStatusTransition transition = getService().getStatusTransitions(instance.getStatus());
        return (transition != null);
    }

    public void setParent(IServiceHandler handler)
    {
        this.parent = handler;
    }

    @Override
    public Object clone()
    {
        IServiceHandler newHandler = newInstance();
        newHandler.setParent(parent);
        newHandler.setService(service);
        return newHandler;
    }

}
