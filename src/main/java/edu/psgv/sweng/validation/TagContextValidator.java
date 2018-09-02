package edu.psgv.sweng.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.psgv.sweng.playlist.MasterPlaylist;
import edu.psgv.sweng.playlist.MediaPlaylist;
import edu.psgv.sweng.playlist.PlaylistType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TagContextValidator extends Validator{
	/* LOGGER */
	private static final Logger logger = LogManager.getLogger();
	
	private static HashMap<String, String> errMap = new HashMap<>();

	/**
	 * validateMediaPlaylist() will validate a MEDIA Playlist for any errors
	 * 	related to tag context.  This includes tag mismatch errors, unknown tags, 
	 * 	deprecated tags, duplicate tags that should not be duplicated, unnecessary 
	 * 	whitespace, etc.
	 * 
	 * @param playlist the Media Playlist.
	 * @return a list of errors.
	 */
	public List<String> validateMediaPlaylist(MediaPlaylist playlist) {
		ArrayList<String> errors = new ArrayList<String>();
		
		List<String> contents = playlist.getContents();
		if(contents == null || contents.isEmpty()) {
			logger.error("The playlist being validated does not contain any content!");
			logger.debug("<<out validateMediaPlaylist()");
			return errors;
		}
		
		int lineNum = 0;
		int numOfDurations = 0;
		int numOfVersions = 0;

		for(String line : contents) {
			LineType type = ValidationUtils.getLineType(line);

			switch(type) {
				case TAG_TYPE:

					if(ValidationUtils.isTargetDuration(line)) {
						numOfDurations++;

						if(numOfDurations > 1) {
							errors.add(Validator.buildErrorMessage(lineNum + 1, "FATAL", errMap.get("manyTargetDurations")));
						}
						
					} else if (ValidationUtils.isVersionTag(line)) {
						numOfVersions++;
						
						if(numOfVersions > 1) {
							errors.add(Validator.buildErrorMessage(lineNum + 1, "FATAL", errMap.get("manyVersions")));
						}
						
					} else if(lineNum > 0 && !ValidationUtils.isCorrectPlaylistTag(PlaylistType.MEDIA, line)) {
						if(ValidationUtils.isDeprecatedTag(line)) {
							errors.add(Validator.buildErrorMessage(lineNum + 1, "WARNING", errMap.get("deprecated")));
						} else if(ValidationUtils.isBogusTag(PlaylistType.MEDIA, line)) {
							errors.add(Validator.buildErrorMessage(lineNum + 1, "WARNING", errMap.get("bogusTag") + "'" + ValidationUtils.getTagValue(line) +"'."));
						} else {
							errors.add(Validator.buildErrorMessage(lineNum + 1, "SEVERE", "Media Playlist must not contain Master Playlist tag: '" + ValidationUtils.getTagValue(line) + "'."));
						}
					}

					break;
				case ERROR_TYPE:
					errors.add(Validator.buildErrorMessage(lineNum + 1, "MINOR", errMap.get("whitespace")));
					
					break;
				case URI_TYPE:
					//No need at this time.
					break;
				case COMMENT_TYPE:
					//No need at this time.
					break;
			}

			lineNum++;
		}
		
		return errors;
	}
	
	/**
	 * validateMediaPlaylist() will validate a MASTER Playlist for any errors
	 * 	related to tag context.  This includes tag mismatch errors, unknown tags, 
	 * 	deprecated tags, duplicate tags that should not be duplicated, unnecessary 
	 * 	whitespace, etc.
	 * 
	 * @param playlist the Master Playlist.
	 * @return a list of errors.
	 */
	public List<String> validateMasterPlaylist(MasterPlaylist playlist) {
		ArrayList<String> errors = new ArrayList<String>();

		List<String> contents = playlist.getContents();
		if(contents == null || contents.isEmpty()) {
			return errors;
		}
		
		int lineNum = 0;
		int numOfVersions = 0;

		for(String line : contents) {
			LineType type = ValidationUtils.getLineType(line);

			switch(type) {
				case TAG_TYPE:
					if(ValidationUtils.isVersionTag(line)) {
						numOfVersions++;
						
						if(numOfVersions > 1) {
							errors.add(Validator.buildErrorMessage(lineNum + 1, "SEVERE", errMap.get("manyVersions")));
						}
					} else if (ValidationUtils.isVariantTag(line)) {
						if(!ValidationUtils.hasBandwidthAttribute(line)) {
							errors.add(Validator.buildErrorMessage(lineNum + 1, "SEVERE", errMap.get("bandwidthMissing")));
						}

					} else if(lineNum > 0 && !ValidationUtils.isCorrectPlaylistTag(PlaylistType.MASTER, line)) {
						if(ValidationUtils.isDeprecatedTag(line)) {
							errors.add(Validator.buildErrorMessage(lineNum + 1, "WARNING", errMap.get("deprecated")));
						} else if(ValidationUtils.isBogusTag(PlaylistType.MASTER, line)) {
							errors.add(Validator.buildErrorMessage(lineNum + 1, "WARNING", errMap.get("bogusTag") + "'" + ValidationUtils.getTagValue(line) +"'."));
						} else {
							errors.add(Validator.buildErrorMessage(lineNum + 1, "SEVERE", "Master Playlist must not contain Media Playlist tag: '" + ValidationUtils.getTagValue(line) + "'."));
						}
					}

					break;
				case ERROR_TYPE:
					errors.add(Validator.buildErrorMessage(lineNum + 1, "MINOR", errMap.get("whitespace")));
					
					break;

				case URI_TYPE:
					//No need to process at this time.
					break;
				case COMMENT_TYPE:
					//No need to process at this time.
					break;
			}
			lineNum++;
		}
		
		return errors;
	}
	
	//Static block for populating error message map with respective error messages
	static {
		errMap.put("manyTargetDurations", "Media Playlist should not contain more than one 'EXT-X-TARGETDURATION' tag.");

		errMap.put("bandwidthMissing", "Master Playlist 'EXT-X-STREAM-INF' tag must contain BANDWIDTH attribute.");

		errMap.put("manyVersions", "Playlist should not contain more than one 'EXT-X-VERSION' tag.");
		errMap.put("whitespace", "Playlist with blank lines should not contain whitespace.");
		errMap.put("bogusTag", "Playlist contains unrecognizable tag: ");
		
		errMap.put("deprecated", "Playlist contains 'EXT-X-ALLOW-CACHE' tag which was removed in protocol version 7.");
	}

}
