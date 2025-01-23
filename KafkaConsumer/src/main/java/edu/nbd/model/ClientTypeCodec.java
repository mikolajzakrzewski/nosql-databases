package edu.nbd.model;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.BsonType;

public class ClientTypeCodec implements Codec<ClientType> {

    @Override
    public void encode(BsonWriter writer, ClientType clientType, EncoderContext encoderContext) {
        writer.writeStartDocument();

        if (clientType instanceof Default) {
            writer.writeString("_type", "default");
        } else if (clientType instanceof Gold) {
            writer.writeString("_type", "gold");
        }

        writer.writeInt32("maxVehicles", clientType.getMaxVehicles());
        writer.writeInt32("discount", clientType.getDiscount());

        writer.writeEndDocument();
    }

    @Override
    public ClientType decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartDocument();

        String type = null;

        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            String fieldName = reader.readName();
            if (fieldName.equals("_type")) {
                type = reader.readString();
            } else {
                reader.skipValue();
            }
        }

        reader.readEndDocument();

        if ("default".equals(type)) {
            return new Default();
        } else if ("gold".equals(type)) {
            return new Gold();
        } else {
            throw new IllegalArgumentException("Unsupported client type: " + type);
        }
    }

    @Override
    public Class<ClientType> getEncoderClass() {
        return ClientType.class;
    }
}
