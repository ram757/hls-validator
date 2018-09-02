package edu.psgv.sweng;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import edu.psgv.sweng.io.HLSUtilities;
import edu.psgv.sweng.playlist.PlaylistFactory;
import edu.psgv.sweng.validation.MediaSegmentTimeValidator;
import edu.psgv.sweng.validation.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.psgv.sweng.playlist.MasterPlaylist;
import edu.psgv.sweng.playlist.MediaPlaylist;
import edu.psgv.sweng.playlist.Playlist;
import edu.psgv.sweng.validation.FirstTagValidator;
import edu.psgv.sweng.validation.TagContextValidator;
import edu.psgv.sweng.validation.URISequenceValidator;

/**
 * HTTPLiveStreamingValidator is the main class for the HLS application.
 * 	This class provides the core invocations of HLS's functionalities
 * 	so that playlist files can be received over HTTP and then validated.
 *
 * @author Ryan McDonald
 * @version 3.1.0
 */
public class HTTPLiveStreamingValidator {

	private static final String VERSION = "3.1.0";

	private HTTPLiveStreamingValidator() {
		//Prevent instantiation/subclassing
	}

	/* LOGGER */
	private static final Logger logger = LogManager.getLogger();

	/**
	 * The main method for execution of HLS1 program.  Checks whether execution
	 * 	should occur in Interactive Mode or Batch Mode and then proceeds
	 * 	accordingly.
	 *
	 * @param args any command line arguments.
	 */
	public static void main(String[] args) {
		logger.info("HLS Application has begun execution.");

		System.out.println("\n======================================");
		System.out.println("        HLS Application v. " + VERSION);
		System.out.println("======================================\n");

		//Check to enter Batch Mode or User Mode
		if(args.length > 0 && !args[0].isEmpty()) {

			ArrayList<String> urls = HLSUtilities.getContentFromFile(args[0]);
			batchMode(urls);

		} else {

			interactiveMode();
		}

		System.out.println("\n======================================");
		System.out.println("     Exiting HLS Application v. " + VERSION);
		System.out.println("======================================\n");
		logger.info("HLS Application has ended execution.");
	}


	/**
	 * interactiveMode() for processing Playlists in interactive mode.
	 * 	Will print to console for user interactions.
	 */
	public static void interactiveMode() {
		logger.debug("in>> interactiveMode()");

		//Accept keyboard input and store in variables
		String userInput = "";
		Scanner keyboard = new Scanner(System.in);

		//Continue accepting input until user explicitly quits
		do {
			System.out.print(MENU);

			//Get input from user
			userInput = keyboard.nextLine();

			//Quit if user choses to do so
			if(userInput.toLowerCase().equals("quit")) {
				logger.info("User key entered {} - TO QUIT", userInput);

				System.out.print("\nThank you for using the HLS Application. Goodbye!");

			} else {
				logger.info("User key entered {} - for validation", userInput);

				//Attempt to validate playlist
				processPlaylist(userInput);
			}

		} while(!userInput.toLowerCase().equals("quit"));

		keyboard.close();

		logger.debug("out>> interactiveMode()");
	}


	/*
	 * batchMode() for processing Playlists in batch mode.
	 *
	 * @param urls a list of URLs to process without user input.
	 */
	public static void batchMode(ArrayList<String> urls) {
		logger.debug("in>> batchMode()");

		System.out.println("--------------------------------");
		System.out.println("     HLS Batch Processing");
		System.out.println("--------------------------------");

		if(urls == null || urls.isEmpty()) {
			logger.error("batchMode(): No URLs to process for BATCH MODE.");
			System.out.println("\nThere are no URLs to process.  Please provide valid file containing URLs.");
		} else {

			//Print all of the playlist files that are to be processed
			System.out.println("\nPlaylist files to be processed:");
			for(String file : urls) {
				System.out.println(file);
			}

			//Process all of the URLs and print the contents extracted from each
			for(String url : urls) {
				System.out.println("\n\n=======================================================================");
				System.out.println("Processing: " + url + "\n");

				processPlaylist(url);
			}

		}

		System.out.println("\n-----------------------------");
		System.out.println("HLS Batch Processing Complete");
		System.out.println("-----------------------------");

		logger.debug("<<out batchMode()");
	}

	/**
	 * processPlaylist() for processing playlists in order to validate then print a report.
	 * 	Will print to console for user interactions.
	 *
	 * @param URL the URL to the playlist to be processed
	 */
	private static void processPlaylist(String URL) {
		logger.debug("in>> processPlaylist()");

		//Get content from URL
		ArrayList<String> content = getContent(URL);

		//Only validate playlist if content was found
		if(content == null || content.isEmpty()) {
			System.out.println("ERROR: Could not process playlist since it could not be found.");
			logger.error("Playlist failed to be read cannot execute playlist processing.");

		} else {
			//Create Playlist from contents
			Playlist playlist = PlaylistFactory.create(URL, content);

			//Validate and generate report
			validateAndPrintReport(playlist);
		}

		logger.debug("<<out processPlaylist()");
	}
	
	/**
	 * getContent() will retrieve the content from a URL or file.
	 * 	Will print to console for user interactions.
	 *
	 * @param path the URL or file path to the content
	 */
	private static ArrayList<String> getContent(String path) {

		System.out.println(".\n.\n.\n");

		ArrayList<String> content = null;
		if(path.toLowerCase().startsWith("file://")) {
			logger.info("User key entered {} - validate local file", path);

			//Get contents of playlist from file
			content = HLSUtilities.getContentFromFile(path.substring(7));

		} else {
			logger.info("User key entered {} - validate URL", path);

			//Get contents of playlist from URL
			content = HLSUtilities.getUrlContents(path);
		}

		return content;
	}

	/**
	 * validateAndPrintReport() for invoking validation on playlist and printing of report.
	 * 	Will print to console for user interactions.
	 *
	 * @param playlist the playlist that will be validated and and have a report generated
	 */
	private static void validateAndPrintReport(Playlist playlist) {
		logger.debug("in>> validateAndPrintReport()");
		
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		System.out.println("       VALIDATION REPORT       ");
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

		runValidators(playlist);
		printReport(playlist);

		
		logger.debug("<<out validateAndPrintReport()");
	}
	
	/**
	 * runValidators() for running all validators on a playlist.
	 * 
	 * @param playlist the playlist for the validators to run against.
	 */
	private static void runValidators(Playlist playlist) {
		logger.debug("in>> runValidators()");

		Validator validator;
		
		logger.trace("RUNNING VALIDATORS.");
		
		validator = new FirstTagValidator();
		playlist.accept(validator);
		
		validator = new URISequenceValidator();
		playlist.accept(validator);
		
		validator = new MediaSegmentTimeValidator();
		playlist.accept(validator);
		
		validator = new TagContextValidator();
		playlist.accept(validator);
		
		logger.debug("<<out runValidators()");
	}
	
	/**
	 * printReport() for printing playlist information to the console as 
	 * 	well as determining which type of playlist to print.
	 * 
	 * @param playlist
	 */
	private static void printReport(Playlist playlist) {
		logger.debug("in>> printReport()");

		if(playlist instanceof MediaPlaylist) {
			System.out.println("Playlist Type: MEDIA PLAYLIST");
		} else {
			System.out.println("Playlist Type: MASTER PLAYLIST");
		}
		
		System.out.println("Playlist URL: " + playlist.getStrURL());

		if(playlist instanceof MediaPlaylist) {
			printMediaErrorReport((MediaPlaylist) playlist, "   ");

		} else if(playlist instanceof MasterPlaylist) {
			printMasterErrorReport((MasterPlaylist) playlist, "   ");

		} else {
			System.out.println("ERROR: Could not process playlist.");
			logger.error("Could not print validation report because playlist was not Media or Master type.");
		}
		
		logger.debug("<<out printReport()");
	}
	
	/**
	 * printMediaErrorReport() is used specifically for printing the validation 
	 * 	errors within a MediaPlaylist.
	 * 
	 * @param playlist the MediaPlaylist validation errors to be printed.
	 * @param prefix any prefix text that should go before a console print statement.
	 */
	private static void printMediaErrorReport(MediaPlaylist playlist, String prefix) {
		logger.debug("in>> printMediaErrorReport()");

		ArrayList<String> errors = (ArrayList<String>) playlist.getErrors();
		
		if(errors == null || errors.isEmpty()) {
			System.out.println(prefix + "SUCCESS - Media Playlist is valid format.");
		} else {
			for(String err : errors) {
				System.out.println(prefix + err);
			}
		}
		
		logger.debug("<<out printMediaErrorReport()");
	}
	
	/**
	 * printMasterErrorReport() is used specifically for printing the validation
	 * 	errors within a MasterPlaylist.
	 * 
	 * @param playlist the MasterPlaylist validation errors to be printed.
	 * @param prefix any prefix text that should go before a console print statement.
	 */
	private static void printMasterErrorReport(MasterPlaylist playlist, String prefix) {
		logger.debug("in>> printMasterErrorReport()");

		ArrayList<String> errors = (ArrayList<String>) playlist.getErrors();
		
		if(errors == null || errors.isEmpty()) {
			System.out.println(prefix + "SUCCESS - Master Playlist is valid format.");
		} else {
			for(String err : errors) {
				System.out.println(prefix + err);
			}
		}
		
		List<MediaPlaylist> variants = playlist.getVariants();
		for(MediaPlaylist variant : variants) {
			System.out.println("\t" + "MEDIA PLAYLIST: " + variant.getStrURL());
			printMediaErrorReport(variant, "\t" + prefix);
		}
		
		logger.debug("<<out printMasterErrorReport()");
	}

	private static String MENU = "\n--------------------------------------------------------\n"
										  + "               HLS Menu\n"
										  + "--------------------------------------------------------\n"
										  + "Please enter the URL or filepath to playlist that you\n wish to validate (or 'QUIT').  (Files must be prefixed\n with 'file://')\n"
										  + "\n>> ";

}
