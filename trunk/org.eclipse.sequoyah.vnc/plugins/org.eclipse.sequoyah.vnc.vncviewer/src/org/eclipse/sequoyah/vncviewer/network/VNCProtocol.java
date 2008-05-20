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

package org.eclipse.tml.vncviewer.network;

import static org.eclipse.tml.vncviewer.VNCViewerPlugin.log;

import java.io.DataInputStream;
import java.io.OutputStream;

import org.eclipse.tml.protocol.lib.IProtocolImplementer;
import org.eclipse.tml.protocol.lib.exceptions.ProtocolException;
import org.eclipse.tml.vncviewer.graphics.swt.VNCSWTPainter;

/**
 * Abstract class that defines the main behavior of the VNC Protocol.
 */
abstract public class VNCProtocol implements IProtocolImplementer {

	private IVNCPainter vncPainter;
	private PixelFormat pixelFormat;
	private String serverName;

	private boolean paintEnabled;

	private static int SHARED_FLAG = 1;

	abstract protected void compareVersion(byte[] b) throws Exception;

	abstract protected void handShake(DataInputStream in, OutputStream out)
			throws Exception;

	/**
	 * Implements the init phase of the RFB Protocol.
	 */
	private void initPhase(DataInputStream in, OutputStream out)
			throws Exception {

		/* ClientInit */
		out.write(SHARED_FLAG); // SharedFlag

		/* ServerInit */
		int fbWidth = in.readUnsignedShort();
		int fbHeight = in.readUnsignedShort();

		// vncPainter.setSize(fbWidth, fbHeight);

		pixelFormat = new PixelFormat();
		pixelFormat.getPixelFormat(in);

		vncPainter.setPixelFormat(pixelFormat);
		vncPainter.setSize(fbWidth, fbHeight);

		int nameLen = in.readInt();

		byte[] serverName = new byte[nameLen];
		in.read(serverName, 0, nameLen);

		this.serverName = "";
		for (int i = 0; i < serverName.length; i++) {
			char c = (char) serverName[i];
			this.serverName += c;
		}
	}

	public void clientInit(DataInputStream in, OutputStream out)
			throws ProtocolException {

		setVncPainter(new VNCSWTPainter());

		try {
			handShake(in, out);
		} catch (Exception e) {
			log(VNCProtocol.class).error(
					"VNC Handshake Phase error: " + e.getMessage());
			throw new ProtocolException("VNC Handshake Phase error.");
		}

		try {
			initPhase(in, out);
		} catch (Exception e) {
			log(VNCProtocol.class).error(
					"VNC Init Phase error: " + e.getMessage());
			throw new ProtocolException("VNC Init Phase error.");
		}
	}

	public void serverInit(DataInputStream in, OutputStream out)
			throws ProtocolException {

	}

	/**
	 * Returns the IVNCPainter assigned to this client.
	 */
	public IVNCPainter getVncPainter() {
		return vncPainter;
	}

	/**
	 * Sets the vncPainter value.
	 */
	public void setVncPainter(IVNCPainter vncPainter) {
		this.vncPainter = vncPainter;
	}

	public String getServerName() {
		return serverName;

	}

	public boolean isPaintEnabled() {
		return paintEnabled;
	}

	public void setPaintEnabled(boolean paintEnabled) {
		this.paintEnabled = paintEnabled;
	}
}