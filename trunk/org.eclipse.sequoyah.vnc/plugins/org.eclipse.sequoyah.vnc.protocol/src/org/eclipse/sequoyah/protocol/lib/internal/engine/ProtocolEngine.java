/********************************************************************************
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributor:
 * Fabio Rigo 
 *
 * Contributors:
 * Daniel Barboza Franco (Eldorado Research Institute) - Bug [233775] - Does not have a way to enter the session password for the vnc connection
 * Fabio Rigo - Bug [238191] - Enhance exception handling
 * Fabio Fantato (Eldorado Research Institute) - Bug [243918] - Wrong "if" test in ProtocolEngine class
 * Fabio Rigo (Eldorado Research Institute)- Bug [242757] - Protocol does not support Unicode on variable sized fields
 * Fabio Rigo - Bug [244067] - The exception handling interface should forward the protocol implementer object
 * Daniel Barboza Franco (Eldorado Research Institute) - Bug [233064] - Add reconnection mechanism to avoid lose connection with the protocol
 * Fabio Rigo (Eldorado Research Institute) - [246212] - Enhance encapsulation of protocol implementer
 * Daniel Barboza Franco (Eldorado Research Institute) - Bug [242924] - There is no way to keep the size of a Variable Size Data read
 * Daniel Barboza Franco (Eldorado Research Institute) - Bug [233121] - There is no support for proxies when connecting the protocol
 * Daniel Barboza Franco (Eldorado Research Institute) - Bug [246916] - Add the correct Number objects to ProtocolMessage objects on reading from input stream
 * Daniel Barboza Franco (Eldorado Research Institute) - [257588] - Add support to ServerCutText message
 ********************************************************************************/
package org.eclipse.tml.protocol.lib.internal.engine;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.tml.protocol.lib.IMessageHandler;
import org.eclipse.tml.protocol.lib.IProtocolExceptionHandler;
import org.eclipse.tml.protocol.lib.IProtocolInit;
import org.eclipse.tml.protocol.lib.IRawDataHandler;
import org.eclipse.tml.protocol.lib.MessageFieldsStore;
import org.eclipse.tml.protocol.lib.ProtocolHandle;
import org.eclipse.tml.protocol.lib.ProtocolMessage;
import org.eclipse.tml.protocol.lib.exceptions.InvalidDefinitionException;
import org.eclipse.tml.protocol.lib.exceptions.InvalidInputStreamDataException;
import org.eclipse.tml.protocol.lib.exceptions.InvalidMessageException;
import org.eclipse.tml.protocol.lib.exceptions.MessageHandleException;
import org.eclipse.tml.protocol.lib.exceptions.ProtocolException;
import org.eclipse.tml.protocol.lib.exceptions.ProtocolInitException;
import org.eclipse.tml.protocol.lib.exceptions.ProtocolRawHandlingException;
import org.eclipse.tml.protocol.lib.internal.model.ClientModel;
import org.eclipse.tml.protocol.lib.internal.model.IModel;
import org.eclipse.tml.protocol.lib.internal.model.ServerModel;
import org.eclipse.tml.protocol.lib.msgdef.ProtocolMsgDefinition;
import org.eclipse.tml.protocol.lib.msgdef.databeans.FixedSizeDataBean;
import org.eclipse.tml.protocol.lib.msgdef.databeans.IMsgDataBean;
import org.eclipse.tml.protocol.lib.msgdef.databeans.IteratableBlockDataBean;
import org.eclipse.tml.protocol.lib.msgdef.databeans.RawDataBean;
import org.eclipse.tml.protocol.lib.msgdef.databeans.VariableSizeDataBean;

/**
 * DESCRIPTION: This class allows protocol communication through a single
 * channel.<br>
 * 
 * RESPONSIBILITY: Open/close/restart the channel among the two parts in the
 * communication. Serialize/deserialize messages, sending or retrieving them
 * to/from the communication streams.<br>
 * 
 * COLABORATORS: None.<br>
 * 
 * USAGE: This class is intended to be used by the protocol framework only.
 * 
 */
public class ProtocolEngine {

	/**
	 * Default value for maximum number of reconnection attempts.
	 */
	private static final int RECONNECTION_MAX = 5;

	/**
	 * True if the protocol is big endian, false if little endian. Note: If a
	 * number has more than one byte, a big endian protocol sends firstly the
	 * most significant byte to the stream. A little endian protocol, on the
	 * other hand, sends firstly the least significant byte.
	 */
	private boolean isBigEndianProtocol;

	/**
	 * The object used to map this connection at the model 
	 */
	private ProtocolHandle handle;

	/**
	 * The message definition map used by this protocol. It contains the
	 * description of all messages supported by the protocol.
	 */
	private Map<Long, ProtocolMsgDefinition> messageDefCollection;

	/**
	 * Number of connection retries left. 
	 */
	private int retries;
	
	/**
	 * Maximum number of reconnection attempts. 
	 */
	private int retriesMax = RECONNECTION_MAX;

	
	/**
	 * This variable is used control concurrency between two consecutive restart
	 * requests. When a restart is successfully done it's value is incremented.
	 * This is necessary to avoid that two consecutive re-connections happen for the same reason.
	 */
	private int connectionSerialNumber = 0;
	
	
	/**
	 * A collection of the incoming messages ids, used to validate if a message
	 * can be retrieved from the input stream.>
	 */
	private Collection<String> incomingMessages;

	/**
	 * A collection of the outgoing messages ids, used to validate if a message
	 * can be sent through the output stream.
	 */
	private Collection<String> outgoingMessages;

	/**
	 * The sequence of steps to execute for connection initialization
	 */
	private IProtocolInit initProcedure;

	/**
	 * The handler that was registered with the protocol at the moment it
	 * started. It is used for exception handling customization by the user.
	 */
	private IProtocolExceptionHandler exceptionHandler;

	/**
	 * The socket opened to the other part of the communication.
	 */
	private Socket socket;

	/**
	 * The host to which the socket is connected.
	 */
	private String host;

	/**
	 * General parameters associated to the protocol implementation, initialized
	 * by the protocol user.
	 */
	private Map parameters;

	/**
	 * The port to which the socket is connected.
	 */
	private int port;

	/**
	 * True if this protocol is running as server. False if running as client.
	 * This information shall be kept as argument to allow restarting.
	 */
	private boolean isServer; 

	/**
	 * The stream from where the incoming bytes flow
	 */
	private DataInputStream in;

	/**
	 * The stream to where the outgoing bytes flow
	 */
	private OutputStream out;

	/**
	 * The consumer that reads bytes from the socket input stream. The consumer
	 * thread monitors the stream to collect its bytes as they arrive.
	 */
	private Consumer consumer;

	/**
	 * Constructor. Sets the attributes that turns the generic engine into a 
	 * specific engine, which are: 
	 * - Protocol handle for identification
	 * - An init/handshaking procedure
	 * - Messages used in interaction phase (definitions and directions)
	 * - Specific exception handling procedures 
	 * - Role of the engine (server, client)
	 */
	public ProtocolEngine(
	        ProtocolHandle handle, IProtocolInit initProcedure,
			Map<Long, ProtocolMsgDefinition> messageDefCollection,
			Collection<String> incomingMessages,
			Collection<String> outgoingMessages,
			IProtocolExceptionHandler exceptionHandler,
			boolean isBigEndianProtocol, boolean isServer, 
			int retries) {

		this.handle = handle;
		this.initProcedure = initProcedure;
		this.messageDefCollection = messageDefCollection;
		this.incomingMessages = incomingMessages;
		this.outgoingMessages = outgoingMessages;
		this.exceptionHandler = exceptionHandler;
		this.isBigEndianProtocol = isBigEndianProtocol;
		this.isServer = isServer;
		this.retriesMax = (retries >= 0) ? retries : RECONNECTION_MAX;
		this.retries = this.retriesMax;
	}

	/**
	 * Starts the communication.
	 * 
	 * @param host
	 *            The host to connect to.
	 * @param port
	 *            The port to connect to.
	 * @param parameters
	 *            A Map with parameters other than host and port, for
	 *            customization purposes. Accepts null if apply.
	 * 
	 * @throws UnknownHostException
	 *             If the provided host cannot be resolved.
	 * @throws IOException
	 *             If the communication socket cannot be opened.
	 * @throws ProtocolInitException
	 *             If the protocol fails to initialize.
	 */
     public synchronized void startProtocol(String host, int port,
			Map parameters) throws UnknownHostException,
			IOException, ProtocolInitException {
		startProtocol(host, port, parameters, -1);
	}

	/**
	 * Starts the communication.
	 * 
	 * @param host
	 *            The host to connect to.
	 * @param port
	 *            The port to connect to.
	 * @param parameters
	 *            A Map with parameters other than host and port, for
	 *            customization purposes. Accepts null if apply.
	 * @param timeout
	 *            The maximum time to wait for the connection to remote site to
	 *            open.
	 * 
	 * @throws UnknownHostException
	 *             If the provided host cannot be resolved.
	 * @throws IOException
	 *             If the communication socket cannot be opened.
	 * @throws ProtocolInitException
	 *             If the protocol fails to initialize.
	 */
	public synchronized void startProtocol(String host, int port,
			Map parameters, int timeout)
			throws UnknownHostException, IOException, ProtocolInitException {
		// Stores the host and port, that will be used if a restart is necessary
		this.host = host;
		this.port = port;

		this.parameters = parameters;

		Boolean bypassProxy = (Boolean)parameters.get("bypassProxy"); //$NON-NLS-1$
		bypassProxy = (bypassProxy != null)? bypassProxy : new Boolean(false);
		
		Proxy proxy = (Proxy)parameters.get("proxy"); //$NON-NLS-1$
			
		if (bypassProxy) { // The connection will not use proxy settings
			socket = new Socket(Proxy.NO_PROXY);
		}
		else if (proxy != null){ // The connection will use this proxy
			socket = new Socket(proxy); 
		} else { // The connection will use default proxy settings, if any
			socket = new Socket(); 
		}
			
		InetSocketAddress socketAdress = new InetSocketAddress(host, port);
		
		if (timeout < 0) {
			socket.connect(socketAdress);
		} else {
			socket.connect(socketAdress, timeout);
		}

		// When the socket is opened, keep the input and output streams in
		// the appropriate attributes
		in = new DataInputStream(socket.getInputStream());
		out = socket.getOutputStream();

		doStartProtocol();

	}

	/**
	 * Starts the communication.
	 * 
	 * @param protocolImplementer
	 *            The protocol instance that will use this engine to communicate
	 * @param connectedSocket
	 *            The socket that needs to be used by the engine to send and
	 *            receive messages
	 * @param parameters
	 *            A Map with parameters other than host and port, for
	 *            customization purposes. Accepts null if apply.
	 * @param isServer
	 *            True if the engine will run as server; false if it will run as
	 *            client
	 * 
	 * @throws IOException
	 * @throws ProtocolInitException
	 */
	public synchronized void startProtocol(Socket connectedSocket,
			Map parameters) throws IOException,
			ProtocolInitException {
		this.socket = connectedSocket;
		this.host = socket.getInetAddress().getHostAddress();
		this.port = socket.getPort();
		this.parameters = parameters;

		// When the socket is opened, keep the input and output streams in
		// the appropriate attributes
		in = new DataInputStream(socket.getInputStream());
		out = socket.getOutputStream();

		doStartProtocol();
	}
	
	/**
	 * Starts the protocol message exchange, by running the handshaking procedure
	 * and starting the consumer thread.  
	 * 
	 * @throws ProtocolInitException
	 */
	private void doStartProtocol() throws ProtocolInitException
	{		
		if (initProcedure != null)
		{
			// Delegate the initialization to the concrete protocol class
			if (isServer) {
				initProcedure.serverInit(handle, in, out, parameters);
			} else {
				initProcedure.clientInit(handle, in, out, parameters);
			}

			// After all initialization is done, start the consumer thread, which
			// will listen to the input stream to collect any byte that arrive
			consumer = new Consumer();
			Thread consumerThread = new Thread(consumer);
			consumerThread.start();
			
			retries = retriesMax;
			connectionSerialNumber++;
		}
	}

	/**
	 * Stops the communication with the current site.
	 * 
	 * @throws IOException
	 *             If an error occurs while closing the streams and socket.
	 */
	public synchronized void stopProtocol() throws IOException {
		consumer.stopConsumer();
		out.close();
		in.close();
		socket.close();
	}

	
	private void reconnect(int serialNumber) throws UnknownHostException, ProtocolInitException, IOException, ProtocolException{
	
		if (this.connectionSerialNumber == serialNumber){
			
			if (retries > 0) { 
				
				try {
					synchronized (this) {
						retries--;
						restartProtocol();
					}
				}
				catch (Exception e) {
					reconnect(serialNumber);
				}
	
			} else throw new ProtocolException ("Number of connection retries exceeded the limit of " + retriesMax + "."); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		
	}
	
	
	/**
	 * Restarts the protocol by closing the current connection (if opened) and
	 * opening it again.
	 * 
	 * @throws UnknownHostException
	 *             If the host used to start the protocol previously is not
	 *             known or inexistent.
	 * @throws IOException
	 *             If the communication socket cannot be opened or closed.
	 * @throws ProtocolInitException
	 *             If the protocol fails to initialize.
	 */
	public void restartProtocol() throws UnknownHostException, IOException,
			ProtocolInitException, ProtocolException {
		
		if (this.isConnected()) {
			stopProtocol();
		}

		/*
		try {
			startProtocol(null, host, port, parameters, isServer);
		} catch (Exception e) {
			reconnect(connectionSerialNumber);
		}
		*/
		
		startProtocol(host, port, parameters);



		
	}

	/**
	 * Return the connection's state.
	 * 
	 * @return True if the connection is alive; false if not.
	 */
	public boolean isConnected() {
		boolean isConnected = false;

		if ((socket != null) && (socket.isConnected()) && (!socket.isClosed())) {
			isConnected = true;
		}

		return isConnected;
	}

	/**
	 * Sends the provided message to the communication stream, serializing it
	 * according to the current message definitions map.<br>
	 * That map contains message definitions declared by this protocol and its
	 * parents.
	 * 
	 * @param message
	 *            The message to send.
	 * 
	 * @throws InvalidMessageException
	 *             If the data collected from the message is invalid,
	 *             considering the message definition made.
	 * @throws InvalidDefinitionException
	 *             If the message definition does not contain all necessary
	 *             information to encode the message.
	 * @throws ProtocolRawHandlingException
	 *             If it is not possible to write the data to the stream due to
	 *             a generic reason.
	 * @throws IOException
	 *             If an output stream disconnection is detected while writing
	 *             data.
	 */
	public synchronized final void sendMessage(ProtocolMessage message)
			throws ProtocolRawHandlingException, InvalidMessageException,
			InvalidDefinitionException, IOException {
		// Can only send a message if the protocol is connected
		if (isConnected()) {
			// Creates a byte output stream to store all bytes that will be sent
			// through the real output stream after the message is completely
			// serialized
			ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();

			// Find the message definition given the message code
			ProtocolMsgDefinition msgDefinition = messageDefCollection
					.get(message.getCode());

			if ((msgDefinition != null)
					&& (isOutgoingMessage(msgDefinition.getId()))) {
				// Serialize the message code
				boolean isCodeSigned = msgDefinition.isMsgCodeSigned();
				int codeSize = msgDefinition.getMsgCodeSizeInBytes();
				writeNumberToStream(byteOutputStream, new Long(message
						.getCode()), codeSize, isCodeSigned);

				// Serialize each field from the message definition
				List<IMsgDataBean> msgDataList = msgDefinition.getMessageData();
				for (IMsgDataBean messageData : msgDataList) {
					writeFilterMessageDef(messageData, byteOutputStream,
							message, null, -1);
				}

				// After the message is completely serialized, write the message
				// to the real stream.
				try {
					if (out != null) {
						byteOutputStream.writeTo(out);
						out.flush();
					}
				} catch (IOException e) {
					// The input stream is probably broken

					try {
						reconnect(connectionSerialNumber);
					} catch (ProtocolException eReconnection) {
						
						handleIOExceptionOnStream();
						throw e;

						
					}
					
				}
			}
		}
	}

	/**
	 * Determines which is the provided message data bean type and calls the
	 * correct handler for it to perform write operation to the provided output
	 * stream.
	 * 
	 * @param messageDataDef
	 *            The message data object that needs filtering.
	 * @param streamToWriteTo
	 *            The output stream where the message will be written.
	 * @param message
	 *            The message that is going to be sent.
	 * @param iterableBlockId
	 *            The id of the iteratable block, as defined at the
	 *            ProtocolMessage extension.
	 * @param index
	 *            The iteration index. Range: 0 ~~ (iteratableBlockLength - 1)
	 * 
	 * @throws InvalidMessageException
	 *             If the data collected from the message is invalid,
	 *             considering the message definition made.
	 * @throws InvalidDefinitionException
	 *             If the message definition does not contain all necessary
	 *             information to encode the message.
	 * @throws ProtocolRawHandlingException
	 *             If it is not possible to write the data to the stream during
	 *             a raw field handling.
	 */
	private void writeFilterMessageDef(IMsgDataBean messageDataDef,
			ByteArrayOutputStream streamToWriteTo, ProtocolMessage message,
			String iteratableBlockId, int index)
			throws ProtocolRawHandlingException, InvalidMessageException,
			InvalidDefinitionException {
		if (messageDataDef instanceof FixedSizeDataBean) {
			writeFixedSizeDataToStream(streamToWriteTo,
					(FixedSizeDataBean) messageDataDef, message);
		} else if (messageDataDef instanceof VariableSizeDataBean) {
			writeVariableSizeDataToStream(streamToWriteTo,
					(VariableSizeDataBean) messageDataDef, message);
		} else if (messageDataDef instanceof RawDataBean) {
			writeRawDataToStream(streamToWriteTo, (RawDataBean) messageDataDef,
					message);
		} else {
			writeIteratableBlockToStream(streamToWriteTo,
					(IteratableBlockDataBean) messageDataDef, message);
		}
	}

	/**
	 * Performs the write operation of a fixed size data field to the output
	 * stream.
	 * 
	 * @param streamToWriteTo
	 *            The output stream where the message will be written.
	 * @param messageDataDef
	 *            The object that contains the definition of how to write
	 *            message data to the output stream.
	 * @param message
	 *            The message that is going to be sent.
	 * 
	 * @throws InvalidMessageException
	 *             If the data collected from the message is invalid,
	 *             considering the message definition made.
	 * @throws InvalidDefinitionException
	 *             If the message definition does not contain all necessary
	 *             information to encode the message.
	 */
	private void writeFixedSizeDataToStream(
			ByteArrayOutputStream streamToWriteTo,
			FixedSizeDataBean messageDataDef, ProtocolMessage message)
			throws InvalidMessageException, InvalidDefinitionException {
		// Retrieve data from the message definition object.
		// The fields description and how to use then can be found at
		// the ProtocolMessage extension point documentation.
		String fieldName = messageDataDef.getFieldName();
		boolean isSigned = messageDataDef.isFieldSigned();
		int fieldSize = messageDataDef.getFieldSizeInBytes();
		Object value = messageDataDef.getValue();

		if (fieldName != null) {
			if (value == null) {
				// If the message definition does not contain a value defined,
				// that means that it is a field is supposed to be defined by
				// the message object. See more information about the
				// "value" attribute in ProtocolMessage extension point
				// documentation.
				value = message.getFieldValue(fieldName);
			}
			if (value instanceof Number) {
				// If a value is defined, then write the field to the output
				// stream.
				writeNumberToStream(streamToWriteTo, (Number) value,
						fieldSize, isSigned);
			} else {
				// If a value is not defined, than raise a protocol exception
				// to warn the caller that this it provided an invalid message
				// object to this method.
				throw new InvalidMessageException(
						"Field does not contain a number"); //$NON-NLS-1$
			}
		} else {
			// The definition of this fixed data does not contain all
			// information it should have.
			throw new InvalidDefinitionException(
					"Incomplete fixed data element"); //$NON-NLS-1$
		}
	}

	/**
	 * Performs the write operation of a variable size data field to the output
	 * stream.
	 * 
	 * @param streamToWriteTo
	 *            The output stream where the message will be written.
	 * @param messageDataDef
	 *            The object that contains the definition of how to write
	 *            message data to the output stream.
	 * @param message
	 *            The message that is going to be sent.
	 * 
	 * @throws InvalidMessageException
	 *             If the data collected from the message is invalid,
	 *             considering the message definition made.
	 * @throws InvalidDefinitionException
	 *             If the message definition does not contain all necessary
	 *             information to encode the message.
	 */
	private void writeVariableSizeDataToStream(
			ByteArrayOutputStream streamToWriteTo,
			VariableSizeDataBean messageDataDef, ProtocolMessage message)
			throws InvalidMessageException, InvalidDefinitionException {
		// Retrieve data from the message definition object.
		// The fields description and how to use then can be found at
		// the ProtocolMessage extension point documentation.
		boolean isSizeSigned = messageDataDef.isSizeFieldSigned();
		int sizeFieldSize = messageDataDef.getSizeFieldSizeInBytes();
		String valueFieldName = messageDataDef.getValueFieldName();
		String charsetName = messageDataDef.getCharsetName();
		Object value = messageDataDef.getValue();

		if (valueFieldName != null) {
			if (value == null) {
				// If the message definition does not contain a value defined,
				// that means that it is a field is supposed to be defined by
				// the message object. See more information about the
				// "value" attribute in ProtocolMessage extension point
				// documentation.
				value = message.getFieldValue(valueFieldName);
			}
			if (value instanceof String) {
				// If a value is defined, then write the field to the output
				// stream.
				// In the variable size data field case, it is needed to write
				// firstly
				// the size of the field, and then its contents.
				try
                {
                    byte[] valueBytes = ((String) value).getBytes(charsetName);
                    int valueBytesSize = valueBytes.length;
                    writeNumberToStream(streamToWriteTo, valueBytesSize,
                    		sizeFieldSize, isSizeSigned);

                    streamToWriteTo.write(valueBytes, 0, valueBytes.length);
                }
                catch (UnsupportedEncodingException e)
                {
                    // If the encoding provided is not supported, that means that the
                    // message definition is incorrect.
                    throw new InvalidDefinitionException("Invalid charset name provided at message definition", e); //$NON-NLS-1$
                }
			} else {
				// If a value is not defined, than raise a protocol exception
				// to warn the caller that this it provided an invalid message
				// object to this method.
				throw new InvalidMessageException(
						"Value field does not contain a string"); //$NON-NLS-1$
			}
		} else {
			// The definition of this fixed data does not contain all
			// information it should have.
			throw new InvalidDefinitionException(
					"Incomplete fixed data element"); //$NON-NLS-1$
		}
	}

	/**
	 * Performs the write operation of a raw data field to the output stream.
	 * 
	 * @param streamToWriteTo
	 *            The output stream where the message will be written.
	 * @param messageDataDef
	 *            The object that contains the definition of how to write
	 *            message data to the output stream.
	 * @param message
	 *            The message that is going to be sent.
	 * 
	 * @throws ProtocolRawHandlingException
	 *             If it is not possible to write the data to the stream during
	 *             a raw field handling.
	 */
	private void writeRawDataToStream(ByteArrayOutputStream streamToWriteTo,
			RawDataBean messageDataDef, ProtocolMessage message)
			throws ProtocolRawHandlingException {
		// Creates a temporary output stream to provide to the raw data writer
		// handler. Then delegates the write operation to the handler class.
		ByteArrayOutputStream tempStream = new ByteArrayOutputStream();
		IRawDataHandler handler = messageDataDef.getHandler();

		handler.writeRawDataToStream(handle, tempStream, message, isBigEndianProtocol);

		try {
			tempStream.writeTo(streamToWriteTo);
		} catch (IOException e) {
			// Do nothing. ByteArrayOutputStreams cannot be closed.
		}
	}

	/**
	 * Performs the write operation of an iteratable block to the output stream.
	 * 
	 * @param streamToWriteTo
	 *            The output stream where the message will be written.
	 * @param messageDataDef
	 *            The object that contains the definition of how to write
	 *            message data to the output stream.
	 * @param message
	 *            The message that is going to be sent.
	 * 
	 * @throws InvalidMessageException
	 *             If the data collected from the message is invalid,
	 *             considering the message definition made.
	 * @throws InvalidDefinitionException
	 *             If the message definition does not contain all necessary
	 *             information to encode the message.
	 * @throws ProtocolRawHandlingException
	 *             If it is not possible to write the data to the stream during
	 *             a raw field handling.
	 */
	private void writeIteratableBlockToStream(
			ByteArrayOutputStream streamToWriteTo,
			IteratableBlockDataBean messageDataDef, ProtocolMessage message)
			throws ProtocolRawHandlingException, InvalidMessageException,
			InvalidDefinitionException {
		// Retrieve data from the message definition object.
		// The fields description and how to use then can be found at
		// the ProtocolMessage extension point documentation.
		String iterateOnField = messageDataDef.getIterateOnField();
		String iteratableBlockId = messageDataDef.getId();

		// Retrieves the number of times to iterate.
		Object fieldValue = message.getFieldValue(iterateOnField);

		if (fieldValue instanceof Integer) {
			// If it is possible to discover how much times to iterate, perform
			// the iteration. But firstly collect the internal beans to iterate
			// on.
			int numberOfIterations = ((Integer) fieldValue).intValue();
			Collection<IMsgDataBean> internalBeans = messageDataDef
					.getDataBeans();

			for (int i = 0; i < numberOfIterations; i++) {
				for (IMsgDataBean internalBean : internalBeans) {
					// Filters the internal beans just like sendMessage method
					// does to message fields not inside iteratable blocks.
					writeFilterMessageDef(internalBean, streamToWriteTo,
							message, iteratableBlockId, i);
				}
			}
		} else {
			// If the number of iterations is not defined, than raise a protocol
			// exception to warn the caller that this it provided an invalid
			// message object to this method.
			throw new InvalidMessageException(
					"Iterate on field value is not numeric"); //$NON-NLS-1$
		}
	}

	/**
	 * Writes number to the output stream, observing its signal and how many
	 * bytes it should use in the message. The signal and number of bytes are as
	 * defined in the message definition for this field.
	 * 
	 * @param streamToWriteTo
	 *            The output stream where the message will be written.
	 * @param data
	 *            The number that needs to be written to the output stream.
	 * @param numberOfBytes
	 *            How many bytes this number needs to use in the message.
	 * @param isSigned
	 *            True if this is a signed field; False if this is a unsigned
	 *            field.
	 */
	private void writeNumberToStream(ByteArrayOutputStream streamToWriteTo,
			Number data, int numberOfBytes, boolean isSigned) {
		byte[] bytesToWrite = getUnsignedBytes(data, numberOfBytes);
		if ((isSigned) && (data.longValue() < 0)) {
			// If the number should be written as "signed" to the output stream,
			// then it is needed to calculate its two-complement for determining
			// its byte representation. This is what is done in the next lines.
			for (int i = 0; i < numberOfBytes; i++) {
				bytesToWrite[i] = (byte) (bytesToWrite[i] ^ bytesToWrite[i]);
			}
			bytesToWrite[numberOfBytes - 1] = (byte) (bytesToWrite[numberOfBytes - 1] + 1);
		}

		// Depending on the protocol setting of big/little endian, the number
		// bytes need to be written in a specific order.
		if (isBigEndianProtocol) {
			for (int i = 0; i < numberOfBytes; i++) {
				streamToWriteTo.write(bytesToWrite[i]);
			}
		} else {
			for (int i = numberOfBytes - 1; i >= 0; i--) {
				streamToWriteTo.write(bytesToWrite[i]);
			}
		}
	}

	/**
	 * Reads a message from the input stream.
	 * 
	 * @param code
	 *            The code of the message to be read.
	 * @param messageDef
	 *            The message definition bean that will guide the decoding of
	 *            the message fields to be read from the input stream.
	 * 
	 * @throws IOException
	 *             If an input stream disconnection is detected while reading
	 *             data.
	 * @throws InvalidMessageException
	 *             If the data collected from the message is invalid,
	 *             considering the message definition made.
	 * @throws InvalidInputStreamDataException
	 *             If the data collected from the input stream is invalid,
	 *             considering the message definition made.
	 * @throws InvalidDefinitionException
	 *             If the message definition does not contain all necessary
	 *             information to decode the message.
	 * @throws ProtocolRawHandlingException
	 *             If it is not possible to read/write the data from/to the
	 *             stream during a raw field handling.
	 * @throws MessageHandleException
	 *             If the message cannot be handled.
	 */
	private void readReceivedMessage(long code, ProtocolMsgDefinition messageDef)
			throws ProtocolRawHandlingException, InvalidDefinitionException,
			InvalidInputStreamDataException, InvalidMessageException,
			MessageHandleException, IOException {
		// Certifies that the message definition is an incoming message
		// definition.
		if (isIncomingMessage(messageDef.getId())) {

			IMessageHandler handler = messageDef.getHandler();

			// Creates a message object to hold the retrieved data.
			ProtocolMessage message = new ProtocolMessage(code);

			// For each field data bean inside the message definition, filter
			// and read the information.
			List<IMsgDataBean> messageDataDefList = messageDef.getMessageData();
			for (IMsgDataBean messageDataDef : messageDataDefList) {
				readFilterMessageDef(messageDataDef, message, null, -1);
			}

			// After the message is entirely read, delegates the handling to the
			// handler defined at the ProtocolMessage extension. The fields
			// description and how to use then can be found at
			// the ProtocolMessage extension point documentation.

			ProtocolMessage returnedMessage = handler.handleMessage(handle, message);
			if (returnedMessage != null) {
				sendMessage(returnedMessage);
			}
		}
	}

	/**
	 * Determines which is the provided message data bean type and calls the
	 * correct handler for it to perform read operation from the input stream.
	 * 
	 * @param messageDataDef
	 *            The message data object that needs filtering.
	 * @param message
	 *            The message that is being decoded.
	 * @param iterableBlockId
	 *            The id of the iteratable block, as defined at the
	 *            ProtocolMessage extension.
	 * @param index
	 *            The iteration index. Range: 0 ~~ (iteratableBlockLength - 1)
	 * 
	 * @throws IOException
	 *             If an input stream disconnection is detected while reading
	 *             data.
	 * @throws InvalidInputStreamDataException
	 *             If the data collected from the input stream is invalid,
	 *             considering the message definition made.
	 * @throws InvalidDefinitionException
	 *             If the message definition does not contain all necessary
	 *             information to decode the message.
	 * @throws ProtocolRawHandlingException
	 *             If it is not possible to read the data from the stream during
	 *             a raw field handling.
	 */
	private void readFilterMessageDef(IMsgDataBean messageDataDef,
			ProtocolMessage message, String iterableBlockId, int index)
			throws ProtocolRawHandlingException, InvalidDefinitionException,
			InvalidInputStreamDataException, IOException {
		if (messageDataDef instanceof FixedSizeDataBean) {
			readFixedSizeData((FixedSizeDataBean) messageDataDef, message,
					iterableBlockId, index);
			FixedSizeDataBean fixedMsgDataDef = (FixedSizeDataBean)messageDataDef;
			message.setFieldSize(fixedMsgDataDef.getFieldName(), iterableBlockId, index, fixedMsgDataDef.getFieldSizeInBytes());	
			
		} else if (messageDataDef instanceof VariableSizeDataBean) {
			readVariableSizeData((VariableSizeDataBean) messageDataDef,
					message, iterableBlockId, index);
			VariableSizeDataBean variableMsgDataDef = (VariableSizeDataBean) messageDataDef;
			
			message.setFieldSize(variableMsgDataDef.getSizeFieldName(), iterableBlockId, index, variableMsgDataDef.getSizeFieldSizeInBytes());
			
		} else if (messageDataDef instanceof RawDataBean) {
			readRawData((RawDataBean) messageDataDef, message, iterableBlockId,
					index);
		} else {
			readIteratableBlock((IteratableBlockDataBean) messageDataDef,
					message);
		}
	}

	/**
	 * Performs the read operation of a fixed size data field from the input
	 * stream.
	 * 
	 * @param messageDataDef
	 *            The object that contains the definition of how to read the
	 *            field data from the input stream.
	 * @param message
	 *            The message that is being read.
	 * @param iterableBlockId
	 *            The id of the iteratable block, as defined at the message
	 *            definition.
	 * @param index
	 *            The iteration index. Range: 0 ~~ (iteratableBlockLength - 1)
	 * 
	 * @throws IOException
	 *             If an input stream disconnection is detected while reading
	 *             data.
	 * @throws InvalidInputStreamDataException
	 *             If the data collected from the input stream is invalid,
	 *             considering the message definition made.
	 * @throws InvalidDefinitionException
	 *             If the message definition does not contain all necessary
	 *             information to decode the message.
	 */
	private void readFixedSizeData(FixedSizeDataBean messageDataDef,
			ProtocolMessage message, String iterableBlockId, int index)
			throws InvalidDefinitionException, InvalidInputStreamDataException,
			IOException {
		// Retrieve data from the message definition object.
		// The fields description and how to use then can be found at
		// the ProtocolMessage extension point documentation.
		String fieldName = messageDataDef.getFieldName();
		boolean isSigned = messageDataDef.isFieldSigned();
		int fieldSize = messageDataDef.getFieldSizeInBytes();

		if (fieldName != null) {
			// Reads the number from the input stream, considering the
			// signed data of the field.
			Number value = getNumberDataFromInputStream(fieldSize, isSigned);

			if (value != null) {
				// If a value was read, then store the read data into the
				// message.
				message.setFieldValue(fieldName, iterableBlockId, index, value);
			}
		} else {
			// The definition of this fixed data does not contain all
			// information it should have.
			throw new InvalidDefinitionException(
					"Incomplete fixed data element"); //$NON-NLS-1$
		}
	}

	/**
	 * Performs the read operation of a variable size data field from the input
	 * stream.
	 * 
	 * @param messageDataDef
	 *            The object that contains the definition of how to read the
	 *            field data from the input stream.
	 * @param message
	 *            The message that is being read.
	 * @param iterableBlockId
	 *            The id of the iteratable block, as defined in the message
	 *            definition.
	 * @param index
	 *            The iteration index. Range: 0 ~~ (iteratableBlockLength - 1)
	 * 
	 * @throws IOException
	 *             If an input stream disconnection is detected while reading
	 *             data.
	 * @throws InvalidInputStreamDataException
	 *             If the data collected from the input stream is invalid,
	 *             considering the message definition made.
	 * @throws InvalidDefinitionException
	 *             If the message definition does not contain all necessary
	 *             information to decode the message.
	 */
	private void readVariableSizeData(VariableSizeDataBean messageDataDef,
			ProtocolMessage message, String iterableBlockId, int index)
			throws InvalidDefinitionException, InvalidInputStreamDataException,
			IOException {
		// Retrieve data from the message definition object.
		// The fields description and how to use then can be found at
		// the ProtocolMessage extension point documentation.
		
		String sizeFieldName = messageDataDef.getSizeFieldName();
		boolean isSizeSigned = messageDataDef.isSizeFieldSigned();
		int sizeFieldSize = messageDataDef.getSizeFieldSizeInBytes();
		String valueFieldName = messageDataDef.getValueFieldName();
		String charsetName = messageDataDef.getCharsetName();

		if (valueFieldName != null) {
			// Reads the size from the input stream, considering the
			// signed data of the field.
			Number size = getNumberDataFromInputStream(sizeFieldSize,
					isSizeSigned);

			// Based on the size read before, collect all bytes that comprise
			// the string (variable data field)
			byte[] valueArray = new byte[size.intValue()];
			int readBytes = 0;
			int remainingBytes = valueArray.length;

			while (remainingBytes > 0) {
				int tempReadBytes = -1;
				try {
					tempReadBytes = in.read(valueArray, readBytes,
							remainingBytes);
				} catch (IOException e) {
					// The input stream is probably broken
					handleIOExceptionOnStream();
					throw e;
				}
				readBytes += tempReadBytes;
				remainingBytes -= tempReadBytes;
			}

			// If a value was read, then store the read data into the
			// message.
			String value = new String(valueArray, charsetName);
			message
					.setFieldValue(valueFieldName, iterableBlockId, index,
							value);
			
			if(sizeFieldName != null && !sizeFieldName.equals("")) //$NON-NLS-1$
				message.setFieldValue(sizeFieldName, iterableBlockId, index,
							new Integer(sizeFieldSize).toString());
			
			
		} else {
			// The definition of this fixed data does not contain all
			// information it should have.
			throw new InvalidDefinitionException(
					"Incomplete fixed data element"); //$NON-NLS-1$
		}
	}

	/**
	 * Performs the read operation of a raw data field from the input stream.
	 * 
	 * @param messageDataDef
	 *            The object that contains the definition of how to read the
	 *            field data from the input stream.
	 * @param message
	 *            The message that is being read.
	 * @param iterableBlockId
	 *            The id of the iteratable block, as defined in the message
	 *            definition.
	 * @param index
	 *            The iteration index. Range: 0 ~~ (iteratableBlockLength - 1)
	 * 
	 * @throws IOException
	 *             If an input stream disconnection is detected while reading
	 *             data.
	 * @throws ProtocolRawHandlingException
	 *             If it is not possible to read the data from the stream during
	 *             a raw field handling.
	 * 
	 */
	private void readRawData(RawDataBean messageDataDef,
			ProtocolMessage message, String iterableBlockId, int index)
			throws ProtocolRawHandlingException, IOException {
		// Creates a temporary input stream for the raw data handler to use.
		// Note that this input stream is a special one that does not allow
		// closing, marking or reseting.
		ProtocolInputStream delegatableStream = new ProtocolInputStream();

		IRawDataHandler handler = (IRawDataHandler) messageDataDef.getHandler();

		// Creates a message store object for the raw data handler to have
		// access to the data already read until this moment. Note that the
		// message store object is read-only.
		MessageFieldsStore currentlyReadFields = new MessageFieldsStore(
				message, iterableBlockId, index);

		// Delegates the read operation to the raw data handler
		Map<String, Object> returnedFields = null;
		try {
			returnedFields = handler.readRawDataFromStream(handle, delegatableStream,
					currentlyReadFields, isBigEndianProtocol);

			// Merge the data read by the handler to the current message being
			// read.
			for (String fieldName : returnedFields.keySet()) {
				Object fieldValue = returnedFields.get(fieldName);
				message.setFieldValue(fieldName, iterableBlockId, index,
						fieldValue);
			}
		} catch (IOException e) {
			// The input stream is probably broken
			handleIOExceptionOnStream();
			throw e;
		}
	}

	/**
	 * Performs the read operation of a iteratable block from the input stream.
	 * 
	 * @param messageDataDef
	 *            The object that contains the definition of how to read the
	 *            iteratable block from the input stream.
	 * @param message
	 *            The message that is being read.
	 * 
	 * @throws IOException
	 *             If an input stream disconnection is detected while reading
	 *             data.
	 * @throws InvalidInputStreamDataException
	 *             If the data collected from the input stream is invalid,
	 *             considering the message definition made.
	 * @throws InvalidDefinitionException
	 *             If the message definition does not contain all necessary
	 *             information to decode the message.
	 * @throws ProtocolRawHandlingException
	 *             If it is not possible to read the data from the stream during
	 *             a raw field handling.
	 */
	private void readIteratableBlock(IteratableBlockDataBean messageDataDef,
			ProtocolMessage message) throws ProtocolRawHandlingException,
			InvalidDefinitionException, InvalidInputStreamDataException,
			IOException {
		// Retrieve data from the message definition object.
		// The fields description and how to use then can be found at
		// the ProtocolMessage extension point documentation.
		String iterateOnField = messageDataDef.getIterateOnField();
		String iteratableBlockId = messageDataDef.getId();

		// Retrieves the number of times to iterate.
		Object valueObj = message.getFieldValue(iterateOnField);

		if (valueObj instanceof Integer) {
			// If it is possible to discover how much times to iterate, perform
			// the iteration. But firstly collect the internal beans to iterate
			// on.
			int numberOfIterations = (Integer) valueObj;
			Collection<IMsgDataBean> internalMsgDefs = messageDataDef
					.getDataBeans();

			for (int i = 0; i < numberOfIterations; i++) {
				for (IMsgDataBean internalMsgDef : internalMsgDefs) {
					// Filters the internal beans just like readReceivedMessage
					// method does to message fields not inside iteratable
					// blocks.
					readFilterMessageDef(internalMsgDef, message,
							iteratableBlockId, i);
				}
			}
		} else {
			// If the number of iterations is not defined, than raise a protocol
			// exception to warn the caller that this it provided an invalid
			// message object to this method.
			throw new InvalidInputStreamDataException(
					"Iterate on field value is not numeric"); //$NON-NLS-1$
		}
	}

	/**
	 * Reads a number from the input stream.
	 * 
	 * @param numberOfBytes
	 *            How many bytes to collect from the input stream to form the
	 *            number.
	 * @param isSigned
	 *            True if the bytes collected are signed two-complement
	 *            representation; False if they can be interpreted as unsigned
	 *            (hence not requiring two-complement translation).
	 * 
	 * @return The number read from the input stream.
	 * 
	 * @throws IOException
	 *             If an input stream disconnection is detected while reading
	 *             data.
	 * @throws InvalidInputStreamDataException
	 *             If the data collected from the input stream is invalid,
	 *             considering the message definition made.
	 */
	private Number getNumberDataFromInputStream(int numberOfBytes,
			boolean isSigned) throws InvalidInputStreamDataException,
			IOException {
		Number value = null;
		try {
			switch (numberOfBytes) {
			case 1: // reads a byte
				if (isSigned) {
					value = in.readByte();
				} else {
					value = in.readUnsignedByte();
				}
				break;
			case 2: // reads a short
				if (isSigned) {
					value = in.readShort();
				} else {
					value = in.readUnsignedShort();
				}
				break;
			case 3:
				if (isSigned) {
					long tmpval;
					tmpval = in.readByte();
					tmpval <<= 8;
					tmpval += in.readShort(); 
					
					value = tmpval;
					
				} else {
					long tmpval;
					tmpval = in.readUnsignedByte();
					tmpval <<= 8;
					tmpval += in.readUnsignedShort(); 
					
					value = tmpval;
				}
				break;
				
			case 4: // reads an integer
				value = in.readInt();
				break;
			case 8: // reads a long
				value = in.readLong();
				break;
			default:
				// If the parameter numberOfBytes if not equal any of the
				// previous
				// alternatives, then it is not in a correct format, as there
				// are
				// not number representations with different sizes available.
				// Throw
				// an exception to warn the caller that it provided an invalid
				// parameter.
				throw new InvalidInputStreamDataException(
						"Unrecognized field size"); //$NON-NLS-1$
			}
		} catch (IOException e) {
			// The input stream is probably broken
			handleIOExceptionOnStream();
			throw e;
		}

		return value;
	}

	/**
	 * Given a number, get the unsigned byte representation of it.
	 * 
	 * @param number
	 *            The number to be converted to a byte representation.
	 * @param numberOfBytes
	 *            The number of bytes to use at the representation.
	 * 
	 * @return The byte representation of the provided number, using the
	 *         provided number of bytes.
	 */
	private byte[] getUnsignedBytes(Number number, int numberOfBytes) {
		long dataTmp = Math.abs(number.longValue());
		byte[] bytes = new byte[numberOfBytes];

		// For each 8 bits of the number binary representation, create a
		// correspondent byte and shift the original number to prepare it
		// for subsequent translations.
		for (int i = 0; i < numberOfBytes; i++) {
			byte oneByte = (byte) (dataTmp & 0xFF);
			dataTmp >>= 8;
			bytes[numberOfBytes - i - 1] = oneByte;
		}

		return bytes;
	}

	/**
	 * Tests if a given message id is an outgoing message
	 * 
	 * @param messageId
	 *            The id to be tested
	 * 
	 * @return True if the message is defined as outgoing message. False
	 *         otherwise
	 */
	private boolean isOutgoingMessage(String messageId) {
		boolean isOutgoingMessage = false;
		if ((outgoingMessages.contains(messageId))
				&& (!incomingMessages.contains(messageId))) {
			isOutgoingMessage = true;
		}

		return isOutgoingMessage;
	}

	/**
	 * Tests if a given message id is an incoming message
	 * 
	 * @param messageId
	 *            The id to be tested
	 * 
	 * @return True if the message is defined as incoming message. False
	 *         otherwise
	 */
	private boolean isIncomingMessage(String messageId) {
		boolean isIncomingMessage = false;
		if ((incomingMessages.contains(messageId))
				&& (!outgoingMessages.contains(messageId))) {
			isIncomingMessage = true;
		}

		return isIncomingMessage;
	}

	/**
	 * Common method for handling IOExceptions when reading/writing to streams
	 */
	private void handleIOExceptionOnStream() {

		if (isConnected()) {
			try {
				stopProtocol();
			} catch (IOException e1) {
				// Do nothing
			}
		}

		IModel model = ClientModel.getInstance();
		model.cleanStoppedProtocols();
		model = ServerModel.getInstance();
		model.cleanStoppedProtocols();
	}

	/**
	 * DESCRIPTION: This class is used to collect bytes that arrive at the input
	 * stream. <br>
	 * 
	 * RESPONSIBILITY: Monitor the input stream for available bytes and collect
	 * the bytes for message reading.<br>
	 * 
	 * COLABORATORS: None.<br>
	 * 
	 * USAGE: The class is intended to be used by the protocol engine class
	 * only.<br>
	 * For that, create a thread and set this class as its runnable. When it is
	 * no more needed, run method stopConsumer.<br>
	 * 
	 */
	private class Consumer implements Runnable {

		/**
		 * Flag that indicates if the consumer is monitoring the input stream
		 * and collecting bytes from it.
		 */
		private boolean isRunning = true;

		/**
		 * Starts monitoring and consuming bytes from the input stream.
		 * 
		 * OVERVIEW: Each time the while loop repeats, a byte is consumed from
		 * the input stream. The consumer continually tries to find a valid code
		 * in the message definitions map. When such message definition is
		 * found, then start reading the message based on it.
		 * 
		 * @see java.lang.Runnable#run()
		 */
		public void run() {

			long code = 0;
			// Executes the monitoring while the consumer is not stopped.
			while (isRunning) {
				try {
					if (code > Long.MAX_VALUE / 2) {
						// If the current code value is too big, being
						// impossible to find a message that fits in a long size
						// variable, then the consumer concludes the message
						// does not exist and the input stream is not
						// synchronized. For this reason the protocol is
						// stopped, because a non synchronized stream is
						// impossible to recover
						System.out
								.println("Message not found. Stopping protocol..."); //$NON-NLS-1$
						stopProtocol();
					} else {
						// Reads a byte from the input stream and append it to
						// the current code variable
						int nextByte = in.read();
						code <<= 8;
						code += nextByte;

						// Tries to find a message definition that has the
						// current code
						// Note: the code for incoming messages is kept as negative
						ProtocolMsgDefinition messageDef = messageDefCollection
								.get(-code);
						if (messageDef != null) {
							// If it finds a message with the current code,
							// reads the remaining of the message fields.
							synchronized (socket) {
								readReceivedMessage(-code, messageDef);
							}
							code = 0;
						}
					}
				} catch (IOException e) {
					// Handle exception only if the IOException was not caused
					// by protocol disconnection
					if (isRunning) {
						isRunning = false;
						if (exceptionHandler != null) {
							exceptionHandler.handleIOException(handle, e);
						}
					}
				} catch (Exception e) {
					isRunning = false;

					if (exceptionHandler != null) {
						// Delegate the exception to user
						if (e instanceof ProtocolInitException) {
							exceptionHandler
									.handleProtocolInitException(handle, (ProtocolInitException) e);
						} else if (e instanceof MessageHandleException) {
							exceptionHandler
									.handleMessageHandleException(handle, (MessageHandleException) e);
						} else if (e instanceof InvalidMessageException) {
							exceptionHandler
									.handleInvalidMessageException(handle, (InvalidMessageException) e);
						} else if (e instanceof InvalidInputStreamDataException) {
							exceptionHandler
									.handleInvalidInputStreamDataException(handle, (InvalidInputStreamDataException) e);
						} else if (e instanceof InvalidDefinitionException) {
							exceptionHandler
									.handleInvalidDefinitionException(handle, (InvalidDefinitionException) e);
						} else if (e instanceof ProtocolRawHandlingException) {
							exceptionHandler
									.handleProtocolRawHandlingException(handle, (ProtocolRawHandlingException) e);
						}
					}
				}
			}
		}

		/**
		 * Stops the consumer. After this operation is performed, the thread
		 * executing the consumer code will finish.
		 */
		public void stopConsumer() {
			isRunning = false;
		}
	}

	/**
	 * DESCRIPTION: The objects of this class are passed as parameter to raw
	 * data handlers. <br>
	 * 
	 * RESPONSIBILITY: Assure that the main protocol input stream will be
	 * protected from misuse by raw data handler developers.<br>
	 * 
	 * COLABORATORS: None.<br>
	 * 
	 * USAGE: The class is intended to be used by the protocol engine class
	 * only. It contains all the contract methods of the input stream classes,
	 * but does not perform close, mark and reset operations. <br>
	 * 
	 */
	private class ProtocolInputStream extends InputStream {
		/**
		 * @see java.io.InputStream#read()
		 */
		@Override
		public int read() throws IOException {
			return in.read();
		}

		/**
		 * @see java.io.InputStream#read(byte[])
		 */
		@Override
		public int read(byte b[]) throws IOException {
			return in.read(b, 0, b.length);
		}

		/**
		 * @see java.io.InputStream#read(byte[], int, int)
		 */
		@Override
		public int read(byte b[], int off, int len) throws IOException {
			return in.read(b, off, len);
		}

		/**
		 * @see java.io.InputStream#skip(long)
		 */
		@Override
		public long skip(long n) throws IOException {
			return in.skip(n);
		}

		/**
		 * @see java.io.InputStream#available()
		 */
		@Override
		public int available() throws IOException {
			return in.available();
		}

		/**
		 * @see java.io.InputStream#close()
		 */
		@Override
		public void close() throws IOException {
			// Do nothing
		}

		/**
		 * @see java.io.InputStream#mark(int)
		 */
		@Override
		public synchronized void mark(int readlimit) {
			// Do nothing
		}

		/**
		 * @see java.io.InputStream#reset()
		 */
		@Override
		public synchronized void reset() throws IOException {
			// Do nothing
		}

		/**
		 * Returns false to indicate that this input stream does not support
		 * marking
		 * 
		 * @see java.io.InputStream#markSupported()
		 */
		@Override
		public boolean markSupported() {
			return false;
		}
	}
}
