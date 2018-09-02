package edu.psgv.sweng.playlist;

import java.util.ArrayList;
import java.util.List;

import edu.psgv.sweng.validation.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * MasterPlaylist provides a container for Master Playlists.  It also
 *  supports containing any Media Playlists (variants) that it links to.
 *  Through the use of the Validator library, it can also find errors in
 *  it's playlists that it contains.
 *
 * @author Ryan McDonald
 */
public class MasterPlaylist extends Playlist{
	/* LOGGER */
	private static final Logger logger = LogManager.getLogger();

	protected List<MediaPlaylist> variants;
	
	public MasterPlaylist(String strURL, List<String> contents, List<MediaPlaylist> variants) {
		this.strURL = strURL;
		this.contents = contents;
		this.variants = variants;

		errors = new ArrayList<String>();
		
		logger.info("MASTER PLAYLIST created.");
	}
	

	/**
	 * getVariants() returns a list of MediaPlaylists that are the variants for this MasterPlaylist
	 *
	 * @return a list of Variants for this MasterPlaylist
	 */
	public List<MediaPlaylist> getVariants() {
		return this.variants;
	}
	
	
	/**
	 * accept() gathers any validation errors found in the playlist for any Validator passed in.
	 * 	It also runs the Validator on it's variant MediaPlaylists.
	 *
	 * @param v is the Validator that will be run on this playlist
	 */
	public void accept(Validator v){
		logger.debug("in>> accept()");

		List<String> errs = v.validateMasterPlaylist(this);
		
		if(errs != null && !errs.isEmpty()) {
			this.errors.addAll(errs);
		}
		
		//Accept the validator in all Media variants
		for(MediaPlaylist variant : this.variants) {
			variant.accept(v);
		}
		
		logger.debug("<<out accept()");
	}

}
