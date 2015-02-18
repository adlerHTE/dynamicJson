package com.adler.dynamicJson.jackson;

import java.io.IOException;
import java.io.PrintWriter;

import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.web.client.RequestCallback;

import com.adler.dynamicJson.base.JsonMap;
import com.adler.dynamicJson.lib.JsonMapPrinterLib;

public class JsonMapRequestCallbak implements RequestCallback {

	private JsonMap jsonMap;
	
	private static final Logger LOGGER = Logger
			.getLogger(JsonMapRequestCallbak.class);
	
	public JsonMapRequestCallbak(JsonMap toBePosted) {
		jsonMap = toBePosted;
	}

	@Override
	public void doWithRequest(ClientHttpRequest request) throws IOException {
		request.getHeaders().add("Accept:", MediaType.APPLICATION_JSON.toString());
		LOGGER.debug(jsonMap);
		PrintWriter pw = new PrintWriter(request.getBody());
		JsonMapPrinterLib.buildJson(jsonMap, pw);
		pw.flush();
		LOGGER.info("JSON="+request.getBody().toString().substring(0,Math.min(20, request.getBody().toString().length())));
		}



}
