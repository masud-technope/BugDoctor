package utility;

import java.util.ArrayList;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ca.usask.cs.srlab.bugdoctor.views.BugDoctorDashboardView;

public class ConfigManager {

	static Logger logger = LoggerFactory.getLogger(BugDoctorDashboardView.class);

	public static void setGlobalConfigs(String HOME_DIR) {

		IEclipsePreferences store = InstanceScope.INSTANCE.getNode("ca.usask.cs.srlab.bugdoctor");
		logger.info("HOME_DIR = " + HOME_DIR);
		String STOPWORD_DIR = store.get("STOPWORD_DIR", "default_stopword");
		logger.info("STOPWORD_DIR = " + STOPWORD_DIR);
		String SAMURAI_DIR = store.get("SAMURAI_DIR", "default_samurai");
		logger.info("SAMURAI_DIR = " + SAMURAI_DIR);
		String MAX_ENT_MODEL_DIR = store.get("MAX_ENT_MODEL_DIR", "default_model");
		logger.info("MAX_ENT_MODEL_DIR = " + MAX_ENT_MODEL_DIR);
		store.put("REPOSITORY_ROOT", "ssystems");
		store.put("GROUND_TRUTH_DIR", HOME_DIR + "/goldset");
		store.put("STACK_TRACE_DIR", HOME_DIR + "/stacktraces");
	}

	public static void setHomeDir(String HOME_DIR) {
		qd.config.StaticData.HOME_DIR = HOME_DIR;
		query.exec.config.StaticData.HOME_DIR = HOME_DIR;
		strict.ca.usask.cs.srlab.strict.config.StaticData.HOME_DIR = HOME_DIR;
		blizzard.config.StaticData.HOME_DIR = HOME_DIR;
		acer.ca.usask.cs.srlab.coderank.tool.config.StaticData.HOME_DIR = HOME_DIR;
		blader.config.StaticData.BLADER_EXP = HOME_DIR;
	}

	public static void showConfigs() {
		System.out.println("-------ALL CONFIGs--------");
		IEclipsePreferences store = InstanceScope.INSTANCE.getNode("ca.usask.cs.srlab.bugdoctor");
		System.out.println("HOME_DIR = "+ store.get("HOME_DIR", "default_home"));
		System.out.println("STOPWORD_DIR = "+store.get("STOPWORD_DIR", "default_stop_dir"));
		System.out.println("SAMURAI_DIR = "+ store.get("SAMURAI_DIR", "default_samurai_data"));
		System.out.println("MAX_ENT_MODEL_DIR = "+ store.get("MAX_ENT_MODEL_DIR", "default_models"));
		System.out.println("SELECTED_REPOSITORY = "+ store.get("SELECTED_REPOSITORY", "default_repo"));
		// System.out.println(store.get("SELECTED_BUG", "default_bug_id"));
		System.out.println("REPOSITORY_ROOT = "+store.get("REPOSITORY_ROOT", "default_repo_root"));
		System.out.println("GROUND_TRUTH_DIR = "+store.get("GROUND_TRUTH_DIR", "default_goldset"));
		System.out.println("STACK_TRACE_DIR = "+store.get("STACK_TRACE_DIR", "default_stacktraces"));
		System.out.println("CORPUS_DIR = "+ store.get("CORPUS_DIR", "default_corpus_dir"));
		System.out.println("REPOSITORY_SRC_DIR = "+store.get("REPOSITORY_SRC_DIR", "default_repo_src_dir"));
		System.out.println("INDEX_DIR = "+ store.get("INDEX_DIR", "default_index_dir"));
	}

	public static String getToolConfigs() {
		IEclipsePreferences store = InstanceScope.INSTANCE.getNode("ca.usask.cs.srlab.bugdoctor");
		ArrayList<String> settingList = new ArrayList<String>();
		settingList.add("HOME_DIR = "+store.get("HOME_DIR", "default_home"));
		settingList.add("STOPWORD_DIR = "+store.get("STOPWORD_DIR", "default_stop_dir"));
		settingList.add("SAMURAI_DIR = "+store.get("SAMURAI_DIR", "default_samurai_data"));
		settingList.add("MAX_ENT_MODEL_DIR = "+store.get("MAX_ENT_MODEL_DIR", "default_models"));
		settingList.add("SELECTED_REPOSITORY = "+store.get("SELECTED_REPOSITORY", "default_repo"));
		// System.out.println(store.get("SELECTED_BUG", "default_bug_id"));
		settingList.add("REPOSITORY_ROOT = "+store.get("REPOSITORY_ROOT", "default_repo_root"));
		settingList.add("GROUND_TRUTH_DIR = "+store.get("GROUND_TRUTH_DIR", "default_goldset"));
		settingList.add("STACK_TRACE_DIR = "+store.get("STACK_TRACE_DIR", "defaukt_stacktraces"));
		settingList.add("CORPUS_DIR = "+store.get("CORPUS_DIR", "default_corpus_dir"));
		settingList.add("REPOSITORY_SRC_DIR = "+store.get("REPOSITORY_SRC_DIR", "default_repo_src_dir"));
		settingList.add("INDEX_DIR = "+store.get("INDEX_DIR", "default_index_dir"));
		String configTexts = new String();
		for (String item : settingList) {
			configTexts += item + "\n";
		}

		return configTexts;
	}

	public static void setRepoSpecificItems(String HOME_DIR, String repoName) {
		IEclipsePreferences store = InstanceScope.INSTANCE.getNode("ca.usask.cs.srlab.bugdoctor");
		// load the entropy
		String corpusDir = HOME_DIR + "\\corpus\\norm-class\\" + repoName;
		store.put("CORPUS_DIR", corpusDir);
		logger.info("CORPUS_DIR = " + corpusDir);

		String repoSourceDir = HOME_DIR + "\\corpus\\class\\" + repoName;
		store.put("REPOSITORY_SRC_DIR", repoSourceDir);
		logger.info("REPOSITORY_SRC_DIR = " + repoSourceDir);

		String indexDir = HOME_DIR + "\\lucene\\index-class\\" + repoName;
		store.put("INDEX_DIR", indexDir);
		logger.info("INDEX_DIR = " + indexDir);
	}

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}
}
