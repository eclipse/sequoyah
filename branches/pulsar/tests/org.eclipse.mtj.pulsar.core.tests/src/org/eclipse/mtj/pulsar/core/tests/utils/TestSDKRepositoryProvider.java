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

package org.eclipse.mtj.pulsar.core.tests.utils;

import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.mtj.internal.provisional.pulsar.core.ISDKRepository;
import org.eclipse.mtj.internal.provisional.pulsar.core.ISDKRepositoryProvider;
import org.eclipse.mtj.internal.pulsar.core.SDKRepository;
import org.eclipse.mtj.pulsar.core.tests.Activator;

public class TestSDKRepositoryProvider implements ISDKRepositoryProvider {

	public TestSDKRepositoryProvider() {
	}

	public Collection<ISDKRepository> getRepositories() {
		try {
			URI muri = getBundleURL("data/test").toURI();
			IPath p = new Path(muri.toString());
			p = p.append("artifactsRepo");
			URI auri = new URL(p.toString()).toURI();
			SDKRepository repository = new SDKRepository("Test local repository", muri, auri);
			URL imageUrl = getBundleURL("data/test/sample.gif");
			repository.setImageDescriptorURL(imageUrl);
			return Collections.singleton((ISDKRepository) repository);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private URL getBundleURL(String relpath) {
		URL[] entries = FileLocator.findEntries(Activator.getDefault().getBundle(), new Path(relpath));
		return entries[0];
	}

}
