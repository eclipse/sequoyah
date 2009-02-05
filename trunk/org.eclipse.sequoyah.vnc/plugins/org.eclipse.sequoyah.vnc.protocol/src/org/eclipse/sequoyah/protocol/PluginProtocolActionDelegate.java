/********************************************************************************
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributor:
 * Fabio Rigo
 *
 * Contributors:
 * Daniel Barboza Franco - Bug [233775] - Does not have a way to enter the session password for the vnc connection
 * Daniel Barboza Franco - Bug [233062] - Protocol connection port is static.
 * Fabio Rigo - Bug [238191] - Enhance exception handling
 * Daniel Barboza Franco (Eldorado Research Institute) - Bug [233064] - Add reconnection mechanism to avoid lose connection with the protocol
 * Fabio Rigo (Eldorado Research Institute) - [246212] - Enhance encapsulation of protocol implementer 
 * Fabio Rigo (Eldorado Research Institute) - [260559] - Enhance protocol framework and VNC viewer robustness
 ********************************************************************************/
package org.eclipse.tml.protocol;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.eclipse.tml.protocol.exceptions.MalformedProtocolExtensionException;
import org.eclipse.tml.protocol.internal.model.PluginProtocolModel;
import org.eclipse.tml.protocol.lib.IProtocolExceptionHandler;
import org.eclipse.tml.protocol.lib.IProtocolHandshake;
import org.eclipse.tml.protocol.lib.ProtocolActionDelegate;
import org.eclipse.tml.protocol.lib.ProtocolHandle;
import org.eclipse.tml.protocol.lib.ProtocolMessage;
import org.eclipse.tml.protocol.lib.exceptions.InvalidDefinitionException;
import org.eclipse.tml.protocol.lib.exceptions.InvalidMessageException;
import org.eclipse.tml.protocol.lib.exceptions.ProtocolException;
import org.eclipse.tml.protocol.lib.exceptions.ProtocolHandshakeException;
import org.eclipse.tml.protocol.lib.exceptions.ProtocolRawHandlingException;
import org.eclipse.tml.protocol.lib.msgdef.ProtocolMsgDefinition;

/**
 * DESCRIPTION: This class contains every method call that can be used by a
 * plugin that depends on this one. <br>
 * 
 * RESPONSIBILITY: Provide an unique entry point for actions demanded from
 * elements outside the plugin domain. Note that it is needed to have all the
 * protocol data correctly declared as extensions before using this class.<br>
 * 
 * COLABORATORS: None.<br>
 * 
 * USAGE: Call any of the public static methods to trigger actions on the
 * protocol framework, using the data declared at the extensions of the points
 * declared by this plugin.<br>
 * 
 */
public class PluginProtocolActionDelegate {

	/**
	 * Starts the protocol identified by protocolId, taking part as client in
	 * the communication
	 * 
	 * @param protocolId
	 *            The id of the protocol to run
	 * @param host
	 *            The host where the server is located
	 * 
	 * @return A handle to identify the connection just made
	 * 
	 * @throws ProtocolHandshakeException
	 *             DOCUMENT ME!!
	 * @throws MalformedProtocolExtensionException
	 *             DOCUMENT ME!!
	 */
	public static ProtocolHandle startClientProtocol(String protocolId,
			IProtocolExceptionHandler exceptionHandler, String host, int port,
			Map parameters) throws ProtocolHandshakeException,
			MalformedProtocolExtensionException {

		PluginProtocolModel model = PluginProtocolModel.getInstance();
		Map<Long, ProtocolMsgDefinition> allMessages = model
				.getAllProtocolMessages(protocolId);
		Collection<String> incomingMessages = model
				.getServerMessages(protocolId);
		Collection<String> outgoingMessages = model
				.getClientMessages(protocolId);
		IProtocolHandshake protocolInitializer = model
				.getProtocolInit(protocolId);
		boolean isBigEndianProtocol = model.isBigEndianProtocol(protocolId);

		return ProtocolActionDelegate.startClientProtocol(allMessages,
				incomingMessages, outgoingMessages, protocolInitializer,
				exceptionHandler, isBigEndianProtocol, host, port, parameters);
	}

	/**
	 * Starts the protocol identified by protocolId, taking part as server in
	 * the communication
	 * 
	 * @param protocolId
	 *            The id of the protocol to run
	 * @param serverPort
	 *            The port where the server will listen to requests
	 * @param exceptionHandler
	 *            An optional custom handler for exceptions caused in the framework            
     *
     * @return A handle to identify the connection just made
	 * 
	 * @throws IOException
	 *             DOCUMENT ME!!
	 * @throws ProtocolHandshakeException
	 *             DOCUMENT ME!!
	 * @throws MalformedProtocolExtensionException
	 *             DOCUMENT ME!!
	 */
	public static ProtocolHandle startServerProtocol(String protocolId, int serverPort, 
			IProtocolExceptionHandler exceptionHandler) throws IOException,
			ProtocolHandshakeException, MalformedProtocolExtensionException {

		PluginProtocolModel model = PluginProtocolModel.getInstance();
		Map<Long, ProtocolMsgDefinition> allMessages = model
				.getAllProtocolMessages(protocolId);
		Collection<String> incomingMessages = model
				.getClientMessages(protocolId);
		Collection<String> outgoingMessages = model
				.getServerMessages(protocolId);
		IProtocolHandshake protocolInitializer = model
				.getProtocolInit(protocolId);
		boolean isBigEndianProtocol = model.isBigEndianProtocol(protocolId);

		return ProtocolActionDelegate.startServerProtocol(serverPort, allMessages,
				incomingMessages, outgoingMessages, protocolInitializer,
				exceptionHandler, isBigEndianProtocol);
	}

	/**
	 * Stops the provided protocol instance
	 * 
	 * @param handle
	 *            An object provided at the connection time, that identifies 
	 *            the connection that is to be stopped
	 * 
	 * @throws IOException
	 *             DOCUMENT ME!!
	 */
	public static void stopProtocol(ProtocolHandle handle)
			throws IOException {

		ProtocolActionDelegate.stopProtocol(handle);
	}

	/**
	 * Restarts the provided protocol instance
	 * 
	 * @param handle
	 *            An object provided at the connection time, that identifies 
	 *            the connection that is to be restarted
	 * 
	 * @throws IOException
	 *             DOCUMENT ME!!
	 * @throws ProtocolHandshakeException
	 *             DOCUMENT ME!!
	 */
	public static void restartProtocol(ProtocolHandle handle)
			throws IOException, ProtocolHandshakeException {

		ProtocolActionDelegate.restartProtocol(handle);
	}

	/**
	 * Sends a message to the server part
	 * 
	 * @param handle
	 *            An object provided at the connection time, that identifies 
	 *            the client connection through which a message will be sent 
	 *            to the server to which it is connected
	 * 
	 * @param message
	 *            The message to send to the server
	 * 
	 * @throws IOException
	 *             DOCUMENT ME!!
	 * @throws ProtocolRawHandlingException
	 *             DOCUMENT ME!!
	 * @throws InvalidMessageException
	 *             DOCUMENT ME!!
	 * @throws InvalidDefinitionException
	 *             DOCUMENT ME!!
	 */
	public static void sendMessageToServer(
			ProtocolHandle handle, ProtocolMessage message)
			throws IOException, InvalidMessageException,
			InvalidDefinitionException, ProtocolRawHandlingException {

		ProtocolActionDelegate
				.sendMessageToServer(handle, message);
	}
	
	/**
	 * Tests if the protocol identified by handle is running or not
	 * 
	 * @param handle The handle that identifies a protocol instance
	 * 
	 * @return True if the protocol is running, false otherwise
	 */
	public static boolean isProtocolRunning(ProtocolHandle handle) {
	    return ProtocolActionDelegate.isProtocolRunning(handle);
	}
}
