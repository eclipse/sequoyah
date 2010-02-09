/********************************************************************************
 * Copyright (c) 2009-2010 Motorola Inc.
 * All rights reserved. This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Vinicius Hernandes (Motorola)
 * 
 * Contributors:
 * Daniel Pastore (Eldorado) - [289870] Moving and renaming Tml to Sequoyah 
 ********************************************************************************/
package org.eclipse.sequoyah.localization.tools.persistence;

import java.util.List;

import org.eclipse.core.resources.IFile;

/**
 *
 */
public interface IFilePersistentData extends IPersistentData {

	/**
	 * @return
	 */
	public abstract IFile getFile();

	/**
	 * @return
	 */
	public abstract List<IPersistentData> getPersistentData();
}
