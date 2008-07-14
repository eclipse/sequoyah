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
import org.eclipse.tml.vncviewer.network.VNCProtocol;

public class HexTilePaintStrategy extends AbstractPaintStrategy {
	
	private static final int RAW = 1;
	private static final int BACKGROUND_SPECIFIED = 2;
	private static final int FOREGROUND_SPECIFIED = 4;
	private static final int ANY_SUBRECTS = 8;
	private static final int SUBRECTS_COLOURED = 16;

	protected int hextileBackground = 0;;

	protected int hextileForeground = 0;

	public HexTilePaintStrategy(IPainterContext context) {
		super(context);
	}

	public void processRectangle(RectHeader rh, DataInputStream in) throws Exception {
		int x = rh.getX();
		int y = rh.getY();
		int width = rh.getWidth();
		int height = rh.getHeight();
		for (int ty = y; ty < y + height; ty += 16) {
	        int th = Math.min(y+height-ty, 16);
			for (int tx = x; tx < x + width; tx += 16) {
				int tw = Math.min(x+width-tx, 16);
				processHextileSubrect(in, tx, ty, tw, th);
			}
		}
	}
	
	void processHextileSubrect(DataInputStream in, int x, int y, int width, int height)
			throws Exception {
		int subencoding = in.readUnsignedByte();
		if ((subencoding & RAW) != 0) {
			getContext().processRectangle(new RectHeader(x, y, width, height, VNCProtocol.RAW_ENCODING),in);
			return;
		}
		if ((subencoding & BACKGROUND_SPECIFIED) != 0) {
			hextileBackground = getContext().readPixel(in);
		}
		getContext().fillRect(hextileBackground, x, y, width, height);

		if ((subencoding & FOREGROUND_SPECIFIED) != 0) {
			hextileForeground = getContext().readPixel(in);
		}
		if ((subencoding & ANY_SUBRECTS) == 0)
			return;

		boolean colored = (subencoding & SUBRECTS_COLOURED) != 0;
		int subRectsCount = in.readUnsignedByte();
		for (int i = 0; i < subRectsCount; i++) {
			int pix = hextileForeground;
			if (colored) {
				pix = getContext().readPixel(in);
				hextileForeground = pix;
			}
			int position = in.readUnsignedByte();
			int size = in.readUnsignedByte();
			int subRectX = x + (position >> 4);
			int subRectY = y + (position & 0xF);
			int subRectWidth = (size >> 4) + 1;
			int subRectHeight = (size & 0xF) + 1;
			getContext().fillRect(pix, subRectX, subRectY, subRectWidth, subRectHeight);
		}
	}
}
