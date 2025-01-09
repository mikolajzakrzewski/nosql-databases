package edu.nbd.repositories;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import edu.nbd.cassandra.CassandraRepository;
import edu.nbd.dao.VehicleDao;
import edu.nbd.mappers.VehicleMapper;
import edu.nbd.mappers.VehicleMapperBuilder;
import edu.nbd.model.Vehicle;

public class CassandraVehicleRepository extends CassandraRepository implements IRepository<Vehicle> {

    private final VehicleDao vehicleDao;

    public CassandraVehicleRepository() {
        createTable();
        VehicleMapper vehicleMapper = new VehicleMapperBuilder(getSession()).build();
        this.vehicleDao = vehicleMapper.vehicleDao();
    }

    public void createTable() {
        SimpleStatement createVehicles = SchemaBuilder.createTable(CqlIdentifier.fromCql("vehicles"))
                .ifNotExists()
                .withPartitionKey(CqlIdentifier.fromCql("plate_number"), DataTypes.TEXT)
                .withColumn(CqlIdentifier.fromCql("base_price"), DataTypes.INT)
                .withColumn(CqlIdentifier.fromCql("archived"), DataTypes.BOOLEAN)
                .withColumn(CqlIdentifier.fromCql("rented"), DataTypes.INT)
                .withColumn(CqlIdentifier.fromCql("discriminator"), DataTypes.TEXT)
                .withColumn(CqlIdentifier.fromCql("engine_displacement"), DataTypes.INT)
                .build();

        getSession().execute(createVehicles);
    }

    public void dropTable() {
        SimpleStatement dropVehicles = SchemaBuilder.dropTable(CqlIdentifier.fromCql("vehicles"))
                .ifExists()
                .build();

        getSession().execute(dropVehicles);
    }

    @Override
    public Vehicle findById(Object id) {
        return vehicleDao.findById((String) id);
    }

    @Override
    public void add(Vehicle obj) {
        vehicleDao.add(obj);
    }

    @Override
    public void update(Vehicle obj) {
        vehicleDao.update(obj);
    }

    @Override
    public void delete(Vehicle obj) {
        vehicleDao.delete(obj);
    }
}
