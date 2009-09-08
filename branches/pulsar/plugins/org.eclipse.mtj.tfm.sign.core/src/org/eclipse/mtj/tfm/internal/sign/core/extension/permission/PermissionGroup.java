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

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.mtj.tfm.sign.core.enumerations.Platform;
import org.eclipse.mtj.tfm.sign.core.extension.permission.IPermission;
import org.eclipse.mtj.tfm.sign.core.extension.permission.IPermissionGroup;

/**
 * @author Diego Sandin
 * @since 1.0
 */
public class PermissionGroup implements IPermissionGroup {

    /**
     * Mapped Class x Permission
     */
    private HashMap<String, TreeSet<IPermission>> mappedClassPerm = new HashMap<String, TreeSet<IPermission>>();

    /**
     * The name of the permission group
     */
    private String name;

    /**
     * The supported platforms.
     */
    private EnumSet<Platform> platforms;

    /**
     * List of permissions not mapped to any Class.
     */
    private TreeSet<IPermission> unmappedPerm = new TreeSet<IPermission>();

    /**
     * Creates a new instance of PermissionGroup.
     */
    public PermissionGroup() {
    }

    /**
     * Creates a new instance of PermissionGroup.
     * 
     * @param name
     */
    public PermissionGroup(String name) {
        this.name = name;
    }

    /**
     * Maps the specified permission list with the specified Class name. If the
     * mapping already exists, the old value is replaced.
     * 
     * @param className Class name with which the specified permission list is
     *            to be associated.
     * @param permissions permission list to be associated with the specified
     *            Class name.
     */
    public void addClassPermissionSetMapping(String className,
            TreeSet<IPermission> permissions) {
        mappedClassPerm.put(className, permissions);
        unmappedPerm.addAll(permissions);
    }

    /**
     * Adds all of the permissions in the Permission Set.
     * 
     * @param permissions elements to be added
     */
    public void addPermissionSet(TreeSet<IPermission> permissions) {
        unmappedPerm.addAll(permissions);
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.permission.IPermissionGroup#getClassList()
     */
    public TreeSet<String> getClassSet() {
        TreeSet<String> classes = null;

        if (!mappedClassPerm.isEmpty()) {
            Set<String> classSet = mappedClassPerm.keySet();
            classes = new TreeSet<String>();
            for (String string : classSet) {
                classes.add(string);
            }
        }
        return classes;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.permission.IPermissionGroup#getClassListByPermission(org.eclipse.mtj.tfm.sign.core.extension.permission.IPermission)
     */
    public TreeSet<String> getClassSetByPermission(IPermission permission) {
        TreeSet<String> classes = null;

        if (!mappedClassPerm.isEmpty()) {
            Set<String> classSet = mappedClassPerm.keySet();
            classes = new TreeSet<String>();
            for (String className : classSet) {
                TreeSet<IPermission> permSet = mappedClassPerm.get(className);
                if ((permSet != null) && (!permSet.isEmpty())
                        && permSet.contains(permission)) {
                    classes.add(className);
                }
            }
        }
        return (TreeSet<String>) Collections.unmodifiableSet(classes);
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.permission.IPermissionGroup#getClassListSize()
     */
    public int getClassSetSize() {
        return mappedClassPerm.keySet().size();
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.permission.IPermissionGroup#getName()
     */
    public String getName() {
        return name;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.permission.IPermissionGroup#getPermissionByName(java.lang.String)
     */
    public IPermission getPermissionByName(String name) {
        Permission perm = new Permission(name);
        if (unmappedPerm.contains(perm)) {
            return perm;
        } else {
            return perm;
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.permission.IPermissionGroup#getPermissionList()
     */
    public TreeSet<IPermission> getPermissionSet() {
        return (TreeSet<IPermission>) Collections.unmodifiableSet(unmappedPerm);
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.permission.IPermissionGroup#getPermissionListByClass(java.lang.String)
     */
    public TreeSet<IPermission> getPermissionSetByClass(String className) {
        return mappedClassPerm.get(className);
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.permission.IPermissionGroup#getPermissionListSize()
     */
    public int getPermissionSetSize() {
        return unmappedPerm.size();
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.permission.IPermissionGroup#getSupportedPlatforms()
     */
    public EnumSet<Platform> getSupportedPlatforms() {
        return platforms;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param platforms the platforms to set
     */
    public void setPlatforms(EnumSet<Platform> platforms) {
        this.platforms = platforms;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "PermissionGroup [mappedClassPerm=" + mappedClassPerm.toString()
                + ", name=" + name + ", platforms=" + platforms.toString()
                + ", unmappedPerm=" + unmappedPerm.toString() + "]";
    }

}
