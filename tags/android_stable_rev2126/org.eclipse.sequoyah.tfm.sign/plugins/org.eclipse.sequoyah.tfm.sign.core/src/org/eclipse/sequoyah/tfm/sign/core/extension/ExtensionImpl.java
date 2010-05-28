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
package org.eclipse.sequoyah.tfm.sign.core.extension;

import org.eclipse.sequoyah.tfm.sign.core.enumerations.ExtensionType;
import org.osgi.framework.Version;

/**
 * @author Diego Sandin
 * @since 1.0
 */
public class ExtensionImpl implements IExtension {

    protected boolean active = false;
    protected String description = "Defaul Sign Extension Description";
    protected String id = "DefaulSignExtensionId";
    protected ExtensionType type = ExtensionType.SECURITY_MANAGEMENT;
    protected String vendor = "DSDP - Eclipse.org";
    protected Version version = Version.emptyVersion;

    /**
     * Creates a new instance of ExtensionImpl.
     */
    public ExtensionImpl() {

    }

    /**
     * Creates a new instance of ExtensionImpl.
     * @param id
     * @param vendor
     * @param version
     * @param description
     * @param type
     */
    public ExtensionImpl(String id, String vendor, Version version,
            String description, ExtensionType type) {
        this.id = id;
        this.vendor = vendor;
        this.version = version;
        this.description = description;
        this.type = type;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof ExtensionImpl))
            return false;
        ExtensionImpl other = (ExtensionImpl) obj;
        if (active != other.active)
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        if (vendor == null) {
            if (other.vendor != null)
                return false;
        } else if (!vendor.equals(other.vendor))
            return false;
        if (version == null) {
            if (other.version != null)
                return false;
        } else if (!version.equals(other.version))
            return false;
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.sequoyah.tfm.sign.core.extension.IExtension#getDescription()
     */
    public String getDescription() {
        return description;
    }



    /* (non-Javadoc)
     * @see org.eclipse.sequoyah.tfm.sign.core.extension.IExtension#getId()
     */
    public String getId() {
        return id;
    }



    /* (non-Javadoc)
     * @see org.eclipse.sequoyah.tfm.sign.core.extension.IExtension#getType()
     */
    public ExtensionType getType() {
        return type;
    }

    /* (non-Javadoc)
     * @see org.eclipse.sequoyah.tfm.sign.core.extension.IExtension#getVendor()
     */
    public String getVendor() {
        return vendor;
    }

    /* (non-Javadoc)
     * @see org.eclipse.sequoyah.tfm.sign.core.extension.IExtension#getVersion()
     */
    public Version getVersion() {
        return version;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (active ? 1231 : 1237);
        result = prime * result
                + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((vendor == null) ? 0 : vendor.hashCode());
        result = prime * result + ((version == null) ? 0 : version.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see org.eclipse.sequoyah.tfm.sign.core.extension.IExtension#isActive()
     */
    public boolean isActive() {
        return active;
    }

    /* (non-Javadoc)
     * @see org.eclipse.sequoyah.tfm.sign.core.extension.IExtension#setActive(boolean)
     */
    public void setActive(boolean value) {
        active = value;
    }

    /* (non-Javadoc)
     * @see org.eclipse.sequoyah.tfm.sign.core.extension.IExtension#setDescription(java.lang.String)
     */
    public void setDescription(String value) {
        description = value;
    }

    /* (non-Javadoc)
     * @see org.eclipse.sequoyah.tfm.sign.core.extension.IExtension#setId(java.lang.String)
     */
    public void setId(String value) {
        id = value;
    }

    /* (non-Javadoc)
     * @see org.eclipse.sequoyah.tfm.sign.core.extension.IExtension#setType(org.eclipse.sequoyah.tfm.sign.core.enumerations.ExtensionType)
     */
    public void setType(ExtensionType value) {
        type = value;
    }

    /* (non-Javadoc)
     * @see org.eclipse.sequoyah.tfm.sign.core.extension.IExtension#setVendor(java.lang.String)
     */
    public void setVendor(String value) {
        vendor = value;
    }

    /* (non-Javadoc)
     * @see org.eclipse.sequoyah.tfm.sign.core.extension.IExtension#setVersion(org.osgi.framework.Version)
     */
    public void setVersion(Version value) {
        version = value;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ExtensionImpl [active="
                + active
                + ", "
                + (description != null ? "description=" + description + ", "
                        : "") + (id != null ? "id=" + id + ", " : "")
                + (type != null ? "type=" + type + ", " : "")
                + (vendor != null ? "vendor=" + vendor + ", " : "")
                + (version != null ? "version=" + version : "") + "]";
    }

}
