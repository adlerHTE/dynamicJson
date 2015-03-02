package com.adler.dynamicJson;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.adler.dynamicJson.base.JsonMap;
import com.adler.dynamicJson.base.TrueDecimal;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * This class use reflection property to construct JsonMap from normal POJOs and
 * vice versa.
 * 
 * @author Davide Zambon
 *
 */
public class JsonMapFactory {

	private static final Logger LOGGER = Logger.getLogger(JsonMapFactory.class);

	private static final List<String> excludedMethods = Arrays
			.asList(new String[]{"getClass", "getModifiers", "getMethods",
					"getSystemClassLoader", "getParent", "get",
					"getDeclaringClass"});

	private static final Comparator<Method> methodComparator = new Comparator<Method>() {

		@Override
		public int compare(Method o1, Method o2) {
			return o1.getName().compareTo(o2.getName());
		}
	};

	/**
	 * Return the JsonMap representation of a generic Object
	 * 
	 * @param o
	 * @return
	 */
	public static JsonMap getJsonMap(Object o) {
		if (o == null) { return JsonMap.NULL; }
		LOGGER.debug("This class is still under development! Use with care!");

		Class<?> c = o.getClass();

		if (c.equals(String.class)) {
			try {
				BigDecimal a = new BigDecimal(o.toString());
				a.intValueExact();
				return new JsonMap(a);
			}
			catch (NumberFormatException | ArithmeticException nfe) {
				LOGGER.debug(o.toString() + " is  NOT a number!");
			}
			return new JsonMap(o.toString());

		}
		if (c.equals(Date.class)) { return new JsonMap(new BigDecimal(
				((Date) o).getTime())); }

		if (c.equals(BigDecimal.class)) {
			//chenge this code to parametric!!!
			TrueDecimal td = new TrueDecimal((BigDecimal) o);
			JsonMap res =  getJsonMap(td);
			return res;
			}
		if (c.equals(BigInteger.class)) { return new JsonMap(new BigDecimal(
				(BigInteger) o)); }
		if (c.equals(Integer.class)) { return new JsonMap(new BigDecimal(
				(Integer) o)); }
		if (c.equals(int.class)) { return new JsonMap(new BigDecimal(
				(Integer) o)); }
		if (c.equals(Long.class)) { return new JsonMap(new BigDecimal((Long) o)); }
		if (c.equals(String[].class)) { return JsonMap
				.JsonMapStringArray(Arrays.asList((String[]) o)); }

		if (o instanceof Enum<?>) { return new JsonMap(o.toString()); }

		if (o instanceof List<?>) {
			List<?> list = (List<?>) o;
			Object first = list.get(0);
			if (first == null) return JsonMap.NULL;
			if (first instanceof String) {
				List<String> res = new ArrayList<String>();
				for (Object s : list) {
					res.add(s.toString());
				}
				return JsonMap.JsonMapStringArray(res);
			} else {
				List<JsonMap> contents = new ArrayList<JsonMap>();
				for (Object s : list) {
					contents.add(getJsonMap(s));
				}
				return JsonMap.JsonMapObjectArray(contents);
			}

		}

		List<String> keys = new ArrayList<String>();
		List<JsonMap> vals = new ArrayList<JsonMap>();

		List<Method> mms = Arrays.asList(c.getMethods());
		Collections.sort(mms, methodComparator);

		for (Method method : mms) {
			if (!excludedMethods.contains(method.getName())
					&& (method.getName().startsWith("get") || method.getName().startsWith("is"))
					&& method.getTypeParameters().length == 0) {
				try {
					LOGGER.debug("Invoking: " + method.getName());
					int n = 3; //get
					if (method.getName().startsWith("is")) n=2;
					String prop = method.getName().substring(n);
					prop = ("" + prop.charAt(0)).toLowerCase()
							+ (prop.length() > 1 ? prop.substring(1) : "");
					JsonMap val;
					val = getJsonMap(method.invoke(o, ((Object[]) null)));
					keys.add(prop);
					vals.add(val);
				}
				catch (IllegalArgumentException e) {
					// nothing to do
//					e.printStackTrace();
				}
				catch (IllegalAccessException e) {
					// nothing to do
//					e.printStackTrace();
				}
				catch (InvocationTargetException e) {
					// nothing to do
//					e.printStackTrace();
				}

			}
		}
		return new JsonMap(keys, vals);

	}

	public static AnnotationData getAnnotationData(Class<?> class1) {
		AnnotationData res = new AnnotationData();
		for (Annotation a : class1.getAnnotations()) {
			if (a.annotationType().equals(
					com.fasterxml.jackson.annotation.JsonTypeInfo.class)) {
				JsonTypeInfo sub = (JsonTypeInfo) a;
				res.setAs(sub.include());
				res.setProperty(sub.property());
				res.setUse(sub.use());
			}
			if (a.annotationType().equals(
					com.fasterxml.jackson.annotation.JsonSubTypes.class)) {
				JsonSubTypes sub = (JsonSubTypes) a;
				for (int i = 0; i < sub.value().length; i++) {
					res.getValues().put(sub.value()[i].name(), sub.value()[i].value());
				}

			}
		}
		
		if (res.getProperty()==null || res.getValues().size()==0){
			return null;
		}
		
		if ((!As.PROPERTY.equals(res.getAs())) ||
				(!Id.NAME.equals(res.getUse()))){
			LOGGER.error(" Sorry, Annotation not supported!!! Ignoring...");
			return null;
		}
			
		return res;

	}

	@SuppressWarnings("unchecked")
	public static <T> T getObjectFromJsonMap(Class<T> mainClass, JsonMap map,
			Class<?> genericClass) throws InstantiationException,
			IllegalAccessException {
		LOGGER.debug("This class is still under development! Use with care!");
		LOGGER.debug("class=" + mainClass);
		LOGGER.debug("generic=" + genericClass);
		AnnotationData aData = getAnnotationData(mainClass);
		if (aData != null) {
			LOGGER.debug("annotations:" + aData);
		}

		if (map.getType() == JsonMap.TYPE_NULL) return null;
		if (map.getType() == JsonMap.TYPE_TRUE) return (T) Boolean.TRUE;
		if (map.getType() == JsonMap.TYPE_FALSE) return (T) Boolean.FALSE;
		
		if (mainClass.equals(Integer.class) || mainClass.equals(int.class)) { return ((T) new Integer(map
				.getValue().intValue())); }
		
		if (mainClass.equals(String.class)) { return ((T) map.toContentString()); }
		
		if (mainClass.equals(Long.class)) { return ((T) new Long(map.getValue()
				.intValue())); }
		if (mainClass.equals(BigDecimal.class)) {
			//Parameterize this behavior
			TrueDecimal td = getObjectFromJsonMap(TrueDecimal.class,map);
			return ((T) td.toBigDecimal()); }
		if (mainClass.equals(BigInteger.class)) { return ((T) map.getValue()
				.toBigInteger()); }
		if (mainClass.equals(Date.class)) { return ((T) new Date(map.getValue()
				.longValue())); }


		if (mainClass.equals(String[].class)) {
			String[] res = new String[map.getContent().size()];
			map.getContent().toArray(res);
			return (T) (res);
		}
		if (List.class.isAssignableFrom(mainClass)) {
			if (map.getType() == JsonMap.TYPE_SIMPLE_ARRAY) {
				List<String> res = new ArrayList<String>();
				res.addAll(map.getContent());
				return (T) res;
			}
			if (map.getType() == JsonMap.TYPE_OBJECT_ARRAY) {
				List<Object> res = new ArrayList<Object>();
				for (JsonMap jm : map.getValues()) {
					Object obj = getObjectFromJsonMap(genericClass, jm, null);
					res.add(obj);
				}
				return (T) res;
			}

		}

		T res = null;

		if (mainClass.isEnum()) {
			T[] contants = mainClass.getEnumConstants();

			try {
				Method testConst = mainClass.getMethod("valueOf", String.class);
				T test = (T) testConst.invoke(null, map.toContentString());
				return test;
			}
			catch (NoSuchMethodException | SecurityException
					| IllegalArgumentException | InvocationTargetException e) {
				LOGGER.info("ENUM valueOf() FAILED!!!!");
			}

			// one more try....
			for (T t : contants) {
				if (t.toString().equalsIgnoreCase(map.toContentString())) return t;
			}
			LOGGER.error(" Impossible to convert  ENUM!! " + mainClass);
			return null;

		}

		Class<?> target = mainClass;
		target = getKnownImplementations(target);
		try {
			if (aData != null) {
				target = aData.getTargetClassFor(map);
			}
			
			res = (T) target.newInstance();
		}
		catch (InstantiationException ie) {
			LOGGER.error("Skipped property: is it an Interface?? (Is it annotated with Jackson JsonTypeInfo??)");
			return null;
		}

		List<Method> mms = Arrays.asList(target.getMethods());
		Collections.sort(mms, methodComparator);
		for (Method method : mms) {
			if (!excludedMethods.contains(method.getName())
					&& method.getName().startsWith("set")
					&& method.getParameterTypes().length == 1) {
				try {
					LOGGER.debug("Invoking: " + method.getName());
					String prop = method.getName().substring(3);
					Class<?> param = method.getParameterTypes()[0];
					Class<?> generic2 = null;
					if (Collection.class.isAssignableFrom(param)) {
						generic2 = (Class<T>) ((ParameterizedType) method
								.getGenericParameterTypes()[0])
								.getActualTypeArguments()[0];
					}

					prop = ("" + prop.charAt(0)).toLowerCase()
							+ (prop.length() > 1 ? prop.substring(1) : "");
					JsonMap paramMap = map.getRootProperty(prop);
					Object paramObj = getObjectFromJsonMap(param, paramMap,
							generic2);
					method.invoke(res, paramObj);

				}
				catch (IllegalArgumentException e) {
					System.err.println(e.getMessage());
				}
				catch (IllegalAccessException e) {
				}
				catch (InvocationTargetException e) {
				}

			}
		}

		return res;
	}

	private static Class<?> getKnownImplementations(Class<?> target) {
		if (!target.isInterface()) return target;
		
		if (target.equals(Map.class)) return HashMap.class;
		LOGGER.info("Interface not matched!!!!");
		return target;
	}

	/**
	 * Convert a JsonMap into an actual object of the given type, CAUTION: enums
	 * are tricky and abstract classes are not instantiable!
	 * 
	 * @param o
	 * @param map
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static <T> T getObjectFromJsonMap(Class<T> o, JsonMap map)
			throws InstantiationException, IllegalAccessException {
		return getObjectFromJsonMap(o, map, null);
	}

	public static JsonMap includeInArray(JsonMap... a) {
		List<JsonMap> contents = Arrays.asList(a);
		JsonMap res = JsonMap.JsonMapObjectArray(contents);
		return res;
	}
	
	
}
