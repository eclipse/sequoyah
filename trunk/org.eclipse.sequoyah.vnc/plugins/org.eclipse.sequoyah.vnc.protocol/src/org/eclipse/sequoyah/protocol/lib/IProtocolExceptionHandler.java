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
 * Fabio Rigo - Bug [244067] - The exception handling interface should forward the protocol implementer object
 * Fabio Rigo (Eldorado Research Institute) - [246212] - Enhance encapsulation of protocol implementer 
 * Fabio Rigo (Eldorado Research Institute) - Bug [262632] - Avoid providing raw streams to the user in the protocol framework
 ********************************************************************************/
package org.eclipse.tml.protocol.lib;

import java.io.IOException;

import org.eclipse.tml.protocol.lib.exceptions.InvalidDefinitionException;
import org.eclipse.tml.protocol.lib.exceptions.InvalidInputStreamDataException;
import org.eclipse.tml.protocol.lib.exceptions.InvalidMessageException;
import org.eclipse.tml.protocol.lib.exceptions.MessageHandleException;
import org.eclipse.tml.protocol.lib.exceptions.ProtocolHandshakeException;
import org.eclipse.tml.protocol.lib.exceptions.ProtocolRawHandlingException;

/**
 * DESCRIPTION: This class contains the methods that the user should implement
 * to handle internal exceptions in protocol plugin. <br>
 * 
 * RESPONSIBILITY: Provide means for the user to handle exceptions that are
 * internal to the protocol plugin mechanism in a custom way.<br>
 * 
 * COLABORATORS: None.<br>
 * 
 * USAGE: Implement this interface and provide an instance of this class when
 * starting the protocol. If an exception occurs while the protocol is running,
 * one of the interface methods will be invoked.<br>
 * 
 */
public interface IProtocolExceptionHandler {

	void handleIOException(ProtocolHandle handle, IOException e);

	void handleProtocolHandshakeException(ProtocolHandle handle, ProtocolHandshakeException e);

	void handleMessageHandleException(ProtocolHandle handle, MessageHandleException e);

	void handleInvalidMessageException(ProtocolHandle handle, InvalidMessageException e);

	void handleInvalidInputStreamDataException(ProtocolHandle handle, InvalidInputStreamDataException e);

	void handleInvalidDefinitionException(ProtocolHandle handle, InvalidDefinitionException e);

	void handleProtocolRawHandlingException(ProtocolHandle handle, ProtocolRawHandlingException e);
}
