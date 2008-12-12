/********************************************************************************
 * Copyright (c) 2007-2008 Motorola Inc and others.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Daniel Barboza Franco (Eldorado Research Institute) - bug 221739
 * 
 * Contributors:
 * Daniel Barboza Franco (Eldorado Research Institute) - Bug [250644] - Instance view keeps enabled buttons while performing a service.
 * Fabio Rigo (Eldorado Research Institute) - Bug [251595] - Proportion of ticks in ServiceHandler class is not adequate
 ********************************************************************************/

package org.eclipse.tml.framework.device.statemachine;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.tml.common.utilities.exception.TmLException;
import org.eclipse.tml.framework.device.DeviceUtils;
import org.eclipse.tml.framework.device.events.InstanceEvent;
import org.eclipse.tml.framework.device.events.InstanceEventManager;
import org.eclipse.tml.framework.device.model.IDeviceType;
import org.eclipse.tml.framework.device.model.IInstance;
import org.eclipse.tml.framework.device.model.IService;
import org.eclipse.tml.framework.device.model.handler.IServiceHandler;
import org.eclipse.tml.framework.device.model.handler.ServiceHandler;
import org.eclipse.tml.framework.status.IStatusTransition;

public class StateMachineHandler {
	
	private IInstance instance;
	private StateMachineModel stmModel;
	private IStatusTransition transition = null;
	private boolean transitioning = false;
	
    public StateMachineHandler(IInstance instance) {
		
		this.instance = instance;
		
		IDeviceType device = DeviceUtils.getDeviceType(instance);
		List<IService> services = device.getServices();

		HashSet transitions = new HashSet();
		for (IService service:services){
			transitions.addAll(service.getStatusTransitions());
		}

		StateMachine stm = new StateMachine(transitions);
		this.stmModel = new StateMachineModel(stm);
		
	}

	public IStatus runService(ServiceHandler svcHnd, final IInstance instance, final Map<Object, Object> arguments,
            String jobName, IProgressMonitor monitor) throws TmLException{

        IStatus status = Status.OK_STATUS;
		this.setTransitioning(true);

        transition = svcHnd.getService().getStatusTransitions(instance.getStatus());
        
        if (!transition.getStartId().equals(stmModel.getState())) {
        	throw new TmLException();
        }
        
        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }

        monitor.beginTask(jobName, 1000);
        status = svcHnd.runService(instance, arguments, new SubProgressMonitor(monitor, 950));
        if (status.isOK()) {
        	IServiceHandler parent = svcHnd.getParent();
            if (parent != null) {
                if (parent instanceof ServiceHandler) {
                    status =
                            ((ServiceHandler) parent).updatingService(instance,
                                    new SubProgressMonitor(monitor, 50));
                }
                else {
                    parent.updatingService(instance);
                    monitor.worked(50);
                }
            }
            else {
                status =
                        svcHnd.updatingService(instance, new SubProgressMonitor(
                                monitor, 50));
            }
        }

               
        if (status.isOK()) {
            stmModel.transitionState(transition.getEndId());
        }
        else if (status.getSeverity() != IStatus.CANCEL) {
            stmModel.transitionState(transition.getHaltId());
        }
        
        InstanceEventManager.getInstance().fireInstanceUpdated(new InstanceEvent(instance));
        monitor.done();
        
        this.setTransitioning(false);
       
        return status;
    }
	
	public synchronized void setState(String dest) {
		stmModel.setState(dest);
		//TODO: use a specific listener for states
        InstanceEventManager.getInstance().fireInstanceUpdated(new InstanceEvent(instance));
	}
	
	/**
	 * Return the current State.
	 */
	public String getState() {
		return stmModel.getState();
	}
	
	/**
	 * Return the current transition if performing a service, otherwise return the last transition.
	 */
	public IStatusTransition getCurrentStatusTransition(){
		return transition;
	}
	
	
	/**
	 * True if a service is in a state transition.
	 * @param transitioning
	 */
	private synchronized void setTransitioning(boolean transitioning) {
		this.transitioning = transitioning;
	}

	/**
	 * @return True if a service is in a state transition.
	 */
	public boolean isTransitioning() {
		return transitioning;
	}
	
}
