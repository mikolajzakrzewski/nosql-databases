package edu.nbd.providers;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BatchStatement;
import com.datastax.oss.driver.api.core.cql.BatchType;
import com.datastax.oss.driver.api.mapper.MapperContext;
import com.datastax.oss.driver.api.querybuilder.delete.Delete;
import com.datastax.oss.driver.api.querybuilder.insert.Insert;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.relation.Relation;
import com.datastax.oss.driver.api.querybuilder.select.Select;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.querybuilder.update.Update;
import edu.nbd.codecs.TimeCodec;
import edu.nbd.model.Rent;

import java.util.List;
import java.util.stream.Collectors;

public class RentProvider {
    private final CqlSession session;
    private final TimeCodec timeCodec = new TimeCodec();

    public static final CqlIdentifier RENTS_BY_CLIENT = CqlIdentifier.fromCql("rents_by_client");
    public static final CqlIdentifier RENTS_BY_VEHICLE = CqlIdentifier.fromCql("rents_by_vehicle");
    public static final CqlIdentifier RENT_ID = CqlIdentifier.fromCql("rent_id");
    public static final CqlIdentifier CLIENT_ID = CqlIdentifier.fromCql("client_id");
    public static final CqlIdentifier VEHICLE_ID = CqlIdentifier.fromCql("vehicle_id");
    public static final CqlIdentifier BEGIN_TIME = CqlIdentifier.fromCql("begin_time");
    public static final CqlIdentifier END_TIME = CqlIdentifier.fromCql("end_time");
    public static final CqlIdentifier RENT_COST = CqlIdentifier.fromCql("rent_cost");
    public static final CqlIdentifier ARCHIVED = CqlIdentifier.fromCql("archived");

    public RentProvider(MapperContext ctx) {
        this.session = ctx.getSession();
    }

    public void add(Rent rent) {
        Insert insertClient = QueryBuilder
                .insertInto(RENTS_BY_CLIENT)
                .value(RENT_ID, QueryBuilder.literal(rent.getRentId()))
                .value(CLIENT_ID, QueryBuilder.literal(rent.getClientId()))
                .value(VEHICLE_ID, QueryBuilder.literal(rent.getPlateNumber()))
                .value(BEGIN_TIME, QueryBuilder.literal(rent.getBeginTime(), timeCodec))
                .value(END_TIME, QueryBuilder.literal(rent.getEndTime(), timeCodec))
                .value(RENT_COST, QueryBuilder.literal(rent.getRentCost()))
                .value(ARCHIVED, QueryBuilder.literal(rent.isArchived()))
                .ifNotExists();

        session.execute(insertClient.build());

        Insert insertVehicle = QueryBuilder
                .insertInto(RENTS_BY_VEHICLE)
                .value(RENT_ID, QueryBuilder.literal(rent.getRentId()))
                .value(CLIENT_ID, QueryBuilder.literal(rent.getClientId()))
                .value(VEHICLE_ID, QueryBuilder.literal(rent.getPlateNumber()))
                .value(BEGIN_TIME, QueryBuilder.literal(rent.getBeginTime(), timeCodec))
                .value(END_TIME, QueryBuilder.literal(rent.getEndTime(), timeCodec))
                .value(RENT_COST, QueryBuilder.literal(rent.getRentCost()))
                .value(ARCHIVED, QueryBuilder.literal(rent.isArchived()))
                .ifNotExists();

        session.execute(insertVehicle.build());
    }

    public Rent findById(long id) {
        Select select = QueryBuilder
                .selectFrom(RENTS_BY_CLIENT)
                .all()
                .where(Relation.column(RENT_ID).isEqualTo(QueryBuilder.literal(id)));
        ResultSet rs = session.execute(select.build());
        List<Rent> rents = convertRowsToRents(rs);
        return rents.isEmpty() ? null : rents.getFirst();
    }

    public List<Rent> findByClientId(String clientId) {
        Select select = QueryBuilder
                .selectFrom(RENTS_BY_CLIENT)
                .all()
                .where(Relation.column(CLIENT_ID).isEqualTo(QueryBuilder.literal(clientId)));
        ResultSet rs = session.execute(select.build());
        return convertRowsToRents(rs);
    }

    public List<Rent> findByVehicleId(String vehicleId) {
        Select select = QueryBuilder
                .selectFrom(RENTS_BY_VEHICLE)
                .all()
                .where(Relation.column(VEHICLE_ID).isEqualTo(QueryBuilder.literal(vehicleId)));
        ResultSet rs = session.execute(select.build());
        return convertRowsToRents(rs);
    }

    public void update(Rent rent) {
        Update updateByClient = QueryBuilder
                .update(RENTS_BY_CLIENT)
                .setColumn(END_TIME, QueryBuilder.literal(rent.getEndTime(), timeCodec))
                .setColumn(RENT_COST, QueryBuilder.literal(rent.getRentCost()))
                .setColumn(ARCHIVED, QueryBuilder.literal(rent.isArchived()))
                .where(Relation.column(RENT_ID).isEqualTo(QueryBuilder.literal(rent.getRentId())))
                .where(Relation.column(CLIENT_ID).isEqualTo(QueryBuilder.literal(rent.getClientId())))
                .ifExists();

        Update updateByVehicle = QueryBuilder
                .update(RENTS_BY_VEHICLE)
                .setColumn(END_TIME, QueryBuilder.literal(rent.getEndTime(), timeCodec))
                .setColumn(RENT_COST, QueryBuilder.literal(rent.getRentCost()))
                .setColumn(ARCHIVED, QueryBuilder.literal(rent.isArchived()))
                .where(Relation.column(RENT_ID).isEqualTo(QueryBuilder.literal(rent.getRentId())))
                .where(Relation.column(VEHICLE_ID).isEqualTo(QueryBuilder.literal(rent.getPlateNumber())))
                .ifExists();

        BatchStatement batch = BatchStatement.builder(BatchType.LOGGED)
                .addStatement(updateByClient.build())
                .addStatement(updateByVehicle.build())
                .build();

        session.execute(batch);
    }

    public void delete(Rent rent) {
        Delete deleteClient = QueryBuilder
                .deleteFrom(RENTS_BY_CLIENT)
                .where(Relation.column(RENT_ID).isEqualTo(QueryBuilder.literal(rent.getRentId())))
                .where(Relation.column(CLIENT_ID).isEqualTo(QueryBuilder.literal(rent.getClientId())));

        Delete deleteVehicle = QueryBuilder
                .deleteFrom(RENTS_BY_VEHICLE)
                .where(Relation.column(RENT_ID).isEqualTo(QueryBuilder.literal(rent.getRentId())))
                .where(Relation.column(VEHICLE_ID).isEqualTo(QueryBuilder.literal(rent.getPlateNumber())));

        BatchStatement batch = BatchStatement.builder(BatchType.LOGGED)
                .addStatement(deleteClient.build())
                .addStatement(deleteVehicle.build())
                .build();

        session.execute(batch);
    }

    private List<Rent> convertRowsToRents(ResultSet rs) {
        return rs.all().stream()
                .map(row -> new Rent(
                        row.getLong(RENT_ID.asCql(true)),
                        row.getString(CLIENT_ID.asCql(true)),
                        row.getString(VEHICLE_ID.asCql(true)),
                        row.isNull(BEGIN_TIME.asCql(true)) ? null : row.get(BEGIN_TIME.asCql(true), timeCodec),
                        row.isNull(END_TIME.asCql(true)) ? null : row.get(END_TIME.asCql(true), timeCodec),
                        row.getDouble(RENT_COST.asCql(true)),
                        row.getBoolean(ARCHIVED.asCql(true))
                ))
                .collect(Collectors.toList());
    }
}
