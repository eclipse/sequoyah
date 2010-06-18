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
package org.eclipse.sequoyah.pulsar.internal.core.action.execution;

import java.io.IOException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.sequoyah.pulsar.core.Activator;

/**
 * 
 */
public class MacDmgExecutionHandler extends ExecutionHandler {

    /**
     * @param executable
     */
    public MacDmgExecutionHandler(String executable) {
        super(executable);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.sequoyah.pulsar.internal.core.action.execution.ExecutionHandler#execute()
     */
    @Override
    protected IStatus execute() {
        IStatus status = new Status(IStatus.OK, Activator.PLUGIN_ID, "");

        ProcessBuilder processBuilder = new ProcessBuilder("open", executable);
        Process process;
        try {
            process = processBuilder.start();
            process.waitFor();
        } catch (IOException e) {
            status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
                    "An I/O error has occurred", e);
        } catch (InterruptedException e) {
            status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
                    "The execution process was interrupted", e);
        }

        return status;
    }

}
