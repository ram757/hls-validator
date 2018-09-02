package edu.psgv.sweng.validation;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.psgv.sweng.playlist.MasterPlaylist;
import edu.psgv.sweng.playlist.MediaPlaylist;

public abstract class Validator {
	/* LOGGER */
	private static final Logger logger = LogManager.getLogger();
	
	/**
	 * validateMediaPlaylist() must be implemented to validate a Media Playlist.
	 * @param playlist the playlist to validate.
	 * 
	 * @return a list of any validation errors.
	 */
	public abstract List<String> validateMediaPlaylist(MediaPlaylist playlist);
	
	/**
	 * validateMediaPlaylist() must be implemented to validate a Master Playlist.
	 * @param playlist the playlist to validate.
	 * 
	 * @return a list of any validation errors.
	 */
	public abstract List<String> validateMasterPlaylist(MasterPlaylist playlist);
	
	/**
	 * buildErrorMessage is a concrete class offered to all Validators for building 
	 * 	error messages easily.  
	 * 
	 * @param lineNum the line number of the error occurence.
	 * @param errLvl the log level for the error to show.
	 * @param errMessage the error message.
	 * @return a standardized error message as a String.
	 */
	protected static String buildErrorMessage(int lineNum, String errLvl , String errMessage) {
		logger.debug("in>> buildErrorMessage()");

		StringBuilder err = new StringBuilder();
		err.append("[").append(errLvl).append(" | line ").append(lineNum).append(": ").append(errMessage);
		
		logger.debug("<<out buildErrorMessage()");
		return err.toString();
	}
	
}
