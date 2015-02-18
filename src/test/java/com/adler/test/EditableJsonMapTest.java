package com.adler.test;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.adler.dynamicJson.EditableJsonMap;
import com.adler.dynamicJson.base.JsonMap;
import com.adler.dynamicJson.exception.InvalidPropertyName;
import com.adler.dynamicJson.lib.JsonMapPrinterLib;
import com.adler.dynamicJson.lib.JsonMapReaderLib;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EditableJsonMapTest {

	ObjectMapper om = new ObjectMapper();

	private static final Logger LOGGER = Logger.getLogger(EditableJsonMapTest.class);

	@Test
	public void test() throws JsonParseException, IOException, InvalidPropertyName {

		String body = "{\"a\":\"b\"}";
		JsonMap jm = JsonMapReaderLib.read(body, om);
		EditableJsonMap ejm = new EditableJsonMap(jm);
		
		ejm.addProperty("c", new JsonMap("Hello World!"));
		
		StringWriter sw = new StringWriter();
		JsonMapPrinterLib.buildJson(ejm, sw);
		
		String res = "{\"a\":\"b\",\"c\":\"Hello World!\"}";
		Assert.assertEquals(res, sw.toString());
		
	}
	
	
	

}
