package org.wymiwyg.regexmap.tests;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Test;
import org.wymiwyg.regegexmap.Matcher;
import org.wymiwyg.regegexmap.Parser;
import org.wymiwyg.regegexmap.RegexMap;
import org.wymiwyg.regegexmap.State;
import org.wymiwyg.regegexmap.Transition;

public class RegexMapTest {
	
	@Test
	public void testPutAndGet() {
		RegexMap<Object> regexMap = new RegexMap<Object>();
		Object value1 = "val 1";
		Object value2 = "val 2";
		Object value3 = "val 3";
		Object value4 = "val 4";
		regexMap.put(".m", value1);
		regexMap.put(".*2", value2);
		regexMap.put("ff", value3);
		regexMap.put(".*foo", value4);
		assertEquals(value1, regexMap.get("rm"));
		assertEquals(value2, regexMap.get("gdfsgdf2"));
		assertEquals(value3, regexMap.get("ff"));
		assertEquals(value4, regexMap.get("dashkjhfoo"));
	}

}
