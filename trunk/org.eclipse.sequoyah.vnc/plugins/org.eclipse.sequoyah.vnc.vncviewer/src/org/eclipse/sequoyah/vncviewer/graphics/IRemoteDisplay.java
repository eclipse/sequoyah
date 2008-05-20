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
 * Fabio Rigo - Bug [221741] - Support to VNC Protocol Extension
 ********************************************************************************/

package org.eclipse.tml.vncviewer.graphics;

import org.eclipse.swt.widgets.Event;
import org.eclipse.tml.protocol.lib.IProtocolImplementer;
import org.eclipse.tml.vncviewer.config.IPropertiesFileHandler;

/**
 * This interface defines the default behavior of a Remote Display component.
 * <br>
 * <br>
 * Classes implementing this Interface must extend the widget container
 * corresponding to the specific tool kit. Example: SWT - implementors using SWT
 * must extend the Composite class.
 */
public interface IRemoteDisplay {

	/**
	 * Creates the connection to the server using the protocol specified.
	 * 
	 * @param protocol
	 *            the IProtocolImplementer used within the IRemoteDisplay.
	 */
	public void start(IProtocolImplementer protocol) throws Exception;

	// public void start(String host, int port, IProtoClient protocol) throws
	// Exception;

	/**
	 * Stops the connection with the server.
	 */
	public void stop();

	/**
	 * Restarts the VNCDisplay respecting the number of retries specified.
	 */
	public void restart() throws Exception;

	/**
	 * Requests a screen update to the associated server.
	 */
	public void updateScreen() throws Exception;

	/**
	 * Reports to the server that a key event occurred at the client's side.
	 * 
	 * @param event
	 *            the associated event.
	 */
	public void keyEvent(Event event) throws Exception;

	/**
	 * Returns the screen's width.
	 */
	public int getScreenWidth();

	/**
	 * Returns the screen's height.
	 */
	public int getScreenHeight();

	/**
	 * Returns the Display status.
	 */
	public boolean isActive();

	/**
	 * Returns the IProtocolImplementer associated to the Display.
	 */
	public IProtocolImplementer getProtocol();

	public void setPropertiesFileHandler(
			IPropertiesFileHandler propertiesFileHandler);

}
