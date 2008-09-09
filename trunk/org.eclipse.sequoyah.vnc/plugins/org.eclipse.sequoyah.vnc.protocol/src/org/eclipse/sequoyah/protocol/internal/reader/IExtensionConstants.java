/********************************************************************************
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributor:
 * Fabio Rigo
 *
 * Contributors:
 * Fabio Rigo - Bug [242757] - Protocol does not support Unicode on variable sized fields
 * Fabio Rigo (Eldorado Research Institute) - [246212] - Enhance encapsulation of protocol implementer
 * Daniel Barboza Franco (Eldorado Research Institute) - Bug [242924] - There is no way to keep the size of a Variable Size Data read
 ********************************************************************************/
package org.eclipse.tml.protocol.internal.reader;

/**
 * DESCRIPTION: This interface describes all identifiers from the
 * <b>ProtocolDefinition</b>, <b>ProtocolMessage</b> and
 * <b>ProtocolMessageOrientation</b> extension points.<br>
 * 
 * RESPONSIBILITY: Define all constants needed to access extension data in one
 * place.<br>
 * 
 * COLABORATORS: None.<br>
 * 
 * USAGE: Define the constants used to read information from extensions.<br>
 * 
 */
public interface IExtensionConstants {

	// Constants that defines the extension points ids
	String PROTOCOL_EXTENSION_POINT = "org.eclipse.tml.protocol.protocolDefinition";
	String PROTOCOL_MESSAGE_EXTENSION_POINT = "org.eclipse.tml.protocol.protocolMessage";
	String PROTOCOL_MESSAGE_ORIENTATION_EXTENSION_POINT = "org.eclipse.tml.protocol.protocolMessageOrientation";

	// Constants that define base constants of ProtocolDefinition extension
	// point
	String PROTOCOL_ELEM = "protocol";
	String PROTOCOL_ID_ATTR = "protocolId";
	String PROTOCOL_PARENT_PROTOCOL_ATTR = "parentProtocol";
	String PROTOCOL_IS_BIG_ENDIAN_ATTR = "isBigEndianProtocol";
	String PROTOCOL_INITIALIZER_ATTR = "initializer";

	// Constants that define base constants of ProtocolMessage extension point
	String PROTOCOL_MESSAGE_ELEM = "message";
	String PROTOCOL_MESSAGE_ID_ATTR = "messageId";
	String PROTOCOL_MESSAGE_PROTOCOL_ID_ATTR = "protocolId";
	String PROTOCOL_MESSAGE_CODE_ATTR = "messageCode";
	String PROTOCOL_MESSAGE_CODE_SIGNED_ATTR = "isMessageCodeSigned";
	String PROTOCOL_MESSAGE_CODE_SIZE_ATTR = "messageCodeSizeInBytes";
	String PROTOCOL_MESSAGE_HANDLER_ATTR = "messageHandler";

	// Constants that define constants for Fixed Data elements, from
	// ProtocolMessage extension point
	String PROTOCOL_MESSAGE_FIXED_DATA_ELEM = "fixedSizeData";
	String PROTOCOL_MESSAGE_FIXED_FIELD_NAME_ATTR = "fieldName";
	String PROTOCOL_MESSAGE_FIXED_FIELD_SIGNED_ATTR = "isFieldSigned";
	String PROTOCOL_MESSAGE_FIXED_FIELD_SIZE_ATTR = "fieldSizeInBytes";
	String PROTOCOL_MESSAGE_FIXED_FIELD_VALUE_ATTR = "value";

	// Constants that define constants for Variable Data elements, from
	// ProtocolMessage extension point
	String PROTOCOL_MESSAGE_VARIABLE_DATA_ELEM = "variableSizeData";
	String PROTOCOL_MESSAGE_VARIABLE_SIZE_FIELD_NAME_ATTR = "sizeFieldName";
	String PROTOCOL_MESSAGE_VARIABLE_SIZE_FIELD_SIGNED_ATTR = "isSizeFieldSigned";
	String PROTOCOL_MESSAGE_VARIABLE_SIZE_FIELD_SIZE_ATTR = "sizeFieldSizeInBytes";
	String PROTOCOL_MESSAGE_VARIABLE_VALUE_FIELD_NAME_ATTR = "valueFieldName";
	String PROTOCOL_MESSAGE_VARIABLE_CHARSET_NAME_ATTR = "charsetName";
	String PROTOCOL_MESSAGE_VARIABLE_VALUE_FIELD_VALUE_ATTR = "value";	

	// Constants that define constants for Raw Data elements, from
	// ProtocolMessage extension point
	String PROTOCOL_MESSAGE_RAW_DATA_HANDLER_ELEM = "rawDataHandler";
	String PROTOCOL_MESSAGE_RAW_DATA_EXECUTABLE_ATTR = "rawDataHandler";

	// Constants that define constants for Iteratable Block elements, from
	// ProtocolMessage extension point
	String PROTOCOL_MESSAGE_ITERATABLE_BLOCK_ELEM = "iteratableBlock";
	String PROTOCOL_MESSAGE_ITERATABLE_BLOCK_ID_ATTR = "iterableBlockId";
	String PROTOCOL_MESSAGE_ITERATABLE_BLOCK_ITERATE_ON_ATTR = "iterateOn";

	// Constants that define constants for ProtocolMessageOrientation extension
	// point
	String PROTOCOL_MESSAGE_ORIENTATION_CLIENT_ELEM = "clientMessage";
	String PROTOCOL_MESSAGE_ORIENTATION_SERVER_ELEM = "serverMessage";
	String PROTOCOL_MESSAGE_ORIENTATION_PROTOCOL_ID_ATTR = "protocolId";
	String PROTOCOL_MESSAGE_ORIENTATION_MESSAGE_ID_ATTR = "messageId";
}
