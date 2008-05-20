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
package org.eclipse.tml.protocol.internal.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.tml.protocol.internal.reader.ProtocolExtensionsReader;
import org.eclipse.tml.protocol.lib.IProtocolImplementer;
import org.eclipse.tml.protocol.lib.exceptions.ProtocolException;
import org.eclipse.tml.protocol.lib.msgdef.ProtocolMsgDefinition;

/**
 * DESCRIPTION: This class controls all data that can be retrieved from the
 * extension points regarding protocols, messages and message orientations. <br>
 * 
 * RESPONSIBILITY: Read data from extension registry on demand for framework
 * use.<br>
 * 
 * COLABORATORS: MessageDefReader: Reads the extension points and returns the
 * data stored by this model.<br>
 * 
 * USAGE: Get the instance and call any of the public methods to get protocol
 * data from the protocol framework extension points.<br>
 * 
 */
public class PluginProtocolModel {

	/**
	 * The unique instance of this model
	 */
	private static PluginProtocolModel instance;

	/**
	 * A map containing the beans that hold data from the ProtocolImplementer
	 * extensions
	 */
	private Map<String, ProtocolBean> protocolDataMap = new HashMap<String, ProtocolBean>();

	/**
	 * A map that holds data from the ProtocolMessage extensions
	 */
	private Map<String, Map<Long, ProtocolMsgDefinition>> allMessagesMap = new HashMap<String, Map<Long, ProtocolMsgDefinition>>();

	/**
	 * A map that holds server message declarations from the
	 * ProtocolMessageOrientation extensions
	 */
	private Map<String, Collection<String>> serverMessagesMap = new HashMap<String, Collection<String>>();

	/**
	 * A map that holds client message declarations from the
	 * ProtocolMessageOrientation extensions
	 */
	private Map<String, Collection<String>> clientMessagesMap = new HashMap<String, Collection<String>>();

	/**
	 * Empty private constructor. It is part of the singleton pattern
	 */
	private PluginProtocolModel() {
		// Do nothing
	}

	/**
	 * Gets the single instance of the model
	 * 
	 * @return The single instance of the model
	 */
	public static PluginProtocolModel getInstance() {

		if (instance == null) {
			instance = new PluginProtocolModel();
		}

		return instance;
	}

	/**
	 * Tests if the protocol identified by protocol id is a big endian protocol
	 * 
	 * @param protocolId
	 *            The identifier of the protocol to be tested
	 * 
	 * @return True if the protocol is big endian. False otherwise.
	 * 
	 * @throws ProtocolException
	 *             DOCUMENT ME!!
	 */
	public boolean isBigEndianProtocol(String protocolId)
			throws ProtocolException {

		ProtocolBean bean = protocolDataMap.get(protocolId);
		if (bean == null) {
			bean = ProtocolExtensionsReader.readProtocolImplDef(protocolId);
			protocolDataMap.put(protocolId, bean);
		}

		return bean.isBigEndianProtocol();
	}

	/**
	 * Creates and returns a new instance of the class declared as the
	 * implementer for the protocol identified by protocolId
	 * 
	 * @param protocolId
	 *            The identifier of the protocol to which an implementer is
	 *            needed
	 * 
	 * @return A new instance of the implementer of the provided protocol
	 * 
	 * @throws ProtocolException
	 *             DOCUMENT ME!!
	 */
	public IProtocolImplementer getProtocolImplementer(String protocolId)
			throws ProtocolException {

		ProtocolBean bean = protocolDataMap.get(protocolId);
		if (bean == null) {
			bean = ProtocolExtensionsReader.readProtocolImplDef(protocolId);
			protocolDataMap.put(protocolId, bean);
		}

		return bean.getProtocolImplementer();
	}

	/**
	 * Retrieves the declared port to which the protocol server is locally bound
	 * 
	 * @param protocolId
	 *            The identifier of the protocol
	 * 
	 * @return The number of the port that the protocol server binds to
	 * 
	 * @throws ProtocolException
	 *             DOCUMENT ME!!
	 */
	public int getServerPort(String protocolId) throws ProtocolException {

		ProtocolBean bean = protocolDataMap.get(protocolId);
		if (bean == null) {
			bean = ProtocolExtensionsReader.readProtocolImplDef(protocolId);
			protocolDataMap.put(protocolId, bean);
		}

		return bean.getServerPort();
	}

	/**
	 * Retrieves a map containing all protocol message definitions that belongs
	 * to the provided protocol
	 * 
	 * @param protocolId
	 *            The identifier of the protocol which message definitions are
	 *            needed
	 * 
	 * @return A map containing all messages that belongs to the provided
	 *         protocol
	 * 
	 * @throws ProtocolException
	 *             DOCUMENT ME!!
	 */
	public Map<Long, ProtocolMsgDefinition> getAllProtocolMessages(
			String protocolId) throws ProtocolException {

		Map<Long, ProtocolMsgDefinition> allMessages = allMessagesMap
				.get(protocolId);
		if (allMessages == null) {
			allMessages = ProtocolExtensionsReader.readMessageDefinitions(protocolId);
		}

		return allMessages;
	}

	/**
	 * Gets a collection of all client messages of the provided protocol
	 * 
	 * @param protocolId
	 *            The protocol that owns the desired client messages
	 * 
	 * @return A collection of all client messages of the provided protocol
	 * 
	 * @throws ProtocolException
	 *             DOCUMENT ME!!
	 */
	public Collection<String> getClientMessages(String protocolId)
			throws ProtocolException {

		Collection<String> clientMessages = clientMessagesMap.get(protocolId);
		if (clientMessages == null) {
			clientMessages = ProtocolExtensionsReader.readClientMessages(protocolId);
		}

		return clientMessages;
	}

	/**
	 * Gets a collection of all server messages of the provided protocol
	 * 
	 * @param protocolId
	 *            The protocol that owns the desired server messages
	 * 
	 * @return A collection of all server messages of the provided protocol
	 * 
	 * @throws ProtocolException
	 *             DOCUMENT ME!!
	 */
	public Collection<String> getServerMessages(String protocolId)
			throws ProtocolException {

		Collection<String> serverMessages = serverMessagesMap.get(protocolId);
		if (serverMessages == null) {
			serverMessages = ProtocolExtensionsReader.readServerMessages(protocolId);
		}

		return serverMessages;
	}
}
