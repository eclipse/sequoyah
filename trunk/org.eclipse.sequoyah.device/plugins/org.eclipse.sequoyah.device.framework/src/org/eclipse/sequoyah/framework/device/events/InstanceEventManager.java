
/**Copyright (c) 2008 Motorola Inc. and others.  All rights reserved.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributor:
 * Fabio Rigo (Eldorado Research Institute)
 * [244951] Implement listener/event mechanism at device framework
 *
 * Contributors:
 * {Name} (company) - description of contribution.
 ********************************************************************************/

package org.eclipse.tml.framework.device.events;

import org.eclipse.core.commands.common.EventManager;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.util.SafeRunnable;

public class InstanceEventManager extends EventManager
{
    private static InstanceEventManager _instance;
    
    private InstanceEventManager()
    {
    }
    
    public static InstanceEventManager getInstance()
    {
        if (_instance == null)
        {
            _instance = new InstanceEventManager();
        }
        
        return _instance;
    }
    
    public void addInstanceListener(IInstanceListener listener)
    {
        addListenerObject(listener);
    }
    
    public void removeInstanceListener(IInstanceListener listener)
    {
        removeListenerObject(listener);
    }

    public void fireInstanceCreated(final InstanceEvent event)
    {
        Object list[] = getListeners();
        for (int i = 0; i < list.length; i++) {
            final IInstanceListener l = (IInstanceListener) list[i];
            SafeRunner.run(new SafeRunnable() {
                public void run() {
                    l.instanceCreated(event);
                }
            });
        }  
    }   
    
    public void fireInstanceDeleted(final InstanceEvent event)
    {
        Object list[] = getListeners();
        for (int i = 0; i < list.length; i++) {
            final IInstanceListener l = (IInstanceListener) list[i];
            SafeRunner.run(new SafeRunnable() {
                public void run() {
                    l.instanceDeleted(event);
                }
            });
        }  
    }   
    
    public void fireInstanceLoaded(final InstanceEvent event)
    {
        Object list[] = getListeners();
        for (int i = 0; i < list.length; i++) {
            final IInstanceListener l = (IInstanceListener) list[i];
            SafeRunner.run(new SafeRunnable() {
                public void run() {
                    l.instanceLoaded(event);
                }
            });
        }  
    }   
    
    public void fireInstanceUnloaded(final InstanceEvent event)
    {
        Object list[] = getListeners();
        for (int i = 0; i < list.length; i++) {
            final IInstanceListener l = (IInstanceListener) list[i];
            SafeRunner.run(new SafeRunnable() {
                public void run() {
                    l.instanceUnloaded(event);
                }
            });
        }  
    }   
    
    public void fireInstanceUpdated(final InstanceEvent event)
    {
        Object list[] = getListeners();
        for (int i = 0; i < list.length; i++) {
            final IInstanceListener l = (IInstanceListener) list[i];
            SafeRunner.run(new SafeRunnable() {
                public void run() {
                    l.instanceUpdated(event);
                }
            });
        }  
    }  
}
