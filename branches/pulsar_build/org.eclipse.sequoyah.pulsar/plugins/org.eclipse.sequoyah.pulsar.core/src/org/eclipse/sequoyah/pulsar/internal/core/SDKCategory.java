/**
 * Copyright (c) 2009 Motorola
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Euclides Neto (Motorola) - Initial implementation.
 */

package org.eclipse.sequoyah.pulsar.internal.core;

import java.net.URI;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.sequoyah.pulsar.internal.provisional.core.IInstallationEnvironment;
import org.eclipse.sequoyah.pulsar.internal.provisional.core.IInstallationInfo;
import org.eclipse.sequoyah.pulsar.internal.provisional.core.ISDKCategory;

public class SDKCategory implements ISDKCategory {

    private String name;
    private String description;
    private IInstallationInfo installationInfo;
    private IInstallationInfo parentInstallationInfo;

    public SDKCategory(String name, String description,
            IInstallationInfo parentInstallationInfo) {
        this.name = name;
        this.description = description;
        this.parentInstallationInfo = parentInstallationInfo;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object obj) {
        return obj.equals(this.name);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    /*
     * (non-Javadoc)
     * @see
     * org.eclipse.sequoyah.pulsar.internal.provisional.core.IInstallationInfoProvider
     * #getInstallationInfo()
     */
    public IInstallationInfo getInstallationInfo() {
        if (this.installationInfo == null) {
            if (parentInstallationInfo != null) {
                // Create an InstallationInfo based on parent's one
                this.installationInfo = new SDKCategoryInfo(
                        parentInstallationInfo.getWebSiteURI(),
                        parentInstallationInfo.getImageDescriptor(),
                        new StringBuffer(this.description));
            }
        }
        return this.installationInfo;
    }

    private class SDKCategoryInfo implements IInstallationInfo {

        private URI site;
        private ImageDescriptor image;
        private StringBuffer description;

        public SDKCategoryInfo(URI site, ImageDescriptor image,
                StringBuffer description) {
            this.site = site;
            this.image = image;
            this.description = description;
        }

        public ImageDescriptor getImageDescriptor() {
            return this.image;
        }

        public StringBuffer getDescription() {
            return this.description;
        }

        public URI getWebSiteURI() {
            return this.site;
        }

        public IInstallationEnvironment getTargetEnvironment() {
            return null;
        }
    }

}
