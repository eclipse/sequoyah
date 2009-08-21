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
package org.eclipse.mtj.tfm.sign.smgmt.sun.impl;

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
import org.eclipse.mtj.tfm.sign.smgmt.sun.SunSmgmtCore;
import org.osgi.framework.Version;

/**
 * @since 1.0
 */
public class SecurityManagementImplementation extends SignExtensionImpl
        implements ISecurityManagement {

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

    public SecurityManagementImplementation() {
        super();
        setId(SunSmgmtCore.getDefault().getBundle().getSymbolicName());
        setVendor(Messages.SecurityManagementImplementation_PluginVendor);
        setVersion(new Version(
                Messages.SecurityManagementImplementation_PluginVersion));
        setDescription(Messages.SecurityManagementImplementation_MTJ_Sun_Keytool);
        setType(ExtensionType.SECURITY_MANAGEMENT);
        securityProviderPrefStore = SunSmgmtCore.getDefault()
                .getPreferenceStore();
    }

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
        monitor.beginTask(
                Messages.SecurityManagementImplementation_Creating_key_alias,
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

    /*
     * 
     * 
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

    /*
     * Generating the Distinguished Name (dname) string using the given user input. 
     * 
     */
    public String generateDname(String commonName, String orgUnit,
            String orgName, String localityName, String stateName,
            String country) {
        String Dname = SecurityManagementImpementationConstants.QUOTE
                + SecurityManagementImpementationConstants.COMMON_NAME_PREFIX
                + commonName
                + SecurityManagementImpementationConstants.COMMA_AND_SPACE
                + SecurityManagementImpementationConstants.ORGANIZATION_UNIT_PREFIX
                + orgUnit
                + SecurityManagementImpementationConstants.COMMA_AND_SPACE
                + SecurityManagementImpementationConstants.ORGANIZATION_NAME_PREFIX
                + orgName
                + SecurityManagementImpementationConstants.COMMA_AND_SPACE
                + SecurityManagementImpementationConstants.LOCALITY_NAME_PREFIX
                + localityName
                + SecurityManagementImpementationConstants.COMMA_AND_SPACE
                + SecurityManagementImpementationConstants.STATE_NAME_PREFIX
                + stateName
                + SecurityManagementImpementationConstants.COMMA_AND_SPACE
                + SecurityManagementImpementationConstants.COUNTRY_PREFIX
                + country + SecurityManagementImpementationConstants.QUOTE;
        return Dname;
    }

    public String getAliaskey() throws SignException {
        return aliaskey;
    }

    /**
     * getCertificateInfo - Get the certificates associated with the alias
     * key/keystore
     * 
     * @param alias - alias key
     * @param storePass
     * @return
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

    public String getKeyStoreNameLoc() throws SignException {
        return keyStoreNameLoc;
    }

    public String getPassWrd() throws SignException {
        return passwrd;
    }

    public String getStoreType() throws SignException {
        return storeType;
    }

    public String getToolLocation(IProgressMonitor monitor)
            throws SignException {
        return securityProviderPrefStore
                .getString(SecurityManagementImpementationConstants.SECURITY_TOOL_LOCATION);
    }

    public String getValidity() throws SignException {
        return validity;
    }

    /*
     * 
     * 
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

    public String[] openKeyStore(String keyStore, String storePass,
            IProgressMonitor monitor) throws SignException {
        monitor.beginTask(
                Messages.SecurityManagementImplementation_Opening_key_store,
                100);
        monitor.worked(10);

        String[] cmdArgs = generateOpenKeyStoreCmd(keyStore, storePass);
        Process p = runSecurityCmd(cmdArgs);

        BufferedReader cmdOutputStream = new BufferedReader(
                new InputStreamReader(p.getInputStream()));

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
                            "invalid keystore format") >= 0) {//$NON-NLS-1$
                        throw new SignException(
                                SignErrors
                                        .getErrorMessage(SignErrors.SECURITY_BAD_KEY_TYPE)
                                        + " :" + cmdOutput); //$NON-NLS-1$
                    } else if (cmdOutput.toLowerCase().indexOf(
                            "password was incorrect") >= 0) {//$NON-NLS-1$
                        throw new SignException(
                                SignErrors
                                        .getErrorMessage(SignErrors.SECURITY_BAD_KEYSTORE_OR_PASSWORD)
                                        + " :" + cmdOutput); //$NON-NLS-1$
                    } else {
                        throw new SignException(
                                SignErrors
                                        .getErrorMessage(SignErrors.GENERIC_SECURITY_ERROR)
                                        + " :" + cmdOutput); //$NON-NLS-1$
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
        return (String[]) aliases.toArray(new String[aliases.size()]);
    }

    public void resetValues() {
    }

    public void setAliaskey(String aliasKey) throws SignException {
        this.aliaskey = aliasKey;
    }

    public void setKeyStoreNameLoc(String keyStoreNameLoc) throws SignException {
        this.keyStoreNameLoc = keyStoreNameLoc;
    }

    public void setPassWrd(String passWrd) throws SignException {
        this.passwrd = passWrd;
    }

    public void setStoreType(String storeType) throws SignException {
        this.storeType = storeType;
    }

    public void setValidity(String validity) throws SignException {
        this.validity = validity;
    }

    public void setValues(String loc, String alias, String psswd, String strtype)
            throws SignException {

        storeType = strtype;
        aliaskey = alias;
        passwrd = psswd;
        keyStoreNameLoc = loc;

    }

    public void storeToolLocation(String loc, IProgressMonitor monitor)
            throws SignException {
        securityProviderPrefStore
                .setValue(
                        SecurityManagementImpementationConstants.SECURITY_TOOL_LOCATION,
                        loc);

    }

    /*
     * Generating the command to change the key store password.
     * 
     */
    private String[] generateChangeStorePasswordCmd(String newStorePass,
            String storePasswd) throws SignException {

        String[] changeStorePasswordCmdArgs = { getSecurityManagementTool(),
                getConsoleEncoding(),
                SecurityManagementImpementationConstants.CHANGE_STORE_PASSWD,
                SecurityManagementImpementationConstants.NEWSTOREPASS,
                newStorePass,
                SecurityManagementImpementationConstants.STORETYPE, storeType,
                SecurityManagementImpementationConstants.KEYSTORE,
                keyStoreNameLoc,
                SecurityManagementImpementationConstants.STOREPASS, storePasswd };

        return changeStorePasswordCmdArgs;
    }

    /*
     * 
     * 
     */
    private String[] generateDeleteKeyCmd() throws SignException {

        String[] deleteKeyCmdArgs = { getSecurityManagementTool(),
                getConsoleEncoding(),
                SecurityManagementImpementationConstants.DELETE_KEY,
                SecurityManagementImpementationConstants.ALIAS, aliaskey,
                SecurityManagementImpementationConstants.STORETYPE, storeType,
                SecurityManagementImpementationConstants.KEYSTORE,
                keyStoreNameLoc,
                SecurityManagementImpementationConstants.STOREPASS, passwrd };

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
                getConsoleEncoding(),
                SecurityManagementImpementationConstants.LIST,
                SecurityManagementImpementationConstants.ALIAS, aliaskey,
                SecurityManagementImpementationConstants.STORETYPE, storeType,
                SecurityManagementImpementationConstants.KEYSTORE,
                keyStoreNameLoc,
                SecurityManagementImpementationConstants.STOREPASS, passwrd };

        return listCertificateCmdArgs;
    }

    /*
     * 
     * 
     */
    private String[] generateGenerateCSRCmd(String certFile)
            throws SignException {

        String[] generateCSRCmdArgs = { getSecurityManagementTool(),
                getConsoleEncoding(),
                SecurityManagementImpementationConstants.GENERATE_CSR,
                SecurityManagementImpementationConstants.ALIAS, aliaskey,
                SecurityManagementImpementationConstants.FILE, certFile,
                SecurityManagementImpementationConstants.STORETYPE, storeType,
                SecurityManagementImpementationConstants.KEYSTORE,
                keyStoreNameLoc,
                SecurityManagementImpementationConstants.STOREPASS, passwrd };

        return generateCSRCmdArgs;
    }

    /*
     * 
     * 
     */
    private String[] generateImportSignedCertCmd(String certFile)
            throws SignException {

        String[] importSignedCertCmdArgs = { getSecurityManagementTool(),
                getConsoleEncoding(),
                SecurityManagementImpementationConstants.IMPORT_CERT,
                SecurityManagementImpementationConstants.NOPROMPT,
                SecurityManagementImpementationConstants.ALIAS, aliaskey,
                SecurityManagementImpementationConstants.KEYPASS, passwrd,
                SecurityManagementImpementationConstants.FILE, certFile,
                SecurityManagementImpementationConstants.STORETYPE, storeType,
                SecurityManagementImpementationConstants.KEYSTORE,
                keyStoreNameLoc,
                SecurityManagementImpementationConstants.STOREPASS, passwrd };

        return importSignedCertCmdArgs;
    }

    /*
     * 
     * 
     */
    private String[] generateNewKeyCmd(String alias, String dname,
            String keyAlg, String sigAlg) throws SignException {

        String[] newKeyCmdArgs = { getSecurityManagementTool(),
                getConsoleEncoding(),
                SecurityManagementImpementationConstants.GENERATE_KEY,
                SecurityManagementImpementationConstants.ALIAS, alias,
                SecurityManagementImpementationConstants.DNAME, dname,
                SecurityManagementImpementationConstants.KEYPASS, passwrd,
                SecurityManagementImpementationConstants.STORETYPE, storeType,
                SecurityManagementImpementationConstants.KEYALG, keyAlg,
                SecurityManagementImpementationConstants.SIGALG, sigAlg,
                SecurityManagementImpementationConstants.KEYSTORE,
                keyStoreNameLoc,
                SecurityManagementImpementationConstants.STOREPASS, passwrd,
                SecurityManagementImpementationConstants.VALIDITY, validity };

        return newKeyCmdArgs;
    }

    /*
     * Generating the command to Open a Key Store and display its contents.
     * 
     */
    private String[] generateOpenKeyStoreCmd(String keyStore, String storePasswd)
            throws SignException {

        String[] openKeyStoreCmdArgs = { getSecurityManagementTool(),
                getConsoleEncoding(),
                SecurityManagementImpementationConstants.LIST,
                SecurityManagementImpementationConstants.STORETYPE, storeType,
                SecurityManagementImpementationConstants.KEYSTORE, keyStore,
                SecurityManagementImpementationConstants.STOREPASS, storePasswd };
        return openKeyStoreCmdArgs;
    }

    /*
     * Get the Security Management tool from pref store location.
     * 
     */
    private final String getSecurityManagementTool() throws SignException {
        String securityToolLocation = getToolLocation(null);

        if (securityToolLocation == null
                || securityToolLocation.length() <= 0
                || securityToolLocation
                        .equals(Messages.PreferenceInitializer_0)) {
            String message = MessageFormat
                    .format(
                            Messages.SecurityManagementImplementation_0,
                            new Object[] {
                                    getId(),
                                    Messages.SecurityManagementImplementation_Security_tool_not_configured_correctly,
                                    Messages.SecurityManagementImplementation_Security_tool_using_features });
            throw new SignException(
                    SignErrors
                            .getErrorMessage(SignErrors.SECURITY_MANAGER_NOT_CONFIGURED)
                            + "\n" + //$NON-NLS-1$
                            message);
        }

        StringBuffer buffer = new StringBuffer("\"");//$NON-NLS-1$
        buffer.append(securityToolLocation).append(File.separator)
                .append("bin") //$NON-NLS-1$						
                .append(File.separator).append("keytool.exe") //$NON-NLS-1$
                .append("\""); //$NON-NLS-1$

        return buffer.toString();
    }

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

            throw new SignException(
                    SignErrors
                            .getErrorMessage(SignErrors.GENERIC_SECURITY_ERROR)
                            + Messages.SecurityManagementImplementation_Could_not_execute
                            + " [" + str + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        return p;
    }
}
