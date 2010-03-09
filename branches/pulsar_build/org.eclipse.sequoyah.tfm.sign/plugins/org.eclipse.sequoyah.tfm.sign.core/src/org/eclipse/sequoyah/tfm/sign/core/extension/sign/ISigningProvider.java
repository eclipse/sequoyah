/**
 * Copyright (c) 2005,2009 Nokia Corporation and others.

 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Nokia Corporation         - Initial Version
 *     Kevin Horowitz (IBM Corp) - Update javadoc
 *     Diego Sandin (Motorola)   - Porting code to TFM Sign Framework [Bug 286387]
 */
package org.eclipse.sequoyah.tfm.sign.core.extension.sign;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.sequoyah.tfm.sign.core.exception.SignException;
import org.eclipse.sequoyah.tfm.sign.core.extension.IExtension;
import org.eclipse.sequoyah.tfm.sign.core.signing.ISigningCertificate;

/**
 * The Signing Provider provides the interface to sign a project's deployment
 * package. <br>
 * 
 */
public interface ISigningProvider extends IExtension {

    /**
     * Returns whether the SingingProvider plug-in can provide root certificate
     * management features.
     * 
     * @return true if root certificate management features are available, false
     *         otherwise
     * @throws SignException
     */
    public boolean isRootCertToolAvailable() throws SignException;

    /**
     * Attempt to import keystore element (alias) into rootcert. Uses password
     * to access keystore
     * 
     * @param rootcert
     * @param keystore
     * @param alias
     * @param password
     * @throws SignException
     */
    public void importToRootCert(String rootcert, String keystore,
            String alias, String password) throws SignException;

    /**
     * Returns string array populated with the contents of the rootcert.
     * 
     * @param rootcert
     * @return
     * @throws SignException
     */
    public String[] listrootcert(String rootcert) throws SignException;

    /**
     * Attempts to remove an entry from rootcert. References target entry by
     * index (removeindex).
     * 
     * @param rootcert
     * @param removeindex
     * @throws SignException
     */
    public void removeCertFromRoot(String rootcert, int removeindex)
            throws SignException;

    /**
     * Method signs the project with the certificates.
     * 
     * @param project
     * @param targetFolder
     * @param certificates
     * @param monitor
     * @return
     * @throws SignException
     */
    public boolean sign(IProject project, IFolder targetFolder,
            ISigningCertificate certificates, IProgressMonitor monitor)
            throws SignException;

    /**
     * Method unsigns the project.
     * 
     * @param sequoyahProject
     * @param targetFolder
     * @param monitor
     * @return
     * @throws SignException
     */
    public boolean unsign(IProject sequoyahProject, IFolder targetFolder,
            IProgressMonitor monitor) throws SignException;

    /**
     * Method determines and reports if deployment is signed.
     * 
     * @param sequoyahProject
     * @param targetFolder
     * @param monitor
     * @return
     * @throws SignException
     */
    public boolean isSigned(IProject sequoyahProject, IFolder targetFolder,
            IProgressMonitor monitor) throws SignException;

    /**
     * Implementations that rely on an external security tool, are responsible
     * for persistant storage of the tool location value. </br> This method
     * should return the location of the tool. Should never return null.
     * 
     * @param monitor
     * @return
     * @throws SignException
     */
    public String getToolLocation(IProgressMonitor monitor)
            throws SignException;

    /**
     * Implementations that rely on an external security tool, are responsible
     * for persistant storage of the tool location value. This method should
     * contain the code to store the location.
     * 
     * @param loc
     * @param monitor
     * @throws SignException
     */
    public void storeToolLocation(String loc, IProgressMonitor monitor)
            throws SignException;

}
