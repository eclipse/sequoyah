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
 *  David Marques (Motorola) - Adding installation environment support.
 */

package org.eclipse.sequoyah.pulsar.internal.core;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.sequoyah.pulsar.core.Activator;
import org.eclipse.sequoyah.pulsar.internal.provisional.core.IInstallationEnvironment;
import org.eclipse.sequoyah.pulsar.internal.provisional.core.IInstallationInfo;
import org.eclipse.sequoyah.pulsar.internal.provisional.core.ISDKRepository;
import org.eclipse.sequoyah.pulsar.internal.provisional.core.ISDKRepositoryProvider;

/**
 * An implementation of the sdk repository provider extension that uses a
 * properties file to provide the sdk repositories
 */
public class DefaultSDKRepositoryProvider implements ISDKRepositoryProvider {

	private static final String NAME_KEY_SUFFIX = ".name"; //$NON-NLS-1$
	private static final String METADATA_KEY_SUFFIX = ".metadata"; //$NON-NLS-1$
	private static final String ARTIFACTS_KEY_SUFFIX = ".artifacts"; //$NON-NLS-1$
	private static final String IMAGE_KEY_SUFFIX = ".image"; //$NON-NLS-1$
	private static final String INFO_URL_SUFFIX   = ".info.url";
	private static final String INFO_IMAGE_SUFFIX = ".info.image";
	private static final String INFO_TEXT_SUFFIX  = ".info.description";
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

						//Reads Repository info
						IInstallationInfo repositoryInfo = readRepositoryInfo(key, properties);
						repository.setInstallationInfo(repositoryInfo);
						repositories.add(repository);
					}
					catch (MalformedURLException e) {
						Activator.logError(Messages.DefaultSDKRepositoryProvider_URLError, e);
					}
					catch (URISyntaxException e) {
						Activator.logError(Messages.DefaultSDKRepositoryProvider_URLError, e);
					}
				}
			}
		} catch (Exception e) {
			Activator.logError(Messages.DefaultSDKRepositoryProvider_FileReadError, e);
		}
		
		return repositories;
	}

	/**
	 * Gets the repository information from the repositories.properties.
	 * 
	 * @param key repository name.
	 * @param properties {@link Properties} instance.
	 * @return the {@link IInstallationInfo} instance with the repository
	 * information.
	 */
	private IInstallationInfo readRepositoryInfo(String key, Properties properties) {
		StringBuffer text = null;
		URL imageUrl = null;
		URL siteUrl = null;

		try {
			imageUrl = new URL(properties.getProperty(key + INFO_IMAGE_SUFFIX));
		} catch (MalformedURLException e) {
		}
		
		try {
			siteUrl = new URL(properties.getProperty(key + INFO_URL_SUFFIX));
		} catch (MalformedURLException e) {
		}
		
		text = new StringBuffer(properties.getProperty(key + INFO_TEXT_SUFFIX, "")); //$NON-NLS-1$
		return new SDKRepositoryInfo(siteUrl, imageUrl, text);
	}
	
	private class SDKRepositoryInfo implements IInstallationInfo {
		
		private URL siteUrl;
		private URL imageUrl;
		private StringBuffer text;

		public SDKRepositoryInfo(URL siteUrl, URL imageUrl, StringBuffer text) {
			this.siteUrl  = siteUrl;
			this.imageUrl = imageUrl;
			this.text = text;
		}
		
		public ImageDescriptor getImageDescriptor() {
			ImageDescriptor result = null;
			if (this.imageUrl != null) {
				result = ImageDescriptor.createFromURL(this.imageUrl);
			}
			return result;
		}

		public StringBuffer getDescription() {
			return this.text;
		}
		
		public URI getWebSiteURI() {
			URI result = null;
			if (this.siteUrl != null) {					
				try {
					result = this.siteUrl.toURI();
				} catch (URISyntaxException e) {}
			}
			return result;
		}

		public IInstallationEnvironment getTargetEnvironment() {
			return null;
		}
	}
	
}
