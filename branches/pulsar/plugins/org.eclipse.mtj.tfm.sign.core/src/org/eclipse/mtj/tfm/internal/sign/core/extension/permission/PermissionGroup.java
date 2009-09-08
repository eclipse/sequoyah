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

import java.util.EnumSet;
import java.util.List;

import org.eclipse.mtj.tfm.sign.core.enumerations.Platform;
import org.eclipse.mtj.tfm.sign.core.extension.permission.IPermission;
import org.eclipse.mtj.tfm.sign.core.extension.permission.IPermissionGroup;

/**
 * @author Diego Sandin
 * @since 1.0
 */
public class PermissionGroup implements IPermissionGroup {

    /**
     * Creates a new instance of PermissionGroup.
     */
    public PermissionGroup() {
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.permission.IPermissionGroup#getClassList()
     */
    public List<String> getClassList() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.permission.IPermissionGroup#getClassListByPermission(org.eclipse.mtj.tfm.sign.core.extension.permission.IPermission)
     */
    public List<String> getClassListByPermission(IPermission permission) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.permission.IPermissionGroup#getClassListSize()
     */
    public int getClassListSize() {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.permission.IPermissionGroup#getName()
     */
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.permission.IPermissionGroup#getPermissionByName(java.lang.String)
     */
    public IPermission getPermissionByName(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.permission.IPermissionGroup#getPermissionList()
     */
    public List<IPermission> getPermissionList() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.permission.IPermissionGroup#getPermissionListByClass(java.lang.String)
     */
    public List<IPermission> getPermissionListByClass(String className) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.permission.IPermissionGroup#getPermissionListSize()
     */
    public int getPermissionListSize() {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.permission.IPermissionGroup#getSupportedPlatforms()
     */
    public EnumSet<Platform> getSupportedPlatforms() {
        // TODO Auto-generated method stub
        return null;
    }

}
