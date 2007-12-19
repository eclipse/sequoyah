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
 * Define a default configuration for one connection. This class should be
 * replaced if the emulator uses a different way to process the properties data.
 * 
 * @author Fabio Fantato
 */
public class DefaultConnection implements IConnection {
	private String display;
	private String host;
	private int port;
	
	public DefaultConnection(String name) {
		super();
		this.display = IConnection. DEFAULT_DISPLAY;
		this.host =  name;
		this.port = IConnection.DEFAULT_PORT;
	}

	public DefaultConnection() {
		super();
		this.display = IConnection. DEFAULT_DISPLAY;
		this.host =  IConnection.DEFAULT_HOST;
		this.port = IConnection.DEFAULT_PORT;
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.tml.emulator.model.IEmulatorConnection#getDisplay()
	 */
	public String getDisplay() {
		return display;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.tml.emulator.model.IEmulatorConnection#setDisplay(java.lang.String)
	 */
	public void setDisplay(String display) {
		this.display = display;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.tml.emulator.model.IEmulatorConnection#getHost()
	 */
	public String getHost() {
		return host;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.tml.emulator.model.IEmulatorConnection#setHost(java.lang.String)
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.tml.emulator.model.IEmulatorConnection#getPort()
	 */
	public int getPort() {
		return port;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.tml.emulator.model.IEmulatorConnection#setPort(int)
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.tml.emulator.model.IEmulatorConnection#getStringHost()
	 */
	public String getStringHost() {
		return host + display;
	}

}
