/*******************************************************************************
 * Copyright (c) 2005 Nokia Corporation
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.eclipse.mtj.tfm.sign.core.extension;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.mtj.tfm.sign.core.enumerations.ExtensionType;

/**
 * @model
 */
public interface ISignExtension extends EObject {
    /**
     * Returns the value of the '<em><b>Id</b></em>' attribute. <!--
     * begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Id</em>' attribute isn't clear, there really
     * should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Id</em>' attribute.
     * @see #setId(String)
     * @see org.eclipse.mtj.api.extension.ExtensionPackage#getMtjExtension_Id()
     * @model
     */
    String getId();

    /**
     * Sets the value of the '
     * {@link org.eclipse.mtj.ISignExtension.SignExtension.MtjExtension#getId <em>Id</em>}'
     * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Id</em>' attribute.
     * @see #getId()
     */
    void setId(String value);

    /**
     * Returns the value of the '<em><b>Vendor</b></em>' attribute. <!--
     * begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Vendor</em>' attribute isn't clear, there
     * really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Vendor</em>' attribute.
     * @see #setVendor(String)
     * @see org.eclipse.mtj.api.extension.ExtensionPackage#getMtjExtension_Vendor()
     * @model
     */
    String getVendor();

    /**
     * Sets the value of the '
     * {@link org.eclipse.mtj.ISignExtension.SignExtension.MtjExtension#getVendor
     * <em>Vendor</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @param value the new value of the '<em>Vendor</em>' attribute.
     * @see #getVendor()
     */
    void setVendor(String value);

    /**
     * Returns the value of the '<em><b>Version</b></em>' attribute. <!--
     * begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Version</em>' attribute isn't clear, there
     * really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Version</em>' attribute.
     * @see #setVersion(String)
     * @see org.eclipse.mtj.api.extension.ExtensionPackage#getMtjExtension_Version()
     * @model
     */
    String getVersion();

    /**
     * Sets the value of the '
     * {@link org.eclipse.mtj.ISignExtension.SignExtension.MtjExtension#getVersion
     * <em>Version</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @param value the new value of the '<em>Version</em>' attribute.
     * @see #getVersion()
     */
    void setVersion(String value);

    /**
     * Returns the value of the '<em><b>Description</b></em>' attribute. <!--
     * begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Description</em>' attribute isn't clear, there
     * really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Description</em>' attribute.
     * @see #setDescription(String)
     * @see org.eclipse.mtj.api.extension.ExtensionPackage#getMtjExtension_Description()
     * @model
     */
    String getDescription();

    /**
     * Sets the value of the '
     * {@link org.eclipse.mtj.ISignExtension.SignExtension.MtjExtension#getDescription
     * <em>Description</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @param value the new value of the '<em>Description</em>' attribute.
     * @see #getDescription()
     */
    void setDescription(String value);

    /**
     * Returns the value of the '<em><b>Type</b></em>' attribute. The literals
     * are from the enumeration
     * {@link org.eclipse.mtj.api.enumerations.ExtensionType}. <!--
     * begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Type</em>' attribute isn't clear, there really
     * should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Type</em>' attribute.
     * @see org.eclipse.mtj.api.enumerations.ExtensionType
     * @see #setType(ExtensionType)
     * @see org.eclipse.mtj.api.extension.ExtensionPackage#getMtjExtension_Type()
     * @model
     */
    ExtensionType getType();

    /**
     * Sets the value of the '
     * {@link org.eclipse.mtj.ISignExtension.SignExtension.MtjExtension#getType
     * <em>Type</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Type</em>' attribute.
     * @see org.eclipse.mtj.api.enumerations.ExtensionType
     * @see #getType()
     */
    void setType(ExtensionType value);

    /**
     * Returns the value of the '<em><b>Active</b></em>' attribute. <!--
     * begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Active</em>' attribute isn't clear, there
     * really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Active</em>' attribute.
     * @see #setActive(boolean)
     * @see org.eclipse.mtj.api.extension.ExtensionPackage#getMtjExtension_Active()
     * @model
     */
    boolean isActive();

    /**
     * Sets the value of the '
     * {@link org.eclipse.mtj.ISignExtension.SignExtension.MtjExtension#isActive
     * <em>Active</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @param value the new value of the '<em>Active</em>' attribute.
     * @see #isActive()
     */
    void setActive(boolean value);

}
