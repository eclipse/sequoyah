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
 * Daniel Barboza Franco (Motorola) - Bug [227793] - Implementation of the several enc(...). A little improvement into the reading mechanism (readPixels()).
 ********************************************************************************/
package org.eclipse.tml.vncviewer.network;

import static org.eclipse.tml.vncviewer.VNCViewerPlugin.log;

import java.io.DataInputStream;
import java.io.IOException;

import org.eclipse.tml.vncviewer.exceptions.ProtoClientException;
import org.eclipse.tml.vncviewer.graphics.HexTilePaintStrategy;
import org.eclipse.tml.vncviewer.graphics.IPaintStrategy;
import org.eclipse.tml.vncviewer.graphics.IPainterContext;
import org.eclipse.tml.vncviewer.graphics.RREPaintStrategy;
import org.eclipse.tml.vncviewer.graphics.RawPaintStrategy;
import org.eclipse.tml.vncviewer.graphics.ZRLEPaintStrategy;
import org.eclipse.tml.vncviewer.graphics.ZlibPaintStrategy;

/**
 * Abstract class that encapsulates common functionality for IVNCPainter 
 * successors 
 */
public abstract class AbstractVNCPainter implements IVNCPainter {

	/**
	 * Pixel format currently used
	 */
	protected PixelFormat pixelFormat;

	/**
	 * Bytes per pixel
	 */
	protected int bytesPerPixel;

	/**
	 * Framebuffer width
	 */
	private int width;

	/**
	 * Framebuffer height
	 */
	private int height;
	
	private IPaintStrategy rawPaintStrategy;

	private IPaintStrategy rrePaintStrategy;

	private IPaintStrategy hexTilePaintStrategy;

	private  IPaintStrategy zlibPaintStrategy;

	private IPaintStrategy zrlePaintStrategy;

	public AbstractVNCPainter() {
		IPainterContext pc = getPainterContext();
		rawPaintStrategy = new RawPaintStrategy(pc);
		rrePaintStrategy = new RREPaintStrategy(pc);
		hexTilePaintStrategy = new HexTilePaintStrategy(pc);
		zlibPaintStrategy = new ZlibPaintStrategy(pc);
		zrlePaintStrategy = new ZRLEPaintStrategy(pc);
	}

	
	public int[] getSupportedEncodings() {
		return new int[] { VNCProtocol.HEXTILE_ENCODING,
				VNCProtocol.ZRLE_ENCODING, VNCProtocol.ZLIB_ENCODING,
				VNCProtocol.RRE_ENCODING, VNCProtocol.RAW_ENCODING, };
	}

	public static int[] getSupportedEncodingsStatic() {
		return new int[] { VNCProtocol.HEXTILE_ENCODING,
				VNCProtocol.ZRLE_ENCODING, VNCProtocol.ZLIB_ENCODING,
				VNCProtocol.RRE_ENCODING, VNCProtocol.RAW_ENCODING, };
	}

	public void processRectangle(RectHeader rectHeader, DataInputStream in) throws Exception {
		IPaintStrategy ps = getPaintStrategy(rectHeader.getEncoding());
		
		if (ps != null) {
			ps.processRectangle(rectHeader, in);
		} else {
			log(AbstractVNCPainter.class).error("This encoding is not supported.");
		}
		
	}

	public PixelFormat getPixelFormat() {
		return pixelFormat;
	}

	public void setPixelFormat(PixelFormat pixelFormat) {
		this.pixelFormat = pixelFormat;
		bytesPerPixel = ((pixelFormat.getBitsPerPixel())/8);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	
	
	
	
	
	
	private byte[] readpixelsbytes(DataInputStream in, int bytesNum) throws ProtoClientException{
		
		byte pixelsb[] = new byte[bytesNum];
		
		int bytesRead = 0;
		
		// Read the array of pixels from the VNC Server
		while (bytesRead < bytesNum) {
			int numRead;
			
			try {
				numRead = in.read(pixelsb, bytesRead, bytesNum - bytesRead);
			}
			catch (IOException ioe){
				log(VNCProtocol.class).error("Rectangle message error: " + ioe.getMessage());
				throw new ProtoClientException("Rectangle message error.");
			}
			
			if (numRead >= 0) {
				bytesRead += numRead;
			}
		}
		
		return pixelsb;
		
	}
	
	
	public int[] readPixels(DataInputStream is, int w, int h){
		
		byte pixelsBucket[] = {0x00};
		int result[] = new int[w*h];
				
		int pixelsNum = w * h;
		int bytesToRead = pixelsNum * ((pixelFormat.getBitsPerPixel())/8);
		
		try {
			pixelsBucket = readpixelsbytes(is, bytesToRead);
		} catch (ProtoClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		for (int j=0; j < (pixelsNum); j++) {
			//int line = ((int)(j / width));
			//int bytesPerPixel = ((pixelFormat.getBitsPerPixel())/8);
			int pos = j * bytesPerPixel;
		
			int pixel = 0;

			/*
			if (bytesPerPixel == 1) {
				pixel = is.readByte() & 0xFF;
			} 
			else {*/
			boolean bigEndian = pixelFormat.getBigEndianFlag() != 0;
			if (bigEndian) {
				for (int k = 0; k < bytesPerPixel ; k++) {
					//int b = is.readUnsignedByte();
					//pixel = (pixel << 8) | (b & 0xFF);
						
					pixel = (pixel << 8) |(pixelsBucket[pos + k] & 0xFF);
				}
			}
			else {
				for (int i = bytesPerPixel - 1 ; i >= 0 ; i--) {
					
					pixel = (pixel << 8) |(pixelsBucket[pos + i] & 0xFF);
				}
			}

		}

		
		return result;
	}
	
	
	
	
	/**
	 * Reads the pixel from the input stream according to the current pixel 
	 * format endianness and <code>bytesPerPixel</code> pixel representation length.   
	 * @param is <code>DataInputStream</code> stream to read the pixel value from
	 * @param bytesPerPixel number of bytes per pixel
	 * @return read pixel
	 * @throws Exception
	 */
	protected int readPixel(DataInputStream is, int bytesPerPixel) throws Exception {
		int result = 0;
		if (bytesPerPixel == 1) {
			result = is.readByte() & 0xFF;
		} else {
			boolean bigEndian = pixelFormat.getBigEndianFlag() != 0;
			if (bigEndian) {
				for (int jj = 0; jj < bytesPerPixel ; jj++) {
					int b = is.readUnsignedByte();
					result = (result << 8) | (b & 0xFF);
				}
			}
			else {
				for (int jj = 0; jj < bytesPerPixel ; jj++) {
					int b = is.readUnsignedByte();
					result = result | ((b & 0xFF) << jj*8);
				}
			}
		}
		return result;
	}
	
	/**
	 * Reads pixel according to the current pixel format from the <code>DataInputStream</code>
	 * @param is input stream to read from
	 * @return the pixel value
	 * @throws Exception
	 */
	protected int readPixel(DataInputStream is) throws Exception {
		return readPixel(is, bytesPerPixel);
	}

	protected abstract IPainterContext getPainterContext();

	/**
	 * Returns the paint strategy object for the encoding
	 * @param encoding
	 * @return the paint strategy object for the encoding
	 */
	protected IPaintStrategy getPaintStrategy(int encoding) {
		switch (encoding) {
		case VNCProtocol.RAW_ENCODING:
			return rawPaintStrategy;
		case VNCProtocol.RRE_ENCODING:
			return rrePaintStrategy;
		case VNCProtocol.HEXTILE_ENCODING:
			return hexTilePaintStrategy;
		case VNCProtocol.ZLIB_ENCODING:
			return zlibPaintStrategy;
		case VNCProtocol.ZRLE_ENCODING:
			return zrlePaintStrategy;
		default:
			return null;
		}
	}

}
