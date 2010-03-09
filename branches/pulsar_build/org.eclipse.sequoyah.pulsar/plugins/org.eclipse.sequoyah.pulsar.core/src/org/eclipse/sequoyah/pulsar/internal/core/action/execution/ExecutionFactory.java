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

/**
 * 
 */
public class ExecutionFactory {

    /**
     * @param osName
     * @param executable
     * @return
     */
    public static ExecutionHandler getExecutionHandler(final String osName,
            final String executable) {

        String lcOsName = osName.toLowerCase();
        if (lcOsName.startsWith("mac")) {
            if (executable.endsWith(".dmg")) {
                return new MacDmgExecutionHandler(executable);
            }
        } else if (lcOsName.startsWith("linux")) {
            return new LinuxBinaryExecutionHandler(executable);
        }
        return new ExecutionHandler(executable);
    }

}
