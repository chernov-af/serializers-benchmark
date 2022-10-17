package ru.chernovaf.serializers.benchmark.serializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Supplier;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.annotation.PropertyAccessor.FIELD;
import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS;
import static com.google.common.base.Suppliers.memoize;

import java.text.SimpleDateFormat;

class DefaultJsonMapperLocator {

    private static final String FULL_DATE_FORMAT_STRING = "yyyy-MM-dd'T'HH:mm:ss.SSSZZ";

    private final Supplier<ObjectMapper> holder = memoize(new Supplier<ObjectMapper>() {
        @Override
        public ObjectMapper get() {
            return new ObjectMapper()
                    .configure(ALLOW_SINGLE_QUOTES, true)
                    .configure(FAIL_ON_EMPTY_BEANS, false)
                    .setDateFormat(new SimpleDateFormat(FULL_DATE_FORMAT_STRING))
                    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                    .setSerializationInclusion(NON_NULL)
                    .setVisibility(FIELD, ANY)
                    .disable(FAIL_ON_UNKNOWN_PROPERTIES);
        }
    });

    public ObjectMapper getMapper() {
        return holder.get();
    }
}
