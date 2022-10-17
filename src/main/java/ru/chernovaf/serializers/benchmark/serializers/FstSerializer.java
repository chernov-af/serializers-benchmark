package ru.chernovaf.serializers.benchmark.serializers;

import org.nustaq.serialization.FSTConfiguration;

public class FstSerializer implements Serializer {

    private final FSTConfiguration fst;

    public static FstSerializer createDefault() {
        return new FstSerializer( FSTConfiguration.createDefaultConfiguration() );
    }

    public static FstSerializer createUnsafe() {
        return new FstSerializer( FSTConfiguration.createUnsafeBinaryConfiguration() );
    }

    private FstSerializer( FSTConfiguration fst ) {
        this.fst = fst;
    }

    @Override
    public <T> byte[] serialize( T object ) {
        return fst.asByteArray( object );
    }

    @Override
    public <T> T deserialize( byte[] bytes, Class<T> objectType ) {
        return ( T )fst.asObject( bytes );
    }
}
