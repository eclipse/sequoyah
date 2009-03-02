/********************************************************************************
 * Copyright (c) 2008 MontaVista Software. All rights reserved.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributor:
 * Eugene Melekhov (Montavista) - Bug [227793] - Implementation of the several encodings, performance enhancement etc
 *
 * Contributors:
 * Fabio Rigo (Eldorado Research Institute) - Bug [262632] - Avoid providing raw streams to the user in the protocol framework
 ********************************************************************************/
package org.eclipse.tml.vncviewer.graphics;

import java.io.DataInput;

import org.eclipse.tml.vncviewer.network.RectHeader;

public interface IPaintStrategy {

	public void processRectangle(RectHeader rh, DataInput in) throws Exception;

}
