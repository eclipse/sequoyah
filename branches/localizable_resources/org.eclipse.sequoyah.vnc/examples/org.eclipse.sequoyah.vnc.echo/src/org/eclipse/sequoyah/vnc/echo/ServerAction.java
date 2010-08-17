/********************************************************************************
 * Copyright (c) 2007-2010 Motorola Inc.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributor:
 * Fabio Rigo
 *
 * Contributors:
 * Fabio Fantato (Eldorado Research Institute - Bug [243305] - The plugin org.eclipse.sequoyah.vnc.echo has compilation errors about exception handling mechanism
 * Fabio Rigo (Eldorado Research Institute) - [246212] - Enhance encapsulation of protocol implementer
 * Fabio Rigo (Eldorado Research Institute) - Bug [262632] - Avoid providing raw streams to the user in the protocol framework
 * Daniel Pastore (Eldorado) - [289870] Moving and renaming Tml to Sequoyah
 ********************************************************************************/
package org.eclipse.sequoyah.vnc.echo;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.sequoyah.vnc.protocol.PluginProtocolActionDelegate;
import org.eclipse.sequoyah.vnc.protocol.lib.exceptions.ProtocolException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class ServerAction implements IWorkbenchWindowActionDelegate {

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub

	}

	public void run(IAction action) {

		try {
			PluginProtocolActionDelegate.requestStartProtocolAsServer("echoProtocol", //$NON-NLS-1$
					10000, null);
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}

}
