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

import java.util.List;

import org.eclipse.sequoyah.tfm.sign.core.enumerations.Platform;
import org.eclipse.sequoyah.tfm.sign.core.extension.IExtension;

/**
 * @author Diego Sandin
 * @since 1.0
 */
public interface IPermissionGroupProvider extends IExtension {

    /**
     * @param name
     * @return
     */
    public abstract IPermissionGroup getPermissionGroupByName(String name);

    /**
     * @param platform
     * @return
     */
    public abstract List<IPermissionGroup> getPermissionGroupList();

    /**
     * @param platform
     * @return
     */
    public abstract List<IPermissionGroup> getPermissionGroupListByPlatform(
            Platform platform);

    /**
     * @param platform
     * @return
     */
    public abstract int getPermissionGroupListSize();
}
