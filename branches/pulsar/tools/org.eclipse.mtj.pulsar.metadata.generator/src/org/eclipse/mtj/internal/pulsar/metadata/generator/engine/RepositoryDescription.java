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

package org.eclipse.mtj.internal.pulsar.metadata.generator.engine;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.IPath;

/**
 * 
 */
public class RepositoryDescription implements IRepositoryDescription {
	
	private String metadataRepoName;		// repository name=
	private String artifactRepoName;		// repository name=
	private IPath repoLocation;			// where repository is physically located (where to load/save from/to)
	private IPath artifactLocation;		// where actual artifacts are located (relative to repoLocation)
	private boolean isCompressed;			// whether repository is compressed
	private Collection<IIUDescription> unitCollection;		// installable units in this repository
	/**
	 * 
	 */
	public RepositoryDescription() {
		super();
		unitCollection = new ArrayList<IIUDescription>();
		unitCollection.clear();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.pulsar.metadata.generator.engine.IRespositoryDescription#getMetadataRepoName()
	 */
	public String getMetadataRepoName() {
		return metadataRepoName;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.pulsar.metadata.generator.engine.IRespositoryDescription#setMetadataRepoName(java.lang.String)
	 */
	public void setMetadataRepoName(String metadataRepoName) {
		this.metadataRepoName = metadataRepoName;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.pulsar.metadata.generator.engine.IRespositoryDescription#getArtifactRepoName()
	 */
	public String getArtifactRepoName() {
		return artifactRepoName;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.pulsar.metadata.generator.engine.IRespositoryDescription#setArtifactRepoName(java.lang.String)
	 */
	public void setArtifactRepoName(String artifactRepoName) {
		this.artifactRepoName = artifactRepoName;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.pulsar.metadata.generator.engine.IRespositoryDescription#getRepoLocation()
	 */
	public IPath getRepoLocation() {
		return repoLocation;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.pulsar.metadata.generator.engine.IRespositoryDescription#setRepoLocation(java.lang.String)
	 */
	public void setRepoLocation(IPath repoLocation) {
		this.repoLocation = repoLocation;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.pulsar.metadata.generator.engine.IRespositoryDescription#isCompressed()
	 */
	public boolean isCompressed() {
		return isCompressed;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.pulsar.metadata.generator.engine.IRespositoryDescription#setCompressed(boolean)
	 */
	public void setCompressed(boolean isCompressed) {
		this.isCompressed = isCompressed;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.pulsar.metadata.generator.engine.IRespositoryDescription#getUnitCollection()
	 */
	public Collection<IIUDescription> getUnitCollection() {
		return unitCollection;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.pulsar.metadata.generator.engine.IRespositoryDescription#setUnitCollection(java.util.Collection)
	 */
	public void setUnitCollection(Collection<IIUDescription> unitCollection) {
		this.unitCollection = unitCollection;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.mtj.internal.pulsar.metadata.generator.engine.IRespositoryDescription#getArtifactLocation()
	 */
	public IPath getArtifactLocation() {
		return artifactLocation;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.mtj.internal.pulsar.metadata.generator.engine.IRespositoryDescription#setArtifactLocation(java.lang.String)
	 */
	public void setArtifactLocation(IPath artifactLocation) {
		this.artifactLocation = artifactLocation;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.mtj.internal.pulsar.metadata.generator.engine.IRespositoryDescription#addIUDescription(org.eclipse.mtj.internal.pulsar.metadata.generator.engine.IIUDescription)
	 */
	public void addIUDescription(IIUDescription desc) {
		if (unitCollection == null) {
			unitCollection = new ArrayList<IIUDescription>();
		}
		unitCollection.add(desc);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.mtj.internal.pulsar.metadata.generator.engine.IRepositoryDescription#removeAllIUDescriptions()
	 */
	public void removeAllIUDescriptions() {
		if (unitCollection == null) {
			unitCollection = new ArrayList<IIUDescription>();
		}
		unitCollection.clear();
	}
}
