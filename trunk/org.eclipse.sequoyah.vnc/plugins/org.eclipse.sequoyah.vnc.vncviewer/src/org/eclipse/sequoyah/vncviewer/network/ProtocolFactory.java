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

import org.eclipse.tml.vncviewer.graphics.swt.VNCSWTPainter;

/**
 * This class implements the "abstract factory method" pattern.
 * It instantiates objects for Protocols chosen at runtime.
 */
public class ProtocolFactory {


	/**
	 * Returns a protocol dynamically chosen.
	 * 
	 * @param prot the String specifying the protocol.
	 * 
	 * @return the Protocol instance or null if prot can't be associated to a known protocol.
	 */
	public static IProtoClient getProtocol (String prot) {
		
		if (prot.equals("VNC 3.3")) {
			
			IVNCPainter painter = new VNCSWTPainter();
			return (VNCProtocol) (new VNCProtocol33(painter));
		}
		else if (prot.equals("VNC 3.7")){

			IVNCPainter painter = new VNCSWTPainter();
			return (VNCProtocol) (new VNCProtocol37(painter));
			
			
		}
		else if (prot.equals("VNC 3.8")){
			
			IVNCPainter painter = new VNCSWTPainter();
			return (VNCProtocol) (new VNCProtocol38(painter));
		}

		
		return null;
	}

}
