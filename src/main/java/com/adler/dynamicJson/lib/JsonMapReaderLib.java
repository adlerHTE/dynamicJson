package com.adler.dynamicJson.lib;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.input.BOMInputStream;

import com.adler.dynamicJson.base.JsonMap;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonMapReaderLib {

	// private static final Logger LOGGER = Logger
	// .getLogger(JsonMapReaderLib.class);
	
//	private static MathContext mc = new MathContext(2,RoundingMode.HALF_DOWN);

	public static JsonMap read(String body, ObjectMapper objectMapper)
			throws JsonParseException, IOException {
		JsonParser jsonParser = objectMapper.getFactory()
				.createJsonParser(body);
		return parse(objectMapper, jsonParser);
	
	}
	
	public static JsonMap read(InputStream input, ObjectMapper objectMapper)throws JsonParseException, IOException  {
		BOMInputStream bomIn = new BOMInputStream(input, false);
		JsonParser jsonParser = objectMapper.getFactory()
				.createJsonParser(bomIn);
		return parse(objectMapper,jsonParser);
	}


	private static JsonMap readObject(JsonParser jParser) throws IOException {
		List<String> names = new ArrayList<String>();
		List<JsonMap> values = new ArrayList<JsonMap>();
		while (jParser.nextToken() != JsonToken.END_OBJECT) {
			if (jParser.getCurrentToken() == JsonToken.FIELD_NAME) {
				names.add(jParser.getCurrentName());
			}

			if (jParser.getCurrentToken() == JsonToken.VALUE_FALSE) {
				values.add(JsonMap.FALSE);
			}
			if (jParser.getCurrentToken() == JsonToken.VALUE_TRUE) {
				values.add(JsonMap.TRUE);
			}

			if (jParser.getCurrentToken() == JsonToken.VALUE_STRING) {
				String a = jParser.getValueAsString();
				
				try {
					if (jParser.getCurrentName().equalsIgnoreCase("_id")) throw new NumberFormatException();
					if ((a.trim().length()>0)&&(a.trim().charAt(0)=='0')){
						throw new NumberFormatException("losing 0 info..");
					}
					BigDecimal bd = new BigDecimal(a);
					bd.intValueExact();
					values.add(new JsonMap(bd));
				}
				catch (NumberFormatException | ArithmeticException nfe) {
					values.add(new JsonMap(a));
				}

				// values.add(new JsonMap(jParser.getValueAsString()));
			}
			if (jParser.getCurrentToken() == JsonToken.VALUE_NULL) {
				values.add(JsonMap.NULL);
			}
			if (jParser.getCurrentToken() == JsonToken.VALUE_NUMBER_INT
					|| jParser.getCurrentToken() == JsonToken.VALUE_NUMBER_FLOAT) {
				//this is the only way to have the same result new BigDecimal(0.35)===>0.3499999999999999777955
				values.add(new JsonMap(jParser.getDecimalValue()));
			}
			if (jParser.getCurrentToken() == JsonToken.START_ARRAY) {
				values.add(readArray(jParser));

			}
			if (jParser.getCurrentToken() == JsonToken.START_OBJECT) {
				values.add(readObject(jParser));

			}
		}
		return new JsonMap(names, values);
	}
	private static JsonMap readArray(JsonParser jParser) throws IOException {
		List<String> content = new ArrayList<String>();
		List<JsonMap> values = new ArrayList<JsonMap>();

		boolean onlyStrings = true;

		while (jParser.nextToken() != JsonToken.END_ARRAY) {

			if (jParser.getCurrentToken() == JsonToken.VALUE_STRING
					|| jParser.getCurrentToken() == JsonToken.VALUE_NUMBER_INT) {

				if (onlyStrings) {
					content.add(jParser.getValueAsString());
				} else {
					values.add(new JsonMap(jParser.getValueAsString()));
				}

			}

			if (jParser.getCurrentToken() == JsonToken.START_OBJECT) {
				values.add(readObject(jParser));
				onlyStrings = false;

			}
		}

		JsonMap res = null;

		if (values.size() > 0) {
			res = JsonMap.JsonMapObjectArray(values);
		} else {
			res = JsonMap.JsonMapStringArray(content);
		}
		return res;

	}

	
	private static JsonMap parse(ObjectMapper objectMapper,
			JsonParser jsonParser) throws JsonParseException, IOException {
		
		JsonMap res = null;
		jsonParser.nextToken();

		if (jsonParser.getCurrentToken() == JsonToken.START_OBJECT) {
			res = readObject(jsonParser);
		}

		if (jsonParser.getCurrentToken() == JsonToken.START_ARRAY) {
			res = readArray(jsonParser);
		}
		if (jsonParser.getCurrentToken() == JsonToken.VALUE_STRING
				|| jsonParser.getCurrentToken() == JsonToken.VALUE_NUMBER_INT) {
			String a = jsonParser.getValueAsString();
			try {
				if ((a.trim().length()>0)&&(a.trim().charAt(0)=='0')){
					throw new NumberFormatException("losing 0 info..");
				}
				BigDecimal bd = new BigDecimal(a);
				bd.intValueExact();
				res = new JsonMap(bd);
			}
			catch (NumberFormatException | ArithmeticException nfe) {
				res = new JsonMap(a);
			}

		}
		if (res == null) { throw new IOException("No json data to be parsed!!!"); }

		jsonParser.close();
		return res;
	}

}
