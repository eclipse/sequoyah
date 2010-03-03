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
import java.util.Collections;
import java.util.TreeSet;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.mtj.tfm.sign.core.SignCore;
import org.eclipse.mtj.tfm.sign.core.exception.SignException;
import org.eclipse.mtj.tfm.sign.core.extension.permission.IPermissionGroupProvider;
import org.eclipse.mtj.tfm.sign.core.extension.permission.IPermissionGroupProviderRegistry;

/**
 * @author Diego Sandin
 * @since 1.0
 */
public class PermissionGroupProviderRegistry implements
        IPermissionGroupProviderRegistry {

    private static PermissionGroupProviderRegistry instance = null;

    /**
     * @return
     */
    public static PermissionGroupProviderRegistry getInstance() {
        if (instance == null) {
            instance = new PermissionGroupProviderRegistry();
        }
        return instance;
    }

    ArrayList<IPermissionGroupProvider> permissionGroupProviders;

    TreeSet<IPermissionGroupProvider> providers = null;

    /**
     * Creates a new instance of PermissionGroupProviderRegistry.
     */
    private PermissionGroupProviderRegistry() {
        initializeRegistry();
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.permission.IPermissionGroupProviderRegistry#getPermissionGroupProviderById(java.lang.String)
     */
    public IPermissionGroupProvider getPermissionGroupProviderById(String id) {
        if (id != null) {
            for (IPermissionGroupProvider provider : providers) {
                if (provider.getId().equals(id)) {
                    return provider;
                }
            }
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.permission.IPermissionGroupProviderRegistry#getPermissionGroupProviderSet()
     */
    public TreeSet<IPermissionGroupProvider> getPermissionGroupProviderSet() {
        return (TreeSet<IPermissionGroupProvider>) Collections
                .unmodifiableSet(providers);
    }

    /**
     * 
     */
    private void initializeRegistry() {
        IExtensionRegistry r = Platform.getExtensionRegistry();
        IExtensionPoint ps = r.getExtensionPoint(SignCore.PLUGIN_ID,
                "securitypermission");
        IExtension[] extensions = ps.getExtensions();

        providers = new TreeSet<IPermissionGroupProvider>();

        for (IExtension extension : extensions) {
            IConfigurationElement[] c = extension.getConfigurationElements();
            if ((c != null) && (c.length == 1)) {
                IConfigurationElement element = c[0];
                if (element.getName().equals("provider")) {
                    try {
                        providers.add((IPermissionGroupProvider) element
                                .createExecutableExtension("class"));
                    } catch (CoreException e) {
                        e.printStackTrace();
                    } catch (ClassCastException cce) {
                        cce.printStackTrace();
                    }
                } else {
                    try {
                        providers.add(new PermissionGroupProvider(extension));
                    } catch (SignException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
