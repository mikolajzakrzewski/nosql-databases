package edu.nbd.cassandra;

import com.datastax.oss.driver.api.core.ProtocolVersion;
import com.datastax.oss.driver.api.core.type.DataType;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.core.type.codec.TypeCodecs;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class TimeCodec implements TypeCodec<LocalDateTime> {
    private final TypeCodec<Instant> instantCodec = TypeCodecs.TIMESTAMP;

    @NonNull
    @Override
    public GenericType<LocalDateTime> getJavaType() {
        return GenericType.LOCAL_DATE_TIME;
    }

    @NonNull
    @Override
    public DataType getCqlType() {
        return instantCodec.getCqlType();
    }

    @Nullable
    @Override
    public LocalDateTime decode(@Nullable ByteBuffer value, @NonNull ProtocolVersion protocolVersion) {
        Instant instant = instantCodec.decode(value, protocolVersion);
        if (instant == null) {
            return null;
        }
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    @Nullable
    @Override
    public ByteBuffer encode(@Nullable LocalDateTime localDateTime, @NonNull ProtocolVersion protocolVersion) {
        if (localDateTime == null) {
            return null;
        }
        return instantCodec.encode(localDateTime.atZone(ZoneId.systemDefault()).toInstant(), protocolVersion);
    }

    @NonNull
    @Override
    public String format(@Nullable LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return "NULL";
        }
        return instantCodec.format(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    @Nullable
    @Override
    public LocalDateTime parse(@Nullable String value) {
        Instant instant = instantCodec.parse(value);
        if (instant == null) {
            return null;
        }
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
}
