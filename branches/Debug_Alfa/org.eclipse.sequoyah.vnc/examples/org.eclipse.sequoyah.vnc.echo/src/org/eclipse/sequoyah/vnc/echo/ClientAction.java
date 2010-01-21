/********************************************************************************
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributor:
 * Fabio Rigo
 *
 * Contributors:
 * Daniel Barboza Franco (Motorola) - Bug [233775] - Does not have a way to enter the session password for the vnc connection
 * Daniel Barboza Franco (Motorola) - Bug [233062] - Protocol connection port is static.
 * Fabio Fantato (Eldorado Research Institute - Bug [243305] - The plugin org.eclipse.sequoyah.vnc.echo has compilation errors about exception handling mechanism
 * Fabio Rigo (Eldorado Research Institute) - [246212] - Enhance encapsulation of protocol implementer
 * Fabio Rigo (Eldorado Research Institute) - Bug [262632] - Avoid providing raw streams to the user in the protocol framework 
 ********************************************************************************/
package org.eclipse.sequoyah.vnc.echo;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.sequoyah.vnc.protocol.PluginProtocolActionDelegate;
import org.eclipse.sequoyah.vnc.protocol.lib.ProtocolHandle;
import org.eclipse.sequoyah.vnc.protocol.lib.ProtocolMessage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class ClientAction implements IWorkbenchWindowActionDelegate {

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub

	}

	public void run(IAction action) {

		try {
			
			Map map = new HashMap(); 
			
			ProtocolHandle handle = PluginProtocolActionDelegate
					.requestStartProtocolAsClient("echoProtocol", null, //$NON-NLS-1$
							"127.0.0.1", 10000, map); //$NON-NLS-1$

			while (!PluginProtocolActionDelegate.isProtocolRunning(handle)) {
			    Thread.sleep(1000);
			}
			
			ProtocolMessage message = new ProtocolMessage(0x600);
			message.setFieldValue("textToEcho", //$NON-NLS-1$
					"I want to see this message printed twice!"); //$NON-NLS-1$
			PluginProtocolActionDelegate.sendMessageToServer(handle, message);

			Thread.sleep(2000);

			PluginProtocolActionDelegate.requestStopProtocol(handle);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
	}
}
