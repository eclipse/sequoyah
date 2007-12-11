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




public interface IVNCPainter extends IPainter{

	/**
	 * Sets the PixelFormat used by the IPainter implementation.
	 * @param pixelFormat the PixelFomat.
	 */
	public void setPixelFormat(PixelFormat pixelFormat);
	
	/**
	 * Process a rectangle of data sent by the VNC Server.
	 * @param encoding the encoding used to represent the data.
	 * @param data the rectangle data
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param width the rectangle's width
	 * @param  height the rectangle's height
	 */
	public void processRectangle(int encoding, byte[] data, int x, int y, int width, int height);
	
}
