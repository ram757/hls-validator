package edu.psgv.sweng861.validation;

import static org.junit.Assert.*;

import edu.psgv.sweng.validation.LineType;
import edu.psgv.sweng.validation.ValidationUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ValidationUtilsTest {

	String targetDurationTag = "EXT-X-TARGETDURATION";
	
	String commentString;
	String tagString;
	String errorString;
	String uriString;
	String uriString2;
	
	String tag1;
	String tag2;
	
	@Before
	public void setUp() throws Exception {
		commentString = "#This is a comment bro.";
		tagString = "#EXTM3U";
		errorString = "  ";
		uriString = "gear1/prog_index.m3u8";
		uriString2 = "640x3600.ts";
		
		tag1 = "#" + targetDurationTag + ":11";
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testGetLineType() {
		assertEquals(LineType.COMMENT_TYPE, ValidationUtils.getLineType(commentString));
		assertEquals(LineType.TAG_TYPE, ValidationUtils.getLineType(tagString));
		assertEquals(LineType.ERROR_TYPE, ValidationUtils.getLineType(errorString));
		assertEquals(LineType.URI_TYPE, ValidationUtils.getLineType(uriString));
		assertEquals(LineType.URI_TYPE, ValidationUtils.getLineType(uriString2));
	}
	
	@Test
	public final void testGetTagValue() {
		String expected = targetDurationTag;
		String actual = ValidationUtils.getTagValue(tag1);
		
		assertEquals(expected, actual);
	}

}
