package edu.nbd.codecs;

import com.datastax.oss.driver.api.core.ProtocolVersion;
import com.datastax.oss.driver.api.core.type.DataType;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import edu.nbd.model.ClientType;
import edu.nbd.model.Default;
import edu.nbd.model.Gold;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ClientTypeCodec implements TypeCodec<ClientType> {

    @NonNull
    @Override
    public GenericType<ClientType> getJavaType() {
        return GenericType.of(ClientType.class);
    }

    @NonNull
    @Override
    public DataType getCqlType() {
        return DataTypes.TEXT;
    }

    @Nullable
    @Override
    public ByteBuffer encode(@Nullable ClientType clientType, @NonNull ProtocolVersion protocolVersion) {
        if (clientType == null) {
            return null;
        }
        String data = clientType.getClass().getSimpleName();
        return ByteBuffer.wrap(data.getBytes(StandardCharsets.UTF_8));
    }

    @Nullable
    @Override
    public ClientType decode(@Nullable ByteBuffer byteBuffer, @NonNull ProtocolVersion protocolVersion) {
        if (byteBuffer == null) {
            return null;
        }

        String clientType = new String(byteBuffer.array(), StandardCharsets.UTF_8);

        return switch (clientType) {
            case "Default" -> new Default();
            case "Gold" -> new Gold();
            default -> throw new IllegalArgumentException("Unknown ClientType: " + clientType);
        };
    }

    @NonNull
    @Override
    public String format(@Nullable ClientType clientType) {
        if (clientType == null) {
            return "NULL";
        }
        return clientType.getClass().getSimpleName() + ";" + clientType.getMaxVehicles() + ";" + clientType.getDiscount();
    }

    @Nullable
    @Override
    public ClientType parse(@Nullable String clientType) {
        if (clientType == null || clientType.isEmpty() || clientType.equalsIgnoreCase("NULL")) {
            return null;
        }

        return switch (clientType) {
            case "Default" -> new Default();
            case "Gold" -> new Gold();
            default -> throw new IllegalArgumentException("Unknown ClientType: " + clientType);
        };
    }
}
