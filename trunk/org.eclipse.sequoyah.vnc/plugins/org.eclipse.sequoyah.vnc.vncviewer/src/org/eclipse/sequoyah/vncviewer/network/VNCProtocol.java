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
 * Eugene Melekhov (Montavista) - Bug [227793] - Implementation of the several encodings, performance enhancement etc
 * Daniel Barboza Franco - Bug [233775] - Does not have a way to enter the session password for the vnc connection
 * Daniel Barboza Franco - Bug [233062] - Protocol connection port is static.
 ********************************************************************************/

package org.eclipse.tml.vncviewer.network;

import static org.eclipse.tml.vncviewer.VNCViewerPlugin.log;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.eclipse.tml.protocol.lib.IProtocolImplementer;
import org.eclipse.tml.protocol.lib.exceptions.ProtocolException;
import org.eclipse.tml.vncviewer.exceptions.ProtoClientException;

/**
 * Abstract class that defines the main behavior of the VNC Protocol.
 */
abstract public class VNCProtocol implements IProtocolImplementer {

	protected DataInputStream in;
	protected OutputStream out;
	
	private static String password;
	
	public static final int SECURITY_TYPE_INVALID = 0;
	public static final int SECURITY_TYPE_NONE = 1;
	public static final int SECURITY_TYPE_VNC = 2;
	public static final int SECURITY_TYPE_RA2 = 5;
	public static final int SECURITY_TYPE_RA2NE = 6;
	public static final int SECURITY_TYPE_TIGHT = 16;
	public static final int SECURITY_TYPE_ULTRA = 17;
	public static final int SECURITY_TYPE_TLS = 18;
	public static final int SECURITY_TYPE_VENCRYPT = 19;

	protected int securityType;
	private IVNCPainter vncPainter;
	private int fbWidth; 
	
	private int fbHeight;
	
	//private IVNCPainter painter;
	
	private PixelFormat pixelFormat;
	
	private String serverName;
	
	private static final int PROTOCOL_VERSION_MESSAGE_SIZE = 12;

	private static final int VNC_AUTHENTICATION_CHALLENGE_MESSAGE_SIZE = 16;
	
	/**
	 * Constant used to determine the Framebuffer update request message size in bytes. 
	 */
	static final int FB_UPDATE_REQUEST_MESSAGE_SIZE = 10; /* number of bytes sent to the server in the framebuffer update request */
	
	/* Client to Server message types */
	
	/**
	 * Constant used to represent the Set Pixel Format RFB Client message.   
	 */
	public final static int SET_PIXEL_FORMAT = 0;
	
	/**
	 * Constant used to represent the Set Encondings RFB Client message.   
	 */
	public final static int SET_ENCODINGS = 2;
	
	/**
	 * Constant used to represent the Framebuffer Update Request RFB Client message.   
	 */
	public final static int FRAMEBUFFER_UPDATE_REQUEST = 3;

	/**
	 * Constant used to represent the Key Event RFB Client message.   
	 */
	public final static int KEY_EVENT = 4;
	
	/**
	 * Constant used to represent the Pointer Event RFB Client message.   
	 */
	public final static int POINTER_EVENT = 5;
	
	/**
	 * Constant used to represent the Client Cut Text RFB Client message.   
	 */
	public final static int CLIENT_CUT_TEXT = 6;
	
	/* Server to Client message types */
	
	/**
	 * Constant used to represent the Framebuffer Update RFB Server message.   
	 */
	public final static int FRAMEBUFFER_UPDATE = 0;

	/**
	 * Constant used to represent the Set Colour Map Entries RFB Server message.   
	 */
	public final static int SET_COLOUR_MAP_ENTRIES = 1;
	
	/**
	 * Constant used to represent the Bell RFB Server message.   
	 */
	public final static int BELL = 2;

	private boolean paintEnabled;
	private Map parameters;
	
	/**
	 * Constant used to represent the Server Cut Text RFB Server message.   
	 */
	public final static int SERVER_CUT_TEXT = 3;
	
	private static int SHARED_FLAG = 1;

	public final static int RAW_ENCODING = 0;
	public final static int COPY_RECT_ENCODING = 1;
	public final static int RRE_ENCODING = 2;
	public final static int HEXTILE_ENCODING = 5;
	public final static int ZRLE_ENCODING = 16;

	public final static int ZLIB_ENCODING = 6;

	/**
	 * Returns the protocol version string
	 * @return the protocol version string
	 */
	protected abstract String getVersion();

	abstract protected void handShake(DataInputStream in, OutputStream out)
			throws Exception;

	
	protected int[] getSupportedEncodings() {
		//return painter.getSupportedEncodings();
		return AbstractVNCPainter.getSupportedEncodingsStatic();
	}
	
	/**
	 * Implements the init phase of the RFB Protocol.
	 */
	private void initPhase()
			throws Exception {

		/* ClientInit */
		out.write(SHARED_FLAG); // SharedFlag

		/* ServerInit */
		fbWidth = in.readUnsignedShort();
		fbHeight = in.readUnsignedShort();

		pixelFormat = new PixelFormat();
		pixelFormat.getPixelFormat(in);

		int nameLen = in.readInt();

		byte[] serverName = new byte[nameLen];
		in.read(serverName, 0, nameLen);

		this.serverName = "";
		for (int i = 0; i < serverName.length; i++) {
			char c = (char) serverName[i];
			this.serverName += c;
		}

		sendEncodingsPreferences(getSupportedEncodings(), out);
		
	}

	void sendEncodingsPreferences(int[] encs, OutputStream out) throws IOException {
		int length = encs.length;
		byte[] b = new byte[4 + 4 * encs.length];

		b[0] = (byte) SET_ENCODINGS;
	    b[1] = (byte) 0;
		b[2] = (byte) ((length >> 8) & 0xff);
		b[3] = (byte) (length & 0xff);

		for (int i = 0; i < length; i++) {
			b[4 + 4 * i] = (byte) ((encs[i] >> 24) & 0xff);
			b[5 + 4 * i] = (byte) ((encs[i] >> 16) & 0xff);
			b[6 + 4 * i] = (byte) ((encs[i] >> 8) & 0xff);
			b[7 + 4 * i] = (byte) (encs[i] & 0xff);
		}
		out.write(b);
	}
	
	
	public String getServerName(){
		return serverName;
		
	}

	public int getHeight() {
		return fbHeight;
	}

	public int getWidth() {
		return fbWidth;
	}
	
	
	
	/**
	 *  This method compares each byte of the RFB Protocol client version using the String sent by the Server.
	 */
	protected void compareVersion(byte[] b) throws Exception {
		String clientVersion = getVersion();
		String serverVersion = new String(b);
		boolean versionOk = false;
		
		if (serverVersion.equals(clientVersion)) {
			versionOk = true;
		}
		else if ( (serverVersion.length() == clientVersion.length()) &&  serverVersion.substring(0, 10).equals(clientVersion.substring(0, 10))) {
			// the last number of the version String
			if (serverVersion.charAt(10) > clientVersion.charAt(10)) {
					versionOk = true;
			}
		}
		if (!versionOk) {
			throw new ProtoClientException("Wrong protocol version.");
		}
	}
	
	protected void negotiateProtocol() throws Exception {
		byte[] b = new byte[PROTOCOL_VERSION_MESSAGE_SIZE];
		in.readFully(b, 0, PROTOCOL_VERSION_MESSAGE_SIZE);
		compareVersion(b);
		out.write(getVersion().getBytes());
	}
	
	protected void negotiateSecurity() throws Exception {
		int [] securityTypes = readSecurityTypes();
		securityType = chooseSecurityType(securityTypes);
		if (securityType != SECURITY_TYPE_INVALID) {
			sendSecurityType(securityType);
		}
	}

	protected int chooseSecurityType(int [] securityTypes ) throws Exception {
		for (int i = 0; i < securityTypes.length; i++) {
			if (securityTypeSupported(securityTypes[i])) {
				return securityTypes[i];
			}
		}
		return SECURITY_TYPE_INVALID;
	}

	protected int[] readSecurityTypes() throws Exception {
		int [] result = null;
		int secTypesNumber = in.readByte();
		if (secTypesNumber > 0) {
			result = new int[secTypesNumber];
			for (int i=0; i < secTypesNumber  ;i++) {
				result[i] = in.readByte(); 
			}
		}
		else {
			handshakeFail();
		}
		return result;
	}

	protected void sendSecurityType(int securityType) throws Exception {
		out.write((byte)securityType);
	}

	protected void handshakeFail() throws Exception {
		int failReasonLength;
		StringBuffer reason = new StringBuffer();
		failReasonLength = in.readInt();
		for (int j=0; j<failReasonLength ; j++){
			reason.append((char)(in.readByte()));
		}
		throw new Exception("Handshake failed: " + reason.toString());
	}
	
	protected boolean securityTypeSupported(int type) {
		switch (type) {
		case SECURITY_TYPE_NONE: // None
		case SECURITY_TYPE_VNC: // VNCAuthentication
			return true;
		default:
			return false;
		}
	}

	protected void authenticate() throws Exception {
		switch (securityType) {
		case SECURITY_TYPE_NONE:
			//No op. We do not need to do anything else
			break;
		case SECURITY_TYPE_VNC:
			authenticateVNC();
			break;
		default:
			throw new Exception("Handshake failed: unsupported security type "+ securityType);
		}
	}

	protected void authenticateVNC() throws Exception {
		byte[] challenge = new byte[VNC_AUTHENTICATION_CHALLENGE_MESSAGE_SIZE];
		in.readFully(challenge, 0, VNC_AUTHENTICATION_CHALLENGE_MESSAGE_SIZE);

		byte[] pwd = {0,0,0,0,0,0,0,0};
		byte[] pwdOrg = (password != null)?password.getBytes():new byte[0];
		for (int i = 0; i < 8 && i < pwdOrg.length; i++) {
			pwd[i] = pwdOrg[i];
		}
				
	    DesEncoder des = new DesEncoder(pwd);
	    des.encode(challenge, challenge);
		out.write(challenge);
	}
	
	protected void readAuthenticationResult() throws Exception {
		int securityResult = in.readInt();
		if (securityResult != 0) {
			handshakeFail();
		}
	}
	
	public void clientInit(DataInputStream in, OutputStream out, Map parameters)
			throws ProtocolException {

		this.in = in;
		this.out = out;
		this.parameters = parameters;
		
		setPassword((String)parameters.get("password"));
		
		try {
			negotiateProtocol();
		}catch (Exception e) {
			log(VNCProtocol.class).error("VNC protocol negotiation error: " + e.getMessage());
			throw new ProtocolException("VNC protocol negotiation error.");
		}

		try {
			negotiateSecurity();
		}
		catch (Exception e) {
			log(VNCProtocol.class).error("VNC security negotiation error: " + e.getMessage());
			throw new ProtocolException("VNC security negotiation error.");
		}
		
		try {
			authenticate();
			readAuthenticationResult();
		} catch (Exception e) {
			log(VNCProtocol.class).error("VNC authenticate error: " + e.getMessage());
			throw new ProtocolException("VNC authenticate error.");
		}
		
		try {
			initPhase();
		}
		catch (Exception e){
			log(VNCProtocol.class).error("VNC Init Phase error: " + e.getMessage());
			throw new ProtocolException("VNC Init Phase error.");
		}
		
		
	}

	public void serverInit(DataInputStream in, OutputStream out, Map parameters)
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

	public boolean isPaintEnabled() {
		return paintEnabled;
	}

	public void setPaintEnabled(boolean paintEnabled) {
		this.paintEnabled = paintEnabled;
	}

	public void setPassword(String password) {
		VNCProtocol.password = password;
	}

	public DataInputStream getInputStream() {
		return in;
	}

	public PixelFormat getPixelFormat() {
		return pixelFormat;
	}
}
