package ru.chernovaf.serializers.benchmark.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.io.Serializable;

public class SectionDto implements Serializable {

    private static final long serialVersionUID = -8931738363258793595L;

    public final Long version;
    public final String serializedData;    // взаимоисключающие поля ...
    public final byte[] serializedDataV2;  // ... используемые в разных версиях

    @JsonCreator
    public SectionDto( @JsonProperty( "version" ) Long version,
                       @JsonProperty( "serializedData" ) String serializedData ) {
        this.version = version;
        this.serializedData = serializedData;
        this.serializedDataV2 = null;
    }

    @BsonCreator
    public SectionDto( @BsonProperty( "version" ) Long version,
                       @BsonProperty( "serializedDataV2" ) byte[] serializedDataV2 ) {
        this.version = version;
        this.serializedData = null;
        this.serializedDataV2 = serializedDataV2;
    }
}
