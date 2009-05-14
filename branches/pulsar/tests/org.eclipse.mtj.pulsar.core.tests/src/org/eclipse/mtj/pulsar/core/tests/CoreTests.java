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
 *  David Marques (Motorola) - Renaming getImageDescriptor method.
 *
 */

package org.eclipse.mtj.pulsar.core.tests;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.equinox.internal.provisional.p2.engine.IProfile;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mtj.internal.provisional.pulsar.core.ISDK;
import org.eclipse.mtj.internal.provisional.pulsar.core.ISDKRepository;
import org.eclipse.mtj.internal.provisional.pulsar.core.ISDKRepositoryProvider;
import org.eclipse.mtj.internal.provisional.pulsar.core.QuickInstallCore;
import org.eclipse.mtj.internal.provisional.pulsar.core.ISDK.EState;
import org.eclipse.mtj.internal.pulsar.core.DefaultSDKRepositoryProvider;
import org.eclipse.mtj.internal.pulsar.core.P2Utils;
import org.eclipse.mtj.internal.pulsar.core.SDK;
import org.eclipse.mtj.pulsar.core.Activator;
import org.eclipse.mtj.pulsar.core.tests.utils.TestUtils;
import org.eclipse.mtj.pulsar.core.tests.utils.TestUtils.Condition;
import org.osgi.framework.Version;

/**
 *
 */
public class CoreTests extends TestCase {

	private static final String OUT_ZIP_DIR = "C:\\Test";
	private static final String OUT_EXE_FILE = "C:\\Test.txt";
	private static final String OUT_ZIP_FILE = "C:\\Test.exe";

	protected void setUp() throws Exception {
	}

	protected void tearDown() throws Exception {
	}

	public void testDefaultSDKRepositoryProvider() throws Exception {
		Collection<ISDKRepositoryProvider> providers = Activator.getDefault().getSDKRepositoryProviders();
		assertFalse(providers.isEmpty());
		ISDKRepositoryProvider provider = null;
		for (ISDKRepositoryProvider p : providers) {
			if (p instanceof DefaultSDKRepositoryProvider) {
				provider = p;
				break;
			}
		}
		assertNotNull(provider);
		Collection<ISDKRepository> repositories = provider.getRepositories();
		assertFalse(repositories.isEmpty());
		boolean found = false;
		for (ISDKRepository repository : repositories) {
			URI uri = repository.getMetadataURI();
			if (uri.toString().contains("nokia.com")) {
				found = true;
				ImageDescriptor descriptor = repository.getIconImageDescriptor();
				assertNotNull(descriptor.getImageData());
			}
		}
		assertTrue(found);
	}
	
	public void testSDKRepository() throws Exception {
		ISDKRepository repository = findTestRepository();
		assertNotNull(repository);
		ImageDescriptor imgdesc = repository.getIconImageDescriptor();
		assertNotNull(imgdesc.getImageData());
		Collection<ISDK> sdks = repository.getSDKs(null);
		assertFalse(sdks.isEmpty());
		for (ISDK sdk : sdks) {
			String sdkName = sdk.getName();
			assertNotNull(sdkName);
			assertTrue(sdk.getVersion().compareTo(Version.parseVersion("0")) > 0);
			String category = sdk.getCategory();
			if (sdkName.endsWith("exe"))
				assertNotNull(category);
			else
				assertNull(category);
			URL url = sdk.getDocumentationURL();
			if (sdkName.endsWith("zip"))
				assertNotNull(url);
			else
				assertNull(url);
		}
	}

	private ISDKRepository findTestRepository() throws CoreException {
		Collection<ISDKRepository> repositories = QuickInstallCore.getInstance().getSDKRepositories();
		ISDKRepository repository = null;
		for (ISDKRepository r : repositories) {
			if (r.getMetadataURI().toString().endsWith("test")) {
				repository = r;
			}
		}
		return repository;
	}
	
	public void testSDKStatus() throws Exception {
		String testProfileId = "test";
		P2Utils.deleteProfile(testProfileId);
		cleanUpInstalledFiles();
		ISDKRepository repository = findTestRepository();
		assertNotNull(repository);
		Collection<ISDK> sdks = repository.getSDKs(null);
		for (ISDK sdk : sdks) {
			assertTrue(sdk.getState().equals(EState.UNINSTALLED));
		}
		IProfile profile = P2Utils.createProfile(testProfileId, new Path("C:\\"));
		assertNotNull(profile);
		for (ISDK sdk : sdks) {
			SDK sdkImpl = (SDK) sdk;
			IStatus status = 
				TestUtils.installIU(profile, sdkImpl.getInstallableUnit(), 
						repository.getMetadataURI(), repository.getArtifactsURI());
			assertTrue(sdk.getName() + ":" + TestUtils.getMessage(status), status.isOK());
			assertTrue(sdk.getState().equals(EState.INSTALLED));
		}
		assertInstalledFilesExist();
		P2Utils.deleteProfile(testProfileId);
		cleanUpInstalledFiles();
	}

	private void assertInstalledFilesExist() throws Exception {
		assertTrue(new File(OUT_ZIP_FILE).exists());
		TestUtils.waitFor(new Condition() {
			public boolean test() {
				return new File(OUT_EXE_FILE).exists();
			}
		});
	}

	private void cleanUpInstalledFiles() {
		new File(OUT_EXE_FILE).delete();
		new File(OUT_ZIP_FILE).delete();
		TestUtils.deleteDir(new File(OUT_ZIP_DIR));
	}
	
	public void testCanInstallMultipleTimes() throws Exception {
		Collection<String> profileIds = new ArrayList<String>();
		cleanUpInstalledFiles();
		ISDKRepository repository = findTestRepository();
		Collection<ISDK> sdks = repository.getSDKs(null);
		assertNotNull(repository);
		for (ISDK sdk : sdks) {
			SDK sdkImpl = (SDK) sdk;
			IProfile profile = P2Utils.createProfileForSDK(sdk, new Path("C:\\"));
			IStatus status = 
				TestUtils.installIU(profile, sdkImpl.getInstallableUnit(), 
						repository.getMetadataURI(), repository.getArtifactsURI());
			profileIds.add(profile.getProfileId());
			assertTrue(sdk.getName() + ":" + TestUtils.getMessage(status), status.isOK());
		}
		for (ISDK sdk : sdks) {
			assertTrue(sdk.getState().equals(EState.INSTALLED));
		}
		assertInstalledFilesExist();
		cleanUpInstalledFiles();
		for (ISDK sdk : sdks) {
			SDK sdkImpl = (SDK) sdk;
			IProfile profile = P2Utils.createProfileForSDK(sdk, new Path("C:\\"));
			IStatus status = 
				TestUtils.installIU(profile, sdkImpl.getInstallableUnit(), 
						repository.getMetadataURI(), repository.getArtifactsURI());
			profileIds.add(profile.getProfileId());
			assertTrue(sdk.getName() + ":" + TestUtils.getMessage(status), status.isOK());
		}
		assertInstalledFilesExist();
		cleanUpInstalledFiles();
		for (String profileId : profileIds) {
			P2Utils.deleteProfile(profileId);
		}
	}
	
	public void testUnzipAndExecute() throws Exception {
		ISDKRepository repository = findTestRepository();
		ISDK sdk = findUnzipAndExecuteSDK(repository);
		assertNotNull(sdk);
		SDK sdkImpl = (SDK) sdk;
		IProfile profile = P2Utils.createProfileForSDK(sdk, new Path("C:\\"));
		IStatus status = 
			TestUtils.installIU(profile, sdkImpl.getInstallableUnit(), 
					repository.getMetadataURI(), repository.getArtifactsURI());
		assertTrue(sdk.getName() + ":" + TestUtils.getMessage(status), status.isOK());
		assertInstalledFilesExist();
		cleanUpInstalledFiles();
		P2Utils.deleteProfile(profile.getProfileId());
	}

	private static ISDK findUnzipAndExecuteSDK(ISDKRepository repository) throws Exception {
		Collection<ISDK> sdks = repository.getSDKs(null);
		for (ISDK sdk : sdks) {
			if (sdk.getName().equals("Test zip and exe"))
				return sdk;
		}
		return null;
	}
}
