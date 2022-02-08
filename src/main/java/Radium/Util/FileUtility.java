package Radium.Util;

import java.io.*;
import java.util.Scanner;

/**
 * Utility for dealing with reading, writing, saving, and loading files
 */
public class FileUtility {

	protected FileUtility() {}

	/**
	 * Load file contents to string
	 * @param path File path
	 * @return File contents
	 */
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

	/**
	 * Load file contents to string
	 * @param f File
	 * @return File contents
	 */
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

	/**
	 * Reads file contents without the use of \n
	 * @param f
	 * @return File contents
	 */
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

	/**
	 * Returns the file extension of the file
	 * @return File extension
	 */
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

	/**
	 * Checks if file has one of extensions
	 * @param f File
	 * @param fileTypes File extensions
	 * @return If file has an extension of one of the fileTypes
	 */
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

	/**
	 * Writes content to a file
	 * @param file File to write
	 * @param text New file content
	 */
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