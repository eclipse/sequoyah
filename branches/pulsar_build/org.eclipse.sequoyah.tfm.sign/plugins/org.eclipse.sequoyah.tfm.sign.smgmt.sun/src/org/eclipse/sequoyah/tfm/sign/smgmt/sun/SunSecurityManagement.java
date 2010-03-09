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
package org.eclipse.sequoyah.tfm.sign.smgmt.sun;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.sequoyah.tfm.sign.internal.smgmt.sun.Messages;
import org.eclipse.sequoyah.tfm.sign.internal.smgmt.sun.SunSmgmtConstants;
import org.eclipse.sequoyah.tfm.sign.core.SignErrors;
import org.eclipse.sequoyah.tfm.sign.core.enumerations.ExtensionType;
import org.eclipse.sequoyah.tfm.sign.core.exception.SignException;
import org.eclipse.sequoyah.tfm.sign.core.extension.ExtensionImpl;
import org.eclipse.sequoyah.tfm.sign.core.extension.security.ISecurityManagement;
import org.eclipse.sequoyah.tfm.sign.core.extension.security.Keytool;
import org.eclipse.sequoyah.tfm.sign.core.extension.security.X500DName;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.Version;

/**
 * @since 1.0
 */
public class SunSecurityManagement extends ExtensionImpl implements
        ISecurityManagement {

    /**
     * Return the line to be used to configure Console Encoding.
     * 
     * @return The character encoding for the console.
     */
    private static String getConsoleEncoding() {
        return Keytool.S_JAVAOPTION
                + "-Dconsole.encoding=" + System.getProperty("file.encoding"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private Keytool keytool;

    private String aliaskey = null;

    /** The certificate will be valid for 365 days. */
    private String ksCertfValidity = "365"; //$NON-NLS-1$

    /** The keystore location in the file system. */
    private IPath ksLocation = Path.EMPTY;

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
     * <b>ID</b>: <i>org.eclipse.sequoyah.tfm.sign.smgmt.sun</i><br>
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

        securityProviderPrefStore
                .addPropertyChangeListener(new IPropertyChangeListener() {

                    public void propertyChange(PropertyChangeEvent event) {
                        if (event.getProperty().equals(
                                SunSmgmtConstants.SECURITY_TOOL_LOCATION)) {
                            if (keytool != null) {
                                keytool.setLocation(new Path((String) event
                                        .getNewValue()));
                            } else {
                                try {
                                    initializeKeytool();
                                } catch (SignException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });
    }

    /* (non-Javadoc)
     * @see org.eclipse.sequoyah.tfm.sign.core.extension.security.ISecurityManagement#changeStorePassword(java.lang.String, java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
     */
    public boolean changeStorePassword(String newStorePass, String storePass,
            IProgressMonitor monitor) throws SignException {

        boolean cmdSuccessful = true;
        initializeKeytool();

        String[] cmdArgs = keytool.generateChangeStorePasswordCmd(newStorePass,
                ksType, ksLocation, storePass, getConsoleEncoding());

        Process p = keytool.execute(cmdArgs);

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
     * @see org.eclipse.sequoyah.tfm.sign.core.extension.security.ISecurityManagement#createNewKey(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
     */
    public boolean createNewKey(String alias, String commonName,
            String orgUnit, String orgName, String localityName,
            String stateName, String country, IProgressMonitor monitor)
            throws SignException {

        monitor.beginTask(Messages.SunSecurityManagement_Creating_key_alias,
                100);

        boolean cmdSuccessful = true;
        initializeKeytool();

        X500DName dName = new X500DName(commonName, orgUnit, orgName,
                localityName, stateName, country);

        String[] cmdArgs = keytool
                .generateNewKeyCmd(
                        dName,
                        "RSA", "SHA1withRSA", ksCertfValidity, alias, ksPasswrd, ksType, ksLocation, ksPasswrd, getConsoleEncoding()); //$NON-NLS-1$ //$NON-NLS-2$

        Process p = keytool.execute(cmdArgs);
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
                                                        cmdOutput }));
                    } else {
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
     * @see org.eclipse.sequoyah.tfm.sign.core.extension.security.ISecurityManagement#deleteKey(org.eclipse.core.runtime.IProgressMonitor)
     */
    public boolean deleteKey(IProgressMonitor monitor) throws SignException {

        boolean cmdSuccessful = true;

        initializeKeytool();

        String[] cmdArgs = keytool.generateDeleteKeyCmd(aliaskey, ksType,
                ksLocation, ksPasswrd, getConsoleEncoding());

        Process p = keytool.execute(cmdArgs);
        BufferedReader cmdOutputStream = new BufferedReader(
                new InputStreamReader(p.getInputStream()));

        String cmdOutput;
        try {
            while ((cmdOutput = cmdOutputStream.readLine()) != null) {

                if (cmdOutput.toLowerCase().indexOf("error") >= 0) { //$NON-NLS-1$
                    cmdSuccessful = false;
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
     * @see org.eclipse.sequoyah.tfm.sign.core.extension.security.ISecurityManagement#generateCSR(java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
     */
    public boolean generateCSR(String certFile, IProgressMonitor monitor)
            throws SignException {

        boolean cmdSuccessful = true;

        initializeKeytool();

        String[] cmdArgs = keytool.generateGenerateCSRCmd(certFile, aliaskey,
                ksType, ksLocation, ksPasswrd, getConsoleEncoding());
        Process p = keytool.execute(cmdArgs);

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
     * @see org.eclipse.sequoyah.tfm.sign.core.extension.security.ISecurityManagement#getAliaskey()
     */
    public String getAliaskey() throws SignException {
        return aliaskey;
    }

    /* (non-Javadoc)
     * @see org.eclipse.sequoyah.tfm.sign.core.extension.security.ISecurityManagement#getCertificateInfo(org.eclipse.core.runtime.IProgressMonitor)
     */
    public String getCertificateInfo(IProgressMonitor monitor)
            throws SignException {

        String certInfo = ""; //$NON-NLS-1$

        if ((aliaskey != null) && (aliaskey.length() > 0)) {

            try {
                initializeKeytool();

                String[] cmdArgs = keytool.generateDisplayCertifcates(aliaskey,
                        ksType, ksLocation, ksPasswrd, getConsoleEncoding());
                Process p = keytool.execute(cmdArgs);

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
     * @see org.eclipse.sequoyah.tfm.sign.core.extension.security.ISecurityManagement#getKeyStoreNameLoc()
     */
    public IPath getKeyStoreNameLoc() throws SignException {
        return ksLocation;
    }

    /* (non-Javadoc)
     * @see org.eclipse.sequoyah.tfm.sign.core.extension.security.ISecurityManagement#getPassWrd()
     */
    public String getPassWrd() throws SignException {
        return ksPasswrd;
    }

    /* (non-Javadoc)
     * @see org.eclipse.sequoyah.tfm.sign.core.extension.security.ISecurityManagement#getStoreType()
     */
    public String getStoreType() throws SignException {
        return ksType;
    }

    /* (non-Javadoc)
     * @see org.eclipse.sequoyah.tfm.sign.core.extension.security.ISecurityManagement#getToolLocation(org.eclipse.core.runtime.IProgressMonitor)
     */
    public IPath getToolLocation(IProgressMonitor monitor) throws SignException {

        IPath path = new Path(securityProviderPrefStore
                .getString(SunSmgmtConstants.SECURITY_TOOL_LOCATION));
        return path;
    }

    /* (non-Javadoc)
     * @see org.eclipse.sequoyah.tfm.sign.core.extension.security.ISecurityManagement#getValidity()
     */
    public String getValidity() throws SignException {
        return ksCertfValidity;
    }

    /* (non-Javadoc)
     * @see org.eclipse.sequoyah.tfm.sign.core.extension.security.ISecurityManagement#importSignedCert(java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
     */
    public boolean importSignedCert(String certFile, IProgressMonitor monitor)
            throws SignException {

        boolean cmdSuccessful = true;
        initializeKeytool();

        String[] cmdArgs = keytool.generateImportSignedCertCmd(certFile,
                aliaskey, ksPasswrd, ksType, ksLocation, ksPasswrd,
                getConsoleEncoding());
        Process p = keytool.execute(cmdArgs);

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
     * @see org.eclipse.sequoyah.tfm.sign.core.extension.security.ISecurityManagement#isKeyStoreSelected()
     */
    public boolean isKeyStoreSelected() throws SignException {

        if ((ksLocation == null) || (ksLocation.isEmpty())) {
            return false;
        }

        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.sequoyah.tfm.sign.core.extension.security.ISecurityManagement#openKeyStore(java.lang.String, java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
     */
    public String[] openKeyStore(IPath keyStore, String storePass,
            IProgressMonitor monitor) throws SignException {
        monitor
                .beginTask(Messages.SunSecurityManagement_Opening_key_store,
                        100);
        monitor.worked(10);

        initializeKeytool();

        String[] cmdArgs = keytool.generateOpenKeyStoreCmd(ksType, keyStore,
                storePass, getConsoleEncoding());
        Process p = keytool.execute(cmdArgs);

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
                                                        cmdOutput }));
                    } else {
                        throw new SignException(
                                NLS
                                        .bind(
                                                Messages.SunSecurityManagement_defaultErrorMessage,
                                                new String[] {
                                                        SignErrors
                                                                .getErrorMessage(SignErrors.GENERIC_SECURITY_ERROR),
                                                        cmdOutput }));
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
     * @see org.eclipse.sequoyah.tfm.sign.core.extension.security.ISecurityManagement#resetValues()
     */
    public void resetValues() {
    }

    /* (non-Javadoc)
     * @see org.eclipse.sequoyah.tfm.sign.core.extension.security.ISecurityManagement#setAliaskey(java.lang.String)
     */
    public void setAliaskey(String aliasKey) throws SignException {
        aliaskey = aliasKey;
    }

    /* (non-Javadoc)
     * @see org.eclipse.sequoyah.tfm.sign.core.extension.security.ISecurityManagement#setKeyStoreNameLoc(java.lang.String)
     */
    public void setKeyStoreNameLoc(IPath keyStoreNameLoc) throws SignException {
        ksLocation = keyStoreNameLoc;
    }

    /* (non-Javadoc)
     * @see org.eclipse.sequoyah.tfm.sign.core.extension.security.ISecurityManagement#setPassWrd(java.lang.String)
     */
    public void setPassWrd(String passWrd) throws SignException {
        ksPasswrd = passWrd;
    }

    /* (non-Javadoc)
     * @see org.eclipse.sequoyah.tfm.sign.core.extension.security.ISecurityManagement#setStoreType(java.lang.String)
     */
    public void setStoreType(String storeType) throws SignException {
        ksType = storeType;
    }

    /* (non-Javadoc)
     * @see org.eclipse.sequoyah.tfm.sign.core.extension.security.ISecurityManagement#setValidity(java.lang.String)
     */
    public void setValidity(String validity) throws SignException {
        ksCertfValidity = validity;
    }

    /* (non-Javadoc)
     * @see org.eclipse.sequoyah.tfm.sign.core.extension.security.ISecurityManagement#setValues(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void setValues(IPath loc, String alias, String psswd, String strtype)
            throws SignException {

        ksType = strtype;
        aliaskey = alias;
        ksPasswrd = psswd;
        ksLocation = loc;

    }

    /* (non-Javadoc)
     * @see org.eclipse.sequoyah.tfm.sign.core.extension.security.ISecurityManagement#storeToolLocation(java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void storeToolLocation(IPath loc, IProgressMonitor monitor)
            throws SignException {
        securityProviderPrefStore.setValue(
                SunSmgmtConstants.SECURITY_TOOL_LOCATION, loc.toString());

    }

    /**
     * Get the Security Management tool from preference store location (@link
     * #securityProviderPrefStore}).
     * 
     * @return the path to the keystore tool.
     * @throws SignException when the JRE home directory is not configured
     *             correctly.
     */
    private final IPath getSecurityManagementTool() throws SignException {
        IPath securityToolLocation = getToolLocation(null);

        if ((securityToolLocation == null) || (securityToolLocation.isEmpty())) {
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
        securityToolLocation = securityToolLocation.append("bin"
                + File.separator + "keytool");
        return securityToolLocation;
    }

    /**
     * @throws SignException when the JRE home directory is not configured
     *             correctly.
     */
    private final void initializeKeytool() throws SignException {
        if (keytool == null) {
            keytool = new Keytool(getSecurityManagementTool());
        }
    }
}
