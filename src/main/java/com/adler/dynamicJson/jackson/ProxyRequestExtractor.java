package com.adler.dynamicJson.jackson;

import java.io.IOException;
import java.io.Writer;

import org.apache.commons.io.IOUtils;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseExtractor;

public class ProxyRequestExtractor implements ResponseExtractor<Object> {

	Writer writer;
	public ProxyRequestExtractor(Writer pw) {
		writer = pw;
	}

	@Override
	public Object extractData(ClientHttpResponse response) throws IOException {
		IOUtils.copy(response.getBody(), writer);
		writer.flush();
		writer.close();
		return null;
	}

}
