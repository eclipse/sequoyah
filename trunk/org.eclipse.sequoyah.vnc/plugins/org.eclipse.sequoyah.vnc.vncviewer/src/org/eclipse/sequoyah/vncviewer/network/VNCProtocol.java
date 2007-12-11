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
 * Absctract clas that defines the main behavior of the VNC Protocol. 
 */
abstract public class VNCProtocol extends Protocol {

	private int 
	fbWidth, 
	fbHeight;
	
	private IVNCPainter vncPainter;
	private PixelFormat pixelFormat;
	private String serverName;
	
	private static int SHARED_FLAG = 1;
	
	/**
	 * The consumer object used by the client.
	 */
	private Consumer consumer;
		
	/**
	 * The consumer thread.
	 */
	private Thread consumerThread;
	
	
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

	/**
	 * Constant used to represent the Server Cut Text RFB Server message.   
	 */
	public final static int SERVER_CUT_TEXT = 3;
	

	public final static int RAW_ENCODING = 0;
	public final static int COPY_RECT_ENCODING = 1;
	public final static int RRE_ENCODING = 2;
	public final static int HEXTILE_ENCODING = 5;
	public final static int ZRLE_ENCODING = 16;

	

	abstract protected void compareVersion(byte[] b) throws Exception; 
	abstract protected void handShake() throws Exception;
	
	
	/**
	 * Starts a consumer thread for the RFB Protocol.
	 */
	protected void startConsumer() throws Exception{
		if (in != null) {
			consumer = new Consumer(in, this);
			consumerThread = new Thread(consumer);
			consumerThread.start();
		}
		else throw new ProtoClientException ("Not connected!");
	}
	
	
	
	/**
	 * Implements the init phase of the RFB Protocol.
	 */
	private void initPhase() throws Exception{
		
		/* ClientInit */
		out.write(SHARED_FLAG); // SharedFlag
		
		/* ServerInit */
		fbWidth = in.readUnsignedShort(); 
		fbHeight = in.readUnsignedShort();
	
		//vncPainter.setSize(fbWidth, fbHeight);
		
		pixelFormat = new PixelFormat();
		pixelFormat.getPixelFormat(in);
		
		vncPainter.setPixelFormat(pixelFormat);
		vncPainter.setSize(fbWidth, fbHeight);
		
		int nameLen = in.readInt();
		
		byte[] serverName = new byte[nameLen];
		in.read(serverName, 0, nameLen);
		
		
		this.serverName = "";
		for (int i=0; i < serverName.length; i++){
			char c = (char)serverName[i];
			this.serverName += c;
		}
		
		/* The server name will be shown in the VNC Viewer View */

	}

	
	
	/**
	 * Performs a framebuffer update request to the RFB server.
	 */
	public void fbUpdateRequest(boolean incremental) throws Exception {
		
		byte upReq[] = new byte[FB_UPDATE_REQUEST_MESSAGE_SIZE];
		upReq[0] = FRAMEBUFFER_UPDATE_REQUEST;
		upReq[1] = (byte) (incremental ? 1 : 0); // incremental
		
		// x, y
		upReq[2] = upReq[3] = upReq[4] = upReq[5] = 0;
	
		// copy width, length  (converts int(32) big-endian to unsigned byte(16)
		
		upReq[6] = (byte) ((fbWidth >> 8) & 0x000000FF);
		upReq[7] = (byte) (((byte)(fbWidth)) & 0x000000FF);
		upReq[8] = (byte) ((fbHeight >> 8) & 0x000000FF);
		upReq[9] = (byte) (((byte)(fbHeight)) & 0x000000FF);

	
		
		synchronized (socket) {
			
			try {
				out.write(upReq);
				out.flush();
			}
			catch (IOException ioe){
				log(VNCProtocol.class).error("Framebuffer Update Request message error: " + ioe.getMessage());
				throw new ProtoClientException("Framebuffer Update Request message error.");			
			}
		}
		
		
	}
	
	
	private void readRect()	throws Exception{
		readRect(vncPainter);
	}
	
	/**
	 * Reads a rectangle of pixels from the RFB server.
	 */
	protected void readRect(IVNCPainter vncPainter) throws Exception {
		int x,y,w,h;
		@SuppressWarnings("unused")
		int encType;
		
		
		try {
			x = in.readUnsignedShort();
			y = in.readUnsignedShort();
			w = in.readUnsignedShort();
			h = in.readUnsignedShort();
		
			encType = in.readInt();
		}
		catch (IOException ioe){
			log(VNCProtocol.class).error("Rectangle message error: " + ioe.getMessage());
			throw new ProtoClientException("Rectangle message error.");
		}
		
		int pixelsNum = w * h;
		
		//final int bytesNum = pixelsNum * 4;
		final int bytesNum = pixelsNum * ((pixelFormat.getBitsPerPixel())/8);
		
		
		byte pixelsb[] = new byte[bytesNum];
		
		int bytesRead = 0;
		
		// Read the array of pixels from the VNC Server
		while (bytesRead < bytesNum) {
			int numRead;
			
			try {
				numRead = in.read(pixelsb, bytesRead, bytesNum - bytesRead);
			}
			catch (IOException ioe){
				log(VNCProtocol.class).error("Rectangle message error: " + ioe.getMessage());
				throw new ProtoClientException("Rectangle message error.");
			}
			
			if (numRead >= 0) {
				bytesRead += numRead;
			}
		}


		if (paintEnabled) {
			vncPainter.processRectangle(encType, pixelsb, x, y, w, h);
		}
		
	}
	
	
	/**
	 * Receives the updated Framebuffer from the RFB Server.
	 */
	protected void fbUpdate() throws Exception{
		
		
		// FramebufferUpdate
		try {
			in.readByte(); // 1 padding byte
		}
		catch (IOException ioe) {
			
			log(VNCProtocol.class).error("Framebuffer Update error: " + ioe.getMessage());
			throw new ProtoClientException("Framebuffer Update error.");
		}
		
		
		synchronized (socket) {
			int fbNumRect; 
			
			try {
				fbNumRect = in.readUnsignedShort();
			}
			catch (IOException ioe){
				log(VNCProtocol.class).error("Framebuffer Update error: " + ioe.getMessage());
				throw new ProtoClientException("Framebuffer Update error.");
			}
			
			//System.out.println("#" + fbNumRect);
			
			for (int i=0; i<fbNumRect; i++){
				readRect();
			}
			
		}
	}
	
	
	public void keyEvent(VNCKeyEvent event) throws Exception{		
		byte[] keyMsg = new byte[8];
		int keysym;
		
		keyMsg[0] = (byte)4;		// msg type
		keyMsg[1] =	event.isPressed() ? (byte)1: (byte)0;	// down flag
		keyMsg[2] = keyMsg[3] = 0;	// padding

		keysym = event.getKeysym();
		
		keyMsg[4] = (byte) ((keysym >> 24) & 0x000000FF);
		keyMsg[5] = (byte) ((keysym >> 16) & 0x000000FF);
		keyMsg[6] = (byte) ((keysym >> 8) & 0x000000FF);
		keyMsg[7] = (byte) (keysym & 0x000000FF);

		
		synchronized (socket) {
			
			try {
				out.write(keyMsg);
				out.flush();
			}
			catch (IOException ioe){
				log(VNCProtocol.class).error("Key Event message error: " + ioe.getMessage());
				throw new ProtoClientException("Key Event message error.");
			}
		}
	
	} 
	

	
	
	
	public void runProtocol(String host, int port) throws IOException , Exception {
		
		try {
			openConnection(host, port);
		}
		catch (Exception e) {
			log(VNCProtocol.class).error("Open Connection error: " + e.getMessage());
			throw new ProtoClientException("Open Connection error.");
		}
		
		try {
			handShake();
		}
		catch (Exception e) {
			log(VNCProtocol.class).error("VNC Handshake Phase error: " + e.getMessage());
			throw new ProtoClientException("VNC Handshake Phase error.");
		}
		
		try {
			initPhase();
		}
		catch (Exception e){
			log(VNCProtocol.class).error("VNC Init Phase error: " + e.getMessage());
			throw new ProtoClientException("VNC Init Phase error.");
		}
	
		try {
			startConsumer();
		}
		catch (Exception e) {
			log(VNCProtocol.class).error("VNC Consumer start error: " + e.getMessage());
			throw new ProtoClientException("VNC Consumer start error.");
		}
		
		//Interaction Phase
	}
	
	

	public void restartProtocol() throws Exception {

		if (this.isConnected()) {
			stopProtocol();
		}
		
		runProtocol(host, port);
	}
	
	
	public void stopProtocol() throws ProtoClientException{

		if (consumer != null) {
			consumer.setRunning(false);
		}
		
		try {
			closeConnection();
		}
		catch (Exception e){
			log(VNCProtocol.class).error("Close connection error: " + e.getMessage());
			throw new ProtoClientException("Close connection error.");
		}
		
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



	public IPainter getPainter() {
		return vncPainter;
	}
	
	

	public String getServerName(){
		return serverName;
		
	}


	public int getHeight() {
		return fbHeight;
	}



	public int getwidth() {
		return fbWidth;
	}

	
	
	public void mouseEvent(VNCMouseEvent event) throws Exception {

		// TODO : correctly handle the buttons and clicks
		
		byte[] keyMsg = new byte[6];
		keyMsg[0] = (byte) 5;		// msg type

		if (event.isButtonPressed()) {	
			keyMsg[1] = (byte) 1;
		}
		else {
			keyMsg[1] = (byte) 0;
		}
		
		
		// x position
		keyMsg[2] = (byte) ((event.getX() >> 8) & 0x000000FF); 
		keyMsg[3] = (byte)	event.getX(); 
		
		// y position
		keyMsg[4] = (byte) ((event.getY() >> 8) & 0x000000FF);
		keyMsg[5] = (byte) event.getY();

		
		synchronized (socket) {
			
			try {
				out.write(keyMsg);
				out.flush();
			}
			catch (IOException ioe){
				log(VNCProtocol.class).error("Mouse Event message error: " + ioe.getMessage());
				throw new ProtoClientException("Mouse Event message error.");
			}
		}
		
	}
	
}


/**
 * The Consumer class implements the consumer part of the RFB Protocol.
 * If the server sends a message not supported, the consumer sets its running state to false and dies.
 * The VNC Protocol uses this class as a separated thread, so producing and consuming can be done concurrently.
 */
class Consumer implements Runnable{
	
	DataInputStream in;
	VNCProtocol vncProtocol;
	
	/**
	 * Indicates if the consumer is running.
	 */
	volatile private boolean running = false;
	
	/**
	 * @param in the DataInputStream associated with the server.
	 * @param vnc the VNCProtocol object.
	 */
	public Consumer(DataInputStream in, VNCProtocol vnc){
		this.in = in;
		this.vncProtocol = vnc;
	}
	
	/**
	 *  This method reads a message from the server, discovers its type and handles it.
	 */
	public void run(){
		byte b = 0;

		setRunning(true);
		
		while (running) {

			
			/* Discovers the message type */
			try {
				b = in.readByte();
			}	
			catch (EOFException eof){
				log(VNCProtocol.class).error("Message Type read error - End of file not expected.");
				setRunning(false);
				break;
			}
			catch (IOException e) {
				log(VNCProtocol.class).error("Message Type read error.");
				setRunning(false);
				break;
			}
			
			switch ( (int) b) {
			
				case VNCProtocol.FRAMEBUFFER_UPDATE:
					
					try {
						vncProtocol.fbUpdate();

					} catch (Exception e) {
						log(VNCProtocol.class).error("Framebuffer update error: " + e.getMessage());
						setRunning(false);
					}
					
					break;
				
				/* The cases below are not implemented yet. */
				case VNCProtocol.SERVER_CUT_TEXT:
				case VNCProtocol.BELL:
				case VNCProtocol.SET_COLOUR_MAP_ENTRIES:
					
				default:
					log(VNCProtocol.class).error("Message type is not a framebuffer update."); 
					setRunning(false);
					break;
			
			} /* switch */
			
		} /* while */
	} /* run */

	
	
	/**
	 * Return the Consumer's state.
	 * @return true if Consumer is running, false othercase.
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * Sets the value of the variable running.
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}
};


