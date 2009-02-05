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
 * Daniel Barboza Franco (Eldorado Research Institute) - Bug [233064] - Add reconnection mechanism to avoid lose connection with the protocol
 * Fabio Rigo (Eldorado Research Institute) - [246212] - Enhance encapsulation of protocol implementer
 * Fabio Rigo (Eldorado Research Institute) - [260559] - Enhance protocol framework and VNC viewer robustness
 ********************************************************************************/
package org.eclipse.tml.protocol.lib.internal.model;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.tml.protocol.lib.IProtocolExceptionHandler;
import org.eclipse.tml.protocol.lib.IProtocolHandshake;
import org.eclipse.tml.protocol.lib.ProtocolHandle;
import org.eclipse.tml.protocol.lib.ProtocolMessage;
import org.eclipse.tml.protocol.lib.exceptions.InvalidDefinitionException;
import org.eclipse.tml.protocol.lib.exceptions.InvalidMessageException;
import org.eclipse.tml.protocol.lib.exceptions.ProtocolException;
import org.eclipse.tml.protocol.lib.exceptions.ProtocolHandshakeException;
import org.eclipse.tml.protocol.lib.exceptions.ProtocolRawHandlingException;
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
	private Map<ProtocolHandle, ProtocolEngine> runningEngines = new HashMap<ProtocolHandle, ProtocolEngine>();

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
	 * @param protocolInitializer
	 *            The sequence of steps to execute for connection initialization
	 * @param isBigEndianProtocol
	 *            True if the protocol is big endian, false if little endian
	 * @param host
	 *            The host where the server is running
	 * @param port
	 *            The ported where the server is listening for requests at host
	 * @param parameters
	 *            A Map with parameters other than host and port, for customization purposes. Accepts null if apply.
	 * 
	 * @throws UnknownHostException
	 *             DOCUMENT ME!!
	 * @throws IOException
	 *             DOCUMENT ME!!
	 * @throws ProtocolHandshakeException
	 *             DOCUMENT ME!!
	 */
	public ProtocolHandle startClientProtocol(
			Map<Long, ProtocolMsgDefinition> allMessages,
			Collection<String> incomingMessages,
			Collection<String> outgoingMessages,
			IProtocolHandshake protocolInitializer,
			IProtocolExceptionHandler exceptionHandler,
			Boolean isBigEndianProtocol,
			String host, int port,
			Map <String, Object> parameters)
			throws ProtocolHandshakeException {

		Integer retriesObj = (Integer) parameters.get("connectionRetries"); //$NON-NLS-1$
		int retries  = (retriesObj != null) ? retriesObj : -1;
		
		ProtocolHandle handle = new ProtocolHandle();
		ProtocolEngine eng = new ProtocolEngine(handle, protocolInitializer, allMessages, incomingMessages,
				outgoingMessages, exceptionHandler, isBigEndianProtocol, false, retries);
		eng.requestStart(host, port, parameters, Thread.currentThread());
		runningEngines.put(handle, eng);
		
		// TODO: Think of a better way to handle the protocol start delay than a busy wait
		while (!eng.isRunning())
		{
		    try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                // An interruption will happen if the start operation fails. 
                // In this case, abort execution of the start operation
                runningEngines.remove(handle);
                eng.dispose();
                throw new ProtocolHandshakeException("Protocol start error");
            }
		}
		
		return handle;
	}

	/**
	 * Stops the provided protocol instance
	 * 
	 * @param handle
	 *            The object that identifies the connection that is to
	 *            be stopped.
	 * 
	 * @throws IOException
	 *             DOCUMENT ME!!
	 */
	public void stopClientProtocol(ProtocolHandle handle)
			throws IOException {

		ProtocolEngine eng = runningEngines.get(handle);
		if (eng != null) {
			eng.requestStop();
			
		     // TODO: Think of a better way to handle the protocol stop delay than a busy wait
	        while (eng.isRunning())
	        {
	            try {
	                Thread.sleep(50);
	            } catch (InterruptedException e) {
	                // Do nothing
	            }
	        }
			
			eng.dispose();
		}	
		runningEngines.remove(handle);
	}

	/**
	 * Restarts the provided protocol instance
	 * 
 	 * @param handle
	 *            The object that identifies the connection that is to 
	 *            be restarted.
	 * 
	 * @return True if the restart was requested; false otherwise
	 */
	public boolean restartClientProtocol(ProtocolHandle handle) {
	    boolean restartRequested = false;
		ProtocolEngine eng = runningEngines.get(handle);
		if (eng != null) {
			eng.requestRestart();
			restartRequested = true; 
		} 
		return restartRequested;
	}

	/**
	 * Sends a message to the server part
	 * 
	 * @param handle
	 *            The object that identifies by which connection the 
	 *            message will be sent.
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
	public void sendMessage(ProtocolHandle handle,
			ProtocolMessage message) throws ProtocolRawHandlingException,
			InvalidMessageException, InvalidDefinitionException, IOException {

		ProtocolEngine eng = runningEngines.get(handle);
		if (eng != null) {
			eng.requestSendMessage(message);
		}
	}

	/**
	 * @see IModel#cleanStoppedProtocols()
	 */
	public void cleanStoppedProtocols() {

		Set<ProtocolHandle> keys = runningEngines.keySet();
		for (ProtocolHandle key : keys) {
			ProtocolEngine aEng = runningEngines.get(key);
			if (!aEng.isConnected()) {
			    aEng.dispose();
				runningEngines.remove(key);
			}
		}
	}
	
	/**
	 * Tests if the protocol identified by handle is running
	 * 
	 * @param handle The handle that identify the protocol instance
	 * 
	 * @return True if the protocol is running; false otherwise
	 */
	public boolean isClientProtocolRunning(ProtocolHandle handle)
	{
	    boolean isRunning = false;
	    
	    ProtocolEngine eng = runningEngines.get(handle);
	    if (eng != null) {
	        isRunning = eng.isRunning();
	    }
	    
	    return isRunning;
	}
}
