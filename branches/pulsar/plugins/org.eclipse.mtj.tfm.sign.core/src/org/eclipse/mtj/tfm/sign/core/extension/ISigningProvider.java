/*******************************************************************************
 * Copyright (c) 2005,2006 Nokia Corporation and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Additional Contributors -
 *  	Kevin Horowitz (IBM Corp) - Update javadoc
 *******************************************************************************/
package org.eclipse.mtj.tfm.sign.core.extension;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mtj.tfm.sign.core.exception.SignException;
import org.eclipse.mtj.tfm.sign.core.signing.ISigningCertificate;

/**
 * The Signing Provider provides the interface to sign a project's deployment
 * package. <br>
 * 
 * @model
 */
public interface ISigningProvider extends ISignExtension {

    /**
     * Returns whether the SingingProvider plug-in can provide root certificate
     * management features.
     * 
     * @return true if root certificate management features are available, false
     *         otherwise
     * @throws SignException
     * @model
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
     * @model
     */
    public void importToRootCert(String rootcert, String keystore,
            String alias, String password) throws SignException;

    /**
     * Returns string array populated with the contents of the rootcert.
     * 
     * @param rootcert
     * @return
     * @throws SignException
     * @model
     */
    public String[] listrootcert(String rootcert) throws SignException;

    /**
     * Attempts to remove an entry from rootcert. References target entry by
     * index (removeindex).
     * 
     * @param rootcert
     * @param removeindex
     * @throws SignException
     * @model
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
     * @model
     */
    public boolean sign(IProject project, IFolder targetFolder,
            ISigningCertificate certificates, IProgressMonitor monitor)
            throws SignException;

    /**
     * Method unsigns the project.
     * 
     * @param mtjProject
     * @param targetFolder
     * @param monitor
     * @return
     * @throws SignException
     * @model
     */
    public boolean unsign(IProject mtjProject, IFolder targetFolder,
            IProgressMonitor monitor) throws SignException;

    /**
     * Method determines and reports if deployment is signed.
     * 
     * @param mtjProject
     * @param targetFolder
     * @param monitor
     * @return
     * @throws SignException
     * @model
     */
    public boolean isSigned(IProject mtjProject, IFolder targetFolder,
            IProgressMonitor monitor) throws SignException;

    /**
     * Implementations that rely on an external security tool, are responsible
     * for persistant storage of the tool location value. </br> This method
     * should return the location of the tool. Should never return null.
     * 
     * @param monitor
     * @return
     * @throws SignException
     * @model
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
     * @model
     */
    public void storeToolLocation(String loc, IProgressMonitor monitor)
            throws SignException;

}
