package edu.psgv.sweng.playlist;

import java.util.ArrayList;
import java.util.List;

import edu.psgv.sweng.io.HLSUtilities;
import edu.psgv.sweng.validation.ValidationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.psgv.sweng.validation.LineType;


/**
 * PlaylistFactory is used to build Playlists based on the contents
 *  that are provided.
 *
 * @author Ryan McDonald
 * @version 3.1.0
 */
public class PlaylistFactory {
	/* LOGGER */
	private static final Logger logger = LogManager.getLogger();

	/**
	 * create() is a factory method for creating a playlist object of the
	 *  correct type based on the content that is passed to the method.
	 *
	 * @param strURL the string URL containing the URL
	 * @param contents the contents of the playlist
	 * @return the Playlist created from the parameters
	 */
	public static Playlist create(String strURL, List<String> contents) {
		logger.debug("in>> create()");

		Playlist playlist = null;
		PlaylistType type = getType(contents);

		switch(type) {
			case MEDIA:
				playlist = new MediaPlaylist(strURL, contents);
				logger.trace("Playlist factory method created a MediaPlaylist.");
				break;

			case MASTER:
				playlist = new MasterPlaylist(strURL, contents, extractVariants(strURL, contents));
				logger.trace("Playlist factory method created a MasterPlaylist.");
				break;

			case ERROR:
				//TODO: will this be necessary?  May be out of scope.
				playlist = null;
				logger.trace("Playlist factory method failed to create Playlist object.");
				break;

			case NULL:
				//TODO: will this be necessary?  May be out of scope.
				playlist = null;
				logger.trace("Playlist factory method failed to create Playlist object.");
				break;
		}

		logger.debug("<<out create()");
		return playlist;
	}

	/**
	 * extractVariants() is used by the factory method when creating a MasterPlaylist
	 *  since a MasterPlaylist requires it's Variants to be extracted as MediaPlaylists.
	 *
	 * @param strURL the string URL containing the URL
	 * @param contents the contents of the playlist
	 * @return a list of MediaPlaylists which are the MasterPlaylist's variants
	 */
	private static List<MediaPlaylist> extractVariants(String strURL, List<String> content) {
		logger.debug("in>> extractVariants()");

		List<MediaPlaylist> variants = new ArrayList<MediaPlaylist>();

		for(int i = 0; i < content.size(); i++) {
			if(ValidationUtils.isVariantTag(content.get(i))) {
				String uri = content.get(i + 1);

				if(ValidationUtils.isURIType(uri)) {
					String url = HLSUtilities.absolutizeURL(strURL, uri);
					logger.trace("Absolutized URL for Variant: {}", url);

					ArrayList<String> variantContent = getVariant(url);

					//TODO: Perform null check?  Or allow MediaPlaylists with null content
					variants.add( new MediaPlaylist(url, variantContent) );
				}
			}
		}

		logger.debug("<<out extractVariants()");
		return variants;
	}


	/**
	 * getVariant() retrieves the content from the variant playlist.
	 *
	 * @param strURL the string URL containing the URL
	 * @return a list containing the content extracted from the variant at the given URL
	 */
	private static ArrayList<String> getVariant(String url) {
		logger.debug("in>> getVariant()");

		ArrayList<String> variantContent = null;

		if(url.startsWith("file://")) {
			variantContent = HLSUtilities.getContentFromFile(url.substring(7));
		} else {
			variantContent = HLSUtilities.getUrlContents(url);
		}

		logger.debug("<<out getVariant()");
		return variantContent;
	}

	/**
	 * getType() retrieves the type of playlist to create based on the contents.
	 *
	 * @param contents the playlist contents that will be used to distinguish playlist type.
	 * @return a Playlist type.
	 */
	private static PlaylistType getType(List<String> contents) {
		logger.debug("in>> getType()");

		PlaylistType type = PlaylistType.NULL;

		for(String line : contents) {
			if(ValidationUtils.getLineType(line) == LineType.TAG_TYPE) {
				if(ValidationUtils.getTagValue(line).equals("EXTINF")) {
					//TODO: Will this be necessary?
					//type = (type != (PlaylistType.MASTER)) ? PlaylistType.MEDIA : PlaylistType.ERROR;
					type = PlaylistType.MEDIA;
					logger.trace("Playlist type is: MEDIA PLAYLIST");
					break;
				}

				if(ValidationUtils.getTagValue(line).equals("EXT-X-STREAM-INF")) {
					//TODO: Will this be necessary?
					//type = (type != (PlaylistType.MEDIA)) ? PlaylistType.MASTER : PlaylistType.ERROR;
					type = PlaylistType.MASTER;
					logger.trace("Playlist type is: MASTER PLAYLIST");
					break;
				}
			}
		}

		logger.debug("<<out getType()");
		return type;
	}

}
