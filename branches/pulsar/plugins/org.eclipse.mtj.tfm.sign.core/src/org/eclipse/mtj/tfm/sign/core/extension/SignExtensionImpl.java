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
package org.eclipse.mtj.tfm.sign.core.extension;

import org.eclipse.mtj.tfm.sign.core.enumerations.ExtensionType;
import org.osgi.framework.Version;

/**
 * @author Diego Sandin
 * @since 1.0
 */
public class SignExtensionImpl implements ISignExtension {

    protected String id = "DefaulSignExtensionId";
    protected String vendor = "DSDP - Eclipse.org";
    protected Version version = Version.emptyVersion;
    protected String description = "Defaul Sign Extension Description";
    protected ExtensionType type = ExtensionType.SECURITY_MANAGEMENT;
    protected boolean active = false;

    /**
     * Creates a new instance of SignExtensionImpl.
     */
    public SignExtensionImpl() {

    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.ISignExtension#getDescription()
     */
    public String getDescription() {
        return description;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.ISignExtension#getId()
     */
    public String getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.ISignExtension#getType()
     */
    public ExtensionType getType() {
        return type;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.ISignExtension#getVendor()
     */
    public String getVendor() {
        return vendor;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.ISignExtension#getVersion()
     */
    public Version getVersion() {
        return version;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.ISignExtension#isActive()
     */
    public boolean isActive() {
        return active;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.ISignExtension#setActive(boolean)
     */
    public void setActive(boolean value) {
        active = value;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.ISignExtension#setDescription(java.lang.String)
     */
    public void setDescription(String value) {
        description = value;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.ISignExtension#setId(java.lang.String)
     */
    public void setId(String value) {
        id = value;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.ISignExtension#setType(org.eclipse.mtj.tfm.sign.core.enumerations.ExtensionType)
     */
    public void setType(ExtensionType value) {
        type = value;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.ISignExtension#setVendor(java.lang.String)
     */
    public void setVendor(String value) {
        vendor = value;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.ISignExtension#setVersion(org.osgi.framework.Version)
     */
    public void setVersion(Version value) {
        version = value;
    }

}
