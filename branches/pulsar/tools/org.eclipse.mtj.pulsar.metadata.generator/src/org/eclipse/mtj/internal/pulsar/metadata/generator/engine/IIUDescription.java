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
import org.eclipse.mtj.internal.provisional.pulsar.core.ISDK.EType;

/**
 * 
 */
@SuppressWarnings("restriction")
public interface IIUDescription {

    /**
     * @return the artifactType
     */
    public EType getArtifactType();

    /**
     * @param artifactType the artifactType to set
     */
    public void setArtifactType(EType artifactType);

    /**
     * @return the artifactId
     */
    public String getArtifactId();

    /**
     * @param artifactId the artifactId to set
     */
    public void setArtifactId(String artifactId);

    /**
     * @return the artifactVersion
     */
    public Version getArtifactVersion();

    /**
     * @param artifactVersion the artifactVersion to set
     */
    public void setArtifactVersion(Version artifactVersion);

    /**
     * @return the unitId
     */
    public String getUnitId();

    /**
     * @param unitId the unitId to set
     */
    public void setUnitId(String unitId);

    /**
     * @return the unitVersion
     */
    public Version getUnitVersion();

    /**
     * @param unitVersion the unitVersion to set
     */
    public void setUnitVersion(Version unitVersion);

    /**
     * @return the unitLicense
     */
    public ILicense getUnitLicense();

    /**
     * @param unitLicense the unitLicense to set
     */
    public void setUnitLicense(ILicense unitLicense);

    /**
     * 
     * @param location the location of a document containing the full license,
     *            or <code>null</code>
     * @param body the license body, cannot be <code>null</code>
     */
    public void setUnitLicense(URI location, String body);

    /**
     * @return the unitCopyright
     */
    public ICopyright getUnitCopyright();

    /**
     * @param unitCopyright the unitCopyright to set
     */
    public void setUnitCopyright(ICopyright unitCopyright);

    /**
     * @param location the location of a document containing the copyright
     *            notice, or <code>null</code>
     * @param body the copyright body, cannot be <code>null</code>
     */
    public void setUnitCopyright(URI location, String body);

    /**
     * @return the isCategory
     */
    public boolean isCategory();

    /**
     * @param unitName the unitName to set
     */
    public void setUnitName(String unitName);

    /**
     * @return the unitName
     */
    public String getUnitName();

    /**
     * @param categoryName the categoryName to set
     */
    public void setCategoryName(String categoryName);

    /**
     * @return the categoryName
     */
    public String getCategoryName();

    /**
     * @param categoryName the artefactDescription to set
     */
    public void setArtifactDescription(String artefactDescription);

    /**
     * @return the artefactDescription
     */
    public String getArtifactDescription();

    /**
     * @param isSingleton the isSingleton to set
     */
    public void setSingleton(boolean isSingleton);

    /**
     * @return the isSingleton
     */
    public boolean isSingleton();

    /**
     * @param unitDocumentationURL URL to documentation for installable unit
     */
    public void setUnitDocumentationURL(URL unitDocumentationURL);

    /**
     * @return URL URL to installable unit documentation
     */
    public URL getUnitDocumentationURL();

    /**
     * @param executablePath
     */
    public void setExecutablePath(IPath executablePath);

    /**
     * @return executablePath
     */
    public IPath getExecutablePath();

    public String getOs();

    public void setOs(String os);

    public String getWs();

    public void setWs(String ws);

    public String getArch();

    public void setArch(String arch);
}
