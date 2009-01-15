
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
 * {Name} (company) - description of contribution.
 ********************************************************************************/
package org.eclipse.tml.framework.device.events;

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
}
