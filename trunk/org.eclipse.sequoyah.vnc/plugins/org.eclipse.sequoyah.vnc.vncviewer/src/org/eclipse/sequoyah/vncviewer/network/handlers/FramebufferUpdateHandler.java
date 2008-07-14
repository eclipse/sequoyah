/********************************************************************************
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributor:
 * Fabio Rigo
 *
 * Contributors:
 * {Name} (company) - description of contribution.
 ********************************************************************************/
package org.eclipse.tml.vncviewer.network.handlers;

import org.eclipse.tml.protocol.lib.IMessageHandler;
import org.eclipse.tml.protocol.lib.IProtocolImplementer;
import org.eclipse.tml.protocol.lib.ProtocolMessage;
import org.eclipse.tml.vncviewer.network.IVNCPainter;
import org.eclipse.tml.vncviewer.network.RectHeader;
import org.eclipse.tml.vncviewer.network.VNCProtocol;

/**
 * DESCRIPTION: This class consists of the Framebuffer Update message handler.<br>
 * 
 * 
 * RESPONSIBILITY: Handle the Framebuffer Update message after it is completely
 * read from the socket
 * 
 * COLABORATORS: None<br>
 * 
 * USAGE: This class is intended to be used by Eclipse.<br>
 * 
 */
public class FramebufferUpdateHandler implements IMessageHandler {

	public ProtocolMessage handleMessage(
			IProtocolImplementer protocolImplementer, ProtocolMessage message) {
		
		
		return null;
	}
}
