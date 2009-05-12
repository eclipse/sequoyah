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

import org.eclipse.equinox.internal.p2.metadata.InstallableUnit;
import org.eclipse.mtj.internal.pulsar.core.SDK;

/**
 * 
 */
public interface RepositoryConstants {
	
	public static final String UNZIP_TOUCHPOINT_DATA="unzip(source:@artifact, target:${installFolder})";
	public static final String EXE_TOUCHPOINT_DATA="execute(executable:@artifact)";
	public static final String UNZIPEXE_TOUCHPOINT_DATA="unzipandexecute(source:@artifact,target:${installFolder},executable:";
	public static final String UNZIPEXE_TOUCHPOINT_DATA_PREFIX="unzipandexecute";
	public static final String UNZIPEXE_TOUCHPOINT_DATA_EXECUTABLE="executable:";
	public static final String INSTALL_TOUCHPOINT_KEY="install";
	public static final String UNZIP_ARTIFACT_CLASSIFIER = "unzip";
	public static final String EXE_ARTIFACT_CLASSIFIER = "exe";
	public static final String NAME_PROP=InstallableUnit.PROP_NAME;
	public static final String PROP_CATEGORY=SDK.PROP_CATEGORY;
	public static final String PROP_DOC_URL=SDK.PROP_DOC_URL;
	public static final String PULSAR_PROP="pulsar";
	public static final String NATIVE_TOUCHPOINT_TYPE="org.eclipse.equinox.p2.native";
	public static final String PROVIDED_DEFAULT="org.eclipse.equinox.p2.iu";
	public static final String MAPPING_RULE_REPOURL="${repoUrl}";
	public static final String MAPPING_RULE_ID="${id}";
	public static final String UNZIP_MAPPING_CLASSIFIER="(& (classifier=unzip))";
	public static final String EXE_MAPPING_CLASSIFIER="(& (classifier=exe))";
	public static final String ARTIFACTS_XML_NAME="artifacts.xml";
	public static final String METADATA_XML_NAME="content.xml";
}
