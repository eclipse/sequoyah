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
 ********************************************************************************/
package org.eclipse.tml.framework.device.model.handler;

import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.tml.common.utilities.exception.TmLException;
import org.eclipse.tml.framework.device.factory.InstanceRegistry;
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
        if (!verifyStatus(instance))
        {
            throw new TmLException();
        }

        createJob(instance, null);
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

        String jobName = (service != null ? service.getName() : "");
        return doRun(instance, arguments, jobName, monitor);
    }

    private void createJob(final IInstance instance, final Map<Object, Object> arguments)
    {
        final String jobName = (service != null ? service.getName() : "");
        final Job serviceJob = new Job(jobName)
        {
            @Override
            public IStatus run(IProgressMonitor monitor)
            {
                return doRun(instance, arguments, jobName, monitor);
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
            String jobName, IProgressMonitor monitor)
    {
        IStatus status = Status.OK_STATUS;

        if (monitor == null)
        {
            monitor = new NullProgressMonitor();
        }

        monitor.beginTask(jobName, 3);
        status = runService(instance, arguments, new SubProgressMonitor(monitor, 1));
        if (status.isOK())
        {
            if (parent != null)
            {
                if (parent instanceof ServiceHandler)
                {
                    status =
                            ((ServiceHandler) parent).updatingService(instance,
                                    new SubProgressMonitor(monitor, 1));
                }
                else
                {
                    parent.updatingService(instance);
                    monitor.worked(1);
                }
            }
            else
            {
                status =
                        ServiceHandler.this.updatingService(instance, new SubProgressMonitor(
                                monitor, 1));
            }
        }

        updateStatus(instance, status);

        monitor.done();

        return status;
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

    public void updateStatus(IInstance instance, IStatus status)
    {
        IStatusTransition transition = getService().getStatusTransitions(instance.getStatus());

        if (status.isOK())
        {
            instance.setStatus(transition.getEndId());
        }
        else
        {
            instance.setStatus(transition.getHaltId());
        }
        InstanceRegistry.getInstance().setDirty(true);
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
