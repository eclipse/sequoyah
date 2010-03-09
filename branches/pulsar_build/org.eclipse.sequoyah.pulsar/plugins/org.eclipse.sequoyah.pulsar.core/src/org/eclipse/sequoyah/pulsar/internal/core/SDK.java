/**
 * Copyright (c) 2009 Nokia Corporation and/or its subsidiary(-ies).
 * All rights reserved.
 * This component and the accompanying materials are made available
 * under the terms of the License "Eclipse Public License v1.0"
 * which accompanies this distribution, and is available
 * at the URL "http://www.eclipse.org/legal/epl-v10.html".
 *
 * Contributors:
 * 	David Dubrow
 *  David Marques (Motorola) - Extending IInstallationInfoProvider.
 *  Euclides Neto (Motorola) - Externalize strings.
 *  David Marques (Motorola) - Adding installation environment support.
 *  David Marques (Motorola) - Adding support for feature installation.
 *  Euclides Neto (Motorola) - Adding SDK Category description support.
 */

package org.eclipse.sequoyah.pulsar.internal.core;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.equinox.internal.provisional.p2.metadata.IInstallableUnit;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.sequoyah.pulsar.core.Activator;
import org.eclipse.sequoyah.pulsar.internal.provisional.core.IInstallationEnvironment;
import org.eclipse.sequoyah.pulsar.internal.provisional.core.IInstallationInfo;
import org.eclipse.sequoyah.pulsar.internal.provisional.core.ISDK;
import org.eclipse.sequoyah.pulsar.internal.provisional.core.ISDKCategory;
import org.eclipse.sequoyah.pulsar.internal.provisional.core.IUInstallationEnvironment;
import org.osgi.framework.Version;

@SuppressWarnings("restriction")
public class SDK extends PlatformObject implements ISDK {

    public static final String PROP_TYPE = "org.eclipse.pulsar.type"; //$NON-NLS-1$
    public static final String ZIPARCHIVE_TYPE = "ziparchive"; //$NON-NLS-1$
    public static final String EXECUTABLE_TYPE = "executable"; //$NON-NLS-1$
    public static final String OSGI_BUNDLE_TYPE = "osgi-bundle"; //$NON-NLS-1$
    public static final String PROP_CATEGORY = "org.eclipse.pulsar.category.name"; //$NON-NLS-1$
    public static final String PROP_CATEGORY_DESC = "org.eclipse.pulsar.category.description"; //$NON-NLS-1$
    public static final String PROP_DOC_URL = "org.eclipse.pulsar.documentation.url"; //$NON-NLS-1$
    public static final String PROP_DESCRIPTION = "org.eclipse.equinox.p2.description"; //$NON-NLS-1$

    private SDKRepository sdkRepository;
    private IInstallableUnit iu;
    private IInstallationInfo info;

    public SDK(SDKRepository sdkRepository, IInstallableUnit iu) {
        this.sdkRepository = sdkRepository;
        this.iu = iu;
    }

    public String getName() {
        return iu.getProperty(IInstallableUnit.PROP_NAME);
    }

    public EState getState() {
        return P2Utils.isInstalled(iu) ? EState.INSTALLED : EState.UNINSTALLED;
    }

    public Version getVersion() {
        return org.eclipse.equinox.internal.provisional.p2.core.Version
                .toOSGiVersion(iu.getVersion());
    }

    public EType getType() {
        String value = iu.getProperty(PROP_TYPE);
        if (value.equals(ZIPARCHIVE_TYPE))
            return EType.ZIP_ARCHIVE;
        else if (value.equals(EXECUTABLE_TYPE))
            return EType.EXECUTABLE;
        else if (value.equals(OSGI_BUNDLE_TYPE))
            return EType.OSGI_BUNDLE;
        return EType.UNKNOWN;
    }

    public ISDKCategory getCategory() {
        String name = iu.getProperty(PROP_CATEGORY);

        // If category name is null, ISDKCategory must be null as well
        if (name != null) {
            String description = iu.getProperty(PROP_CATEGORY_DESC);
            // If description is not set, try to get the description of
            // repository (keep compatibility with previous implementation.
            if (description == null) {
                if (getRepository() != null
                        && getRepository().getInstallationInfo() != null
                        && getRepository().getInstallationInfo()
                                .getDescription() != null) {
                    description = getRepository().getInstallationInfo()
                            .getDescription().toString();
                }
            }
            return new SDKCategory(name, description, this.getRepository()
                    .getInstallationInfo());
        } else {
            return null;
        }
    }

    public URL getDocumentationURL() {
        String value = iu.getProperty(PROP_DOC_URL);
        if (value != null) {
            try {
                return new URL(value);
            } catch (MalformedURLException e) {
                Activator.logError(Messages.SDK_DocumentationURLError, e);
            }
        }
        return null;
    }

    public IInstallableUnit getInstallableUnit() {
        return iu;
    }

    public SDKRepository getRepository() {
        return sdkRepository;
    }

    /* (non-Javadoc)
     * @see org.eclipse.sequoyah.pulsar.internal.provisional.core.IInstallationInfoProvider#getInstallationInfo()
     */
    public IInstallationInfo getInstallationInfo() {
        if (this.info == null) {
            this.info = new SDKInfo(this);
        }
        return this.info;
    }

    private class SDKInfo implements IInstallationInfo {

        private SDK sdk;

        public SDKInfo(SDK sdk) {
            if (sdk == null) {
                throw new IllegalArgumentException(
                        Messages.SDK_InvalidSDKInstanceError);
            }
            this.sdk = sdk;
        }

        public StringBuffer getDescription() {
            StringBuffer result = null;
            IInstallableUnit unit = sdk.getInstallableUnit();
            if (unit != null) {
                String description = unit.getProperty(PROP_DESCRIPTION);
                if (description != null) {
                    result = new StringBuffer(description);
                }
            }
            return result;
        }

        public ImageDescriptor getImageDescriptor() {
            ImageDescriptor result = null;
            IInstallationInfo info = this.sdk.getRepository()
                    .getInstallationInfo();
            if (info != null) {
                result = info.getImageDescriptor();
            }
            return result;
        }

        public URI getWebSiteURI() {
            URI result = null;
            try {
                URL url = sdk.getDocumentationURL();
                if (url != null) {
                    result = url.toURI();
                }
            } catch (URISyntaxException e) {
            }
            return result;
        }

        public IInstallationEnvironment getTargetEnvironment() {
            IInstallableUnit iu = sdk.getInstallableUnit();
            if (iu != null) {
                return new IUInstallationEnvironment(iu.getFilter());
            }
            return null;
        }
    }
}
