/**
 * Copyright (c) 2009 Nokia Corporation and/or its subsidiary(-ies).
 * All rights reserved.
 * This component and the accompanying materials are made available
 * under the terms of the License "Eclipse Public License v1.0"
 * which accompanies this distribution, and is available
 * at the URL "http://www.eclipse.org/legal/epl-v10.html".
 *
 * Contributors:
 * Chad Peckham
 *
 */

package org.eclipse.sequoyah.pulsar.internal.metadata.generator.engine;

import java.util.Collection;

import org.eclipse.core.runtime.IPath;

/**
 * 
 */
public interface IRepositoryDescription {

	/**
	 * @return the metadataRepoName
	 */
	public String getMetadataRepoName();

	/**
	 * @param metadataRepoName the metadataRepoName to set
	 */
	public void setMetadataRepoName(String metadataRepoName);

	/**
	 * @return the artifactRepoName
	 */
	public String getArtifactRepoName();

	/**
	 * @param artifactRepoName the artifactRepoName to set
	 */
	public void setArtifactRepoName(String artifactRepoName);

	/**
	 * @return the repoLocation
	 */
	public IPath getRepoLocation();

	/**
	 * @param repoLocation the repoLocation to set
	 */
	public void setRepoLocation(IPath repoLocation);

	/**
	 * @return the isCompressed
	 */
	public boolean isCompressed();

	/**
	 * @param isCompressed the isCompressed to set
	 */
	public void setCompressed(boolean isCompressed);

	/**
	 * @return the unitCollection
	 */
	public Collection<IIUDescription> getUnitCollection();

	/**
	 * @param unitCollection the unitCollection to set
	 */
	public void setUnitCollection(
			Collection<IIUDescription> unitCollection);

	/**
	 * @param artifactLocation the relative artifactLocation to set
	 */
	public void setArtifactLocation(IPath artifactLocation);
	
	/**
	 * @param desc the IU to add to collection
	 */
	public void addIUDescription(IIUDescription desc);
	/**
	 * 
	 */
	public void removeAllIUDescriptions();
	
	/**
	 * @return the relative artifactLocation
	 */
	public IPath getArtifactLocation();
}
