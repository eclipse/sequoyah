/**
 * 
 */
package org.eclipse.sequoyah.android.cdt.internal.build.core;

import java.io.File;

import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.envvar.IBuildEnvironmentVariable;
import org.eclipse.cdt.managedbuilder.envvar.IConfigurationEnvironmentVariableSupplier;
import org.eclipse.cdt.managedbuilder.envvar.IEnvironmentVariableProvider;
import org.eclipse.core.runtime.Platform;
import org.eclipse.sequoyah.android.cdt.build.core.INDKService;

/**
 * @author dschaefer
 *
 */
public class AndroidEnvironmentSupplier implements
		IConfigurationEnvironmentVariableSupplier {

	private static class AndroidPathEnvVar implements IBuildEnvironmentVariable {
		public String getName() {
			return "PATH";
		}

		public String getValue() {
			return Activator.getService(INDKService.class).getNDKLocation();
		}

		public int getOperation() {
			return IBuildEnvironmentVariable.ENVVAR_PREPEND;
		}

		public String getDelimiter() {
			return Platform.getOS().equals(Platform.OS_WIN32) ? ";" : ":";
		}
	}
	
	private AndroidPathEnvVar pathVar = new AndroidPathEnvVar();
	
	public IBuildEnvironmentVariable getVariable(String variableName,
			IConfiguration configuration, IEnvironmentVariableProvider provider) {
		if (variableName.equals(pathVar.getName()))
			return pathVar;
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.cdt.managedbuilder.envvar.IConfigurationEnvironmentVariableSupplier#getVariables(org.eclipse.cdt.managedbuilder.core.IConfiguration, org.eclipse.cdt.managedbuilder.envvar.IEnvironmentVariableProvider)
	 */
	public IBuildEnvironmentVariable[] getVariables(
			IConfiguration configuration, IEnvironmentVariableProvider provider) {
		return new IBuildEnvironmentVariable[] { pathVar };
	}

}
