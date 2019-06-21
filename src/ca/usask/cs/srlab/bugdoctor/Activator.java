package ca.usask.cs.srlab.bugdoctor;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

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
		IEclipsePreferences store = InstanceScope.INSTANCE
				.getNode("ca.usask.cs.srlab.bugdoctor");

		store.put("HOME_DIR", "F:/MyWorks/Thesis Works/PhDThesisTool");
		store.put("STOPWORD_DIR",
				"F:/MyWorks/Thesis Works/PhDThesisTool/thesis-tool/pp-data");
		store.put("SAMURAI_DIR",
				"F:/MyWorks/Thesis Works/PhDThesisTool/thesis-tool/samurai-data");
		store.put("MAX_ENT_MODEL_DIR",
				"F:/MyWorks/Thesis Works/PhDThesisTool/thesis-tool/models");
		store.put("SELECTED_REPOSITORY", "eclipse.jdt.debug");
		store.put("SELECTED_BUG", "5653");
		store.put("REPOSITORY_ROOT", "ssystems");
		store.put("GROUND_TRUTH_DIR",
				"F:/MyWorks/Thesis Works/PhDThesisTool/goldset");
		store.put("STACK_TRACE_DIR",
				"F:/MyWorks/Thesis Works/PhDThesisTool/stacktraces");

		/*
		 * store.put("HOME_DIR", "C:\\MyWorks\\PhDThesisTool");
		 * store.put("STOPWORD_DIR",
		 * "C:\\MyWorks\\PhDThesisTool\\BugDoctor\\pp-data");
		 * store.put("SAMURAI_DIR",
		 * "C:\\MyWorks\\PhDThesisTool\\BugDoctor\\samurai-data");
		 * store.put("MAX_ENT_MODEL_DIR",
		 * "C:\\MyWorks\\PhDThesisTool\\BugDoctor\\models");
		 * store.put("SELECTED_REPOSITORY", "eclipse.jdt.debug");
		 * store.put("SELECTED_BUG", "5653"); store.put("REPOSITORY_ROOT",
		 * "ssystems"); store.put("GROUND_TRUTH_DIR",
		 * "C:\\MyWorks\\PhDThesisTool\\goldset");
		 */

	}

}
