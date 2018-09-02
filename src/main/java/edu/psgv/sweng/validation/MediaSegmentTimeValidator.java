package edu.psgv.sweng.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.psgv.sweng.playlist.MasterPlaylist;
import edu.psgv.sweng.playlist.MediaPlaylist;

public class MediaSegmentTimeValidator extends Validator{
	/* LOGGER */
	private static final Logger logger = LogManager.getLogger();
	
	/* Container for error messages */
	private static HashMap<String, String> errMap = new HashMap<>();

	/**
	 * validateMediaPlaylist() will validate a Media Playlist for any errors
	 * 	related Media Segment time.
	 * 
	 * @param playlist the Media Playlist.
	 * @return a list of errors.
	 */
	public List<String> validateMediaPlaylist(MediaPlaylist playlist) {
		logger.debug("in>> validateMediaPlaylist()");

		ArrayList<String> errors = new ArrayList<>();
		
		//Get contents and check if that they exist
		List<String> contents = playlist.getContents();
		if(contents == null || contents.isEmpty()) {
			logger.error("The playlist being validated does not contain any content!");
			logger.debug("<<out validateMediaPlaylist()");

			return errors;
		}
		
		//Get Target duration and check whether or not media segments can be checked
		double targetDuration = ValidationUtils.getTargetDuration(contents);
		if(targetDuration < 0) {
			//Target duration found was invalid in some way so media segments cannot be validated
			
			int lineNum = ValidationUtils.hasTargetDuration(contents);
			
			if(lineNum < 0) {
				errors.add(Validator.buildErrorMessage(lineNum + 1, "FATAL", errMap.get("errMsgNoTarget")));
				logger.trace(errMap.get("errMsgNoTarget"));

			} else {
				errors.add(Validator.buildErrorMessage(lineNum + 1, "FATAL", errMap.get("errMsgTargetWithoutTime")));
				logger.trace(errMap.get("errMsgTargetWithoutTime"));
			}
			
		} else {
			//Target duration was found; Perform validation on media segments
			
			int lineNum = 0;

			for(String line : contents) {
				LineType type = ValidationUtils.getLineType(line);

				switch(type) {
					case COMMENT_TYPE:
						break;
					case URI_TYPE:
						break;
					case ERROR_TYPE:
						break;
						
					case TAG_TYPE:
						
						if(ValidationUtils.isMediaSegmentTag(line)) {
							double time = ValidationUtils.getDuration(line);

							if(time == -1.0) {
								errors.add(Validator.buildErrorMessage(lineNum + 1, "FATAL", errMap.get("errMsgTime")));
								logger.trace(errMap.get("errMsgTime"));
							} else if (time > targetDuration) {
								errors.add(Validator.buildErrorMessage(lineNum + 1, "SEVERE", errMap.get("errMsgTimeExceed") + targetDuration + "."));
								logger.trace(errMap.get("errMsgTimeExceed") + targetDuration + ".");
							}
						}
						
						break;
				}

				lineNum++;
			}
		}
		
		logger.debug("<<out validateMediaPlaylist()");
		return errors;
	}
	
	/**
	 * validateMasterPlaylist() will validate a Master Playlist for any errors
	 * 	related to Media Segment time.
	 * 
	 * 	There is no need to validate Master playlists for this.  Returning empty list.
	 * 
	 * @param playlist the Master Playlist.
	 * @return a list of errors.
	 */
	public List<String> validateMasterPlaylist(MasterPlaylist playlist) {
		logger.debug("in>> validateMasterPlaylist()");

		logger.warn("MediaSegmentValidator has no validation to perform on Master playlists.");
		
		logger.debug("<<out validateMasterPlaylist()");
		return new ArrayList<String>();
	}
	
	//Static block for populating error message map with respective error messages
	static {
		errMap.put("errMsgTime", "Media Playlist segment tag 'EXTINF' must include a duration time.");
		errMap.put("errMsgTimeExceed", "Media Playlist segment duration should not exceed the target duration of: ");
		errMap.put("errMsgNoTarget", "Media Playlist must contain a target duration tag.");
		errMap.put("errMsgTargetWithoutTime", "Media Playlist target duration tag 'EXT-X-TARGETDURATION' must contain a numeric value.");
	}
}
