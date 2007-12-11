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

/**
 * This class is used to represent VNC specific key event information.
 */
public class VNCMouseEvent{
	

	public static int MOUSE_MOVE = 0;
	public static int MOUSE_UP = 1;
	public static int MOUSE_DOWN = 2;
	
	
	private int button;
	private int type;
	
	private int x;
	private int y;
	
	private boolean buttonPressed;
	
	
	/**
	 * @param button the button that is pressed/released if so.
	 * @param type the event type.
	 */
	public VNCMouseEvent(int button, int type, int x, int y, boolean buttonPressed){

		this.button = button;
		this.type = type;
		
		this.x = x;
		this.y = y;
		
		this.buttonPressed = buttonPressed;
		
	}

	public int getButton() {
		return button;
	}

	public int getType() {
		return type;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public boolean isButtonPressed() {
		return buttonPressed;
	}
	
	
	
	

}
