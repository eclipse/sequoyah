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
 ********************************************************************************/
package org.eclipse.tml.protocol.lib;

import java.io.DataInputStream;
import java.io.OutputStream;
import java.util.Map;

import org.eclipse.tml.protocol.lib.exceptions.ProtocolException;

/**
 * DESCRIPTION: This interface describes the contract to be used by an object
 * which purpose is to define a protocol implementer instance. <br>
 * 
 * RESPONSIBILITY: Define methods that shall be implemented by objects that
 * represents a protocol implementer instance. At least one of the
 * initialization methods must be provided for the protocol to work.<br>
 * 
 * COLABORATORS: None.<br>
 * 
 * USAGE: The protocol implementer is declared by the user but instantiated by
 * the protocol framework at protocol start time. The initialization methods are
 * executed at this moment. It is expected that the user maintain at this class
 * any data referring to a single instance of the part (server or client)
 * running the protocol.<br>
 * 
 */
public interface IProtocolImplementer {

	/**
	 * Performs protocol initialization at server side. If this protocol
	 * implementation does not include the server part, the method can be left
	 * blank.<br>
	 * <br>
	 * In this method, it is expected that each protocol class implement its
	 * initialization routine (including handshaking), until the normal message
	 * exchange phase is reached.
	 * 
	 * @param in
	 *            The input stream where to read data sent by the server
	 * @param out
	 *            The output stream where to write data to send to the server
	 * @param parameters
	 *            A Map with parameters other than host and port, for customization purposes. Accepts null if apply.            
	 * 
	 * @throws ProtocolException
	 *             If during the initialization an error that prevents the
	 *             protocol to continue working is identified.
	 */
	void serverInit(DataInputStream in, OutputStream out, Map parameters)
			throws ProtocolException;

	/**
	 * Performs protocol initialization at client side. If this protocol
	 * implementation does not include the client part, the method can be left
	 * blank.<br>
	 * <br>
	 * In this method, it is expected that each protocol class implement its
	 * initialization routine (including handshaking), until the normal message
	 * exchange phase is reached.
	 * 
	 * @param in
	 *            The input stream where to read data sent by the client
	 * @param out
	 *            The output stream where to write data to send to the client
	 * 
	 * @throws ProtocolException
	 *             If during the initialization an error that prevents the
	 *             protocol to continue working is identified.
	 */
	void clientInit(DataInputStream in, OutputStream out, Map parameters)
			throws ProtocolException;
}
