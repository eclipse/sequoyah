/********************************************************************************
 * Copyright (c) 2009-2010 Motorola Mobility, Inc.
 * All rights reserved. This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Matheus Tait Lima (Eldorado)
 * 
 * Contributors:
 * name (company) - description.
 * Marcel Gorri (Eldorado) - Bug 325110 - Add support to new Android Localization qualifiers
 * Marcel Gorri (Eldorado) - Bug 325630 - Fix validation of some Android localization qualifiers
 * 
 ********************************************************************************/
package org.eclipse.sequoyah.localization.pde;

import java.awt.Dimension;
import java.util.HashMap;

import org.eclipse.sequoyah.localization.pde.i18n.Messages;
import org.eclipse.sequoyah.localization.tools.datamodel.LocaleAttribute;

public class PDELocaleAttribute extends LocaleAttribute {

	public enum PDELocaleAttributes {
		COUNTRY_CODE, LANGUAGE
	};

	//O que seria esse tipo PDE?
	//O tipo do atributo que está sendo traduzido
	private int pdeType;

	private String stringValue = ""; //$NON-NLS-1$

	//Verifica se os atributos estão setados devidamente
	protected boolean isSet = false;

	/***
	 * Creates a new AndroidLocaleAttribute
	 * 
	 * @param value
	 * @param androidType
	 */
	public PDELocaleAttribute(Object value, int pdeType) {
		super("", LocaleAttribute.STRING_TYPE, 0, 0, null, "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		this.pdeType = pdeType;
		setPdeValue(value);
		isSet = false;
	}

	/***
	 * Creates an PDE Language Attribute based on a pde resource
	 * qualifier.
	 * 
	 * @param qualifier
	 */
	// What is qualifier?
	public PDELocaleAttribute(String qualifier, int type) {
		super("", LocaleAttribute.STRING_TYPE, 0, 0, null, "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		pdeType = type;
		Object value = getValueAndTypeFromQualifier(qualifier);
		setPdeValue(value);
	}

	/***
	 * Gets the PDE type.
	 * 
	 * @return
	 */
	public int getPdeType() {
		return pdeType;
	}

	/***
	 * Gets the string to be used to compose folder names.
	 * 
	 * @param value
	 * @return
	 */
	// Why we use "mcc = mobile country"?
	private String getCountryCodeFolder(String value) {
		return value; //$NON-NLS-1$
	}

	/***
	 * Gets the string to be used to compose folder names.
	 * 
	 * @param value
	 * @return
	 */
	private String getLanguageFolder(String value) {
		return value.toLowerCase();
	}

	/***
	 * Gets the string to be used to compose folder names.
	 * 
	 * @param value
	 * @return
	 */
	public String getStringValue() {
		return stringValue;
	}

	/***
	 * - Parse the qualifier in order to retrieve necessary information to be
	 * used for other methods of this class.
	 * 
	 * - Sets the attribute type according to the qualifier type.
	 * 
	 * @param qualifier
	 * @return
	 */
	private Object getValueAndTypeFromQualifier(String strValue) {
		Object result = null;
		// return 0
		//Pq tem esse substring?
		if (pdeType == PDELocaleAttributes.COUNTRY_CODE.ordinal()) {
			result = strValue.substring(3);
		// return 1
		} else if (pdeType == PDELocaleAttributes.LANGUAGE.ordinal()) {
			result = strValue;
		}
		return result;
	}

	/***
	 * Checks if this attribute is set.
	 * 
	 * All attributes may exist for a given language but they will only be used
	 * for creating the path if they are set.
	 */
	public boolean isSet() {
		return isSet;
	}

	/***
	 * Sets the values of this attribute.
	 * 
	 * The object received as param will be parsed according to the android type
	 * of the attribute.
	 * 
	 * So, this method WILL fail if you pass an attribute type that is not the
	 * one expected according to the type of the attribute. For instance, if the
	 * atribute type is Dimension, so the object of the parameter MUST be a
	 * Dimension.
	 * 
	 * @param value
	 */
	public void setPdeValue(Object value) {

		if (pdeType == PDELocaleAttributes.COUNTRY_CODE.ordinal()) {
			setCountryCodeNode(value);
		}else if (pdeType == PDELocaleAttributes.LANGUAGE.ordinal()) {
			setLanguageNode(value);
		}else {
			throw new IllegalArgumentException(Messages.Unknown_PDE_Type);
		}
		isSet = true;
	}
	
	/**
	 * Sets the type and values of this attribute according to the object
	 * received.
	 * 
	 * @param value
	 */
	public void setIntValue(Object value) {

		Integer intValue = -1;
		if (value instanceof Integer) {
			intValue = (Integer) value;
		} else if (value instanceof String) {
			intValue = Integer.parseInt((String) value);
		} else {
			throw new IllegalArgumentException(Messages.Invalid_PDE_Value);
		}

		if (fixedSize > 0) {
			if (intValue.toString().length() != fixedSize) {
				throw new IllegalArgumentException(
						Messages.Invalid_PDE_Value_Size + fixedSize);
			}
		}

		if (maximumSize > 0) {
			if (intValue.toString().length() > maximumSize) {
				throw new IllegalArgumentException(
						Messages.Invalid_PDE_Value_Size + maximumSize);
			}
		}

		displayValue = intValue.toString();
	}

	/***
	 * Sets this attribute.
	 * 
	 * All attributes may exist for a given language but they will only be used
	 * for creating the path if they are set.
	 */
	public void setAttribute() {
		isSet = true;
	}

	/**
	 * Sets the type and values of this attribute according to the object
	 * received.
	 * 
	 * @param value
	 */
	private void setCountryCodeNode(Object value) {
		displayName = "Country Code"; //$NON-NLS-1$
		type = LocaleAttribute.STRING_TYPE;
		fixedSize = 3;
		maximumSize = 3;
		allowedValues = null;
		setIntValue(value);
		folderValue = getCountryCodeFolder(displayValue);
	}

	
	/**
	 * Sets the type and values of this attribute according to the object
	 * received.
	 * 
	 * @param value
	 */
	private void setLanguageNode(Object value) {
		displayName = "Language"; //$NON-NLS-1$
		type = LocaleAttribute.STRING_TYPE;
		fixedSize = 2;
		maximumSize = 2;
		allowedValues = null;
		setStringValue(value);
		folderValue = getLanguageFolder(displayValue);
	}

	/**
	 * Sets the type and values of this attribute according to the object
	 * received.
	 * 
	 * @param value
	 */
	private void setStringValue(Object value) {
		if (!(value instanceof String)) {
			throw new IllegalArgumentException(Messages.Invalid_PDE_Value);
		}

		if (type == FIXED_TEXT_TYPE) {
			setValuesBasedOnDisplayValue((String) value);
		} else {
			if (fixedSize > 0) {
				if (((String) value).length() != fixedSize) {
					throw new IllegalArgumentException(
							Messages.Invalid_PDE_Value_Size + fixedSize);
				}
			}

			if (maximumSize > 0) {
				if (((String) value).length() > maximumSize) {
					throw new IllegalArgumentException(
							Messages.Invalid_PDE_Value_Size + maximumSize);
				}
			}

			displayValue = (String) value;
		}

	}

	/**
	 * Sets the type and values of this attribute according to the object
	 * received.
	 * 
	 * @param value
	 */
	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	
	/***
	 * Unsets this attribute.
	 * 
	 * All attributes may exist for a given language but they will only be used
	 * for creating the path if they are set.
	 */
	public void unsetAttribute() {
		isSet = false;
	}

}
