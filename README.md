# dynamicJson

This library can be used to read any json [in java] and provide some methods to query (and edit) them via JsonPath.

The library was initially developed to interact with CouchDB.

The library is still in development.

Basic Example:
```
  	String body = "{\"a\":\"b\"}";
  	JsonMap jm = JsonMapReaderLib.read(body, om);

  	JsonMap bb = jm.getProperty("$.a");
  	Assert.assertEquals(new JsonMap("b"), bb);
		
```


Advanced Example:
```
		String body = "{\"a\":\"b\"}";
		JsonMap jm = JsonMapReaderLib.read(body, om);
		EditableJsonMap ejm = new EditableJsonMap(jm);
		
		ejm.addProperty("c", new JsonMap("Hello World!"));
		
		StringWriter sw = new StringWriter();
		JsonMapPrinterLib.buildJson(ejm, sw);
		
		String res = "{\"a\":\"b\",\"c\":\"Hello World!\"}";
		Assert.assertEquals(res, sw.toString());
```
