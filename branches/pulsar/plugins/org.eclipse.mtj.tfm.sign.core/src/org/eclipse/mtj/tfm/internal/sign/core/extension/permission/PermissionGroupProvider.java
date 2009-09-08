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
package org.eclipse.mtj.tfm.internal.sign.core.extension.permission;

import java.util.List;

import org.eclipse.mtj.tfm.sign.core.enumerations.Platform;
import org.eclipse.mtj.tfm.sign.core.extension.ExtensionImpl;
import org.eclipse.mtj.tfm.sign.core.extension.permission.IPermissionGroup;
import org.eclipse.mtj.tfm.sign.core.extension.permission.IPermissionGroupProvider;

/**
 * @author Diego Sandin
 * @since 1.0
 */
public class PermissionGroupProvider extends ExtensionImpl implements
        IPermissionGroupProvider {

    /**
     * Creates a new instance of PermissionGroupProvider.
     */
    public PermissionGroupProvider() {
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.permission.IPermissionGroupProvider#getPermissionGroupByName(java.lang.String)
     */
    public IPermissionGroup getPermissionGroupByName(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.permission.IPermissionGroupProvider#getPermissionGroupList()
     */
    public List<IPermissionGroup> getPermissionGroupList() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.permission.IPermissionGroupProvider#getPermissionGroupListByPlatform(org.eclipse.mtj.tfm.sign.core.extension.Platform)
     */
    public List<IPermissionGroup> getPermissionGroupListByPlatform(
            Platform platform) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.permission.IPermissionGroupProvider#getPermissionGroupListSize()
     */
    public int getPermissionGroupListSize() {
        // TODO Auto-generated method stub
        return 0;
    }
}
