package ca.usask.cs.srlab.bugdoctor;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qd.core.EntropyCalc;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "ca.usask.cs.srlab.bugdoctor"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	public static String SELECTED_REPOSITORY = new String();
	Logger logger = LoggerFactory.getLogger(Activator.class);

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		this.loadDefaultConfigs();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
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

	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public void loadDefaultConfigs() {

		try {
			IEclipsePreferences store = InstanceScope.INSTANCE.getNode("ca.usask.cs.srlab.bugdoctor");

			store.put("HOME_DIR", "F:\\MyWorks\\Thesis Works\\PhDThesisTool");
			String HOME_DIR = store.get("HOME_DIR", "C:\\MyWorks\\PhDThesisTool");

			store.put("STOPWORD_DIR", HOME_DIR + "/pp-data");
			store.put("SAMURAI_DIR", HOME_DIR + "/samurai-data");
			store.put("MAX_ENT_MODEL_DIR", HOME_DIR + "/models");
			store.put("SELECTED_REPOSITORY", "ecf");
			store.put("SELECTED_BUG", "192756");
			store.put("REPOSITORY_ROOT", "ssystems");
			store.put("GROUND_TRUTH_DIR", HOME_DIR + "/goldset");
			store.put("STACK_TRACE_DIR", HOME_DIR + "/stacktraces");

		} catch (Exception e) {
			System.err.println("Failed to load the default configuration");
			logger.error("Failed to load the default configuration!");
		}
	}

}
