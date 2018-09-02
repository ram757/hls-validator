package edu.psgv.sweng.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.psgv.sweng.playlist.MasterPlaylist;
import edu.psgv.sweng.playlist.MediaPlaylist;
import edu.psgv.sweng.playlist.Playlist;
import edu.psgv.sweng.playlist.PlaylistType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class URISequenceValidator extends Validator{
	/* LOGGER */
	private static final Logger logger = LogManager.getLogger();

	/* Container for error messages */
	private static HashMap<String, String> errMap = new HashMap<>();

	/**
	 * validateMediaPlaylist() will validate a MEDIA Playlist for any errors
	 * 	related to improper sequencing of URI tags and URIs that should directly
	 * 	follow.
	 * 
	 * @param playlist the Media Playlist.
	 * @return a list of errors.
	 */
	public List<String> validateMediaPlaylist(MediaPlaylist playlist) {
		logger.debug("validateMediaPlaylist() deferring responsibilty to validatePlaylist()");

		return validatePlaylist(playlist, PlaylistType.MEDIA);
	}
	
	/**
	 * validateMasterPlaylist() will validate a MASTER Playlist for any errors
	 * 	related to improper sequencing of URI tags and URIs that should directly
	 * 	follow.
	 * 
	 * @param playlist the Master Playlist.
	 * @return a list of errors.
	 */
	public List<String> validateMasterPlaylist(MasterPlaylist playlist) {
		logger.debug("validateMasterPlaylist() deferring responsibilty to validatePlaylist()");

		return validatePlaylist(playlist, PlaylistType.MASTER);
	}
	
	
	/**
	 * Performs the actual validation of the playlists.
	 * 
	 * @param playlist the playlist to be validated.
	 * @param plType the type of playlist.
	 * 
	 * @return a list of errors.
	 */
	private List<String> validatePlaylist(Playlist playlist, PlaylistType plType) {
		logger.debug("in>> validatePlaylist()");

		ArrayList<String> errors = new ArrayList<String>();

		List<String> contents = playlist.getContents();
		if(contents == null || contents.isEmpty()) {
			logger.error("The playlist being validated does not contain any content!");
			logger.debug("<<out validatePlaylist()");
			return errors;
		}
		
		int lineNum = 0;
		boolean shouldBeURI = false;

		for(String line : contents) {
			LineType type = ValidationUtils.getLineType(line);

			if(shouldBeURI) {
				if(type != LineType.URI_TYPE) {
					String err = (plType == PlaylistType.MEDIA) ? errMap.get("errMsgMedia") : errMap.get("errMsgMaster");
					
					errors.add(Validator.buildErrorMessage(lineNum + 1, "FATAL", err));
					logger.trace("Media segment file or Media Playlist file should have been found.");
				}

				shouldBeURI = false;
			}

			switch(type) {
				case COMMENT_TYPE:
					break;
				case URI_TYPE:
					break;
				case ERROR_TYPE:
					break;
					
				case TAG_TYPE:
					
					if(plType == PlaylistType.MEDIA) {
						if(ValidationUtils.isMediaSegmentTag(line)) {
							shouldBeURI = true;
						}
						
					} else {
						if(ValidationUtils.isVariantTag(line)) {
							shouldBeURI = true;
						}
					}

					break;
			}

			lineNum++;
		}
		
		logger.debug("<<out validatePlaylist()");
		return errors;
	}
	
	//Static block for populating error message map with respective error messages
	static {
		errMap.put("errMsgMedia", "Media Playlist must have media segment file on line after 'EXTINF' tag.");
		errMap.put("errMsgMaster", "Master Playlist must have Media Playlist file on line after 'EXT-X-STREAM' tag.");
	}
}
