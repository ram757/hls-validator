package edu.psgv.sweng.playlist;

import java.util.List;

import edu.psgv.sweng.validation.Validator;

/**
 * Playlist is an abstract class that should be implemented through concrete
 *  representations of playlists.
 *
 * @author Ryan McDonald
 */
public abstract class Playlist {
	/* LOGGER - NOT NECESSARY */
	//private static final Logger logger = LogManager.getLogger();

	protected String strURL;
	protected List<String> contents;
	protected List<String> errors;

	/**
	 * getStrURL() returns the string representation of the Playlists URL.
	 *
	 * @return a string representation of the Playlists URL.
	 */
	public String getStrURL() {
		return strURL;
	}

	/**
	 * getContents() returns a list of content that this playlist contains.
	 *
	 * @return the list of content contained within this playlist.
	 */
	public List<String> getContents() {
		return contents;
	}
	
	/**
	 * getErrors() returns a list of errors that this playlist contains based on 
	 * 	any validators that run on it.
	 *
	 * @return the list of errors contained within this playlist.
	 */
	public List<String> getErrors() {
		return errors;
	}


	/**
	 * accept() should be implemented for playlists to accept validators that populate the
	 * 	playlists 'errors' field.
	 *
	 * @param v the Validator to run on the contents of the playlist
	 */
	public abstract void accept(Validator v);
	
}
