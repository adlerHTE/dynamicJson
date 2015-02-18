package com.adler.dynamicJson.jackson;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseExtractor;

import com.adler.dynamicJson.base.JsonMap;
import com.adler.dynamicJson.lib.JsonMapReaderLib;
import com.fasterxml.jackson.databind.ObjectMapper;



public class JsonMapRequestExtractor implements ResponseExtractor<JsonMap> {


	private static final Logger LOGGER = Logger
			.getLogger(JsonMapRequestExtractor.class);

	ObjectMapper objectMapper = null;
	
	
	public JsonMapRequestExtractor(ObjectMapper objetMapper) {
		this.setObjectMapper(objetMapper);
	}

	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public JsonMap extractData(ClientHttpResponse response) throws IOException {

		LOGGER.debug("Handling response as a generic JSON");
		JsonMap res = JsonMapReaderLib.read(response.getBody(),objectMapper);
		LOGGER.debug("res="+res);
		return res;
	}

	
}
