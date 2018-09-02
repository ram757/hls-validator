package edu.psgv.sweng.validation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.psgv.sweng.playlist.PlaylistType;


/**
 * Validator provides a library of static methods for retrieving information from
 *  lines of a Playlist as well as validating content of a Playlist.
 *
 * @author Ryan McDonald
 */
public class ValidationUtils {

	/* LOGGER */
	private static final Logger logger = LogManager.getLogger();
	
	/* Containers for known tag types */
	private static HashSet<String> mediaPlaylistTags = new HashSet<String>();
	private static HashSet<String> masterPlaylistTags = new HashSet<String>();
	private static HashSet<String> deprecatedTags = new HashSet<String>();

	/**
	 * getLineType() finds what the type of line is in reference to a Playlist.
	 *
	 * @param line the line of content
	 * @return the type of line
	 */
	public static LineType getLineType(String line) {
		logger.debug("in>> getLineType()");

		LineType type = null;

		if(line.length() == 0) {
			type = LineType.COMMENT_TYPE;

		} else if(line.startsWith("#")) {
			if(line.startsWith("#EXT")) {
				type = LineType.TAG_TYPE;
			} else {
				type = LineType.COMMENT_TYPE;
			}

		} else if (line.trim().length() == 0) {
			type = LineType.ERROR_TYPE;

		} else {
			type = LineType.URI_TYPE;
		}

		logger.debug("<<out getLineType()");
		return type;
	}

	/**
	 * getTagValue() finds the tag value of a Playlist tag
	 *
	 * @param line the line of content
	 * @return the value that the tag contains
	 */
	public static String getTagValue(String line) {
		logger.debug("in>> getTagValue()");

		int startPos = 0;
		int endPos = line.length();

		if(line.startsWith("#")) {
			startPos = 1;
		}

		if(line.substring(startPos).indexOf(":") > 0 ) {
			endPos = (line.substring(startPos).indexOf(":") - (startPos == 0 ? 0 : -1));
		}

		logger.debug("<<out getTagValue()");
		return line.substring(startPos, endPos);
	}

	/**
	 * getAttributeList() finds all of the attributes of a tag and returns them
	 *  as a list.
	 *
	 * @param line the line of content
	 * @return a list of attributes where each attribute contains it's key-value pair
	 */
	public static ArrayList<String> getAttributeList(String line) {
		logger.debug("in>> getAttributeList()");

		ArrayList<String> attList = new ArrayList<>();

		int startPos = line.indexOf(":") >= 0 ? line.indexOf(":") + 1 : 0;
		String attributesPart = line.substring(startPos);

		if(attributesPart.indexOf(",") < 0) {
			logger.debug("Found single non-attribute field: {}.", attributesPart);

			attList.add(attributesPart);
		} else {
			String[] attributes = attributesPart.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
			for(String att : attributes) {
				if(!att.isEmpty()) {
					logger.debug("Found attribute pair: {}", att);

					attList.add(att);
				}
			}
		}

		logger.debug("<<out getAttributeList()");
		return attList;
	}

	//TAG VALIDATION METHODS

	/**
	 * isURIType() Checks if a line of content is a URI
	 *
	 * @param line the line of content
	 * @return whether or not a line of content is a URI
	 */
	public static boolean isURIType(String line) {
		return LineType.URI_TYPE == getLineType(line) ? true : false;
	}

	/**
	 * isVariantTag() Checks if a line of content is a Variant Tag
	 *
	 * @param line the line of content
	 * @return whether or not a line of content is a Variant Tag
	 */
	public static boolean isVariantTag(String line) {
		logger.debug("in>> isVariantTag()");

		if(ValidationUtils.getTagValue(line).equals("EXT-X-STREAM-INF")) {
			return true;
		}

		logger.debug("<<out isVariantTag()");
		return false;
	}

	/**
	 * isMediaSegmentTag() Checks if a line of content is Media Segment Tag
	 *
	 * @param line the line of content
	 * @return whether or not a line of content is a Media Segment Tag
	 */
	public static boolean isMediaSegmentTag(String line) {
		logger.debug("in>> isMediaSegmentTag()");

		boolean toReturn = false;

		//Determine if proper file extension
		if(getTagValue(line).equals("EXTINF")) {
			logger.debug("Media segment comment; Next line should be media segment.");

			toReturn = true;
		}

		logger.debug("<<out isMediaSegmentTag()");
		return toReturn;
	}
	
	/**
	 * isVersionTag() Checks if a line of content is a Version Tag.
	 *
	 * @param line the line of content.
	 * @return whether or not a line of content is a Version Tag.
	 */
	public static boolean isVersionTag(String line) {
		logger.debug("in>> isVersionTag()");

		boolean toReturn = false;

		//Determine if proper file extension
		if(getTagValue(line).equals("EXT-X-VERSION")) {
			logger.trace("Playlist version tag found.");

			toReturn = true;
		}

		logger.debug("<<out isVersionTag()");
		return toReturn;
	}
	
	/**
	 * isBogusTag() Checks if a line of content is a bogus Tag.
	 *
	 * @param line the line of content.
	 * @return whether or not a line of content is a bogus Tag.
	 */
	public static boolean isBogusTag(PlaylistType type, String line) {
		logger.debug("in>> isBogusTag()");

		boolean toReturn = false;
		String tagValue = getTagValue(line);
	
		if(!mediaPlaylistTags.contains(tagValue) && !masterPlaylistTags.contains(tagValue)) {
			logger.warn("An unknown (or bogus) tag with value of '" + tagValue + "' was found!");

			toReturn = true;
		}

		logger.debug("<<out isBogusTag()");
		return toReturn;
	}
	
	/**
	 * isDeprecatedTag() Checks if a line of content contains a deprecated tag.
	 *
	 * @param line the line of content.
	 * @return whether or not a line of content contains a deprecated tag.
	 */
	public static boolean isDeprecatedTag(String line) {
		logger.debug("in>> isDeprecatedTag()");

		boolean toReturn = false;
		String tagValue = getTagValue(line);
		
		if(deprecatedTags.contains(tagValue)) {
			logger.warn("The deprecated tag with value of '" + tagValue + "' was found!");
			toReturn = true;
		}

		logger.debug("<<out isDeprecatedTag()");
		return toReturn;
	}
	
	/**
	 * isCorrectPlaylistTag() Checks if a line of content is of the correct playlist.
	 *
	 * @param line the line of content.
	 * @return whether or not a line of content is of the correct playlist.
	 */
	public static boolean isCorrectPlaylistTag(PlaylistType type, String line) {
		logger.debug("in>> isCorrectPlaylistTag()");

		boolean toReturn = false;
		String tagValue = getTagValue(line);
		
		if(type == PlaylistType.MEDIA) {
			if(mediaPlaylistTags.contains(tagValue)) {
				toReturn = true;
			}
		} else {
			if(masterPlaylistTags.contains(tagValue)) {
				toReturn = true;
			}
		}

		logger.debug("<<out isCorrectPlaylistTag()");
		return toReturn;
	}

	/**
	 * isTargetDuration() Checks if a line of content is a Target Duration Tag.
	 *
	 * @param line the line of content.
	 * @return whether or not a line of content is a Target Duration Tag.
	 */
	public static boolean isTargetDuration(String line) {
		logger.debug("in>> isTargetDuration()");

		boolean toReturn = false;

		if(getTagValue(line).equals("EXT-X-TARGETDURATION")) {
			logger.trace("Media target duration tag found.");

			toReturn = true;
		} 

		logger.debug("<<out isTargetDuration()");
		return toReturn;
	}


	/**
	 * getDuration() Returns the duration contained in a line of content.
	 *
	 * @param line the line of content
	 * @return the decimal value of the duration
	 */
	public static double getDuration(String line) {
		logger.debug("in>> getDuration()");

		double duration = - 1.0;

		ArrayList<String> list = getAttributeList(line);
		if(list.size() > 0) {
			try {
				duration = Double.parseDouble(list.get(0));
				logger.trace("Duration sucessfully found with value of " + duration + "!");

			} catch(NumberFormatException e) {
				logger.warn("Duration should contain an integer or decimal value.");
			}
		}

		logger.debug("<<out getDuration()");
		return duration;
	}

	/**
	 * getTargetDuration() Returns the target duration contained in a line of content
	 *
	 * @param content the list of playlist content
	 * @return the decimal value of the target duration
	 */
	public static double getTargetDuration(List<String> content) {
		logger.debug("in>> getTargetDuration()");

		double duration = - 1.0;

		for(String line : content) {
			if(isTargetDuration(line)) {
				duration = getDuration(line);
				if(duration >= 0.0) {
					logger.trace("Target duration with duration of " + duration + " was found!");
					break;
				}
			}
		}

		logger.debug("<<out getTargetDuration()");
		return duration;
	}
	
	/**
	 * hasTargetDuration() Returns a line number of the first target duration tag
	 * 	if one exists.  Otherwise it returns -1.
	 *
	 * @param content the list of playlist content
	 * @return the line number of the target duration tag
	 */
	public static int hasTargetDuration(List<String> content) {
		logger.debug("in>> hasTargetDuration()");

		boolean hasTargetDuration = false;
		int lineNum = 1;
		for(String line : content) {
			if(isTargetDuration(line)) {
				hasTargetDuration = true;
				logger.trace("Playlist has target duration tag!");
				break;
			}
			
			lineNum++;
		}
		
		if(!hasTargetDuration) {
			lineNum = -1;
		}

		logger.debug("<<out hasTargetDuration()");
		return lineNum;
	}

	/**
	 * hasBandwidthAttribute() Checks if a line of content has a bandwidth attribute
	 *
	 * @param line the line of content
	 * @return whether or not a line of content contains a bandwidth attribute
	 */
	public static boolean hasBandwidthAttribute(String line) {
		logger.debug("in>> hasBandwidthAttribute()");

		boolean hasBandwidth = false;

		ArrayList<String> attributes = getAttributeList(line);
		if(attributes != null && !attributes.isEmpty()) {
			for(String att : attributes) {
				if(att.toLowerCase().startsWith("bandwidth")) {
					hasBandwidth = true;
					logger.trace("Bandwidth attribute was found!");
				}
			}
		}

		logger.debug("<<out hasBandwidthAttribute()");
		return hasBandwidth;
	}
	
	/**
	 * static block for initializing known playlist tags for validation.
	 */
	static {
		//Media Playlist tags
		mediaPlaylistTags.add("EXTM3U");
		mediaPlaylistTags.add("EXT-X-VERSION");
		
		mediaPlaylistTags.add("EXTINF");
		mediaPlaylistTags.add("EXT-X-BYTERANGE");
		mediaPlaylistTags.add("EXT-X-DISCONTINUITY");
		mediaPlaylistTags.add("EXT-X-KEY");
		mediaPlaylistTags.add("EXT-X-MAP");
		mediaPlaylistTags.add("EXT-X-PROGRAM-DATE-TIME");
		mediaPlaylistTags.add("EXT-X-DATERANGE");

		mediaPlaylistTags.add("EXT-X-INDEPENDENT-SEGMENTS");
		mediaPlaylistTags.add("EXT-X-START");
		mediaPlaylistTags.add("EXT-X-TARGETDURATION");
		mediaPlaylistTags.add("EXT-X-MEDIA-SEQUENCE");
		mediaPlaylistTags.add("EXT-X-DISCONTINUITY-SEQUENCE");
		mediaPlaylistTags.add("EXT-X-ENDLIST");
		mediaPlaylistTags.add("EXT-X-PLAYLIST-TYPE");
		mediaPlaylistTags.add("EXT-X-I-FRAMES-ONLY");
		
		//Master Playlist tags
		masterPlaylistTags.add("EXTM3U");
		masterPlaylistTags.add("EXT-X-VERSION");
		masterPlaylistTags.add("EXT-X-INDEPENDENT-SEGMENTS");
		masterPlaylistTags.add("EXT-X-START");
		masterPlaylistTags.add("EXT-X-MEDIA");
		masterPlaylistTags.add("EXT-X-STREAM-INF");
		masterPlaylistTags.add("EXT-X-I-FRAME-STREAM-INF");
		masterPlaylistTags.add("EXT-X-SESSION-DATA");
		masterPlaylistTags.add("EXT-X-SESSION-KEY");
		
		//Deprecated tags
		deprecatedTags.add("EXT-X-ALLOW-CACHE");
	}

}
