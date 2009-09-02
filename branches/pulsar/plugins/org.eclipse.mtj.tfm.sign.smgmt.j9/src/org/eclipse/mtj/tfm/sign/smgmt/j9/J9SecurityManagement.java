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

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.mtj.tfm.internal.sign.smgmt.j9.J9SmgmtConstants;
import org.eclipse.mtj.tfm.internal.sign.smgmt.j9.Messages;
import org.eclipse.mtj.tfm.sign.core.SignErrors;
import org.eclipse.mtj.tfm.sign.core.enumerations.ExtensionType;
import org.eclipse.mtj.tfm.sign.core.exception.SignException;
import org.eclipse.mtj.tfm.sign.core.extension.ExtensionImpl;
import org.eclipse.mtj.tfm.sign.core.extension.security.ISecurityManagement;
import org.eclipse.mtj.tfm.sign.core.extension.security.Keytool;
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
    private IPath ksLocation = Path.EMPTY; //$NON-NLS-1$

    /** Keystore Password */
    private String ksPasswrd = null;

    /** Java KeyStore (JKS) */
    private String ksType = "JKS"; //$NON-NLS-1$

    private Keytool keytool;

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
        securityProviderPrefStore
                .addPropertyChangeListener(new IPropertyChangeListener() {

                    public void propertyChange(PropertyChangeEvent event) {
                        if (event.getProperty().equals(
                                J9SmgmtConstants.SECURITY_TOOL_LOCATION)) {
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
     * @see org.eclipse.mtj.tfm.sign.core.extension.security.ISecurityManagement#changeStorePassword(java.lang.String, java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
     */
    public boolean changeStorePassword(String newStorePass, String storePass,
            IProgressMonitor monitor) throws SignException {

        boolean cmdSuccessful = true;

        initializeKeytool();

        String[] cmdArgs = keytool.generateChangeStorePasswordCmd(newStorePass,
                ksType, ksLocation, storePass);

        Process p = keytool.execute(cmdArgs);
        ;
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
        initializeKeytool();
        X500DName dName = new X500DName(commonName, orgUnit, orgName,
                localityName, stateName, country);

        String[] cmdArgs = keytool
                .generateNewKeyCmd(
                        dName,
                        "RSA", "SHA1withRSA", ksCertfValidity, alias, ksPasswrd, ksType, ksLocation, ksPasswrd); //$NON-NLS-1$ //$NON-NLS-2$

        Process p = keytool.execute(cmdArgs);
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
        initializeKeytool();

        String[] cmdArgs = keytool.generateDeleteKeyCmd(aliaskey, ksType,
                ksLocation, ksPasswrd);

        Process p = keytool.execute(cmdArgs);
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
        initializeKeytool();

        String[] cmdArgs = keytool.generateGenerateCSRCmd(certFile, aliaskey,
                ksType, ksLocation, ksPasswrd);
        Process p = keytool.execute(cmdArgs);

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
                initializeKeytool();

                String[] cmdArgs = keytool.generateDisplayCertifcates(aliaskey,
                        ksType, ksLocation, ksPasswrd);
                Process p = keytool.execute(cmdArgs);

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
    public IPath getKeyStoreNameLoc() throws SignException {
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
    public IPath getToolLocation(IProgressMonitor monitor) throws SignException {

        IPath path = new Path(securityProviderPrefStore
                .getString(J9SmgmtConstants.SECURITY_TOOL_LOCATION));
        return path;
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
        initializeKeytool();

        String[] cmdArgs = keytool.generateImportSignedCertCmd(certFile,
                aliaskey, ksPasswrd, ksType, ksLocation, ksPasswrd);
        Process p = keytool.execute(cmdArgs);

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

        if ((ksLocation == null) || (ksLocation.isEmpty()))
            return false;

        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.security.ISecurityManagement#openKeyStore(java.lang.String, java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
     */
    public String[] openKeyStore(IPath keyStore, String storePass,
            IProgressMonitor monitor) throws SignException {
        monitor.beginTask(Messages.J9SecurityManager_Opening_keystore, 100);

        initializeKeytool();

        String[] cmdArgs = keytool.generateOpenKeyStoreCmd(ksType, keyStore,
                storePass);
        Process p = keytool.execute(cmdArgs);

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
    public void setKeyStoreNameLoc(IPath keyStoreNameLoc) throws SignException {
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
    public void setValues(IPath loc, String alias, String psswd, String strtype)
            throws SignException {

        ksType = strtype;
        aliaskey = alias;
        ksPasswrd = psswd;
        ksLocation = loc;

    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.extension.security.ISecurityManagement#storeToolLocation(java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void storeToolLocation(IPath loc, IProgressMonitor monitor)
            throws SignException {
        securityProviderPrefStore.setValue(
                J9SmgmtConstants.SECURITY_TOOL_LOCATION, loc.toString());
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