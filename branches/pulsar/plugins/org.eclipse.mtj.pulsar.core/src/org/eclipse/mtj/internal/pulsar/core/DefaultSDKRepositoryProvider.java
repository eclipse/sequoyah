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

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.mtj.internal.provisional.pulsar.core.ISDKRepository;
import org.eclipse.mtj.internal.provisional.pulsar.core.ISDKRepositoryProvider;
import org.eclipse.mtj.pulsar.core.Activator;

/**
 * An implementation of the sdk repository provider extension that uses a
 * properties file to provide the sdk repositories
 */
public class DefaultSDKRepositoryProvider implements ISDKRepositoryProvider {

	private static final String NAME_KEY_SUFFIX = ".name"; //$NON-NLS-1$
	private static final String METADATA_KEY_SUFFIX = ".metadata"; //$NON-NLS-1$
	private static final String ARTIFACTS_KEY_SUFFIX = ".artifacts"; //$NON-NLS-1$
	private static final String IMAGE_KEY_SUFFIX = ".image"; //$NON-NLS-1$
	private URL repositoriesFileUrl;
	
	public DefaultSDKRepositoryProvider() {
		URL[] entries = 
			FileLocator.findEntries(Activator.getDefault().getBundle(), 
					new Path("repositories/repositories.properties")); //$NON-NLS-1$
		repositoriesFileUrl = entries[0];
	}

	public Collection<ISDKRepository> getRepositories() {
		Collection<ISDKRepository> repositories = new HashSet<ISDKRepository>();
		try {
			Properties properties = new Properties();
			InputStream inputStream = repositoriesFileUrl.openStream();
			properties.load(inputStream);
			for (Object o : properties.keySet()) {
				String s = o.toString();
				// first, look for the name key
				if (s.endsWith(NAME_KEY_SUFFIX)) {
					try {
						String name = properties.getProperty(s);
						// get the base key
						String key = s.substring(0, s.lastIndexOf(NAME_KEY_SUFFIX));
						// next, get the metadata key
						URL metadataUrl = new URL(properties.getProperty(key + METADATA_KEY_SUFFIX));
						// next, get the artifacts key
						URL artifactsUrl = new URL(properties.getProperty(key + ARTIFACTS_KEY_SUFFIX));
						SDKRepository repository = new SDKRepository(name, metadataUrl.toURI(), artifactsUrl.toURI());
						// now get the optional image url
						String imageUrlString = properties.getProperty(key + IMAGE_KEY_SUFFIX);
						if (imageUrlString != null) {
							repository.setImageDescriptorURL(new URL(imageUrlString));
						}

						repositories.add(repository);
					}
					catch (MalformedURLException e) {
						Activator.logError("Could not read sdk repository url", e);
					}
					catch (URISyntaxException e) {
						Activator.logError("Could not read sdk repository url", e);
					}
				}
			}
		} catch (Exception e) {
			Activator.logError("Could not read sdk repositories properties file", e);
		}
		
		return repositories;
	}
	
}
