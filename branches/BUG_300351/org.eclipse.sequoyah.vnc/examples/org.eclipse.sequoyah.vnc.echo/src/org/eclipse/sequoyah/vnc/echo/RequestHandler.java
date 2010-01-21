/********************************************************************************
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
 ********************************************************************************/
package org.eclipse.sequoyah.vnc.echo;

import org.eclipse.sequoyah.vnc.protocol.lib.IMessageHandler;
import org.eclipse.sequoyah.vnc.protocol.lib.ProtocolHandle;
import org.eclipse.sequoyah.vnc.protocol.lib.ProtocolMessage;

public class RequestHandler implements IMessageHandler {

	public ProtocolMessage handleMessage(ProtocolHandle handle,
			ProtocolMessage message) {

		String messageToEcho = (String) message.getFieldValue("textToEcho"); //$NON-NLS-1$
		ProtocolMessage messageWithSizeDefined = null;
		ProtocolMessage messageWithoutSizeDefined = null;
		
		try {
			/*
			messageWithoutSizeDefined = new ProtocolMessage(0x700);
			messageWithoutSizeDefined.setFieldValue("echoedMessage", messageToEcho);
			*/
			
			messageWithSizeDefined = new ProtocolMessage(0x800);
			messageWithSizeDefined.setFieldValue("echoedMessage", messageToEcho); //$NON-NLS-1$
		} catch (Exception e) {
			e.printStackTrace();
		} 

		System.out.println("Server has received and echoed message \"" //$NON-NLS-1$
				+ messageToEcho + "\""); //$NON-NLS-1$

		return messageWithSizeDefined;
	}
}
