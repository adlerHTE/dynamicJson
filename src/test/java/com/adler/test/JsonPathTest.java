package com.adler.test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.adler.dynamicJson.base.JsonMap;
import com.adler.dynamicJson.jpath.JPath;
import com.adler.dynamicJson.lib.JsonMapReaderLib;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonPathTest {

	ObjectMapper om = new ObjectMapper();
	
	String store = "{ \"store\": {\r\n" + 
			"    \"book\": [ \r\n" + 
			"      { \"category\": \"reference\",\r\n" + 
			"        \"author\": \"Nigel Rees\",\r\n" + 
			"        \"title\": \"Sayings of the Century\",\r\n" + 
			"        \"price\": 8.95\r\n" + 
			"      },\r\n" + 
			"      { \"category\": \"fiction\",\r\n" + 
			"        \"author\": \"Evelyn Waugh\",\r\n" + 
			"        \"title\": \"Sword of Honour\",\r\n" + 
			"        \"price\": 12.99\r\n" + 
			"      },\r\n" + 
			"      { \"category\": \"fiction\",\r\n" + 
			"        \"author\": \"Herman Melville\",\r\n" + 
			"        \"title\": \"Moby Dick\",\r\n" + 
			"        \"isbn\": \"0-553-21311-3\",\r\n" + 
			"        \"price\": 8.99\r\n" + 
			"      },\r\n" + 
			"      { \"category\": \"fiction\",\r\n" + 
			"        \"author\": \"J. R. R. Tolkien\",\r\n" + 
			"        \"title\": \"The Lord of the Rings\",\r\n" + 
			"        \"isbn\": \"0-395-19395-8\",\r\n" + 
			"        \"price\": 22.99\r\n" + 
			"      }\r\n" + 
			"    ],\r\n" + 
			"    \"bicycle\": {\r\n" + 
			"      \"color\": \"red\",\r\n" + 
			"      \"price\": 19.95\r\n" + 
			"    }\r\n" + 
			"  }\r\n" + 
			"}";

	private static final Logger LOGGER = Logger.getLogger(JsonPathTest.class);

	@Test
	public void test1() throws JsonParseException, IOException {

		
		JsonMap jm = JsonMapReaderLib.read(store, om);
				
		JPath p = new JPath("$.store.book[*].author");
		p.printTokens();
		
		JsonMap res = p.apply(jm);
		System.err.println(res.toFullString());
		Assert.assertEquals(4, res.getValues().size());
		Assert.assertEquals("Nigel Rees", res.getValues().get(0).toContentString());
		Assert.assertEquals("Evelyn Waugh", res.getValues().get(1).toContentString());
		Assert.assertEquals("Herman Melville", res.getValues().get(2).toContentString());
		Assert.assertEquals("J. R. R. Tolkien", res.getValues().get(3).toContentString());
		
		
		}
	
	
	@Test
	public void test2() throws JsonParseException, IOException {

		
		JsonMap jm = JsonMapReaderLib.read(store, om);
		JPath p = new JPath("$..author");
		
		JsonMap res = p.apply(jm);
		System.err.println(res.toFullString());
		Assert.assertEquals(4, res.getValues().size());
		Assert.assertEquals("Nigel Rees", res.getValues().get(0).toContentString());
		Assert.assertEquals("Evelyn Waugh", res.getValues().get(1).toContentString());
		Assert.assertEquals("Herman Melville", res.getValues().get(2).toContentString());
		Assert.assertEquals("J. R. R. Tolkien", res.getValues().get(3).toContentString());
		
		
		}
	
	
	@Test
	public void test3() throws JsonParseException, IOException {

		
		JsonMap jm = JsonMapReaderLib.read(store, om);
		JPath p = new JPath("$.store.*");
		
		JsonMap res = p.apply(jm);
		System.err.println(res.toFullString());
		Assert.assertEquals(2, res.getValues().size());
		//res must contain 4 books [in 1 array] and a bycicle
		
		Assert.assertTrue(res.getValues().contains(jm.getProperty("$.store.bicycle")));
		Assert.assertTrue(res.getValues().contains(jm.getProperty("$.store.book")));
		
		
		
		}
	
	
	@Test
	public void test4() throws JsonParseException, IOException {

		
		JsonMap jm = JsonMapReaderLib.read(store, om);
		JPath p = new JPath("$.store..price");
		
		JsonMap res = p.apply(jm);
		System.err.println(res.toFullString());
		Assert.assertEquals(5, res.getValues().size());
		
		Assert.assertTrue(res.getValues().contains(new JsonMap(new BigDecimal("8.95"))));
		Assert.assertTrue(res.getValues().contains(new JsonMap(new BigDecimal("12.99"))));
		Assert.assertTrue(res.getValues().contains(new JsonMap(new BigDecimal("8.99"))));
		Assert.assertTrue(res.getValues().contains(new JsonMap(new BigDecimal("22.99"))));
		Assert.assertTrue(res.getValues().contains(new JsonMap(new BigDecimal("19.95"))));
		
		
		
		}

	
	
	@Test
	public void test5() throws JsonParseException, IOException {

		
		JsonMap jm = JsonMapReaderLib.read(store, om);
		JPath p = new JPath("$..book[2]");
		
		JsonMap res = p.apply(jm);
		
		
		Assert.assertEquals(1, res.getValues().size());
		 String sRes = "[{ \"category\": \"fiction\",\r\n" + 
					"        \"author\": \"Herman Melville\",\r\n" + 
					"        \"title\": \"Moby Dick\",\r\n" + 
					"        \"isbn\": \"0-553-21311-3\",\r\n" + 
					"        \"price\": 8.99\r\n" + 
					"      }]\r\n";
		 JsonMap reference = JsonMapReaderLib.read(sRes, om);
		System.err.println("RES="+res.toFullString());
		System.err.println("REF="+reference.toFullString());
		 Assert.assertEquals(reference, res);
		
		
		}
	
	
//	@Test
//	public void testRegexp(){
//		String REGEXP= "\\.|\\[";
//		String[] tt= "$..test.tes2[(@.aaa.bbbb.ccc)]".split(REGEXP);
//		System.err.println(Arrays.toString(tt));
//		int i=0;
//		for (String string : tt) {
//			System.err.println((i++)+" "+string);	
//		}
//		Assert.assertEquals(4,tt.length);
//		tt= "#.%^.aerds.dsa[@.43]".split(REGEXP);
//		System.err.println(Arrays.toString(tt));
////		Assert.assertEquals(4,tt.length);
//		
//		tt= "$..book[2]".split(REGEXP);
//		System.err.println(Arrays.toString(tt));
////		Assert.assertEquals("$", tt[0]);
//		
//	}
//	
	
	
	@Test
	public void test6() throws JsonParseException, IOException {

		String sRes = "[{ \"category\": \"fiction\",\r\n" + 
				"        \"author\": \"J. R. R. Tolkien\",\r\n" + 
				"        \"title\": \"The Lord of the Rings\",\r\n" + 
				"        \"isbn\": \"0-395-19395-8\",\r\n" + 
				"        \"price\": 22.99\r\n" + 
				"      }]\r\n"; 
		JsonMap reference = JsonMapReaderLib.read(sRes, om);
		
		JsonMap jm = JsonMapReaderLib.read(store, om);
		JPath p = new JPath("$..book[(@.length-1)]"); //$..book[-1:]
		Assert.assertTrue(4==p.getTokens().size());
		p.printTokens();
		JsonMap res = p.apply(jm);
		
		
		Assert.assertEquals(1, res.getValues().size());
		System.err.println("RES="+res.toFullString());
		System.err.println("REF="+reference.toFullString());
		
		
		 Assert.assertEquals(reference, res);
		 
		 
		p = new JPath("$..book[-1:]"); 
		res = p.apply(jm);
		Assert.assertEquals(1, res.getValues().size());
		System.err.println("RES="+res.toFullString());
		System.err.println("REF="+reference.toFullString());
		 Assert.assertEquals(reference, res);
		
		
		}
	
	@Test
	public void test7() throws JsonParseException, IOException {

		String sRes = "[ \r\n" + 
		    			"      { \"category\": \"reference\",\r\n" + 
		    			"        \"author\": \"Nigel Rees\",\r\n" + 
		    			"        \"title\": \"Sayings of the Century\",\r\n" + 
		    			"        \"price\": 8.95\r\n" + 
		    			"      },\r\n" + 
		    			"      { \"category\": \"fiction\",\r\n" + 
		    			"        \"author\": \"Evelyn Waugh\",\r\n" + 
		    			"        \"title\": \"Sword of Honour\",\r\n" + 
		    			"        \"price\": 12.99\r\n" + 
		    			"      }]";
		JsonMap reference = JsonMapReaderLib.read(sRes, om);
		
		JsonMap jm = JsonMapReaderLib.read(store, om);
		JPath p = new JPath("$..book[0,1]"); //$..book[:2]
		Assert.assertTrue(4==p.getTokens().size());
		p.printTokens();
		JsonMap res = p.apply(jm);
		
		
		Assert.assertEquals(2, res.getValues().size());
		System.err.println("RES="+res.toFullString());
		System.err.println("REF="+reference.toFullString());
		
		
		 Assert.assertEquals(reference, res);
		 
		 
		p = new JPath("$..book[:2]"); 
		res = p.apply(jm);
		Assert.assertEquals(2, res.getValues().size());
		System.err.println("RES="+res.toFullString());
		System.err.println("REF="+reference.toFullString());
		 Assert.assertEquals(reference, res);
		
		
		}
	
	

	@Test
	public void test8() throws JsonParseException, IOException {

		String sRes = "[ \r\n" + 
				"      { \"category\": \"fiction\",\r\n" + 
				"        \"author\": \"Herman Melville\",\r\n" + 
				"        \"title\": \"Moby Dick\",\r\n" + 
				"        \"isbn\": \"0-553-21311-3\",\r\n" + 
				"        \"price\": 8.99\r\n" + 
				"      },\r\n" + 
				"      { \"category\": \"fiction\",\r\n" + 
				"        \"author\": \"J. R. R. Tolkien\",\r\n" + 
				"        \"title\": \"The Lord of the Rings\",\r\n" + 
				"        \"isbn\": \"0-395-19395-8\",\r\n" + 
				"        \"price\": 22.99\r\n" + 
				"      }\r\n" + 
		    			"      ]";
		JsonMap reference = JsonMapReaderLib.read(sRes, om);
		
		JsonMap jm = JsonMapReaderLib.read(store, om);
		JPath p = new JPath("$..book[?(@.isbn)]"); 
		Assert.assertTrue(4==p.getTokens().size());
		p.printTokens();
		JsonMap res = p.apply(jm);
		
		
		Assert.assertEquals(2, res.getValues().size());
		System.err.println("RES="+res.toFullString());
		System.err.println("REF="+reference.toFullString());
		
		
		 Assert.assertEquals(reference, res);
		 
		 
		
		
		}
	
	

	@Test
	public void test9() throws JsonParseException, IOException {

		String sRes = "[ \r\n" 
				+ "{ \"category\": \"reference\",\r\n" + 
				"        \"author\": \"Nigel Rees\",\r\n" + 
				"        \"title\": \"Sayings of the Century\",\r\n" + 
				"        \"price\": 8.95\r\n" + 
				"      },\r\n" +
				"      { \"category\": \"fiction\",\r\n" + 
				"        \"author\": \"Herman Melville\",\r\n" + 
				"        \"title\": \"Moby Dick\",\r\n" + 
				"        \"isbn\": \"0-553-21311-3\",\r\n" + 
				"        \"price\": 8.99\r\n" + 
				"      }\r\n" +" ]";
		JsonMap reference = JsonMapReaderLib.read(sRes, om);
		
		JsonMap jm = JsonMapReaderLib.read(store, om);
		JPath p = new JPath("$..book[?(@.price<10)]"); 
		Assert.assertTrue(4==p.getTokens().size());
		p.printTokens();
		JsonMap res = p.apply(jm);
		
		
		Assert.assertEquals(2, res.getValues().size());
		System.err.println("RES="+res.toFullString());
		System.err.println("REF="+reference.toFullString());
		
		
		 Assert.assertEquals(reference, res);
		 p = new JPath("$..book[?(@.price<10)].title"); 
		 res = p.apply(jm);
		System.err.println(res.getValues().get(0));
		System.err.println(res.getValues().get(1));
		
		
		}
	
	
	
	@Test
	public void test10() throws JsonParseException, IOException {
		
		JsonMap jm = JsonMapReaderLib.read(store, om);
		JPath p = new JPath("$..*");
		
		JsonMap res = p.apply(jm);
		
		
		Assert.assertEquals(27, res.getValues().size());
		
		System.err.println("RES="+res.toFullString());
		
		System.err.println("CONTAINS EVERY SINGLE ");
		
		}
	
	

	
	@Test
	public void test11() throws JsonParseException, IOException {
		
		JsonMap jm = JsonMapReaderLib.read(store, om);
		JPath p = new JPath("$..book[?(@.title.length>=15)].title");
		
		JsonMap res = p.apply(jm);
		
		Assert.assertEquals(3, res.getValues().size());

		
		System.err.println("RES="+res.toFullString());
		
		
		
		}
	

}
