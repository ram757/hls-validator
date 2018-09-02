package edu.psgv.sweng.playlist;

import java.util.ArrayList;
import java.util.List;

import edu.psgv.sweng.validation.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * MediaPlaylist provides a container for Media Playlists.
 *  Through the use of the Validator library, it can also find errors in
 *  it's playlists that it contains.
 *
 * @author Ryan McDonald
 */
public class MediaPlaylist extends Playlist {
	/* LOGGER */
	private static final Logger logger = LogManager.getLogger();
	
	public MediaPlaylist(String strURL, List<String> contents) {
		this.strURL = strURL;
		this.contents = contents;
		
		this.errors = new ArrayList<String>();
		
		logger.info("MEDIA PLAYLIST created.");
	}
	
	
	/**
	 * accept() gathers any validation errors found in the playlist for any validator passed in.
	 *
	 * @param v is the Validator that will be run on this playlist
	 */
	public void accept(Validator v){
		logger.debug("in>> accept()");

		List<String> errs = v.validateMediaPlaylist(this);
		
		if(errs != null && !errs.isEmpty()) {
			this.errors.addAll(errs);
		}
		
		logger.debug("<<out accept()");
	}
}
