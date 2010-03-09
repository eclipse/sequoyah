/**
 * Copyright (c) 2005,2009 Nokia Corporation and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Nokia Corporation         - Initial Version
 *     Diego Sandin (Motorola)   - Porting code to TFM Sign Framework [Bug 286387]
 */
package org.eclipse.sequoyah.tfm.sign.core.signing;

/**
 * @since 1.0
 */
public interface ISigningCertificate {

    /**
     * @return
     */
    String getKeystore();

    /**
     * @param value
     */
    void setKeystore(String value);

    /**
     * @return
     */
    String getAliasName();

    /**
     * @param value
     */
    void setAliasName(String value);

    /**
     * @return
     */
    String getKeystorePassword();

    /**
     * @param value
     */
    void setKeystorePassword(String value);

    /**
     * @return
     */
    String getCertificatePassword();

    /**
     * @param value
     */
    void setCertificatePassword(String value);

}
