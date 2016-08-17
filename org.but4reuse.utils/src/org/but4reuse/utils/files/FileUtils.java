package org.but4reuse.utils.files;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.but4reuse.utils.workbench.WorkbenchUtils;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;

/**
 * File Utils class
 * 
 * @author jabier.martinez
 */
public class FileUtils {

	/**
	 * Try to return the expected icon for a file name
	 * 
	 * @param fileName
	 * @return
	 */
	public static ImageDescriptor getIconFromFileName(String fileName) {
		if (fileName != null && fileName.contains(".")) {
			String extension = fileName.substring(fileName.lastIndexOf("."));
			return PlatformUI.getWorkbench().getEditorRegistry().getImageDescriptor("random." + extension);
		}
		return null;
	}

	/**
	 * Get an image from a plugin
	 * 
	 * @param pluginID
	 * @param imagePath
	 *            for example "/icons/myIcon.gif"
	 * @return an imageDescriptor
	 */
	public static ImageDescriptor getImageFromPlugin(String pluginID, String imagePath) {
		final Bundle pluginBundle = Platform.getBundle(pluginID);
		final Path imageFilePath = new Path(imagePath);
		final URL imageFileUrl = FileLocator.find(pluginBundle, imageFilePath, null);
		return ImageDescriptor.createFromURL(imageFileUrl);
	}

	/**
	 * Try to return a file related to a uri
	 * 
	 * @param uri
	 * @return the file or null if not found
	 */
	public static File getFile(URI uri) {
		File file = null;
		// file
		if (uri.getScheme().equals("file")) {
			file = new File(uri);
			// eclipse workbench
		} else if (uri.getScheme().equals("platform")) {
			IResource ifile = WorkbenchUtils.getIResourceFromURI(uri);
			if (ifile == null) {
				return null;
			}
			file = WorkbenchUtils.getFileFromIResource(ifile);
			// web
		} else if (uri.getScheme().equals("http") || uri.getScheme().equals("https")) {
			URL url;
			try {
				url = uri.toURL();
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return null;
			}
			String tDir = System.getProperty("java.io.tmpdir");
			String fileName = url.toString().substring(url.toString().lastIndexOf('/') + 1, url.toString().length());
			String path = tDir + "tmp_" + fileName;
			file = new File(path);
			file.deleteOnExit();
			downloadFileFromURL(url, file);
		}
		return file;
	}

	/**
	 * Create file if it does not exist
	 * 
	 * @param file
	 * @throws IOException
	 */
	public static void createFile(File file) throws IOException {
		if (file != null && !file.exists()) {
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			file.createNewFile();
		}
	}

	/**
	 * Append line to file
	 * 
	 * @param file
	 * @param text
	 * @throws Exception
	 */
	public static void appendToFile(File file, String text) throws Exception {
		BufferedWriter output;
		output = new BufferedWriter(new FileWriter(file, true));
		output.append(text);
		output.newLine();
		output.close();
	}

	/**
	 * No append, just overwrite with new text
	 * 
	 * @param file
	 * @param text
	 * @throws Exception
	 */
	public static void writeFile(File file, String text) throws Exception {
		BufferedWriter output;
		output = new BufferedWriter(new FileWriter(file, false));
		output.append(text);
		output.close();
	}

	/**
	 * Creation date of a file
	 * 
	 * @param file
	 * @return date
	 */
	public static Date getCreationDate(File file) {
		try {
			java.nio.file.Path path = file.toPath();
			BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
			FileTime ft = attr.creationTime();
			if (ft != null) {
				Date date = new Date();
				date.setTime(ft.toMillis());
				return date;
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Check if a file contains a file with a given extension
	 * 
	 * @param file
	 * @param extension
	 * @return whether it is found or not
	 */
	public static boolean containsFileWithExtension(File file, String extension) {
		if (getExtension(file).equals(extension)) {
			return true;
		} else {
			if (file.isDirectory()) {
				for (File child : file.listFiles()) {
					if (containsFileWithExtension(child, extension)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Get file extension
	 * 
	 * @param file
	 * @return extension or empty string
	 */
	public static String getExtension(File file) {
		int i = file.getName().lastIndexOf('.');
		if (i > 0) {
			return file.getName().substring(i + 1);
		}
		return "";
	}

	/**
	 * Check if a file has a given extension
	 * 
	 * @param file
	 * @param extension
	 * @return
	 */
	public static boolean isExtension(File file, String extension) {
		return getExtension(file).equalsIgnoreCase(extension);
	}

	/**
	 * Get lines of a file
	 * 
	 * @param file
	 * @return list of strings
	 */
	public static List<String> getLinesOfFile(File file) {
		List<String> lines = new ArrayList<String>();
		try {
			FileInputStream fstream = new FileInputStream(file);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			while ((strLine = br.readLine()) != null) {
				lines.add(strLine);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lines;
	}

	/**
	 * Get string
	 * 
	 * @param file
	 * @return
	 */
	public static String getStringOfFile(File file) {
		StringBuilder string = new StringBuilder();
		for (String line : getLinesOfFile(file)) {
			string.append(line + "\n");
		}
		string.setLength(string.length() - 1);
		return string.toString();
	}

	/**
	 * Check whether two files have the same content
	 * 
	 * @param file1
	 * @param file2
	 * @return
	 */
	public static boolean isFileContentIdentical(File file1, File file2) {
		// We compare the checksum of the two files
		String checksumFile1 = getChecksumMD5(file1);
		String checksumFile2 = getChecksumMD5(file2);
		return checksumFile1.equals(checksumFile2);
	}

	/**
	 * Download file from url
	 * 
	 * @param url
	 * @param file
	 * @return true if file was correctly updated
	 */
	public static boolean downloadFileFromURL(URL url, File file) {
		URLConnection connection;
		try {
			connection = url.openConnection();
			InputStream in = connection.getInputStream();
			FileOutputStream fos = new FileOutputStream(file);
			byte[] buf = new byte[512];
			while (true) {
				int len = in.read(buf);
				if (len == -1) {
					break;
				}
				fos.write(buf, 0, len);
			}
			in.close();
			fos.flush();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * get checksum of a file using MD5 hashing algorithm
	 * 
	 * @param file
	 * @return the checksum
	 */
	public static String getChecksumMD5(File file) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			FileInputStream fis = new FileInputStream(file);

			byte[] dataBytes = new byte[1024];

			int nread = 0;
			while ((nread = fis.read(dataBytes)) != -1) {
				md.update(dataBytes, 0, nread);
			}
			;
			byte[] mdbytes = md.digest();

			// Convert to hex format
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < mdbytes.length; i++) {
				sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			fis.close();
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Replace one file with another
	 * 
	 * @param oldFile
	 * @param newFile
	 */
	public static void replace(File oldFile, File newFile) {
		oldFile.delete();
		newFile.renameTo(oldFile);
	}

	/**
	 * Copy file
	 * 
	 * @param sourceFile
	 * @param destinationFile
	 */
	public static void copyFile(File sourceFile, File destinationFile) {
		// TODO Improve this! copy it correctly
		try {
			writeFile(destinationFile, FileUtils.getStringOfFile(sourceFile));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get all files (not folders) inside one folder. Or the file itself if the
	 * input is a file
	 * 
	 * @param dir
	 * @return list of files
	 */
	public static List<File> getAllFiles(File dir) {
		return getAllFiles(null, dir);
	}

	private static List<File> getAllFiles(List<File> files, File dir) {
		if (files == null) {
			files = new ArrayList<File>();
		}

		if (!dir.isDirectory()) {
			files.add(dir);
			return files;
		}

		for (File file : dir.listFiles()) {
			getAllFiles(files, file);
		}
		return files;
	}  


}
