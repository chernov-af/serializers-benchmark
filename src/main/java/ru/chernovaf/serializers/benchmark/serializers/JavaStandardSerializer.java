package ru.chernovaf.serializers.benchmark.serializers;

import java.io.*;

public class JavaStandardSerializer implements Serializer {

    @Override
    public <T> byte[] serialize( T object ) throws IOException {
        try ( ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
              ObjectOutputStream out = new ObjectOutputStream( bytesOut ) ) {
            out.writeObject( object );
            return bytesOut.toByteArray();
        }
    }

    @Override
    public <T> T deserialize( byte[] bytes, Class<T> objectType ) throws IOException, ClassNotFoundException {
        try ( ByteArrayInputStream bytesIn = new ByteArrayInputStream( bytes );
              ObjectInputStream in = new ObjectInputStream( bytesIn ) ) {
            return ( T )in.readObject();
        }
    }
}
