package utility;

import java.util.ArrayList;
import java.util.HashSet;

public class MiscUtility {

	public static String list2Str(ArrayList<String> list) {
		String itemStr = new String();
		for (String item : list) {
			itemStr += item + " ";
		}
		return itemStr.trim();
	}

	public static String list2Str(String[] list) {
		String itemStr = new String();
		for (String item : list) {
			itemStr += item + "\t";
		}
		return itemStr.trim();
	}
	
	public static String list2Str(HashSet<String> list) {
		String itemStr = new String();
		for (String item : list) {
			itemStr += item + " ";
		}
		return itemStr.trim();
	}

	protected void sendEmail()
	{
		//sending email in Java
	}	
}
