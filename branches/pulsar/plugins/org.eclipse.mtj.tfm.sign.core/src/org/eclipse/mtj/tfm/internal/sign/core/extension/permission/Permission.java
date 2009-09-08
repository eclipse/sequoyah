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
package org.eclipse.mtj.tfm.internal.sign.core.extension.permission;

import org.eclipse.mtj.tfm.sign.core.extension.permission.IPermission;
import org.eclipse.ui.IPersistableElement;

/**
 * Default implementation of {@link IPersistableElement} for TFM.
 * 
 * @author Diego Sandin
 * @since 1.0
 */
public class Permission implements IPermission {

    /**
     * A named permission defined by an API or function to prevent it from being
     * used without authorization
     */
    private String name;

    /**
     * Creates a new instance of Permission.
     */
    public Permission() {
        name = ""; //$NON-NLS-1$
    }

    /**
     * Creates a new instance of Permission.
     * 
     * @param name
     */
    public Permission(String name) {
        this.name = name;
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(IPermission o) {
        return o.getName().compareTo(getName());
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Permission)) {
            return false;
        }
        Permission other = (Permission) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.permission.IPermission#getName()
     */
    public String getName() {
        return name;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Permission [name=" + name + "]";
    }

}
