/********************************************************************************
 * Copyright (c) 2008 Motorola Inc and Others. All rights reserved.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributor:
 * Fabio Rigo (Eldorado Research Institute) 
 * [246212] - Enhance encapsulation of protocol implementer
 *
 * Contributors:
 * Fabio Rigo (Eldorado Research Institute) - Bug [262632] - Avoid providing raw streams to the user in the protocol framework
 *******************************************************************************/

package org.eclipse.tml.vncviewer.network;

import java.io.IOException;

import org.eclipse.tml.protocol.lib.IProtocolExceptionHandler;
import org.eclipse.tml.protocol.lib.ProtocolHandle;
import org.eclipse.tml.protocol.lib.exceptions.InvalidDefinitionException;
import org.eclipse.tml.protocol.lib.exceptions.InvalidInputStreamDataException;
import org.eclipse.tml.protocol.lib.exceptions.InvalidMessageException;
import org.eclipse.tml.protocol.lib.exceptions.MessageHandleException;
import org.eclipse.tml.protocol.lib.exceptions.ProtocolHandshakeException;
import org.eclipse.tml.protocol.lib.exceptions.ProtocolRawHandlingException;

public class VNCProtocolExceptionHandler implements IProtocolExceptionHandler {

	public void handleIOException(ProtocolHandle handle, IOException e) {
		//VNCProtocolRegistry.getInstance().unregister(handle);
	}

	public void handleInvalidDefinitionException(ProtocolHandle handle,
			InvalidDefinitionException e) {
	}

	public void handleInvalidInputStreamDataException(ProtocolHandle handle,
			InvalidInputStreamDataException e) {
	}

	public void handleInvalidMessageException(ProtocolHandle handle,
			InvalidMessageException e) {
	}

	public void handleMessageHandleException(ProtocolHandle handle,
			MessageHandleException e) {
	}

	public void handleProtocolHandshakeException(ProtocolHandle handle,
			ProtocolHandshakeException e) {
	}

	public void handleProtocolRawHandlingException(ProtocolHandle handle,
			ProtocolRawHandlingException e) {
	}
}
