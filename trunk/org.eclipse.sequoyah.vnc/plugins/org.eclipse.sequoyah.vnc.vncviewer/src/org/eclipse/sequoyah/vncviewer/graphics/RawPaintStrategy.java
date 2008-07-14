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

public class RawPaintStrategy extends AbstractPaintStrategy {

	public RawPaintStrategy(IPainterContext context) {
		super(context);
	}
	
	public void processRectangle(RectHeader rh, DataInputStream in) throws Exception{
		int width = rh.getWidth();
		int height = rh.getHeight();
		int pixels[] = new int[width*height];
		IPainterContext pc = getContext();
		
		/*
		for (int i = 0, e = width*height; i < e; i++) {
			pixels[i] = pc.readPixel(in);
		}
		*/
		
		pc.readpixels(in, width, height);
		pc.setPixels(rh.getX(), rh.getY(), width, height, pixels, 0);
	}

}
