/********************************************************************************
 * Copyright (c) 2008 Motorola Inc and Others. All rights reserved.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributor:
 * Fabio Rigo (Eldorado Research Institute) 
 * [246212] - Enhance encapsulation of protocol implementer
 *
 * Contributors:
 * (name) - (contribution)
 *******************************************************************************/
package org.eclipse.tml.vncviewer.network.handlers;

import org.eclipse.tml.protocol.lib.IMessageHandler;
import org.eclipse.tml.protocol.lib.ProtocolHandle;
import org.eclipse.tml.protocol.lib.ProtocolMessage;

/**
 * DESCRIPTION: This class consists of the Framebuffer Update message handler.<br>
 * 
 * 
 * RESPONSIBILITY: Handle the Framebuffer Update message after it is completely
 * read from the socket
 * 
 * COLABORATORS: None<br>
 * 
 * USAGE: This class is intended to be used by Eclipse.<br>
 * 
 */
public class FramebufferUpdateHandler implements IMessageHandler {

	public ProtocolMessage handleMessage(ProtocolHandle handle,
			ProtocolMessage message) {

		return null;
	}
}
