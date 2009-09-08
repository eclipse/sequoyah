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

    public abstract IPermission getPermissionByName(String name);

    public abstract List<IPermission> getPermissionListByClass(String className);

    public abstract List<IPermission> getPermissionList();

    public abstract int getPermissionListSize();

    public abstract EnumSet<Platform> getSupportedPlatforms();
}
