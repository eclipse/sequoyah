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

package org.eclipse.sequoyah.vnc.vncviewer.graphics.swt;

import org.eclipse.sequoyah.vnc.vncviewer.network.IPainter;
import org.eclipse.swt.graphics.ImageData;


/**
 * Interface that defines the behavior of a SWT Painter.
 */
public interface ISWTPainter extends IPainter {

	/**
	 * Returns the ImageData used to render the display.
	 */
	public ImageData getImageData();

}

