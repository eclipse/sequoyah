/**
 * Copyright (c) 2006,2009 IBM Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation         - initial API and implementation
 *     Diego Sandin (Motorola) - Porting code to TFM Sign Framework [Bug 286387]
 */
package org.eclipse.mtj.tfm.sign.smgmt.ibm.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.mtj.tfm.internal.sign.core.extension.SignExtensionImpl;
import org.eclipse.mtj.tfm.sign.core.SignErrors;
import org.eclipse.mtj.tfm.sign.core.enumerations.ExtensionType;
import org.eclipse.mtj.tfm.sign.core.exception.SignException;
import org.eclipse.mtj.tfm.sign.core.extension.ISecurityManagement;
import org.eclipse.mtj.tfm.sign.smgmt.ibm.IBMSmgmtCore;
import org.osgi.framework.Version;

public class SecurityManagementImpl extends SignExtensionImpl implements
        ISecurityManagement {

    /**
     * @return
     */
    private static String getConsoleEncoding() {
        return "-J-Dconsole.encoding=" + System.getProperty("file.encoding"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    private String aliaskey = null;
    private String keyStoreNameLoc = ""; //$NON-NLS-1$
    private String passwrd = null;
    private IPreferenceStore securityProviderPrefStore;

    // private variables
    private String storeType = "JKS"; //$NON-NLS-1$
    private String validity = "365"; //$NON-NLS-1$

    /**
     * Creates a new instance of SecurityManagementImpl.
     */
    public SecurityManagementImpl() {
        super();
        setId(IBMSmgmtCore.getDefault().getBundle().getSymbolicName());
        setVendor(Messages.SecurityManagementImpl_PluginVendor);
        setVersion(new Version(Messages.SecurityManagementImpl_PluginVersion));
        setDescription(Messages.SecurityManagementImpl_Security_manager);
        setType(ExtensionType.SECURITY_MANAGEMENT);
        securityProviderPrefStore = IBMSmgmtCore.getDefault()
                .getPreferenceStore();
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.ISecurityManagement#changeStorePassword(java.lang.String, java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
     */
    public boolean changeStorePassword(String newStorePass, String storePass,
            IProgressMonitor monitor) throws SignException {

        boolean cmdSuccessful = true;
        String[] cmdArgs = generateChangeStorePasswordCmd(newStorePass,
                storePass);
        Process p = runSecurityCmd(cmdArgs);

        BufferedReader cmdOutputStream = new BufferedReader(
                new InputStreamReader(p.getInputStream()));

        String cmdOutput;
        try {
            while ((cmdOutput = cmdOutputStream.readLine()) != null) {

                if (cmdOutput.toLowerCase().indexOf("error") >= 0) { //$NON-NLS-1$
                    //MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),"Error", cmdOutput);  //$NON-NLS-1$
                    // cmdSuccessful = false;
                    throw new SignException(SignErrors
                            .getErrorMessage(SignErrors.GENERIC_SECURITY_ERROR)
                            + " " + cmdOutput); //$NON-NLS-1$
                }
            }
        } catch (IOException ee) {
            throw new SignException(SignErrors
                    .getErrorMessage(SignErrors.GENERIC_SECURITY_ERROR), ee);
        }
        return cmdSuccessful;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.ISecurityManagement#createNewKey(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
     */
    public boolean createNewKey(String alias, String commonName,
            String orgUnit, String orgName, String localityName,
            String stateName, String country, IProgressMonitor monitor)
            throws SignException {
        monitor.beginTask(Messages.SecurityManagementImpl_Creating_key_alias,
                100);

        boolean cmdSuccessful = true;
        String Dname = generateDname(commonName, orgUnit, orgName,
                localityName, stateName, country);
        String[] cmdArgs = generateNewKeyCmd(alias, Dname, "RSA", "SHA1withRSA"); //$NON-NLS-1$ //$NON-NLS-2$
        Process p = runSecurityCmd(cmdArgs);
        monitor.worked(30);

        BufferedReader cmdOutputStream = new BufferedReader(
                new InputStreamReader(p.getInputStream()));

        String cmdOutput;
        try {
            while ((cmdOutput = cmdOutputStream.readLine()) != null) {
                monitor.worked(40);
                if (cmdOutput.toLowerCase().indexOf("error") >= 0) { //$NON-NLS-1$
                    //MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error", cmdOutput); //$NON-NLS-1$
                    // cmdSuccessful = false;
                    monitor.done();
                    if (cmdOutput.toLowerCase().indexOf(
                            "alias <" + alias + "> already exists") >= 0) { //$NON-NLS-1$ //$NON-NLS-2$
                        throw new SignException(
                                SignErrors
                                        .getErrorMessage(SignErrors.SECURITY_ALIAS_DUPLICATE)
                                        + " " + cmdOutput); //$NON-NLS-1$
                    } else {
                        throw new SignException(
                                SignErrors
                                        .getErrorMessage(SignErrors.GENERIC_SECURITY_ERROR)
                                        + " " + cmdOutput); //$NON-NLS-1$
                    }
                }
            }
        } catch (IOException ee) {
            throw new SignException(SignErrors
                    .getErrorMessage(SignErrors.GENERIC_SECURITY_ERROR), ee);
        }
        monitor.worked(100);
        monitor.done();
        return cmdSuccessful;

    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.ISecurityManagement#deleteKey(org.eclipse.core.runtime.IProgressMonitor)
     */
    public boolean deleteKey(IProgressMonitor monitor) throws SignException {

        boolean cmdSuccessful = true;
        String[] cmdArgs = generateDeleteKeyCmd();
        Process p = runSecurityCmd(cmdArgs);

        BufferedReader cmdOutputStream = new BufferedReader(
                new InputStreamReader(p.getInputStream()));

        String cmdOutput;
        try {
            while ((cmdOutput = cmdOutputStream.readLine()) != null) {

                if (cmdOutput.toLowerCase().indexOf("error") >= 0) { //$NON-NLS-1$
                    //MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error", cmdOutput); //$NON-NLS-1$
                    // cmdSuccessful = false;
                    throw new SignException(SignErrors
                            .getErrorMessage(SignErrors.GENERIC_SECURITY_ERROR)
                            + " " + cmdOutput); //$NON-NLS-1$

                }
            }
        } catch (IOException ee) {
            throw new SignException(SignErrors
                    .getErrorMessage(SignErrors.GENERIC_SECURITY_ERROR), ee);
        }
        return cmdSuccessful;

    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.ISecurityManagement#generateCSR(java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
     */
    public boolean generateCSR(String certFile, IProgressMonitor monitor)
            throws SignException {

        boolean cmdSuccessful = true;
        String[] cmdArgs = generateGenerateCSRCmd(certFile);
        Process p = runSecurityCmd(cmdArgs);

        BufferedReader cmdOutputStream = new BufferedReader(
                new InputStreamReader(p.getInputStream()));

        String cmdOutput;
        try {
            while ((cmdOutput = cmdOutputStream.readLine()) != null) {

                if (cmdOutput.toLowerCase().indexOf("error") >= 0) { //$NON-NLS-1$
                    //MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error", cmdOutput); //$NON-NLS-1$
                    // cmdSuccessful = false;
                    throw new SignException(SignErrors
                            .getErrorMessage(SignErrors.GENERIC_SECURITY_ERROR)
                            + " " + cmdOutput); //$NON-NLS-1$
                }
            }
        } catch (IOException ee) {
            throw new SignException(SignErrors
                    .getErrorMessage(SignErrors.GENERIC_SECURITY_ERROR), ee);
        }
        return cmdSuccessful;

    }

    /**
     * Generating the Distinguished Name (dname) string using the given user
     * input.
     * 
     * @param commonName
     * @param orgUnit
     * @param orgName
     * @param localityName
     * @param stateName
     * @param country
     * @return
     */
    public String generateDname(String commonName, String orgUnit,
            String orgName, String localityName, String stateName,
            String country) {
        String Dname = SecurityManagementImplConstants.QUOTE
                + SecurityManagementImplConstants.COMMON_NAME_PREFIX
                + commonName + SecurityManagementImplConstants.COMMA_AND_SPACE
                + SecurityManagementImplConstants.ORGANIZATION_UNIT_PREFIX
                + orgUnit + SecurityManagementImplConstants.COMMA_AND_SPACE
                + SecurityManagementImplConstants.ORGANIZATION_NAME_PREFIX
                + orgName + SecurityManagementImplConstants.COMMA_AND_SPACE
                + SecurityManagementImplConstants.LOCALITY_NAME_PREFIX
                + localityName
                + SecurityManagementImplConstants.COMMA_AND_SPACE
                + SecurityManagementImplConstants.STATE_NAME_PREFIX + stateName
                + SecurityManagementImplConstants.COMMA_AND_SPACE
                + SecurityManagementImplConstants.COUNTRY_PREFIX + country
                + SecurityManagementImplConstants.QUOTE;
        return Dname;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.ISecurityManagement#getAliaskey()
     */
    public String getAliaskey() throws SignException {
        return aliaskey;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.ISecurityManagement#getCertificateInfo(org.eclipse.core.runtime.IProgressMonitor)
     */
    public String getCertificateInfo(IProgressMonitor monitor)
            throws SignException {

        String certInfo = ""; //$NON-NLS-1$

        if ((aliaskey != null) && (aliaskey.length() > 0)) {

            try {
                String[] cmdArgs = generateDisplayCertifcates();
                Process p = runSecurityCmd(cmdArgs);
                BufferedReader cmdOutputStream = new BufferedReader(
                        new InputStreamReader(p.getInputStream()));
                monitor.worked(20);

                String cmdOutput;

                while ((cmdOutput = cmdOutputStream.readLine()) != null) {

                    if (cmdOutput.toLowerCase().indexOf("error") >= 0) { //$NON-NLS-1$
                        //MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error", cmdOutput);  //$NON-NLS-1$
                        //return ""; //$NON-NLS-1$
                        throw new SignException(
                                SignErrors
                                        .getErrorMessage(SignErrors.GENERIC_SECURITY_ERROR)
                                        + " " + cmdOutput); //$NON-NLS-1$
                    } else if (cmdOutput.length() >= 0) {
                        certInfo = certInfo + cmdOutput;
                    }
                }

            } catch (IOException ee) {
                certInfo = ""; //$NON-NLS-1$
                throw new SignException(SignErrors
                        .getErrorMessage(SignErrors.GENERIC_SECURITY_ERROR), ee);
            } catch (Exception e) {
                certInfo = ""; //$NON-NLS-1$
                throw new SignException(SignErrors
                        .getErrorMessage(SignErrors.GENERIC_SECURITY_ERROR), e);
            }
        }// if aliaskey

        return certInfo;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.ISecurityManagement#getKeyStoreNameLoc()
     */
    public String getKeyStoreNameLoc() throws SignException {
        return keyStoreNameLoc;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.ISecurityManagement#getPassWrd()
     */
    public String getPassWrd() throws SignException {
        return passwrd;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.ISecurityManagement#getStoreType()
     */
    public String getStoreType() throws SignException {
        return storeType;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.ISecurityManagement#getToolLocation(org.eclipse.core.runtime.IProgressMonitor)
     */
    public String getToolLocation(IProgressMonitor monitor)
            throws SignException {
        return securityProviderPrefStore
                .getString(SecurityManagementImplConstants.SECURITY_TOOL_LOCATION);
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.ISecurityManagement#getValidity()
     */
    public String getValidity() throws SignException {
        return validity;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.ISecurityManagement#importSignedCert(java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
     */
    public boolean importSignedCert(String certFile, IProgressMonitor monitor)
            throws SignException {

        boolean cmdSuccessful = true;
        String[] cmdArgs = generateImportSignedCertCmd(certFile);
        Process p = runSecurityCmd(cmdArgs);

        BufferedReader cmdOutputStream = new BufferedReader(
                new InputStreamReader(p.getInputStream()));

        String cmdOutput;
        try {
            while ((cmdOutput = cmdOutputStream.readLine()) != null) {

                if (cmdOutput.indexOf("error") >= 0) { //$NON-NLS-1$
                    //MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error", cmdOutput);  //$NON-NLS-1$
                    // cmdSuccessful = false;
                    throw new SignException(SignErrors
                            .getErrorMessage(SignErrors.GENERIC_SECURITY_ERROR)
                            + cmdOutput);
                }
            }
        } catch (IOException ee) {
            throw new SignException(SignErrors
                    .getErrorMessage(SignErrors.GENERIC_SECURITY_ERROR), ee);
        }
        return cmdSuccessful;
    }

    /**
     * isKeyStoreSelected - user of this class will specify the keystore
     * name/location to manage.
     * 
     * @return true if a keystore name and location was set during this session.
     */
    public boolean isKeyStoreSelected() throws SignException {

        if ((keyStoreNameLoc == null) || (keyStoreNameLoc.length() <= 0))
            return false;

        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.ISecurityManagement#openKeyStore(java.lang.String, java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
     */
    public String[] openKeyStore(String keyStore, String storePass,
            IProgressMonitor monitor) throws SignException {
        monitor.beginTask(Messages.SecurityManagementImpl_Opening_key_store,
                100);
        monitor.worked(10);
        BufferedReader cmdOutputStream;
        String[] cmdArgs = generateOpenKeyStoreCmd(keyStore, storePass);
        Process p = runSecurityCmd(cmdArgs);
        if (p != null) {
            cmdOutputStream = new BufferedReader(new InputStreamReader(p
                    .getInputStream()));
        } else {
            StringBuffer str = new StringBuffer(""); //$NON-NLS-1$
            for (int i = 0; i < cmdArgs.length; i++) {
                str.append(" " + cmdArgs[i]); //$NON-NLS-1$
            }

            throw new SignException(SignErrors
                    .getErrorMessage(SignErrors.GENERIC_SECURITY_ERROR)
                    + Messages.SecurityManagementImpl_Could_not_execute
                    + " [" + str + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        String cmdOutput;
        // ArrayList aliases = new ArrayList();
        List<String> aliases = new ArrayList<String>();

        try {
            while ((cmdOutput = cmdOutputStream.readLine()) != null) {
                monitor.subTask(cmdOutput);
                monitor.worked(25);
                if (cmdOutput.toLowerCase().indexOf("error") >= 0) { //$NON-NLS-1$
                    monitor.done();
                    if (cmdOutput.toLowerCase().indexOf(
                            "invalid keystore format") >= 0) { //$NON-NLS-1$
                        throw new SignException(
                                SignErrors
                                        .getErrorMessage(SignErrors.SECURITY_BAD_KEY_TYPE)
                                        + " " + cmdOutput); //$NON-NLS-1$
                    } else if (cmdOutput.toLowerCase().indexOf(
                            "password was incorrect") >= 0) { //$NON-NLS-1$
                        throw new SignException(
                                SignErrors
                                        .getErrorMessage(SignErrors.SECURITY_BAD_KEYSTORE_OR_PASSWORD)
                                        + " " + cmdOutput); //$NON-NLS-1$
                    } else {
                        throw new SignException(
                                SignErrors
                                        .getErrorMessage(SignErrors.GENERIC_SECURITY_ERROR)
                                        + " " + cmdOutput); //$NON-NLS-1$	
                    }
                } else if (cmdOutput.indexOf(",") >= 0) { //$NON-NLS-1$
                    StringTokenizer strtok = new StringTokenizer(cmdOutput,
                            ".,"); //$NON-NLS-1$
                    aliases.add(strtok.nextToken());
                    // aliases.add(strtok.nextToken());
                }
            }
        } catch (IOException ee) {
            throw new SignException(SignErrors
                    .getErrorMessage(SignErrors.GENERIC_SECURITY_ERROR), ee);
        }
        monitor.worked(100);
        monitor.done();
        // return (String[])aliases.toArray(new String[aliases.size()]);
        return (String[]) aliases.toArray(new String[aliases.size()]);
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.ISecurityManagement#resetValues()
     */
    public void resetValues() {


    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.ISecurityManagement#setAliaskey(java.lang.String)
     */
    public void setAliaskey(String aliasKey) throws SignException {
        this.aliaskey = aliasKey;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.ISecurityManagement#setKeyStoreNameLoc(java.lang.String)
     */
    public void setKeyStoreNameLoc(String keyStoreNameLoc) throws SignException {
        this.keyStoreNameLoc = keyStoreNameLoc;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.ISecurityManagement#setPassWrd(java.lang.String)
     */
    public void setPassWrd(String passWrd) throws SignException {
        this.passwrd = passWrd;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.ISecurityManagement#setStoreType(java.lang.String)
     */
    public void setStoreType(String storeType) throws SignException {
        this.storeType = storeType;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.ISecurityManagement#setValidity(java.lang.String)
     */
    public void setValidity(String validity) throws SignException {
        this.validity = validity;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.ISecurityManagement#setValues(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void setValues(String loc, String alias, String psswd, String strtype)
            throws SignException {

        storeType = strtype;
        aliaskey = alias;
        passwrd = psswd;
        keyStoreNameLoc = loc;

    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.ISecurityManagement#storeToolLocation(java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void storeToolLocation(String loc, IProgressMonitor monitor)
            throws SignException {
        securityProviderPrefStore.setValue(
                SecurityManagementImplConstants.SECURITY_TOOL_LOCATION, loc);

    }

    /**
     * Generating the command to change the key store password.
     * 
     * @param newStorePass
     * @param storePasswd
     * @return
     * @throws SignException
     */
    private String[] generateChangeStorePasswordCmd(String newStorePass,
            String storePasswd) throws SignException {

        String[] changeStorePasswordCmdArgs = { getSecurityManagementTool(),
                getConsoleEncoding(),
                SecurityManagementImplConstants.CHANGE_STORE_PASSWD,
                SecurityManagementImplConstants.NEWSTOREPASS, newStorePass,
                SecurityManagementImplConstants.STORETYPE, storeType,
                SecurityManagementImplConstants.KEYSTORE, keyStoreNameLoc,
                SecurityManagementImplConstants.STOREPASS, storePasswd };

        return changeStorePasswordCmdArgs;
    }

    /**
     * @return
     * @throws SignException
     */
    private String[] generateDeleteKeyCmd() throws SignException {

        String[] deleteKeyCmdArgs = { getSecurityManagementTool(),
                getConsoleEncoding(),
                SecurityManagementImplConstants.DELETE_KEY,
                SecurityManagementImplConstants.ALIAS, aliaskey,
                SecurityManagementImplConstants.STORETYPE, storeType,
                SecurityManagementImplConstants.KEYSTORE, keyStoreNameLoc,
                SecurityManagementImplConstants.STOREPASS, passwrd };

        return deleteKeyCmdArgs;
    }

    /**
     * generateDisplayCertifcates - Command to display all certificates for a
     * given key (alias)
     * 
     * @param alias - alias key for which the certificate info is being
     *            requested.
     * @param storePasswd - password used to open keystore
     * @return
     * @throws SignException
     */
    private String[] generateDisplayCertifcates() throws SignException {

        String[] listCertificateCmdArgs = { getSecurityManagementTool(),
                getConsoleEncoding(), SecurityManagementImplConstants.LIST,
                SecurityManagementImplConstants.ALIAS, aliaskey,
                SecurityManagementImplConstants.STORETYPE, storeType,
                SecurityManagementImplConstants.KEYSTORE, keyStoreNameLoc,
                SecurityManagementImplConstants.STOREPASS, passwrd };

        return listCertificateCmdArgs;
    }

    /**
     * @param certFile
     * @return
     * @throws SignException
     */
    private String[] generateGenerateCSRCmd(String certFile)
            throws SignException {

        String[] generateCSRCmdArgs = { getSecurityManagementTool(),
                getConsoleEncoding(),
                SecurityManagementImplConstants.GENERATE_CSR,
                SecurityManagementImplConstants.ALIAS, aliaskey,
                SecurityManagementImplConstants.FILE, certFile,
                SecurityManagementImplConstants.STORETYPE, storeType,
                SecurityManagementImplConstants.KEYSTORE, keyStoreNameLoc,
                SecurityManagementImplConstants.STOREPASS, passwrd };

        return generateCSRCmdArgs;
    }

    /**
     * @param certFile
     * @return
     * @throws SignException
     */
    private String[] generateImportSignedCertCmd(String certFile)
            throws SignException {

        String[] importSignedCertCmdArgs = { getSecurityManagementTool(),
                getConsoleEncoding(),
                SecurityManagementImplConstants.IMPORT_CERT,
                SecurityManagementImplConstants.NOPROMPT,
                SecurityManagementImplConstants.ALIAS, aliaskey,
                SecurityManagementImplConstants.KEYPASS, passwrd,
                SecurityManagementImplConstants.FILE, certFile,
                SecurityManagementImplConstants.STORETYPE, storeType,
                SecurityManagementImplConstants.KEYSTORE, keyStoreNameLoc,
                SecurityManagementImplConstants.STOREPASS, passwrd };

        return importSignedCertCmdArgs;
    }

    /**
     * @param alias
     * @param dname
     * @param keyAlg
     * @param sigAlg
     * @return
     * @throws SignException
     */
    private String[] generateNewKeyCmd(String alias, String dname,
            String keyAlg, String sigAlg) throws SignException {

        String[] newKeyCmdArgs = { getSecurityManagementTool(),
                getConsoleEncoding(),
                SecurityManagementImplConstants.GENERATE_KEY,
                SecurityManagementImplConstants.ALIAS, alias,
                SecurityManagementImplConstants.DNAME, dname,
                SecurityManagementImplConstants.KEYPASS, passwrd,
                SecurityManagementImplConstants.STORETYPE, storeType,
                SecurityManagementImplConstants.KEYALG, keyAlg,
                SecurityManagementImplConstants.SIGALG, sigAlg,
                SecurityManagementImplConstants.KEYSTORE, keyStoreNameLoc,
                SecurityManagementImplConstants.STOREPASS, passwrd,
                SecurityManagementImplConstants.VALIDITY, validity };

        return newKeyCmdArgs;
    }

    /**
     * Generating the command to Open a Key Store and display its contents.
     * 
     * @param keyStore
     * @param storePasswd
     * @return
     * @throws SignException
     */
    private String[] generateOpenKeyStoreCmd(String keyStore, String storePasswd)
            throws SignException {

        String[] openKeyStoreCmdArgs = { getSecurityManagementTool(),
                getConsoleEncoding(), SecurityManagementImplConstants.LIST,
                SecurityManagementImplConstants.STORETYPE, storeType,
                SecurityManagementImplConstants.KEYSTORE, keyStore,
                SecurityManagementImplConstants.STOREPASS, storePasswd };
        return openKeyStoreCmdArgs;
    }

    /**
     * Get the Security Management tool from pref store location.
     * 
     * 
     * @return
     * @throws SignException
     */
    private final String getSecurityManagementTool() throws SignException {
        String securityToolLocation = getToolLocation(null);

        if (securityToolLocation == null
                || securityToolLocation.length() <= 0
                || securityToolLocation
                        .equals(Messages.SecurityManagementImpl_Specify_home_directory)) {
            String message = MessageFormat
                    .format(
                            Messages.SecurityManagementImpl_GetSecurityManagmentException,
                            new Object[] {
                                    getId(),
                                    Messages.SecurityManagementImpl_Security_tool_not_configured_correctly,
                                    Messages.SecurityManagementImpl_6 });
            throw new SignException(
                    SignErrors
                            .getErrorMessage(SignErrors.SECURITY_MANAGER_NOT_CONFIGURED)
                            + "\n" + message); //$NON-NLS-1$
        }

        StringBuffer buffer = new StringBuffer("\"");//$NON-NLS-1$
        buffer.append(securityToolLocation).append(File.separator)
                .append("bin") //$NON-NLS-1$						
                .append(File.separator).append("keytool.exe") //$NON-NLS-1$
                .append("\"");//$NON-NLS-1$

        return buffer.toString();
    }

    /**
     * @param cmd
     * @return
     * @throws SignException
     */
    private Process runSecurityCmd(String[] cmd) throws SignException {

        Process p = null;

        try {
            p = Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            throw new SignException(SignErrors
                    .getErrorMessage(SignErrors.GENERIC_SECURITY_ERROR));
        }

        if (p == null) {
            StringBuffer str = new StringBuffer(""); //$NON-NLS-1$

            for (int i = 0; i < cmd.length; i++) {
                str.append(" " + cmd[i]); //$NON-NLS-1$
            }

            throw new SignException(SignErrors
                    .getErrorMessage(SignErrors.GENERIC_SECURITY_ERROR)
                    + Messages.SecurityManagementImpl_Could_not_execute
                    + " [" + str + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        return p;
    }
}