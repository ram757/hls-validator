package edu.psgv.sweng861.playlist;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.instanceOf;

import java.util.ArrayList;

import edu.psgv.sweng.playlist.MediaPlaylist;
import edu.psgv.sweng.playlist.Playlist;
import edu.psgv.sweng.playlist.PlaylistFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PlaylistFactoryTest {

	String strURL;
	ArrayList<String> mediaContents;
	ArrayList<String> masterContents;
	
	@Before
	public void setUp() throws Exception {
		strURL = "http://myURL.com";
		mediaContents = new ArrayList<String>();
		masterContents = new ArrayList<String>();
		
		//Build media contents
		mediaContents.add("#EXTM3U");
		mediaContents.add("#EXT-X-VERSION:3");
		mediaContents.add("#EXT-X-TARGETDURATION:11");
		mediaContents.add("#EXT-X-MEDIA-SEQUENCE:0");
		mediaContents.add("#EXTINF:9.189889,");
		mediaContents.add("640x3600.ts");
		mediaContents.add("#EXTINF:8.916667,");
		mediaContents.add("640x3601.ts");
		mediaContents.add("#EXT-X-ENDLIST");
		
		//Build master contents
		masterContents.add("#EXTM3U");
		masterContents.add("#EXT-X-STREAM-INF:PROGRAM-ID=1,BANDWIDTH=232370,CODECS=\"mp4a.40.2, avc1.4d4015\"");
		masterContents.add("gear1/prog_index.m3u8");
		masterContents.add("#EXT-X-STREAM-INF:PROGRAM-ID=1,BANDWIDTH=649879,CODECS=\"mp4a.40.2, avc1.4d401e\"");
		masterContents.add("gear2/prog_index.m3u8");

	}
	
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCreateWithMediaPlaylist() {
		
		Playlist actual = PlaylistFactory.create(strURL, mediaContents);
		
		assertThat(actual, instanceOf(MediaPlaylist.class));
	}


}
