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

package org.eclipse.sequoyah.pulsar.internal.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.equinox.internal.p2.core.DefaultAgentProvider;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.engine.IProfile;
import org.eclipse.equinox.p2.engine.IProfileRegistry;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.query.IQuery;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.sequoyah.pulsar.core.Activator;
import org.eclipse.sequoyah.pulsar.internal.provisional.core.ISDK;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * A class of utilities for P2 common operations
 */
@SuppressWarnings("restriction")
public class P2Utils {

    public static final String PROP_PULSAR_PROFILE = "org.eclipse.pulsar.profile"; //$NON-NLS-1$

    /**
     * @param iu
     * @return
     */
    public static boolean isInstalled(IInstallableUnit iu) {
    	IQuery<IInstallableUnit> query = QueryUtil.createIUQuery(iu.getId(), iu.getVersion());
    	BundleContext context = Activator.getContext();
    	IProvisioningAgent agent = getProvisioningAgent(context);
		IProfileRegistry profileRegistry = (IProfileRegistry) agent.getService(IProfileRegistry.SERVICE_NAME);
        for (IProfile profile : profileRegistry.getProfiles()) {
        	IQueryResult<IInstallableUnit> available = profile.query(query, new NullProgressMonitor());
            if (!available.isEmpty())
                return true;
        }
        return false;
    }

	public static ServiceReference getProvisiningAgentReference(
			BundleContext context) {
		ServiceReference serviceReference = context.getServiceReference(IProvisioningAgent.SERVICE_NAME);
		return serviceReference;
	}

    /**
     * @param id
     * @param installFolder
     * @return
     * @throws ProvisionException
     */
    public static IProfile createProfile(String id, IPath installFolder)
            throws ProvisionException {
    	BundleContext context = Activator.getContext();
    	IProvisioningAgent agent = getProvisioningAgent(context);
    	IProfileRegistry profileRegistry = (IProfileRegistry) agent.getService(IProfileRegistry.SERVICE_NAME);
        Map<String, String> propMap = new HashMap<String, String>();
        if (installFolder != null)
        	propMap.put(IProfile.PROP_INSTALL_FOLDER, installFolder
                    .toOSString());
        propMap.put(PROP_PULSAR_PROFILE, Boolean.TRUE.toString());
        return profileRegistry.addProfile(id, propMap);
    }

    /**
     * @param id
     */
    public static void deleteProfile(String id) {
    	BundleContext context = Activator.getContext();
    	IProvisioningAgent agent = getProvisioningAgent(context);
    	IProfileRegistry profileRegistry = (IProfileRegistry) agent.getService(IProfileRegistry.SERVICE_NAME);
        profileRegistry.removeProfile(id);
    }

    /**
     * @param sdk
     * @param installFolder
     * @return
     * @throws ProvisionException
     */
    public static IProfile createProfileForSDK(ISDK sdk, IPath installFolder)
            throws ProvisionException {
        StringBuilder sb = new StringBuilder();
        sb.append("org.eclipse.pulsar.profile."); //$NON-NLS-1$
        sb.append(((SDK) sdk).getInstallableUnit().getId());
        sb.append("."); //$NON-NLS-1$
        sb.append(System.currentTimeMillis());

        return createProfile(sb.toString(), installFolder);
    }

    /**
     * @param profile
     * @return
     */
    public static boolean isSupportedProfile(IProfile profile) {
        return profile.getProperty(PROP_PULSAR_PROFILE) != null;
    }

    /**
     * @return
     */
    public static Collection<IProfile> getProfiles() {
    	BundleContext context = Activator.getContext();
    	IProvisioningAgent agent = getProvisioningAgent(context);
		IProfileRegistry profileRegistry = (IProfileRegistry) agent.getService(IProfileRegistry.SERVICE_NAME);
        Collection<IProfile> profiles = new ArrayList<IProfile>();
        for (IProfile profile : profileRegistry.getProfiles()) {
            if (isSupportedProfile(profile))
                profiles.add(profile);
        }

        return profiles;
    }

	public static IProvisioningAgent getProvisioningAgent(BundleContext context) {
		DefaultAgentProvider agentProvider = new DefaultAgentProvider();
        agentProvider.activate(context);
        IProvisioningAgent agent = agentProvider.createAgent(null);
		return agent;
	}
	
	public static Set<String> getInstalledFeatureIds(IProgressMonitor monitor) {
		Set<String> ids = new HashSet<String>();
    	BundleContext context = Activator.getContext();
    	IProvisioningAgent agent = getProvisioningAgent(context);
		IProfileRegistry profileRegistry = (IProfileRegistry) agent.getService(IProfileRegistry.SERVICE_NAME);
		IProfile profile = profileRegistry.getProfile(IProfileRegistry.SELF);
		IQuery<IInstallableUnit> query = QueryUtil.createMatchQuery("properties['org.eclipse.pulsar.type'] != null");
		IQueryResult<IInstallableUnit> result = profile.query(query, monitor);
		for (Iterator<IInstallableUnit> iterator = result.iterator(); iterator.hasNext();) {
			ids.add(iterator.next().getId());
		}
		return ids;
	}
}
