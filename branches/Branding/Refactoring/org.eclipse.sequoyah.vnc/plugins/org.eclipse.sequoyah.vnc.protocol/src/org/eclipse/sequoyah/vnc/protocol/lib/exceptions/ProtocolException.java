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
package org.eclipse.sequoyah.vnc.protocol.lib.exceptions;

@SuppressWarnings("serial")
public class ProtocolException extends Exception 
{
    /**
     * Constructor with no arguments
     */
    public ProtocolException()
    {
        super();
    }
	
    /**
     * Constructor with a string argument: the message to be added to the
     * exception.
     *
     * @param message The message to be added to exception
     */
	public ProtocolException(String message)
	{
		super(message);
	}
	
    /**
     * Constructor with a throwable argument: a throwable object to be added to
     * the exception
     *
     * @param cause The throwable that caused the exception to happen
     */
    public ProtocolException(Throwable cause)
    {
        super(cause);
    }

    /**
     * Constructor with a string argument and a throwable argument: the string
     * refers to the message to be added to the exception and the throwable
     * object is the object to be added to the exception
     *
     * @param message The message to be added to exception
     * @param cause The throwable that caused the exception to happen
     */
    public ProtocolException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
