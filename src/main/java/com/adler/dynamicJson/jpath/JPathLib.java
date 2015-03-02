package com.adler.dynamicJson.jpath;

import java.util.ArrayList;
import java.util.List;

import com.adler.dynamicJson.base.JsonMap;

public class JPathLib {

	static List<JsonMap> getAllChildren(List<JsonMap> list) {
		List<JsonMap> res = new ArrayList<JsonMap>();
		for (JsonMap jsonMap : list) {
			res.add(jsonMap);
			res.addAll(getAllChildren(jsonMap.getValues()));
		}
		return res;
	}

	static JToken readInterval(String exp) {
		String[] vv = exp.split(":");
	
		int start = 0;
		if (vv[0].length() > 0) {
			start = Integer.parseInt(vv[0]);
		}
	
		int stop = 0;
		if (vv.length > 1 && vv[1].length() > 0) {
			stop = Integer.parseInt(vv[1]);
	
		}
	
		int step = 1;
		if (vv.length > 2 && vv[2].length() > 0) {
			step = Integer.parseInt(vv[2]);
		}
	
		JToken res = new JToken(start, stop, step);
		return res;
	}

	static JToken readUnion(String exp) {
		String[] ss = exp.split(",");
		Integer[] vv = new Integer[ss.length];
		int i = 0;
		for (String val : ss) {
			vv[i++] = Integer.parseInt(val);
		}
		return new JToken(vv);
	}
	
	static JToken readNumericExpression(String exp) {
		JToken res = null;
		if (exp.contains("@.length")) {
			// refer to end
			int beginIndex = exp.indexOf("@.length") + 8;
			int endIndex = exp.indexOf(")");

			String startString = exp.substring(beginIndex, endIndex);
			Integer start = Integer.parseInt(startString);

			res = new JToken(start, 0, -start); // converted in slice array
		}
		
		if (res==null)
			throw new RuntimeException("Not implemented converter for: "+exp);
		return res;
	}

	static JToken readQueryExpression(final String exp) {
		String subject="";
		JOperator operator=null;
		String reference="";
		int start = exp.indexOf("(")+1;
		int end = exp.indexOf(")");
		
		int jopPos = exp.length();
		for (String s: new String[]{"<",">","=","!"}) {
			int a =exp.indexOf(s);
			if (a>=0) jopPos = Math.min(jopPos,a);
		}
		
		if (jopPos<exp.length()){
			subject = exp.substring(start,jopPos);
			String second = (""+exp.charAt(jopPos+1)).equals("=")?"=":"";
			operator = JOperator.fromSymbol(""+exp.charAt(jopPos)+second);
			if (second.length()>0) jopPos++;
			reference = exp.substring(jopPos+1,end);
		}else{
			
			subject = exp.substring(start,end);
		}
		subject= subject.replace("@", "$");
		return new JToken(subject,operator, reference);
		
	}

	/**
	 * Example of expressions:
	 * 
	 * @param exp
	 * @return
	 */
	static JToken parseExpression(String exp) {
	
	
		if (exp.contains(":")) { return readInterval(exp);
	
		}
		if (exp.contains(",")) { return readUnion(exp);
	
		}
		if (exp.startsWith("?")) {
			return readQueryExpression(exp);
	
		}
	
		if (exp.contains("@")) {
			// read complex Expression
			return readNumericExpression(exp);
	
		}
	
		// simple char
		if (exp.equals(JToken.WILD.getValue())) { return JToken.WILD; }
	
		// simple int
		try {
			int key = Integer.parseInt(exp);
			return new JToken(key);
	
		}
		catch (NumberFormatException ne) {
		}
	
		return null;
	}

	static List<String> extractExpressions(String s) {
		List<String> res = new ArrayList<String>();
		int parStart = s.indexOf(JPath.LEFT_PARENTHESIS);
		while (parStart >= 0) {
			int parEnd = s.indexOf(JPath.RIGHT_PARENTHESIS);
			res.add(s.substring(parStart + 1, parEnd));
			parStart = s.indexOf(JPath.LEFT_PARENTHESIS, parStart + 1);
		}
		return res;
	}

}
