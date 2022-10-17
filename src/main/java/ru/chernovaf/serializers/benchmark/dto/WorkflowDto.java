package ru.chernovaf.serializers.benchmark.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.io.Serializable;
import java.util.Map;

public class WorkflowDto implements Serializable {

    private static final long serialVersionUID = -2756273362440449053L;

    public final String id;
    public final String subsystem;
    public final SectionDto publicData;
    public final Map<String, SectionDto> privateData;
    public final Map<String, Serializable> attributes;

    @JsonCreator
    @BsonCreator
    public WorkflowDto( @JsonProperty( "id" ) @BsonProperty( "id" ) String id,
                        @JsonProperty( "subsystem" ) @BsonProperty( "subsystem" ) String subsystem,
                        @JsonProperty( "publicData" ) @BsonProperty( "publicData" ) SectionDto publicData,
                        @JsonProperty( "privateData" ) @BsonProperty( "privateData" ) Map<String, SectionDto> privateData,
                        @JsonProperty( "attributes" ) @BsonProperty( "attributes" ) Map<String, Object> attributes ) {
        this.id = id;
        this.subsystem = subsystem;
        this.publicData = publicData;
        this.privateData = privateData;
        this.attributes = ( Map )attributes;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder( getClass().getSimpleName() );
        sb.append( String.format( "{%n" ) );
        sb.append( String.format( "\tid: %s,%n", id ) );
        sb.append( String.format( "\tsubsystem: %s,%n", subsystem ) );

        if ( publicData == null )
            sb.append( String.format( "\tpublicData: null,%n" ) );
        else {
            sb.append( String.format( "\tpublicData: {%n" ) );
            sb.append( String.format( "\t\tversion: %s,%n", publicData.version ) );
            sb.append( String.format( "\t\tdata: %s%n", getDataAsString( publicData ) ) );
            sb.append( String.format( "\t},%n" ) );
        }

        if ( privateData == null )
            sb.append( String.format( "\tprivateData: null,%n" ) );
        else {
            sb.append( String.format( "\tprivateData: {%n" ) );
            int i = 0;
            for ( Map.Entry<String, SectionDto> entry : privateData.entrySet() ) {
                sb.append( String.format( "\t\t{%n" ) );
                sb.append( String.format( "\t\t\tscopeId: %s,%n", entry.getKey() ) );
                sb.append( String.format( "\t\t\tversion: %s,%n", entry.getValue().version ) );
                sb.append( String.format( "\t\t\tdata: %s%n", getDataAsString( entry.getValue() ) ) );
                String commaOrEmpty = ( i < privateData.size() - 1 ? "," : "" );
                sb.append( String.format( "\t\t}" + commaOrEmpty + "%n" ) );
                ++i;
            }
            sb.append( String.format( "\t},%n" ) );
        }

        if ( attributes == null )
            sb.append( String.format( "\tattributes: null%n" ) );
        else {
            sb.append( String.format( "\tattributes: {%n" ) );
            int i = 0;
            for ( Map.Entry<String, Serializable> entry : attributes.entrySet() ) {
                sb.append( String.format( "\t\t%s: %s", entry.getKey(), entry.getValue() ) );
                String commaOrEmpty = ( i < attributes.size() - 1 ? "," : "" );
                sb.append( String.format( commaOrEmpty + "%n" ) );
                ++i;
            }
            sb.append( String.format( "\t}%n" ) );
        }

        return sb.append("}").toString();
    }

    private String getDataAsString( SectionDto section ) {
        if ( section.serializedData == null &&
             section.serializedDataV2 == null )
            return "null";

        if ( section.serializedData != null)
            return "size = " + section.serializedData.length();
        else
            return "size = " + section.serializedDataV2.length;
    }
}
