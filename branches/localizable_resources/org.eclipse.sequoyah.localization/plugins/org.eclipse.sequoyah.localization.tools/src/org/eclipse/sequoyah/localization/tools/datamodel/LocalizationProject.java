/********************************************************************************
 * Copyright (c) 2009-2010 Motorola Inc.
 * All rights reserved. This program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is 
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Initial Contributors:
 * Vinicius Hernandes (Motorola)
 * Matheus Tait Lima (Eldorado)
 * 
 * Contributors:
 * Marcelo Marzola Bossoni (Eldorado) - Bug [289146] - Performance and Usability Issues
 *  * Vinicius Rigoni Hernandes (Eldorado) - Bug [289885] - Localization Editor doesn't recognize external file changes
 ********************************************************************************/
package org.eclipse.sequoyah.localization.tools.datamodel;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

/**
 * This class represents a real project and contains other information about it
 */
public class LocalizationProject {

	/*
	 * A reference to the project being represented
	 */
	private IProject project;

	/*
	 * The list of LocalizationFiles which belong to the project
	 */
	private List<StringLocalizationFile> localizationFiles;

	/*
	 * Whether the data in the model has been modified and differs from the
	 * values saved or not
	 */
	private boolean dirty;

	/*
	 * Whether there are changes in the associated meta-data / extra-info or not
	 */
	private boolean dirtyMetaExtraData;

	public LocalizationProject(IProject project, List<StringLocalizationFile> files) {
		this.project = project;
		this.localizationFiles = files;

		for (Iterator<StringLocalizationFile> iterator = localizationFiles.iterator(); iterator
				.hasNext();) {
			StringLocalizationFile localizationFile = iterator.next();
			localizationFile.setLocalizationProject(this);
		}

	}

	/**
	 * Get the project that is being represented
	 * 
	 * @return the project that is being represented
	 */
	public IProject getProject() {
		return project;
	}

	/**
	 * Set the project that is being represented
	 * 
	 * @param project
	 *            the project that is being represented
	 */
	public void setProject(IProject project) {
		this.project = project;
	}

	/**
	 * Get the list of LocalizationFiles which belong to the project
	 * 
	 * @return the list of LocalizationFiles which belong to the project
	 */
	public List<StringLocalizationFile> getLocalizationFiles() {
		return localizationFiles;
	}

	/**
	 * Set the list of LocalizationFiles which belong to the project
	 * 
	 * @param localizationFiles
	 *            the list of LocalizationFiles which belong to the project
	 */
	public void setLocalizationFiles(List<StringLocalizationFile> localizationFiles) {
		this.localizationFiles = localizationFiles;
	}

	/**
	 * Check whether the data in the model has been modified and differs from
	 * the values saved or not
	 * 
	 * @return true if the data in the model has been modified and differs from
	 *         the values saved, false otherwise
	 */
	public boolean isDirty() {
		return dirty;
	}

	/**
	 * Set whether the data in the model has been modified and differs from the
	 * values saved or not
	 * 
	 * @param dirty
	 *            true if the data in the model has been modified and differs
	 *            from the values saved, false otherwise
	 */
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	/**
	 * Check whether there are changes in the associated meta-data / extra-info
	 * or not
	 * 
	 * @return true if there are changes in the associated meta-data /
	 *         extra-info, false otherwise
	 */
	public boolean isDirtyMetaExtraData() {
		return dirtyMetaExtraData;
	}

	/**
	 * Set whether there are changes in the associated meta-data / extra-info or
	 * not
	 * 
	 * @param dirtyMetaExtraData
	 *            true if there are changes in the associated meta-data /
	 *            extra-info, false otherwise
	 */
	public void setDirtyMetaExtraData(boolean dirtyMetaExtraData) {
		this.dirtyMetaExtraData = dirtyMetaExtraData;
	}

	/**
	 * Get the localization file for a specific locale
	 * 
	 * @param localeInfo
	 *            the LocaleInfo object that represents the locale
	 * @return the localization file for the given locale
	 */
	public StringLocalizationFile getLocalizationFile(LocaleInfo localeInfo) {

		StringLocalizationFile localizationFile = null;

		boolean found = false;
		Iterator<StringLocalizationFile> iterator = localizationFiles.iterator();
		while (iterator.hasNext() && !found) {
			StringLocalizationFile file = iterator.next();

			if (file.getLocaleInfo().equals(localeInfo)) {
				localizationFile = file;
				found = true;
			}
		}

		return localizationFile;
	}

	/**
	 * Get the localization file for a specific IFile
	 * 
	 * @param file
	 * @return the localization file for the given file
	 */
	public StringLocalizationFile getLocalizationFile(IFile file) {

		StringLocalizationFile localizationFile = null;

		boolean found = false;
		Iterator<StringLocalizationFile> iterator = localizationFiles.iterator();
		while (iterator.hasNext() && !found) {
			StringLocalizationFile locFile = iterator.next();

			if (locFile.getFile().equals(file)) {
				localizationFile = locFile;
				found = true;
			}
		}

		return localizationFile;
	}

	/**
	 * Add a new localization file
	 * 
	 * @param localizationFile
	 * @return true if the file has been successfully added, false otherwise
	 */
	public boolean addLocalizationFile(StringLocalizationFile localizationFile) {
		localizationFile.setLocalizationProject(this);
		return localizationFiles.add(localizationFile);
	}

	/**
	 * Remove a localization file
	 * 
	 * @param localizationFile
	 * @return true if the file has been successfully removed, false otherwise
	 */
	public boolean removeLocalizationFile(StringLocalizationFile localizationFile) {
		return localizationFiles.remove(localizationFile);
	}

	/**
	 * @return
	 */
	public Set<StringArray> getAllStringArrays() {
		Set<StringArray> allStringArrays = new TreeSet<StringArray>(
				new Comparator<StringArray>() {

					public int compare(StringArray o1, StringArray o2) {
						return o1.getKey().compareTo(o2.getKey());
					}
				});

		List<StringLocalizationFile> localizationFiles = getLocalizationFiles();

		for (StringLocalizationFile locFile : localizationFiles) {
			allStringArrays.addAll(locFile.getStringArrays());
		}

		return allStringArrays;
	}

}
