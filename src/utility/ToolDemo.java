package utility;

import bugdoctor.core.StaticData;

public class ToolDemo {

	
	public static void main(String[] args) {
		String filePath="\\My MSc\\ThesisWorks\\Crowdsource_Knowledge_Base\\M4CPBugs\\experiment\\ssystems\\eclipse.jdt.debug\\org.eclipse.jdt.debug.ui\\ui\\org\\eclipse\\jdt\\internal\\debug\\ui\\IJDIPreferencesConstants.java";
		int rootIndex=filePath.indexOf("ssystems");
		System.out.println(StaticData.HOME_DIR+"\\"+filePath.substring(rootIndex));
		
	} 
}
