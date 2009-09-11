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
package org.eclipse.mtj.tfm.sign.test.core.extension.permission;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.mtj.tfm.internal.sign.core.extension.permission.PermissionGroupProvider;
import org.eclipse.mtj.tfm.sign.core.SignCore;
import org.eclipse.mtj.tfm.sign.core.exception.SignException;

/**
 * @author Diego Sandin
 * @since 1.0
 */
public class PermissionGroupProviderTest extends TestCase {

    private IExtension[] extensions;

    /**
     * Creates a new instance of PermissionGroupProviderTest.
     * 
     * @param name
     */
    public PermissionGroupProviderTest(String name) {
        super(name);
        IExtensionRegistry r = Platform.getExtensionRegistry();
        IExtensionPoint ps = r.getExtensionPoint(SignCore.PLUGIN_ID,
                "securitypermission");
        extensions = ps.getExtensions();
    }

    /**
     * Test method for
     * {@link org.eclipse.mtj.tfm.internal.sign.core.extension.permission.PermissionGroupProvider#PermissionGroupProvider()}
     * .
     */
    public final void testPermissionGroupProvider() {
        try {
            PermissionGroupProvider provider = new PermissionGroupProvider(
                    extensions[0]);
            
            provider.toString();
        } catch (SignException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test method for
     * {@link org.eclipse.mtj.tfm.internal.sign.core.extension.permission.PermissionGroupProvider#PermissionGroupProvider(org.eclipse.core.runtime.IExtensionPoint)}
     * .
     */
    public final void testPermissionGroupProviderIExtensionPoint() {
        fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for
     * {@link org.eclipse.mtj.tfm.internal.sign.core.extension.permission.PermissionGroupProvider#PermissionGroupProvider(java.lang.String, java.lang.String, org.osgi.framework.Version, java.lang.String)}
     * .
     */
    public final void testPermissionGroupProviderStringStringVersionString() {
        fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for
     * {@link org.eclipse.mtj.tfm.internal.sign.core.extension.permission.PermissionGroupProvider#getPermissionGroupByName(java.lang.String)}
     * .
     */
    public final void testGetPermissionGroupByName() {
        fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for
     * {@link org.eclipse.mtj.tfm.internal.sign.core.extension.permission.PermissionGroupProvider#getPermissionGroupList()}
     * .
     */
    public final void testGetPermissionGroupList() {
        fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for
     * {@link org.eclipse.mtj.tfm.internal.sign.core.extension.permission.PermissionGroupProvider#getPermissionGroupListByPlatform(org.eclipse.mtj.tfm.sign.core.enumerations.Platform)}
     * .
     */
    public final void testGetPermissionGroupListByPlatform() {
        fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for
     * {@link org.eclipse.mtj.tfm.internal.sign.core.extension.permission.PermissionGroupProvider#getPermissionGroupListSize()}
     * .
     */
    public final void testGetPermissionGroupListSize() {
        fail("Not yet implemented"); // TODO
    }

}
