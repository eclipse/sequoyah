/********************************************************************************
 * Copyright (c) 2008 Motorola Inc. and others.  All rights reserved.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributor:
 * Fabio Rigo (Eldorado Research Institute)
 * [244951] Implement listener/event mechanism at device framework
 *
 * Contributors:
 * Fabio Rigo (Eldorado Research Institute) - Bug [287995] - Provide an instance is about to transition event
 ********************************************************************************/
package org.eclipse.sequoyah.device.framework.events;

public abstract class InstanceAdapter implements IInstanceListener
{
    public void instanceCreated(InstanceEvent e) {
    }

    public void instanceDeleted(InstanceEvent e) {
    }

    public void instanceLoaded(InstanceEvent e) {
    }

    public void instanceUnloaded(InstanceEvent e) {
    }

    public void instanceUpdated(InstanceEvent e) {
    }
    
    public void instanceTransitioned(InstanceEvent e) {
    }
    
    public void instanceAboutToTransition(InstanceEvent e){        
    }
}
