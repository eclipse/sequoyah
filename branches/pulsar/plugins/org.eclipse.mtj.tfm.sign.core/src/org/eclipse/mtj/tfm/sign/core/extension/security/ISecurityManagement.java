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
package org.eclipse.mtj.tfm.sign.core.extension.security;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mtj.tfm.sign.core.exception.SignException;
import org.eclipse.mtj.tfm.sign.core.extension.IExtension;

/**
 * The Security Management Provider provides the interface to interact with Key
 * Stores.
 * 
 * @since 1.0
 */
public interface ISecurityManagement extends IExtension {

    /**
     * Accesses an existing keystore, opens the keystore and returns a string
     * array containing the key aliases within the keystore.
     * 
     * @param keyStore
     * @param storePass
     * @param monitor
     * @return String[]
     * @throws SignException
     */
    public String[] openKeyStore(String keyStore, String storePass,
            IProgressMonitor monitor) throws SignException;

    /**
     * Returns the keystore type used by keystore.
     * 
     * @return String
     * @throws SignException
     */
    public String getStoreType() throws SignException;

    /**
     * Returns the current key alias. Calling classes will set this value based
     * on user selection from the keystore.
     * 
     * @return key alias that is currently being used.
     * @throws SignException
     */
    public String getAliaskey() throws SignException;

    /**
     * Returns the current keystore password.
     * 
     * @return
     * @throws SignException
     */
    public String getPassWrd() throws SignException;

    /**
     * Returns the location of the current keystore.
     * 
     * @return
     * @throws SignException
     */
    public String getKeyStoreNameLoc() throws SignException;

    /**
     * Returns the validity of the current key pair.
     * 
     * @return
     * @throws SignException
     */
    public String getValidity() throws SignException;

    /**
     * Sets the type of keystore to generate.
     * 
     * @param storeType
     * @throws SignException
     */
    public void setStoreType(String storeType) throws SignException;

    /**
     * Sets the alias for the current key pair.
     * 
     * @param aliasKey
     * @throws SignException
     */
    public void setAliaskey(String aliasKey) throws SignException;

    /**
     * Sets the password for the current keystore.
     * 
     * @param passWrd
     * @throws SignException
     */
    public void setPassWrd(String passWrd) throws SignException;

    /**
     * Sets the Key Store name location.
     * 
     * @param keyStoreNameLoc
     * @throws SignException
     */
    public void setKeyStoreNameLoc(String keyStoreNameLoc) throws SignException;

    /**
     * Sets the validity period for the certificate.
     * 
     * @param validity
     * @throws SignException
     */
    public void setValidity(String validity) throws SignException;

    /**
     * Allows users of this class to set the keystore values.
     * 
     * @param loc
     * @param alias
     * @param psswd
     * @param strtype
     * @throws SignException
     */
    public void setValues(String loc, String alias, String psswd, String strtype)
            throws SignException;

    /**
     * Resets all values including keystore, password, alias key, etc
     * 
     * @throws SignException
     */
    public void resetValues() throws SignException;

    /**
     * Return true if the keystore name and location were set during this
     * session.
     * 
     * @return true if a keystore name and location were set during this
     *         session, false otherwise.
     */
    public boolean isKeyStoreSelected() throws SignException;

    /**
     * Generates a new CSR.
     * 
     * @param certFile - location and name of file to generate
     * @param monitor
     * @return - True if success, otherwise false.
     * @throws SignException
     */
    public boolean generateCSR(String certFile, IProgressMonitor monitor)
            throws SignException;

    /**
     * Imports signed certificate to current keystore.
     * 
     * @param certFile - location of signed certificate to import
     * @param monitor
     * @return - True if success, otherwise false.
     * @throws SignException
     */
    public boolean importSignedCert(String certFile, IProgressMonitor monitor)
            throws SignException;

    /**
     * Deletes the current key pair, from the current keystore
     * 
     * @param monitor
     * @return - True if success, otherwise false.
     * @throws SignException
     */
    public boolean deleteKey(IProgressMonitor monitor) throws SignException;

    /**
     * Changes the password of the current keystore.
     * 
     * @param newStorePass - Changes the keystore password to newStorePass.
     * @param storePass - Previous keystore password.
     * @param monitor
     * @return - True if success, otherwise false.
     * @throws SignException
     */
    public boolean changeStorePassword(String newStorePass, String storePass,
            IProgressMonitor monitor) throws SignException;

    /**
     * Creates a new key pair with the information passed in. Attaches the new
     * key pair to the current keystore.
     * 
     * @param alias - New key pair alias.
     * @param commonName - New key pair common name.
     * @param orgUnit - New key pair organization unit name.
     * @param orgName - New key pair organization name.
     * @param localityName - New key pair locality name.
     * @param stateName - New key pair state name.
     * @param country - New key pair country name.
     * @param monitor - Progress monitor.
     * @return - true is success, otherwise false.
     * @throws SignException
     */
    public boolean createNewKey(String alias, String commonName,
            String orgUnit, String orgName, String localityName,
            String stateName, String country, IProgressMonitor monitor)
            throws SignException;

    /**
     * Get the certificates associated with the alias key/keystore.
     * 
     * @param monitor
     * @return
     * @throws SignException
     */
    public String getCertificateInfo(IProgressMonitor monitor)
            throws SignException;

    /**
     * Implementations that rely on an external security tool, are responsible
     * for persistent storage of the tool location value. This method should
     * return the location of the tool. Should never return null.
     * 
     * @param monitor
     * @return - Tool location.
     * @throws SignException
     */
    public String getToolLocation(IProgressMonitor monitor)
            throws SignException;

    /**
     * Implementations that rely on an external security tool, are responsible
     * for persistent storage of the tool location value. This method should
     * contain the code to store the location.
     * 
     * @param loc - Directory where tool resides.
     * @param monitor
     * @throws SignException
     */
    public void storeToolLocation(String loc, IProgressMonitor monitor)
            throws SignException;
}
