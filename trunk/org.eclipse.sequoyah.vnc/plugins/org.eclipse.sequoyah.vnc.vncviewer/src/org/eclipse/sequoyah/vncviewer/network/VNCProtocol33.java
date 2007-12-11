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

import static org.eclipse.tml.vncviewer.VNCViewerPlugin.log;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;

import org.eclipse.tml.vncviewer.exceptions.ProtoClientException;



/**
 *  This class implements the version 3.3 of the RFB Protocol.
 */
public class VNCProtocol33 extends VNCProtocol {


	
	/**
	 * Constant that represents the RFB protocol version.
	 */
	static final String RFB_VERSION = "RFB 003.003\n"; /* used to compare the implemented version with the server version */
	
	/**
	 * Constant that defines the number of bytes read in the handshake phase.
	 */
	static final int HANDSHAKE_MESSAGE_SIZE = 12; /* number of bytes read in the handshake phase */
	
	

	
	
	public VNCProtocol33(IVNCPainter vncPainter) {
		
		super();
		setVncPainter(vncPainter);

	}
	

	/**
	 *  This method compares each byte of the RFB Protocol client version using the String sent by the Server.
	 */
	protected void compareVersion(byte[] b) throws Exception {
		
		
		if (!(new String(b)).equals(RFB_VERSION)) {
			
			throw new ProtoClientException("Wrong protocol version.");
			
		}
	
	}

	/**
	 * Implements the handshake phase of the RFB Protocol.
	 */
	protected void handShake() throws Exception {

	
		@SuppressWarnings("unused")
		int rfbSecType;
		byte[] b = new byte[HANDSHAKE_MESSAGE_SIZE];
	
		in.readFully(b, 0, HANDSHAKE_MESSAGE_SIZE);
		
		compareVersion(b);
		out.write(b);
		
		rfbSecType = in.readInt(); /* The security type used by the server */
		/* The Sec-types handling is not implemented yet */
	}	

}
