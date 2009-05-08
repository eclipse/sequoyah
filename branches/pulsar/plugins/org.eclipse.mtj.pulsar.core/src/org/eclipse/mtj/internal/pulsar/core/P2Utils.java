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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.eclipse.core.runtime.IPath;
import org.eclipse.equinox.internal.p2.console.ProvisioningHelper;
import org.eclipse.equinox.internal.p2.core.helpers.ServiceHelper;
import org.eclipse.equinox.internal.provisional.p2.core.ProvisionException;
import org.eclipse.equinox.internal.provisional.p2.engine.IProfile;
import org.eclipse.equinox.internal.provisional.p2.engine.IProfileRegistry;
import org.eclipse.equinox.internal.provisional.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.internal.provisional.p2.metadata.query.InstallableUnitQuery;
import org.eclipse.equinox.internal.provisional.p2.query.Collector;
import org.eclipse.mtj.internal.provisional.pulsar.core.ISDK;
import org.eclipse.mtj.pulsar.core.Activator;

/**
 * A class of utilities for P2 common operations
 */
public class P2Utils {

	public static final String PROP_PULSAR_PROFILE = "org.eclipse.pulsar.profile"; //$NON-NLS-1$

	public static boolean isInstalled(IInstallableUnit iu) {
		for (IProfile profile : ProvisioningHelper.getProfiles()) {
			Collector collector = new Collector();
			profile.available(new InstallableUnitQuery(iu.getId(), iu.getVersion()), collector, null);
			if (!collector.isEmpty())
				return true;
		}
		return false;
	}

	public static IProfile createProfile(String id, IPath installFolder) throws ProvisionException {
		IProfileRegistry profileRegistry = 
			(IProfileRegistry) ServiceHelper.getService(Activator.getContext(), IProfileRegistry.class.getName());
		Properties properties = new Properties();
		if (installFolder != null)
			properties.setProperty(IProfile.PROP_INSTALL_FOLDER, installFolder.toOSString());
		properties.setProperty(PROP_PULSAR_PROFILE, Boolean.TRUE.toString());
		return profileRegistry.addProfile(id, properties, null);
	}
	
	
	public static void deleteProfile(String id) {
		IProfileRegistry profileRegistry = 
			(IProfileRegistry) ServiceHelper.getService(Activator.getContext(), IProfileRegistry.class.getName());
		profileRegistry.removeProfile(id);
	}
	
	public static IProfile createProfileForSDK(ISDK sdk, IPath installFolder) throws ProvisionException {
		StringBuilder sb = new StringBuilder();
		sb.append("org.eclipse.pulsar.profile."); //$NON-NLS-1$
		sb.append(((SDK) sdk).getInstallableUnit().getId());
		sb.append("."); //$NON-NLS-1$
		sb.append(System.currentTimeMillis());
		
		return createProfile(sb.toString(), installFolder);
	}
	
	public static boolean isSupportedProfile(IProfile profile) {
		return profile.getProperty(PROP_PULSAR_PROFILE) != null;
	}
	
	public static Collection<IProfile> getProfiles() {
		Collection<IProfile> profiles = new ArrayList<IProfile>();
		for (IProfile profile : ProvisioningHelper.getProfiles()) {
			if (isSupportedProfile(profile))
				profiles.add(profile);
		}
		
		return profiles;
	}
}
