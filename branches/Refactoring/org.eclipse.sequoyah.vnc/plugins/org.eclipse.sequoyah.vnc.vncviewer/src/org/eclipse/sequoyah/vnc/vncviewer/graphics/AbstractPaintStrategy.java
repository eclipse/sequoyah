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
package org.eclipse.sequoyah.vnc.vncviewer.graphics;


public abstract class AbstractPaintStrategy implements IPaintStrategy {

	private IPainterContext context;

	public AbstractPaintStrategy(IPainterContext context) {
		setContext(context);
	}

	public IPainterContext getContext() {
		return context;
	}

	public void setContext(IPainterContext context) {
		this.context = context;
	}

}
