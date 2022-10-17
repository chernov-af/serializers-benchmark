package ru.chernovaf.serializers.benchmark.serializers;

import org.bson.*;
import org.bson.codecs.*;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.io.BasicOutputBuffer;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Map;

public class BsonMongoDbSerializer implements Serializer {

    private final static CodecRegistry codecs;
    private final static EncoderContext encoderContext;
    private final static DecoderContext decoderContext;

    static {
        PojoCodecProvider pojoCodecProvider = PojoCodecProvider.builder()
                                                          .automatic( true )
                                                          .build();

        codecs = CodecRegistries.fromRegistries( CodecRegistries.fromProviders( new ValueCodecProvider(),
                                                                                new MapCodecProvider(),
                                                                                new CollectionCodecProvider(),
                                                                                pojoCodecProvider ) );  // should be last
        encoderContext = EncoderContext.builder().build();
        decoderContext = DecoderContext.builder().build();
    }

    private static class CollectionCodecProvider implements CodecProvider {

        private Codec<Collection<Object>> collectionCodec;

        @Override
        public <T> Codec<T> get( Class<T> clazz, CodecRegistry registry ) {
            if ( Collection.class.isAssignableFrom( clazz ) ) {
                if ( collectionCodec == null )
                    collectionCodec = new CollectionCodec( registry );
                return ( Codec<T> )collectionCodec;
            }
            return null;
        }
    }

    private static class CollectionCodec implements Codec<Collection<Object>> {

        private final IterableCodec iterableCodec;

        public CollectionCodec( CodecRegistry registry ) {
            this.iterableCodec = new IterableCodec( registry, new BsonTypeClassMap() );
        }

        @Override
        public void encode( final BsonWriter writer, final Collection<Object> value, final EncoderContext encoderContext ) {
            writer.writeStartDocument();
            writer.writeName( "col" );
            iterableCodec.encode( writer, value, encoderContext );
            writer.writeEndDocument();
        }

        @Override
        public Collection<Object> decode( final BsonReader reader, final DecoderContext decoderContext ) {
            reader.readStartDocument();
            if ( !reader.readName().equals( "col" ) )
                throw new BsonInvalidOperationException( "It was expected Collection to be deserialized, but it is not" );
            Collection<Object> result = ( Collection )iterableCodec.decode( reader, decoderContext );
            reader.readEndDocument();
            return result;
        }

        @Override
        public Class<Collection<Object>> getEncoderClass() {
            return ( Class )Collection.class;
        }
    }

    private static class MapCodecProvider implements CodecProvider {

        private MapCodec mapCodec;

        @Override
        public <T> Codec<T> get( Class<T> clazz, CodecRegistry registry ) {
            if ( Map.class.isAssignableFrom( clazz ) ) {
                if ( mapCodec == null )
                    mapCodec = new MapCodec( registry );
                return ( Codec<T> )mapCodec;
            }
            return null;
        }
    }

    @Override
    public <T> byte[] serialize( T object ) {
        try ( BasicOutputBuffer buffer = new BasicOutputBuffer() ) {
            BsonWriter writer = new BsonBinaryWriter( buffer );

            Codec<T> codec = codecs.get( ( Class<T> )object.getClass() );
            codec.encode( writer, object, encoderContext );

            return buffer.toByteArray();
        }
    }

    @Override
    public <T> T deserialize( byte[] bytes, Class<T> objectType ) {
        try ( BsonReader reader = new BsonBinaryReader( ByteBuffer.wrap( bytes ) ) ) {
            Codec<T> codec = codecs.get( objectType );
            return codec.decode( reader, decoderContext );
        }
    }
}
