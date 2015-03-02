package com.adler.dynamicJson.jpath;

import org.springframework.util.Assert;

/**
 * A JToken is a single step in the json path string parsing.
 * There is a ROOT object, property objects and filter objects.
 * They all are represented internally as JToken [of differnet types]
 * 
 * @author Davide Zambon
 *
 */
public class JToken {
	
	public static final JToken WILD_ALL_LEVELS = new JToken("**",
			JTType.KEYWORD);
	public static final JToken ROOT = new JToken("$", JTType.KEYWORD);
	public static final JToken WILD = new JToken("*", JTType.KEYWORD);
	public static final JToken CURRENT = new JToken("@", JTType.KEYWORD);

	public static enum JTType {
		KEYWORD, PROPERTY, ARRAY_INDEX, EXP_SLICE, EXP_UNION, EXP_FILTER
	};

	// ALL
	private String value;
	private JTType type;
	// INDEX
	private Integer arrayParam;
	// EXP
	private int[] sliceParam;
	private Integer[] unionParam;
	// FILTER
	private String subject;
	private JOperator operator;
	private String reference;

	public JToken(String def) {
		this.setValue(def);
		type = JTType.PROPERTY;
	}

	public JToken(String def, JTType t) {
		this.setValue(def);
		type = t;
	}

	public JToken(int key) {
		this.arrayParam = key;
		type = JTType.ARRAY_INDEX;
	}

	public JToken(int start, int stop, int step) {
		sliceParam = new int[3];
		sliceParam[0] = start;
		sliceParam[1] = stop;
		sliceParam[2] = step;
		type = JTType.EXP_SLICE;
	}

	public JToken(Integer[] vv) {
		unionParam = vv;
		type = JTType.EXP_UNION;
	}

	public JToken(String s, JOperator o, String r) {
		type = JTType.EXP_FILTER;
		subject = s;
		operator = o;
		reference = r;
	}

	public String getValue() {
		return value;
	}

	public void setArrayParam(Integer arrayParam) {
		Assert.isTrue(type == JTType.KEYWORD);
		this.arrayParam = arrayParam;
	}

	private void setValue(String value) {
		this.value = value;
	}

	public boolean isProperty() {
		return type.equals(JTType.PROPERTY);
	}

	public int[] getSliceParam() {
		Assert.isTrue(type == JTType.EXP_SLICE);
		return sliceParam;
	}

	public Integer getArrayParam() {
		Assert.isTrue(type == JTType.ARRAY_INDEX);
		return arrayParam;
	}

	public Integer[] getUnionParam() {
		Assert.isTrue(type == JTType.EXP_UNION);
		return unionParam;
	}

	public JTType getType() {
		return type;
	}

	public String getSubject() {
		return subject;
	}

	public JOperator getOperator() {
		return operator;
	}

	public String getReference() {
		return reference;
	}

	@Override
	public String toString() {
		String res = "JToken [";
		res += " type=" + type + ", ";
		switch (type) {
			case KEYWORD :
				res += "value=" + value + ", ";
				res += "arrayParam=" + arrayParam + ", ";
				break;
			case ARRAY_INDEX :
				res += "arrayParam=" + arrayParam + ", ";
				break;
			case PROPERTY :
				res += "property=" + value + ", ";
				break;
			case EXP_SLICE :
				res += "sliceParam=" + sliceParam + ", ";
				break;
			case EXP_UNION :
				res += "unionParam=" + unionParam + ", ";
				break;
			case EXP_FILTER :
				res += "subject=" + subject + ", ";
				res += "op=" + operator + ", ";
				res += "object=" + reference + ", ";
				break;
			default :
				res += " add more data...";
		}

		return res + "]";
	}

}
