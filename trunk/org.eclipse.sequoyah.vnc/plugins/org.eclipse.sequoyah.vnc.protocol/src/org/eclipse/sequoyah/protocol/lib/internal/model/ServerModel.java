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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.tml.protocol.lib.IProtocolImplementer;
import org.eclipse.tml.protocol.lib.exceptions.ProtocolException;
import org.eclipse.tml.protocol.lib.internal.engine.ProtocolEngine;
import org.eclipse.tml.protocol.lib.msgdef.ProtocolMsgDefinition;

/**
 * DESCRIPTION: This class implements a model for protocols running as server
 * parts.<br>
 * 
 * RESPONSIBILITY: Maintain record of all protocols running as server,
 * associating them to their listening sockets, engine factories and running
 * engines. Provide actions to be performed at protocol running as servers.<br>
 * 
 * COLABORATORS: None.<br>
 * 
 * USAGE: This class is intended to be used by the protocol framework only.
 * 
 */
public class ServerModel implements IModel {

	/**
	 * The only instance of this class
	 */
	private static ServerModel instance;

	/**
	 * A map containing all sockets that are currently listening to ports
	 * associated to the running server protocols
	 */
	private Map<IProtocolImplementer, ServerSocket> openedServerSockets = new HashMap<IProtocolImplementer, ServerSocket>();

	/**
	 * A map containing all engine factories associated to each running server
	 * protocol
	 */
	private Map<IProtocolImplementer, ServerProtocolEngineFactory> engineFactories = new HashMap<IProtocolImplementer, ServerProtocolEngineFactory>();

	/**
	 * A map containing a collection of all engines running associated to each
	 * running server protocol
	 */
	private Map<IProtocolImplementer, Collection<ProtocolEngine>> connectedClients = new HashMap<IProtocolImplementer, Collection<ProtocolEngine>>();

	/**
	 * Private constructor to fit singleton pattern
	 */
	private ServerModel() {
		// Do nothing
	}

	/**
	 * Gets the single instance of this model
	 * 
	 * @return The model
	 */
	public static ServerModel getInstance() {

		if (instance == null) {
			instance = new ServerModel();
		}

		return instance;
	}

	/**
	 * Starts listening to a local port. This allows a client to connect and a
	 * protocol engine to be started upon the connection event
	 * 
	 * @param portToBind
	 *            The local port where to bind the server to
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
	 * 
	 * @throws IOException
	 *             DOCUMENT ME!!
	 * @throws ProtocolException
	 *             DOCUMENT ME!!
	 */
	public void startListeningToPort(int portToBind,
			Map<Long, ProtocolMsgDefinition> allMessages,
			Collection<String> incomingMessages,
			Collection<String> outgoingMessages,
			IProtocolImplementer protocolImplementer,
			boolean isBigEndianProtocol) throws ProtocolException, IOException {

		if ((portToBind <= 0) || (allMessages == null)
				|| (incomingMessages == null) || (outgoingMessages == null)
				|| (protocolImplementer == null)) {
			throw new ProtocolException("Invalid parameters provided to method");
		}

		ServerSocket serverSocket = new ServerSocket(portToBind);
		ServerProtocolEngineFactory factory = new ServerProtocolEngineFactory(
				allMessages, incomingMessages, outgoingMessages,
				isBigEndianProtocol);
		openedServerSockets.put(protocolImplementer, serverSocket);
		engineFactories.put(protocolImplementer, factory);

		Runnable deamon = new ServerDeamon(serverSocket, factory,
				protocolImplementer);
		Thread deamonThread = new Thread(deamon);
		deamonThread.start();
	}

	/**
	 * Stops listening to the port. This causes all the running server protocol
	 * engines to be stopped as well as a model cleanup.
	 * 
	 * @param protocolImplementer
	 *            The protocol instance that is to be stopped
	 * 
	 * @throws IOException
	 *             DOCUMENT ME!!
	 */
	public void stopListeningToPort(IProtocolImplementer protocolImplementer)
			throws IOException {

		ServerSocket serverSocket = openedServerSockets
				.get(protocolImplementer);
		if (serverSocket != null) {
			serverSocket.close();
		}
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
	public void restartServerProtocol(IProtocolImplementer protocolImplementer)
			throws IOException, ProtocolException {

		ServerSocket ss = openedServerSockets.get(protocolImplementer);
		int portToBind = ss.getLocalPort();
		ServerProtocolEngineFactory factory = engineFactories
				.get(protocolImplementer);

		stopListeningToPort(protocolImplementer);
		startListeningToPort(portToBind, factory.getAllMessages(), factory
				.getIncomingMessages(), factory.getOutgoingMessages(),
				protocolImplementer, factory.isBigEndianProtocol());
	}

	/**
	 * @see IModel#cleanStoppedProtocols()
	 */
	public void cleanStoppedProtocols() {

		Set<IProtocolImplementer> keys = connectedClients.keySet();
		for (IProtocolImplementer key : keys) {
			Collection<ProtocolEngine> aImplCollection = connectedClients
					.get(key);
			for (ProtocolEngine aImpl : aImplCollection) {
				if (!aImpl.isConnected()) {
					aImplCollection.remove(aImpl);
				}
			}
		}
	}

	/**
	 * DESCRIPTION: This class is the deamon that runs after a port starts to be
	 * listened to. <br>
	 * 
	 * RESPONSIBILITY: It keeps waiting for client connections, and when it
	 * receives one it starts a new protocol engine to communicate with that
	 * client.<br>
	 * 
	 * COLABORATORS: None.<br>
	 * 
	 * USAGE: This class is intended to be used by the server model only.<br>
	 * 
	 */
	private class ServerDeamon implements Runnable {

		/**
		 * The server socket that is used for listening to incoming connections
		 */
		private ServerSocket serverSocket;

		/**
		 * A factory that is able to create new engines to handle client
		 * connections
		 */
		private ServerProtocolEngineFactory factory;

		/**
		 * The protocol that is being run by the server
		 */
		private IProtocolImplementer protocolImplementer;

		/**
		 * Constructor. It sets all necessary parameters for running the deamon
		 * 
		 * @param serverSocket
		 *            The socket to listen to
		 * @param factory
		 *            The factory that builds new protocol engines
		 * @param protocolImplementer
		 *            The protocol implementer being run by the server
		 */
		public ServerDeamon(ServerSocket serverSocket,
				ServerProtocolEngineFactory factory,
				IProtocolImplementer protocolImplementer) {
			this.serverSocket = serverSocket;
			this.factory = factory;
			this.protocolImplementer = protocolImplementer;
		}

		/**
		 * Runs the deamon
		 * 
		 * @see Runnable#run()
		 */
		public void run() {
			try {
				if ((serverSocket != null) && (factory != null)
						&& (protocolImplementer != null)) {
					while (true) {
						final Socket s;
						try {
							s = serverSocket.accept();
						} catch (IOException e) {
							// stopListeningToPort closes the server socket and
							// causes this exception to happen. The treatment
							// for this is to exit the while(true) block to
							// allow the cleanup to be performed (in finally
							// block) and then finish the thread.
							break;
						}

						final ProtocolEngine eng = factory
								.getServerProtocolEngine();
						eng.startProtocol(protocolImplementer, s, true);

						Collection<ProtocolEngine> allClients = connectedClients
								.get(protocolImplementer);
						if (allClients == null) {
							allClients = new HashSet<ProtocolEngine>();
							connectedClients.put(protocolImplementer,
									allClients);
						}
						allClients.add(eng);
					}
				}
			} catch (Exception e) {
				// TODO This is a temporary exception handling
				e.printStackTrace();
			} finally {
				try {
					// Perform the cleanup before finishing the thread
					// execution. This includes stopping all connections
					// from server side and removing the created objects
					// from model
					if (!serverSocket.isClosed()) {
						serverSocket.close();
					}

					Collection<ProtocolEngine> allClients = connectedClients
							.get(serverSocket.getLocalPort());
					for (ProtocolEngine aClient : allClients) {
						aClient.stopProtocol();
						allClients.remove(aClient);
					}

					openedServerSockets.remove(protocolImplementer);
					engineFactories.remove(protocolImplementer);
					connectedClients.remove(protocolImplementer);

				} catch (IOException e) {
					// TODO This is a temporary exception handling
					e.printStackTrace();
				}
			}
		}
	}
}
