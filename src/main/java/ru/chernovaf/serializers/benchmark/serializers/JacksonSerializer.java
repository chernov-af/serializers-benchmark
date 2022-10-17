package ru.chernovaf.serializers.benchmark.serializers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class JacksonSerializer implements Serializer {

    private static final Charset UTF_CHARSET = StandardCharsets.UTF_8;

    private final ObjectMapper mapper;

    public static JacksonSerializer createDefault() {
        return new JacksonSerializer( new DefaultJsonMapperLocator().getMapper() );
    }

    public static JacksonSerializer createSystem() {
        return new JacksonSerializer( new SystemJsonMapperLocator().getMapper() );
    }

    private JacksonSerializer( ObjectMapper mapper ) {
        this.mapper = mapper;
    }

    @Override
    public <T> byte[] serialize( T object ) throws JsonProcessingException {
        String json = mapper.writeValueAsString( object );
        return json.getBytes( UTF_CHARSET );
    }

    @Override
    public <T> T deserialize( byte[] bytes, Class<T> objectType ) throws IOException {
        String json = new String( bytes, UTF_CHARSET );
        return mapper.readValue( json, objectType );
    }
}
