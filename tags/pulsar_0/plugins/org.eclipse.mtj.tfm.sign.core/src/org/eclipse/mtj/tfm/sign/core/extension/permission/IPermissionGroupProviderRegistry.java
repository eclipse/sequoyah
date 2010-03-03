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
package org.eclipse.mtj.tfm.sign.core.extension.permission;

import java.util.TreeSet;

/**
 * @author Diego Sandin
 * @since 1.0
 */
public interface IPermissionGroupProviderRegistry {

    /**
     * @return
     */
    public abstract TreeSet<IPermissionGroupProvider> getPermissionGroupProviderSet();

    /**
     * @param id
     * @return
     */
    public abstract IPermissionGroupProvider getPermissionGroupProviderById(
            String id);

}
