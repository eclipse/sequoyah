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

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.equinox.internal.p2.console.ProvisioningHelper;
import org.eclipse.equinox.internal.provisional.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.internal.provisional.p2.query.Collector;
import org.eclipse.equinox.internal.provisional.p2.query.MatchQuery;
import org.eclipse.equinox.internal.provisional.p2.query.Query;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mtj.internal.provisional.pulsar.core.ISDK;
import org.eclipse.mtj.internal.provisional.pulsar.core.ISDKRepository;

public class SDKRepository implements ISDKRepository {

	private URI metadataUri;
	private URI artifactsUri;
	private String name;
	private ImageDescriptor imageDescriptor;

	public SDKRepository(String name, URI metadataUri, URI artifactsUri) {
		this.name = name;
		this.metadataUri = metadataUri;
		this.artifactsUri = artifactsUri;
	}

	@SuppressWarnings("unchecked")
	public Collection<ISDK> getSDKs(IProgressMonitor monitor) {
		Collection<ISDK> sdks = new ArrayList<ISDK>();
		Collector installableUnits = 
			ProvisioningHelper.getInstallableUnits(getMetadataURI(), getSDKQuery(), monitor);
		for (IInstallableUnit iu : (Collection<IInstallableUnit>) installableUnits.toCollection()) {
			sdks.add(new SDK(this, iu));
		}
		return sdks;
	}

	private Query getSDKQuery() {
		return new MatchQuery() {
			@Override
			public boolean isMatch(Object candidate) {
				if (candidate instanceof IInstallableUnit) {
					IInstallableUnit iu = (IInstallableUnit) candidate;
					return iu.getProperty(SDK.PROP_TYPE) != null;
				}
				return false;
			}
		};
	}

	public URI getMetadataURI() {
		return metadataUri;
	}

	public URI getArtifactsURI() {
		return artifactsUri;
	}
	
	public String getName() {
		return name;
	}

	public ImageDescriptor getImageDescriptor() {
		return imageDescriptor;
	}

	public void setImageDescriptorURL(URL imageUrl) {
		this.imageDescriptor = ImageDescriptor.createFromURL(imageUrl);
	}

}
