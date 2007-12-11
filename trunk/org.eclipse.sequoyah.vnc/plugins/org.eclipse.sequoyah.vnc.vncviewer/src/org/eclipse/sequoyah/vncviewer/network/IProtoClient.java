/********************************************************************************
 * Copyright (c) 2007 Motorola Inc. All rights reserved.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributor:
 * Daniel Franco (Motorola)
 *
 * Contributors:
 * {Name} (company) - description of contribution.
 ********************************************************************************/

package org.eclipse.tml.vncviewer.network;

import org.eclipse.tml.vncviewer.exceptions.ProtoClientException;

public interface IProtoClient {

	
	
	
	/**
	 * Starts the protocol. This method sends input events, requests display updates and processes the received data.
	 * @param host the host String. 
	 * @param port the server port number.
	 */
	abstract public void runProtocol(String host, int port) throws Exception;
	
	
	/**
	 * Performs the restart of a running Protocol instance, using the same port and host predefined in the run method.
	 */
	abstract public void restartProtocol() throws Exception;
	
	
	
	/**
	 *  Stops the protocol execution. Closes the connection to the server and kills all running threads.
	 * @throws ProtoClientException 
	 */
	abstract public void stopProtocol() throws ProtoClientException;
	
	
	
	
	/**
	 * Send to the server a key event fired at the client's side.
	 */
	abstract public void keyEvent(VNCKeyEvent event) throws Exception;
	
	
	/**
	 * Send to the server a key event fired at the client's side.
	 */
	abstract public void mouseEvent(VNCMouseEvent event) throws Exception;

	
	
	/**
	 * Performs a framebuffer update request.
	 */
	abstract public void fbUpdateRequest(boolean incremental) throws Exception;


	/**
	 * Returns the IPainter assigned to this client.
	 */
	abstract public IPainter getPainter();
	
	
	/**
	 * Returns the current server name.
	 */
	abstract public String getServerName();
	
	/**
	 * Returns the screen height. 
	 */
	abstract int getHeight();
	
	/**
	 * Returns the screen width.
	 */
	abstract int getwidth();

	/**
	 * Flag indicating whether the paint methods are enabled or not.
	 * Used when the protocol is running but there is no UI object to render the images.  
	 */
	abstract boolean isPaintEnabled();
	
	/**
	 * Set the paintEnabled flag value.
	 */
	abstract void setPaintEnabled(boolean enabled);
	
	
	
}

