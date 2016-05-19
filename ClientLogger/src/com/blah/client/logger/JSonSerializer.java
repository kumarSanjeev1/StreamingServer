package com.blah.client.logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSonSerializer {

	public static String serializeToJson(ClientPayLoad data) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.writeValueAsString(data);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

}
