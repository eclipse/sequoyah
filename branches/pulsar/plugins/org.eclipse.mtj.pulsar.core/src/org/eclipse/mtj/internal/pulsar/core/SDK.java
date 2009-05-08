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
 *
 */

package org.eclipse.mtj.internal.pulsar.core;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.equinox.internal.provisional.p2.metadata.IInstallableUnit;
import org.eclipse.mtj.internal.provisional.pulsar.core.ISDK;
import org.eclipse.mtj.pulsar.core.Activator;
import org.osgi.framework.Version;

public class SDK extends PlatformObject implements ISDK {

	public static final String PROP_TYPE = "org.eclipse.pulsar.type"; //$NON-NLS-1$
	public static final String ZIPARCHIVE_TYPE = "ziparchive"; //$NON-NLS-1$
	public static final String EXECUTABLE_TYPE = "executable"; //$NON-NLS-1$
	public static final String PROP_CATEGORY = "org.eclipse.pulsar.category.name"; //$NON-NLS-1$
	public static final String PROP_DOC_URL = "org.eclipse.pulsar.documentation.url"; //$NON-NLS-1$
	
	private IInstallableUnit iu;
	private SDKRepository sdkRepository;

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
		return org.eclipse.equinox.internal.provisional.p2.core.Version.toOSGiVersion(iu.getVersion());
	}

	public EType getType() {
		String value = iu.getProperty(PROP_TYPE);
		if (value.equals(ZIPARCHIVE_TYPE))
			return EType.ZIP_ARCHIVE;
		else if (value.equals(EXECUTABLE_TYPE))
			return EType.EXECUTABLE;
		
		return EType.UNKNOWN;
	}
	
	public String getCategory() {
		return iu.getProperty(PROP_CATEGORY);
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
}
