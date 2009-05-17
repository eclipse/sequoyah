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
 */

package org.eclipse.mtj.internal.pulsar.metadata.generator.engine;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.equinox.internal.p2.artifact.repository.simple.SimpleArtifactRepository;
import org.eclipse.equinox.internal.p2.console.ProvisioningHelper;
import org.eclipse.equinox.internal.p2.core.helpers.OrderedProperties;
import org.eclipse.equinox.internal.p2.core.helpers.ServiceHelper;
import org.eclipse.equinox.internal.p2.metadata.ArtifactKey;
import org.eclipse.equinox.internal.provisional.p2.artifact.repository.IArtifactDescriptor;
import org.eclipse.equinox.internal.provisional.p2.artifact.repository.IArtifactRepository;
import org.eclipse.equinox.internal.provisional.p2.artifact.repository.IArtifactRepositoryManager;
import org.eclipse.equinox.internal.provisional.p2.core.Version;
import org.eclipse.equinox.internal.provisional.p2.metadata.IArtifactKey;
import org.eclipse.equinox.internal.provisional.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.internal.provisional.p2.metadata.IProvidedCapability;
import org.eclipse.equinox.internal.provisional.p2.metadata.ITouchpointData;
import org.eclipse.equinox.internal.provisional.p2.metadata.ITouchpointInstruction;
import org.eclipse.equinox.internal.provisional.p2.metadata.MetadataFactory;
import org.eclipse.equinox.internal.provisional.p2.metadata.MetadataFactory.InstallableUnitDescription;
import org.eclipse.equinox.internal.provisional.p2.metadata.query.InstallableUnitQuery;
import org.eclipse.equinox.internal.provisional.p2.metadata.repository.IMetadataRepository;
import org.eclipse.equinox.internal.provisional.p2.metadata.repository.IMetadataRepositoryManager;
import org.eclipse.equinox.internal.provisional.p2.query.Collector;
import org.eclipse.equinox.internal.provisional.p2.repository.IRepository;
import org.eclipse.equinox.spi.p2.publisher.PublisherHelper;
import org.eclipse.mtj.internal.provisional.pulsar.core.ISDK.EType;
import org.eclipse.mtj.internal.pulsar.core.SDK;
import org.eclipse.mtj.internal.pulsar.metadata.generator.Messages;
import org.eclipse.mtj.pulsar.core.Activator;
import org.eclipse.osgi.util.NLS;

public class GeneratorEngine implements RepositoryConstants {

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
	public static IRepositoryDescription loadRepository(IPath path) throws Exception {
		IRepositoryDescription desc = null;
		try {
			desc = parseRepository(path);
		} catch (Exception e) {
			Activator.logError(Messages.GeneratorEngine_ParseRepositoryError, e);
			throw e;
		}
		return desc;
	}

	/**
	 * @param path
	 * @return
	 * @throws Exception
	 */
	private static IRepositoryDescription parseRepository(IPath path) throws Exception {
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
		
		IMetadataRepository metaRepo = ProvisioningHelper.getMetadataRepository(uriDir);
		IArtifactRepository artiRepo = ProvisioningHelper.getArtifactRepository(uriDir);
		IRepositoryDescription repoDesc = new RepositoryDescription();
		OrderedProperties props = (OrderedProperties) metaRepo.getProperties();
		if (props.getProperty(IRepository.PROP_COMPRESSED).equalsIgnoreCase("true")) { //$NON-NLS-1$
			repoDesc.setCompressed(true);
		}

		repoDesc.setRepoLocation(path);
		repoDesc.setMetadataRepoName(metaRepo.getName());
		repoDesc.setArtifactRepoName(artiRepo.getName());
		SimpleArtifactRepository srepo = (SimpleArtifactRepository)artiRepo;
		String[][] rules = srepo.getRules();
		String output = rules[0][1];
		int beginIndex = output.indexOf(IPath.SEPARATOR);
		int endIndex = output.lastIndexOf(IPath.SEPARATOR);
		if (beginIndex != endIndex) {
			repoDesc.setArtifactLocation(new Path(output.substring(beginIndex+1, endIndex)));
		}
		Collector ius = ProvisioningHelper.getInstallableUnits(uriDir, InstallableUnitQuery.ANY, new NullProgressMonitor());
		for (IInstallableUnit iu : (Collection<IInstallableUnit>) ius.toCollection()) {
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
			if (iu.getProperty(SDK.PROP_DOC_URL) != null) {
				iuDesc.setUnitDocumentationURL(new URL(iu.getProperty(SDK.PROP_DOC_URL)));
			}
			if (iu.getProperty(SDK.PROP_TYPE).equalsIgnoreCase(SDK.ZIPARCHIVE_TYPE)) {
				iuDesc.setArtifactType(EType.ZIP_ARCHIVE);
			} else if (iu.getProperty(SDK.PROP_TYPE).equalsIgnoreCase(SDK.EXECUTABLE_TYPE)) {
				iuDesc.setArtifactType(EType.EXECUTABLE);
			}
			if (iuDesc.getArtifactType().equals(EType.ZIP_ARCHIVE)) {
				ITouchpointData[] touchpointData = iu.getTouchpointData();
				ITouchpointInstruction inst = touchpointData[0].getInstruction(INSTALL_TOUCHPOINT_KEY);
				String instBody = inst.getBody();
				if (instBody.startsWith(UNZIPEXE_TOUCHPOINT_DATA_PREFIX)) {
					// get executable path
					int begin = instBody.indexOf(UNZIPEXE_TOUCHPOINT_DATA_EXECUTABLE)+UNZIPEXE_TOUCHPOINT_DATA_EXECUTABLE.length();
					int end = instBody.indexOf(')', begin);
					if (begin > 0 && end > begin) {
						String exec = instBody.substring(begin, end);
						if (exec.length() > 0) {
							iuDesc.setExecutablePath(new Path(exec));
						} else {
							throw new Exception(Messages.GeneratorEngine_ExecutablePathNotFound);
						}
					} else {
						throw new Exception(Messages.GeneratorEngine_ExecutablePathNotFound);
					}
				}
			}
			IArtifactKey[] arts = iu.getArtifacts();
			iuDesc.setArtifactId(arts[0].getId());
			iuDesc.setArtifactVersion(arts[0].getVersion());
			
			iuDesc.setUnitCopyright(iu.getCopyright());
			iuDesc.setUnitLicense(iu.getLicense());
			repoDesc.addIUDescription(iuDesc);
		}
		return repoDesc;
	}
	/**
	 * 
	 * @param location the root folder where the repositories exist - created if folder does not exist
	 * @param desc the description to save
	 * @throws Exception 
	 * @throws Exception
	 */
	public static void saveRespository(IPath location, IRepositoryDescription desc) throws Exception {
		try {
			createMetadataRepository(location, desc);
		} catch (Exception e) {
			Activator.logError(Messages.GeneratorEngine_CreateMetadataError, e);
			throw e;
		}
		try {
			createArtifactRepository(location, desc);
		} catch (Exception e) {
			Activator.logError(Messages.GeneratorEngine_CreateArtefactsError, e);
			throw e;
		}
	}

	private static void createMetadataRepository(IPath location, IRepositoryDescription desc) throws Exception {
		if (location == null) {
			throw new Exception(Messages.GeneratorEngine_NullPath);
		}
		File dir = location.toFile();
		
		// create the content.xml
		// delete old content.xml
		File file = location.append(METADATA_XML_NAME).toFile();
		delete(file);
	
		URI uriDir = dir.toURI();
		IMetadataRepositoryManager repoManager = (IMetadataRepositoryManager) ServiceHelper.getService(Activator.getContext(), IMetadataRepositoryManager.class.getName());
		IMetadataRepository	repo = repoManager.createRepository(uriDir, desc.getMetadataRepoName(), IMetadataRepositoryManager.TYPE_SIMPLE_REPOSITORY, new OrderedProperties());
		
		// compression flag
		if (desc.isCompressed()) {
			repo.setProperty(IRepository.PROP_COMPRESSED, "true"); //$NON-NLS-1$
		} else {
			repo.setProperty(IRepository.PROP_COMPRESSED, "false"); //$NON-NLS-1$
		}
		
		// IUs
		Collection<IIUDescription> newIuDescCollection = desc.getUnitCollection();
		if (newIuDescCollection != null && newIuDescCollection.size() > 0) {
			Iterator<IIUDescription> iterator = newIuDescCollection.iterator();
			while(iterator.hasNext()) {
				InstallableUnitDescription p2IuDesc = new MetadataFactory.InstallableUnitDescription();
				IIUDescription newIuDesc = iterator.next();
				
				// IU Id, version
				p2IuDesc.setId(newIuDesc.getUnitId());
				p2IuDesc.setVersion(newIuDesc.getUnitVersion());
				p2IuDesc.setSingleton(newIuDesc.isSingleton());
				
				// IU name
				if (newIuDesc.getUnitName() != null)
					p2IuDesc.setProperty(NAME_PROP, newIuDesc.getUnitName());
				
				// not using P2 categories - category is part of real IU (not a separate category IU)
				if (newIuDesc.getCategoryName() != null) {
					p2IuDesc.setProperty(SDK.PROP_CATEGORY, newIuDesc.getCategoryName());
				}
				
				if(newIuDesc.getArtifactDescription() != null){
					p2IuDesc.setProperty(SDK.PROP_DESCRIPTION, newIuDesc.getArtifactDescription());
				}
				
				if (newIuDesc.getUnitDocumentationURL() != null) {
					p2IuDesc.setProperty(SDK.PROP_DOC_URL, newIuDesc.getUnitDocumentationURL().toString());
				}
				if (newIuDesc.getArtifactType().equals(EType.EXECUTABLE)) {
					p2IuDesc.setProperty(SDK.PROP_TYPE, SDK.EXECUTABLE_TYPE);
				} else if (newIuDesc.getArtifactType().equals(EType.ZIP_ARCHIVE)) {
					p2IuDesc.setProperty(SDK.PROP_TYPE, SDK.ZIPARCHIVE_TYPE);
				}
				// artifacts & touchpoints
				IArtifactKey key = null;
				// touchpoint
				String installData = null;
				if (newIuDesc.getArtifactType().equals(EType.ZIP_ARCHIVE)) {
					key = new ArtifactKey(UNZIP_ARTIFACT_CLASSIFIER, newIuDesc.getArtifactId(), newIuDesc.getArtifactVersion());
					if (newIuDesc.getExecutablePath() != null) {
						installData = UNZIPEXE_TOUCHPOINT_DATA + newIuDesc.getExecutablePath().toString() + ")"; //$NON-NLS-1$
					} else {
						installData = UNZIP_TOUCHPOINT_DATA;
					}
				}
				else if (newIuDesc.getArtifactType().equals(EType.EXECUTABLE)) {
					key = new ArtifactKey(EXE_ARTIFACT_CLASSIFIER, newIuDesc.getArtifactId(), newIuDesc.getArtifactVersion());
					installData = EXE_TOUCHPOINT_DATA;
				}
				p2IuDesc.setArtifacts(new IArtifactKey[] {key});

				p2IuDesc.setTouchpointType(MetadataFactory.createTouchpointType(NATIVE_TOUCHPOINT_TYPE, Version.createOSGi(1,0,0)));
				Map<String, String> touchpointData = new HashMap<String, String>();
				touchpointData.put(INSTALL_TOUCHPOINT_KEY, installData);
				p2IuDesc.addTouchpointData(MetadataFactory.createTouchpointData(touchpointData));

				// Self capability Provides
				IProvidedCapability cap = PublisherHelper.createSelfCapability(newIuDesc.getUnitId(), newIuDesc.getUnitVersion());
				Collection<IProvidedCapability> capColl = new ArrayList<IProvidedCapability>();
				capColl.add(cap);
				p2IuDesc.addProvidedCapabilities(capColl);
				
				// Eula stuff
				if (newIuDesc.getUnitLicense() != null)
					p2IuDesc.setLicense(newIuDesc.getUnitLicense());
				if (newIuDesc.getUnitCopyright() != null)
					p2IuDesc.setCopyright(newIuDesc.getUnitCopyright());
				
				StringBuffer buffer = new StringBuffer();
				String value = null;
				value = newIuDesc.getOs();
				if (value != null && value.length() > 0) {
					buffer.append(NLS.bind("(osgi.os={0})", value));
					buffer.append(" ");
				}
				
				value = newIuDesc.getWs();
				if (value != null && value.length() > 0) {
					buffer.append(NLS.bind("(osgi.ws={0})", value));
					buffer.append(" ");
				}
				
				value = newIuDesc.getArch();
				if (value != null && value.length() > 0) {
					buffer.append(NLS.bind("(osgi.arch={0})", value));
				}
				
				if (buffer.length() > 0) {					
					p2IuDesc.setFilter(NLS.bind("({0})", buffer.toString()));
				}
				
				// add this IU
				IInstallableUnit iu = MetadataFactory.createInstallableUnit(p2IuDesc);
				repo.addInstallableUnits(new IInstallableUnit[] {iu});
			}
		}
	}
	private static void createArtifactRepository(IPath location, IRepositoryDescription desc) throws Exception {
		if (location == null) {
			throw new Exception(Messages.GeneratorEngine_NullPath);
		}
		File dir = location.toFile();
		
		// create the artifacts.xml
		// delete old xml
		File file = location.append(ARTIFACTS_XML_NAME).toFile();
		delete(file);
	
		URI uriDir = dir.toURI();
		IArtifactRepositoryManager repoManager = (IArtifactRepositoryManager) ServiceHelper.getService(Activator.getContext(), IArtifactRepositoryManager.class.getName());
		IArtifactRepository	repo = repoManager.createRepository(uriDir, desc.getArtifactRepoName(), IArtifactRepositoryManager.TYPE_SIMPLE_REPOSITORY, new OrderedProperties());
		
		// compression flag
		if (desc.isCompressed()) {
			repo.setProperty(IRepository.PROP_COMPRESSED, "true"); //$NON-NLS-1$
		} else {
			repo.setProperty(IRepository.PROP_COMPRESSED, "false"); //$NON-NLS-1$
		}
		// IUs
		Collection<IIUDescription> newIuDescCollection = desc.getUnitCollection();
		if (newIuDescCollection != null && newIuDescCollection.size() > 0) {
			Iterator<IIUDescription> iterator = newIuDescCollection.iterator();
			while(iterator.hasNext()) {
				IIUDescription newIuDesc = iterator.next();
				IArtifactKey key = null;
				if (newIuDesc.getArtifactType().equals(EType.ZIP_ARCHIVE)) {
					key = new ArtifactKey(UNZIP_ARTIFACT_CLASSIFIER, newIuDesc.getArtifactId(), newIuDesc.getArtifactVersion());
				}
				else if (newIuDesc.getArtifactType().equals(EType.EXECUTABLE)) {
					key = new ArtifactKey(EXE_ARTIFACT_CLASSIFIER, newIuDesc.getArtifactId(), newIuDesc.getArtifactVersion());
				}
				IPath artifactFile = location.append(desc.getArtifactLocation()).append(newIuDesc.getArtifactId());
				if (desc.getArtifactLocation() == null) {
					artifactFile = location.append(newIuDesc.getArtifactId());
				}
				File pathOnDisk = artifactFile.toFile();
				IArtifactDescriptor artDesc = PublisherHelper.createArtifactDescriptor(key, pathOnDisk);
				repo.addDescriptor(artDesc);
			}
		}
		// set mapping rules using given artifact folder
		String output = MAPPING_RULE_REPOURL + IPath.SEPARATOR + desc.getArtifactLocation() + IPath.SEPARATOR + MAPPING_RULE_ID;
		if (desc.getArtifactLocation() == null) {
			output = MAPPING_RULE_REPOURL + IPath.SEPARATOR + MAPPING_RULE_ID;
		}
		String[][] rules = {{UNZIP_MAPPING_CLASSIFIER, output},{EXE_MAPPING_CLASSIFIER, output}};
		SimpleArtifactRepository srepo = (SimpleArtifactRepository) repo;
		srepo.setRules(rules);
		srepo.save();	
	}
}
