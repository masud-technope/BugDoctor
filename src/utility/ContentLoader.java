package utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class ContentLoader {

	public static String loadFileContent(String fileName) {
		// code for loading the file name
		String fileContent = new String();
		try {
			File f = new File(fileName);
			BufferedReader bufferedReader = new BufferedReader(
					new FileReader(f));
			while (bufferedReader.ready()) {
				String line = bufferedReader.readLine().trim();
				if (line.trim().isEmpty())
					continue;
				fileContent += line + "\n";
			}
			bufferedReader.close();
		} catch (Exception ex) {
			// handle the exception
		}
		return fileContent;
	}

	public static String[] getAllLines(String fileName) {
		// reading items as array
		String content = loadFileContent(fileName);
		String[] lines = content.split("\n");
		return lines;
	}

	public static ArrayList<String> getAllLinesOptList(String fileName) {
		ArrayList<String> lines = new ArrayList<>();
		try {
			File f = new File(fileName);
			BufferedReader bufferedReader = new BufferedReader(
					new FileReader(f));
			while (bufferedReader.ready()) {
				String line = bufferedReader.readLine().trim();
				lines.add(line);
			}
			bufferedReader.close();
		} catch (Exception ex) {
			// handle the exception
		}
		return lines;
	}

	public static ArrayList<String> getAllLinesList(String fileName) {
		// reading items as list
		String[] items = getAllLines(fileName);
		return new ArrayList<>(Arrays.asList(items));
	}

	public static ArrayList<String> getAllTokensSC(String fileName) {
		ArrayList<String> tokens = new ArrayList<>();
		try {
			Scanner scanner = new Scanner(new File(fileName));
			while (scanner.hasNext()) {
				String token = scanner.next();
				tokens.add(token);
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tokens;
	}

	public static ArrayList<Integer> getAllLinesInt(String fileName) {
		ArrayList<String> lines = getAllLinesOptList(fileName);
		ArrayList<Integer> temp = new ArrayList<>();
		for (String line : lines) {
			if (line.trim().isEmpty())
				continue;
			temp.add(Integer.parseInt(line.trim()));
		}
		return temp;
	}

	public static String downloadURL(String issueURL) {
		// downloading the issue URL
		String content = new String();
		try {
			URL u = new URL(issueURL);
			HttpURLConnection connection = (HttpURLConnection) u
					.openConnection();
			System.out.println(connection.getResponseMessage());
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						connection.getInputStream()));
				String line = null;
				while ((line = br.readLine()) != null) {
					content += line + "\n";
				}
				// System.out.println(content);
			}
		} catch (Exception exc) {
			// handle the exception
		}
		return content;
	}
}
