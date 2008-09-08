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
 * Fabio Rigo - Bug [238191] - Enhance exception handling
 * Fabio Rigo - Bug [244067] - The exception handling interface should forward the protocol implementer object
 * Daniel Barboza Franco (Eldorado Research Institute) - Bug [233064] - Add reconnection mechanism to avoid lose connection with the protocol
 * Fabio Rigo (Eldorado Research Institute) - [246212] - Enhance encapsulation of protocol implementer 
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

import org.eclipse.tml.protocol.lib.IProtocolExceptionHandler;
import org.eclipse.tml.protocol.lib.IProtocolInit;
import org.eclipse.tml.protocol.lib.ProtocolHandle;
import org.eclipse.tml.protocol.lib.exceptions.InvalidDefinitionException;
import org.eclipse.tml.protocol.lib.exceptions.InvalidInputStreamDataException;
import org.eclipse.tml.protocol.lib.exceptions.InvalidMessageException;
import org.eclipse.tml.protocol.lib.exceptions.MessageHandleException;
import org.eclipse.tml.protocol.lib.exceptions.ProtocolInitException;
import org.eclipse.tml.protocol.lib.exceptions.ProtocolRawHandlingException;
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
	private Map<ProtocolHandle, ServerSocket> openedServerSockets = new HashMap<ProtocolHandle, ServerSocket>();

	/**
	 * A map containing all engine factories associated to each running server
	 * protocol
	 */
	private Map<ProtocolHandle, ServerProtocolEngineFactory> engineFactories = new HashMap<ProtocolHandle, ServerProtocolEngineFactory>();

	/**
	 * A map containing a collection of all engines running associated to each
	 * running server protocol
	 */
	private Map<ProtocolHandle, Collection<ProtocolEngine>> connectedClients = new HashMap<ProtocolHandle, Collection<ProtocolEngine>>();

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
	 * @param protocolInitializer
	 *            The sequence of steps to execute for connection initialization
	 * @param isBigEndianProtocol
	 *            True if the protocol is big endian, false if little endian
	 * 
	 * @throws IOException
	 *             DOCUMENT ME!!
	 * @throws ProtocolInitException
	 *             DOCUMENT ME!!
	 */
	public ProtocolHandle startListeningToPort(int portToBind,
			Map<Long, ProtocolMsgDefinition> allMessages,
			Collection<String> incomingMessages,
			Collection<String> outgoingMessages,
			IProtocolInit protocolInitializer,
			IProtocolExceptionHandler exceptionHandler,
			boolean isBigEndianProtocol) throws ProtocolInitException,
			IOException {

		if ((portToBind <= 0) || (allMessages == null)
				|| (incomingMessages == null) || (outgoingMessages == null)) {
			throw new ProtocolInitException(
					"Invalid parameters provided to method");
		}

        ProtocolHandle handle = new ProtocolHandle();
		ServerSocket serverSocket = new ServerSocket(portToBind);
		ServerProtocolEngineFactory factory = new ServerProtocolEngineFactory(
				handle, protocolInitializer, allMessages, incomingMessages, outgoingMessages,
				exceptionHandler, isBigEndianProtocol);
		openedServerSockets.put(handle, serverSocket);
		engineFactories.put(handle, factory);

		Runnable deamon = new ServerDeamon(handle, serverSocket, factory);
		Thread deamonThread = new Thread(deamon);
		deamonThread.start();
				
		return handle;
	}

	/**
	 * Stops listening to the port. This causes all the running server protocol
	 * engines to be stopped as well as a model cleanup.
	 * 
 	 * @param handle
	 *            The object that identifies the server socket that is to
	 *            stop listening to.
	 * 
	 * @throws IOException
	 *             DOCUMENT ME!!
	 */
	public void stopListeningToPort(ProtocolHandle handle)
			throws IOException {

		ServerSocket serverSocket = openedServerSockets
				.get(handle);
		if (serverSocket != null) {
			serverSocket.close();
		}
	}

	/**
	 * Restarts the provided protocol instance
	 * 
	 * @param handle
	 *            The object that identifies the server socket that is to
	 *            be reconnected.
	 * 
	 * @throws IOException
	 *             DOCUMENT ME!!
	 * @throws ProtocolInitException
	 *             DOCUMENT ME!!
	 */
	public void restartServerProtocol(ProtocolHandle handle)
			throws IOException, ProtocolInitException {

		ServerSocket ss = openedServerSockets.get(handle);
		
		if (ss != null) {
			int portToBind = ss.getLocalPort();
			ServerProtocolEngineFactory factory = engineFactories
					.get(handle);
	
			stopListeningToPort(handle);
			startListeningToPort(portToBind, factory.getAllMessages(), factory
					.getIncomingMessages(), factory.getOutgoingMessages(),
					null, factory.getExceptionHandler(), factory
							.isBigEndianProtocol());
		}
	}

	/**
	 * @see IModel#cleanStoppedProtocols()
	 */
	public void cleanStoppedProtocols() {

		Set<ProtocolHandle> keys = connectedClients.keySet();
		for (ProtocolHandle key : keys) {
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
		 * The object used to identify the server at the model
		 */
		private ProtocolHandle handle;

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
		 * Constructor. It sets all necessary parameters for running the deamon
         *
         * @param handle
	     *            The object used to map the server at the model
		 * @param serverSocket
		 *            The socket to listen to
		 * @param factory
		 *            The factory that builds new protocol engines
		 */
		public ServerDeamon(ProtocolHandle handle, ServerSocket serverSocket,
				ServerProtocolEngineFactory factory) {
		    this.handle = handle;
			this.serverSocket = serverSocket;
			this.factory = factory;
		}

		/**
		 * Runs the deamon
		 * 
		 * @see Runnable#run()
		 */
		public void run() {
			try {
				if ((serverSocket != null) && (factory != null)) {
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
						eng.startProtocol(s, null);

						Collection<ProtocolEngine> allClients = connectedClients
								.get(handle);
						if (allClients == null) {
							allClients = new HashSet<ProtocolEngine>();
							connectedClients.put(handle,
									allClients);
						}
						allClients.add(eng);
					}
				}
			} catch (Exception e) {
				IProtocolExceptionHandler exceptionHandler = factory
						.getExceptionHandler();

				if (exceptionHandler != null) {
					// Delegate the exception to user
					if (e instanceof IOException) {
						exceptionHandler.handleIOException(handle, (IOException) e);
					} else if (e instanceof ProtocolInitException) {
						exceptionHandler
								.handleProtocolInitException(handle, (ProtocolInitException) e);
					} else if (e instanceof MessageHandleException) {
						exceptionHandler
								.handleMessageHandleException(handle, (MessageHandleException) e);
					} else if (e instanceof InvalidMessageException) {
						exceptionHandler
								.handleInvalidMessageException(handle, (InvalidMessageException) e);
					} else if (e instanceof InvalidInputStreamDataException) {
						exceptionHandler
								.handleInvalidInputStreamDataException(handle, (InvalidInputStreamDataException) e);
					} else if (e instanceof InvalidDefinitionException) {
						exceptionHandler
								.handleInvalidDefinitionException(handle, (InvalidDefinitionException) e);
					} else if (e instanceof ProtocolRawHandlingException) {
						exceptionHandler
								.handleProtocolRawHandlingException(handle, (ProtocolRawHandlingException) e);
					}
				}

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

					openedServerSockets.remove(handle);
					engineFactories.remove(handle);
					connectedClients.remove(handle);

				} catch (IOException e) {
					IProtocolExceptionHandler exceptionHandler = factory
							.getExceptionHandler();

					if (exceptionHandler != null) {
						exceptionHandler.handleIOException(handle, e);
					}
				}
			}
		}
	}
}
