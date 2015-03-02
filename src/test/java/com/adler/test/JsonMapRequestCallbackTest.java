package com.adler.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.mock.http.client.MockClientHttpResponse;

import com.adler.dynamicJson.EditableJsonMap;
import com.adler.dynamicJson.base.JsonMap;
import com.adler.dynamicJson.jackson.JsonMapRequestCallbak;
import com.adler.dynamicJson.jackson.JsonMapRequestExtractor;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonMapRequestCallbackTest {

	JsonMapRequestExtractor jExtractor = new JsonMapRequestExtractor(new ObjectMapper());
	ClientHttpResponse response;
	JsonMapRequestCallbak jCallback;
	MockClientHttpRequest request;
	
	@Test
	public void basicTest() throws IOException {

		List<String> k = new ArrayList<String>();
		k.add("a");
		List<JsonMap> v = new ArrayList<JsonMap>();
		v.add(new JsonMap("b"));
		JsonMap jmap = new JsonMap(k, v);
		
		jCallback = new JsonMapRequestCallbak(jmap);
		
		request =  new MockClientHttpRequest();
		jCallback.doWithRequest(request);
		
		String body = request.getBodyAsString();
		String bodyRef = "{\"a\":\"b\"}";
		System.err.println("body="+body);
		System.err.println("refb="+bodyRef);
		Assert.assertEquals(bodyRef, body);
	}

	@Test
	public void couchTest1() throws IOException {

		String body = "{\"total_rows\":793,\"offset\":514,\"rows\":["
				+ "{\"id\":\"_design/test\",\"key\":\"_design/test\",\"value\":{\"rev\":\"1-ff0aae6fd652f5e8165ec76e4770dd2a\"},\"doc\":{\"_id\":\"_design/test\",\"_rev\":\"1-ff0aae6fd652f5e8165ec76e4770dd2a\",\"language\":\"javascript\",\"views\":{\"test\":{\"map\":\"function(doc) {\\n  if (doc.code)\\n     emit(doc._id, doc);\\n}\"}}}},"
				+ "{\"id\":\"_design/test2\",\"key\":\"_design/test2\",\"value\":{\"rev\":\"6-3b01722e55b0ed07aa15f625c19c4081\"},\"doc\":{\"_id\":\"_design/test2\",\"_rev\":\"6-3b01722e55b0ed07aa15f625c19c4081\",\"language\":\"javascript\",\"views\":{\"test2\":{\"map\":\"function(doc) {\\n  if (doc.release && doc.totals){\\n     emit(doc._id, doc);\\n}\\n\\n}\"}}}}"
				+ "]}";

		response = new MockClientHttpResponse(new ByteArrayInputStream(
				body.getBytes()), org.springframework.http.HttpStatus.OK);
		JsonMap res = jExtractor.extractData(response);
		
		jCallback = new JsonMapRequestCallbak(res);
		
		request =  new MockClientHttpRequest();
		jCallback.doWithRequest(request);
		
		String bodyRes = request.getBodyAsString();
		System.err.println("refb="+body);
		System.err.println("body="+bodyRes);
		Assert.assertEquals(body, bodyRes);
		
		
	}
	
	
	
	
	@Test
	public void couchWithAttachmentTest() throws IOException{
		String body="{\r\n" + 
				"   \"_id\": \"04a6dfa0-a1ac-4a83-b8b9-c8c07c401823\",\r\n" + 
				"   \"_rev\": \"2-1ca22fc87558521a1f62a14da095648a\",\r\n" + 
				"   \"id\": \"841211001536541\",\r\n" + 
				"   \"scandata\": {\r\n" + 
				"       \"id\": \"841211001536541\",\r\n" + 
				"       \"date\": \"05/11/2014 12:28:59\"\r\n" +
				"   },\r\n" + 
				"   \"_attachments\": {\r\n" + 
				"       \"image0012.tif\": {\r\n" + 
				"           \"content_type\": \"image/jpeg\",\r\n" + 
				"           \"revpos\": 2,\r\n" + 
				"           \"digest\": \"md5-Q8BIAONIlbiRRt9RGmPCKw==\",\r\n" + 
				"           \"length\": 1179703,\r\n" + 
				"           \"stub\": true\r\n" + 
				"       }\r\n" + 
				"   }\r\n" + 
				"}".replaceAll("\\n", "").replaceAll("\\r", "");
		
		
		response = new MockClientHttpResponse(new ByteArrayInputStream(
				body.getBytes()), org.springframework.http.HttpStatus.OK);
		JsonMap res = jExtractor.extractData(response);
		EditableJsonMap ejm = new EditableJsonMap(res);
		JsonMap mm =ejm.getProperty("$._attachments");
		
		
		jCallback = new JsonMapRequestCallbak(res);
		
		request =  new MockClientHttpRequest();
		jCallback.doWithRequest(request);
		
		String bodyRes = request.getBodyAsString();
		
		System.err.println("refb="+body);
		System.err.println("body="+bodyRes);
		
//		Assert.assertEquals(body, bodyRes);
		
		Assert.assertTrue(bodyRes.contains("\"stub\":true"));
	}
	
	
	
	@Test
	public void arrayTest1() throws IOException {

		String body = "[\"aa\",\"bb\"]";

		response = new MockClientHttpResponse(new ByteArrayInputStream(
				body.getBytes()), org.springframework.http.HttpStatus.OK);
		JsonMap res = jExtractor.extractData(response);
		
		jCallback = new JsonMapRequestCallbak(res);
		
		request =  new MockClientHttpRequest();
		jCallback.doWithRequest(request);
		
		String bodyRes = request.getBodyAsString();
		System.err.println("refb="+body);
		System.err.println("body="+bodyRes);
		Assert.assertEquals(body, bodyRes);
		
		
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
		Assert.assertEquals(new JsonMap(BigDecimal.ZERO), res.getValues().get(0));
		Assert.assertEquals(new JsonMap(BigDecimal.ZERO), res.getValues().get(1));
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
