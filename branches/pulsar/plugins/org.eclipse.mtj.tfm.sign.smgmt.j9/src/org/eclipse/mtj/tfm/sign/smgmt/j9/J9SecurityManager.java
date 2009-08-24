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
import org.eclipse.mtj.tfm.internal.sign.smgmt.j9.J9SecurityManagerConstants;
import org.eclipse.mtj.tfm.internal.sign.smgmt.j9.Messages;
import org.eclipse.mtj.tfm.sign.core.SignErrors;
import org.eclipse.mtj.tfm.sign.core.enumerations.ExtensionType;
import org.eclipse.mtj.tfm.sign.core.exception.SignException;
import org.eclipse.mtj.tfm.sign.core.extension.ISecurityManagement;
import org.eclipse.mtj.tfm.sign.core.extension.SignExtensionImpl;
import org.osgi.framework.Version;

/**
 * @author Rodrigo Pastrana
 * @since 1.0
 */
public class J9SecurityManager extends SignExtensionImpl implements
        ISecurityManagement {

    private String aliaskey = null;
    private String keyStoreNameLoc = ""; //$NON-NLS-1$
    private String passwrd = null;
    private IPreferenceStore securityProviderPrefStore;
    // private variables
    private String storeType = "JKS"; //$NON-NLS-1$

    private String validity = "365"; //$NON-NLS-1$

    /**
	 * 
	 */
    public J9SecurityManager() {
        super();

        setId(J9SmgmtCore.getDefault().getBundle().getSymbolicName());
        setVendor(Messages.J9SecurityManager_PluginVendor);
        setVersion(new Version(Messages.J9SecurityManager_PluginVersion));
        setDescription(Messages.J9SecurityManager_Description);
        setType(ExtensionType.SECURITY_MANAGEMENT);
        securityProviderPrefStore = J9SmgmtCore.getDefault()
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

        monitor.beginTask(Messages.J9SecurityManager_Creating_key_alias, 100);

        boolean cmdSuccessful = true;
        String Dname = generateDname(commonName, orgUnit, orgName,
                localityName, stateName, country);
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
        String Dname = J9SecurityManagerConstants.QUOTE
                + J9SecurityManagerConstants.COMMON_NAME_PREFIX + commonName
                + J9SecurityManagerConstants.COMMA_AND_SPACE
                + J9SecurityManagerConstants.ORGANIZATION_UNIT_PREFIX + orgUnit
                + J9SecurityManagerConstants.COMMA_AND_SPACE
                + J9SecurityManagerConstants.ORGANIZATION_NAME_PREFIX + orgName
                + J9SecurityManagerConstants.COMMA_AND_SPACE
                + J9SecurityManagerConstants.LOCALITY_NAME_PREFIX
                + localityName + J9SecurityManagerConstants.COMMA_AND_SPACE
                + J9SecurityManagerConstants.STATE_NAME_PREFIX + stateName
                + J9SecurityManagerConstants.COMMA_AND_SPACE
                + J9SecurityManagerConstants.COUNTRY_PREFIX + country
                + J9SecurityManagerConstants.QUOTE;
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
                .getString(J9SecurityManagerConstants.SECURITY_TOOL_LOCATION);
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

    public void resetValues() throws SignException {
        // TODO Auto-generated method stub
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
        securityProviderPrefStore.setValue(
                J9SecurityManagerConstants.SECURITY_TOOL_LOCATION, loc);
    }

    /*
     * Generating the command to change the key store password.
     * 
     */
    private String[] generateChangeStorePasswordCmd(String newStorePass,
            String storePasswd) throws SignException {

        String[] changeStorePasswordCmdArgs = { getSecurityManagementTool(),
                J9SecurityManagerConstants.CHANGE_STORE_PASSWD,
                J9SecurityManagerConstants.NEWSTOREPASS, newStorePass,
                J9SecurityManagerConstants.STORETYPE, storeType,
                J9SecurityManagerConstants.KEYSTORE, keyStoreNameLoc,
                J9SecurityManagerConstants.STOREPASS, storePasswd };

        return changeStorePasswordCmdArgs;
    }

    /*
     * 
     * 
     */
    private String[] generateDeleteKeyCmd() throws SignException {

        String[] deleteKeyCmdArgs = { getSecurityManagementTool(),
                J9SecurityManagerConstants.DELETE_KEY,
                J9SecurityManagerConstants.ALIAS, aliaskey,
                J9SecurityManagerConstants.STORETYPE, storeType,
                J9SecurityManagerConstants.KEYSTORE, keyStoreNameLoc,
                J9SecurityManagerConstants.STOREPASS, passwrd };

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
                J9SecurityManagerConstants.LIST,
                J9SecurityManagerConstants.ALIAS, aliaskey,
                J9SecurityManagerConstants.STORETYPE, storeType,
                J9SecurityManagerConstants.KEYSTORE, keyStoreNameLoc,
                J9SecurityManagerConstants.STOREPASS, passwrd };

        return listCertificateCmdArgs;
    }

    /*
     * 
     * 
     */
    private String[] generateGenerateCSRCmd(String certFile)
            throws SignException {

        String[] generateCSRCmdArgs = { getSecurityManagementTool(),
                J9SecurityManagerConstants.GENERATE_CSR,
                J9SecurityManagerConstants.ALIAS, aliaskey,
                J9SecurityManagerConstants.FILE, certFile,
                J9SecurityManagerConstants.STORETYPE, storeType,
                J9SecurityManagerConstants.KEYSTORE, keyStoreNameLoc,
                J9SecurityManagerConstants.STOREPASS, passwrd };

        return generateCSRCmdArgs;
    }

    /*
     * 
     * 
     */
    private String[] generateImportSignedCertCmd(String certFile)
            throws SignException {

        String[] importSignedCertCmdArgs = { getSecurityManagementTool(),
                J9SecurityManagerConstants.IMPORT_CERT,
                J9SecurityManagerConstants.NOPROMPT,
                J9SecurityManagerConstants.ALIAS, aliaskey,
                J9SecurityManagerConstants.KEYPASS, passwrd,
                J9SecurityManagerConstants.FILE, certFile,
                J9SecurityManagerConstants.STORETYPE, storeType,
                J9SecurityManagerConstants.KEYSTORE, keyStoreNameLoc,
                J9SecurityManagerConstants.STOREPASS, passwrd };

        return importSignedCertCmdArgs;
    }

    /*
     * 
     * 
     */
    private String[] generateNewKeyCmd(String alias, String dname,
            String keyAlg, String sigAlg) throws SignException {

        String[] newKeyCmdArgs = { getSecurityManagementTool(),
                J9SecurityManagerConstants.GENERATE_KEY,
                J9SecurityManagerConstants.ALIAS, alias,
                J9SecurityManagerConstants.DNAME, dname,
                J9SecurityManagerConstants.KEYPASS, passwrd,
                J9SecurityManagerConstants.STORETYPE, storeType,
                J9SecurityManagerConstants.KEYALG, keyAlg,
                J9SecurityManagerConstants.SIGALG, sigAlg,
                J9SecurityManagerConstants.KEYSTORE, keyStoreNameLoc,
                J9SecurityManagerConstants.STOREPASS, passwrd,
                J9SecurityManagerConstants.VALIDITY, validity };

        return newKeyCmdArgs;
    }

    /*
     * Generating the command to Open a Key Store and display its contents.
     * 
     */
    private String[] generateOpenKeyStoreCmd(String keyStore, String storePasswd)
            throws SignException {

        String[] openKeyStoreCmdArgs = { getSecurityManagementTool(),
                J9SecurityManagerConstants.LIST,
                J9SecurityManagerConstants.STORETYPE, storeType,
                J9SecurityManagerConstants.KEYSTORE, keyStore,
                J9SecurityManagerConstants.STOREPASS, storePasswd };
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
                .append(File.separator).append("keytool.exe") //$NON-NLS-1$
                .append("\"");

        return buffer.toString();
    }

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
