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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.mtj.tfm.sign.core.enumerations.Platform;
import org.eclipse.mtj.tfm.sign.core.exception.SignException;
import org.eclipse.mtj.tfm.sign.core.extension.ExtensionImpl;
import org.eclipse.mtj.tfm.sign.core.extension.permission.IPermission;
import org.eclipse.mtj.tfm.sign.core.extension.permission.IPermissionGroup;
import org.eclipse.mtj.tfm.sign.core.extension.permission.IPermissionGroupProvider;
import org.osgi.framework.Version;

/**
 * @author Diego Sandin
 * @since 1.0
 */
public class PermissionGroupProvider extends ExtensionImpl implements
        IPermissionGroupProvider {

    HashMap<Platform, ArrayList<IPermissionGroup>> permGroupPlatformMapping;

    /**
     * Creates a new instance of PermissionGroupProvider.
     * 
     * @param extension
     * @throws SignException
     */
    public PermissionGroupProvider(IExtension extension) throws SignException {
        if (extension != null) {
            if (extension.getExtensionPointUniqueIdentifier().equals(
                    "org.eclipse.mtj.tfm.sign.core.securitypermission")) {

                ArrayList<IPermissionGroup> androidPermissionGroups = new ArrayList<IPermissionGroup>();
                ArrayList<IPermissionGroup> javamePermissionGroups = new ArrayList<IPermissionGroup>();

                IConfigurationElement[] c = extension
                        .getConfigurationElements();
                if ((c != null) && (c.length == 1)) {
                    IConfigurationElement element = c[0];
                    if (!element.getName().equals("provider")) {

                        String att = element.getAttribute("id");
                        if (att != null) {
                            setId(att);
                        } else {
                            throw new SignException(
                                    "The Abstract Provider has no id defined");
                        }

                        att = element.getAttribute("name");
                        if (att != null) {
                            setDescription(att);
                        } else {
                            throw new SignException(
                                    "The Abstract Provider has no name defined");
                        }

                        att = element.getAttribute("version");
                        if (att != null) {

                            try {
                                setVersion(new Version(att));
                            } catch (IllegalArgumentException e) {
                                throw new SignException(
                                        "The Abstract Provider has invalid version");
                            }
                        } else {
                            throw new SignException(
                                    "The Abstract Provider has no version defined");
                        }

                        att = element.getAttribute("vendor");
                        if (att != null) {
                            setVendor(att);
                        } else {
                            throw new SignException(
                                    "The Abstract Provider has no vendor defined");
                        }

                        IConfigurationElement[] groups = element.getChildren();

                        if ((groups != null) && (groups.length > 0)) {
                            for (IConfigurationElement group : groups) {
                                String platName = group
                                        .getAttribute("platform");
                                if (platName == null) {
                                    throw new SignException("Missing Platform");
                                }

                                String name = group.getAttribute("name");
                                if (name == null) {
                                    throw new SignException(
                                            "Missing Group Name");
                                }
                                Platform plat;
                                try {
                                    plat = Enum.valueOf(Platform.class,
                                            platName);
                                } catch (Exception e) {
                                    throw new SignException("Invalid Platform");
                                }

                                IConfigurationElement[] permElements = group
                                        .getChildren();
                                if ((permElements != null)
                                        && (permElements.length > 0)) {

                                    ArrayList<IPermissionGroup> permissionGroupList = plat
                                            .equals(Platform.ANDROID) ? androidPermissionGroups
                                            : javamePermissionGroups;

                                    PermissionGroup permissionGroup = new PermissionGroup(
                                            name);
                                    permissionGroup.setPlatform(plat);

                                    TreeSet<IPermission> permSet = new TreeSet<IPermission>();

                                    for (IConfigurationElement permElement : permElements) {
                                        if (permElement.getName().equals(
                                                "permission")) {
                                            String perm = permElement
                                                    .getAttribute("name");
                                            if (perm != null) {
                                                permSet
                                                        .add(new Permission(
                                                                perm));
                                            }
                                        } else if (permElement.getName()
                                                .equals("class")) {
                                            String className = permElement
                                                    .getAttribute("name");
                                            IConfigurationElement[] classPermElements = permElement
                                                    .getChildren();
                                            if ((classPermElements != null)
                                                    && (classPermElements.length > 0)) {
                                                TreeSet<IPermission> permSet2 = new TreeSet<IPermission>();
                                                for (IConfigurationElement classPermElement : classPermElements) {
                                                    String perm = classPermElement
                                                            .getAttribute("name");
                                                    if (perm != null) {
                                                        permSet2
                                                                .add(new Permission(
                                                                        perm));
                                                    }
                                                }
                                                if (!permSet2.isEmpty()) {
                                                    permissionGroup
                                                            .addClassPermissionSetMapping(
                                                                    className,
                                                                    permSet2);
                                                }

                                            }
                                        }
                                    }
                                    if (!permSet.isEmpty()) {
                                        permissionGroup
                                                .addPermissionSet(permSet);
                                    }

                                    permissionGroupList.add(permissionGroup);

                                }

                            }

                        } else {
                            throw new SignException(
                                    "No Permission Group Defined");
                        }
                    } else {
                        throw new SignException(
                                "An Implemtation of IPermissionGroupProvider was already defined");
                    }
                }

                permGroupPlatformMapping = new HashMap<Platform, ArrayList<IPermissionGroup>>();
                if (!androidPermissionGroups.isEmpty()) {
                    permGroupPlatformMapping.put(Platform.ANDROID,
                            androidPermissionGroups);
                }
                if (!javamePermissionGroups.isEmpty()) {
                    permGroupPlatformMapping.put(Platform.JAVAME,
                            javamePermissionGroups);
                }
            } else {
                throw new SignException(
                        "Not an implementation of securitypermission Extension Point");
            }
        } else {
            throw new SignException("No Extension Point provided");
        }
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

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "PermissionGroupProvider [permGroupPlatformMapping="
                + permGroupPlatformMapping.toString() + "]";
    }

}
