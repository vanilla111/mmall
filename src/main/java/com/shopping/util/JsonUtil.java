package com.shopping.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import java.text.SimpleDateFormat;

@Slf4j
public class JsonUtil {
    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        //对象所有属性全部列入
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.ALWAYS);

        //取消默认的时间戳格式
        objectMapper.configure(SerializationConfig.Feature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false);

        //忽略空对象转json的错误
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);

        //日期格式统一
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.STANDARD_FORMAT));

        //忽略在json中存在，但java对象中不存在的属性的情况，忽略这种错误
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static <T> String objectToJson(T obj) {
        if (obj == null) {
            return null;
        }

        try {
            return obj instanceof String ? (String) obj : objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Parse object to String error ", e);
            return null;
        }
    }

    public static <T> String objectToJsonPretty(T obj) {
        if (obj == null) {
            return null;
        }

        try {
            return obj instanceof String ? (String) obj : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Parse object to String error ", e);
            return null;
        }
    }

    public static <T> T stringToObj(String s, Class<T> klass) {
        if (StringUtils.isEmpty(s) || klass == null)
            return null;

        try {
            return klass.equals(String.class) ? (T)s : objectMapper.readValue(s, klass);
        } catch (Exception e) {
            log.warn("Parse string to obj error");
            return null;
        }
    }


    public static <T> T stringToObj(String s, TypeReference<T> type){
        if (StringUtils.isEmpty(s) || type == null)
            return null;

        try {
            return (T)(type.getType().equals(String.class) ? s : objectMapper.readValue(s, type));
        } catch (Exception e) {
            log.warn("Parse string to obj error");
            return null;
        }
    }

    public static <T> T stringToObj(String s, Class<?> collectionClass, Class<?>... elementsClasses) {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClass, elementsClasses);

        try {
            return objectMapper.readValue(s, javaType);
        } catch (Exception e) {
            log.warn("Parse string to obj error");
            return null;
        }
    }
}
