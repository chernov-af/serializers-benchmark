package ru.chernovaf.serializers.benchmark.serializers;

import java.io.IOException;

public interface Serializer {

    <T> byte[] serialize( T object ) throws IOException;

    <T> T deserialize( byte[] bytes, Class<T> objectType ) throws IOException, ClassNotFoundException;
}
