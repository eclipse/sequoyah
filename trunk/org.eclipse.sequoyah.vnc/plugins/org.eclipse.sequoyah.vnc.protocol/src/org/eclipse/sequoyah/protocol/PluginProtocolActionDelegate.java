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
 ********************************************************************************/
package org.eclipse.tml.protocol;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.eclipse.tml.protocol.exceptions.MalformedProtocolExtensionException;
import org.eclipse.tml.protocol.internal.model.PluginProtocolModel;
import org.eclipse.tml.protocol.lib.IProtocolExceptionHandler;
import org.eclipse.tml.protocol.lib.IProtocolImplementer;
import org.eclipse.tml.protocol.lib.ProtocolActionDelegate;
import org.eclipse.tml.protocol.lib.ProtocolMessage;
import org.eclipse.tml.protocol.lib.exceptions.InvalidDefinitionException;
import org.eclipse.tml.protocol.lib.exceptions.InvalidMessageException;
import org.eclipse.tml.protocol.lib.exceptions.ProtocolInitException;
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
	 * @return An IProtocolImplementer instance that represents the client
	 *         protocol just started. It shall be kept by the callee for
	 *         instance manipulation (if the protocol declarer is keeping
	 *         instance information at the IProtocolImplementer object) and for
	 *         requiring further actions from this class, such as stopping,
	 *         restarting and sending messages through this instance.
	 * 
	 * @throws IOException
	 *             DOCUMENT ME!!
	 * @throws ProtocolInitException
	 *             DOCUMENT ME!!
	 * @throws MalformedProtocolExtensionException
	 *             DOCUMENT ME!!
	 */
	public static IProtocolImplementer startClientProtocol(String protocolId,
			IProtocolExceptionHandler exceptionHandler, String host, int port,
			Map parameters) throws IOException, ProtocolInitException,
			MalformedProtocolExtensionException {

		PluginProtocolModel model = PluginProtocolModel.getInstance();
		Map<Long, ProtocolMsgDefinition> allMessages = model
				.getAllProtocolMessages(protocolId);
		Collection<String> incomingMessages = model
				.getServerMessages(protocolId);
		Collection<String> outgoingMessages = model
				.getClientMessages(protocolId);
		IProtocolImplementer protocolImplementer = model
				.getProtocolImplementer(protocolId);
		boolean isBigEndianProtocol = model.isBigEndianProtocol(protocolId);

		ProtocolActionDelegate.startClientProtocol(allMessages,
				incomingMessages, outgoingMessages, protocolImplementer,
				exceptionHandler, isBigEndianProtocol, host, port, parameters);

		return protocolImplementer;
	}

	/**
	 * Starts the protocol identified by protocolId, taking part as server in
	 * the communication
	 * 
	 * @param protocolId
	 *            The id of the protocol to run
	 * 
	 * @return An IProtocolImplementer instance that represents the server
	 *         protocol just started. It shall be kept by the callee for
	 *         instance manipulation (if the protocol declarer is keeping
	 *         instance information at the IProtocolImplementer object) and for
	 *         requiring further actions from this class, such as stopping and
	 *         restarting
	 * 
	 * @throws IOException
	 *             DOCUMENT ME!!
	 * @throws ProtocolInitException
	 *             DOCUMENT ME!!
	 * @throws MalformedProtocolExtensionException
	 *             DOCUMENT ME!!
	 */
	public static IProtocolImplementer startServerProtocol(String protocolId,
			IProtocolExceptionHandler exceptionHandler) throws IOException,
			ProtocolInitException, MalformedProtocolExtensionException {

		PluginProtocolModel model = PluginProtocolModel.getInstance();
		Map<Long, ProtocolMsgDefinition> allMessages = model
				.getAllProtocolMessages(protocolId);
		Collection<String> incomingMessages = model
				.getClientMessages(protocolId);
		Collection<String> outgoingMessages = model
				.getServerMessages(protocolId);
		IProtocolImplementer protocolImplementer = model
				.getProtocolImplementer(protocolId);
		boolean isBigEndianProtocol = model.isBigEndianProtocol(protocolId);
		int serverPort = model.getServerPort(protocolId);

		ProtocolActionDelegate.startServerProtocol(serverPort, allMessages,
				incomingMessages, outgoingMessages, protocolImplementer,
				exceptionHandler, isBigEndianProtocol);

		return protocolImplementer;
	}

	/**
	 * Stops the provided protocol instance
	 * 
	 * @param protocolImplementer
	 *            The protocol instance that is to be stopped
	 * 
	 * @throws IOException
	 *             DOCUMENT ME!!
	 */
	public static void stopProtocol(IProtocolImplementer protocolImplementer)
			throws IOException {

		ProtocolActionDelegate.stopProtocol(protocolImplementer);
	}

	/**
	 * Restarts the provided protocol instance
	 * 
	 * @param protocolImplementer
	 *            The protocol instance that is to be restarted
	 * 
	 * @throws IOException
	 *             DOCUMENT ME!!
	 * @throws ProtocolInitException
	 *             DOCUMENT ME!!
	 */
	public static void restartProtocol(IProtocolImplementer protocolImplementer)
			throws IOException, ProtocolInitException {

		ProtocolActionDelegate.restartProtocol(protocolImplementer);
	}

	/**
	 * Sends a message to the server part
	 * 
	 * @param protocolImplementer
	 *            The client protocol instance that is to send a message to the
	 *            server it is connected to
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
			IProtocolImplementer protocolImplementer, ProtocolMessage message)
			throws IOException, InvalidMessageException,
			InvalidDefinitionException, ProtocolRawHandlingException {

		ProtocolActionDelegate
				.sendMessageToServer(protocolImplementer, message);
	}
}
