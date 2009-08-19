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
package org.eclipse.mtj.tfm.sign.core.extension;

import org.eclipse.mtj.tfm.sign.core.enumerations.ExtensionType;

/**
 * 
 */
public interface ISignExtension {

    String getId();

    void setId(String value);

    String getVendor();

    void setVendor(String value);

    String getVersion();

    void setVersion(String value);

    String getDescription();

    void setDescription(String value);

    ExtensionType getType();

    void setType(ExtensionType value);

    boolean isActive();

    void setActive(boolean value);

}
