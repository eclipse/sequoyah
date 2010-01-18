/********************************************************************************
 * Copyright (c) 2008 Motorola Inc and Others. All rights reserved.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributor:
 * Daniel Barboza Franco (Eldorado Research Institute) - Bug [260817] - Connection is stopped unexpectedly
 *
 * Contributors:
 * <name> <company> <description>
 *******************************************************************************/
package org.eclipse.sequoyah.vnc.vncviewer.network.handlers;

import org.eclipse.sequoyah.vnc.protocol.lib.IMessageHandler;
import org.eclipse.sequoyah.vnc.protocol.lib.ProtocolHandle;
import org.eclipse.sequoyah.vnc.protocol.lib.ProtocolMessage;
import org.eclipse.sequoyah.vnc.protocol.lib.exceptions.MessageHandleException;

public class BellHandler implements IMessageHandler {

	public BellHandler() {
	}

	public ProtocolMessage handleMessage(ProtocolHandle handle,
			ProtocolMessage message) throws MessageHandleException {
		
		java.awt.Toolkit.getDefaultToolkit().beep();
		return null;
	}

}
