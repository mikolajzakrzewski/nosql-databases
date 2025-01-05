package edu.nbd.providers;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.mapper.MapperContext;
import com.datastax.oss.driver.api.mapper.entity.EntityHelper;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.relation.Relation;
import com.datastax.oss.driver.api.querybuilder.select.Select;
import edu.nbd.model.Bicycle;
import edu.nbd.model.MotorVehicle;
import edu.nbd.model.Vehicle;

public class VehicleProvider {

    private final CqlSession session;
    private final EntityHelper<Bicycle> bicycleEntityHelper;
    private final EntityHelper<MotorVehicle> motorVehicleEntityHelper;

    public VehicleProvider(MapperContext ctx, EntityHelper<Bicycle> bicycleEntityHelper, EntityHelper<MotorVehicle> motorVehicleEntityHelper) {
        this.session = ctx.getSession();
        this.bicycleEntityHelper = bicycleEntityHelper;
        this.motorVehicleEntityHelper = motorVehicleEntityHelper;
    }

    public Vehicle findById(String id) {
        Select selectVehicle = QueryBuilder
                .selectFrom(CqlIdentifier.fromCql("vehicles"))
                .all()
                .where(Relation.column(CqlIdentifier.fromCql("plate_number")).isEqualTo(QueryBuilder.literal(id)));

        Row row = session.execute(selectVehicle.build()).one();
        if (row == null) {
            throw new IllegalStateException("Vehicle with given id doesn't exist.");
        }

        String discriminator = row.getString(CqlIdentifier.fromCql("discriminator"));
        if (discriminator == null) {
            throw new IllegalStateException("Vehicle with given id doesn't have a discriminator.");
        }

        return switch (discriminator) {
            case "bicycle" -> getBicycle(row);
            case "motor_vehicle" -> getMotorVehicle(row);
            default -> throw new IllegalStateException();
        };
    }

    public void add(Vehicle vehicle) {
        session.execute(
                switch (vehicle.getDiscriminator()) {
                    case "bicycle" -> {
                        Bicycle bicycle = (Bicycle) vehicle;
                        yield session.prepare(bicycleEntityHelper.insert().build())
                                .bind()
                                .setString(CqlIdentifier.fromCql("plate_number"), bicycle.getPlateNumber())
                                .setInt(CqlIdentifier.fromCql("base_price"), bicycle.getBasePrice())
                                .setBoolean(CqlIdentifier.fromCql("archived"), bicycle.isArchived())
                                .setInt(CqlIdentifier.fromCql("rented"), bicycle.getRented())
                                .setString(CqlIdentifier.fromCql("discriminator"), bicycle.getDiscriminator());
                    }
                    case "motor_vehicle" -> {
                        MotorVehicle motorVehicle = (MotorVehicle) vehicle;
                        yield session.prepare(motorVehicleEntityHelper.insert().build())
                                .bind()
                                .setString(CqlIdentifier.fromCql("plate_number"), motorVehicle.getPlateNumber())
                                .setInt(CqlIdentifier.fromCql("base_price"), motorVehicle.getBasePrice())
                                .setBoolean(CqlIdentifier.fromCql("archived"), motorVehicle.isArchived())
                                .setInt(CqlIdentifier.fromCql("rented"), motorVehicle.getRented())
                                .setString(CqlIdentifier.fromCql("discriminator"), motorVehicle.getDiscriminator())
                                .setInt(CqlIdentifier.fromCql("engine_displacement"), motorVehicle.getEngineDisplacement());
                    }
                    default -> throw new IllegalStateException();
                }
        );
    }

    public void update(Vehicle vehicle) {
        session.execute(
                switch (vehicle.getDiscriminator()) {
                    case "bicycle" -> {
                        Bicycle bicycle = (Bicycle) vehicle;
                        yield session.prepare(bicycleEntityHelper.updateByPrimaryKey().build())
                                .bind()
                                .setString(CqlIdentifier.fromCql("plate_number"), bicycle.getPlateNumber())
                                .setInt(CqlIdentifier.fromCql("base_price"), bicycle.getBasePrice())
                                .setBoolean(CqlIdentifier.fromCql("archived"), bicycle.isArchived())
                                .setInt(CqlIdentifier.fromCql("rented"), bicycle.getRented())
                                .setString(CqlIdentifier.fromCql("discriminator"), bicycle.getDiscriminator());
                    }
                    case "motor_vehicle" -> {
                        MotorVehicle motorVehicle = (MotorVehicle) vehicle;
                        yield session.prepare(motorVehicleEntityHelper.updateByPrimaryKey().build())
                                .bind()
                                .setString(CqlIdentifier.fromCql("plate_number"), motorVehicle.getPlateNumber())
                                .setInt(CqlIdentifier.fromCql("base_price"), motorVehicle.getBasePrice())
                                .setBoolean(CqlIdentifier.fromCql("archived"), motorVehicle.isArchived())
                                .setInt(CqlIdentifier.fromCql("rented"), motorVehicle.getRented())
                                .setString(CqlIdentifier.fromCql("discriminator"), motorVehicle.getDiscriminator())
                                .setInt(CqlIdentifier.fromCql("engine_displacement"), motorVehicle.getEngineDisplacement());
                    }
                    default -> throw new IllegalStateException();
                }
        );
    }

    private Bicycle getBicycle(Row bicycleRow) {
        return new Bicycle(
                bicycleRow.getString(CqlIdentifier.fromCql("plate_number")),
                bicycleRow.getInt(CqlIdentifier.fromCql("base_price")),
                bicycleRow.getBoolean(CqlIdentifier.fromCql("archived")),
                bicycleRow.getInt(CqlIdentifier.fromCql("rented")),
                bicycleRow.getString(CqlIdentifier.fromCql("discriminator"))
        );
    }

    private MotorVehicle getMotorVehicle(Row motorVehicleRow) {
        return new MotorVehicle(
                motorVehicleRow.getString(CqlIdentifier.fromCql("plate_number")),
                motorVehicleRow.getInt(CqlIdentifier.fromCql("base_price")),
                motorVehicleRow.getBoolean(CqlIdentifier.fromCql("archived")),
                motorVehicleRow.getInt(CqlIdentifier.fromCql("rented")),
                motorVehicleRow.getString(CqlIdentifier.fromCql("discriminator")),
                motorVehicleRow.getInt(CqlIdentifier.fromCql("engine_displacement"))
        );
    }
}
