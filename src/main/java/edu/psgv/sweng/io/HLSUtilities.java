package edu.psgv.sweng.io;

import java.util.ArrayList;
import java.io.*;
import java.net.*;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * HLSUtilities provides utility methods for HLS purposes.  These methods
 * 	provide the main working logic for content extraction (from files
 * 	and websites).  As well as web communication.
 *
 * @author Ryan McDonald
 * @version 3.1.0
 */
public class HLSUtilities {

	/* LOGGER */
	private static final Logger logger = LogManager.getLogger();

	private HLSUtilities() {
		//Prevent instantiation/subclassing
	}

	/**
	 * getContentFromFile() is used to read content line by line from a provided file.
	 *
	 * @param filepath the file path containing the file to read from.
	 * @return a list of the lines of content or null if no content is found.
	 */
	public static ArrayList<String> getContentFromFile(String filepath) {
		logger.debug("in>> getContentFromFile()");
		logger.info("Looking for file {}", filepath);

		ArrayList<String> content = new ArrayList<>();

		//Read through the file and extract lines into a list
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filepath))) {

			String url = "";
			while((url = bufferedReader.readLine()) != null) {
				content.add(url);
			}

			//If list is empty, always want to return null
			if(content.isEmpty()) content = null;

		} catch(Exception e) {

			if(e instanceof FileNotFoundException) {
				System.out.println("\nUnable to locate and open file '" + filepath + "' ...\n");
				logger.error("Unable to open file: '{}'", filepath);
			} else if(e instanceof IOException) {
				System.out.println("\nError reading file contents from '" + filepath + "' ...\n");
				logger.error("Unable to read file: '{}'", filepath);
			} else {
				System.out.println("\nError reading file '" + filepath + "' ...\n");
				logger.error("getContentFromFile(): '{}'",e.getMessage());
			}

			content = null;
		}

		//DEBUGGER: Show content in list only if Trace debugging is enabled.
		if(logger.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder();

			if(content != null) {
				for(int i = 0; i < content.size(); i++) {
					sb.append("\t").append("URL").append(i+1).append(": ").append(content.get(i));
				}

				logger.trace("getContentFromFile(): Extracted content:\n", sb.toString());
			} else {
				logger.trace("getContentFromFile(): List of content is null.");
			}
		}

		logger.debug("out>> getContentFromFile()");
		return content;
	}

	/**
	 * getURLConnection() establishes a URL connection to the string version of
	 * 	the URL that is passed in as a parameter.
	 *
	 * @param urlString the string representation of a URL.
	 * @return an HTTP GET connection to the provided URL.
	 */
	public static HttpURLConnection getURLConnection(String urlString) {
		logger.debug("in>> getURLConnection()");
		HttpURLConnection urlConnection = null;

		try {
			//Create URL object and open HTTP connection to perform GET request
			URL url = new URL(urlString);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");

		} catch(Exception e) {

			urlConnection = null;	//reset urlConnection

			if(e instanceof MalformedURLException) {
				//System.out.println("\nMalformed URL: '" + urlString + "'\nPlease verify that provided URL is correct.\n");
				logger.error("Malformed URL; failed to open: '{}'", urlString);
			} else {
				logger.error("getUrlContents(): '{}'", e.getMessage());
			}
		}

		logger.debug("<<out getURLConnection()");

		return urlConnection;
	}

	/**
	 * readURLContents() extracts content from a file through an HTTP Connection.
	 *
	 * @param urlConnection the HTTP Get connection to the content that will be read.
	 * @return a list of all lines of content present at the connection.
	 */
	public static ArrayList<String> readURLContents(HttpURLConnection urlConnection) {
		logger.debug("in>> readURLContents()");

		ArrayList<String> content = new ArrayList<>();

		//Read web contents into a list through HTTP
		try {
			//Wrap HTTP connection in buffered reader for content extraction
			BufferedReader urlContentReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

			logger.info("Reading content from URL: {}", urlConnection.getURL().toString());

			//Read web content line by line
			String line = "";
			while((line = urlContentReader.readLine()) != null) {
				content.add(line);
			}

			urlContentReader.close();
			logger.info("Finished reading content from URL: {}", urlConnection.getURL().toString());

		} catch(Exception e) {
			content = null;

			if(e instanceof IOException) {
				//System.out.println("\nError reading URL contents from '" + urlString + "'\nPlease verify that URL exists.\n");
				logger.error("Unable to read content from URL: '{}'", urlConnection.getURL().toString());
			} else {
				//System.out.println("\nError extracting content from URL '" + urlString + "' ...\n");
				logger.error("readURLContents(): '{}'", e.getMessage());
			}
		}

		//DEBUGGER: Only show content received from URL if Trace debugging is enabled.
		if(logger.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder();

			if(content != null) {
				for(int i = 0; i < content.size(); i++) {
					sb.append("\t").append(content.get(i)).append("\n");
				}

				logger.trace("readURLContents(): URL CONTENT:\n{}", sb.toString());
			} else {
				logger.trace("readURLContents(): Content from URL is null.");
			}
		}

		logger.debug("<<out readURLContents()");
		return content;
	}


	/**
	 * getUrlContents() extracts content from a given URL.  An HTTP connection is opened
	 * 	to the URL then content from the URL is read line by line to build a list to store
	 * 	the content.
	 *
	 * @param url the url to be opened via HTTP GET for content to be read from.
	 * @return a list of all lines of content present at the URL.
	 */
	public static ArrayList<String> getUrlContents(String urlString) {
		logger.debug("in>> getUrlContents()");
		logger.info("Extracting content from URL: {}", urlString);

		ArrayList<String> contents = null;

		HttpURLConnection urlConnection = HLSUtilities.getURLConnection(urlString);
		try {
			if(urlConnection != null) {
				logger.info("Response from URL Connection: {} - {}.", urlConnection.getResponseCode(), urlConnection.getResponseMessage());

				if(urlConnection.getResponseCode() == 200) {
					contents = HLSUtilities.readURLContents(urlConnection);
				} else if(urlConnection.getResponseCode() == 404) {
					logger.warn("Encountered a 404 error when trying to connect to URL!");
					System.out.println("404 ERROR: URL not found.  Please check that the following URL exists:\n\t" + urlString +"\n");
				} else {
					logger.error("Encountered invalid response from URL; Could not retrieve content!");
					System.out.println("ERROR: Failed to establish connection to URL:\n\t" + urlString);
				}

			} else {
				logger.warn("Encountered a Malformed URL; Could not retrieve content!");
				System.out.println("\nMalformed URL: '" + urlString + "'\nPlease verify that provided URL is formatted correctly.\n");
			}
		} catch(Exception e) {
			logger.error("FAILED TO CONNECT TO URL!\n\t" + e.getMessage());
			System.out.println("ERROR: Unable to connect to URL.");
		} finally {
			if(urlConnection != null) {
				urlConnection.disconnect();
			}
		}

		logger.debug("<<out getUrlContents()");
		return contents;
	}

	/**
	 * absolutizeURL() takes a Master URL and a URI found within it's Playlist contents
	 *  to create an absolute URL to the media playlist.
	 *
	 * @param masterURL the url of the Master Playlist
	 * @param URI the URI of the variant playlist
	 * @return a String representation of the absolute URL to a Media Playlist
	 */
	public static String absolutizeURL(String masterURL, String URI) {
		logger.debug("in>> absolutizeURL()");

		String url = masterURL.substring(0, masterURL.lastIndexOf("/") + 1);
		url += URI;

		logger.debug("<<out absolutizeURL()");

		return url;
	}

}
