package edu.psgv.sweng861.io;

import static org.junit.Assert.*;

import edu.psgv.sweng.io.HLSUtilities;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HLSUtilitiesTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testGetUrlContents() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testAbsolutizeURL() {
		String expectedURL = "http://gv8748.gv.psu.edu:8084/sweng861/walterebert-master-errors-01/ts/640x360.m3u8";
		
		String URL = "http://gv8748.gv.psu.edu:8084/sweng861/walterebert-master-errors-01/sintel-trailer.m3u8";
		String URI = "ts/640x360.m3u8";
		
		String actualURL = HLSUtilities.absolutizeURL(URL, URI);
		
		assertEquals(expectedURL, actualURL);
	}

}
