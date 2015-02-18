package com.adler.dynamicJson;

import java.util.HashMap;
import java.util.Map;

import com.adler.dynamicJson.base.JsonMap;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

public class AnnotationData {

	private As as = null;
	private String property;
	private Id use = null;
	private Map<String, Class<?>> values = new HashMap<String, Class<?>>();

	public As getAs() {
		return as;
	}

	public void setAs(As as) {
		this.as = as;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public Id getUse() {
		return use;
	}

	public void setUse(Id use) {
		this.use = use;
	}

	public Map<String, Class<?>> getValues() {
		return values;
	}

	public void setValues(Map<String, Class<?>> values) {
		this.values = values;
	}

	public Class<?> getTargetClassFor(JsonMap map) {
		JsonMap prop = map.getRootProperty(property);
		if (prop ==null) throw new IllegalStateException("");
		for (String  s : values.keySet()) {
			if (prop.toContentString().equalsIgnoreCase(s)){
				return values.get(s);
			}
		}
		
		return null;
	}

	@Override
	public String toString() {
		return "AnnotationData [as=" + as + ", property=" + property + ", use="
				+ use + ", values=" + values + "]";
	}

	
}
