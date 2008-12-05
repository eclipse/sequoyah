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
 * Daniel Barboza Franco (Eldorado Research Institute) -  [243167] - Zoom mechanism not working properly 
 * Daniel Barboza Franco (Eldorado Research Institute) - Bug [248663] - Dependency between protocol and SWTRemoteDisplay
 ********************************************************************************/
package org.eclipse.tml.vncviewer.graphics.swt.img;

import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.tml.protocol.lib.ProtocolHandle;
import org.eclipse.tml.vncviewer.config.IPropertiesFileHandler;
import org.eclipse.tml.vncviewer.graphics.swt.ISWTPainter;
import org.eclipse.tml.vncviewer.graphics.swt.SWTRemoteDisplay;
import org.eclipse.tml.vncviewer.network.IVNCPainter;

public class SWTRemoteDisplayImg extends SWTRemoteDisplay {


	public SWTRemoteDisplayImg(Composite parent, Properties configProperties, IPropertiesFileHandler propertiesFileHandler) {
		this(parent, configProperties, propertiesFileHandler, null);
	}
	
	public SWTRemoteDisplayImg(Composite parent, Properties configProperties, IPropertiesFileHandler propertiesFileHandler, ISWTPainter painter_){
		super(parent, configProperties, propertiesFileHandler);
		
		painter = painter_;
		
		if (painter == null) {
			painter = new Painter(this);
		}
		
		paintListener = new PaintListener() {
            public void paintControl(PaintEvent e)
            {
            	SWTRemoteDisplayImg.this.paintControl(e);
            }
        };
        

	}

	public synchronized void start(ProtocolHandle handle) throws Exception {
		super.start(handle);

		canvas.getDisplay().asyncExec(new Runnable(){
			public void run() {
				canvas.addPaintListener(paintListener);
			}

		});
		((Painter)painter).addSWTRemoteDisplayImg(this);
	}
	
	
	public synchronized void stop() {
		super.stop();
		((Painter)painter).removeSWTRemoteDisplayImg((SWTRemoteDisplayImg)this);
	}
	
	protected int getCanvasStyle() {
		return SWT.NO_BACKGROUND;  
	}

	public IVNCPainter getPainter() {
		return (Painter) painter;
	}

	protected void redrawScreen() {
		getDisplay().asyncExec(new Runnable() {
			public void run() {
				Canvas c = getCanvas();
				if (c != null && !c.isDisposed()) {
					c.redraw();
				}
			}
		});
	}

	protected void redrawScreen(final int x, final int y, final int width,
			final int height) {
		getDisplay().asyncExec(new Runnable() {
			public void run() {
				Canvas c = getCanvas();
				if (c != null && !c.isDisposed()) {
					c.redraw(x, y, width, height, false);
				}
			}
		});
	}

	public void dispose() {
		super.dispose();
		if (painter != null) {
			((Painter)painter).dispose();
			
			
			//TODO: REMOVESWTRD
			
		}
	}

	protected void paintControl(PaintEvent event) {
		
		ImageData id = painter != null? ((Painter)painter).getImageData():null;
		Image image = null;
		
		if (id != null) {
			id = id.scaledTo((int)(id.width * getZoomFactor()), (int)(id.height * getZoomFactor()));
			image = new Image(event.gc.getDevice(),id);	
		}
		
		
		event.gc.setBackground(canvas.getBackground());
		event.gc.setForeground(canvas.getForeground());
		
		if (image == null) {
			event.gc.fillRectangle(event.x, event.y, event.width, event.height);
		} else {
			Rectangle r = image.getBounds();
			int w = Math.min(event.width, r.width);
			int h = Math.min(event.height, r.height);
			
			event.gc.drawImage(image, event.x, event.y, w, h, event.x, event.y, w, h);
			
			/* TODO : Bug 244249 - Canvas background repaint
			if (w < event.width) {
				event.gc.fillRectangle(event.x + w, event.y, event.width - w,
						event.height);
			}
			if (h < event.height) {
				event.gc.fillRectangle(event.x, event.y + h, event.width,
						event.height - h);
			}
			*/
			
			
			image.dispose();
		}
	}
	
}
