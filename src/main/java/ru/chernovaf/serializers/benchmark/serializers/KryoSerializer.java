package ru.chernovaf.serializers.benchmark.serializers;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.io.UnsafeInput;
import com.esotericsoftware.kryo.io.UnsafeOutput;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.google.common.base.Function;
import org.objenesis.strategy.StdInstantiatorStrategy;

public class KryoSerializer implements Serializer {

    private static final Kryo KRYO = new Kryo();

    private final Function<Integer, Output> outputFactory;
    private final Function<byte[], Input> inputFactory;

    static {
        // Выключаем использование конструктора без параметров при десериализации
        KRYO.setInstantiatorStrategy( new Kryo.DefaultInstantiatorStrategy( new StdInstantiatorStrategy() ) );
        // Включаем обратную и прямую совместимости (и становится почти в 2 раза медленнее...)
        KRYO.setDefaultSerializer( CompatibleFieldSerializer.class );
    }

    public static KryoSerializer createDefault() {
        return new KryoSerializer( createOutputFactory(), createInputFactory() );
    }

    public static KryoSerializer createUnsafe() {
        return new KryoSerializer( createUnsafeOutputFactory(), createUnsafeInputFactory() );
    }

    private static Function<Integer, Output> createOutputFactory() {
        return new Function<Integer, Output>() {
            @Override public Output apply( Integer bufferSize ) {
                return new Output( bufferSize, -1 );
            }
        };
    }

    private static Function<Integer, Output> createUnsafeOutputFactory() {
        return new Function<Integer, Output>() {
            @Override public Output apply( Integer bufferSize ) {
                return new UnsafeOutput( bufferSize, -1 );
            }
        };
    }

    private static Function<byte[], Input> createInputFactory() {
        return new Function<byte[], Input>() {
            @Override public Input apply( byte[] buffer ) {
                return new Input( buffer );
            }
        };
    }

    private static Function<byte[], Input> createUnsafeInputFactory() {
        return new Function<byte[], Input>() {
            @Override public Input apply( byte[] buffer ) {
                return new UnsafeInput( buffer );
            }
        };
    }

    private KryoSerializer( Function<Integer, Output> outputFactory, Function<byte[], Input> inputFactory ) {
        this.outputFactory = outputFactory;
        this.inputFactory = inputFactory;
    }

    @Override
    public <T> byte[] serialize( T object ) {
        try ( Output out = outputFactory.apply( 4096 ) ) {
            KRYO.writeObject( out, object );
            out.close();
            return out.toBytes();
        }
    }

    @Override
    public <T> T deserialize( byte[] bytes, Class<T> objectType ) {
        try ( Input in = inputFactory.apply( bytes ) ) {
            return KRYO.readObject( in, objectType );
        }
    }
}
