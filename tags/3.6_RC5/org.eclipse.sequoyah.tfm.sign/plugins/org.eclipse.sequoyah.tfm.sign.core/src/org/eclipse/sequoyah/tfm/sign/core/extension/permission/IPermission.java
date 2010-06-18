/**
 * Copyright (c) 2009 Motorola.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Diego Sandin (Motorola) - Initial Version
 */
package org.eclipse.sequoyah.tfm.sign.core.extension.permission;

/**
 * This Class wraps a named permission defined by an API or function to prevent
 * it from being used without authorization.
 * 
 * @author Diego Sandin
 * @since 1.0
 */
public interface IPermission extends Comparable<IPermission> {

    /**
     * Get the permission name. The names of permissions have a hierarchical
     * organization similar to Java package names and are case sensitive.
     * 
     * @return the permission name.
     */
    public abstract String getName();
}
