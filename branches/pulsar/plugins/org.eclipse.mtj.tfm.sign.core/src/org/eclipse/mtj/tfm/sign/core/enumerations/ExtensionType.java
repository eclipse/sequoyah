/*******************************************************************************
 * Copyright (c) 2005 Nokia Corporation
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.eclipse.mtj.tfm.sign.core.enumerations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.AbstractEnumerator;

/**
 * <!-- begin-user-doc --> A representation of the literals of the enumeration '
 * <em><b>Extension Type</b></em>', and utility methods for working with them.
 * <!-- end-user-doc -->
 * 
 * @see org.eclipse.mtj.api.enumerations.EnumerationsPackage#getExtensionType()
 * @model
 */
public final class ExtensionType extends AbstractEnumerator {

    /**
     * The '<em><b>SECURITY MANAGEMENT</b></em>' literal value. <!--
     * begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>SECURITY MANAGEMENT</b></em>' literal object
     * isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @see #SECURITY_MANAGEMENT_LITERAL
     * @model
     * 
     * @ordered
     */
    public static final int SECURITY_MANAGEMENT = 1;

    /**
     * The '<em><b>SIGNING PROVIDER</b></em>' literal value. <!-- begin-user-doc
     * -->
     * <p>
     * If the meaning of '<em><b>SIGNING PROVIDER</b></em>' literal object isn't
     * clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @see #SIGNING_PROVIDER_LITERAL
     * @model
     * 
     * @ordered
     */
    public static final int SIGNING_PROVIDER = 2;

    /**
     * The '<em><b>SECURITY MANAGEMENT</b></em>' literal object. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #SECURITY_MANAGEMENT
     * 
     * @ordered
     */
    public static final ExtensionType SECURITY_MANAGEMENT_LITERAL = new ExtensionType(
            SECURITY_MANAGEMENT, "SECURITY_MANAGEMENT");

    /**
     * The '<em><b>SIGNING PROVIDER</b></em>' literal object. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #SIGNING_PROVIDER
     * 
     * @ordered
     */
    public static final ExtensionType SIGNING_PROVIDER_LITERAL = new ExtensionType(
            SIGNING_PROVIDER, "SIGNING_PROVIDER");

    /**
     * An array of all the '<em><b>Extension Type</b></em>' enumerators. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     */
    private static final ExtensionType[] VALUES_ARRAY = new ExtensionType[] {
            SECURITY_MANAGEMENT_LITERAL, SIGNING_PROVIDER_LITERAL, };

    /**
     * A public read-only list of all the '<em><b>Extension Type</b></em>'
     * enumerators. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     */
    public static final List VALUES = Collections.unmodifiableList(Arrays
            .asList(VALUES_ARRAY));

    /**
     * Returns the '<em><b>Extension Type</b></em>' literal with the specified
     * name. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     */
    public static ExtensionType get(String name) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            ExtensionType result = VALUES_ARRAY[i];
            if (result.toString().equals(name)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Extension Type</b></em>' literal with the specified
     * value. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     */
    public static ExtensionType get(int value) {
        switch (value) {
            case SECURITY_MANAGEMENT:
                return SECURITY_MANAGEMENT_LITERAL;
            case SIGNING_PROVIDER:
                return SIGNING_PROVIDER_LITERAL;
        }
        return null;
    }

    /**
     * Only this class can construct instances. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     */
    private ExtensionType(int value, String name) {
        super(value, name);
    }

}
