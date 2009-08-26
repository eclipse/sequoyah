/**
 * Copyright (c) 2005,2009 Nokia Corporation and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Nokia Corporation         - Initial Version
 *     Diego Sandin (Motorola)   - Porting code to TFM Sign Framework [Bug 286387]
 */
package org.eclipse.mtj.tfm.internal.sign.core.extension;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.mtj.tfm.internal.sign.core.Messages;
import org.eclipse.mtj.tfm.sign.core.SignCore;
import org.eclipse.mtj.tfm.sign.core.enumerations.ExtensionType;
import org.eclipse.mtj.tfm.sign.core.exception.SignException;
import org.eclipse.mtj.tfm.sign.core.extension.IExtension;
import org.eclipse.mtj.tfm.sign.core.extension.IExtensionManager;

/**
 * The extensibility functionalities are accessible by SignExtensionManager.
 * 
 * @since 1.0
 */
public class SignExtensionManager implements IExtensionManager {

    private static IExtensionManager instance;

    /**
     * Method is used to get reference to the SignExtensionManager -object.
     * 
     * @return
     */
    public static IExtensionManager getInstance() {
        if (instance == null) {
            instance = new SignExtensionManager();
        }

        return instance;
    }

    Properties properties = null;

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.ISignExtensionService#capitalizeIdentifier(java.lang.String)
     */
    public String capitalizeIdentifier(String _value) {

        if (_value == null) {
            return null;
        }
        java.util.StringTokenizer _utokenizer = new StringTokenizer(_value,
                "_", true); //$NON-NLS-1$
        StringBuffer _uresult = new StringBuffer();
        while (_utokenizer.hasMoreTokens()) {
            String _word = _utokenizer.nextToken();
            String _tmp = _word;
            _word = _word.toLowerCase();

            if ((_word.equals(_tmp.toUpperCase()))
                    || (!_utokenizer.hasMoreTokens())) {

                if (!_word.equals("_")) { //$NON-NLS-1$
                    _uresult.append(_word);
                }
            } else {
                _uresult.append(_word + " "); //$NON-NLS-1$
            }
        }
        return capitalize(_uresult.toString());
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.ISignExtensionService#getAllImplementations()
     */
    public IExtension[] getAllImplementations() {
        return getAllImplementations(null);
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.ISignExtensionService#getAllImplementations(java.lang.String)
     */
    public IExtension[] getAllImplementations(String project) {
        ArrayList<IExtension> l = loadExtensions(SignCore.PLUGIN_ID, null);
        IExtension[] ret = new IExtension[l.size()];
        for (int i = 0; i < l.size(); i++) {
            ret[i] = (IExtension) l.get(i);
            ret[i]
                    .setActive(isActive(ret[i].getId(), ret[i].getType(),
                            project));
        }

        return ret;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.ISignExtensionService#getImplementations(org.eclipse.mtj.tfm.sign.core.enumerations.ExtensionType, java.lang.String, java.lang.String)
     */
    public List<IExtension> getImplementations(ExtensionType extensionType,
            String version, String vendor) {
        return getImplementations(extensionType, version, vendor, true);
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.ISignExtensionService#getImplementations(org.eclipse.mtj.tfm.sign.core.enumerations.ExtensionType, java.lang.String, java.lang.String, boolean)
     */
    public List<IExtension> getImplementations(ExtensionType extensionType,
            String version, String vendor, boolean onlyActive) {
        return getImplementations(extensionType, version, vendor, null,
                onlyActive);
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.ISignExtensionService#getImplementations(org.eclipse.mtj.tfm.sign.core.enumerations.ExtensionType, java.lang.String, java.lang.String, java.lang.String)
     */
    public List<IExtension> getImplementations(ExtensionType extensionType,
            String version, String vendor, String project) {
        return getImplementations(extensionType, version, vendor, project, true);
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.ISignExtensionService#getImplementations(org.eclipse.mtj.tfm.sign.core.enumerations.ExtensionType, java.lang.String, java.lang.String, java.lang.String, boolean)
     */
    public List<IExtension> getImplementations(ExtensionType extensionType,
            String version, String vendor, String project, boolean onlyActive) {

        ArrayList<IExtension> l = loadExtensions(SignCore.PLUGIN_ID,
                extensionType);

        ArrayList<IExtension> r = new ArrayList<IExtension>();
        for (int i = 0; i < l.size(); i++) {
            IExtension ex = (IExtension) l.get(i);

            boolean active = isActive(ex.getId(), extensionType, project);

            //
            // PersistentStoreProvider must be always Active
            // TODO: Check that at least one PersistentStoreProvider must be
            // Active
            //
            if (extensionType.toString().equalsIgnoreCase(
                    "PERSISTENT_STORE_PROVIDER")) { //$NON-NLS-1$
                ex.setActive(true);
                active = true;
            } else {
                ex.setActive(active);
            }

            if (!onlyActive || active) {
                if ((vendor == null) || vendor.equals(ex.getVendor())) {

                    if ((version == null) || version.equals(ex.getVersion())) {
                        r.add(ex);
                    }
                }
            }
        }

        // Create an IExtension array from the ArrayList
        List<IExtension> ret = new ArrayList<IExtension>(r.size());
        for (int i = 0; i < r.size(); i++) {
            ret.add((IExtension) r.get(i));
        }

        return ret;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.ISignExtensionService#isActive(java.lang.String, org.eclipse.mtj.tfm.sign.core.enumerations.ExtensionType)
     */
    public boolean isActive(String id, ExtensionType type) {
        return isActive(id, type, null);
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.ISignExtensionService#isActive(java.lang.String, org.eclipse.mtj.tfm.sign.core.enumerations.ExtensionType, java.lang.String)
     */
    public boolean isActive(String id, ExtensionType type, String project) {
        Properties props = getProperties();
        if (props == null) {
            return true;
        }

        String val = (String) props.get(getPropertyName(id, type, project));
        if (val == null) {
            if (project != null) {
                return isActive(id, type, null);
            } else {
                return true;
            }
        }

        return (new Boolean(val)).booleanValue();
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.ISignExtensionService#loadExtensions(java.lang.String)
     */
    public ArrayList<IExtension> loadExtensions(String extensionName) {
        IExtensionRegistry r = Platform.getExtensionRegistry();

        IExtensionPoint[] ps = r.getExtensionPoints(SignCore.PLUGIN_ID);
        ArrayList<IExtension> l = new ArrayList<IExtension>();
        for (IExtensionPoint p : ps) {
            if ((p.getSimpleIdentifier() != null)
                    && (p.getSimpleIdentifier().equalsIgnoreCase(extensionName))) {
                IConfigurationElement[] c = p.getConfigurationElements();
                if (c != null) {
                    for (IConfigurationElement element : c) {
                        try {
                            IExtension o = (IExtension) element
                                    .createExecutableExtension("class"); //$NON-NLS-1$
                            if (o != null) {
                                l.add(o);
                            }
                        } catch (CoreException x) {
                            x.printStackTrace();
                        }
                    }
                }
            }
        }
        return l;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.ISignExtensionService#loadExtensions(java.lang.String, org.eclipse.mtj.tfm.sign.core.enumerations.ExtensionType)
     */
    public ArrayList<IExtension> loadExtensions(String plugin_id,
            ExtensionType extensionType) {
        IExtensionRegistry r = Platform.getExtensionRegistry();
        // Testing

        IExtensionPoint[] ps = r.getExtensionPoints(plugin_id);
        ArrayList<IExtension> l = new ArrayList<IExtension>();
        for (IExtensionPoint p : ps) {
            if ((p.getSimpleIdentifier() != null)
                    && ((extensionType == null) || p.getSimpleIdentifier()
                            .equalsIgnoreCase(
                                    capitalizeIdentifier(extensionType
                                            .toString())))) {
                IConfigurationElement[] c = p.getConfigurationElements();
                if (c != null) {
                    for (IConfigurationElement element : c) {
                        try {
                            IExtension o = (IExtension) element
                                    .createExecutableExtension("class"); //$NON-NLS-1$
                            if (o != null) {
                                // Testing
                                if (o instanceof IExtension) {
                                    // System.out.println(o.toString());
                                    l.add(o);
                                }
                            }
                        } catch (CoreException x) {
// System.out.println(">>>Error: " + p.getSimpleIdentifier() + " - " +
                            // x.getMessage());
                        }
                    }
                }
            }
        }
        return l;
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.ISignExtensionService#setActive(java.lang.String, org.eclipse.mtj.tfm.sign.core.enumerations.ExtensionType, boolean)
     */
    public void setActive(String id, ExtensionType type, boolean isActive)
            throws SignException {
        setActive(id, type, null, isActive);
    }

    /* (non-Javadoc)
     * @see org.eclipse.mtj.tfm.sign.core.ISignExtensionService#setActive(java.lang.String, org.eclipse.mtj.tfm.sign.core.enumerations.ExtensionType, java.lang.String, boolean)
     */
    public void setActive(String id, ExtensionType type, String project,
            boolean isActive) throws SignException {
        Properties props = getProperties();
        if (props == null) {
            throw new SignException(
                    Messages.SignExtensionManager_ErrorGettingSignProperties);
        }

        props.setProperty(getPropertyName(id, type, project), "" + isActive); //$NON-NLS-1$
        storeProperties();
    }

    /**
     * Capitalize a string - i.e. Convert something like "device management" to
     * "Device Management".
     * 
     * @param value The value to capitalize - e.g. "device management".
     * @return The capitalized value - e.g. "Device Management".
     */
    private String capitalize(String _value) {

        if (_value == null) {
            return null;
        }

        java.util.StringTokenizer _tokenizer = new StringTokenizer(_value, " "); //$NON-NLS-1$
        StringBuffer _result = new StringBuffer();

        while (_tokenizer.hasMoreTokens()) {
            StringBuffer _word = new StringBuffer(_tokenizer.nextToken());

            /*
             * Upper case first character
             */
            _word.replace(0, 1, _word.substring(0, 1).toUpperCase());

            if (!_tokenizer.hasMoreTokens()) {
                _result.append(_word);
            } else {
                _result.append(_word + " "); //$NON-NLS-1$
            }
        }
        String _tmp = _result.toString();
        _tmp = _tmp.substring(0, 1).toLowerCase() + _tmp.substring(1);

        return replacePattern(_tmp, " ", ""); //$NON-NLS-1$ //$NON-NLS-2$

    }

    private String getFileName() throws IOException {
        StringBuffer sb = new StringBuffer();

        IPath path = new Path(""); //$NON-NLS-1$
        String url = FileLocator
                .toFileURL(
                        FileLocator.find(SignCore.getDefault().getBundle(),
                                path, null)).toString();
        if (url.startsWith("file:/")) { //$NON-NLS-1$
            url = url.substring("file:/".length()); //$NON-NLS-1$
        }

        sb.append(url);
        sb.append("mtj.properties"); //$NON-NLS-1$

        return sb.toString();
    }

    private Properties getProperties() {
        if (properties == null) {
            properties = new Properties();
            try {
                properties.load(new FileInputStream(getFileName()));
            } catch (FileNotFoundException e) {
                return properties;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        return properties;
    }

    private String getPropertyName(String id, ExtensionType type, String project) {
        return "plugin." + type.toString() + "_" + id + "." //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                + (project != null ? project : "all") + ".active"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Replaces all occurrences of a given pattern with a replacement string.
     * Thus replacePattern("hey you!", " ",""); returns in "heyyou".
     * 
     * @param _original
     * @param _pattern
     * @param _replacement
     * @return
     */
    private String replacePattern(String _original, String _pattern,
            String _replacement) {
        StringTokenizer _strtok = new StringTokenizer(_original, _pattern, true);
        StringBuffer _result = new StringBuffer();

        if (_replacement == null) {
            _replacement = ""; //$NON-NLS-1$
        }
        while (_strtok.hasMoreTokens()) {
            String _token = _strtok.nextToken();

            if (_token.equals(_pattern)) {
                _result.append(_replacement);
            } else {
                _result.append(_token);
            }
        }
        return _result.toString();
    }

    private void storeProperties() throws SignException {
        if (properties != null) {
            try {
                properties.store(new FileOutputStream(getFileName()), ""); //$NON-NLS-1$
            } catch (FileNotFoundException e) {
                try {
                    File file = new File(getFileName());
                    file.mkdirs();
                    file.createNewFile();
                    properties.store(new FileOutputStream(getFileName()), ""); //$NON-NLS-1$
                } catch (Exception ex) {
                    throw new SignException(
                            Messages.SignExtensionManager_ErrorStoringSignProperties,
                            e);
                }
            } catch (IOException e) {
                throw new SignException(
                        Messages.SignExtensionManager_ErrorStoringSignProperties,
                        e);
            }
        }
    }

}
