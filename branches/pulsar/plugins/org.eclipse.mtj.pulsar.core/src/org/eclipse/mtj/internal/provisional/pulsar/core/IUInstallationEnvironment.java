/**
 * Copyright (c) 2009 Motorola.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     David Marques (Motorola) - Initial version
 */
package org.eclipse.mtj.internal.provisional.pulsar.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.equinox.internal.provisional.p2.metadata.IInstallableUnit;

/**
 * IUInstallationEnvironment implements the {@link IInstallationEnvironment}
 * interface in order to get environment information from an {@link IInstallableUnit}
 * class instance's filter.
 * 
 * @author David Marques
 */
public class IUInstallationEnvironment implements IInstallationEnvironment {

	private String os   = IInstallationEnvironment.ALL;
	private String ws   = IInstallationEnvironment.ALL;;
	private String arch = IInstallationEnvironment.ALL;;
	
	/**
	 * Creates an instance of an IUInstallationEnvironment
	 * for the specified filter value.
	 * 
	 * @param filter {@link IInstallableUnit} filter.
	 */
	public IUInstallationEnvironment(String filter) {
		if (filter != null) {
			Matcher matcher = Pattern.compile("osgi[.]([^=])+[=]([^)])+").matcher(filter);
			while (matcher.find()) {
				String pair[] = matcher.group().split("=");
				processKeyValuePair(pair);
			}
		}
	}
	
	/**
	 * Processes the key/value pair and sets the
	 * proper environment values.
	 * 
	 * @param pair key/value pair.
	 */
	private void processKeyValuePair(String[] pair) {
		String key = pair[0x00];
		if (key.equals("osgi.os")) {
			this.os = pair[0x01].trim();
		} else
		if (key.equals("osgi.ws")) {
			this.ws = pair[0x01].trim();
		} else
		if (key.equals("osgi.arch")) {
			this.arch = pair[0x01].trim();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.mtj.internal.provisional.pulsar.core.IInstallationEnvironment#getTargetOS()
	 */
	public String getTargetOS() {
		return this.os;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.mtj.internal.provisional.pulsar.core.IInstallationEnvironment#getTargetOSArch()
	 */
	public String getTargetOSArch() {
		return this.arch;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.mtj.internal.provisional.pulsar.core.IInstallationEnvironment#getTargetWS()
	 */
	public String getTargetWS() {
		return this.ws;
	}

}
