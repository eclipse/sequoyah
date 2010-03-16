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

package org.eclipse.sequoyah.pulsar.core.tests.utils;

import java.io.File;
import java.net.URI;

import junit.framework.AssertionFailedError;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.equinox.internal.p2.core.DefaultAgentProvider;
import org.eclipse.equinox.internal.p2.core.helpers.ServiceHelper;
import org.eclipse.equinox.internal.provisional.p2.director.IDirector;
import org.eclipse.equinox.internal.provisional.p2.director.ProfileChangeRequest;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.engine.IProfile;
import org.eclipse.equinox.p2.engine.ProvisioningContext;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.sequoyah.pulsar.core.tests.Activator;
import org.osgi.framework.BundleContext;

public class TestUtils {
	
	public interface Condition {
		public boolean test();
	}

	public static IStatus installIU(IProfile profile, IInstallableUnit iu, URI metadataURI, URI artifactsURI) {
		ProfileChangeRequest changeRequest = new ProfileChangeRequest(profile);
		changeRequest.addInstallableUnits(new IInstallableUnit[] { iu });
		URI[] muris = { metadataURI };
		BundleContext bundleContext = Activator.getDefault().getBundle().getBundleContext();
		DefaultAgentProvider agentProvider = new DefaultAgentProvider();
        agentProvider.activate(bundleContext);
        IProvisioningAgent agent = agentProvider.createAgent(null);
		ProvisioningContext context = new ProvisioningContext(agent);
		context.setMetadataRepositories(muris);
		URI[] auris = { artifactsURI };
        context.setArtifactRepositories(auris);
        IDirector director = 
        	(IDirector) ServiceHelper.getService(bundleContext, IDirector.class.getName());
        return director.provision(changeRequest, context, null);
	}

	public static String getMessage(IStatus status) {
		if (status.isMultiStatus()) {
			StringBuilder builder = new StringBuilder(status.getMessage());
			MultiStatus ms = (MultiStatus) status;
			for (IStatus s : ms.getChildren()) {
				builder.append('\n');
				builder.append(getMessage(s));
			}
			return builder.toString();
		}
		else
			return status.getMessage();
	}
	
    public static void deleteDir(File file) {
        File[] files = file.listFiles();
        if (files != null) { 
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDir(files[i]);
                }
            }
        }
        files = file.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                files[i].delete();
            }
        }
        file.delete();
    }

	public static void waitFor(Condition condition) throws AssertionFailedError, InterruptedException {
		for (int i = 0; i < 20; i++) {
			if (condition.test())
				return;
			Thread.sleep(500);
		}
		throw new AssertionFailedError();
	}
    
}
