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
package org.eclipse.mtj.tfm.sign.test.core;

import junit.framework.TestCase;

import org.eclipse.mtj.tfm.internal.sign.core.extension.SignExtensionManager;
import org.eclipse.mtj.tfm.sign.core.extension.ISignExtensionManager;

/**
 * @author Diego Sandin
 * @since 1.0
 * 
 */
public class SignExtensionManagerTest extends TestCase {

    /**
     * Test method for
     * {@link org.eclipse.mtj.tfm.internal.sign.core.extension.SignExtensionManager#getInstance()}
     * .
     */
    public final void testGetInstance() {
        
        ISignExtensionManager extensionManager1 = SignExtensionManager
                .getInstance();

        assertNotNull(
                "[Error 1] No Instance was returned by SignExtensionManager#getInstance()",
                extensionManager1);

        ISignExtensionManager extensionManager2 = SignExtensionManager
                .getInstance();

        assertNotNull(
                "[Error 2] No Instance was returned by SignExtensionManager#getInstance() second time",
                extensionManager2);

        assertEquals(
                "[Error 3] Instances returned by SignExtensionManager#getInstance() are not equals",
                extensionManager1, extensionManager2);

    }

    /**
     * Test method for
     * {@link org.eclipse.mtj.tfm.internal.sign.core.extension.SignExtensionManager#capitalizeIdentifier(java.lang.String)}
     * .
     */
    public final void testCapitalizeIdentifier() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link org.eclipse.mtj.tfm.internal.sign.core.extension.SignExtensionManager#getAllImplementations()}
     * .
     */
    public final void testGetAllImplementations() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link org.eclipse.mtj.tfm.internal.sign.core.extension.SignExtensionManager#getAllImplementations(java.lang.String)}
     * .
     */
    public final void testGetAllImplementationsString() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link org.eclipse.mtj.tfm.internal.sign.core.extension.SignExtensionManager#getImplementations(org.eclipse.mtj.tfm.sign.core.enumerations.ExtensionType, java.lang.String, java.lang.String)}
     * .
     */
    public final void testGetImplementationsExtensionTypeStringString() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link org.eclipse.mtj.tfm.internal.sign.core.extension.SignExtensionManager#getImplementations(org.eclipse.mtj.tfm.sign.core.enumerations.ExtensionType, java.lang.String, java.lang.String, boolean)}
     * .
     */
    public final void testGetImplementationsExtensionTypeStringStringBoolean() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link org.eclipse.mtj.tfm.internal.sign.core.extension.SignExtensionManager#getImplementations(org.eclipse.mtj.tfm.sign.core.enumerations.ExtensionType, java.lang.String, java.lang.String, java.lang.String)}
     * .
     */
    public final void testGetImplementationsExtensionTypeStringStringString() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link org.eclipse.mtj.tfm.internal.sign.core.extension.SignExtensionManager#getImplementations(org.eclipse.mtj.tfm.sign.core.enumerations.ExtensionType, java.lang.String, java.lang.String, java.lang.String, boolean)}
     * .
     */
    public final void testGetImplementationsExtensionTypeStringStringStringBoolean() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link org.eclipse.mtj.tfm.internal.sign.core.extension.SignExtensionManager#isActive(java.lang.String, org.eclipse.mtj.tfm.sign.core.enumerations.ExtensionType)}
     * .
     */
    public final void testIsActiveStringExtensionType() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link org.eclipse.mtj.tfm.internal.sign.core.extension.SignExtensionManager#isActive(java.lang.String, org.eclipse.mtj.tfm.sign.core.enumerations.ExtensionType, java.lang.String)}
     * .
     */
    public final void testIsActiveStringExtensionTypeString() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link org.eclipse.mtj.tfm.internal.sign.core.extension.SignExtensionManager#loadExtensions(java.lang.String)}
     * .
     */
    public final void testLoadExtensionsString() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link org.eclipse.mtj.tfm.internal.sign.core.extension.SignExtensionManager#loadExtensions(java.lang.String, org.eclipse.mtj.tfm.sign.core.enumerations.ExtensionType)}
     * .
     */
    public final void testLoadExtensionsStringExtensionType() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link org.eclipse.mtj.tfm.internal.sign.core.extension.SignExtensionManager#setActive(java.lang.String, org.eclipse.mtj.tfm.sign.core.enumerations.ExtensionType, boolean)}
     * .
     */
    public final void testSetActiveStringExtensionTypeBoolean() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link org.eclipse.mtj.tfm.internal.sign.core.extension.SignExtensionManager#setActive(java.lang.String, org.eclipse.mtj.tfm.sign.core.enumerations.ExtensionType, java.lang.String, boolean)}
     * .
     */
    public final void testSetActiveStringExtensionTypeStringBoolean() {
        fail("Not yet implemented");
    }

}
