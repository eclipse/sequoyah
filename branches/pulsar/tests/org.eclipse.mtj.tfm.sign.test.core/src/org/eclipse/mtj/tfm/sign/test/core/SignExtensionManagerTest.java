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

import java.util.List;

import junit.framework.TestCase;

import org.eclipse.mtj.tfm.internal.sign.core.extension.SignExtensionManager;
import org.eclipse.mtj.tfm.sign.core.enumerations.ExtensionType;
import org.eclipse.mtj.tfm.sign.core.exception.SignException;
import org.eclipse.mtj.tfm.sign.core.extension.IExtension;
import org.eclipse.mtj.tfm.sign.core.extension.IExtensionManager;
import org.osgi.framework.Version;

/**
 * @author Diego Sandin
 * @since 1.0
 */
public class SignExtensionManagerTest extends TestCase {

    /**
     * Test method for
     * {@link org.eclipse.mtj.tfm.internal.sign.core.extension.SignExtensionManager#getInstance()}
     * .
     */
    public final void testGetInstance() {

        IExtensionManager extensionManager1 = SignExtensionManager
                .getInstance();

        assertNotNull(
                "[Error 1] No Instance was returned by SignExtensionManager#getInstance()",
                extensionManager1);

        IExtensionManager extensionManager2 = SignExtensionManager
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
        IExtensionManager extensionManager = SignExtensionManager.getInstance();

        String cap1 = extensionManager
                .capitalizeIdentifier("Device_Management");
        assertEquals("deviceManagement", cap1);

        String cap2 = extensionManager
                .capitalizeIdentifier("DEVICE_MANAGEMENT");
        assertEquals("deviceManagement", cap2);
    }

    /**
     * Test method for
     * {@link org.eclipse.mtj.tfm.internal.sign.core.extension.SignExtensionManager#getAllImplementations()}
     * .
     */
    public final void testGetAllImplementations() {
        final int totalImpls = 3;
        IExtensionManager extensionManager = SignExtensionManager.getInstance();

        IExtension[] extension = extensionManager.getAllImplementations();

        assertEquals(totalImpls, extension.length);
    }

    /**
     * Test method for
     * {@link org.eclipse.mtj.tfm.internal.sign.core.extension.SignExtensionManager#getAllImplementations(java.lang.String)}
     * .
     */
    public final void testGetAllImplementationsString() {

        IExtensionManager extensionManager = SignExtensionManager.getInstance();

        try {
            extensionManager.setActive("org.eclipse.mtj.tfm.sign.smgmt.sun",
                    ExtensionType.SECURITY_MANAGEMENT, "testProject", true);
            extensionManager.setActive("org.eclipse.mtj.tfm.sign.smgmt.ibm",
                    ExtensionType.SECURITY_MANAGEMENT, "testProject2", true);
        } catch (SignException e) {
            e.printStackTrace();
        }
        IExtension[] extension = extensionManager
                .getAllImplementations("testProject");

        for (IExtension ex : extension) {
            if (ex.getId().equals("org.eclipse.mtj.tfm.sign.smgmt.sun")) {
                assertTrue(ex.isActive());
            } else {
                assertFalse(
                        "The \"org.eclipse.mtj.tfm.sign.smgmt.sun\" should be enabled for testProject",
                        ex.isActive());
            }
        }

        IExtension[] extension2 = extensionManager
                .getAllImplementations("testProject2");

        for (IExtension ex : extension2) {
            if (ex.getId().equals("org.eclipse.mtj.tfm.sign.smgmt.ibm")) {
                assertTrue(ex.isActive());
            } else {
                assertFalse(
                        "The \"org.eclipse.mtj.tfm.sign.smgmt.ibm\" should be enabled for testProject2",
                        ex.isActive());
            }
        }
    }

    /**
     * Test method for
     * {@link org.eclipse.mtj.tfm.internal.sign.core.extension.SignExtensionManager#getImplementations(org.eclipse.mtj.tfm.sign.core.enumerations.ExtensionType, java.lang.String, java.lang.String)}
     * .
     */
    public final void testGetImplementationsExtensionTypeStringString() {
        IExtensionManager extensionManager = SignExtensionManager.getInstance();

        List<IExtension> extension = extensionManager.getImplementations(
                ExtensionType.SECURITY_MANAGEMENT, Version.emptyVersion
                        .toString(), "Foo");

        assertTrue(extension.isEmpty());

        extension = extensionManager.getImplementations(
                ExtensionType.SECURITY_MANAGEMENT, "1.0.0", "Foo");

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
