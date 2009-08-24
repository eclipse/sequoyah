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
package org.eclipse.mtj.tfm.sign.smgmt.sun;

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
import org.eclipse.mtj.tfm.internal.sign.smgmt.sun.Messages;
import org.eclipse.mtj.tfm.internal.sign.smgmt.sun.SunSmgmtConstants;
import org.eclipse.mtj.tfm.sign.core.SignErrors;
import org.eclipse.mtj.tfm.sign.core.enumerations.ExtensionType;
import org.eclipse.mtj.tfm.sign.core.exception.SignException;
import org.eclipse.mtj.tfm.sign.core.extension.ISecurityManagement;
import org.eclipse.mtj.tfm.sign.core.extension.SignExtensionImpl;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.Version;

/**
 * @since 1.0
 */
public class SunSecurityManagement extends SignExtensionImpl implements
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
    private String ksCertfvalidity = "365"; //$NON-NLS-1$

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
     * Creates a new instance of SunSecurityManagement.<br>
     * <br>
     * <b>ID</b>: <i>org.eclipse.mtj.tfm.sign.smgmt.sun</i><br>
     * <b>Vendor</b>: <i>DSDP - Eclipse.org</i><br>
     * <b>Version</b>: <i>1.0.0</i><br>
     * <b>Description</b>: <i>Security Manager for SUN's JRE Keytool.</i><br>
     * <b>Type</b>: <i>{@link ExtensionType#SECURITY_MANAGEMENT
     * SECURITY_MANAGEMENT}</i><br>
     */
    public SunSecurityManagement() {
        super();
        setId(SunSmgmtCore.getDefault().getBundle().getSymbolicName());
        setVendor(Messages.SunSecurityManagement_PluginVendor);
        setVersion(new Version(Messages.SunSecurityManagement_PluginVersion));
        setDescription(Messages.SunSecurityManagement_Sun_Keytool);
        setType(ExtensionType.SECURITY_MANAGEMENT);
        securityProviderPrefStore = SunSmgmtCore.getDefault()
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
                    throw new SignException(
                            NLS
                                    .bind(
                                            Messages.SunSecurityManagement_defaultErrorMessage,
                                            new String[] {
                                                    SignErrors
                                                            .getErrorMessage(SignErrors.GENERIC_SECURITY_ERROR),
                                                    cmdOutput }));
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

        monitor.beginTask(Messages.SunSecurityManagement_Creating_key_alias,
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

                    monitor.done();
                    if (cmdOutput.toLowerCase().indexOf(
                            "alias <" + alias + "> already exists") >= 0) { //$NON-NLS-1$ //$NON-NLS-2$
                        throw new SignException(

                                NLS
                                        .bind(
                                                Messages.SunSecurityManagement_defaultErrorMessage,
                                                new String[] {
                                                        SignErrors
                                                                .getErrorMessage(SignErrors.SECURITY_ALIAS_DUPLICATE),
                                                        cmdOutput })); //$NON-NLS-1$
                    } else {
                        throw new SignException(
                                NLS
                                        .bind(
                                                Messages.SunSecurityManagement_defaultErrorMessage,
                                                new String[] {
                                                        SignErrors
                                                                .getErrorMessage(SignErrors.GENERIC_SECURITY_ERROR),
                                                        cmdOutput })); //$NON-NLS-1$
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
                    throw new SignException(
                            NLS
                                    .bind(
                                            Messages.SunSecurityManagement_defaultErrorMessage,
                                            new String[] {
                                                    SignErrors
                                                            .getErrorMessage(SignErrors.GENERIC_SECURITY_ERROR),
                                                    cmdOutput })); //$NON-NLS-1$
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
                    throw new SignException(
                            NLS
                                    .bind(
                                            Messages.SunSecurityManagement_defaultErrorMessage,
                                            new String[] {
                                                    SignErrors
                                                            .getErrorMessage(SignErrors.GENERIC_SECURITY_ERROR),
                                                    cmdOutput })); //$NON-NLS-1$
                }
            }
        } catch (IOException ee) {
            throw new SignException(SignErrors
                    .getErrorMessage(SignErrors.GENERIC_SECURITY_ERROR), ee);
        }
        return cmdSuccessful;

    }

    /**
     * Generating the <b>X.500 Distinguished Name</b> string using the given
     * user input.<br>
     * <b>Note:</b> If a distinguished name string value contains a comma, the
     * comma must be escaped by a "\" character.
     * 
     * @param commonName common name of a person, e.g., "Susan Jones"
     * @param organizationUnit small organization (e.g, department or division)
     *            name, e.g., "Purchasing"
     * @param orgName large organization name, e.g., "ABCSystems, Inc."
     * @param localityName locality (city) name, e.g., "Palo Alto"
     * @param stateName state or province name, e.g., "California"
     * @param country state or province name, e.g., "California"
     * @return the generated string in the following format:
     * 
     * <pre>
     * "CN=<b><i>commonName</i></b>, OU=<b><i>organizationUnit</i></b>, O=<b><i>orgName</i></b>, L=<b><i>localityName</i></b>, S=<b><i>stateName</i></b>, C=<b><i>country</i></b>" 
     * </pre>
     * 
     */
    public String generateDname(String commonName, String organizationUnit,
            String orgName, String localityName, String stateName,
            String country) {
        String Dname = SunSmgmtConstants.QUOTE
                + SunSmgmtConstants.COMMON_NAME_PREFIX + commonName
                + SunSmgmtConstants.COMMA_AND_SPACE
                + SunSmgmtConstants.ORGANIZATION_UNIT_PREFIX + organizationUnit
                + SunSmgmtConstants.COMMA_AND_SPACE
                + SunSmgmtConstants.ORGANIZATION_NAME_PREFIX + orgName
                + SunSmgmtConstants.COMMA_AND_SPACE
                + SunSmgmtConstants.LOCALITY_NAME_PREFIX + localityName
                + SunSmgmtConstants.COMMA_AND_SPACE
                + SunSmgmtConstants.STATE_NAME_PREFIX + stateName
                + SunSmgmtConstants.COMMA_AND_SPACE
                + SunSmgmtConstants.COUNTRY_PREFIX + country
                + SunSmgmtConstants.QUOTE;
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
        return ksLocation;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.ISecurityManagement#getPassWrd()
     */
    public String getPassWrd() throws SignException {
        return ksPasswrd;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.ISecurityManagement#getStoreType()
     */
    public String getStoreType() throws SignException {
        return ksType;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.ISecurityManagement#getToolLocation(org.eclipse.core.runtime.IProgressMonitor)
     */
    public String getToolLocation(IProgressMonitor monitor)
            throws SignException {
        return securityProviderPrefStore
                .getString(SunSmgmtConstants.SECURITY_TOOL_LOCATION);
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.ISecurityManagement#getValidity()
     */
    public String getValidity() throws SignException {
        return ksCertfvalidity;
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
                    throw new SignException(
                            NLS
                                    .bind(
                                            Messages.SunSecurityManagement_defaultErrorMessage,
                                            new String[] {
                                                    SignErrors
                                                            .getErrorMessage(SignErrors.GENERIC_SECURITY_ERROR),
                                                    cmdOutput }));

                }
            }
        } catch (IOException ee) {
            throw new SignException(SignErrors
                    .getErrorMessage(SignErrors.GENERIC_SECURITY_ERROR), ee);
        }
        return cmdSuccessful;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.ISecurityManagement#isKeyStoreSelected()
     */
    public boolean isKeyStoreSelected() throws SignException {

        if ((ksLocation == null) || (ksLocation.length() <= 0)) {
            return false;
        }

        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.ISecurityManagement#openKeyStore(java.lang.String, java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
     */
    public String[] openKeyStore(String keyStore, String storePass,
            IProgressMonitor monitor) throws SignException {
        monitor
                .beginTask(Messages.SunSecurityManagement_Opening_key_store,
                        100);
        monitor.worked(10);

        String[] cmdArgs = generateOpenKeyStoreCmd(keyStore, storePass);
        Process p = runSecurityCmd(cmdArgs);

        BufferedReader cmdOutputStream = new BufferedReader(
                new InputStreamReader(p.getInputStream()));

        String cmdOutput;
        List<String> aliases = new ArrayList<String>();

        try {
            while ((cmdOutput = cmdOutputStream.readLine()) != null) {
                monitor.subTask(cmdOutput);
                monitor.worked(25);

                if (cmdOutput.toLowerCase().indexOf("error") >= 0) { //$NON-NLS-1$
                    monitor.done();
                    if (cmdOutput.toLowerCase().indexOf(
                            "invalid keystore format") >= 0) {//$NON-NLS-1$
                        throw new SignException(
                                NLS
                                        .bind(
                                                Messages.SunSecurityManagement_defaultErrorMessage,
                                                new String[] {
                                                        SignErrors
                                                                .getErrorMessage(SignErrors.SECURITY_BAD_KEY_TYPE),
                                                        cmdOutput }));
                    } else if (cmdOutput.toLowerCase().indexOf(
                            "password was incorrect") >= 0) {//$NON-NLS-1$
                        throw new SignException(
                                NLS
                                        .bind(
                                                Messages.SunSecurityManagement_defaultErrorMessage,
                                                new String[] {
                                                        SignErrors
                                                                .getErrorMessage(SignErrors.SECURITY_BAD_KEYSTORE_OR_PASSWORD),
                                                        cmdOutput })); //$NON-NLS-1$
                    } else {
                        throw new SignException(
                                NLS
                                        .bind(
                                                Messages.SunSecurityManagement_defaultErrorMessage,
                                                new String[] {
                                                        SignErrors
                                                                .getErrorMessage(SignErrors.GENERIC_SECURITY_ERROR),
                                                        cmdOutput })); //$NON-NLS-1$
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
        return aliases.toArray(new String[aliases.size()]);
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
        aliaskey = aliasKey;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.ISecurityManagement#setKeyStoreNameLoc(java.lang.String)
     */
    public void setKeyStoreNameLoc(String keyStoreNameLoc) throws SignException {
        this.ksLocation = keyStoreNameLoc;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.ISecurityManagement#setPassWrd(java.lang.String)
     */
    public void setPassWrd(String passWrd) throws SignException {
        ksPasswrd = passWrd;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.ISecurityManagement#setStoreType(java.lang.String)
     */
    public void setStoreType(String storeType) throws SignException {
        this.ksType = storeType;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.ISecurityManagement#setValidity(java.lang.String)
     */
    public void setValidity(String validity) throws SignException {
        this.ksCertfvalidity = validity;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.ISecurityManagement#setValues(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void setValues(String loc, String alias, String psswd, String strtype)
            throws SignException {

        ksType = strtype;
        aliaskey = alias;
        ksPasswrd = psswd;
        ksLocation = loc;

    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.ISecurityManagement#storeToolLocation(java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void storeToolLocation(String loc, IProgressMonitor monitor)
            throws SignException {
        securityProviderPrefStore.setValue(
                SunSmgmtConstants.SECURITY_TOOL_LOCATION, loc);

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
     *         &lt;keytool Path&gt; -J-Dconsole.encoding=&lt;console character encoding&gt; 
     *         -storepasswd -new <b>newStorePass</b> -storetype {@link #ksType} -keystore {@link #ksLocation} -storepass
     *        <b>storePasswd</b></code>
     * @throws SignException in case fails to get the Security Management tool
     *             location in the file system.
     */
    private String[] generateChangeStorePasswordCmd(String newStorePass,
            String storePasswd) throws SignException {

        String[] changeStorePasswordCmdArgs = { getSecurityManagementTool(),
                getConsoleEncoding(), SunSmgmtConstants.CHANGE_STORE_PASSWD,
                SunSmgmtConstants.NEWSTOREPASS, newStorePass,
                SunSmgmtConstants.STORETYPE, ksType,
                SunSmgmtConstants.KEYSTORE, ksLocation,
                SunSmgmtConstants.STOREPASS, storePasswd };

        return changeStorePasswordCmdArgs;
    }

    /**
     * Create the command line to delete from the keystore the entry identified
     * by {@link #aliaskey}.
     * 
     * @return the generated command line string in the following format: <br>
     *         <code>
     *         &lt;keytool Path&gt; -J-Dconsole.encoding=&lt;console character encoding&gt; 
     *         -delete -alias {@link #aliaskey} -storetype {@link #ksType} -keystore {@link #ksLocation} -storepass
     *        <b>storePasswd</b></code>
     * @throws SignException in case fails to get the Security Management tool
     *             location in the file system.
     */
    private String[] generateDeleteKeyCmd() throws SignException {

        String[] deleteKeyCmdArgs = { getSecurityManagementTool(),
                getConsoleEncoding(), SunSmgmtConstants.DELETE_KEY,
                SunSmgmtConstants.ALIAS, aliaskey, SunSmgmtConstants.STORETYPE,
                ksType, SunSmgmtConstants.KEYSTORE, ksLocation,
                SunSmgmtConstants.STOREPASS, ksPasswrd };

        return deleteKeyCmdArgs;
    }

    /**
     * Create the command line to display the contents of the keystore entry
     * identified by {@link #aliaskey}.
     * 
     * @return the generated command line string in the following format: <br>
     *         <code>
     *         &lt;keytool Path&gt; -J-Dconsole.encoding=&lt;console character encoding&gt; 
     *         -list -alias {@link #aliaskey} -storetype {@link #ksType} -keystore {@link #ksLocation} -storepass
     *        <b>storePasswd</b></code>
     * @throws SignException in case fails to get the Security Management tool
     *             location in the file system.
     */
    private String[] generateDisplayCertifcates() throws SignException {

        String[] listCertificateCmdArgs = { getSecurityManagementTool(),
                getConsoleEncoding(), SunSmgmtConstants.LIST,
                SunSmgmtConstants.ALIAS, aliaskey, SunSmgmtConstants.STORETYPE,
                ksType, SunSmgmtConstants.KEYSTORE, ksLocation,
                SunSmgmtConstants.STOREPASS, ksPasswrd };

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
     *         &lt;keytool Path&gt; -J-Dconsole.encoding=&lt;console character encoding&gt; 
     *         -export -alias {@link #aliaskey} -file <b>certFile</b> -storetype {@link #ksType} -keystore {@link #ksLocation} -storepass
     *        <b>storePasswd</b></code>
     * @throws SignException in case fails to get the Security Management tool
     *             location in the file system.
     */
    private String[] generateGenerateCSRCmd(String certFile)
            throws SignException {

        String[] generateCSRCmdArgs = { getSecurityManagementTool(),
                getConsoleEncoding(), SunSmgmtConstants.GENERATE_CSR,
                SunSmgmtConstants.ALIAS, aliaskey, SunSmgmtConstants.FILE,
                certFile, SunSmgmtConstants.STORETYPE, ksType,
                SunSmgmtConstants.KEYSTORE, ksLocation,
                SunSmgmtConstants.STOREPASS, ksPasswrd };

        return generateCSRCmdArgs;
    }

    /**
     * Create the command line to read the certificate from the file certFile,
     * and stores it in the keystore entry identified by {@link #aliaskey}.
     * 
     * @param certFile the file where the certificate is stored.
     * @return the generated command line string in the following format: <br>
     *         <code>
     *         &lt;keytool Path&gt; -J-Dconsole.encoding=&lt;console character encoding&gt; 
     *         -import -noprompt -alias {@link #aliaskey} -keypass {@link #ksPasswrd} -file <b>certFile</b> -storetype {@link #ksType} -keystore {@link #ksLocation} -storepass
     *        <b>storePasswd</b></code>
     * @throws SignException in case fails to get the Security Management tool
     *             location in the file system.
     */
    private String[] generateImportSignedCertCmd(String certFile)
            throws SignException {

        String[] importSignedCertCmdArgs = { getSecurityManagementTool(),
                getConsoleEncoding(), SunSmgmtConstants.IMPORT_CERT,
                SunSmgmtConstants.NOPROMPT, SunSmgmtConstants.ALIAS, aliaskey,
                SunSmgmtConstants.KEYPASS, ksPasswrd, SunSmgmtConstants.FILE,
                certFile, SunSmgmtConstants.STORETYPE, ksType,
                SunSmgmtConstants.KEYSTORE, ksLocation,
                SunSmgmtConstants.STOREPASS, ksPasswrd };

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
     *         {@link #ksPasswrd} -validity {@link #ksCertfvalidity}</code>
     * @throws SignException in case fails to get the Security Management tool
     *             location in the file system.
     */
    private String[] generateNewKeyCmd(String alias, String dname,
            String keyAlg, String sigAlg) throws SignException {

        String[] newKeyCmdArgs = { getSecurityManagementTool(),
                getConsoleEncoding(), SunSmgmtConstants.GENERATE_KEY,
                SunSmgmtConstants.ALIAS, alias, SunSmgmtConstants.DNAME, dname,
                SunSmgmtConstants.KEYPASS, ksPasswrd,
                SunSmgmtConstants.STORETYPE, ksType, SunSmgmtConstants.KEYALG,
                keyAlg, SunSmgmtConstants.SIGALG, sigAlg,
                SunSmgmtConstants.KEYSTORE, ksLocation,
                SunSmgmtConstants.STOREPASS, ksPasswrd,
                SunSmgmtConstants.VALIDITY, ksCertfvalidity };

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
     *         &lt;keytool Path&gt; -J-Dconsole.encoding=&lt;console character encoding&gt; 
     *         -list -storetype {@link #ksType} -keystore <b>keyStore</b> -storepass
     *        <b>storePasswd</b></code>
     * @throws SignException in case fails to get the Security Management tool
     *             location in the file system.
     */
    private String[] generateOpenKeyStoreCmd(String keyStore, String storePasswd)
            throws SignException {

        String[] openKeyStoreCmdArgs = { getSecurityManagementTool(),
                getConsoleEncoding(), SunSmgmtConstants.LIST,
                SunSmgmtConstants.STORETYPE, ksType,
                SunSmgmtConstants.KEYSTORE, keyStore,
                SunSmgmtConstants.STOREPASS, storePasswd };
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

        if ((securityToolLocation == null)
                || (securityToolLocation.length() <= 0)
                || securityToolLocation
                        .equals(Messages.PreferenceInitializer_set_location_message)) {
            String message = MessageFormat
                    .format(
                            Messages.SunSecurityManagement_defaultErrorMessage2,
                            new Object[] {
                                    getId(),
                                    Messages.SunSecurityManagement_Security_tool_not_configured_correctly,
                                    Messages.SunSecurityManagement_Security_tool_using_features });
            throw new SignException(
                    SignErrors
                            .getErrorMessage(SignErrors.SECURITY_MANAGER_NOT_CONFIGURED)
                            + "\n" + //$NON-NLS-1$
                            message);
        }

        StringBuffer buffer = new StringBuffer("\"");//$NON-NLS-1$
        buffer.append(securityToolLocation).append(File.separator)
                .append("bin") //$NON-NLS-1$						
                .append(File.separator).append("keytool") //$NON-NLS-1$
                .append("\""); //$NON-NLS-1$

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
                    .getErrorMessage(SignErrors.GENERIC_SECURITY_ERROR), e);
        }

        if (p == null) {
            StringBuffer str = new StringBuffer(""); //$NON-NLS-1$

            for (String element : cmd) {
                str.append(" " + element); //$NON-NLS-1$
            }

            throw new SignException(SignErrors
                    .getErrorMessage(SignErrors.GENERIC_SECURITY_ERROR)
                    + Messages.SunSecurityManagement_Could_not_execute
                    + " [" + str + " ]"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        return p;
    }
}
