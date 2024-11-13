package edu.nbd.model;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.BsonType;

import java.util.Objects;

public class VehicleCodec implements Codec<Vehicle> {

    @Override
    public void encode(BsonWriter writer, Vehicle vehicle, EncoderContext encoderContext) {
        writer.writeStartDocument();

        if (vehicle instanceof Bicycle) {
            writer.writeString("_type", "bicycle");
        } else if (vehicle instanceof MotorVehicle) {
            writer.writeString("_type", "motorVehicle");
        }

        writer.writeString("_id", vehicle.getPlateNumber());
        writer.writeInt32("basePrice", vehicle.getBasePrice());
        writer.writeBoolean("archived", vehicle.isArchived());
        writer.writeInt32("rented", vehicle.getRented());

        if (vehicle instanceof MotorVehicle) {
            writer.writeInt32("engineDisplacement", ((MotorVehicle) vehicle).getEngineDisplacement());
        }

        writer.writeEndDocument();
    }

    @Override
    public Vehicle decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartDocument();

        String type = null;
        String plateNumber = null;
        int basePrice = 0;
        boolean archived = false;
        int rented = 0;
        int engineDisplacement = 0;

        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            String fieldName = reader.readName();
            if (fieldName.equals("_type")) {
                type = reader.readString();
            } else if (fieldName.equals("_id")) {
                plateNumber = reader.readString();
            } else if (fieldName.equals("basePrice")) {
                basePrice = reader.readInt32();
            } else if (fieldName.equals("archived")) {
                archived = reader.readBoolean();
            } else if (fieldName.equals("rented")) {
                rented = reader.readInt32();
            } else if (fieldName.equals("engineDisplacement") && Objects.equals(type, "motorVehicle")) {
                engineDisplacement = reader.readInt32();
            } else {
                reader.skipValue();
            }
        }

        reader.readEndDocument();

        if ("bicycle".equals(type)) {
            return new Bicycle(plateNumber, basePrice);
        } else if ("motorVehicle".equals(type)) {
            return new MotorVehicle(plateNumber, basePrice, engineDisplacement);
        } else {
            throw new IllegalArgumentException("Unsupported vehicle type: " + type);
        }
    }

    @Override
    public Class<Vehicle> getEncoderClass() {
        return Vehicle.class;
    }
}
