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

package org.eclipse.sequoyah.pulsar.internal.metadata.generator.engine.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

import junit.framework.TestCase;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.equinox.internal.provisional.p2.core.Version;
import org.eclipse.sequoyah.pulsar.core.tests.Activator;
import org.eclipse.sequoyah.pulsar.internal.metadata.generator.engine.GeneratorEngine;
import org.eclipse.sequoyah.pulsar.internal.metadata.generator.engine.IIUDescription;
import org.eclipse.sequoyah.pulsar.internal.metadata.generator.engine.IRepositoryDescription;
import org.eclipse.sequoyah.pulsar.internal.metadata.generator.engine.IUDescription;
import org.eclipse.sequoyah.pulsar.internal.metadata.generator.engine.RepositoryDescription;

import sun.security.krb5.internal.crypto.EType;

/**
 * 
 */
public class TestEngine extends TestCase {

	public TestEngine(String name) {
		super(name);
	}

	/**
	 * @throws java.lang.Exception
	 */
	protected void setUp() throws Exception {
	}

	/**
	 * @param relpath
	 * @return
	 */
	private URL getBundleURL(String relpath) {
		URL[] entries = FileLocator.findEntries(Activator.getDefault().getBundle(), new Path(relpath));
		return entries[0];
	}

	/**
	 * @throws java.lang.Exception
	 */
	protected void tearDown() throws Exception {
	}

	/**
	 * @param destDir
	 * @param destDirArtifacts
	 * @throws Exception 
	 */
	private void copyBundleDataToDestination(IPath destDir,
			IPath destDirArtifacts) throws Exception {
		
		// create destination folders
		File destFile = destDir.toFile();
		if (destFile.exists() == false) {
			destFile.createNewFile();
		}
		if (destDirArtifacts != null) {
			File destArtFile = destDirArtifacts.toFile();
			if (destArtFile.exists() == false) {
				destArtFile.createNewFile();
			}
		}

		// get bundle source folders
		URL muri = getBundleURL("data/test");
		muri = FileLocator.resolve(muri);
		IPath mp = new Path(muri.getPath());
		IPath ap = mp.append("artifactsRepo");

		IPath from = mp.append("content.xml");
		IPath to = destDir.append("content.xml");
		copyFile(from, to);
		
		from = ap.append("artifacts.xml");
		to = destDir.append("artifacts.xml");
		copyFile(from, to);
		
		from = ap.append("artifacts").append("Test.exe");
		if (destDirArtifacts == null) {
			to = destDir.append("Test.exe");
		} else {
			to = destDirArtifacts.append("Test.exe");
		}
		copyFile(from, to);
		
		from = ap.append("artifacts").append("Test.zip");
		if (destDirArtifacts == null) {
			to = destDir.append("Test.zip");
		} else {
			to = destDirArtifacts.append("Test.zip");
		}
		copyFile(from, to);
		
		from = ap.append("artifacts").append("Test.exe.zip");
		if (destDirArtifacts == null) {
			to = destDir.append("Test.exe.zip");
		} else {
			to = destDirArtifacts.append("Test.exe.zip");
		}
		copyFile(from, to);
	}
	/**
	 * @param from
	 * @param to
	 * @throws Exception 
	 */
	private void copyFile(IPath fromPath, IPath toPath) throws Exception {
		File fromFile = fromPath.toFile();
		File toFile = toPath.toFile();
		if (!fromFile.exists()) {
			throw new IOException("no such source file: " + fromFile.getName());
		}
		if (!fromFile.canRead()) {
			throw new IOException("source file is unreadable: " + fromFile.getName());
		}
		if (toFile.exists()) {
			if (!toFile.canWrite()) {
				throw new IOException("destination file is unwriteable: " + toFile.getName());
			}
		}
		FileInputStream from = null;
	    FileOutputStream to = null;
	    try {
	      from = new FileInputStream(fromFile);
	      to = new FileOutputStream(toFile);
	      byte[] buffer = new byte[4096];
	      int bytesRead;

	      while ((bytesRead = from.read(buffer)) != -1)
	        to.write(buffer, 0, bytesRead); // write
	    } finally {
	      if (from != null)
	        try {
	          from.close();
	        } catch (IOException e) {
	          ;
	        }
	      if (to != null)
	        try {
	          to.close();
	        } catch (IOException e) {
	          ;
	        }
	  	}
	}

	public void testEngineSave() throws Exception {
		
		IPath destDir = new Path("c:\\temp\\pulsarTest");
		IPath destDirArtifacts = destDir.append("artifacts");
		copyBundleDataToDestination(destDir, destDirArtifacts);
		
		IPath repoLocation = new Path("c:\\Temp\\pulsarTest");
		IPath artifactLocation = new Path("artifacts");
		IRepositoryDescription desc = new RepositoryDescription();
	
		desc.setArtifactLocation(artifactLocation);
		desc.setRepoLocation(repoLocation);
		desc.setCompressed(false);
		desc.setArtifactRepoName("test repo");
		desc.setMetadataRepoName("test repo");
		desc.removeAllIUDescriptions();
		
		IIUDescription iuDesc = new IUDescription();
		iuDesc.setArtifactId("Test.zip");
		iuDesc.setArtifactType(EType.ZIP_ARCHIVE);
		iuDesc.setArtifactVersion(Version.createOSGi(1,0,0));
		iuDesc.setUnitId("test.unzip");
		iuDesc.setUnitName("Test zip");
		iuDesc.setUnitVersion(Version.createOSGi(1,0,0));
		iuDesc.setUnitCopyright(new URI("http://www.example.com/copyright"), "[Enter Copyright Description here.]");
		iuDesc.setUnitLicense(new URI("http://www.example.com/license"), "[Enter License Description here.]");
		iuDesc.setUnitDocumentationURL(new URL("http://www.yahoo.com"));
		
		desc.addIUDescription(iuDesc);
		
		iuDesc = new IUDescription();
		iuDesc.setArtifactId("Test.exe");
		iuDesc.setArtifactType(EType.EXECUTABLE);
		iuDesc.setArtifactVersion(Version.createOSGi(1,0,0));
		iuDesc.setCategoryName("Test Category");
		iuDesc.setUnitId("test.exe");
		iuDesc.setUnitName("Test exe");
		iuDesc.setUnitVersion(Version.createOSGi(1,0,0));
		iuDesc.setUnitCopyright(new URI("http://www.example.com/copyright"), "[Enter Copyright Description here.]");
		iuDesc.setUnitLicense(new URI("http://www.example.com/license"), "[Enter License Description here.]");
		
		desc.addIUDescription(iuDesc);
		
		iuDesc = new IUDescription();
		iuDesc.setArtifactId("Test.exe.zip");
		iuDesc.setArtifactType(EType.ZIP_ARCHIVE);
		iuDesc.setArtifactVersion(Version.createOSGi(1,0,0));
		iuDesc.setExecutablePath(new Path("Test.exe"));
		iuDesc.setCategoryName("Test Category");
		iuDesc.setUnitId("test.exe.unzip");
		iuDesc.setUnitName("Test zip and exe");
		iuDesc.setUnitVersion(Version.createOSGi(1,0,0));
		iuDesc.setUnitCopyright(new URI("http://www.example.com/copyright"), "[Enter Copyright Description here.]");
		iuDesc.setUnitLicense(new URI("http://www.example.com/license"), "[Enter License Description here.]");
		
		desc.addIUDescription(iuDesc);

		GeneratorEngine.saveRespository(repoLocation, desc);
	}

	public void testEngineLoad() throws Exception {
		IPath repoLocation = new Path("c:\\Temp\\pulsarTest");
		IRepositoryDescription desc = GeneratorEngine.loadRepository(repoLocation);
		assertEquals("artifacts", desc.getArtifactLocation().toString());
		assertEquals(repoLocation, desc.getRepoLocation());
		assertEquals("test repo", desc.getArtifactRepoName());
		assertEquals("test repo", desc.getMetadataRepoName());
		
		assertEquals(false, desc.isCompressed());
		Collection<IIUDescription> IuDescCollection = desc.getUnitCollection();
		assertEquals(3, IuDescCollection.size());
		Iterator<IIUDescription> iterator = IuDescCollection.iterator();
		
		while(iterator.hasNext()) {
			IIUDescription iuDesc = iterator.next();
			if (iuDesc.getArtifactId().equalsIgnoreCase("Test.zip")) {
				assertEquals("Test.zip", iuDesc.getArtifactId());
				assertEquals(EType.ZIP_ARCHIVE, iuDesc.getArtifactType());
				assertEquals(Version.createOSGi(1,0,0).toString(), iuDesc.getArtifactVersion().toString());
				assertNull(iuDesc.getCategoryName());
				assertNull(iuDesc.getExecutablePath());
				assertEquals("test.unzip", iuDesc.getUnitId());
				assertEquals("Test zip", iuDesc.getUnitName());
				assertEquals(Version.createOSGi(1,0,0).toString(), iuDesc.getUnitVersion().toString());
				assertEquals("[Enter Copyright Description here.]", iuDesc.getUnitCopyright().getBody());
				assertEquals("http://www.example.com/copyright", iuDesc.getUnitCopyright().getLocation().toString());
				assertEquals("[Enter License Description here.]", iuDesc.getUnitLicense().getBody());
				assertEquals("http://www.example.com/license", iuDesc.getUnitLicense().getLocation().toString());
				assertEquals(new URL("http://www.yahoo.com"), iuDesc.getUnitDocumentationURL());
			} else if (iuDesc.getArtifactId().equalsIgnoreCase("Text.exe")) {
				assertEquals("Test.exe", iuDesc.getArtifactId());
				assertEquals(EType.EXECUTABLE, iuDesc.getArtifactType());
				assertEquals(Version.createOSGi(1,0,0).toString(), iuDesc.getArtifactVersion().toString());
				assertNotNull(iuDesc.getCategoryName());
				assertNull(iuDesc.getExecutablePath());
				assertEquals("Test Category", iuDesc.getCategoryName());
				assertEquals("test.exe", iuDesc.getUnitId());
				assertEquals("Test exe", iuDesc.getUnitName());
				assertEquals(Version.createOSGi(1,0,0).toString(), iuDesc.getUnitVersion().toString());
				assertEquals("[Enter Copyright Description here.]", iuDesc.getUnitCopyright().getBody());
				assertEquals("http://www.example.com/copyright", iuDesc.getUnitCopyright().getLocation().toString());
				assertEquals("[Enter License Description here.]", iuDesc.getUnitLicense().getBody());
				assertEquals("http://www.example.com/license", iuDesc.getUnitLicense().getLocation().toString());
				assertNull(iuDesc.getUnitDocumentationURL());
			} else if (iuDesc.getArtifactId().equalsIgnoreCase("Test.exe.zip")) {
				assertEquals("Test.exe.zip", iuDesc.getArtifactId());
				assertEquals(EType.ZIP_ARCHIVE, iuDesc.getArtifactType());
				assertEquals(Version.createOSGi(1,0,0).toString(), iuDesc.getArtifactVersion().toString());
				assertEquals(false, iuDesc.isCategory());
				assertEquals("Test.exe", iuDesc.getExecutablePath().toString());
				assertEquals("test.exe.unzip", iuDesc.getUnitId());
				assertEquals("Test zip and exe", iuDesc.getUnitName());
				assertEquals(Version.createOSGi(1,0,0).toString(), iuDesc.getUnitVersion().toString());
				assertEquals("[Enter Copyright Description here.]", iuDesc.getUnitCopyright().getBody());
				assertEquals("http://www.example.com/copyright", iuDesc.getUnitCopyright().getLocation().toString());
				assertEquals("[Enter License Description here.]", iuDesc.getUnitLicense().getBody());
				assertEquals("http://www.example.com/license", iuDesc.getUnitLicense().getLocation().toString());
				assertNull(iuDesc.getUnitDocumentationURL());
			} else {
				assertTrue("unknown artifactId: " + iuDesc.getArtifactId(), true);
			}
		}
	}

	public void testEngineSaveFlat() throws Exception {
		IPath destDir = new Path("c:\\temp\\pulsarTest2");
		IPath destDirArtifacts = null;
		copyBundleDataToDestination(destDir, destDirArtifacts);

		IPath repoLocation = new Path("c:\\Temp\\pulsarTest2");
		IPath artifactLocation = null;		// flat file system
		IRepositoryDescription desc = new RepositoryDescription();
	
		desc.setArtifactLocation(artifactLocation);
		desc.setRepoLocation(repoLocation);
		desc.setCompressed(false);
		desc.setArtifactRepoName("test repo");
		desc.setMetadataRepoName("test repo");
		desc.removeAllIUDescriptions();
		
		IIUDescription iuDesc = new IUDescription();
		iuDesc.setArtifactId("Test.zip");
		iuDesc.setArtifactType(EType.ZIP_ARCHIVE);
		iuDesc.setArtifactVersion(Version.createOSGi(1,0,0));
		iuDesc.setUnitId("test.unzip");
		iuDesc.setUnitName("Test zip");
		iuDesc.setUnitVersion(Version.createOSGi(1,0,0));
		iuDesc.setUnitCopyright(new URI("http://www.example.com/copyright"), "[Enter Copyright Description here.]");
		iuDesc.setUnitLicense(new URI("http://www.example.com/license"), "[Enter License Description here.]");
		iuDesc.setUnitDocumentationURL(new URL("http://www.yahoo.com"));
		
		desc.addIUDescription(iuDesc);
		
		iuDesc = new IUDescription();
		iuDesc.setArtifactId("Test.exe");
		iuDesc.setArtifactType(EType.EXECUTABLE);
		iuDesc.setArtifactVersion(Version.createOSGi(1,0,0));
		iuDesc.setCategoryName("Test Category");
		iuDesc.setUnitId("test.exe");
		iuDesc.setUnitName("Test exe");
		iuDesc.setUnitVersion(Version.createOSGi(1,0,0));
		iuDesc.setUnitCopyright(new URI("http://www.example.com/copyright"), "[Enter Copyright Description here.]");
		iuDesc.setUnitLicense(new URI("http://www.example.com/license"), "[Enter License Description here.]");
		
		desc.addIUDescription(iuDesc);
		
		iuDesc = new IUDescription();
		iuDesc.setArtifactId("Test.exe.zip");
		iuDesc.setArtifactType(EType.ZIP_ARCHIVE);
		iuDesc.setArtifactVersion(Version.createOSGi(1,0,0));
		iuDesc.setExecutablePath(new Path("Test.exe"));
		iuDesc.setCategoryName("Test Category");
		iuDesc.setUnitId("test.exe.unzip");
		iuDesc.setUnitName("Test zip and exe");
		iuDesc.setUnitVersion(Version.createOSGi(1,0,0));
		iuDesc.setUnitCopyright(new URI("http://www.example.com/copyright"), "[Enter Copyright Description here.]");
		iuDesc.setUnitLicense(new URI("http://www.example.com/license"), "[Enter License Description here.]");
		
		desc.addIUDescription(iuDesc);

		GeneratorEngine.saveRespository(repoLocation, desc);
	}
	
	public void testEngineLoadFlat() throws Exception {
		IPath repoLocation = new Path("c:\\Temp\\pulsarTest2");
		IRepositoryDescription desc = GeneratorEngine.loadRepository(repoLocation);
		assertNull(desc.getArtifactLocation());
		assertEquals(repoLocation, desc.getRepoLocation());
		assertEquals("test repo", desc.getArtifactRepoName());
		assertEquals("test repo", desc.getMetadataRepoName());
		
		assertEquals(false, desc.isCompressed());
		Collection<IIUDescription> IuDescCollection = desc.getUnitCollection();
		assertEquals(3, IuDescCollection.size());
		Iterator<IIUDescription> iterator = IuDescCollection.iterator();
		
		while(iterator.hasNext()) {
			IIUDescription iuDesc = iterator.next();
			if (iuDesc.getArtifactId().equalsIgnoreCase("Test.zip")) {
				assertEquals("Test.zip", iuDesc.getArtifactId());
				assertEquals(EType.ZIP_ARCHIVE, iuDesc.getArtifactType());
				assertEquals(Version.createOSGi(1,0,0).toString(), iuDesc.getArtifactVersion().toString());
				assertNull(iuDesc.getCategoryName());
				assertNull(iuDesc.getExecutablePath());
				assertEquals("test.unzip", iuDesc.getUnitId());
				assertEquals("Test zip", iuDesc.getUnitName());
				assertEquals(Version.createOSGi(1,0,0).toString(), iuDesc.getUnitVersion().toString());
				assertEquals("[Enter Copyright Description here.]", iuDesc.getUnitCopyright().getBody());
				assertEquals("http://www.example.com/copyright", iuDesc.getUnitCopyright().getLocation().toString());
				assertEquals("[Enter License Description here.]", iuDesc.getUnitLicense().getBody());
				assertEquals("http://www.example.com/license", iuDesc.getUnitLicense().getLocation().toString());
				assertEquals(new URL("http://www.yahoo.com"), iuDesc.getUnitDocumentationURL());
			} else if (iuDesc.getArtifactId().equalsIgnoreCase("Text.exe")) {
				assertEquals("Test.exe", iuDesc.getArtifactId());
				assertEquals(EType.EXECUTABLE, iuDesc.getArtifactType());
				assertEquals(Version.createOSGi(1,0,0).toString(), iuDesc.getArtifactVersion().toString());
				assertNotNull(iuDesc.getCategoryName());
				assertNull(iuDesc.getExecutablePath());
				assertEquals("Test Category", iuDesc.getCategoryName());
				assertEquals("test.exe", iuDesc.getUnitId());
				assertEquals("Test exe", iuDesc.getUnitName());
				assertEquals(Version.createOSGi(1,0,0).toString(), iuDesc.getUnitVersion().toString());
				assertEquals("[Enter Copyright Description here.]", iuDesc.getUnitCopyright().getBody());
				assertEquals("http://www.example.com/copyright", iuDesc.getUnitCopyright().getLocation().toString());
				assertEquals("[Enter License Description here.]", iuDesc.getUnitLicense().getBody());
				assertEquals("http://www.example.com/license", iuDesc.getUnitLicense().getLocation().toString());
				assertNull(iuDesc.getUnitDocumentationURL());
			} else if (iuDesc.getArtifactId().equalsIgnoreCase("Test.exe.zip")) {
				assertEquals("Test.exe.zip", iuDesc.getArtifactId());
				assertEquals(EType.ZIP_ARCHIVE, iuDesc.getArtifactType());
				assertEquals(Version.createOSGi(1,0,0).toString(), iuDesc.getArtifactVersion().toString());
				assertEquals(false, iuDesc.isCategory());
				assertEquals("Test.exe", iuDesc.getExecutablePath().toString());
				assertEquals("test.exe.unzip", iuDesc.getUnitId());
				assertEquals("Test zip and exe", iuDesc.getUnitName());
				assertEquals(Version.createOSGi(1,0,0).toString(), iuDesc.getUnitVersion().toString());
				assertEquals("[Enter Copyright Description here.]", iuDesc.getUnitCopyright().getBody());
				assertEquals("http://www.example.com/copyright", iuDesc.getUnitCopyright().getLocation().toString());
				assertEquals("[Enter License Description here.]", iuDesc.getUnitLicense().getBody());
				assertEquals("http://www.example.com/license", iuDesc.getUnitLicense().getLocation().toString());
				assertNull(iuDesc.getUnitDocumentationURL());
			} else {
				assertTrue("unknown artifactId: " + iuDesc.getArtifactId(), true);
			}
		}
	}

}
