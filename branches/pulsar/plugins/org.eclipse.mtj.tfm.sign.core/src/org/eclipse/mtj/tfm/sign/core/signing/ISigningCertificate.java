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
package org.eclipse.mtj.tfm.sign.core.signing;


/**
 * @since 1.0
 */
public interface ISigningCertificate {

    String getKeystore();

    void setKeystore(String value);

    String getAliasName();

    void setAliasName(String value);

    String getKeystorePassword();

    void setKeystorePassword(String value);

    String getCertificatePassword();

    void setCertificatePassword(String value);

}
