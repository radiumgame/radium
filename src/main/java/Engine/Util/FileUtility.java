package Engine.Util;

import java.io.*;
import java.util.Scanner;

public final class FileUtility extends NonInstantiatable {

	public static String LoadAsString(String path) {
		StringBuilder result = new StringBuilder();	
		try {
		      File myObj = new File(path);
		      Scanner myReader = new Scanner(myObj);
		      while (myReader.hasNextLine()) {
		        result.append(myReader.nextLine() + '\n');
		      }
		      myReader.close();
		    } catch (FileNotFoundException e) {
		      System.out.println("File not found");
		      e.printStackTrace();
		    }
		catch (NullPointerException e) {
			System.out.println("Error: " + e);
		}
		
		return result.toString();
	}

	public static String ReadFile(File f) {

		try {
			String result = "";
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);

			String line;
			while ((line = br.readLine()) != null) {
				result += line + "\n";
			}

			return result;
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static String ReadRaw(File f) {
		try {
			String result = "";
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);

			String line;
			while ((line = br.readLine()) != null) {
				result += line;
			}

			return result;
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static String GetFileExtension(File file) {
		String fileName = file.getName();
		if (fileName == null) {
			throw new IllegalArgumentException("fileName must not be null!");
		}

		String extension = "";

		int index = fileName.lastIndexOf('.');
		if (index > 0) {
			extension = fileName.substring(index + 1);
		}

		return extension;

	}

	public static boolean IsFileType(File f, String[] fileTypes) {
		String extension = GetFileExtension(f);

		boolean is = false;
		for (String fileType : fileTypes) {
			if (extension.equals(fileType)) {
				is = true;
			}
		}

		return is;
	}

	public static void Write(File file, String text) {
		try {
			FileWriter writer = new FileWriter(file);

			writer.write(text);

			writer.flush();
			writer.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}