/********************************************************************************
 * Copyright (c) 2009 Motorola Inc.
 * All rights reserved. This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Vinicius Hernandes (Motorola)
 * 
 * Contributors:
 * name (company) - description.
 ********************************************************************************/
package org.eclipse.tml.localization.tools.managers;

import org.eclipse.core.resources.IProject;

/**
 *
 */
public class ProjectPreferencesManager {

	private IProject project;

	private ProjectLocalizationManager projectLocalizationManager;

	private boolean metadataEnabled;

	/**
	 * @return
	 */
	public boolean hasMetadata() {
		return false;
	}

	/**
	 * @return
	 */
	public boolean hasExtraInfo() {
		return false;
	}

	/**
	 * 
	 */
	public void clearMetadata() {

	}

	/**
	 * 
	 */
	public void clearExtraInfo() {

	}

	/**
	 * 
	 */
	public boolean hasComments() {
		return false;
	}

	/**
	 * 
	 */
	public boolean hasTranslationDetails() {
		return false;
	}

	/**
	 * 
	 */
	public boolean hasGrammarCheckerDetails() {
		return false;
	}

	/**
	 * 
	 */
	public void clearComments() {

	}

	/**
	 * 
	 */
	public void clearTranslationDetails() {

	}

	/**
	 * 
	 */
	public void clearGrammarCheckerDetails() {

	}

	/**
	 * @return
	 */
	public boolean isMetadataEnabled() {
		return metadataEnabled;
	}

	/**
	 * @param metadataEnabled
	 */
	public void setMetadataEnabled(boolean metadataEnabled) {
		this.metadataEnabled = metadataEnabled;
	}

}
