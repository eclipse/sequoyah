/********************************************************************************
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributor:
 * Fabio Rigo
 *
 * Contributors:
 * Fabio Rigo - Bug [238191] - Enhance exception handling
 * Fabio Rigo (Eldorado Research Institute) - [246212] - Enhance encapsulation of protocol implementer
 * Fabio Rigo (Eldorado Research Institute) - Bug [262632] - Avoid providing raw streams to the user in the protocol framework
 ********************************************************************************/
package org.eclipse.tml.protocol.lib.msgdef;

import org.eclipse.tml.common.utilities.BasePlugin;
import org.eclipse.tml.protocol.lib.IMessageHandler;
import org.eclipse.tml.protocol.lib.ProtocolHandle;
import org.eclipse.tml.protocol.lib.ProtocolMessage;
import org.eclipse.tml.protocol.lib.exceptions.MessageHandleException;

/**
 * DESCRIPTION: This class is the handler used by messages that does not need to
 * be handled. <br>
 * 
 * RESPONSIBILITY: Provide null handling to messages that are not to be handled
 * by the implemented part in a partial implementation of the protocol.
 * 
 * COLABORATORS: None.<br>
 * 
 * USAGE: The class is intended to be instantiated by the protocol framework
 * only.<br>
 * 
 */
public final class NullMessageHandler implements IMessageHandler {

	/**
	 * Do nothing.
	 * 
	 * @see IMessageHandler#handleMessage(ProtocolHandle, ProtocolMessage)
	 */
	public ProtocolMessage handleMessage(ProtocolHandle handle, 
			ProtocolMessage message)
			throws MessageHandleException {

	    BasePlugin.logDebugMessage("NullMessageHandler","The message was not handled, because its handler is null");
		return null;
	}
}
