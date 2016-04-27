package com.dp.simplerest.util;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	private static final TypeReference<Map<String, Object>> STRING_OBJECT_REFERENCE = new TypeReference<Map<String, Object>>() {
	};

	private static final Logger logger = Logger.getLogger(JsonUtils.class);

	static {
		OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		OBJECT_MAPPER.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
	}

	public static String toJsonString(Object object) {
		try {
			return OBJECT_MAPPER.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			String message = "convert to json string fail " + object;
			logger.warn(message, e);
			throw new RuntimeException(message, e);
		}
	}

	public static <T> T fromJsonString(Class<T> clazz, String jsonString) {
		if (StringUtils.isBlank(jsonString)) {
			return null;
		}
		try {
			return OBJECT_MAPPER.readValue(jsonString, clazz);
		} catch (Exception e) {
			String message = "convert from json string fail " + clazz + " " + jsonString;
			logger.warn(message, e);
			throw new RuntimeException(message, e);
		}
	}

	public static Map<String, Object> toFieldMap(Object object) {
		return OBJECT_MAPPER.convertValue(object, STRING_OBJECT_REFERENCE);
	}

}
