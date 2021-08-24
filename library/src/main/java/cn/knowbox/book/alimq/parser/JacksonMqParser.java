package cn.knowbox.book.alimq.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * jackson解析
 *
 * @author Created by gold on 2019/11/22 10:51
 */
public class JacksonMqParser implements MqParser {

    private final ObjectMapper mapper;

    public JacksonMqParser() {
        this.mapper = new ObjectMapper();
    }

    @Override
    public String format(Object value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public <T> T parse(String json, Type type) {
        try {
            JavaType javaType = TypeFactory.defaultInstance().constructType(type);

            return mapper.readValue(json, javaType);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
