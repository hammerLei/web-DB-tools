package com.dbTool.util;

//
//Source code recreated from a .class file by IntelliJ IDEA
//(powered by Fernflower decompiler)
//


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class JacksonUtils {
  private static final ObjectMapper objMapper = new ObjectMapper();

  public JacksonUtils() {
  }

  public static String toJson(Object obj) throws JsonProcessingException {
    return objMapper.writeValueAsString(obj);
  }

  public static String toJsonNotNull(Object obj) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    return mapper.writeValueAsString(obj);
  }

  public static <T> String toJson(Object obj, Class<T> jsonViewClazz) throws
    JsonProcessingException {
    return objMapper.writerWithView(jsonViewClazz).writeValueAsString(obj);
  }

  public static String toJsonWithInclude(Object obj, Class clazz, String... includeFileds) throws
    JsonProcessingException {
    HashMap include = new HashMap();
    include.put(clazz, new HashSet(Arrays.asList(includeFileds)));
    return toJson(obj, include, (Map) null);
  }

  public static String toJsonWithInclude(Object obj, Map<Class, Set<String>> include) throws
    JsonProcessingException {
    return toJson(obj, include, (Map) null);
  }

  public static String toJsonWithExclude(Object obj, Class clazz, String... excludeFields) throws
    JsonProcessingException {
    HashMap exclude = new HashMap();
    exclude.put(clazz, new HashSet(Arrays.asList(excludeFields)));
    return toJson(obj, (Map) null, exclude);
  }

  public static String toJsonWithExclude(Object obj, Map<Class, Set<String>> exclude) throws
    JsonProcessingException {
    return toJson(obj, (Map) null, exclude);
  }

  public static String toJson(Object obj, Map<Class, Set<String>> include, Map<Class,
    Set<String>> exclude) throws JsonProcessingException {
    if ((null == include || include.isEmpty()) && (null == exclude || exclude.isEmpty())) {
      toJson(obj);
    }

    ObjectMapper mapper = new ObjectMapper();
    SimpleFilterProvider filters = new SimpleFilterProvider();
    Iterator filterNames;
    Entry e;
    Class clazz;
    Set excludeFileds;
    if (null != include && !include.isEmpty()) {
      filterNames = include.entrySet().iterator();

      while (filterNames.hasNext()) {
        e = (Entry) filterNames.next();
        clazz = (Class) e.getKey();
        excludeFileds = (Set) e.getValue();
        ((SimpleFilterProvider) filters).addFilter(clazz.getName(), SimpleBeanPropertyFilter
          .filterOutAllExcept(excludeFileds));
      }
    }

    if (null != exclude && !exclude.isEmpty()) {
      filterNames = exclude.entrySet().iterator();

      while (filterNames.hasNext()) {
        e = (Entry) filterNames.next();
        clazz = (Class) e.getKey();
        excludeFileds = (Set) e.getValue();
        ((SimpleFilterProvider) filters).addFilter(clazz.getName(), SimpleBeanPropertyFilter
          .serializeAllExcept(excludeFileds));
      }
    }

    mapper.setFilters(filters);
    final HashSet filterNames1 = new HashSet();
    Iterator e1;
    if (null != include && !include.isEmpty()) {
      e1 = include.keySet().iterator();

      while (e1.hasNext()) {
        clazz = (Class) e1.next();
        filterNames1.add(clazz.getName());
      }
    }

    if (null != exclude && !exclude.isEmpty()) {
      e1 = exclude.keySet().iterator();

      while (e1.hasNext()) {
        clazz = (Class) e1.next();
        filterNames1.add(clazz.getName());
      }
    }

    mapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector() {
      public Object findFilterId(Annotated ac) {
        String name = ac.getName();
        return filterNames1.contains(name) ? name : null;
      }
    });
    return mapper.writeValueAsString(obj);
  }

  public static JsonNode toJsonNode(String jsonText) {
    JsonNode jsonNode = null;

    try {
      jsonNode = objMapper.readTree(jsonText);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return jsonNode;
  }

  public static <T> T fromJson(String jsonText, Class<T> clazz) throws IOException {
    return jsonText != null && !"".equals(jsonText) ? objMapper.readValue(jsonText, clazz) : null;
  }

  public static <T> T fromJson(String jsonText, TypeReference<T> valueTypeRef) throws IOException {
    return jsonText != null && !"".equals(jsonText) ? objMapper.readValue(jsonText, valueTypeRef)
      : null;
  }

  public static <T> List<T> fromJson2List(String jsonText, Class<T> clazz) throws IOException {
    if (jsonText != null && !"".equals(jsonText)) {
      List objList = null;

      try {
        JavaType t = objMapper.getTypeFactory().constructParametricType(List.class, new
          Class[]{clazz});
        objList = (List) objMapper.readValue(jsonText, t);
      } catch (Exception var4) {
        ;
      }

      return objList;
    } else {
      return null;
    }
  }

  public static JsonNode getValueFromJson(String json, String key) throws
    JsonProcessingException, IOException {
    JsonNode node = objMapper.readTree(json);
    return node.get(key);
  }

  public static List<JsonNode> getListFromJson(String json, String key) throws
    JsonProcessingException, IOException {
    ArrayList jsonNodes = null;
    JsonNode node = objMapper.readTree(json);
    JsonNode arrayNode = node.get(key);
    if (arrayNode.isArray()) {
      jsonNodes = new ArrayList();
      Iterator var5 = arrayNode.iterator();

      while (var5.hasNext()) {
        JsonNode jsonNode = (JsonNode) var5.next();
        jsonNodes.add(jsonNode);
      }
    }

    return jsonNodes;
  }

  public static String map2Json(Map<String, Object> map) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.writeValueAsString(map);
  }

  public static Map<String, Object> json2Map(String json) throws JsonParseException,
    JsonMappingException, IOException {
    ObjectMapper mapper = new ObjectMapper();
    return (Map<String, Object>) mapper.readValue(json, new TypeReference() {
    });
  }

  public static Map parseJSON2Map(String jsonStr)throws JsonParseException,
      JsonMappingException, IOException {
    Map<String, Object> map = json2Map(jsonStr);
    parseMap(map);
    return map;
  }

  public static Map parseMap(Map<String, Object> map){
    if(MapUtils.isEmpty(map)){
      return map;
    }
    for (Map.Entry<String, Object> entry : map.entrySet()) {
      String param2value = entry.getKey() + "=" + entry.getValue();
      if(entry.getValue() != null){
        if(entry.getValue() instanceof Map){
         Map childMap =  parseMap((Map<String, Object>) entry.getValue());
         map.put(entry.getKey(),childMap);
        }else if(entry.getValue() instanceof List){
          List list = (List)entry.getValue();
          List newList = new ArrayList();
          for (Object obj:list) {
            if(obj instanceof Map){
              Map childMap =  parseMap((Map<String, Object>) obj);
              newList.add(childMap);
            }else{
              newList.add(obj);
            }
          }
          map.put(entry.getKey(),newList);
        }else if(entry.getKey().endsWith("_date") &&  entry.getValue() instanceof Long){
          Date date = new Date((Long)entry.getValue());
          map.put(entry.getKey(),date);
        }
      }
    }
    return map;
  }


  public static boolean isJson(String jsonText) {
    if(StringUtils.isBlank(jsonText)) {
      return false;
    } else {
      try {
        objMapper.readTree(jsonText);
        return true;
      } catch (Exception var2) {
        return false;
      }
    }
  }

  static {
    objMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }


  public static void main(String args []){
    String a = "{ \"a_date\":1516159322000,\"b_date\":\"1234sd\",\"c\":{\"d\":123,\"e\":\"wqe3\"},\"f\":[{\"g\":123,\"t\":321},{\"g\":1233,\"t\":4321}],\"r\":123}";
    try{
//      parseJSON2Map(a);
      JSONObject c = JSONObject.fromObject(a);
      Map<String,Object> map = new HashedMap();
      map.put("id",1);
      String mapJson = map2Json(map);

      boolean flag = isJson("{ \"a\":1,\"b\":\"1234sd\"}");
      Map mapa = parseJSON2Map("{ \"a\":1,\"b\":\"1234sd\"}");

      Map mapb = parseJSON2Map(a);
      Map mapc = json2Map(a);
      System.out.println("111");
    }catch (Exception e){
e.printStackTrace();
    }

  }
}

