package ru.chernovaf.serializers.benchmark.serializers;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class Serializers {

    private static final Map<String, Serializer> serializers;

    static {
        Map<String, Serializer> map = new LinkedHashMap<>();
        map.put( "Java standard",        new JavaStandardSerializer() );
        map.put( "Jackson default",      JacksonSerializer.createDefault() );
        map.put( "Jackson system",       JacksonSerializer.createSystem() );
        map.put( "JacksonSmile default", JacksonSmileSerializer.createDefault() );
        map.put( "JacksonSmile system",  JacksonSmileSerializer.createSystem() );
        map.put( "Bson4Jackson default", Bson4JacksonSerializer.createDefault() );
        map.put( "Bson4Jackson system",  Bson4JacksonSerializer.createSystem() );
        map.put( "Bson MongoDb",         new BsonMongoDbSerializer() );
        map.put( "Kryo default",         KryoSerializer.createDefault() );
        map.put( "Kryo unsafe",          KryoSerializer.createUnsafe() );
        map.put( "FST default",          FstSerializer.createDefault() );
        map.put( "FST unsafe",           FstSerializer.createUnsafe() );
        map.put( "One-Nio default",      OneNioSerializer.createDefault() );
        map.put( "One-Nio for persist",  OneNioSerializer.createForPersist() );
        serializers = Collections.unmodifiableMap( map );
    }

    private Serializers() {
    }

    public static Map<String, Serializer> getMap() {
        return serializers;
    }
}
