/*******************************************************************************
 * Copyright (c) 2005 Nokia Corporation
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.eclipse.mtj.tfm.sign.core.signing;

import org.eclipse.emf.ecore.EObject;

/**
 * @see org.eclipse.mtj.api.signings.SigningsPackage#getSigningCertificate()
 * @model
 */
public interface ISigningCertificate extends EObject {

    /**
     * Returns the value of the '<em><b>Keystore</b></em>' attribute. <!--
     * begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Keystore</em>' attribute isn't clear, there
     * really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Keystore</em>' attribute.
     * @see #setKeystore(String)
     * @see org.eclipse.mtj.api.signings.SigningsPackage#getSigningCertificate_Keystore()
     * @model
     */
    String getKeystore();

    /**
     * Sets the value of the '
     * {@link org.eclipse.mtj.ISigningCertificate.signings.SigningCertificate#getKeystore
     * <em>Keystore</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @param value the new value of the '<em>Keystore</em>' attribute.
     * @see #getKeystore()
     */
    void setKeystore(String value);

    /**
     * Returns the value of the '<em><b>Alias Name</b></em>' attribute. <!--
     * begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Alias Name</em>' attribute isn't clear, there
     * really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Alias Name</em>' attribute.
     * @see #setAliasName(String)
     * @see org.eclipse.mtj.api.signings.SigningsPackage#getSigningCertificate_AliasName()
     * @model
     */
    String getAliasName();

    /**
     * Sets the value of the '
     * {@link org.eclipse.mtj.ISigningCertificate.signings.SigningCertificate#getAliasName
     * <em>Alias Name</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @param value the new value of the '<em>Alias Name</em>' attribute.
     * @see #getAliasName()
     */
    void setAliasName(String value);

    /**
     * Returns the value of the '<em><b>Keystore Password</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Keystore Password</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Keystore Password</em>' attribute.
     * @see #setKeystorePassword(String)
     * @see org.eclipse.mtj.api.signings.SigningsPackage#getSigningCertificate_KeystorePassword()
     * @model
     */
    String getKeystorePassword();

    /**
     * Sets the value of the '
     * {@link org.eclipse.mtj.ISigningCertificate.signings.SigningCertificate#getKeystorePassword
     * <em>Keystore Password</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @param value the new value of the '<em>Keystore Password</em>' attribute.
     * @see #getKeystorePassword()
     */
    void setKeystorePassword(String value);

    /**
     * Returns the value of the '<em><b>Certificate Password</b></em>'
     * attribute. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Certificate Password</em>' attribute isn't
     * clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Certificate Password</em>' attribute.
     * @see #setCertificatePassword(String)
     * @see org.eclipse.mtj.api.signings.SigningsPackage#getSigningCertificate_CertificatePassword()
     * @model
     */
    String getCertificatePassword();

    /**
     * Sets the value of the '
     * {@link org.eclipse.mtj.ISigningCertificate.signings.SigningCertificate#getCertificatePassword
     * <em>Certificate Password</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @param value the new value of the '<em>Certificate Password</em>'
     *            attribute.
     * @see #getCertificatePassword()
     */
    void setCertificatePassword(String value);

} // ISigningCertificate
