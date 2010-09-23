/**
 * Copyright (c) 2009 Motorola.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     David Marques (Motorola) - Initial version
 */
package org.eclipse.sequoyah.pulsar.internal.provisional.core;

/**
 * IInstallationEnvironment interface defines methods to retrieve
 * target installation environment information for any installable
 * class type.
 * 
 * @author David Marques
 */
public interface IInstallationEnvironment {
	
	public static final String ALL = "all";
	
	/**
	 * Gets the target installation environment
	 * operating system architecture.
	 * 
	 * @return target architecture or <b>OS_ALL</b>
	 * if no target architecture is specified.
	 */
	public String getTargetOSArch();
	
	/**
	 * Gets the target installation environment
	 * operating system.
	 * 
	 * @return target OS or <b>OS_ALL</b>
	 * if no target operating system is specified.
	 */
	public String getTargetOS();
	
	/**
	 * Gets the target installation environment
	 * window system.
	 * 
	 * @return target window system or <b>OS_ALL</b>
	 * if no target window system is specified.
	 */
	public String getTargetWS();
	
}
