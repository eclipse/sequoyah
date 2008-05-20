/********************************************************************************
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributor:
 * Fabio Rigo
 *
 * Contributors:
 * {Name} (company) - description of contribution.
 ********************************************************************************/
package org.eclipse.tml.protocol.lib.internal.model;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.tml.protocol.lib.IProtocolImplementer;
import org.eclipse.tml.protocol.lib.ProtocolMessage;
import org.eclipse.tml.protocol.lib.exceptions.ProtocolException;
import org.eclipse.tml.protocol.lib.internal.engine.ProtocolEngine;
import org.eclipse.tml.protocol.lib.msgdef.ProtocolMsgDefinition;

/**
 * DESCRIPTION: This class implements a model for protocols running as client
 * parts.<br>
 * 
 * RESPONSIBILITY: Maintain record of all protocols running as client,
 * associating them to their running engines. Provide actions to be performed at
 * protocol running as clients.<br>
 * 
 * COLABORATORS: None.<br>
 * 
 * USAGE: This class is intended to be used by the protocol framework only.
 * 
 */
public class ClientModel implements IModel {

	/**
	 * The only instance of this class
	 */
	private static ClientModel instance;

	/**
	 * A map containing an association of each client protocol with its running
	 * engine
	 */
	private Map<IProtocolImplementer, ProtocolEngine> runningEngines = new HashMap<IProtocolImplementer, ProtocolEngine>();

	/**
	 * Private constructor to fit singleton pattern
	 */
	private ClientModel() {
		// Do nothing
	}

	/**
	 * Gets the single instance of this model
	 * 
	 * @return The model
	 */
	public static ClientModel getInstance() {

		if (instance == null) {
			instance = new ClientModel();
		}

		return instance;
	}

	/**
	 * Starts a protocol, taking part as client in the communication
	 * 
	 * @param allMessages
	 *            A map containing all messages that belong to the protocol. The
	 *            message code as key
	 * @param incomingMessages
	 *            A collection containing the message ids of all incoming
	 *            messages. The message ids much match the id field in a
	 *            ProtocolMsgDefinition object from the allMessages map
	 * @param outgoingMessages
	 *            A collection containing the message ids of all outgoing
	 *            messages. The message ids much match the id field in a
	 *            ProtocolMsgDefinition object from the allMessages map
	 * @param protocolImplementer
	 *            An instance of the implementer object. This object contains
	 *            the initialization procedure definition, as well as any needed
	 *            protocol instance particular data
	 * @param isBigEndianProtocol
	 *            True if the protocol is big endian, false if little endian
	 * @param host
	 *            The host where the server is running
	 * @param port
	 *            The ported where the server is listening for requests at host
	 * 
	 * @throws UnknownHostException
	 *             DOCUMENT ME!!
	 * @throws IOException
	 *             DOCUMENT ME!!
	 * @throws ProtocolException
	 *             DOCUMENT ME!!
	 */
	public void startClientProtocol(
			Map<Long, ProtocolMsgDefinition> allMessages,
			Collection<String> incomingMessages,
			Collection<String> outgoingMessages,
			IProtocolImplementer protocolImplementer,
			boolean isBigEndianProtocol, String host, int port)
			throws UnknownHostException, IOException, ProtocolException {

		ProtocolEngine eng = new ProtocolEngine(allMessages, incomingMessages,
				outgoingMessages, isBigEndianProtocol);
		eng.startProtocol(protocolImplementer, host, port, false);
		runningEngines.put(protocolImplementer, eng);
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
	public void stopClientProtocol(IProtocolImplementer protocolImplementer)
			throws IOException {

		ProtocolEngine eng = runningEngines.get(protocolImplementer);
		if (eng != null) {
			eng.stopProtocol();
		}
		runningEngines.remove(eng);
	}

	/**
	 * Restarts the provided protocol instance
	 * 
	 * @param protocolImplementer
	 *            The protocol instance that is to be restarted
	 * 
	 * @throws IOException
	 *             DOCUMENT ME!!
	 * @throws ProtocolException
	 *             DOCUMENT ME!!
	 */
	public void restartClientProtocol(IProtocolImplementer protocolImplementer)
			throws IOException, ProtocolException {
		ProtocolEngine eng = runningEngines.get(protocolImplementer);
		if (eng != null) {
			eng.restartProtocol();
		}
		runningEngines.remove(eng);
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
	 * @throws ProtocolException
	 *             DOCUMENT ME!!
	 */
	public void sendMessage(IProtocolImplementer protocolImplementer,
			ProtocolMessage message) throws ProtocolException, IOException {

		ProtocolEngine eng = runningEngines.get(protocolImplementer);
		if (eng != null) {
			eng.sendMessage(message);
		}
	}

	/**
	 * @see IModel#cleanStoppedProtocols()
	 */
	public void cleanStoppedProtocols() {

		Set<IProtocolImplementer> keys = runningEngines.keySet();
		for (IProtocolImplementer key : keys) {
			ProtocolEngine aEng = runningEngines.get(key);
			if (!aEng.isConnected()) {
				runningEngines.remove(aEng);
			}
		}
	}
}
