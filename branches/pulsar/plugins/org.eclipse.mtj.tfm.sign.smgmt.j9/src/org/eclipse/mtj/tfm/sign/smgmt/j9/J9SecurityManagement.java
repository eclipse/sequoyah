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
 *     Rodrigo Pastrana        - Updates to parameters for command line execution
 *     Diego Sandin (Motorola) - Porting code to TFM Sign Framework [Bug 286387]
 */
package org.eclipse.mtj.tfm.sign.smgmt.j9;

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
import org.eclipse.mtj.tfm.internal.sign.smgmt.j9.J9SmgmtConstants;
import org.eclipse.mtj.tfm.internal.sign.smgmt.j9.Messages;
import org.eclipse.mtj.tfm.sign.core.SignErrors;
import org.eclipse.mtj.tfm.sign.core.enumerations.ExtensionType;
import org.eclipse.mtj.tfm.sign.core.exception.SignException;
import org.eclipse.mtj.tfm.sign.core.extension.ExtensionImpl;
import org.eclipse.mtj.tfm.sign.core.extension.security.ISecurityManagement;
import org.eclipse.mtj.tfm.sign.core.extension.security.X500DName;
import org.osgi.framework.Version;

/**
 * @author Rodrigo Pastrana
 * @since 1.0
 */
public class J9SecurityManagement extends ExtensionImpl implements
        ISecurityManagement {

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
     * Creates a new instance of J9SecurityManagement.<br>
     * <br>
     * <b>ID</b>: <i>org.eclipse.mtj.tfm.sign.smgmt.j9</i><br>
     * <b>Vendor</b>: <i>DSDP - Eclipse.org</i><br>
     * <b>Version</b>: <i>1.0.0</i><br>
     * <b>Description</b>: <i>Security Manager for IBM's WebSphere Everyplace
     * Micro Environment keytool.</i><br>
     * <b>Type</b>: <i>{@link ExtensionType#SECURITY_MANAGEMENT
     * SECURITY_MANAGEMENT}</i><br>
     */
    public J9SecurityManagement() {
        super();

        setId(J9SmgmtCore.getDefault().getBundle().getSymbolicName());
        setVendor(Messages.J9SecurityManager_PluginVendor);
        setVersion(new Version(Messages.J9SecurityManager_PluginVersion));
        setDescription(Messages.J9SecurityManager_Description);
        setType(ExtensionType.SECURITY_MANAGEMENT);
        securityProviderPrefStore = J9SmgmtCore.getDefault()
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
     * @see org.eclipse.mtj.tfm.sign.core.extension.security.ISecurityManagement#createNewKey(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
     */
    public boolean createNewKey(String alias, String commonName,
            String orgUnit, String orgName, String localityName,
            String stateName, String country, IProgressMonitor monitor)
            throws SignException {

        monitor.beginTask(Messages.J9SecurityManager_Creating_key_alias, 100);

        boolean cmdSuccessful = true;
        String Dname = new X500DName(commonName, orgUnit, orgName,
                localityName, stateName, country).toString();

        String[] cmdArgs = generateNewKeyCmd(alias, Dname, "RSA", "SHA1withRSA"); //$NON-NLS-1$ //$NON-NLS-2$
        Process p = runSecurityCmd(cmdArgs);

        monitor.worked(10);

        BufferedReader cmdOutputStream = new BufferedReader(
                new InputStreamReader(p.getInputStream()));

        String cmdOutput;
        try {
            if ((cmdOutput = cmdOutputStream.readLine()) != null) {
                monitor.worked(40);
                if (cmdOutput.toLowerCase().indexOf("error") >= 0) { //$NON-NLS-1$
                    monitor.done();
                    if (cmdOutput.toLowerCase().indexOf(
                            "keystore contains the given alias") >= 0) { //$NON-NLS-1$
                        throw new SignException(cmdOutput);
                    }
                    throw new SignException(cmdOutput);
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

                String cmdOutput;

                while ((cmdOutput = cmdOutputStream.readLine()) != null) {

                    if (cmdOutput.toLowerCase().indexOf("error") >= 0) { //$NON-NLS-1$
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
                .getString(J9SmgmtConstants.SECURITY_TOOL_LOCATION);
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

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.security.ISecurityManagement#isKeyStoreSelected()
     */
    public boolean isKeyStoreSelected() throws SignException {

        if ((ksLocation == null) || (ksLocation.length() <= 0))
            return false;

        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.security.ISecurityManagement#openKeyStore(java.lang.String, java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
     */
    public String[] openKeyStore(String keyStore, String storePass,
            IProgressMonitor monitor) throws SignException {
        monitor.beginTask(Messages.J9SecurityManager_Opening_keystore, 100);

        String[] cmdArgs = generateOpenKeyStoreCmd(keyStore, storePass);
        Process p = runSecurityCmd(cmdArgs);

        BufferedReader cmdOutputStream = new BufferedReader(
                new InputStreamReader(p.getInputStream()));

        String cmdOutput;
        List<String> aliases = new ArrayList<String>();

        monitor.worked(50);
        try {
            while ((cmdOutput = cmdOutputStream.readLine()) != null) {

                if (cmdOutput.toLowerCase().indexOf("error") >= 0) { //$NON-NLS-1$
                    monitor.done();
                    if (cmdOutput
                            .toLowerCase()
                            .indexOf(
                                    "Keystore password should have at least 6 characters") >= 0) { //$NON-NLS-1$
                        throw new SignException(
                                SignErrors
                                        .getErrorMessage(SignErrors.SECURITY_MALFORMED_PASSWORD)
                                        + cmdOutput);
                    } else if (cmdOutput.toLowerCase().indexOf(
                            "Check the file path and password.") >= 0) { //$NON-NLS-1$
                        throw new SignException(
                                SignErrors
                                        .getErrorMessage(SignErrors.SECURITY_BAD_KEYSTORE_OR_PASSWORD)
                                        + cmdOutput);
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
                }
            }
        } catch (IOException ee) {
            throw new SignException(SignErrors
                    .getErrorMessage(SignErrors.GENERIC_SECURITY_ERROR), ee);
        }
        monitor.worked(100);
        monitor.done();
        return (String[]) aliases.toArray(new String[aliases.size()]);
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.security.ISecurityManagement#resetValues()
     */
    public void resetValues() throws SignException {
        // TODO Reset the values to default
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.security.ISecurityManagement#setAliaskey(java.lang.String)
     */
    public void setAliaskey(String aliasKey) throws SignException {
        this.aliaskey = aliasKey;
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
        this.ksPasswrd = passWrd;
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
                J9SmgmtConstants.SECURITY_TOOL_LOCATION, loc);
    }

    /**
     * Create the command line to change the password used to protect the
     * integrity of the keystore contents.
     * 
     * @param newStorePass the new keystore password. <b>Restriction:</b>The
     *            password must be at least 6 characters long.
     * @param storePasswd the current keystore password
     * @return the generated command line string in the following format: <br>
     *         <code>
     *         &lt;keytool Path&gt; 
     *         -storepasswd -new <b>newStorePass</b> -storetype {@link #ksType} -keystore {@link #ksLocation} -storepass
     *        <b>storePasswd</b></code>
     * @throws SignException in case fails to get the Security Management tool
     *             location in the file system.
     */
    private String[] generateChangeStorePasswordCmd(String newStorePass,
            String storePasswd) throws SignException {

        String[] changeStorePasswordCmdArgs = { getSecurityManagementTool(),
                J9SmgmtConstants.CHANGE_STORE_PASSWD,
                J9SmgmtConstants.NEWSTOREPASS, newStorePass,
                J9SmgmtConstants.STORETYPE, ksType, J9SmgmtConstants.KEYSTORE,
                ksLocation, J9SmgmtConstants.STOREPASS, storePasswd };

        return changeStorePasswordCmdArgs;
    }

    /**
     * Create the command line to delete from the keystore the entry identified
     * by {@link #aliaskey}.
     * 
     * @return the generated command line string in the following format: <br>
     *         <code>
     *         &lt;keytool Path&gt;  
     *         -delete -alias {@link #aliaskey} -storetype {@link #ksType} -keystore {@link #ksLocation} -storepass
     *        {@link #ksPasswrd}</code>
     * @throws SignException in case fails to get the Security Management tool
     *             location in the file system.
     */
    private String[] generateDeleteKeyCmd() throws SignException {

        String[] deleteKeyCmdArgs = { getSecurityManagementTool(),
                J9SmgmtConstants.DELETE_KEY, J9SmgmtConstants.ALIAS, aliaskey,
                J9SmgmtConstants.STORETYPE, ksType, J9SmgmtConstants.KEYSTORE,
                ksLocation, J9SmgmtConstants.STOREPASS, ksPasswrd };

        return deleteKeyCmdArgs;
    }

    /**
     * Create the command line to display the contents of the keystore entry
     * identified by {@link #aliaskey}.
     * 
     * @return the generated command line string in the following format: <br>
     *         <code>
     *         &lt;keytool Path&gt;
     *         -list -alias {@link #aliaskey} -storetype {@link #ksType} 
     *         -keystore {@link #ksLocation} -storepass
     *        {@link #ksPasswrd}</code>
     * @throws SignException in case fails to get the Security Management tool
     *             location in the file system.
     */
    private String[] generateDisplayCertifcates() throws SignException {

        String[] listCertificateCmdArgs = { getSecurityManagementTool(),
                J9SmgmtConstants.LIST, J9SmgmtConstants.ALIAS, aliaskey,
                J9SmgmtConstants.STORETYPE, ksType, J9SmgmtConstants.KEYSTORE,
                ksLocation, J9SmgmtConstants.STOREPASS, ksPasswrd };

        return listCertificateCmdArgs;
    }

    /**
     * Create the command line to read from the keystore the certificate
     * associated with {@link #aliaskey}, and stores it in the file certFile.
     * 
     * @param certFile where to store the certificate associated with
     *            {@link #aliaskey}.
     * @return the generated command line string in the following format: <br>
     *         <code>
     *         &lt;keytool Path&gt;
     *         -export -alias {@link #aliaskey} -file <b>certFile</b> 
     *         -storetype {@link #ksType} -keystore {@link #ksLocation} -storepass
     *         {@link #ksPasswrd}</code>
     * @throws SignException in case fails to get the Security Management tool
     *             location in the file system.
     */
    private String[] generateGenerateCSRCmd(String certFile)
            throws SignException {

        String[] generateCSRCmdArgs = { getSecurityManagementTool(),
                J9SmgmtConstants.GENERATE_CSR, J9SmgmtConstants.ALIAS,
                aliaskey, J9SmgmtConstants.FILE, certFile,
                J9SmgmtConstants.STORETYPE, ksType, J9SmgmtConstants.KEYSTORE,
                ksLocation, J9SmgmtConstants.STOREPASS, ksPasswrd };

        return generateCSRCmdArgs;
    }

    /**
     * Create the command line to read the certificate from the file certFile,
     * and stores it in the keystore entry identified by {@link #aliaskey}.
     * 
     * @param certFile the file where the certificate is stored.
     * @return the generated command line string in the following format: <br>
     *         <code>
     *         &lt;keytool Path&gt; 
     *         -import -noprompt -alias {@link #aliaskey} -keypass {@link #ksPasswrd} 
     *         -file <b>certFile</b> -storetype {@link #ksType} -keystore {@link #ksLocation} 
     *         -storepass {@link #ksPasswrd}</code>
     * @throws SignException in case fails to get the Security Management tool
     *             location in the file system.
     */
    private String[] generateImportSignedCertCmd(String certFile)
            throws SignException {

        String[] importSignedCertCmdArgs = { getSecurityManagementTool(),
                J9SmgmtConstants.IMPORT_CERT, J9SmgmtConstants.NOPROMPT,
                J9SmgmtConstants.ALIAS, aliaskey, J9SmgmtConstants.KEYPASS,
                ksPasswrd, J9SmgmtConstants.FILE, certFile,
                J9SmgmtConstants.STORETYPE, ksType, J9SmgmtConstants.KEYSTORE,
                ksLocation, J9SmgmtConstants.STOREPASS, ksPasswrd };

        return importSignedCertCmdArgs;
    }

    /**
     * Create the command line to generate a key pair (a public key and
     * associated private key).
     * 
     * @param alias keypair alias
     * @param dname specifies the X.500 Distinguished Name to be associated with
     *            alias, and is used as the issuer and subject fields in the
     *            self-signed certificate.
     * @param keyAlg specifies the algorithm to be used to generate the key
     *            pair.
     * @param sigAlg specifies the algorithm that should be used to sign the
     *            self-signed certificate; this algorithm must be compatible
     *            with keyalg.
     * @return the generated command line string in the following format: <br>
     *         <code>
     *         &lt;keytool Path&gt; -genkey -alias <b>alias</b> -dname <b>dname</b>
     *         -keypass {@link #ksPasswrd} -storetype {@link #ksType} -keyalg
     *         <b>keyAlg</b> -keystore {@link #ksLocation} -storepass
     *         {@link #ksPasswrd} -validity {@link #ksCertfValidity}</code>
     * @throws SignException in case fails to get the Security Management tool
     *             location in the file system.
     */
    private String[] generateNewKeyCmd(String alias, String dname,
            String keyAlg, String sigAlg) throws SignException {

        String[] newKeyCmdArgs = { getSecurityManagementTool(),
                J9SmgmtConstants.GENERATE_KEY, J9SmgmtConstants.ALIAS, alias,
                J9SmgmtConstants.DNAME, dname, J9SmgmtConstants.KEYPASS,
                ksPasswrd, J9SmgmtConstants.STORETYPE, ksType,
                J9SmgmtConstants.KEYALG, keyAlg, J9SmgmtConstants.SIGALG,
                sigAlg, J9SmgmtConstants.KEYSTORE, ksLocation,
                J9SmgmtConstants.STOREPASS, ksPasswrd,
                J9SmgmtConstants.VALIDITY, ksCertfValidity };

        return newKeyCmdArgs;
    }

    /**
     * Create the command line to open a Key Store and display its contents.
     * 
     * @param keyStore the keystore location.
     * @param storePasswd the password which is used to protect the integrity of
     *            the keystore.
     * @return the generated command line string in the following format: <br>
     *         <code>
     *         &lt;keytool Path&gt;
     *         -list -storetype {@link #ksType} -keystore <b>keyStore</b> -storepass
     *        <b>storePasswd</b></code>
     * @throws SignException in case fails to get the Security Management tool
     *             location in the file system.
     */
    private String[] generateOpenKeyStoreCmd(String keyStore, String storePasswd)
            throws SignException {

        String[] openKeyStoreCmdArgs = { getSecurityManagementTool(),
                J9SmgmtConstants.LIST, J9SmgmtConstants.STORETYPE, ksType,
                J9SmgmtConstants.KEYSTORE, keyStore,
                J9SmgmtConstants.STOREPASS, storePasswd };
        return openKeyStoreCmdArgs;
    }

    /**
     * Get the Security Management tool from preference store location (@link
     * #securityProviderPrefStore}).
     * 
     * @return the path to the keystore tool.
     * @throws SignException when the JRE home directory is not configured
     *             correctly.
     */
    private final String getSecurityManagementTool() throws SignException {
        String securityToolLocation = getToolLocation(null);

        if (securityToolLocation == null
                || securityToolLocation.length() <= 0
                || securityToolLocation
                        .equals(Messages.J9SecurityManager_Specify_directory_here)) {
            String message = MessageFormat
                    .format(
                            Messages.J9SecurityManager_getSecurityManagerException,
                            new Object[] {
                                    getId(),
                                    Messages.J9SecurityManager_Tool_not_configured_correctly,
                                    Messages.J9SecurityManager_Using_Security_management_features });
            throw new SignException(
                    SignErrors
                            .getErrorMessage(SignErrors.SECURITY_MANAGER_NOT_CONFIGURED)
                            + "\n" + //$NON-NLS-1$
                            message);
        }

        StringBuffer buffer = new StringBuffer("\"");
        buffer.append(securityToolLocation).append(File.separator)
                .append("bin") //$NON-NLS-1$						
                .append(File.separator).append("keytool") //$NON-NLS-1$
                .append("\"");

        return buffer.toString();
    }

    /**
     * Executes the specified command and arguments in a separate process.
     * 
     * @param cmd array containing the command to call and its arguments.
     * @return A new Process object for managing the subprocess
     * @throws SignException if fails to create the new process.
     */
    private Process runSecurityCmd(String[] cmd) throws SignException {

        Process p = null;

        try {
            p = Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
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
                    + Messages.J9SecurityManager_Could_not_execute
                    + " [" + str + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        return p;
    }

}
