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
package org.eclipse.mtj.tfm.sign.core.extension.permission;

import java.util.TreeSet;

import org.eclipse.mtj.tfm.sign.core.enumerations.Platform;

/**
 * @author Diego Sandin
 * @since 1.0
 */
public interface IPermissionGroup {

    /**
     * @return
     */
    public abstract TreeSet<String> getClassSet();

    /**
     * @param permission
     * @return
     */
    public abstract TreeSet<String> getClassSetByPermission(IPermission permission);

    /**
     * @return
     */
    public abstract int getClassSetSize();

    /**
     * @return
     */
    public abstract String getName();

    /**
     * @param name
     * @return
     */
    public abstract IPermission getPermissionByName(String name);

    /**
     * @return an unmodifiable view of the permission list.
     */
    public abstract TreeSet<IPermission> getPermissionSet();

    /**
     * @param className
     * @return
     */
    public abstract TreeSet<IPermission> getPermissionSetByClass(String className);

    /**
     * @return
     */
    public abstract int getPermissionSetSize();

    /**
     * @return
     */
    public abstract Platform getSupportedPlatform();
}
