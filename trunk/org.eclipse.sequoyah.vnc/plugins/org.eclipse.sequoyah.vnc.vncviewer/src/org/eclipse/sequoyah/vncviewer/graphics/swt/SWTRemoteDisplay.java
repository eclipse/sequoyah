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
 * Fabio Rigo - Bug [221741] - Support to VNC Protocol Extension
 * Eugene Melekhov (Montavista) - Bug [227793] - Implementation of the several encodings, performance enhancement etc
 * Fabio Rigo(Eldorado Research Institute) - Bug [244062] - SWTRemoteDisplay do not force the first update request to be full
 * Fabio Rigo(Eldorado Research Institute) - Bug [244806] - SWTRemoteDisplay state is not consistent on errors
 ********************************************************************************/

package org.eclipse.tml.vncviewer.graphics.swt;

import static org.eclipse.tml.vncviewer.VNCViewerPlugin.log;

import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.tml.protocol.PluginProtocolActionDelegate;
import org.eclipse.tml.protocol.lib.IProtocolImplementer;
import org.eclipse.tml.protocol.lib.ProtocolMessage;
import org.eclipse.tml.vncviewer.config.IPropertiesFileHandler;
import org.eclipse.tml.vncviewer.config.IVNCProperties;
import org.eclipse.tml.vncviewer.graphics.IRemoteDisplay;
import org.eclipse.tml.vncviewer.graphics.swt.img.Painter;
import org.eclipse.tml.vncviewer.network.IVNCPainter;
import org.eclipse.tml.vncviewer.network.VNCProtocol;

/**
 * This class implements the GUI part of a Remote Desktop Viewer. It also uses a
 * protocol to send local events and receive server updates.
 */
public class SWTRemoteDisplay extends Composite implements IRemoteDisplay {

	//private IProtocolImplementer protoClient;
	protected Canvas canvas;
	private VNCProtocol protoClient;
	private Image screen = null;
	// private ImageData imgData;
	private int retries;

	private Properties configurationProperties;
	private IPropertiesFileHandler propertiesFileHanlder;
	private SWTVNCEventTranslator eventTranslator;

	private boolean active = false;

	private long firstRefreshDelayMs; /*
	 * Time in milliseconds for the first
	 * update
	 */
	private long refreshDelayPeriodMs; /*
	 * Time in milliseconds between 2
	 * updates
	 */
	private int connectionRetries;

	private Timer refreshTimer;
	private Listener keyListener;
	private Listener mouseListener;

	private double zoomFactor;

	protected ISWTPainter painter;

	/**
	 * This variable is used control concurrency between two consecutive restart
	 * requests. When a restart is successfully done it's value is incremented.
	 * The connectionSerialNumber has to be volatile so any change in it's value
	 * will be automatically refreshed in other threads. This is necessary to
	 * avoid that two consecutive restarts happen for the same reason.
	 */
	volatile private int connectionSerialNumber = 0;
	private IPropertiesFileHandler propertiesFileHandler;

	/*
	 * /** @param parent the Composite to be used as the GUI components parent.
	 * @param protocol the IProtoClient object to be used.
	 */
	/*
	 * public SWTRemoteDisplay(Composite parent, IProtoClient protocol){
	 * 
	 * super(parent, SWT.BACKGROUND); this.setLayout( parent.getLayout());
	 * 
	 * canvas = new Canvas (this, SWT.BACKGROUND);
	 * 
	 * protoClient = protocol; painter = (ISWTPainter) protoClient.getPainter(); }
	 */

	/**
	 * @param parent
	 *            the Composite to be used as the GUI components parent.
	 */
	public SWTRemoteDisplay(Composite parent, Properties configProperties,
			IPropertiesFileHandler propertiesFileHandler) {

		super(parent, SWT.BACKGROUND);

		configurationProperties = configProperties;
		this.propertiesFileHandler = propertiesFileHandler;

		this.setLayout(parent.getLayout());

		//canvas = new Canvas(this, getCanvasStyle());
		canvas = new Canvas(this, SWT.BACKGROUND);
		eventTranslator = new SWTVNCEventTranslator(configProperties,
				propertiesFileHandler);

		initConfiguration();

	}

	protected int getCanvasStyle() {
		return SWT.BACKGROUND;  
	}
	
	private void initConfiguration() {

		connectionRetries = Integer.valueOf(
				(configurationProperties
						.getProperty(IVNCProperties.CONNECTION_RETRIES)))
				.intValue();

		retries = connectionRetries;
		zoomFactor = Double
				.valueOf(
						configurationProperties
								.getProperty(IVNCProperties.ZOOM_FACTOR))
				.doubleValue();

		firstRefreshDelayMs = Long.valueOf(
				configurationProperties
						.getProperty(IVNCProperties.FIRST_REFRESH_DELAY_MS))
				.longValue();
		refreshDelayPeriodMs = Long.valueOf(
				configurationProperties
						.getProperty(IVNCProperties.REFRESH_DELAY_PERIOD_MS))
				.longValue();

	}

	/**
	 * Adds a KeyListener.
	 */
	private void addKeyListener() {
		keyListener = new Listener() {
			public void handleEvent(Event event) {
				final Event ev = event;

				if (isActive()) {

					try {
						keyEvent(ev);
						updateRequest(true);
					} catch (Exception e) {
						log(SWTRemoteDisplay.class).error(
								"Remote Display error on key event.");
					}
				}
			}

		};

		getCanvas().getDisplay().asyncExec(new Runnable() {
			public void run() {
				canvas.addListener(SWT.KeyDown, keyListener);
				canvas.addListener(SWT.KeyUp, keyListener);
			}
		});
	}

	/**
	 * Adds a MouseListener.
	 */
	private void addMouseListener() {
		mouseListener = new Listener() {
			public void handleEvent(Event event) {
				final Event ev = event;

				if (isActive()) {

					try {
						mouseEvent(ev);
					} 
					catch (Exception e) {
						log(SWTRemoteDisplay.class).error(
								"Remote Display error on key event.");
					}
				}
			}
		};

		getCanvas().getDisplay().asyncExec(new Runnable() {
			public void run() {
				canvas.addListener(SWT.MouseMove, mouseListener);
				canvas.addListener(SWT.MouseUp, mouseListener);
				canvas.addListener(SWT.MouseDown, mouseListener);

			}
		});
	}

	/**
	 * Adds a timer that schedules the screen's update in a fixed period.
	 */
	private void addRefreshTimer() {

		refreshTimer = new Timer();
		final Display display = this.getDisplay();
		final SWTRemoteDisplay swtDisplay = this;

        display.syncExec(new Runnable() {
            public void run() {
                try {
                    // The first request must not be incremental
                    updateRequest(false);
                } catch (Exception e) {
                    // If the first request fails, ignore it.
                }
            }
        });
		
		refreshTimer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				display.syncExec(new Runnable() {
					public void run() {
						try {
							updateRequest(true);
						} catch (Exception e) {
						    setRunning(false);
						    refreshTimer.cancel();
							log(SWTRemoteDisplay.class).error(
									"Update screen error: " + e.getMessage());
						}
						if (!swtDisplay.isActive() || canvas.isDisposed()) {
						    setRunning(false);
                            refreshTimer.cancel();
						}
					}
				});

			}
		}, firstRefreshDelayMs, refreshDelayPeriodMs);

	}

	synchronized private void decreaseRetries() {
		retries--;
	}

	synchronized private int getRetries() {
		return retries;
	}

	synchronized private void setRetries(int newValue) {
		retries = newValue;
	}

	synchronized public void restart() throws Exception {

		int currentId = this.connectionSerialNumber;
		restart(currentId);
	}

	/**
	 * @param connectionId
	 *            the Id for the current connection.
	 */
	synchronized private void restart(int connectionId) throws Exception {

		if (connectionId == connectionSerialNumber) {

			if (getRetries() > 0) {
				decreaseRetries();

				try {
					PluginProtocolActionDelegate.stopProtocol(protoClient);
				} catch (Exception e) {
					log(SWTRemoteDisplay.class).error(
							"Remote Display stop error : " + e.getMessage());
				}

				stop();

				PluginProtocolActionDelegate.restartProtocol(protoClient);
				start(protoClient);
			} else {
				throw new Exception(
						"Number of connection retries exceeded the limit of "
								+ connectionRetries + ".");
			}
		}

	}

	// synchronized public void start(String host, int port, IProtoClient
	// protocol) throws Exception{
	synchronized public void start(IProtocolImplementer protocol)
			throws Exception {

		if (protocol instanceof VNCProtocol) {
			try {

				protoClient = (VNCProtocol) protocol;

				protoClient.setPaintEnabled(true);
				protoClient.setVncPainter((IVNCPainter)painter);

				((Painter)painter).setPixelFormat(protoClient.getPixelFormat());
				((IVNCPainter)painter).setSize(protoClient.getWidth(), protoClient.getHeight());
				
				addRefreshTimer();
				addKeyListener();
				addMouseListener();

				connectionSerialNumber++;

			} catch (Exception e) {
				log(SWTRemoteDisplay.class).error(
						"Remote Display start error: " + e.getMessage());

				int currentConnection = connectionSerialNumber;
				restart(currentConnection);
			}

			setRetries(connectionRetries);
			setRunning(true);
		}
	}

	synchronized public void stop() {

		setRunning(false);

		refreshTimer.cancel();

		canvas.removeListener(SWT.KeyUp, keyListener);
		canvas.removeListener(SWT.KeyDown, keyListener);
		canvas.removeListener(SWT.MouseMove, mouseListener);
		canvas.removeListener(SWT.MouseUp, mouseListener);
		canvas.removeListener(SWT.MouseDown, mouseListener);

		keyListener = null;

		// repaints the background to the default color
		if (canvas != null) {
			Point p = canvas.getSize();
			GC gc = new GC(canvas);
			gc.fillRectangle(0, 0, p.x, p.y);
			gc.dispose();
		}

	}

	protected void updateRequest(boolean incremental) throws Exception {

		try {
			// Creates a update request message, setting the request
			// area to be the whole screen, and defining the incremental
			// field equal to the provided parameter
			ProtocolMessage message = new ProtocolMessage(3);
			message.setFieldValue("x-position", 0);
			message.setFieldValue("y-position", 0);
			message.setFieldValue("width", painter.getWidth());
			message.setFieldValue("height", painter.getHeight());
			message.setFieldValue("incremental", incremental ? 1 : 0);

			PluginProtocolActionDelegate.sendMessageToServer(protoClient,
					message);
		} catch (Exception e) {
			log(SWTRemoteDisplay.class).error(
					"Remote Display update screen error : " + e.getMessage());

			int currentConnection = connectionSerialNumber;
			restart(currentConnection);
		}

		if (screen != null) {
			screen.dispose();
		}

	}

	public void updateScreen() throws Exception {
		updateRequest(true);
	}

	public void keyEvent(Event event) throws Exception {

		try {

			ProtocolMessage message = eventTranslator.getKeyEventMessage(event);
			PluginProtocolActionDelegate.sendMessageToServer(protoClient, message);

		} catch (Exception e) {
			log(SWTRemoteDisplay.class).error(
					"Remote Display key event error : " + e.getMessage());
			setRunning(false);

			int currentConnection = connectionSerialNumber;
			restart(currentConnection);
		}
	}

	public void mouseEvent(Event event) throws Exception {

		try {
			ProtocolMessage message = eventTranslator.getMouseEventMessage(event);
			PluginProtocolActionDelegate.sendMessageToServer(protoClient, message);

		} catch (Exception e) {
			log(SWTRemoteDisplay.class).error(
					"Remote Display mouse event error : " + e.getMessage());
			setRunning(false);

			int currentConnection = connectionSerialNumber;
			restart(currentConnection);
		}
	}

	/**
	 * Gets the Canvas used to show the screen.
	 * 
	 * @return the Canvas object.
	 */
	public Canvas getCanvas() {
		return canvas;
	}

	/**
	 * Sets the Canvas object.
	 * 
	 * @param canvas
	 *            the Canvas object.
	 */
	public void setCanvas(Canvas canvas) {
		this.canvas = canvas;
	}

	/**
	 * Gets the Image object that represents the screen.
	 */
	public Image getScreen() {
		return screen;
	}

	/**
	 * Sets the Image screen.
	 * 
	 * @param screen
	 *            the Image object that represents the screen.
	 */
	public void setScreen(Image screen) {
		this.screen = screen;
	}

	/**
	 * Returns true if the component is running.
	 */
	synchronized public boolean isActive() {
		return active;
	}

	synchronized private void setRunning(boolean running) {
		this.active = running;
	}

	public void dispose() {

		try {
			if (protoClient != null) {
				// protoClient.stopProtocol();
				protoClient.setPaintEnabled(false); // There are no UI components
			}
			// paint the images for now on.
		} catch (Exception e) {
			log(SWTRemoteDisplay.class).error(
					"Remote Display Stop error: " + e.getMessage());
		}

		if (screen != null) {
			screen.dispose();
		}
		if (canvas != null) {
			canvas.dispose();
		}

		super.dispose();

	}

	public int getScreenWidth() {
		return painter.getWidth();
	}

	public int getScreenHeight() {
		return painter.getHeight();
	}

	/**
	 * Returns the RefreshTimer.
	 */
	public Timer getRefreshTimer() {
		return refreshTimer;
	}

	public double getZoomFactor() {
		return zoomFactor;
	}

	public void setZoomFactor(double zoomFactor) {
		this.zoomFactor = zoomFactor;
	}

	public void setBackground(Color color) {
		super.setBackground(color);
		if (canvas != null) {
			canvas.setBackground(color);
		}
	}

	public IProtocolImplementer getProtocol() {

		return protoClient;
	}

	public void setPropertiesFileHandler(
			IPropertiesFileHandler propertiesFileHandler) {
		this.propertiesFileHandler = propertiesFileHandler;
	}

}
