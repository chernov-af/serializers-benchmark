package ru.chernovaf.serializers.benchmark.serializers;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Supplier;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.fasterxml.jackson.annotation.PropertyAccessor.ALL;
import static com.fasterxml.jackson.annotation.PropertyAccessor.FIELD;
import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES;
import static com.fasterxml.jackson.databind.DeserializationFeature.*;
import static com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.NON_FINAL;
import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS;
import static com.google.common.base.Suppliers.memoize;

import java.text.SimpleDateFormat;

class SystemJsonMapperLocator {

    private static final String FULL_DATE_FORMAT_STRING = "yyyy-MM-dd'T'HH:mm:ss.SSSZZ";

	private final Supplier<ObjectMapper> holder = memoize(new Supplier<ObjectMapper>() {
		@Override
		public ObjectMapper get() {
			return new ObjectMapper()
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
					.enable(MapperFeature.PROPAGATE_TRANSIENT_MARKER)
					.enable(ALLOW_SINGLE_QUOTES)
					.disable(FAIL_ON_EMPTY_BEANS)
					.setDateFormat(new SimpleDateFormat(FULL_DATE_FORMAT_STRING))
					.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
					.setVisibility(ALL, NONE)
					.setVisibility(FIELD, ANY)
					.enableDefaultTyping(NON_FINAL);
		}
	});

	public ObjectMapper getMapper() {
		return holder.get();
	}
}
