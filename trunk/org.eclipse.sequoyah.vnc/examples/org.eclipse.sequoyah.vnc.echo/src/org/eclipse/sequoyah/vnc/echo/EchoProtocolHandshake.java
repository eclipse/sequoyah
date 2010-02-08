/********************************************************************************
 * Copyright (c) 2007-2010 Motorola Inc.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributor:
 * Fabio Rigo
 *
 * Contributors:
 * Daniel Barboza Franco (Motorola) - Bug [233775] - Does not have a way to enter the session password for the vnc connection
 * Fabio Fantato (Eldorado Research Institute - Bug [243305] - The plugin org.eclipse.sequoyah.vnc.echo has compilation errors about exception handling mechanism
 * Fabio Rigo (Eldorado Research Institute) - [246212] - Enhance encapsulation of protocol implementer 
 * Fabio Rigo (Eldorado Research Institute) - [260559] - Enhance protocol framework and VNC viewer robustness
 * Fabio Rigo (Eldorado Research Institute) - Bug [262632] - Avoid providing raw streams to the user in the protocol framework
 * Daniel Pastore (Eldorado) - [289870] Moving and renaming Tml to Sequoyah
 ********************************************************************************/
package org.eclipse.sequoyah.vnc.echo;

import java.io.DataInput;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.eclipse.sequoyah.vnc.protocol.lib.IProtocolHandshake;
import org.eclipse.sequoyah.vnc.protocol.lib.ProtocolHandle;
import org.eclipse.sequoyah.vnc.protocol.lib.exceptions.ProtocolHandshakeException;

public class EchoProtocolHandshake implements IProtocolHandshake {

	public void serverHandshake(ProtocolHandle handle, DataInput in,
			OutputStream out, Map parameters) throws ProtocolHandshakeException {

		try {
			String stringRead = in.readLine();

			if ((stringRead.length() != 13)
					|| (!stringRead.equals("Hello server!"))) { //$NON-NLS-1$
				System.out.println("ERROR"); //$NON-NLS-1$
				throw new ProtocolHandshakeException();
			} else {
				out.write(new String("Hello client.\n").getBytes()); //$NON-NLS-1$
			}

		} catch (IOException e) {
			throw new ProtocolHandshakeException();
		}
	}

	public void clientHandshake(ProtocolHandle handle, DataInput in,
			OutputStream out, Map parameters) throws ProtocolHandshakeException {

		try {
			out.write(new String("Hello server!\n").getBytes()); //$NON-NLS-1$
			String stringRead = in.readLine();

			if ((stringRead.length() != 13)
					|| (!stringRead.equals("Hello client."))) { //$NON-NLS-1$
				System.out.println("ERROR"); //$NON-NLS-1$
				throw new ProtocolHandshakeException();
			}

		} catch (IOException e) {
			throw new ProtocolHandshakeException();
		}
	}
}
