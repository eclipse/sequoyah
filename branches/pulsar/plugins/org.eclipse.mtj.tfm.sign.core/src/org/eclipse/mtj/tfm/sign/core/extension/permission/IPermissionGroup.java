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

import java.util.EnumSet;
import java.util.List;

import org.eclipse.mtj.tfm.sign.core.enumerations.Platform;

/**
 * @author Diego Sandin
 * @since 1.0
 */
public interface IPermissionGroup {

    /**
     * @return
     */
    public abstract List<String> getClassList();

    /**
     * @param permission
     * @return
     */
    public abstract List<String> getClassListByPermission(IPermission permission);

    /**
     * @return
     */
    public abstract int getClassListSize();

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
     * @return
     */
    public abstract List<IPermission> getPermissionList();

    /**
     * @param className
     * @return
     */
    public abstract List<IPermission> getPermissionListByClass(String className);

    /**
     * @return
     */
    public abstract int getPermissionListSize();

    /**
     * @return
     */
    public abstract EnumSet<Platform> getSupportedPlatforms();
}
