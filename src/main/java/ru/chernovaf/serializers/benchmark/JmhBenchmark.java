package ru.chernovaf.serializers.benchmark;

import ru.chernovaf.serializers.benchmark.dto.DtoFactory;
import ru.chernovaf.serializers.benchmark.serializers.Serializer;
import ru.chernovaf.serializers.benchmark.serializers.Serializers;

import one.nio.serial.Repository;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;

public class JmhBenchmark {

    @State( Scope.Benchmark )
    public static class Parameters {

        private int prevOneNioOptions;

        @Param( {
            "Java standard",
            "Jackson default",
            "Jackson system",
            "JacksonSmile default",
            "JacksonSmile system",
            "Bson4Jackson default",
            "Bson4Jackson system",
            "Bson MongoDb",
            "Kryo default",
            "Kryo unsafe",
            "FST default",
            "FST unsafe",
            "One-Nio default",
            "One-Nio for persist"
        } )
        public String serializer;
        public Serializer serializerInstance;

        // For debug
        @Param( { "0", "100", "1000" } )
        // Toward 1 КБ
        // @Param( { "0", "100", "200", "300", "400", "500", "600", "700", "800", "900", "1000" } )
        // Toward 10 КБ
        // @Param( { "0", "100", "200", "300", "400", "500", "600", "700", "800", "900", "1000", "1100", "1200", "1300", "1400", "1500", "1600", "1700", "1800", "1900", "2000", "2100", "2200", "2300", "2400", "2500", "2600", "2700", "2800", "2900", "3000", "3100", "3200", "3300", "3400", "3500", "3600", "3700", "3800", "3900", "4000", "4100", "4200", "4300", "4400", "4500", "4600", "4700", "4800", "4900", "5000", "5100", "5200", "5300", "5400", "5500", "5600", "5700", "5800", "5900", "6000", "6100", "6200", "6300", "6400", "6500", "6600", "6700", "6800", "6900", "7000", "7100", "7200", "7300", "7400", "7500", "7600", "7700", "7800", "7900", "8000", "8100", "8200", "8300", "8400", "8500", "8600", "8700", "8800", "8900", "9000", "9100", "9200", "9300", "9400", "9500", "9600", "9700", "9800", "9900", "10000" } )
        // Toward 1 MB
        // @Param( { "0", "100", "200", "300", "400", "500", "600", "700", "800", "900", "1000", "1100", "1200", "1300", "1400", "1500", "1600", "1700", "1800", "1900", "2000", "2100", "2200", "2300", "2400", "2500", "2600", "2700", "2800", "2900", "3000", "3100", "3200", "3300", "3400", "3500", "3600", "3700", "3800", "3900", "4000", "4100", "4200", "4300", "4400", "4500", "4600", "4700", "4800", "4900", "5000", "5100", "5200", "5300", "5400", "5500", "5600", "5700", "5800", "5900", "6000", "6100", "6200", "6300", "6400", "6500", "6600", "6700", "6800", "6900", "7000", "7100", "7200", "7300", "7400", "7500", "7600", "7700", "7800", "7900", "8000", "8100", "8200", "8300", "8400", "8500", "8600", "8700", "8800", "8900", "9000", "9100", "9200", "9300", "9400", "9500", "9600", "9700", "9800", "9900", "10000", "11000", "12000", "13000", "14000", "15000", "16000", "17000", "18000", "19000", "20000", "21000", "22000", "23000", "24000", "25000", "26000", "27000", "28000", "29000", "30000", "31000", "32000", "33000", "34000", "35000", "36000", "37000", "38000", "39000", "40000", "41000", "42000", "43000", "44000", "45000", "46000", "47000", "48000", "49000", "50000", "51000", "52000", "53000", "54000", "55000", "56000", "57000", "58000", "59000", "60000", "61000", "62000", "63000", "64000", "65000", "66000", "67000", "68000", "69000", "70000", "71000", "72000", "73000", "74000", "75000", "76000", "77000", "78000", "79000", "80000", "81000", "82000", "83000", "84000", "85000", "86000", "87000", "88000", "89000", "90000", "91000", "92000", "93000", "94000", "95000", "96000", "97000", "98000", "99000", "100000", "110000", "120000", "130000", "140000", "150000", "160000", "170000", "180000", "190000", "200000", "210000", "220000", "230000", "240000", "250000", "260000", "270000", "280000", "290000", "300000", "310000", "320000", "330000", "340000", "350000", "360000", "370000", "380000", "390000", "400000", "410000", "420000", "430000", "440000", "450000", "460000", "470000", "480000", "490000", "500000", "510000", "520000", "530000", "540000", "550000", "560000", "570000", "580000", "590000", "600000", "610000", "620000", "630000", "640000", "650000", "660000", "670000", "680000", "690000", "700000", "710000", "720000", "730000", "740000", "750000", "760000", "770000", "780000", "790000", "800000", "810000", "820000", "830000", "840000", "850000", "860000", "870000", "880000", "890000", "900000", "910000", "920000", "930000", "940000", "950000", "960000", "970000", "980000", "990000", "1000000" } )
        public int sizeOfDto;
        public Object dtoInstance;
        public byte[] serializedDto;

        @Setup( Level.Trial )
        public void setup() throws IOException {
            prevOneNioOptions = Repository.getOptions();
            Repository.setOptions( Repository.COLLECTION_STUBS | Repository.MAP_STUBS );

            serializerInstance = Serializers.getMap().get( serializer );

            dtoInstance = DtoFactory.createWorkflowDto( sizeOfDto );
            serializedDto = serializerInstance.serialize( dtoInstance );
        }

        @TearDown( Level.Trial )
        public void tearDown() {
            Repository.setOptions( prevOneNioOptions );

            serializerInstance = null;

            dtoInstance = null;
            serializedDto = null;
        }
    }

    @Benchmark
    public byte[] serialization( Parameters parameters ) throws IOException {
        return parameters.serializerInstance.serialize( parameters.dtoInstance );
    }

    @Benchmark
    public Object unserialization( Parameters parameters ) throws IOException, ClassNotFoundException {
        return parameters.serializerInstance.deserialize( parameters.serializedDto,
                                                          parameters.dtoInstance.getClass() );
    }
}
