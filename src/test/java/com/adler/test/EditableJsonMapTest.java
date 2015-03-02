package com.adler.test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.adler.dynamicJson.EditableJsonMap;
import com.adler.dynamicJson.base.JsonMap;
import com.adler.dynamicJson.exception.InvalidPropertyName;
import com.adler.dynamicJson.lib.JsonMapReaderLib;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EditableJsonMapTest {

	ObjectMapper om = new ObjectMapper();

	private static final Logger LOGGER = Logger.getLogger(EditableJsonMapTest.class);

	@Test
	public void test() throws JsonParseException, IOException {

		String body = "{\"a\":\"b\"}";
		JsonMap jm = JsonMapReaderLib.read(body, om);
		
		EditableJsonMap ejm = new EditableJsonMap(jm);
		try {
			ejm.addProperty("test", new JsonMap("test"));
		}
		catch (InvalidPropertyName e) {
			e.printStackTrace();
			Assert.fail();

		}
		
		ejm.deleteProperty("$.test");
		
		Assert.assertTrue(ejm.getKeys().contains("a"));
		Assert.assertTrue(ejm.getKeys().size()==1);
		Assert.assertTrue(ejm.getValues().size()==1);
		Assert.assertFalse(ejm.getKeys().contains("test"));
		
		System.err.println(ejm.toFullString());
		
	}
	
	
	@Test
	public void testArray() throws JsonParseException, IOException, InvalidPropertyName {

		String body = "{\"a\":\"b\"}";
		JsonMap jm = JsonMapReaderLib.read(body, om);
		
		EditableJsonMap ejm = new EditableJsonMap(jm);
		
		List<JsonMap> contents = new ArrayList<JsonMap>();
		contents.add(new JsonMap("aa"));
		contents.add(new JsonMap("bb"));
		JsonMap array = JsonMap.JsonMapObjectArray(contents);
		ejm.addProperty("array", array);
		ejm.addPropertyToArray("array", new JsonMap("cc"));
		
		JsonMap ae = ejm.getProperty("$.array");
		Assert.assertEquals(3, ae.getValues().size());
		ejm.addPropertyToArray("array", new JsonMap("dd"));
		Assert.assertEquals(4, ae.getValues().size());
	}
	


	@Test
	public void testMissingArray() throws JsonParseException, IOException, InvalidPropertyName {

		String body = "{\"a\":\"b\"}";
		JsonMap jm = JsonMapReaderLib.read(body, om);
		
		EditableJsonMap ejm = new EditableJsonMap(jm);
		
		
		ejm.addPropertyToArray("array", new JsonMap("cc"));
		
		JsonMap ae = ejm.getProperty("$.array");
		Assert.assertEquals(1, ae.getValues().size());
		ejm.addPropertyToArray("array", new JsonMap("dd"));
		Assert.assertEquals(2, ae.getValues().size());
	}
	
}
