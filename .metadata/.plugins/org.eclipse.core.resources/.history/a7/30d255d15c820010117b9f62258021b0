package com.dmart.oms.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
//common/util/JsonUtils.java
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {
	
	private static final ObjectMapper mapper = new ObjectMapper();

	public static String toJson(Object obj) {
		try {
			return mapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Serialization failed", e);
		}
	}
}
