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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.mtj.tfm.sign.core.enumerations.ExtensionType;
import org.eclipse.mtj.tfm.sign.core.exception.SignException;

/**
 * @since 1.0
 */
public interface ISignExtensionManager {

    /**
     * CamelCase identifiers! Capitalize an identifier - i.e. Convert something
     * like "Device_Management" to "deviceManagement" or "DEVICE_MANAGEMENT" to
     * "deviceManagement"
     * 
     * @param value The identifier to capitalize - e.g. "DEVICE_MANAGEMENT".
     * @return The capitalized identifier - e.g. "deviceManagement".
     */
    public abstract String capitalizeIdentifier(String _value);

    /**
     * Method returns all existing Sign Extensions.
     * 
     * @return
     */
    public abstract ISignExtension[] getAllImplementations();

    /**
     * @param project
     * @return
     */
    public abstract ISignExtension[] getAllImplementations(String project);

    /**
     * Method returns all active Mtj Extensions that are the defined extension
     * type and is implemented by the vendor. Also vendor's specific version can
     * be defined. Vendor and version attributes could have also null values.
     * 
     * @param type
     * @param version
     * @param vendor
     * @return
     */
    public abstract List<ISignExtension> getImplementations(
            ExtensionType extensionType, String version, String vendor);

    /**
     * @param extensionType
     * @param version
     * @param vendor
     * @param onlyActive
     * @return
     */
    public abstract List<ISignExtension> getImplementations(
            ExtensionType extensionType, String version, String vendor,
            boolean onlyActive);

    /**
     * @param extensionType
     * @param version
     * @param vendor
     * @param project
     * @return
     */
    public abstract List<ISignExtension> getImplementations(
            ExtensionType extensionType, String version, String vendor,
            String project);

    /**
     * @param extensionType
     * @param version
     * @param vendor
     * @param project
     * @param onlyActive
     * @return
     */
    public abstract List<ISignExtension> getImplementations(
            ExtensionType extensionType, String version, String vendor,
            String project, boolean onlyActive);

    /**
     * Method is used to verify if the extension is active.
     * 
     * @param vendor
     * @param version
     * @param type
     * @return
     */
    public abstract boolean isActive(String id, ExtensionType type);

    /**
     * Method is used to verify if the extension is active.
     * 
     * @param vendor
     * @param version
     * @param type
     * @param project
     * @return
     */
    public abstract boolean isActive(String id, ExtensionType type,
            String project);

    /**
     * @param extensionName
     * @return
     */
    public abstract ArrayList<ISignExtension> loadExtensions(
            String extensionName);

    /**
     * @param plugin_id
     * @param extensionType
     * @return
     */
    public abstract ArrayList<ISignExtension> loadExtensions(String plugin_id,
            ExtensionType extensionType);

    /**
     * Method is used to set the extension's activity.
     * 
     * @param vendor
     * @param version
     * @param type
     * @param isActive
     */
    public abstract void setActive(String id, ExtensionType type,
            boolean isActive) throws SignException;

    /**
     * Method is used to set the extension's activity.
     * 
     * @param vendor
     * @param version
     * @param type
     * @param project
     * @param isActive
     */
    public abstract void setActive(String id, ExtensionType type,
            String project, boolean isActive) throws SignException;

}