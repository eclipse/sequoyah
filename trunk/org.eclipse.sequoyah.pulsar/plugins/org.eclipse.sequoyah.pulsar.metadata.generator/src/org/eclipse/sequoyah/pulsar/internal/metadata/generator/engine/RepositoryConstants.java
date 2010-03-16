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
 * Henrique Magalhaes (Motorola) - Internalization of messages
 */

package org.eclipse.sequoyah.pulsar.internal.metadata.generator.engine;

import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.sequoyah.pulsar.internal.core.SDK;

/**
 * 
 */
@SuppressWarnings("restriction")
public interface RepositoryConstants {

    public static final String UNZIP_TOUCHPOINT_DATA = "unzip(source:@artifact, target:${installFolder})"; //$NON-NLS-1$
    public static final String EXE_TOUCHPOINT_DATA = "execute(executable:@artifact)";//$NON-NLS-1$
    public static final String UNZIPEXE_TOUCHPOINT_DATA = "unzipandexecute(source:@artifact,target:${installFolder},executable:";//$NON-NLS-1$
    public static final String UNZIPEXE_TOUCHPOINT_DATA_PREFIX = "unzipandexecute";//$NON-NLS-1$
    public static final String UNZIPEXE_TOUCHPOINT_DATA_EXECUTABLE = "executable:";//$NON-NLS-1$
    public static final String INSTALL_TOUCHPOINT_KEY = "install"; //$NON-NLS-1$
    public static final String UNZIP_ARTIFACT_CLASSIFIER = "unzip"; //$NON-NLS-1$
    public static final String EXE_ARTIFACT_CLASSIFIER = "exe"; //$NON-NLS-1$
    public static final String NAME_PROP = IInstallableUnit.PROP_NAME;
    public static final String PROP_CATEGORY = SDK.PROP_CATEGORY;
    public static final String PROP_DOC_URL = SDK.PROP_DOC_URL;
    public static final String PULSAR_PROP = "pulsar"; //$NON-NLS-1$
    public static final String NATIVE_TOUCHPOINT_TYPE = "org.eclipse.equinox.p2.native"; //$NON-NLS-1$
    public static final String PROVIDED_DEFAULT = "org.eclipse.equinox.p2.iu"; //$NON-NLS-1$
    public static final String MAPPING_RULE_REPOURL = "${repoUrl}"; //$NON-NLS-1$
    public static final String MAPPING_RULE_ID = "${id}"; //$NON-NLS-1$
    public static final String UNZIP_MAPPING_CLASSIFIER = "(& (classifier=unzip))"; //$NON-NLS-1$
    public static final String EXE_MAPPING_CLASSIFIER = "(& (classifier=exe))"; //$NON-NLS-1$
    public static final String ARTIFACTS_XML_NAME = "artifacts.xml"; //$NON-NLS-1$
    public static final String METADATA_XML_NAME = "content.xml"; //$NON-NLS-1$
}
