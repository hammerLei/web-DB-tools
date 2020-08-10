package com.dbTool.util;

import com.dbTool.domain.DatabaseUserInfo;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;

import javax.servlet.http.HttpServletRequest;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BeanUtil extends BeanUtils {

  public final static Class I = Integer.class;
  public final static Class D = Double.class;
  public final static Class L = Long.class;
  private static Log LOG = LogFactory.getLog(BeanUtil.class);

  public static boolean isEqual(Object o1, Object o2) {

    return o1 == null ? o2 == null : o1.equals(o2);
  }

  public static boolean isEqualValues(Object o1, Object o2) {
    if (o1 instanceof Boolean) {
      o1 = Boolean.TRUE.equals(o1) ? 1 : 0;
    }
    if (o2 instanceof Boolean) {
      o2 = Boolean.TRUE.equals(o2) ? 1 : 0;
    }
    if (isEqual(o1, o2)) {
      return true;
    }
    return String.valueOf(o1).equals(String.valueOf(o2));
  }


  public static Object getFieldValue(Object obj, String fieldName) {

    if (obj == null) {
      return null;
    }

    Field targetField = getTargetField(obj.getClass(), fieldName);

    try {
      return FieldUtils.readField(targetField, obj, true);
    } catch (IllegalAccessException e) {
      LOG.error(e, e);
    }
    return null;
  }

  private static Field getTargetField(Class<?> targetClass, String fieldName) {
    Field field = null;

    if (targetClass == null) {
      return field;
    }

    if (Object.class.equals(targetClass)) {
      return field;
    }

    field = FieldUtils.getDeclaredField(targetClass, fieldName, true);
    if (field == null) {
      field = getTargetField(targetClass.getSuperclass(), fieldName);
    }


    return field;
  }

  public static Map<String, Object> objectToMap(Object obj) throws IntrospectionException,
      IllegalAccessException, InvocationTargetException {
    if (obj == null)
      return null;

    Map<String, Object> map = new HashMap<>();

    BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
    PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
    for (PropertyDescriptor property : propertyDescriptors) {
      String key = property.getName();
      if (key.compareToIgnoreCase("class") == 0) {
        continue;
      }
      Method getter = property.getReadMethod();
      Object value = getter != null ? getter.invoke(obj) : null;
      map.put(key, value);
    }
    return map;
  }


  /**自动匹配参数赋值到实体bean中
   * @param bean
   * @param request
   */
  public static void populate(Object bean, HttpServletRequest request){
    Class clazz = bean.getClass();
    Method ms[] = clazz.getDeclaredMethods();
    for(Method m : ms){
      String mname = m.getName();
      if(!mname.startsWith("set")
          || ArrayUtils.isEmpty(m.getParameterTypes())){
        continue;
      }
      try{
        String field = mname.toLowerCase().charAt(3) + mname.substring(4, mname.length());
//        System.out.println(field);
        String value = request.getParameter(field);
        if(StringUtils.isEmpty(value)){
          continue;
        }

        Class parameter = m.getParameterTypes()[0];

        if((parameter.isAssignableFrom(Short.class) || parameter.isAssignableFrom(short.class)) && NumberUtils.isDigits(value)){
          m.invoke(bean, Short.valueOf(value));
        }else if((parameter.isAssignableFrom(Integer.class) || parameter.isAssignableFrom(int.class)) && NumberUtils.isDigits(value)){
          m.invoke(bean, Integer.valueOf(value));
        }else if((parameter.isAssignableFrom(Long.class) || parameter.isAssignableFrom(long.class)) && NumberUtils.isDigits(value)){
          m.invoke(bean, Long.valueOf(value));
        }else if((parameter.isAssignableFrom(Float.class) || parameter.isAssignableFrom(float.class)) && NumberUtils.isNumber(value)){
          m.invoke(bean, Float.valueOf(value));
        }else if((parameter.isAssignableFrom(Double.class) || parameter.isAssignableFrom(double.class)) && NumberUtils.isNumber(value)){
          m.invoke(bean, Double.valueOf(value));
        }else if(parameter.isAssignableFrom(Date.class)){
          m.invoke(bean, DateUtils.parseDate(value, "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss"));
        }else if((parameter.isAssignableFrom(Boolean.class) || parameter.isAssignableFrom(boolean.class)) && ("true".equals(value) || "false".equals(value))){
          m.invoke(bean, Boolean.valueOf(value));
        }else{
          m.invoke(bean, value);
        }
      }catch(Exception e){
        LOG.error("[BeanUtil populate]",e);
        continue;
      }
    }
  }

  public static void main(String arsg []) {
    DatabaseUserInfo bean = new DatabaseUserInfo();
    Class clazz = bean.getClass();
    Method ms[] = clazz.getDeclaredMethods();
    String mname;
    String field;
    String fieldType;
    String value;
    for (Method m : ms) {
      mname = m.getName();
      if (!mname.startsWith("set")
          || ArrayUtils.isEmpty(m.getParameterTypes())) {
        continue;
      }
      try {
        field = mname.toLowerCase().charAt(3) + mname.substring(4, mname.length());
//        System.out.println(field);
        value = "1111";
        if (StringUtils.isEmpty(value)) {
          continue;
        }

        fieldType = m.getParameterTypes()[0].getName();
        Class<?> ft = m.getParameterTypes()[0];

        Object ob2 = m.getParameterTypes();
        Object ob1 = m.getParameterTypes()[0];
        Object ob = m.getParameters()[0];
        //以下可以确认value为String类型
//        if(String.class.getName().equals(fieldType) ){
//          m.invoke(bean, value);
//        }else
        if ((ft.isAssignableFrom(Long.class) || ft.isAssignableFrom(long.class) ) && NumberUtils.isNumber(value)) {
          System.out.println("3fieldType:"+fieldType);
          m.invoke(bean, 12333333l);
        } else if ((ft.isAssignableFrom(Integer.class) || ft.isAssignableFrom(int.class))&& NumberUtils.isDigits(value)) {
          System.out.println("1fieldType:"+fieldType);
          m.invoke(bean, 1);
        } else if (Short.class.getName().equals(fieldType) && NumberUtils.isDigits(value)) {
          m.invoke(bean, Short.valueOf(value));
        } else if (ob instanceof Float && NumberUtils.isNumber(value)) {
          System.out.println("2fieldType:"+fieldType);
          m.invoke(bean, 2.345f);
        }  else if (ob instanceof Double && NumberUtils.isNumber(value)) {
          System.out.println("4fieldType:"+fieldType);
          m.invoke(bean, 3.456d);
        } else if (ft.isAssignableFrom(Date.class)) {
          m.invoke(bean, DateUtils.parseDate("2018-01-01", "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss"));
        } else {
          if(ob instanceof Boolean){
            value = "false";
          }
          m.invoke(bean, value);
        }

      } catch (Exception e) {
        LOG.error("[BeanUtil populate]", e);
        continue;
      }
    }
    System.out.println("end");
  }

}

