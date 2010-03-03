/**
 * Copyright (c) 2009 Motorola.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Diego Sandin (Motorola) - Initial Version
 */
package org.eclipse.mtj.tfm.sign.core.extension.security;

import java.util.ArrayList;

import org.eclipse.core.runtime.IPath;
import org.eclipse.mtj.tfm.sign.core.SignErrors;
import org.eclipse.mtj.tfm.sign.core.exception.SignException;

/**
 * This is a wrapper class for keytool(Key and Certificate Management Tool)
 * which is a key and certificate management utility used during the process of
 * signing Java ME and Android Applications.
 * 
 * @author Diego Sandin
 * @since 1.0
 */
public class Keytool {

    /**
     * Changes the password used to protect the integrity of the keystore
     * contents.
     */
    public static final String P_CHANGE_STORE_PASSWD = "-storepasswd"; //$NON-NLS-1$

    /**
     * Deletes from the keystore the entry identified by an alias.
     */
    public static final String P_DELETE_KEY = "-delete"; //$NON-NLS-1$

    /**
     * Reads (from the keystore) the certificate associated with an alias, and
     * stores it in the file.
     */
    public static final String P_GENERATE_CSR = "-export"; //$NON-NLS-1$

    /**
     * Generates a key pair (a public key and associated private key).
     */
    public static final String P_GENERATE_KEY = "-genkey"; //$NON-NLS-1$

    /**
     * Reads a certificate or certificate chain (where the latter is supplied in
     * a PKCS#7 formatted reply) from a file, and stores it in the keystore
     * entry identified an alias.
     */
    public static final String P_IMPORT_CERT = "-import"; //$NON-NLS-1$

    /**
     * Prints (to stdout) the contents of the keystore entry identified by an
     * alias. If no alias is specified, the contents of the entire keystore are
     * printed.
     */
    public static final String P_LIST = "-list"; //$NON-NLS-1$

    // Command Arguments
    public static final String S_ALIAS = "-alias"; //$NON-NLS-1$
    public static final String S_DNAME = "-dname"; //$NON-NLS-1$
    public static final String S_FILE = "-file"; //$NON-NLS-1$
    public static final String S_JAVAOPTION = "-J"; //$NON-NLS-1$
    public static final String S_KEYALG = "-keyalg"; //$NON-NLS-1$
    public static final String S_KEYPASS = "-keypass"; //$NON-NLS-1$
    public static final String S_KEYSIZE = "-keysize"; //$NON-NLS-1$
    public static final String S_KEYSTORE = "-keystore"; //$NON-NLS-1$
    public static final String S_NEWSTOREPASS = "-new"; //$NON-NLS-1$
    public static final String S_NOPROMPT = "-noprompt"; //$NON-NLS-1$
    public static final String S_PROVIDER = "-provider"; //$NON-NLS-1$
    public static final String S_SIGALG = "-sigalg"; //$NON-NLS-1$
    public static final String S_STOREPASS = "-storepass"; //$NON-NLS-1$
    public static final String S_STORETYPE = "-storetype"; //$NON-NLS-1$
    public static final String S_VALIDITY = "-validity"; //$NON-NLS-1$
    public static final String S_VERBOSE = "-v"; //$NON-NLS-1$

    /**
     * The Keytool location in the file system.
     */
    private IPath location;

    /**
     * Creates a new instance of Keytool.
     */
    public Keytool(IPath location) {
        this.location = location;
    }

    /**
     * Executes keytool with the specified arguments in a separate process.
     * 
     * @param args array containing the command to call and its arguments.
     * @return A new Process object for managing the subprocess
     * @throws SignException if fails to create the new process.
     */
    public synchronized Process execute(String[] args) throws SignException {

        Process p = null;

        try {
            p = Runtime.getRuntime().exec(args);
        } catch (Exception e) {
            throw new SignException(SignErrors
                    .getErrorMessage(SignErrors.GENERIC_SECURITY_ERROR), e);
        }

        if (p == null) {
            StringBuffer str = new StringBuffer(""); //$NON-NLS-1$

            for (String element : args) {
                str.append(" " + element); //$NON-NLS-1$
            }

            throw new SignException(SignErrors
                    .getErrorMessage(SignErrors.GENERIC_SECURITY_ERROR)
                    + "Could not execute [" + str + " ]"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return p;
    }

    /**
     * Create the command line to change the password used to protect the
     * integrity of the keystore contents.
     * 
     * @param newStorePass the new keystore password. <b>Restriction:</b>The
     *            password must be at least 6 characters long.
     * @param storeType specifies the type of keystore to be instantiated.
     * @param keystore The keystore location.
     * @param storePass the current keystore password.
     * @param otherArgs extra arguments to be appended in the default command.
     *            The number of arguments is variable and may be zero. The
     *            maximum number of arguments is limited by the maximum
     *            dimension of a Java array as defined by the Java Virtual
     *            Machine Specification. A <code>null</code> argument wont throw
     *            exception.
     * @return the generated command line string in the following format: <br>
     * <br>
     *         <code>
     *         -storepasswd -new <b>newStorePass</b> -storetype <b>storeType</b>
     *         -keystore <b>keystore</b> -storepass <b>storePasswd</b> <i>{otherArgs}</i>
     *         </code> <br>
     * <br>
     *         All <code>null</code> arguments wont be included.
     * @throws SignException in case newStorePass argument is invalid.
     */
    public String[] generateChangeStorePasswordCmd(String newStorePass,
            String storeType, IPath keystore, String storePass,
            String... otherArgs) throws SignException {

        ArrayList<String> cmd = new ArrayList<String>();
        cmd.add(P_CHANGE_STORE_PASSWD);
        cmd.add(S_NEWSTOREPASS);

        if ((newStorePass != null) && (newStorePass.length() >= 6)) {
            cmd.add(newStorePass);
        } else {
            throw new SignException(SignErrors
                    .getErrorMessage(SignErrors.SECURITY_MALFORMED_PASSWORD)
                    + " The password must be at least 6 characters long.");
        }

        cmd.addAll(generateSecondaryCommands(null, null, storeType, keystore,
                storePass, otherArgs));

        return cmd.toArray(new String[cmd.size()]);
    }

    /**
     * Create the command line to delete from the keystore the entry identified
     * by an alias.
     * 
     * @param alias an alias that identify an entry in the keystore.
     * @param storeType specifies the type of keystore to be instantiated.
     * @param keystore The keystore location.
     * @param storePass the keystore password.
     * @param otherArgs extra arguments to be appended in the default command.
     *            The number of arguments is variable and may be zero. The
     *            maximum number of arguments is limited by the maximum
     *            dimension of a Java array as defined by the Java Virtual
     *            Machine Specification. A <code>null</code> argument wont throw
     *            exception.
     * @return the generated command line string in the following format: <br>
     * <br>
     *         <code>
     *         -delete -alias alias -storetype storeType -keystore keystore 
     *         -storepass storePass <i>{otherArgs}</i></code> <br>
     * <br>
     *         All <code>null</code> arguments wont be included.
     */
    public String[] generateDeleteKeyCmd(String alias, String storeType,
            IPath keystore, String storePass, String... otherArgs) {

        ArrayList<String> cmd = new ArrayList<String>();

        cmd.add(P_DELETE_KEY);
        cmd.addAll(generateSecondaryCommands(alias, null, storeType, keystore,
                storePass, otherArgs));
        return cmd.toArray(new String[cmd.size()]);
    }

    /**
     * Create the command line to display the contents of the keystore entry
     * identified by an alias.
     * 
     * @param alias an alias that identify an entry in the keystore.
     * @param storeType specifies the type of keystore to be instantiated.
     * @param keystore The keystore location.
     * @param storePass the keystore password.
     * @param otherArgs extra arguments to be appended in the default command.
     *            The number of arguments is variable and may be zero. The
     *            maximum number of arguments is limited by the maximum
     *            dimension of a Java array as defined by the Java Virtual
     *            Machine Specification. A <code>null</code> argument wont throw
     *            exception.
     * @return the generated command line string in the following format: <br>
     * <br>
     *         <code>
     *         -list -alias alias -storetype storeType -keystore keystore 
     *         -storepass storePass <i>{otherArgs}</i></code> <br>
     * <br>
     *         All <code>null</code> arguments wont be included.
     */
    public String[] generateDisplayCertifcates(String alias, String storeType,
            IPath keystore, String storePass, String... otherArgs) {

        ArrayList<String> cmd = new ArrayList<String>();

        cmd.add(P_LIST);
        cmd.addAll(generateSecondaryCommands(alias, null, storeType, keystore,
                storePass, otherArgs));

        return cmd.toArray(new String[cmd.size()]);
    }

    /**
     * Create the command line to read from the keystore the certificate
     * associated with an alias, and stores it in the file certFile.
     * 
     * @param file where to store the certificate associated with the informed
     *            alias.
     * @param alias an alias that identify an entry in the keystore.
     * @param storeType specifies the type of keystore to be instantiated.
     * @param keystore The keystore location.
     * @param storePass the keystore password.
     * @param otherArgs extra arguments to be appended in the default command.
     *            The number of arguments is variable and may be zero. The
     *            maximum number of arguments is limited by the maximum
     *            dimension of a Java array as defined by the Java Virtual
     *            Machine Specification. A <code>null</code> argument wont throw
     *            exception.
     * @return the generated command line string in the following format: <br>
     * <br>
     *         <code>
     *         -export -file <b>file</b> -alias <b>alias</b> -storetype <b>storeType</b> 
     *         -keystore <b>keystore</b> -storepass <b>storePass</b> 
     *         <i>{otherArgs}</i></code> <br>
     * <br>
     *         All <code>null</code> arguments wont be included.
     */
    public String[] generateGenerateCSRCmd(String file, String alias,
            String storeType, IPath keystore, String storePass,
            String... otherArgs) {

        ArrayList<String> cmd = new ArrayList<String>();

        cmd.add(P_GENERATE_CSR);
        cmd.add(S_FILE);
        cmd.add(file);

        cmd.addAll(generateSecondaryCommands(alias, null, storeType, keystore,
                storePass, otherArgs));
        return cmd.toArray(new String[cmd.size()]);

    }

    /**
     * Create the command line to read the certificate from the a file, and
     * stores it in the keystore entry identified by an alias.
     * 
     * @param file where to store the certificate associated with the informed
     *            alias.
     * @param alias an alias that identify an entry in the keystore.
     * @param keypass password used to protect the private key of a generated
     *            key pair.
     * @param storeType specifies the type of keystore to be instantiated.
     * @param keystore The keystore location.
     * @param storePass the keystore password.
     * @param otherArgs extra arguments to be appended in the default command.
     *            The number of arguments is variable and may be zero. The
     *            maximum number of arguments is limited by the maximum
     *            dimension of a Java array as defined by the Java Virtual
     *            Machine Specification. A <code>null</code> argument wont throw
     *            exception.
     * @return the generated command line string in the following format: <br>
     * <br>
     *         <code>
     *         -import -noprompt -file <b>certFile</b> -alias alias -keypass keypass
     *          -storetype storeType -keystore keystore -storepass storePass
     *           <i>{otherArgs}</i></code> <br>
     * <br>
     *         All <code>null</code> arguments wont be included.
     */
    public String[] generateImportSignedCertCmd(String file, String alias,
            String keypass, String storeType, IPath keystore, String storePass,
            String... otherArgs) {

        ArrayList<String> cmd = new ArrayList<String>();

        cmd.add(P_IMPORT_CERT);
        cmd.add(S_NOPROMPT);
        cmd.add(S_FILE);
        cmd.add(file);
        cmd.addAll(generateSecondaryCommands(alias, keypass, storeType,
                keystore, storePass, otherArgs));

        return cmd.toArray(new String[cmd.size()]);
    }

    /**
     * Create the command line to generate a key pair (a public key and
     * associated private key).
     * 
     * @param dname specifies the X.500 Distinguished Name to be associated with
     *            alias, and is used as the issuer and subject fields in the
     *            self-signed certificate.
     * @param keyAlg specifies the algorithm to be used to generate the key
     *            pair.
     * @param sigAlg specifies the algorithm that should be used to sign the
     *            self-signed certificate; this algorithm must be compatible
     *            with keyalg.
     * @param validity the expected period that entities can rely on the public
     *            value, if the associated private key has not been compromised.
     * @param alias an alias that identify an entry in the keystore.
     * @param keypass password used to protect the private key of a generated
     *            key pair.
     * @param storeType specifies the type of keystore to be instantiated.
     * @param keystore The keystore location.
     * @param storePass the keystore password.
     * @param otherArgs extra arguments to be appended in the default command.
     *            The number of arguments is variable and may be zero. The
     *            maximum number of arguments is limited by the maximum
     *            dimension of a Java array as defined by the Java Virtual
     *            Machine Specification. A <code>null</code> argument wont throw
     *            exception.
     * @return the generated command line string in the following format: <br>
     * <br>
     *         <code>
     *         -genkey -dname <b>dname</b> -keyalg <b>keyAlg</b> -sigalg <b>sigAlg</b> 
     *         -validity <b>validity</b> -alias <b>alias</b>
     *         -keypass <b>keypass</b> -storetype <b>storeType</b> 
     *          -keystore <b>keystore</b> -storepass <b>storePass</b> <i>{otherArgs}</i></code>
     * <br>
     * <br>
     *         All <code>null</code> arguments wont be included.
     */
    public String[] generateNewKeyCmd(X500DName dname, String keyAlg,
            String sigAlg, String validity, String alias, String keypass,
            String storeType, IPath keystore, String storePass,
            String... otherArgs) {

        ArrayList<String> cmd = new ArrayList<String>();

        cmd.add(P_GENERATE_KEY);
        cmd.add(S_DNAME);
        cmd.add(dname.toString());
        cmd.add(S_KEYALG);
        cmd.add(keyAlg);
        cmd.add(S_SIGALG);
        cmd.add(sigAlg);
        cmd.add(S_VALIDITY);
        cmd.add(validity);

        cmd.addAll(generateSecondaryCommands(alias, keypass, storeType,
                keystore, storePass, otherArgs));

        return cmd.toArray(new String[cmd.size()]);
    }

    /**
     * Create the command line to open a Key Store and display its contents.
     * 
     * @param storeType specifies the type of keystore to be instantiated.
     * @param keystore The keystore location.
     * @param storePass the keystore password.
     * @param otherArgs extra arguments to be appended in the default command.
     *            The number of arguments is variable and may be zero. The
     *            maximum number of arguments is limited by the maximum
     *            dimension of a Java array as defined by the Java Virtual
     *            Machine Specification. A <code>null</code> argument wont throw
     *            exception.
     * @return the generated command line string in the following format: <br>
     * <br>
     *         <code>
     *         -list -storetype storeType -keystore keystore 
     *         -storepass storePass <i>{otherArgs}</i></code> <br>
     * <br>
     *         All <code>null</code> arguments wont be included.
     */
    public String[] generateOpenKeyStoreCmd(String storeType, IPath keystore,
            String storePass, String... otherArgs) throws SignException {

        ArrayList<String> cmd = new ArrayList<String>();

        cmd.add(P_LIST);
        cmd.addAll(generateSecondaryCommands(null, null, storeType, keystore,
                storePass, otherArgs));

        return cmd.toArray(new String[cmd.size()]);
    }

    /**
     * Create the command line with the default Secondary Commands.
     * 
     * @param alias an alias that identify an entry in the keystore.
     * @param keypass password used to protect the private key of a generated
     *            key pair.
     * @param storeType specifies the type of keystore to be instantiated.
     * @param keystore The keystore location.
     * @param storePass the keystore password.
     * @param otherArgs extra arguments to be appended in the default command.
     *            The number of arguments is variable and may be zero. The
     *            maximum number of arguments is limited by the maximum
     *            dimension of a Java array as defined by the Java Virtual
     *            Machine Specification. A <code>null</code> argument wont throw
     *            exception.
     * @return the generated command line string in the following format: <br>
     * <br>
     *         <code>
     *         -alias <b>alias</b> -keypass <b>keypass</b> -storetype <b>storeType</b> 
     *         -keystore <b>keystore</b> -storepass <b>storePass</b> 
     *         <i>{otherArgs}</i></code> <br>
     * <br>
     *         All <code>null</code> arguments wont be included.
     */
    public ArrayList<String> generateSecondaryCommands(String alias,
            String keypass, String storeType, IPath keystore, String storePass,
            String... otherArgs) {

        ArrayList<String> cmd = new ArrayList<String>();

        if (alias != null) {
            cmd.add(S_ALIAS);
            cmd.add(alias);
        }

        if (keypass != null) {
            cmd.add(S_KEYPASS);
            cmd.add(keypass);
        }

        if (storeType != null) {
            cmd.add(S_STORETYPE);
            cmd.add(storeType);
        }

        if (keystore != null) {
            cmd.add(S_KEYSTORE);
            cmd.add(keystore.toOSString());
        }

        if (storePass != null) {
            cmd.add(S_STOREPASS);
            cmd.add(storePass);
        }

        if (otherArgs != null) {
            for (String arg : otherArgs) {
                cmd.add(arg);
            }
        }

        return cmd;
    }

    /**
     * Gets the location of the Keytool in the file system.
     * 
     * @return the Keytool location in the file system.
     */
    public synchronized IPath getLocation() {
        return location;
    }

    /**
     * Sets the location of the Keytool in the file system.
     * 
     * @param location the Keytool location in the file system.
     */
    public synchronized void setLocation(IPath location) {
        this.location = location;
    }
}
