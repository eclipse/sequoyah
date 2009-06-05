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
 * Henrique Magalhaes (Motorola) - Added description field
 * David Marques (Motorola) - Implementing environment filtering.
 */

package org.eclipse.mtj.internal.pulsar.metadata.generator.engine;

import java.net.URI;
import java.net.URL;

import org.eclipse.core.runtime.IPath;
import org.eclipse.equinox.internal.provisional.p2.core.Version;
import org.eclipse.equinox.internal.provisional.p2.metadata.ICopyright;
import org.eclipse.equinox.internal.provisional.p2.metadata.ILicense;
import org.eclipse.equinox.internal.provisional.p2.metadata.MetadataFactory;
import org.eclipse.mtj.internal.provisional.pulsar.core.ISDK.EType;

@SuppressWarnings("restriction")
public class IUDescription implements IIUDescription {
    private EType artifactType; // zip or exe
    private String artifactId; // e.g., com.nokia.s60
    private Version artifactVersion; // e.g 1.0.0
    private String unitId; // maybe same as artifactId
    private Version unitVersion;
    private URL unitDocumentationURL; // URL to documentation for installable
                                      // unit (optional)
    private boolean isSingleton;
    private String unitName; // name property
    private ILicense unitLicense; // EULA stuff
    private ICopyright unitCopyright;
    private boolean isCategory; // whether this is a category IU
    private String categoryName; // category name
    private String artefactDescription; // artefact description
    private IPath executablePath; // installer path for unzip&execute
    private String os;
    private String ws;
    private String arch;

    /**
	 * 
	 */
    public IUDescription() {
        artifactVersion = Version.createOSGi(0, 0, 0);
        unitVersion = Version.createOSGi(0, 0, 0);
        artifactType = EType.UNKNOWN;
    }

    /* (non-Javadoc)
     * @see org.eclipse.pulsar.metadata.generator.engine.IIUDescription#getArtifactType()
     */
    public EType getArtifactType() {
        return artifactType;
    }

    /* (non-Javadoc)
     * @see org.eclipse.pulsar.metadaversionta.generator.engine.IIUDescription#setArtifactType(java.lang.String)
     */
    public void setArtifactType(EType artifactType) {
        this.artifactType = artifactType;
    }

    /* (non-Javadoc)
     * @see org.eclipse.pulsar.metadata.generator.engine.IIUDescription#getArtifactId()
     */
    public String getArtifactId() {
        return artifactId;
    }

    /* (non-Javadoc)
     * @see org.eclipse.pulsar.metadata.generator.engine.IIUDescription#setArtifactId(java.lang.String)
     */
    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    /* (non-Javadoc)
     * @see org.eclipse.pulsar.metadata.generator.engine.IIUDescription#getArtifactVersion()
     */
    public Version getArtifactVersion() {
        return artifactVersion;
    }

    /* (non-Javadoc)
     * @see org.eclipse.pulsar.metadata.generator.engine.IIUDescription#setArtifactVersion(org.eclipse.equinox.internal.provisional.p2.core.Version)
     */
    public void setArtifactVersion(Version artifactVersion) {
        this.artifactVersion = artifactVersion;
    }

    /* (non-Javadoc)
     * @see org.eclipse.pulsar.metadata.generator.engine.IIUDescription#getUnitId()
     */
    public String getUnitId() {
        return unitId;
    }

    /* (non-Javadoc)
     * @see org.eclipse.pulsar.metadata.generator.engine.IIUDescription#setUnitId(java.lang.String)
     */
    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    /* (non-Javadoc)
     * @see org.eclipse.pulsar.metadata.generator.engine.IIUDescription#getUnitVersion()
     */
    public Version getUnitVersion() {
        return unitVersion;
    }

    /* (non-Javadoc)
     * @see org.eclipse.pulsar.metadata.generator.engine.IIUDescription#setUnitVersion(org.eclipse.equinox.internal.provisional.p2.core.Version)
     */
    public void setUnitVersion(Version unitVersion) {
        this.unitVersion = unitVersion;
    }

    /* (non-Javadoc)
     * @see org.eclipse.pulsar.metadata.generator.engine.IIUDescription#getUnitLicense()
     */
    public ILicense getUnitLicense() {
        return unitLicense;
    }

    /* (non-Javadoc)
     * @see org.eclipse.pulsar.metadata.generator.engine.IIUDescription#setUnitLicense(org.eclipse.equinox.internal.provisional.p2.metadata.ILicense)
     */
    public void setUnitLicense(ILicense unitLicense) {
        this.unitLicense = unitLicense;
    }

    /* (non-Javadoc)
     * @see org.eclipse.pulsar.metadata.generator.engine.IIUDescription#getUnitCopyright()
     */
    public ICopyright getUnitCopyright() {
        return unitCopyright;
    }

    /* (non-Javadoc)
     * @see org.eclipse.pulsar.metadata.generator.engine.IIUDescription#setUnitCopyright(org.eclipse.equinox.internal.provisional.p2.metadata.ICopyright)
     */
    public void setUnitCopyright(ICopyright unitCopyright) {
        this.unitCopyright = unitCopyright;
    }

    /* (non-Javadoc)
     * @see org.eclipse.pulsar.metadata.generator.engine.IIUDescription#isCategory()
     */
    public boolean isCategory() {
        return isCategory;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.internal.pulsar.metadata.generator.engine.IIUDescription#getUnitName()
     */
    public String getUnitName() {
        return unitName;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.internal.pulsar.metadata.generator.engine.IIUDescription#setUnitName(java.lang.String)
     */
    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.internal.pulsar.metadata.generator.engine.IIUDescription#getCategoryName()
     */
    public String getCategoryName() {
        return categoryName;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.internal.pulsar.metadata.generator.engine.IIUDescription#setCategoryName(java.lang.String)
     */
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.internal.pulsar.metadata.generator.engine.IIUDescription#getArtifactDescription()
     */
    public String getArtifactDescription() {
        return this.artefactDescription;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.internal.pulsar.metadata.generator.engine.IIUDescription#setArtifactDescription(java.lang.String)
     */
    public void setArtifactDescription(String artefactDescription) {
        this.artefactDescription = artefactDescription;

    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.internal.pulsar.metadata.generator.engine.IIUDescription#setUnitCopyright(java.net.URI, java.lang.String)
     */
    public void setUnitCopyright(URI location, String body) {
        this.unitCopyright = MetadataFactory.createCopyright(location, body);
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.internal.pulsar.metadata.generator.engine.IIUDescription#setUnitLicense(java.net.URI, java.lang.String)
     */
    public void setUnitLicense(URI location, String body) {
        this.unitLicense = MetadataFactory.createLicense(location, body);
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.internal.pulsar.metadata.generator.engine.IIUDescription#isSingleton()
     */
    public boolean isSingleton() {
        return isSingleton;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.internal.pulsar.metadata.generator.engine.IIUDescription#setSingleton(boolean)
     */
    public void setSingleton(boolean isSingleton) {
        this.isSingleton = isSingleton;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.internal.pulsar.metadata.generator.engine.IIUDescription#getUnitDocumentationURL()
     */
    public URL getUnitDocumentationURL() {
        return unitDocumentationURL;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.internal.pulsar.metadata.generator.engine.IIUDescription#setUnitDocumentationURL(java.net.URL)
     */
    public void setUnitDocumentationURL(URL unitDocumentationURL) {
        this.unitDocumentationURL = unitDocumentationURL;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.internal.pulsar.metadata.generator.engine.IIUDescription#getExecutablePath()
     */
    public IPath getExecutablePath() {
        return executablePath;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.internal.pulsar.metadata.generator.engine.IIUDescription#setExecutablePath(org.eclipse.core.runtime.IPath)
     */
    public void setExecutablePath(IPath executablePath) {
        this.executablePath = executablePath;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.internal.pulsar.metadata.generator.engine.IIUDescription#getOs()
     */
    public String getOs() {
        return os;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.internal.pulsar.metadata.generator.engine.IIUDescription#setOs(java.lang.String)
     */
    public void setOs(String os) {
        this.os = os;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.internal.pulsar.metadata.generator.engine.IIUDescription#getWs()
     */
    public String getWs() {
        return ws;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.internal.pulsar.metadata.generator.engine.IIUDescription#setWs(java.lang.String)
     */
    public void setWs(String ws) {
        this.ws = ws;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.internal.pulsar.metadata.generator.engine.IIUDescription#getArch()
     */
    public String getArch() {
        return arch;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.internal.pulsar.metadata.generator.engine.IIUDescription#setArch(java.lang.String)
     */
    public void setArch(String arch) {
        this.arch = arch;
    }

}
