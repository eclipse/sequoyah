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
package org.eclipse.sequoyah.tfm.sign.core.extension.security;

/**
 * X.500 Distinguished Names are used to identify entities, such as those which
 * are named by the subject and issuer (signer) fields of X.509 certificates.
 * 
 * @author Diego Sandin
 * @since 1.0
 */
public class X500DName {

    public static final String COMMON_NAME_PREFIX = "CN="; //$NON-NLS-1$
    public static final String COUNTRY_PREFIX = "C="; //$NON-NLS-1$
    public static final String LOCALITY_NAME_PREFIX = "L="; //$NON-NLS-1$
    public static final String ORGANIZATION_NAME_PREFIX = "O="; //$NON-NLS-1$
    public static final String ORGANIZATION_UNIT_PREFIX = "OU="; //$NON-NLS-1$
    public static final String STATE_NAME_PREFIX = "S="; //$NON-NLS-1$

    // constants
    private static final String COMMA = ","; //$NON-NLS-1$
    private static final String COMMA_AND_SPACE = ", "; //$NON-NLS-1$
    private static final String ESCAPED_COMMA = "\\,"; //$NON-NLS-1$
    private static final String QUOTE = "\""; //$NON-NLS-1$

    /**
     * Common name of a person, e.g., "Susan Jones"
     */
    private String commonName;

    /**
     * Two-letter country code, e.g., "CH"
     */
    private String country;

    /**
     * Locality (city) name, e.g., "Palo Alto"
     */
    private String localityName;

    /**
     * large organization name, e.g., "ABCSystems, Inc."
     */
    private String organizationName;

    /**
     * Small organization (e.g, department or division) name, e.g., "Purchasing"
     */
    private String organizationUnit;

    /**
     * State or province name, e.g., "California"
     */
    private String stateName;

    /**
     * Creates a new instance of X500DName.
     * 
     * @param commonName common name of a person, e.g., "Susan Jones"
     * @param organizationUnit small organization (e.g, department or division)
     *            name, e.g., "Purchasing"
     * @param organizationName large organization name, e.g., "ABCSystems, Inc."
     * @param localityName locality (city) name, e.g., "Palo Alto"
     * @param stateName state or province name, e.g., "California"
     * @param country Two-letter country code, e.g., "CH"
     */
    public X500DName(String commonName, String organizationUnit,
            String organizationName, String localityName, String stateName,
            String country) {
        this.commonName = commonName;
        this.organizationUnit = organizationUnit;
        this.organizationName = organizationName;
        this.localityName = localityName;
        this.stateName = stateName;
        this.country = country;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof X500DName))
            return false;
        X500DName other = (X500DName) obj;
        if (commonName == null) {
            if (other.commonName != null)
                return false;
        } else if (!commonName.equals(other.commonName))
            return false;
        if (country == null) {
            if (other.country != null)
                return false;
        } else if (!country.equals(other.country))
            return false;
        if (localityName == null) {
            if (other.localityName != null)
                return false;
        } else if (!localityName.equals(other.localityName))
            return false;
        if (organizationName == null) {
            if (other.organizationName != null)
                return false;
        } else if (!organizationName.equals(other.organizationName))
            return false;
        if (organizationUnit == null) {
            if (other.organizationUnit != null)
                return false;
        } else if (!organizationUnit.equals(other.organizationUnit))
            return false;
        if (stateName == null) {
            if (other.stateName != null)
                return false;
        } else if (!stateName.equals(other.stateName))
            return false;
        return true;
    }

    /**
     * @return the commonName
     */
    public synchronized String getCommonName() {
        return commonName;
    }

    /**
     * @return the country
     */
    public synchronized String getCountry() {
        return country;
    }

    /**
     * @return the localityName
     */
    public synchronized String getLocalityName() {
        return localityName;
    }

    /**
     * @return the organizationName
     */
    public synchronized String getOrganizationName() {
        return organizationName;
    }

    /**
     * @return the organizationUnit
     */
    public synchronized String getOrganizationUnit() {
        return organizationUnit;
    }

    /**
     * @return the stateName
     */
    public synchronized String getStateName() {
        return stateName;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((commonName == null) ? 0 : commonName.hashCode());
        result = prime * result + ((country == null) ? 0 : country.hashCode());
        result = prime * result
                + ((localityName == null) ? 0 : localityName.hashCode());
        result = prime
                * result
                + ((organizationName == null) ? 0 : organizationName.hashCode());
        result = prime
                * result
                + ((organizationUnit == null) ? 0 : organizationUnit.hashCode());
        result = prime * result
                + ((stateName == null) ? 0 : stateName.hashCode());
        return result;
    }

    /**
     * @param commonName the commonName to set
     */
    public synchronized void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    /**
     * @param country the country to set
     */
    public synchronized void setCountry(String country) {
        this.country = country;
    }

    /**
     * @param localityName the localityName to set
     */
    public synchronized void setLocalityName(String localityName) {
        this.localityName = localityName;
    }

    /**
     * @param organizationName the organizationName to set
     */
    public synchronized void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    /**
     * @param organizationUnit the organizationUnit to set
     */
    public synchronized void setOrganizationUnit(String organizationUnit) {
        this.organizationUnit = organizationUnit;
    }

    /**
     * @param stateName the stateName to set
     */
    public synchronized void setStateName(String stateName) {
        this.stateName = stateName;
    }

    /**
     * Returns a string representation of the <b>X.500 Distinguished Name</b>.<br>
     * <b>Note:</b> If a distinguished name string value contains a comma, the
     * comma will be escaped by a "\" character.
     * 
     * @return the generated string in the following format:
     * 
     * <pre>
     * "CN=<b><i>commonName</i></b>, OU=<b><i>organizationUnit</i></b>, O=<b><i>organizationName</i></b>, L=<b><i>localityName</i></b>, S=<b><i>stateName</i></b>, C=<b><i>country</i></b>" 
     * </pre>
     */
    @Override
    public String toString() {
        String tempCommonName = commonName.replace(COMMA, ESCAPED_COMMA);
        String tempOrganizationUnit = organizationUnit.replace(COMMA, ESCAPED_COMMA);
        String tempOrganizationName = organizationName.replace(COMMA, ESCAPED_COMMA);
        String tempLocalityName = localityName.replace(COMMA, ESCAPED_COMMA);
        String tempStateName = stateName.replace(COMMA, ESCAPED_COMMA);
        String tempCountry = country.replace(COMMA, ESCAPED_COMMA);

        StringBuilder dname = new StringBuilder();
        dname.append(QUOTE).append(COMMON_NAME_PREFIX).append(tempCommonName)
                .append(COMMA_AND_SPACE).append(ORGANIZATION_UNIT_PREFIX)
                .append(tempOrganizationUnit).append(COMMA_AND_SPACE).append(
                        ORGANIZATION_NAME_PREFIX).append(tempOrganizationName)
                .append(COMMA_AND_SPACE).append(LOCALITY_NAME_PREFIX).append(
                        tempLocalityName).append(COMMA_AND_SPACE).append(
                        STATE_NAME_PREFIX).append(tempStateName).append(
                        COMMA_AND_SPACE).append(COUNTRY_PREFIX).append(
                        tempCountry).append(QUOTE);
        return dname.toString();
    }
}
