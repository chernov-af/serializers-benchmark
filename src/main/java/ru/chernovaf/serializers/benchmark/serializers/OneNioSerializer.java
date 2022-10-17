package ru.chernovaf.serializers.benchmark.serializers;

import one.nio.serial.*;

import java.io.IOException;

public class OneNioSerializer implements Serializer {

    private final boolean needPlaceSerializerInStream;

    static {
        // Чтобы коллекции и Map-ы десериализовывались, даже если конкретных Collection- и Map-классов нет в classpath.
        Repository.setOptions( Repository.COLLECTION_STUBS | Repository.MAP_STUBS );
    }

    public static OneNioSerializer createDefault() {
        return new OneNioSerializer( false );
    }

    public static OneNioSerializer createForPersist() {
        return new OneNioSerializer( true );
    }

    private OneNioSerializer( boolean needPlaceSerializerInStream ) {
        this.needPlaceSerializerInStream = needPlaceSerializerInStream;
    }

    @Override
    public <T> byte[] serialize( T object ) throws IOException {
        if ( !needPlaceSerializerInStream )
            return serializeOnlyObject( object, calcBufferSizeForOnlyObject( object ) );
        else
            return serializeForPersist( object );
    }

    private <T> int calcBufferSizeForOnlyObject( T object ) throws IOException {
        CalcSizeStream css = new CalcSizeStream();
        css.writeObject(object);
        return css.count();
    }

    private <T> byte[] serializeOnlyObject( T object, int bufferSize ) throws IOException {
        byte[] bufForOnlyObject = new byte[bufferSize];
        SerializeStream out = new SerializeStream( bufForOnlyObject );
        out.writeObject( object );
        // assert out.count() == bufferSize;
        return bufForOnlyObject;
    }

    public <T> byte[] serializeForPersist( T object ) throws IOException {
        int bufferSize = getBufferSizeIfScalar( object );
        if ( bufferSize == 0 )
            bufferSize = increaseBufferSizeForMeta( calcBufferSizeForOnlyObject( object ) );
        return serializeObjectAndItsMeta( object, bufferSize );
    }

    /**
     * @return Размер буффера, необходимый для сериализации объекта {@code object}, используя One-Nio.
     *         Для неизвестного размера возвращается 0.
     */
    public <T> int getBufferSizeIfScalar( T object ) {
        if ( object == null )         return 1;
        Class<?> cls = object.getClass();
        if ( cls == Boolean.class )   return 2;
        if ( cls == Byte.class )      return 2;
        if ( cls == Short.class )     return 3;
        if ( cls == Character.class ) return 3;
        if ( cls == Integer.class )   return 5;
        if ( cls == Long.class )      return 9;
        if ( cls == Float.class )     return 5;
        if ( cls == Double.class )    return 9;
        if ( cls == byte[].class )    return 5 + (( byte[] )object).length;
        return 0;
    }

    private int increaseBufferSizeForMeta( int bufSizeForOnlyObject ) {
        if ( bufSizeForOnlyObject < 200 )         return 500;  // minimum 500 B
        if ( bufSizeForOnlyObject < 500 )         return bufSizeForOnlyObject * 4;
        else if ( bufSizeForOnlyObject < 1000 )   return bufSizeForOnlyObject * 3;
        else if ( bufSizeForOnlyObject < 5000 )   return bufSizeForOnlyObject * 2;
        else if ( bufSizeForOnlyObject < 10000 )  return bufSizeForOnlyObject + bufSizeForOnlyObject / 2;   // + 50%
        else if ( bufSizeForOnlyObject < 50000 )  return bufSizeForOnlyObject + bufSizeForOnlyObject / 5;   // + 25%
        else if ( bufSizeForOnlyObject < 100000 ) return bufSizeForOnlyObject + bufSizeForOnlyObject / 10;  // + 10%
        else                              return bufSizeForOnlyObject + bufSizeForOnlyObject / 20;  // +  5%
    }

    private <T> byte[] serializeObjectAndItsMeta( T object, int bufferSize ) throws IOException {
        byte[] bufForObjectAndMeta = new byte[bufferSize];
        PersistStream out = new PersistStream( bufForObjectAndMeta );
        out.writeObject(object);
        return out.toByteArray();  // buffer with exact length
    }

    @Override
    public <T> T deserialize( byte[] bytes, Class<T> objectType ) throws IOException, ClassNotFoundException {
        DeserializeStream stream = new DeserializeStream( bytes );
        return ( T )stream.readObject();
    }
}
