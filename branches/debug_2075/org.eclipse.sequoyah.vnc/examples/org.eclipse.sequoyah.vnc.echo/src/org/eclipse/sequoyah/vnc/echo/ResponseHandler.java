/********************************************************************************
 * Copyright (c) 2007-2010 Motorola Inc.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributor:
 * Fabio Rigo
 *
 * Contributors:
 * Fabio Fantato (Eldorado Research Institute - Bug [243305] - The plugin org.eclipse.sequoyah.vnc.echo has compilation errors about exception handling mechanism
 * Fabio Rigo (Eldorado Research Institute) - [246212] - Enhance encapsulation of protocol implementer
 * Daniel Pastore (Eldorado) - [289870] Moving and renaming Tml to Sequoyah
 ********************************************************************************/
package org.eclipse.sequoyah.vnc.echo;

import org.eclipse.sequoyah.vnc.protocol.lib.IMessageHandler;
import org.eclipse.sequoyah.vnc.protocol.lib.ProtocolHandle;
import org.eclipse.sequoyah.vnc.protocol.lib.ProtocolMessage;

public class ResponseHandler implements IMessageHandler {

	public ProtocolMessage handleMessage(ProtocolHandle handle,
			ProtocolMessage message) {

		String echoedMessage = (String) message.getFieldValue("echoedMessage"); //$NON-NLS-1$

		System.out.println("Client has received the echoed message \"" //$NON-NLS-1$
				+ echoedMessage + "\""); //$NON-NLS-1$

		Object sizeObj = message.getFieldValue("echoedMessageSize"); //$NON-NLS-1$
		if (sizeObj != null)
		{
			Integer size = new Integer((String)sizeObj);
			System.out.println("Size is present in message which code is " + message.getCode() + ". Its value is " + size.intValue()); //$NON-NLS-1$ //$NON-NLS-2$
		}
		else
		{
			System.out.println("Size is NOT present in message which code is " + message.getCode()); //$NON-NLS-1$
		}
		
		return null;
	}
}
