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
 ********************************************************************************/
package org.eclipse.tml.protocol.lib;

import java.io.IOException;

import org.eclipse.tml.protocol.lib.exceptions.InvalidDefinitionException;
import org.eclipse.tml.protocol.lib.exceptions.InvalidInputStreamDataException;
import org.eclipse.tml.protocol.lib.exceptions.InvalidMessageException;
import org.eclipse.tml.protocol.lib.exceptions.MessageHandleException;
import org.eclipse.tml.protocol.lib.exceptions.ProtocolInitException;
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

	void handleIOException(IOException e);

	void handleProtocolInitException(ProtocolInitException e);

	void handleMessageHandleException(MessageHandleException e);

	void handleInvalidMessageException(InvalidMessageException e);

	void handleInvalidInputStreamDataException(InvalidInputStreamDataException e);

	void handleInvalidDefinitionException(InvalidDefinitionException e);

	void handleProtocolRawHandlingException(ProtocolRawHandlingException e);
}
