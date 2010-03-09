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
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.sequoyah.pulsar.core.Activator;

/**
 * 
 */
public class ExecutionHandler {

    protected String executable;

    /**
     * @noreference This constructor is not intended to be referenced by
     *              clients.
     */
    public ExecutionHandler(String executable) {
        this.executable = executable;
    }

    /**
     * @return
     */
    public IStatus handleExecution() {

        MultiStatus multiStatus;

        IStatus preExecuteStatus;
        IStatus executeStatus;
        IStatus posExecuteStatus;

        preExecuteStatus = preExecute();
        executeStatus = execute();
        posExecuteStatus = posExecute();

        if (preExecuteStatus.isOK() && executeStatus.isOK()
                && posExecuteStatus.isOK()) {
            multiStatus = new MultiStatus(Activator.PLUGIN_ID, IStatus.OK,
                    new IStatus[] { preExecuteStatus, executeStatus,
                            posExecuteStatus },
                    "The process was executed correctly.", null);
        } else {
            multiStatus = new MultiStatus(Activator.PLUGIN_ID, IStatus.ERROR,
                    new IStatus[] { preExecuteStatus, executeStatus,
                            posExecuteStatus },
                    "Some errors were found durig the execution.", null);
        }

        return multiStatus;

    }

    /**
     * @return
     */
    protected IStatus execute() {
        IStatus status = new Status(IStatus.OK, Activator.PLUGIN_ID, "");

        ProcessBuilder processBuilder = new ProcessBuilder(executable);
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

    /**
     * @return
     */
    protected IStatus posExecute() {
        return new Status(IStatus.OK, Activator.PLUGIN_ID, "");
    }

    /**
     * @return
     */
    protected IStatus preExecute() {
        return new Status(IStatus.OK, Activator.PLUGIN_ID, "");
    }
}
