package com.adler.test;

import java.io.IOException;
import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.adler.dynamicJson.EditableJsonMap;
import com.adler.dynamicJson.base.JsonMap;
import com.adler.dynamicJson.exception.InvalidPropertyName;
import com.adler.dynamicJson.lib.JsonMapReaderLib;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonMapTest {

	ObjectMapper om = new ObjectMapper();

	private static final Logger LOGGER = Logger.getLogger(JsonMapTest.class);

	@Test
	public void test() throws JsonParseException, IOException {

		String body = "{\"a\":\"b\"}";
		JsonMap jm = JsonMapReaderLib.read(body, om);

		JsonMap bb = jm.getRootProperty("a");
		Assert.assertEquals(new JsonMap("b"), bb);

		bb = jm.getProperty("$.a");
		Assert.assertEquals(new JsonMap("b"), bb);
		
	}
	
	
	@Test
	public void testArray() throws JsonParseException, IOException {

		String body = "[{\"a\":\"b\"}]";
		JsonMap jm = JsonMapReaderLib.read(body, om);

		JsonMap el =jm.getProperty("$.[0]");
		JsonMap bb = el.getRootProperty("a");
		Assert.assertEquals(new JsonMap("b"), bb);
		
		try{
			el =jm.getProperty("$.[1]");
			Assert.fail();
		}catch(IllegalArgumentException e){}
		
	}
	

	@Test
	public void testJsonPath() throws JsonParseException, IOException {

		String body = "{\"a\":{\"b\":{\"c\":\"d\"}}}";
		JsonMap jm = JsonMapReaderLib.read(body, om);

		EditableJsonMap ejm = new EditableJsonMap(jm);

		JsonMap bb = jm.getProperty("$.a.b.c");
		Assert.assertEquals(new JsonMap("d"), bb);

		try {
			ejm.addProperty("c", new JsonMap(BigDecimal.TEN));
		}
		catch (InvalidPropertyName e1) {
			Assert.fail();
		}
		bb = ejm.getProperty("$.c");
		Assert.assertEquals(new JsonMap(BigDecimal.TEN), bb);

		try {
			ejm.addProperty("c", new JsonMap(BigDecimal.TEN));
			Assert.fail();
		}
		catch (InvalidPropertyName e) {

		}
		ejm.addPropertyForceOverride("c", new JsonMap(BigDecimal.ONE));
		bb = ejm.getProperty("$.c");
		Assert.assertEquals(new JsonMap(BigDecimal.ONE), bb);

		ejm.addPropertyForceOverride("b", new JsonMap(BigDecimal.ONE));
		bb = ejm.getProperty("$.b");
		Assert.assertEquals(new JsonMap(BigDecimal.ONE), bb);

	}

	@Test
	public void testJsonPathArray() throws JsonParseException, IOException {

		String body = "{\"a\":[{\"c\":\"d\"}]}";
		JsonMap jm = JsonMapReaderLib.read(body, om);

		JsonMap bb = jm.getProperty("$.a[0].c");
		Assert.assertEquals(new JsonMap("d"), bb);

		body = "{\"a\":[{\"a\":\"a\"},{\"c\":\"d\"}]}";
		jm = JsonMapReaderLib.read(body, om);

		bb = jm.getProperty("$.a[1].c");
		Assert.assertEquals(new JsonMap("d"), bb);

		bb = jm.getProperty("$.a[0].a");
		Assert.assertEquals(new JsonMap("a"), bb);

		body = "{\"a\":[{\"a\":[{\"a\":\"b\"}]},{\"c\":\"d\"}]}";
		jm = JsonMapReaderLib.read(body, om);

		bb = jm.getProperty("$.a[0].a[0].a");
		Assert.assertEquals(new JsonMap("b"), bb);

	}

	@Test
	public void matchingRegExpExample() {

		String pattern = JsonMap.currentObjectPropertyPattern;
		String str = "$.a";
		Assert.assertTrue(str.matches(pattern));

		str = "$.nameLong";
		Assert.assertTrue(str.matches(pattern));
		str = "$.aaaaaaaaaa";
		Assert.assertTrue(str.matches(pattern));
		str = "$.assdsd";
		Assert.assertTrue(str.matches(pattern));
		str = "$.aasdas.dsadasd";
		Assert.assertFalse(str.matches(pattern));
		str = "$12";
		Assert.assertFalse(str.matches(pattern));
		str = "$.1232";
		Assert.assertTrue(str.matches(pattern));

		str = "$.aaa[1]";
		Assert.assertTrue(str.matches(pattern));

		str = "$.aaa[0]";
		Assert.assertTrue(str.matches(pattern));

		str = "$.aaa[100]";
		Assert.assertTrue(str.matches(pattern));

		pattern = JsonMap.hierarchyObjectPropertyPattern;
		str = "$.a";
		Assert.assertTrue(str.matches(pattern));

		str = "$.nameLong";
		Assert.assertTrue(str.matches(pattern));
		str = "$.aaaaaaaaaa";
		Assert.assertTrue(str.matches(pattern));
		str = "$.assdsd";
		Assert.assertTrue(str.matches(pattern));
		str = "$.aasdas.dsadasd";
		Assert.assertTrue(str.matches(pattern));
		str = "$12";
		Assert.assertFalse(str.matches(pattern));
		str = "$.1232";
		Assert.assertTrue(str.matches(pattern));

		str = "$.aasdas.dsadasd.sad.dsa.dsa.asdd";
		Assert.assertTrue(str.matches(pattern));

		str = "$.aaa.aaa[1]";
		Assert.assertTrue(str.matches(pattern));

		str = "$.aaa.aaa[0]";
		Assert.assertTrue(str.matches(pattern));

		str = "$.aaa.aaa[100]";
		Assert.assertTrue(str.matches(pattern));

	}
	

	@Test
	public void testUTF8() throws JsonParseException, IOException {

		String body = "{\"a\":\"€/ha\"}";
		String ref = "€/ha";
		JsonMap jm = JsonMapReaderLib.read(body, om);

		JsonMap bb = jm.getRootProperty("a");
		Assert.assertEquals(new JsonMap(ref), bb);
		System.err.println(bb);

		
	}

}
