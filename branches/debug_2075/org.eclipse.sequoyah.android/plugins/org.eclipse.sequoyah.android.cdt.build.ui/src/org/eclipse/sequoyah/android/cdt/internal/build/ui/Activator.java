package org.eclipse.sequoyah.android.cdt.internal.build.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.sequoyah.android.cdt.build.ui"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	@SuppressWarnings("unchecked")
	public <T> T getService(Class<T> clazz) {
		BundleContext context = getBundle().getBundleContext();
		ServiceReference ref = context.getServiceReference(clazz.getName());
		try{
			return (ref != null) ? (T)context.getService(ref) : null;
		} finally {
			if(ref != null)
				context.ungetService(ref);
		}
	}

}
