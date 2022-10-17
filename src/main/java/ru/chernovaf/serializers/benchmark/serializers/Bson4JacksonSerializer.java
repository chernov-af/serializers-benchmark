package ru.chernovaf.serializers.benchmark.serializers;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.undercouch.bson4jackson.BsonFactory;
import de.undercouch.bson4jackson.BsonGenerator;
import de.undercouch.bson4jackson.BsonModule;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.databind.DeserializationFeature.*;
import static com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.NON_FINAL;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class Bson4JacksonSerializer implements Serializer {

    private static final String FULL_DATE_FORMAT_STRING = "yyyy-MM-dd'T'HH:mm:ss.SSSZZ";

    private static final ObjectMapper defaultMapper;
    private static final ObjectMapper systemMapper;

    static {
        BsonFactory factory = new BsonFactory();
        factory.enable( BsonGenerator.Feature.ENABLE_STREAMING );

        defaultMapper = new ObjectMapper( factory ).registerModule( new BsonModule() )
                                                   .configure( JsonParser.Feature.ALLOW_SINGLE_QUOTES, true )
                                                   .configure( SerializationFeature.FAIL_ON_EMPTY_BEANS, false )
                                                   .setDateFormat( new SimpleDateFormat( FULL_DATE_FORMAT_STRING ) )
                                                   .configure( SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false )
                                                   .setSerializationInclusion( NON_NULL )
                                                   .setVisibility( PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY )
                                                   .disable( FAIL_ON_UNKNOWN_PROPERTIES );

        systemMapper = new ObjectMapper( factory ).registerModule( new BsonModule() )
                                                  .enable(
                                                          ACCEPT_SINGLE_VALUE_AS_ARRAY,
                                                          ACCEPT_EMPTY_STRING_AS_NULL_OBJECT,
                                                          ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT,
                                                          READ_UNKNOWN_ENUM_VALUES_AS_NULL,
                                                          UNWRAP_SINGLE_VALUE_ARRAYS
                                                         )
                                                  .disable(
                                                          FAIL_ON_INVALID_SUBTYPE,
                                                          FAIL_ON_NULL_FOR_PRIMITIVES,
                                                          FAIL_ON_IGNORED_PROPERTIES,
                                                          FAIL_ON_UNKNOWN_PROPERTIES,
                                                          FAIL_ON_NUMBERS_FOR_ENUMS,
                                                          FAIL_ON_UNRESOLVED_OBJECT_IDS,
                                                          WRAP_EXCEPTIONS
                                                          )
                                                  .enable( MapperFeature.PROPAGATE_TRANSIENT_MARKER )
                                                  .enable( JsonParser.Feature.ALLOW_SINGLE_QUOTES )
                                                  .disable( SerializationFeature.FAIL_ON_EMPTY_BEANS )
                                                  .setDateFormat( new SimpleDateFormat( FULL_DATE_FORMAT_STRING ) )
                                                  .configure( SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false )
                                                  .setVisibility( PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE )
                                                  .setVisibility( PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY )
                                                  .enableDefaultTyping( NON_FINAL );
    }

    public static Bson4JacksonSerializer createDefault() {
        return new Bson4JacksonSerializer( defaultMapper );
    }

    public static Bson4JacksonSerializer createSystem() {
        return new Bson4JacksonSerializer( systemMapper );
    }

    private final ObjectMapper mapper;

    private Bson4JacksonSerializer( ObjectMapper mapper ) {
        this.mapper = mapper;
    }

    @Override
    public <T> byte[] serialize( T object ) throws JsonProcessingException {
        return mapper.writeValueAsBytes( object );
    }

    @Override
    public <T> T deserialize( byte[] bytes, Class<T> objectType ) throws IOException {
        return mapper.readValue( bytes, objectType );
    }
}
