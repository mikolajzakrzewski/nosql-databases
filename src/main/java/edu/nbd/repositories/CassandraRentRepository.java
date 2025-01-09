package edu.nbd.repositories;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import edu.nbd.cassandra.CassandraRepository;
import edu.nbd.dao.RentDao;
import edu.nbd.mappers.RentMapper;
import edu.nbd.mappers.RentMapperBuilder;
import edu.nbd.model.Rent;

import java.util.List;

public class CassandraRentRepository extends CassandraRepository implements IRepository<Rent> {

    private final RentDao rentDao;

    public CassandraRentRepository() {
        createTables();
        RentMapper rentMapper = new RentMapperBuilder(getSession()).build();
        this.rentDao = rentMapper.rentDao();
    }

    public void createTables() {
        SimpleStatement createRentsByClient = SchemaBuilder.createTable(CqlIdentifier.fromCql("rents_by_client"))
                .ifNotExists()
                .withPartitionKey(CqlIdentifier.fromCql("personal_id"), DataTypes.TEXT)
                .withClusteringColumn(CqlIdentifier.fromCql("rent_id"), DataTypes.BIGINT)
                .withColumn(CqlIdentifier.fromCql("plate_number"), DataTypes.TEXT)
                .withColumn(CqlIdentifier.fromCql("begin_time"), DataTypes.TIMESTAMP)
                .withColumn(CqlIdentifier.fromCql("end_time"), DataTypes.TIMESTAMP)
                .withColumn(CqlIdentifier.fromCql("rent_cost"), DataTypes.DOUBLE)
                .withColumn(CqlIdentifier.fromCql("archived"), DataTypes.BOOLEAN)
                .build();

        SimpleStatement createRentsByVehicle = SchemaBuilder.createTable(CqlIdentifier.fromCql("rents_by_vehicle"))
                .ifNotExists()
                .withPartitionKey(CqlIdentifier.fromCql("plate_number"), DataTypes.TEXT)
                .withClusteringColumn(CqlIdentifier.fromCql("rent_id"), DataTypes.BIGINT)
                .withColumn(CqlIdentifier.fromCql("personal_id"), DataTypes.TEXT)
                .withColumn(CqlIdentifier.fromCql("begin_time"), DataTypes.TIMESTAMP)
                .withColumn(CqlIdentifier.fromCql("end_time"), DataTypes.TIMESTAMP)
                .withColumn(CqlIdentifier.fromCql("rent_cost"), DataTypes.DOUBLE)
                .withColumn(CqlIdentifier.fromCql("archived"), DataTypes.BOOLEAN)
                .build();

        getSession().execute(createRentsByClient);
        getSession().execute(createRentsByVehicle);
    }

    public void dropTables() {
        SimpleStatement dropRentsByClient = SchemaBuilder.dropTable(CqlIdentifier.fromCql("rents_by_client"))
                .ifExists()
                .build();

        SimpleStatement dropRentsByVehicle = SchemaBuilder.dropTable(CqlIdentifier.fromCql("rents_by_vehicle"))
                .ifExists()
                .build();

        getSession().execute(dropRentsByClient);
        getSession().execute(dropRentsByVehicle);
    }

    public List<Rent> findByClientId(String clientId) {
        return rentDao.findByClientId(clientId);
    }

    public List<Rent> findByVehicleId(String vehicleId) {
        return rentDao.findByVehicleId(vehicleId);
    }

    @Override
    public Rent findById(Object id) {
        return rentDao.findById((long) id);
    }

    @Override
    public void add(Rent obj) {
        rentDao.add(obj);
    }

    @Override
    public void update(Rent obj) {
        rentDao.update(obj);
    }

    @Override
    public void delete(Rent obj) {
        rentDao.delete(obj);
    }
}
