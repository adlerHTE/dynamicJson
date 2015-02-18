package com.adler.dynamicJson;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.adler.dynamicJson.base.JsonMap;
import com.adler.dynamicJson.exception.InvalidPropertyName;

public class EditableJsonMap extends JsonMap {

	private static final long serialVersionUID = -7944795403653243173L;

	private static final Logger LOGGER = Logger
			.getLogger(EditableJsonMap.class);

	public EditableJsonMap(JsonMap jm) {
		super(jm);
	}

	@Override
	protected void loggInfo(String a) {
		LOGGER.info(a);
	}

	public void addProperty(String name, JsonMap toBeAdded)
			throws InvalidPropertyName {
		this.addProperty(name, toBeAdded, false);
	}

	public void addPropertyForceOverride(String name, JsonMap toBeAdded)
		{
		try{
			this.addProperty(name, toBeAdded, true);
		}catch(InvalidPropertyName ie){
			throw new IllegalStateException("Should never happenes");
		} 
	}
	
	private void addProperty(String name, JsonMap toBeAdded,
			boolean overrideIfExists) throws InvalidPropertyName {

		switch (this.getType()) {
			case JsonMap.TYPE_NULL :
				throw new IllegalArgumentException(
						"Cannot add property to null json");
			case JsonMap.TYPE_SIMPLE_STR :
			case JsonMap.TYPE_SIMPLE_DEC :
				throw new IllegalArgumentException(
						"Cannot add property to String / Decimal json value");
			case JsonMap.TYPE_OBJECT_ARRAY :
				LOGGER.info("property name: " + name
						+ " is ignored in Array Json");
				addToArray(toBeAdded);
				break;
			case JsonMap.TYPE_COMPLEX :
				addToMap(name, toBeAdded, overrideIfExists);
				break;
			default :
				throw new IllegalArgumentException("Unknown jsonMap type: "
						+ this.getType());
		}

	}

	private void addToMap(String name, JsonMap toBeAdded, boolean override)
			throws InvalidPropertyName {
		JsonMap alreadyPresent = this.getRootPropertySilently(name);

		if (alreadyPresent != null
				&& alreadyPresent.getType() != JsonMap.TYPE_SIMPLE_ARRAY
				&& alreadyPresent.getType() != JsonMap.TYPE_OBJECT_ARRAY
				&& !override) {
			LOGGER.info("Error: poperty already exists!");
			throw new InvalidPropertyName("A property called " + name
					+ " already exists. "+alreadyPresent.toFullString());
		}
		if (alreadyPresent == null) {
			this.getKeys().add(name);
			this.getValues().add(toBeAdded);
		}

		if (alreadyPresent != null && override) {
			int element = this.getKeys().indexOf(name);
			this.getValues().set(element, toBeAdded);
		}

		if (alreadyPresent != null && !override) {
			if (alreadyPresent.getType() == JsonMap.TYPE_OBJECT_ARRAY) {
				if (toBeAdded.getType() != JsonMap.TYPE_COMPLEX) throw new IllegalArgumentException(
						"Only TYPE_COMPLEX allowed to be added in this array, actual type is "
								+ toBeAdded.getType());
				alreadyPresent.getValues().add(toBeAdded);
			}
			else if (alreadyPresent.getType() == JsonMap.TYPE_SIMPLE_ARRAY) {
				if (toBeAdded.getType() != JsonMap.TYPE_SIMPLE_STR) throw new IllegalArgumentException(
						"Only TYPE_SIMPLE_STR allowed to be added in this array, actual type is "
								+ toBeAdded.getType());
				alreadyPresent.getValues().add(toBeAdded);
			}else{
				throw new IllegalStateException("This case should be already checked!");
			}

		}
		LOGGER.info(" new JsonMap size: " + this.getKeys().size() + " / "
				+ this.getValues().size());
	}

	private void addToArray(JsonMap toBeAdded) {
		this.getValues().add(toBeAdded);
	}

	public void addPropertyToArray(String property, JsonMap jsonMap) {
		JsonMap array = this.getRootProperty(property);
		if (array.getType()!=JsonMap.TYPE_OBJECT_ARRAY)
			throw new IllegalArgumentException();
		array.getValues().add(jsonMap);
		
		
	}
	public void addPropertyToArray(String property, JsonMap jsonMap,boolean force) {
		try{
		   this.getRootProperty(property);
		  }catch(IllegalArgumentException e){
			  //property not present
			try {
				this.addProperty(property, JsonMap.JsonMapObjectArray(new ArrayList<JsonMap>()));
			}
			catch (InvalidPropertyName e1) {
			}  
		  }
		this.addPropertyToArray(property, jsonMap);
		
		
	}

}
