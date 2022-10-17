package ru.chernovaf.serializers.benchmark.dto;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class DtoFactory {

    public static final String TEST_SUBSYSTEM = "testSubsystemCode";
    public static final String SESSION_ID = "session0624c6ab-a455-4625-9126-e04e94c6c1b6";
    public static final String PROCESS_ID = "pidc9c8a90c-67e4-47b5-b80f-6b3787b81a2d";

    /**
     * Данные из реальной секции потребителя, взятые из логов, сериализованные в byte[] алгоритмом "Json with zip",
     * а затем в строку алгоритмом "Json default" (при этом нетекстовые символы были экранированы в Unicode).
     */
    private static final String BUSINESS_DATA_FRAGMENT = "xRMk\u00021\u0010ý+³\u0006ñ\u001boÅ\u0015.öPÙÃìn´)»d\u0005)µôÔcÿ\u001e¤-ú\u0017²ÿ¨Y\u0015k\u000bÅ\u001c¼÷f2y}t\u000f#À¢>¾\u0000y×\u0005²\u000fÈºjß´;ÍV³ê}$\",\u001d1ÀÑ@bî\u001a0\u0011`IÜHP5Æ£\u0002ö\u0014Ã #ê!\u0000'\" RR\u0016ÊDáw\u001eQ(ÛG]\baH¬\u001f$Þf.\r\u0019Ô&qæ\u00054´¨ÏÔ-\u000b´ $>Ê¦fîãSgûÔME=â½L\u0003\u0014ølhîN(Ú1Ô±z[T\u0012Ñ°\u0017qÎÚ¦Rp\u001a,àf¡JKüÛâî]§ÓÚæ{ ãáHÏ_\u001c\u0006ÂKfiÛY4\u0014,âÿ*¥Y[HâSê¡:ªU%§\u0002C<ÈÊ57ç¸^!W)½|±JòÄ-vC\bAë×x\u001a?éµYó^ÇS½Ð\u001fúSÏõ»^Æ/ñsfsü2é¥^ÄñÌ@æñL¯Ì¾ÒohbÛöÄ¬o\u001få]&";
    private static final Charset UTF_CHARSET = StandardCharsets.UTF_8;

    private DtoFactory() {
    }

    public static SectionDto createSectionDto( int size ) {
        byte[] publicData = generateRealData( size );
        return new SectionDto( 0L, publicData );
    }

    public static WorkflowDto createWorkflowDto( int size ) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put( "sessionId", SESSION_ID );

        return new WorkflowDto( PROCESS_ID, TEST_SUBSYSTEM,
                                createSectionDto( size ), null,
                                attributes );
    }

    private static byte[] generateRealData( int size ) {
        byte[] bytes = new byte[size];

        // Заполним буфер байтами из реального фрагмента бизнес-данных
        byte[] realBusinessData = BUSINESS_DATA_FRAGMENT.getBytes( UTF_CHARSET );
        int alreadyWritten = 0;
        for ( int newEdge = realBusinessData.length; alreadyWritten < size; newEdge += realBusinessData.length ) {
            int needToWrite = newEdge - alreadyWritten;
            int possibleToWrite = size - alreadyWritten;
            int writeLength = Math.min( needToWrite, possibleToWrite );
            System.arraycopy( realBusinessData, 0,
                              bytes, alreadyWritten, writeLength );
            alreadyWritten += writeLength;
        }

        return bytes;
    }
}
