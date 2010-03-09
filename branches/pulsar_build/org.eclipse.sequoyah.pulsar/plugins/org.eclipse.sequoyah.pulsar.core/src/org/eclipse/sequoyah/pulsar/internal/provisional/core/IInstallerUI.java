/**
 * Copyright (c) 2009 Nokia Corporation and/or its subsidiary(-ies).
 * All rights reserved.
 * This component and the accompanying materials are made available
 * under the terms of the License "Eclipse Public License v1.0"
 * which accompanies this distribution, and is available
 * at the URL "http://www.eclipse.org/legal/epl-v10.html".
 *
 * Contributors:
 * 	David Dubrow
 *
 */

package org.eclipse.sequoyah.pulsar.internal.provisional.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Shell;

/**
 * An interface encapsulating any UI required to install an ISDK
 */
public interface IInstallerUI {

	/**
	 * Runs installer user interface
	 * 
	 * @param parentShell Shell
	 * @param sdk ISDK
	 * 
	 * @throws CoreException
	 */
	public void runInstaller(Shell parentShell, ISDK sdk) throws CoreException;
	
	/**
	 * Runs uninstaller user interface
	 * 
	 * @param parentShell Shell
	 * @param sdk ISDK
	 * 
	 * @throws CoreException
	 */
	public void runUninstaller(Shell parentShell, ISDK sdk) throws CoreException;
	
}
