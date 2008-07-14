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
 * {Name} (company) - description of contribution.
 ********************************************************************************/
package org.eclipse.tml.vncviewer.graphics;

import java.io.DataInputStream;

import org.eclipse.tml.vncviewer.network.RectHeader;

public class RREPaintStrategy extends AbstractPaintStrategy {

	public RREPaintStrategy(IPainterContext context) {
		super(context);
	}
	
	public void processRectangle(RectHeader rh, DataInputStream in) throws Exception {
		int x = rh.getX();
		int y = rh.getY();
		int width = rh.getWidth();
		int height = rh.getHeight();

		int subrectsCount = in.readInt();
	    int backgroundPixel = getContext().readPixel(in); 
        getContext().fillRect(backgroundPixel, x, y, width, height);

	    for (int i = 0; i < subrectsCount; i++) {
		    int subRectBackgroundPixel = getContext().readPixel(in); 
		    int subRectX = x + in.readUnsignedShort();
		    int subRectY = y + in.readUnsignedShort();
		    int subRectWidth = in.readUnsignedShort();
		    int subRectHeight = in.readUnsignedShort();
		    getContext().fillRect(subRectBackgroundPixel, subRectX, subRectY, subRectWidth, subRectHeight);
	    }
	}

}
