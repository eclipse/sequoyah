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
 *  David Marques (Motorola) - Updating test class.
 */

package org.eclipse.mtj.pulsar.core.tests.utils;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mtj.internal.provisional.pulsar.core.ISDKRepository;
import org.eclipse.mtj.internal.provisional.pulsar.core.ISDKRepositoryProvider;
import org.eclipse.mtj.internal.provisional.pulsar.core.IInstallationInfo;
import org.eclipse.mtj.internal.pulsar.core.SDKRepository;
import org.eclipse.mtj.pulsar.core.tests.Activator;

public class TestSDKRepositoryProvider implements ISDKRepositoryProvider {

	public TestSDKRepositoryProvider() {
	}

	public Collection<ISDKRepository> getRepositories() {
		try {
			URI auri = getBundleURL("data/test/artifactsRepo").toURI();
			URI muri = getBundleURL("data/test").toURI();
			SDKRepository repository = new SDKRepository("Pulsar local test repository", muri, auri);
			URL imageUrl = getBundleURL("data/test/sample.png");
			repository.setImageDescriptorURL(imageUrl);
			repository.setInstallationInfo(new TestInstallationInfo());
			return Collections.singleton((ISDKRepository) repository);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private URL getBundleURL(String relpath) {
		return FileLocator.find(Activator.getDefault().getBundle()
				, new Path(relpath), null);
	}

	private class TestInstallationInfo implements IInstallationInfo {

		public StringBuffer getDescription() {
			return new StringBuffer("Pulsar Rocks....");
		}

		public ImageDescriptor getImageDescriptor() {
			ImageDescriptor result = null;
			URL url = getBundleURL("icons/pulsar_logo.png");
			if (url != null) {				
				result = ImageDescriptor.createFromURL(url);
			}
			return result;
		}

		public URI getWebSiteURI() {
			URI result = null;
			try {
				result = new URI("http://www.eclipse.org/pulsar");
			} catch (URISyntaxException e) {
			}
			return result;
		}
		
	}
}
