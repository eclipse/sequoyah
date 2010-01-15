/********************************************************************************
 * Copyright (c) 2007 Motorola Inc. All rights reserved.
 * This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributor:
 * Fabio Fantato (Motorola)
 *
 * Contributors:
 * {Name} (company) - description of contribution.
 ********************************************************************************/


package org.eclipse.tml.common.utilities.exception;

import org.eclipse.core.runtime.CoreException;


@SuppressWarnings("serial")
public class TmLException extends CoreException
{
	
    public TmLException(AbstractExceptionStatus status)
    {
        super(status.getStatus());        
    }
    
    public TmLException()
    {
        super(ExceptionStatus.ERROR_STATUS_DEFAULT);        
    }    
    
}

