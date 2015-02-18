package com.adler.dynamicJson.lib;

import java.io.IOException;
import java.io.Writer;

import com.adler.dynamicJson.base.JsonMap;
import com.fasterxml.jackson.core.JsonToken;

public class JsonMapPrinterLib {

	
//	private static final Logger LOGGER = Logger
//			.getLogger(JsonMapPrinterLib.class);
	
	
	public static void buildJson(JsonMap jm, Writer out) throws IOException{
		buildJson(jm,out,true);
	}
	public static void buildJson(JsonMap jm, Writer out,boolean addDoubleQuote) throws IOException {
		switch (jm.getType()){
			case JsonMap.TYPE_TRUE:
				out.append("true");
				break;
			case JsonMap.TYPE_FALSE:
				out.append("false");
				break;
			case JsonMap.TYPE_SIMPLE_STR:
				out.append(printValue(jm.getContent().get(0),addDoubleQuote));
				break;
			case JsonMap.TYPE_SIMPLE_DEC:
				out.append(jm.getValue()!=null?jm.getValue().toPlainString():"null");
				break;
			case JsonMap.TYPE_SIMPLE_ARRAY:
			case JsonMap.TYPE_OBJECT_ARRAY:
				buildArray(jm,out,addDoubleQuote);
				break;
			case JsonMap.TYPE_COMPLEX:
				buildMap(jm,out,addDoubleQuote);
				break;
			case JsonMap.TYPE_NULL:
				out.append("null");
				break;
		}
		
		
	}

	private static CharSequence printValue(String s,boolean addDoubleQuote) {
		if (s.equalsIgnoreCase(JsonToken.VALUE_FALSE.asString()))
			return "false";
		if (s.equalsIgnoreCase(JsonToken.VALUE_TRUE.asString()))
			return "true";
		if (s.equalsIgnoreCase(JsonToken.VALUE_NULL.asString()))
			return "null";
		try{
			Integer i = Integer.parseInt(s);
			return ""+i;
		}catch(Exception e){}
		
		return doubleQuote(s,addDoubleQuote);
	}

	private static void buildMap(JsonMap jm, Writer out,boolean addDoubleQuote) throws IOException {
		if (jm.getKeys().size()<=0) {
			out.append("null");
			return;
		}
		out.append(JsonToken.START_OBJECT.asString());
		for(int i=0; i<jm.getKeys().size();i++){
			if (i>0) out.append(",");
			out.append(doubleQuote(jm.getKeys().get(i),addDoubleQuote));
			out.append(":");
			buildJson(jm.getValues().get(i),out,addDoubleQuote);
			
		}
		
		out.write(JsonToken.END_OBJECT.asString());
	}

	private static void buildArray(JsonMap jm, Writer out,boolean addDoubleQuote) throws IOException {
		out.append(JsonToken.START_ARRAY.asString().trim());
		if (jm.getType() == JsonMap.TYPE_SIMPLE_ARRAY){
			for(int i=0; i<jm.getContent().size();i++){
				if (i>0) out.append(",");
				out.append(doubleQuote(jm.getContent().get(i),addDoubleQuote));
				
			}			
		}
		if (jm.getType() == JsonMap.TYPE_OBJECT_ARRAY){
			for(int i=0; i<jm.getValues().size();i++){
				if (i>0) out.append(",");
				buildJson(jm.getValues().get(i), out,addDoubleQuote);
			}			
		}
		out.append(JsonToken.END_ARRAY.asString());
		
	}

	private static CharSequence doubleQuote(String s,boolean addDoubleQuote) {
		if (addDoubleQuote){
			return "\""+s.replace("\"", "\\\"").replace("\n", "\\n")+"\"";
			}
		return new String("\""+s+"\"");
	}
}
