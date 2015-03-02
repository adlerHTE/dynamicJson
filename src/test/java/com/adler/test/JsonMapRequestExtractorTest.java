package com.adler.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpResponse;

import com.adler.dynamicJson.base.JsonMap;
import com.adler.dynamicJson.jackson.JsonMapRequestExtractor;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonMapRequestExtractorTest {

	JsonMapRequestExtractor jExtractor = new JsonMapRequestExtractor(new ObjectMapper());
	ClientHttpResponse response;

	@Test
	public void basicTest() throws IOException {

		String body = "{\"a\":\"b\"}";

		response = new MockClientHttpResponse(new ByteArrayInputStream(
				body.getBytes()), org.springframework.http.HttpStatus.OK);
		JsonMap res = jExtractor.extractData(response);

		Assert.assertEquals(0, res.getContent().size());
		Assert.assertEquals(1, res.getValues().size());
		Assert.assertEquals(1, res.getKeys().size());
		Assert.assertEquals(new JsonMap("b"), res.getValues().get(0));
		Assert.assertEquals("a", res.getKeys().get(0));

	}
	
	
	@Test
	public void basicTestNull() throws IOException {

		String body = "{\"a\":null}";

		response = new MockClientHttpResponse(new ByteArrayInputStream(
				body.getBytes()), org.springframework.http.HttpStatus.OK);
		JsonMap res = jExtractor.extractData(response);

		Assert.assertEquals(0, res.getContent().size());
		Assert.assertEquals(1, res.getValues().size());
		Assert.assertEquals(1, res.getKeys().size());
		Assert.assertEquals(JsonMap.NULL, res.getValues().get(0));
		Assert.assertEquals("a", res.getKeys().get(0));

	}
	
	
	
	@Test
	public void basicTestNumber() throws IOException {

		String body = "{\"a\":3333.33333333333999,\"b\":3333.34}";

		response = new MockClientHttpResponse(new ByteArrayInputStream(
				body.getBytes()), org.springframework.http.HttpStatus.OK);
		JsonMap res = jExtractor.extractData(response);

		Assert.assertEquals(0, res.getContent().size());
		Assert.assertEquals(2, res.getValues().size());
		Assert.assertEquals(2, res.getKeys().size());
		Assert.assertEquals( new BigDecimal("3333.34"), res.getValues().get(1).getValue());
		Assert.assertEquals( new BigDecimal("3333.33333333333999"), 
				res.getValues().get(0).getValue());

	}


	@Test
	public void couchTest1() throws IOException {

		String body = "{\"total_rows\":0,\"offset\":0,\"rows\":[]}";

		response = new MockClientHttpResponse(new ByteArrayInputStream(
				body.getBytes()), org.springframework.http.HttpStatus.OK);
		JsonMap res = jExtractor.extractData(response);

		Assert.assertEquals(0, res.getContent().size());
		Assert.assertEquals(3, res.getValues().size());
		Assert.assertEquals(3, res.getKeys().size());
		Assert.assertEquals(new BigDecimal("0"), res.getValues().get(0).getValue());
		Assert.assertEquals(new BigDecimal("0"), res.getValues().get(1).getValue());
		Assert.assertEquals(JsonMap.TYPE_SIMPLE_ARRAY, res.getValues().get(2)
				.getType());

	}

	@Test
	public void couchTest2() throws IOException {

		String body = "{\"total_rows\":793,\"offset\":514,\"rows\":[\r\n"
				+ "	{\"id\":\"_design/test\",\"key\":\"_design/test\",\"value\":{\"rev\":\"1-ff0aae6fd652f5e8165ec76e4770dd2a\"},\"doc\":{\"_id\":\"_design/test\",\"_rev\":\"1-ff0aae6fd652f5e8165ec76e4770dd2a\",\"language\":\"javascript\",\"views\":{\"test\":{\"map\":\"function(doc) {\\n  if (doc.codFiscale)\\n     emit(doc._id, doc);\\n}\"}}}},\r\n"
				+ "	{\"id\":\"_design/test2\",\"key\":\"_design/test2\",\"value\":{\"rev\":\"6-3b01722e55b0ed07aa15f625c19c4081\"},\"doc\":{\"_id\":\"_design/test2\",\"_rev\":\"6-3b01722e55b0ed07aa15f625c19c4081\",\"language\":\"javascript\",\"views\":{\"test2\":{\"map\":\"function(doc) {\\n  if (doc.release && doc.totaleRecord){\\n     emit(doc._id, doc);\\n}\\n\\n}\"}}}}\r\n"
				+ "	]}";

		response = new MockClientHttpResponse(new ByteArrayInputStream(
				body.getBytes()), org.springframework.http.HttpStatus.OK);
		JsonMap res = jExtractor.extractData(response);

		Assert.assertEquals(0, res.getContent().size());
		Assert.assertEquals(3, res.getValues().size());
		Assert.assertEquals(3, res.getKeys().size());
		
		JsonMap rows = res.getValues().get(2);
		JsonMap row1 = rows.getValues().get(0);
		JsonMap row2 = rows.getValues().get(1);
		System.err.println("R1="+row1);
		System.err.println("R2="+row2);
		
		Assert.assertEquals(4, row1.getKeys().size());
		Assert.assertEquals(4, row1.getValues().size());
		
		Assert.assertEquals("rev",row1.getValues().get(2).getKeys().get(0));
		Assert.assertEquals("rev",row2.getValues().get(2).getKeys().get(0));
		// add others

		Assert.assertEquals("_design/test",row1.getValues().get(3).getValues().get(0).getContent().get(0));
	}

	
	@Test
	public void testArray() throws IOException {
		String body = "{\"total_rows\":0,\"offset\":0,\"rows\":[\"aa\",\"bb\",\"cc\",\"dd\"]}";

		response = new MockClientHttpResponse(new ByteArrayInputStream(
				body.getBytes()), org.springframework.http.HttpStatus.OK);
		JsonMap res = jExtractor.extractData(response);

		Assert.assertEquals(0, res.getContent().size());
		Assert.assertEquals(3, res.getValues().size());
		Assert.assertEquals(3, res.getKeys().size());
		Assert.assertEquals(new BigDecimal("0"), res.getValues().get(0).getValue());
		Assert.assertEquals(new BigDecimal("0"), res.getValues().get(1).getValue());
		Assert.assertEquals(JsonMap.TYPE_SIMPLE_ARRAY, res.getValues().get(2)
				.getType());
		System.err.println(res.getValues().get(2));
		Assert.assertEquals("aa", res.getValues().get(2).getContent().get(0));

	}
	
	
	@Test
	public void testArray2() throws IOException {
		String body = "[\"aa\",\"bb\",\"cc\",\"dd\"]";

		response = new MockClientHttpResponse(new ByteArrayInputStream(
				body.getBytes()), org.springframework.http.HttpStatus.OK);
		JsonMap res = jExtractor.extractData(response);

		
		System.err.println(res);
		Assert.assertEquals("aa", res.getContent().get(0));
		Assert.assertEquals("bb", res.getContent().get(1));
		Assert.assertEquals("cc", res.getContent().get(2));
		Assert.assertEquals("dd", res.getContent().get(3));

	}
	
	
	@Test
	public void simpleText() throws IOException {
		String body = "[\"goofy\"]";

		response = new MockClientHttpResponse(new ByteArrayInputStream(
				body.getBytes()), org.springframework.http.HttpStatus.OK);
		JsonMap res = jExtractor.extractData(response);

		
		System.err.println(res);
		Assert.assertEquals("goofy", res.getContent().get(0));
		
	}
}
