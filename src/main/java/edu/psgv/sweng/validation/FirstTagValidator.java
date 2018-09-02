package edu.psgv.sweng.validation;

import java.util.ArrayList;
import java.util.List;

import edu.psgv.sweng.playlist.MasterPlaylist;
import edu.psgv.sweng.playlist.MediaPlaylist;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FirstTagValidator provides a validator for validating that playlists 
 * 	contain a 'EXTM3U' tag in it's first line.  This is done so using the
 *  Visitor pattern.
 * 
 * 	Errors are returned in a list.
 *
 */
public class FirstTagValidator extends Validator{
	/* LOGGER */
	private static final Logger logger = LogManager.getLogger();
	
	private String errMsg = "Playlist file does not contain required M3U tag on line 1.  Caution processing playlist.";
	private String errMsgFatal = "Playlist is empty.  Cannot validate.";

	/**
	 * validateMediaPlaylist() will validate a Media Playlist for any errors
	 * 	related to the first tag of the playlist.
	 * 
	 * @param playlist the Media Playlist.
	 * @return a list of errors.
	 */
	public List<String> validateMediaPlaylist(MediaPlaylist playlist) {
		logger.debug("validateMediaPlaylist() deferring responsibilty to validatePlaylist()");

		return this.validatePlaylist(playlist.getContents());
	}
	
	/**
	 * validateMasterPlaylist() will validate a Master Playlist for any errors
	 * 	related to the first tag of the playlist.
	 * 
	 * @param playlist the Master Playlist.
	 * @return a list of errors.
	 */
	public List<String> validateMasterPlaylist(MasterPlaylist playlist) {
		logger.debug("validateMediaPlaylist() deferring responsibilty to validatePlaylist()");

		return this.validatePlaylist(playlist.getContents());
	}
	
	/**
	 * validatePlaylist() will validate Playlist's contents for any errors
	 * 	related to the first tag of the playlist.
	 * 
	 * @param contents the contents of a Playlist.
	 * @return a list of errors.
	 */
	private List<String> validatePlaylist(List<String> contents) {
		logger.debug("in>> validatePlaylist()");

		ArrayList<String> errors = new ArrayList<String>();
		
		if(contents == null || contents.size() == 0) {
			errors.add(Validator.buildErrorMessage(1, "FATAL", errMsgFatal));
			logger.error("The playlist being validated does not contain any content!");
		} else if(!FirstTagValidator.isValidPlaylist(contents.get(0))) {
			errors.add(Validator.buildErrorMessage(1, "MINOR", errMsg));
		}
		
		logger.debug("<<out validatePlaylist()");

		return errors;
	}
	
	/**
	 * isValidPlaylist() returns whether or not this is a valid playlist or not based
	 *  on the requirement of containing the 'EXTM3U' tag on it's first line of content.
	 *
	 * @param firstLine is the first line of the playlist that will be checked
	 * @return whether or not the playlist is valid.
	 */
	private static boolean isValidPlaylist(String firstLine) {
		logger.debug("in>> isValidPlaylist()");

		boolean isValid = false;

		if(firstLine != null && !firstLine.isEmpty() && firstLine.contains("EXTM3U")) {
			isValid = true;
			logger.trace("Playlist is valid!  Line contains EXTM3U tag.");
		}

		logger.debug("<<out isValidPlaylist()");
		return isValid;
	}
}
