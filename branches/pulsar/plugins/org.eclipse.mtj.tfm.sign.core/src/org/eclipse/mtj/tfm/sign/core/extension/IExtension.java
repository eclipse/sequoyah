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
import org.osgi.framework.Version;

/**
 * 
 */
public interface IExtension {

    /**
     * @return
     */
    String getId();

    /**
     * @param value
     */
    void setId(String value);

    /**
     * @return
     */
    String getVendor();

    /**
     * @param value
     */
    void setVendor(String value);

    /**
     * @return
     */
    Version getVersion();

    /**
     * @param value
     */
    void setVersion(Version value);

    /**
     * @return
     */
    String getDescription();

    /**
     * @param value
     */
    void setDescription(String value);

    /**
     * @return
     */
    ExtensionType getType();

    /**
     * @param value
     */
    void setType(ExtensionType value);

    /**
     * @return
     */
    boolean isActive();

    /**
     * @param value
     */
    void setActive(boolean value);

}
