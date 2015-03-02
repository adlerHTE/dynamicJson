package com.adler.dynamicJson.jpath;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import com.adler.dynamicJson.base.JsonMap;
import com.adler.dynamicJson.jpath.JToken.JTType;

/**
 *  Implementation of JsonPath
 * http://goessner.net/articles/JsonPath/
 * 
 * @author Davide Zambon
 *
 */
public class JPath {

	static final String RIGHT_PARENTHESIS = "]";

	static final String LEFT_PARENTHESIS = "[";

	private static final Logger LOGGER = Logger.getLogger(JPath.class);

	private static final String SEPARATOR = ".";

	private String jPath;

	private List<JToken> tokens = new ArrayList<JToken>();

	public JPath(String s) {
		jPath = s;
		process();
	}

	public List<JToken> getTokens() {
		return tokens;
	}

	public void printTokens() {
		LOGGER.info("Tokens:");
		for (JToken token : tokens) {
			LOGGER.info(" " + token);
		}
	}

	private void process() {
		LOGGER.info("JPath: " + jPath);

		List<String> expressions = JPathLib.extractExpressions(jPath);
		LOGGER.debug("expressions: " + expressions.size());

		// replace the expressions with separators %i
		String workingPath = jPath;
		int j = 0;
		for (String exp : expressions) {
			LOGGER.debug("exp=" + exp);
			workingPath = StringUtils.replace(workingPath, exp, "%" + (j++), 1);
		}

		LOGGER.info("workingPath:" + workingPath);
		String[] tt = workingPath.split("\\.|\\["); // we leave the "]" in the
													// response
		if (!jPath.startsWith(JToken.ROOT.getValue() + SEPARATOR)) { throw new IllegalArgumentException(
				"Invalid JsonPath:" + jPath); }

		Assert.isTrue(tt[0].equals(JToken.ROOT.getValue()));
		tokens.add(JToken.ROOT);

		for (int i = 1; i < tt.length; i++) {
			String current = tt[i];

			// check for token names:
			if (current.equals(JToken.WILD.getValue())) {
				tokens.add(JToken.WILD);
				continue;
			}
			if (current.startsWith("%")) {
				// add token expression!
				current = current.substring(1, current.length() - 1); // this -1
																		// is
																		// for
																		// the
																		// "]"
				int expN = Integer.parseInt(current);
				tokens.add(JPathLib.parseExpression(expressions.get(expN)));
				continue;
			}

			if (current.equals("")) { // special case ".."
				tokens.add(JToken.WILD_ALL_LEVELS);
				continue;
			}

			if (current.length() > 0) {
				JToken tmp = new JToken(current);
				tokens.add(tmp);

			}

		}

	}

	public JsonMap apply(JsonMap jm) {
		LOGGER.info("applying: " + this.jPath);

		List<JsonMap> current = new ArrayList<JsonMap>();
		for (JToken t : this.tokens) {
			LOGGER.info("Processing TOKEN: " + t);
			if (t.equals(JToken.ROOT)) {
				current.add(jm);
			}

			if (t.isProperty()) {
				List<JsonMap> newCurrr = new ArrayList<JsonMap>();
				for (JsonMap jsonMap : current) {
					JsonMap newJM = jsonMap.getRootPropertySilently(t
							.getValue());
					if (newJM != null) {
						newCurrr.add(newJM);
					}
				}
				current.clear();
				current.addAll(newCurrr);

			}

			if (t.getType().equals(JTType.ARRAY_INDEX)) {
				List<JsonMap> newCurrr = new ArrayList<JsonMap>();
				for (JsonMap jsonMap : current) {
					if (jsonMap.getValues().size() > t.getArrayParam()) newCurrr
							.add(jsonMap.getValues().get(t.getArrayParam()));
				}
				current.clear();
				current.addAll(newCurrr);
			}

			if (t.equals(JToken.WILD)) {
				List<JsonMap> newCurrr = new ArrayList<JsonMap>();
				for (JsonMap jsonMap : current) {
					newCurrr.addAll(jsonMap.getValues());
				}
				current.clear();
				current.addAll(newCurrr);
			}

			if (t.equals(JToken.WILD_ALL_LEVELS)) {
				List<JsonMap> newDocs = JPathLib.getAllChildren(current);
				current.clear();
				current.addAll(newDocs);
			}

			if (t.getType() == JTType.EXP_SLICE) {
				List<JsonMap> newCurrr = new ArrayList<JsonMap>();

				int[] param = t.getSliceParam();
				for (JsonMap jsonMap : current) {
					for (int i = param[0]; i < param[1]; i += param[2]) {
						int actualIndex = i >= 0 ? i : i
								+ jsonMap.getValues().size();
						if (jsonMap.getValues().size() > actualIndex) newCurrr
								.add(jsonMap.getValues().get(actualIndex));
					}

				}
				current.clear();
				current.addAll(newCurrr);
			}

			if (t.getType() == JTType.EXP_UNION) {
				List<JsonMap> newCurrr = new ArrayList<JsonMap>();

				Integer[] param = t.getUnionParam();
				for (JsonMap jsonMap : current) {
					for (Integer integer : param) {
						if (jsonMap.getValues().size() > integer) newCurrr
								.add(jsonMap.getValues().get(integer));

					}
				}

				current.clear();
				current.addAll(newCurrr);
			}
			
			if (t.getType() == JTType.EXP_FILTER){
				List<JsonMap> newCurrr = new ArrayList<JsonMap>();

				String subject = t.getSubject();
				JOperator op = t.getOperator();
				String obj = t.getReference();
				for (JsonMap jsonMap : current) {
					for (JsonMap jsonMap2 : jsonMap.getValues()) {
						JsonMap sub = jsonMap2.getPropertySilently(subject);
						if (!sub.equals(JsonMap.NULL)){
							if (applyFilter(sub,op,obj)){
								newCurrr.add(jsonMap2);
							}
							
						}	
						
					}

					
				}
				current.clear();
				current.addAll(newCurrr);


			}

			

			LOGGER.info("Currents: " + current.size());
			for (JsonMap jsonMap : current) {
				LOGGER.info(" " + jsonMap.toFullString());
			}

		}

		if (current.size() > 0) { return JsonMap.JsonMapObjectArray(current);

		}

		return JsonMap.NULL;
	}

	private boolean applyFilter(JsonMap sub, JOperator op, String obj) {
		if (op==null) return true;
		
		switch (sub.getType()){
			case JsonMap.TYPE_NULL:
				return false;
			case JsonMap.TYPE_FALSE:
				boolean a = op.equals(JOperator.EQ) && obj.equalsIgnoreCase("false");
				boolean b = op.equals(JOperator.NE) && obj.equalsIgnoreCase("true");
				return a || b;
			case JsonMap.TYPE_TRUE:
				boolean c = op.equals(JOperator.EQ) && obj.equalsIgnoreCase("true");
				boolean d = op.equals(JOperator.NE) && obj.equalsIgnoreCase("false");
				return c || d;
				
			case JsonMap.TYPE_SIMPLE_DEC:
				int compare = sub.getValue().compareTo(new BigDecimal(obj));
				return op.isTrueWithCompareVal(compare);

			case JsonMap.TYPE_SIMPLE_STR:
				String val = sub.toContentString();
				int compare2 = val.compareTo(obj); 
				return op.isTrueWithCompareVal(compare2);
				
			default :
				LOGGER.info("Filter NOT APPLIED!!!");

				
		}
		
		return false;
	}

	public JToken removeLastLevel() {
		return this.tokens.remove(this.tokens.size()-1);
		
	}

}
