/********************************************************************************
 * Copyright (c) 2009 Motorola Inc. All rights reserved.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributor:
 * Daniel Barboza Franco (Eldorado) -  Bug [288301]
 *
 * Contributors:
 * {Name} (company) - description of contribution.
 ********************************************************************************/

package org.eclipse.tml.framework.device.model.handler;

import org.eclipse.tml.framework.device.model.AbstractMobileInstance;

public class UndefinedDeviceInstance extends AbstractMobileInstance {

	public UndefinedDeviceInstance(String id) {
		this.id = id;
		this.pid = 0;
	}

}
