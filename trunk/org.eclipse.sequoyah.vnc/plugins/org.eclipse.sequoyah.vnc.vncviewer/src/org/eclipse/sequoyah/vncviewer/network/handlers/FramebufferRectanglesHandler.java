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

import static org.eclipse.tml.vncviewer.VNCViewerPlugin.log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.tml.protocol.lib.IMessageFieldsStore;
import org.eclipse.tml.protocol.lib.IProtocolImplementer;
import org.eclipse.tml.protocol.lib.IRawDataHandler;
import org.eclipse.tml.protocol.lib.ProtocolMessage;
import org.eclipse.tml.protocol.lib.exceptions.ProtocolException;
import org.eclipse.tml.vncviewer.graphics.swt.VNCSWTPainter;
import org.eclipse.tml.vncviewer.network.PixelFormat;
import org.eclipse.tml.vncviewer.network.VNCProtocol;

/**
 * DESCRIPTION: This class consists of the reader for the framebuffer
 * rectangles, which is part of the Framebuffer Update message.<br>
 * 
 * 
 * RESPONSIBILITY: Provide the protocol framework with framebuffer data.<br>
 * 
 * COLABORATORS: None<br>
 * 
 * USAGE: This class is intended to be used by Eclipse.<br>
 * 
 */
public class FramebufferRectanglesHandler implements IRawDataHandler {

	public Map<String, Object> readRawDataFromStream(InputStream dataStream,
			IMessageFieldsStore currentlyReadFields,
			IProtocolImplementer protocolImplementer, boolean isBigEndian)
			throws IOException, ProtocolException {

		// Determine the number of pixels using the width and height already
		// read
		int w = (Integer) currentlyReadFields.getFieldValue("width");
		int h = (Integer) currentlyReadFields.getFieldValue("height");
		int pixelsNum = w * h;

		Map<String, Object> fieldsMap = new HashMap<String, Object>();

		if (protocolImplementer instanceof VNCProtocol) {
			// Gets the pixel format, that defines how the pixels are
			// transmitted
			// in this VNC connection.
			VNCSWTPainter painter = (VNCSWTPainter) ((VNCProtocol) protocolImplementer)
					.getVncPainter();
			PixelFormat pixelFormat = painter.getPixelFormat();

			// final int bytesNum = pixelsNum * 4;
			final int bytesNum = pixelsNum
					* ((pixelFormat.getBitsPerPixel()) / 8);

			byte pixelsb[] = new byte[bytesNum];
			int bytesRead = 0;

			// Read the array of pixels from the VNC Server
			while (bytesRead < bytesNum) {
				int numRead;

				try {
					numRead = dataStream.read(pixelsb, bytesRead, bytesNum
							- bytesRead);
				} catch (IOException ioe) {
					log(VNCProtocol.class).error(
							"Rectangle message error: " + ioe.getMessage());
					throw new ProtocolException("Rectangle message error.");
				}

				if (numRead >= 0) {
					bytesRead += numRead;
				}
			}

			// Stores the pixel data into the map to be returned to the
			// framework
			fieldsMap.put("pixelsData", pixelsb);
		}

		return fieldsMap;
	}

		public void writeRawDataToStream(ByteArrayOutputStream dataStream,
			ProtocolMessage messageToGetInformationFrom,
			IProtocolImplementer protocolImplementer, boolean isBigEndian)
			throws ProtocolException {

		// No implementation. This is a client plugin only
	}
}
