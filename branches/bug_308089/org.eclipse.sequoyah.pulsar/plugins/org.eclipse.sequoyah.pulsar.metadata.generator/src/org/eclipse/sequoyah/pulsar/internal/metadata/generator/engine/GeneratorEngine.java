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
 * David Marques (Motorola) - Implementing environment filtering.
 * Henrique Magalhaes(Motorola)/
 * Euclides Neto (Motorola) - Fixed environment filtering.
 * Euclides Neto (Motorola) - Adding Category description support.
 */

package org.eclipse.sequoyah.pulsar.internal.metadata.generator.engine;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.equinox.internal.p2.artifact.repository.simple.SimpleArtifactRepository;
import org.eclipse.equinox.internal.p2.core.helpers.OrderedProperties;
import org.eclipse.equinox.internal.p2.metadata.ArtifactKey;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.metadata.IArtifactKey;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.metadata.ILicense;
import org.eclipse.equinox.p2.metadata.IProvidedCapability;
import org.eclipse.equinox.p2.metadata.ITouchpointData;
import org.eclipse.equinox.p2.metadata.ITouchpointInstruction;
import org.eclipse.equinox.p2.metadata.MetadataFactory;
import org.eclipse.equinox.p2.metadata.MetadataFactory.InstallableUnitDescription;
import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.equinox.p2.query.IQuery;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.equinox.p2.repository.IRepository;
import org.eclipse.equinox.p2.repository.artifact.IArtifactDescriptor;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepository;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepositoryManager;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepository;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;
import org.eclipse.equinox.spi.p2.publisher.PublisherHelper;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sequoyah.pulsar.core.Activator;
import org.eclipse.sequoyah.pulsar.internal.core.P2Utils;
import org.eclipse.sequoyah.pulsar.internal.core.SDK;
import org.eclipse.sequoyah.pulsar.internal.metadata.generator.Messages;
import org.eclipse.sequoyah.pulsar.internal.provisional.core.ISDK.EType;

@SuppressWarnings( { "restriction" })
public class GeneratorEngine implements RepositoryConstants {

    private static final String FEATURE_GROUP_ID_SUFFIX = ".feature.group"; //$NON-NLS-1$

	private static boolean delete(File file) {
        if (!file.exists())
            return true;
        return file.delete();
    }

    /**
     * @param path path to the repository root folder
     * @return IRepositoryDescription null if repository does not exist
     * @throws Exception
     */
    public static IRepositoryDescription loadRepository(IPath path)
            throws Exception {
        IRepositoryDescription desc = null;
        try {
            desc = parseRepository(path);
        } catch (Exception e) {
            Activator
                    .logError(Messages.GeneratorEngine_ParseRepositoryError, e);
            throw e;
        }
        return desc;
    }

    /**
     * @param path
     * @return
     * @throws Exception
     */
    private static IRepositoryDescription parseRepository(IPath path)
            throws Exception {
        if (path == null) {
            throw new Exception(Messages.GeneratorEngine_NullPath);
        }
        File dir = path.toFile();
        File mFile = path.append(METADATA_XML_NAME).toFile();
        File aFile = path.append(ARTIFACTS_XML_NAME).toFile();
        if (mFile.exists() == false && aFile.exists() == false) {
            // create new repository
            return new RepositoryDescription();
        }
        if (mFile.exists() == false) {
            throw new Exception(Messages.GeneratorEngine_MetadataDoesNotExist);
        }
        if (aFile.exists() == false) {
            throw new Exception(Messages.GeneratorEngine_ArtifactsDoesNotExist);
        }
        URI uriDir = dir.toURI();
        
        IProvisioningAgent agent = P2Utils.getProvisioningAgent(Activator.getContext());
        IMetadataRepositoryManager metadataManager = (IMetadataRepositoryManager) agent.getService(IMetadataRepositoryManager.SERVICE_NAME);
		IMetadataRepository metaRepo = metadataManager.loadRepository(uriDir, new NullProgressMonitor());

		IArtifactRepositoryManager artifactManager = (IArtifactRepositoryManager) agent.getService(IArtifactRepositoryManager.SERVICE_NAME);
		IArtifactRepository artiRepo = artifactManager.loadRepository(uriDir, new NullProgressMonitor());
        IRepositoryDescription repoDesc = new RepositoryDescription();
        OrderedProperties props = (OrderedProperties) metaRepo.getProperties();
        if (props.getProperty(IRepository.PROP_COMPRESSED).equalsIgnoreCase(
                "true")) { //$NON-NLS-1$
            repoDesc.setCompressed(true);
        }

        repoDesc.setRepoLocation(path);
        repoDesc.setMetadataRepoName(metaRepo.getName());
        repoDesc.setArtifactRepoName(artiRepo.getName());
        SimpleArtifactRepository srepo = (SimpleArtifactRepository) artiRepo;
        String[][] rules = srepo.getRules();
        String output = rules[0][1];
        int beginIndex = output.indexOf(IPath.SEPARATOR);
        int endIndex = output.lastIndexOf(IPath.SEPARATOR);
        if (beginIndex != endIndex) {
            repoDesc.setArtifactLocation(new Path(output.substring(
                    beginIndex + 1, endIndex)));
        }
        
        IQuery<IInstallableUnit> query = QueryUtil.createIUAnyQuery();

        IQueryResult<IInstallableUnit> ius = metadataManager.query(query, new NullProgressMonitor());
        Set<IInstallableUnit> iusSet = ius.toSet();
        for (IInstallableUnit iu : iusSet) {
            IIUDescription iuDesc = new IUDescription();
            iuDesc.setUnitId(iu.getId());
            iuDesc.setUnitVersion(iu.getVersion());
            iuDesc.setSingleton(iu.isSingleton());

            if (iu.getProperty(NAME_PROP) != null) {
                iuDesc.setUnitName(iu.getProperty(NAME_PROP));
            }
            if (iu.getProperty(SDK.PROP_CATEGORY) != null) {
                iuDesc.setCategoryName(iu.getProperty(SDK.PROP_CATEGORY));
            }
            if (iu.getProperty(SDK.PROP_CATEGORY_DESC) != null) {
                iuDesc.setCategoryName(iu.getProperty(SDK.PROP_CATEGORY_DESC));
            }
            if (iu.getProperty(SDK.PROP_DOC_URL) != null) {
                iuDesc.setUnitDocumentationURL(new URL(iu
                        .getProperty(SDK.PROP_DOC_URL)));
            }
            if (iu.getProperty(SDK.PROP_TYPE).equalsIgnoreCase(
                    SDK.ZIPARCHIVE_TYPE)) {
                iuDesc.setArtifactType(EType.ZIP_ARCHIVE);
            } else if (iu.getProperty(SDK.PROP_TYPE).equalsIgnoreCase(
                    SDK.EXECUTABLE_TYPE)) {
                iuDesc.setArtifactType(EType.EXECUTABLE);
            }
            if (iuDesc.getArtifactType().equals(EType.ZIP_ARCHIVE)) {
                List<ITouchpointData> touchpointData = (List<ITouchpointData>) iu.getTouchpointData();
                ITouchpointInstruction inst = touchpointData.get(0)
                        .getInstruction(INSTALL_TOUCHPOINT_KEY);
                String instBody = inst.getBody();
                if (instBody.startsWith(UNZIPEXE_TOUCHPOINT_DATA_PREFIX)) {
                    // get executable path
                    int begin = instBody
                            .indexOf(UNZIPEXE_TOUCHPOINT_DATA_EXECUTABLE)
                            + UNZIPEXE_TOUCHPOINT_DATA_EXECUTABLE.length();
                    int end = instBody.indexOf(')', begin);
                    if (begin > 0 && end > begin) {
                        String exec = instBody.substring(begin, end);
                        if (exec.length() > 0) {
                            iuDesc.setExecutablePath(new Path(exec));
                        } else {
                            throw new Exception(
                                    Messages.GeneratorEngine_ExecutablePathNotFound);
                        }
                    } else {
                        throw new Exception(
                                Messages.GeneratorEngine_ExecutablePathNotFound);
                    }
                }
            }
            IArtifactKey[] arts = (IArtifactKey[]) iu.getArtifacts().toArray();
            iuDesc.setArtifactId(arts[0].getId());
            iuDesc.setArtifactVersion(arts[0].getVersion());

            iuDesc.setUnitCopyright(iu.getCopyright());
            iuDesc.setUnitLicenses(iu.getLicenses());
            repoDesc.addIUDescription(iuDesc);
        }
        return repoDesc;
    }

    /**
     * @param location the root folder where the repositories exist - created if
     *            folder does not exist
     * @param desc the description to save
     * @throws Exception
     * @throws Exception
     */
    public static void saveRespository(IPath location,
            IRepositoryDescription desc) throws Exception {
        try {
            createMetadataRepository(location, desc);
        } catch (Exception e) {
            Activator.logError(Messages.GeneratorEngine_CreateMetadataError, e);
            throw e;
        }
        try {
            createArtifactRepository(location, desc);
        } catch (Exception e) {
            Activator
                    .logError(Messages.GeneratorEngine_CreateArtefactsError, e);
            throw e;
        }
    }

    private static void createMetadataRepository(IPath location,
            IRepositoryDescription desc) throws Exception {
        if (location == null) {
            throw new Exception(Messages.GeneratorEngine_NullPath);
        }
        File dir = location.toFile();

        // create the content.xml
        // delete old content.xml
        File file = location.append(METADATA_XML_NAME).toFile();
        delete(file);

        URI uriDir = dir.toURI();
        IProvisioningAgent agent = P2Utils.getProvisioningAgent(Activator.getContext());
        IMetadataRepositoryManager metadataManager = (IMetadataRepositoryManager) agent.getService(IMetadataRepositoryManager.SERVICE_NAME);
        IMetadataRepository repo = metadataManager.createRepository(uriDir, desc
                .getMetadataRepoName(),
                IMetadataRepositoryManager.TYPE_SIMPLE_REPOSITORY,
                new OrderedProperties());

        // compression flag
        if (desc.isCompressed()) {
            repo.setProperty(IRepository.PROP_COMPRESSED, "true"); //$NON-NLS-1$
        } else {
            repo.setProperty(IRepository.PROP_COMPRESSED, "false"); //$NON-NLS-1$
        }

        // IUs
        Collection<IIUDescription> newIuDescCollection = desc
                .getUnitCollection();
        if (newIuDescCollection != null && newIuDescCollection.size() > 0) {
            Iterator<IIUDescription> iterator = newIuDescCollection.iterator();
            while (iterator.hasNext()) {
                InstallableUnitDescription p2IuDesc = new MetadataFactory.InstallableUnitDescription();
                IIUDescription newIuDesc = iterator.next();
                
                p2IuDesc.setProperty(InstallableUnitDescription.PROP_TYPE_GROUP, Boolean.TRUE.toString());

                // IU Id, version
                String id = ensureGroupId(newIuDesc);
				p2IuDesc.setId(id);
                p2IuDesc.setVersion(newIuDesc.getUnitVersion());
                p2IuDesc.setSingleton(newIuDesc.isSingleton());

                // IU name
                if (newIuDesc.getUnitName() != null)
                    p2IuDesc.setProperty(NAME_PROP, newIuDesc.getUnitName());

                // not using P2 categories - category is part of real IU (not a
                // separate category IU)
                if (newIuDesc.getCategoryName() != null) {
                    p2IuDesc.setProperty(SDK.PROP_CATEGORY, newIuDesc
                            .getCategoryName());
                }
                
                // Category description
                if (newIuDesc.getCategoryDescription() != null) {
                    p2IuDesc.setProperty(SDK.PROP_CATEGORY_DESC, newIuDesc
                            .getCategoryDescription());
                }

                if (newIuDesc.getArtifactDescription() != null) {
                    p2IuDesc.setProperty(SDK.PROP_DESCRIPTION, newIuDesc
                            .getArtifactDescription());
                }

                if (newIuDesc.getUnitDocumentationURL() != null) {
                    p2IuDesc.setProperty(SDK.PROP_DOC_URL, newIuDesc
                            .getUnitDocumentationURL().toString());
                }
                if (newIuDesc.getArtifactType().equals(EType.EXECUTABLE)) {
                    p2IuDesc.setProperty(SDK.PROP_TYPE, SDK.EXECUTABLE_TYPE);
                } else if (newIuDesc.getArtifactType()
                        .equals(EType.ZIP_ARCHIVE)) {
                    p2IuDesc.setProperty(SDK.PROP_TYPE, SDK.ZIPARCHIVE_TYPE);
                }
                // artifacts & touchpoints
                IArtifactKey key = null;
                // touchpoint
                String installData = null;
                if (newIuDesc.getArtifactType().equals(EType.ZIP_ARCHIVE)) {
                    key = new ArtifactKey(UNZIP_ARTIFACT_CLASSIFIER, newIuDesc
                            .getArtifactId(), newIuDesc.getArtifactVersion());
                    if (newIuDesc.getExecutablePath() != null) {
                        installData = UNZIPEXE_TOUCHPOINT_DATA
                                + newIuDesc.getExecutablePath().toString()
                                + ")"; //$NON-NLS-1$
                    } else {
                        installData = UNZIP_TOUCHPOINT_DATA;
                    }
                } else if (newIuDesc.getArtifactType().equals(EType.EXECUTABLE)) {
                    key = new ArtifactKey(EXE_ARTIFACT_CLASSIFIER, newIuDesc
                            .getArtifactId(), newIuDesc.getArtifactVersion());
                    installData = EXE_TOUCHPOINT_DATA;
                }
                p2IuDesc.setArtifacts(new IArtifactKey[] { key });

                p2IuDesc.setTouchpointType(MetadataFactory
                        .createTouchpointType(NATIVE_TOUCHPOINT_TYPE, Version
                                .createOSGi(1, 0, 0)));
                Map<String, String> touchpointData = new HashMap<String, String>();
                touchpointData.put(INSTALL_TOUCHPOINT_KEY, installData);
                p2IuDesc.addTouchpointData(MetadataFactory
                        .createTouchpointData(touchpointData));

                // Self capability Provides
                IProvidedCapability cap = PublisherHelper.createSelfCapability(id, newIuDesc.getUnitVersion());
                Collection<IProvidedCapability> capColl = new ArrayList<IProvidedCapability>();
                capColl.add(cap);
                p2IuDesc.addProvidedCapabilities(capColl);

                // Eula stuff
                if (newIuDesc.getUnitLicense() != null)
                    p2IuDesc.setLicenses((ILicense[]) newIuDesc.getUnitLicense().toArray());
                if (newIuDesc.getUnitCopyright() != null)
                    p2IuDesc.setCopyright(newIuDesc.getUnitCopyright());

                StringBuffer buffer = new StringBuffer();
                String value = null;
                value = newIuDesc.getOs();
                if (value != null && value.length() > 0) {
                    buffer.append(NLS.bind("(osgi.os={0})", value).trim()); //$NON-NLS-1$
                    buffer.append(" "); //$NON-NLS-1$
                }

                value = newIuDesc.getWs();
                if (value != null && value.length() > 0) {
                    buffer.append(NLS.bind("(osgi.ws={0})", value).trim()); //$NON-NLS-1$
                    buffer.append(" "); //$NON-NLS-1$
                }

                value = newIuDesc.getArch();
                if (value != null && value.length() > 0) {
                    buffer.append(NLS.bind("(osgi.arch={0})", value).trim()); //$NON-NLS-1$
                }

                if (buffer.length() > 0) {
                    p2IuDesc.setFilter(NLS.bind("(& {0})", buffer.toString())); //$NON-NLS-1$
                }

                // add this IU
                IInstallableUnit iu = MetadataFactory.createInstallableUnit(p2IuDesc);
                repo.addInstallableUnits(Collections.singletonList(iu));
            }
        }
    }

	private static String ensureGroupId(IIUDescription newIuDesc) {
		String id = newIuDesc.getUnitId();
		if (!id.endsWith(FEATURE_GROUP_ID_SUFFIX))
			id += FEATURE_GROUP_ID_SUFFIX;
		return id;
	}

    private static void createArtifactRepository(IPath location,
            IRepositoryDescription desc) throws Exception {
        if (location == null) {
            throw new Exception(Messages.GeneratorEngine_NullPath);
        }
        File dir = location.toFile();

        // create the artifacts.xml
        // delete old xml
        File file = location.append(ARTIFACTS_XML_NAME).toFile();
        delete(file);

        URI uriDir = dir.toURI();
        IProvisioningAgent agent = P2Utils.getProvisioningAgent(Activator.getContext());
		IArtifactRepositoryManager artifactManager = (IArtifactRepositoryManager) agent .getService(IArtifactRepositoryManager.SERVICE_NAME);
        IArtifactRepository repo = artifactManager.createRepository(uriDir, desc
                .getArtifactRepoName(),
                IArtifactRepositoryManager.TYPE_SIMPLE_REPOSITORY,
                new OrderedProperties());

        // compression flag
        if (desc.isCompressed()) {
            repo.setProperty(IRepository.PROP_COMPRESSED, "true"); //$NON-NLS-1$
        } else {
            repo.setProperty(IRepository.PROP_COMPRESSED, "false"); //$NON-NLS-1$
        }
        // IUs
        Collection<IIUDescription> newIuDescCollection = desc
                .getUnitCollection();
        if (newIuDescCollection != null && newIuDescCollection.size() > 0) {
            Iterator<IIUDescription> iterator = newIuDescCollection.iterator();
            while (iterator.hasNext()) {
                IIUDescription newIuDesc = iterator.next();
                IArtifactKey key = null;
                if (newIuDesc.getArtifactType().equals(EType.ZIP_ARCHIVE)) {
                    key = new ArtifactKey(UNZIP_ARTIFACT_CLASSIFIER, newIuDesc
                            .getArtifactId(), newIuDesc.getArtifactVersion());
                } else if (newIuDesc.getArtifactType().equals(EType.EXECUTABLE)) {
                    key = new ArtifactKey(EXE_ARTIFACT_CLASSIFIER, newIuDesc
                            .getArtifactId(), newIuDesc.getArtifactVersion());
                }
                IPath artifactFile = location
                        .append(desc.getArtifactLocation()).append(
                                newIuDesc.getArtifactId());
                if (desc.getArtifactLocation() == null) {
                    artifactFile = location.append(newIuDesc.getArtifactId());
                }
                File pathOnDisk = artifactFile.toFile();
                IArtifactDescriptor artDesc = PublisherHelper
                        .createArtifactDescriptor(key, pathOnDisk);
                repo.addDescriptor(artDesc);
            }
        }
        // set mapping rules using given artifact folder
        String output = MAPPING_RULE_REPOURL + IPath.SEPARATOR
                + desc.getArtifactLocation() + IPath.SEPARATOR
                + MAPPING_RULE_ID;
        if (desc.getArtifactLocation() == null) {
            output = MAPPING_RULE_REPOURL + IPath.SEPARATOR + MAPPING_RULE_ID;
        }
        String[][] rules = { { UNZIP_MAPPING_CLASSIFIER, output },
                { EXE_MAPPING_CLASSIFIER, output } };
        SimpleArtifactRepository srepo = (SimpleArtifactRepository) repo;
        srepo.setRules(rules);
        srepo.save();
    }
}
