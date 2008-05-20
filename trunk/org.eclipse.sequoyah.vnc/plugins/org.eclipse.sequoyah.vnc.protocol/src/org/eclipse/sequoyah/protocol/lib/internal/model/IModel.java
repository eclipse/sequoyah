/********************************************************************************
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributor:
 * Fabio Rigo
 *
 * Contributors:
 * {Name} (company) - description of contribution.
 ********************************************************************************/
package org.eclipse.tml.protocol.lib.internal.model;

/**
 * DESCRIPTION: This interface describes the contract to be used by an object
 * that implements a protocol model. <br>
 * 
 * RESPONSIBILITY: Define methods that shall be implemented by objects that
 * represents a protocol model.<br>
 * 
 * COLABORATORS: None.<br>
 * 
 * USAGE: The interface is not intended to be implemented by users.<br>
 * 
 */
public interface IModel {

	/**
	 * Removes all data associated to stopped protocols from the model
	 */
	void cleanStoppedProtocols();
}
