package controller.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monitorjbl.json.JsonViewModule;

public class JacksonObjectMapper extends ObjectMapper {

    private static final ObjectMapper MAPPER = new JacksonObjectMapper().registerModule(new JsonViewModule());

    public static ObjectMapper getMapper() {
        return MAPPER;
    }
}
