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
package org.eclipse.mtj.tfm.sign.smgmt.ibm;

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
import org.eclipse.mtj.tfm.internal.sign.smgmt.ibm.Messages;
import org.eclipse.mtj.tfm.internal.sign.smgmt.ibm.IBMSmgmtConstants;
import org.eclipse.mtj.tfm.sign.core.SignErrors;
import org.eclipse.mtj.tfm.sign.core.enumerations.ExtensionType;
import org.eclipse.mtj.tfm.sign.core.exception.SignException;
import org.eclipse.mtj.tfm.sign.core.extension.ExtensionImpl;
import org.eclipse.mtj.tfm.sign.core.extension.security.ISecurityManagement;
import org.eclipse.mtj.tfm.sign.core.extension.security.X500DName;
import org.osgi.framework.Version;

/**
 * @since 1.0
 */
public class IBMSecurityManagement extends ExtensionImpl implements
        ISecurityManagement {

    /**
     * Return the line to be used to configure Console Encoding.
     * 
     * @return The character encoding for the console.
     */
    private static String getConsoleEncoding() {
        return "-J-Dconsole.encoding=" + System.getProperty("file.encoding"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private String aliaskey = null;
    
    /** The certificate will be valid for 365 days. */
    private String ksCertfValidity = "365"; //$NON-NLS-1$
    
    /** The keystore location in the file system. */
    private String ksLocation = ""; //$NON-NLS-1$
    
    /** Keystore Password */
    private String ksPasswrd = null;

    /** Java KeyStore (JKS) */
    private String ksType = "JKS"; //$NON-NLS-1$
    
    /**
     * Preference store for this SecurityManagement plug-in. This preference
     * store is used to hold persistent settings for this plug-in in the context
     * of a workbench.
     */
    private IPreferenceStore securityProviderPrefStore;

    /**
     * Creates a new instance of IBMSecurityManagement.<br>
     * <br>
     * <b>ID</b>: <i>org.eclipse.mtj.tfm.sign.smgmt.ibm</i><br>
     * <b>Vendor</b>: <i>Eclipse.org - DSDP</i><br>
     * <b>Version</b>: <i>1.0.0</i><br>
     * <b>Description</b>: <i>Security Manager for IBM's JRE (v.5.0.0) Keytool.</i><br>
     * <b>Type</b>: <i>{@link ExtensionType#SECURITY_MANAGEMENT
     * SECURITY_MANAGEMENT}</i><br>
     */
    public IBMSecurityManagement() {
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
     * @see org.eclipse.mtj.tfm.sign.core.extension.security.ISecurityManagement#changeStorePassword(java.lang.String, java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
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

    public boolean createNewKey(String alias, String commonName,
            String orgUnit, String orgName, String localityName,
            String stateName, String country, IProgressMonitor monitor)
            throws SignException {
        monitor.beginTask(Messages.SecurityManagementImpl_Creating_key_alias,
                100);

        boolean cmdSuccessful = true;
        String Dname = new X500DName(commonName, orgUnit, orgName,
                localityName, stateName, country).toString();
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
     * @see org.eclipse.mtj.tfm.sign.core.extension.security.ISecurityManagement#deleteKey(org.eclipse.core.runtime.IProgressMonitor)
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
     * @see org.eclipse.mtj.tfm.sign.core.extension.security.ISecurityManagement#generateCSR(java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
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

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.security.ISecurityManagement#getAliaskey()
     */
    public String getAliaskey() throws SignException {
        return aliaskey;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.security.ISecurityManagement#getCertificateInfo(org.eclipse.core.runtime.IProgressMonitor)
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
     * @see org.eclipse.mtj.tfm.sign.core.extension.security.ISecurityManagement#getKeyStoreNameLoc()
     */
    public String getKeyStoreNameLoc() throws SignException {
        return ksLocation;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.security.ISecurityManagement#getPassWrd()
     */
    public String getPassWrd() throws SignException {
        return ksPasswrd;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.security.ISecurityManagement#getStoreType()
     */
    public String getStoreType() throws SignException {
        return ksType;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.security.ISecurityManagement#getToolLocation(org.eclipse.core.runtime.IProgressMonitor)
     */
    public String getToolLocation(IProgressMonitor monitor)
            throws SignException {
        return securityProviderPrefStore
                .getString(IBMSmgmtConstants.SECURITY_TOOL_LOCATION);
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.security.ISecurityManagement#getValidity()
     */
    public String getValidity() throws SignException {
        return ksCertfValidity;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.security.ISecurityManagement#importSignedCert(java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
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

        if ((ksLocation == null) || (ksLocation.length() <= 0)) {
            return false;
        }

        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.security.ISecurityManagement#openKeyStore(java.lang.String, java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
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
            for (String cmdArg : cmdArgs) {
                str.append(" " + cmdArg); //$NON-NLS-1$
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
        return aliases.toArray(new String[aliases.size()]);
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.security.ISecurityManagement#resetValues()
     */
    public void resetValues() {

    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.security.ISecurityManagement#setAliaskey(java.lang.String)
     */
    public void setAliaskey(String aliasKey) throws SignException {
        aliaskey = aliasKey;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.security.ISecurityManagement#setKeyStoreNameLoc(java.lang.String)
     */
    public void setKeyStoreNameLoc(String keyStoreNameLoc) throws SignException {
        this.ksLocation = keyStoreNameLoc;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.security.ISecurityManagement#setPassWrd(java.lang.String)
     */
    public void setPassWrd(String passWrd) throws SignException {
        ksPasswrd = passWrd;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.security.ISecurityManagement#setStoreType(java.lang.String)
     */
    public void setStoreType(String storeType) throws SignException {
        this.ksType = storeType;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.security.ISecurityManagement#setValidity(java.lang.String)
     */
    public void setValidity(String validity) throws SignException {
        this.ksCertfValidity = validity;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.security.ISecurityManagement#setValues(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void setValues(String loc, String alias, String psswd, String strtype)
            throws SignException {

        ksType = strtype;
        aliaskey = alias;
        ksPasswrd = psswd;
        ksLocation = loc;

    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.security.ISecurityManagement#storeToolLocation(java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void storeToolLocation(String loc, IProgressMonitor monitor)
            throws SignException {
        securityProviderPrefStore.setValue(
                IBMSmgmtConstants.SECURITY_TOOL_LOCATION, loc);

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
                getConsoleEncoding(), IBMSmgmtConstants.CHANGE_STORE_PASSWD,
                IBMSmgmtConstants.NEWSTOREPASS, newStorePass,
                IBMSmgmtConstants.STORETYPE, ksType,
                IBMSmgmtConstants.KEYSTORE, ksLocation,
                IBMSmgmtConstants.STOREPASS, storePasswd };

        return changeStorePasswordCmdArgs;
    }

    /**
     * @return
     * @throws SignException
     */
    private String[] generateDeleteKeyCmd() throws SignException {

        String[] deleteKeyCmdArgs = { getSecurityManagementTool(),
                getConsoleEncoding(), IBMSmgmtConstants.DELETE_KEY,
                IBMSmgmtConstants.ALIAS, aliaskey, IBMSmgmtConstants.STORETYPE,
                ksType, IBMSmgmtConstants.KEYSTORE, ksLocation,
                IBMSmgmtConstants.STOREPASS, ksPasswrd };

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
                getConsoleEncoding(), IBMSmgmtConstants.LIST,
                IBMSmgmtConstants.ALIAS, aliaskey, IBMSmgmtConstants.STORETYPE,
                ksType, IBMSmgmtConstants.KEYSTORE, ksLocation,
                IBMSmgmtConstants.STOREPASS, ksPasswrd };

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
                getConsoleEncoding(), IBMSmgmtConstants.GENERATE_CSR,
                IBMSmgmtConstants.ALIAS, aliaskey, IBMSmgmtConstants.FILE,
                certFile, IBMSmgmtConstants.STORETYPE, ksType,
                IBMSmgmtConstants.KEYSTORE, ksLocation,
                IBMSmgmtConstants.STOREPASS, ksPasswrd };

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
                getConsoleEncoding(), IBMSmgmtConstants.IMPORT_CERT,
                IBMSmgmtConstants.NOPROMPT, IBMSmgmtConstants.ALIAS, aliaskey,
                IBMSmgmtConstants.KEYPASS, ksPasswrd, IBMSmgmtConstants.FILE,
                certFile, IBMSmgmtConstants.STORETYPE, ksType,
                IBMSmgmtConstants.KEYSTORE, ksLocation,
                IBMSmgmtConstants.STOREPASS, ksPasswrd };

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
                getConsoleEncoding(), IBMSmgmtConstants.GENERATE_KEY,
                IBMSmgmtConstants.ALIAS, alias, IBMSmgmtConstants.DNAME, dname,
                IBMSmgmtConstants.KEYPASS, ksPasswrd,
                IBMSmgmtConstants.STORETYPE, ksType,
                IBMSmgmtConstants.KEYALG, keyAlg, IBMSmgmtConstants.SIGALG,
                sigAlg, IBMSmgmtConstants.KEYSTORE, ksLocation,
                IBMSmgmtConstants.STOREPASS, ksPasswrd,
                IBMSmgmtConstants.VALIDITY, ksCertfValidity };

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
                getConsoleEncoding(), IBMSmgmtConstants.LIST,
                IBMSmgmtConstants.STORETYPE, ksType,
                IBMSmgmtConstants.KEYSTORE, keyStore,
                IBMSmgmtConstants.STOREPASS, storePasswd };
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

        if ((securityToolLocation == null)
                || (securityToolLocation.length() <= 0)
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

            for (String element : cmd) {
                str.append(" " + element); //$NON-NLS-1$
            }

            throw new SignException(SignErrors
                    .getErrorMessage(SignErrors.GENERIC_SECURITY_ERROR)
                    + Messages.SecurityManagementImpl_Could_not_execute
                    + " [" + str + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        return p;
    }
}