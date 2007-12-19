/********************************************************************************
 * Copyright (c) 2007 Motorola Inc. All rights reserved.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributor:
 * Fabio Fantato (Motorola)
 *
 * Contributors:
 * {Name} (company) - description of contribution.
 ********************************************************************************/

package org.eclipse.tml.service.start.launcher;

/**
 * @author Fabio Fantato
 */
public interface IConnection {
	/**
	 * Default value for display property
	 */
	public static final String DEFAULT_DISPLAY = ":0.0";

	/**
	 * Default value for host property
	 */
	public static final String DEFAULT_HOST = "127.0.0.1";

	/**
	 * Default value for port property
	 */
	public static final int DEFAULT_PORT = 5900;

	/**
	 * Creates an instance of default configuration
	 */
	/**
	 * Gets the ip address for the host of the connection
	 * 
	 * @return a string that represents the host ip
	 */
	public String getHost();

	/**
	 * Set the ip address for this host connection
	 * 
	 * @param host
	 *            is a string that represents the host ip
	 */
	public void setHost(String host);

	/**
	 * Gets the port of connection
	 * 
	 * @return a port number
	 */
	public int getPort();

	/**
	 * Set the port number for this connection
	 * 
	 * @param port
	 */
	public void setPort(int port);

	/**
	 * Gets the display for this host connection
	 * 
	 * @return a string that represents the display of this connection
	 */
	public String getDisplay();

	/**
	 * Set the display for this host connection
	 * 
	 * @param display
	 *            is a string for the host connection
	 */
	public void setDisplay(String display);

	/**
	 * Gets the Host string that represents this connection
	 * 
	 * @return a string that represets the connection
	 */
	public String getStringHost();

}
