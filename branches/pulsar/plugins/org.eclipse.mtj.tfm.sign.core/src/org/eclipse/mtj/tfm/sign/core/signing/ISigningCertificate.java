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
 * @since 1.0
 */
public interface ISigningCertificate extends EObject {

    String getKeystore();

    void setKeystore(String value);

    String getAliasName();

    void setAliasName(String value);

    String getKeystorePassword();

    void setKeystorePassword(String value);

    String getCertificatePassword();

    void setCertificatePassword(String value);

}
