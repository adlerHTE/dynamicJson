package com.adler.dynamicJson.base;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a dynamicJson. Every json attribute is a key in a map
 * (and the value is another JsonMap). There are static constructors for
 * Array-json and some constants object for: true, false, null. It is possible
 * to check internal properties via "getProperty(jsonPath)" method.
 * 
 * @author Davide Zambon
 *
 */
public class JsonMap implements Serializable {

	private static final long serialVersionUID = -7696328528442715809L;

	public static final int TYPE_SIMPLE_STR = 1;
	public static final int TYPE_SIMPLE_DEC = 2;
	public static final int TYPE_COMPLEX = 3;
	public static final int TYPE_SIMPLE_ARRAY = 4;
	public static final int TYPE_OBJECT_ARRAY = 5;
	public static final int TYPE_NULL = 6;
	public static final int TYPE_TRUE = 7;
	public static final int TYPE_FALSE = 8;

	public static JsonMap NULL = new JsonMap(TYPE_NULL);
	public static JsonMap TRUE = new JsonMap(TYPE_TRUE);
	public static JsonMap FALSE = new JsonMap(TYPE_FALSE);

	private int type;
	private List<String> content = new ArrayList<String>();

	private List<String> keys = new ArrayList<String>();
	private List<JsonMap> values = new ArrayList<JsonMap>();
	private BigDecimal value;
	private String name;

	public static final String hierarchyObjectPropertyPattern = "^[$](\\.)((\\w)*(\\[(\\d)*\\])*)+(\\.((\\w)+(\\[(\\d)*\\])*)+)*";

	public static final String currentObjectPropertyPattern = "^[$](\\.)((\\w)*(\\[(\\d)*\\])*)+";

	protected JsonMap(JsonMap clone) {
		this.type = clone.getType();
		this.getContent().addAll(clone.getContent());
		this.getKeys().addAll(clone.getKeys());
		this.setName(clone.getName());
		this.setValue(clone.getValue());
		this.getValues().addAll(clone.getValues());
	}

	public JsonMap() {
	}

	protected JsonMap(int t) {
		this.type = t;
	}

	public JsonMap(String a) {
		content.add(a);
		type = TYPE_SIMPLE_STR;
	}

	public JsonMap(List<String> a, List<JsonMap> b) {
		if ((a.size() == 0) && (b.size() == 0)) {
			type = TYPE_NULL;
		} else {
			keys.addAll(a);
			values.addAll(b);
			type = TYPE_COMPLEX;
		}
	}

	public JsonMap(BigDecimal db) {
		type = TYPE_SIMPLE_DEC;
		value = db;
	}

	public static JsonMap JsonMapStringArray(List<String> contents) {
		JsonMap res = new JsonMap();
		res.content.addAll(contents);
		res.type = TYPE_SIMPLE_ARRAY;
		return res;
	}

	public static JsonMap JsonMapObjectArray(List<JsonMap> contents) {
		JsonMap res = new JsonMap();
		res.values.addAll(contents);
		res.type = TYPE_OBJECT_ARRAY;
		return res;
	}

	public JsonMap getRootPropertySilently(String name) {
		try {
			JsonMap res = this.getRootProperty(name);
			return res;
		}
		catch (IllegalArgumentException e) {
		}
		catch (IllegalStateException e) {
		}
		return null;

	}

	/**
	 * Return the value of a property at root level called "name"
	 * 
	 * @param name
	 * @return
	 */
	public JsonMap getRootProperty(String name) {
		int candidate = name.indexOf("[");
		int endPointer =  candidate> 0 ? candidate : name
				.length();
		int index = 0;
		if (candidate>=0){
			String str = name.substring(name.indexOf("[") + 1,
					name.indexOf("]"));
			 index = Integer.parseInt(str);
		}
		if (candidate==0){
			return getArrayElement(this,index);
		}
			
		String truePropName = (String) name.subSequence(0, endPointer);
		int n = this.getKeys().indexOf(truePropName);
		if (n < 0) { throw new IllegalArgumentException(
				"No local property called: " + name + " found. Alowed: "
						+ this.getKeys()); }
		if (n >= this.getValues().size()) { throw new IllegalStateException(
				"Invalid index [too big] for property: " + n); }

		JsonMap res = null;
		if (name.contains("[")) {
			JsonMap array = this.getValues().get(n);
			res = getArrayElement(array,index);
			
		} else res = this.getValues().get(n);
		return res;

	}
	
	
	
	

	private JsonMap getArrayElement(JsonMap array, int index) {
		if (array.getValues().size() <= index) { throw new IllegalArgumentException(
				"index of property: " + index + " is smaller then the array: "
						+ this.getValues().size()); }
		return array.getValues().get(index);
	}

	/**
	 * Retrieve the child json given the jsonPath (from current object)
	 * 
	 * @param path
	 *            subset of JsonPath expression Language (es:
	 *            $.{rootProp}.{childProp}[X])
	 * @return matched property
	 */
	public JsonMap getProperty(String path) {

		boolean current = path.matches(currentObjectPropertyPattern);
		boolean heir = path.matches(hierarchyObjectPropertyPattern);

		if (current) {
			loggInfo("getting property from current object: "
					+ path.substring(2));
			return this.getRootProperty(path.substring(2));
		}

		if (heir) {
			loggInfo("search in hierarchy!!!");
			String[] hierarchy = path.substring(2).split("\\.");
			JsonMap m = this;
			for (String p : hierarchy) {
				m = m.getRootProperty(p);
			}
			return m;
		}

		loggInfo("Wrong path??? " + path);

		return null;
	}

	/**
	 * I cannot import any kind of log here. In need of a log, override the
	 * following method!
	 * 
	 * @param a
	 */
	protected void loggInfo(String a) {
	}

	public BigDecimal getValue() {
		return value;
	}
	public void setValue(BigDecimal value) {
		this.value = value;
	}
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public List<String> getContent() {
		return content;
	}
	public void setContent(List<String> content) {
		this.content = content;
	}
	public List<String> getKeys() {
		return keys;
	}
	public void setKeys(List<String> keys) {
		this.keys = keys;
	}
	public List<JsonMap> getValues() {
		return values;
	}
	public void setValues(List<JsonMap> values) {
		this.values = values;
	}

	public String toFullString() {
		String vv = "";
		if (this.type == JsonMap.TYPE_COMPLEX) {
			for (int k = 0; k < this.keys.size(); k++) {
				vv += "" + this.getKeys().get(k) + ":"
						+ this.getValues().get(k).toFullString() + "\n";
			}
			return "JsonMap type: " + this.type + "  content=" + vv + "\n";
		}
		if (this.type == JsonMap.TYPE_OBJECT_ARRAY) {
			for (JsonMap jm : this.values) {
				vv += jm.toFullString() + ",";

			}
			return "JsonMap type: " + this.type + "  content=" + vv + "\n";
		}
		if (this.type == JsonMap.TYPE_SIMPLE_ARRAY) {
			for (String jm : this.content) {
				vv += jm + ",";

			}
			return "JsonMap type: " + this.type + "  content=" + vv + "\n";
		}

		return "JsonMap [type=" + type + ", content=" + content + ", keys="
				+ keys + ", values=" + vv + ", value="
				+ (value != null ? value.toPlainString() : "null") + ", name="
				+ name + "]";
	}

	@Override
	public String toString() {
		return this.toContentString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + ((keys == null) ? 0 : keys.hashCode());
		result = prime * result + type;
		result = prime * result + ((values == null) ? 0 : values.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		JsonMap other = (JsonMap) obj;
		if (content == null) {
			if (other.content != null) return false;
		} else if (!content.equals(other.content)) return false;
		if (keys == null) {
			if (other.keys != null) return false;
		} else if (!keys.equals(other.keys)) return false;
		if (type != other.type) return false;
		if (values == null) {
			if (other.values != null) return false;
		} else if (!values.equals(other.values)) return false;
		return true;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String toContentString() {
		switch (this.getType()) {
			case JsonMap.TYPE_NULL :
				return "null";
			case JsonMap.TYPE_TRUE :
				return "true";
			case JsonMap.TYPE_FALSE :
				return "false";
			case JsonMap.TYPE_SIMPLE_STR :
				return this.getContent().get(0);
			case JsonMap.TYPE_SIMPLE_DEC :
				return "" + this.getValue().toPlainString();
			case JsonMap.TYPE_OBJECT_ARRAY :
			case JsonMap.TYPE_SIMPLE_ARRAY :
				return "array";
			case JsonMap.TYPE_COMPLEX :
				return "object";
		}
		return "unknown";
	}

	public JsonMap getPropertySilently(String string) {
		JsonMap res = JsonMap.NULL;
		try{
			res =this.getProperty(string);
		}catch(IllegalArgumentException e){}
		return res;
		
	}
	

}
