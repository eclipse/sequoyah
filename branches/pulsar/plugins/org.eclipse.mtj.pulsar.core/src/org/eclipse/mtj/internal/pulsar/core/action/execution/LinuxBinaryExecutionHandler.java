/**
 * Copyright (c) 2009 Motorola
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Diego Madruga (Motorola) - Initial implementation
 */
package org.eclipse.mtj.internal.pulsar.core.action.execution;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.equinox.internal.p2.touchpoint.natives.actions.ActionConstants;
import org.eclipse.equinox.internal.p2.touchpoint.natives.actions.ChmodAction;

/**
 * 
 */
@SuppressWarnings("restriction")
public class LinuxBinaryExecutionHandler extends ExecutionHandler {

    /* (non-Javadoc)
     * @see org.eclipse.mtj.internal.pulsar.core.action.execution.ExecutionHandler#preExecute()
     */
    @Override
    public IStatus preExecute() {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(ActionConstants.PARM_TARGET_DIR, executable.substring(0,
                executable.lastIndexOf('/')));
        parameters.put(ActionConstants.PARM_TARGET_FILE, executable.substring(
                executable.lastIndexOf('/') + 1, executable.length()));
        parameters.put(ActionConstants.PARM_PERMISSIONS, "777");

        ChmodAction ca = new ChmodAction();

        return ca.execute(parameters);
    }

    /**
     * @param executable
     */
    public LinuxBinaryExecutionHandler(String executable) {
        super(executable);
    }

}
