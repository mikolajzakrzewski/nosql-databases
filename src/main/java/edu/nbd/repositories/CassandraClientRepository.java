package edu.nbd.repositories;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import edu.nbd.dao.ClientDao;
import edu.nbd.mappers.ClientMapper;
import edu.nbd.mappers.ClientMapperBuilder;
import edu.nbd.model.Client;

public class CassandraClientRepository extends AbstractCassandraRepository implements IRepository<Client> {

    private final ClientDao clientDao;

    public CassandraClientRepository() {
        createTable();
        ClientMapper clientMapper = new ClientMapperBuilder(getSession()).build();
        clientDao = clientMapper.clientDao();
    }

    public void createTable() {
        SimpleStatement createClients = SchemaBuilder.createTable(CqlIdentifier.fromCql("clients"))
                .ifNotExists()
                .withPartitionKey(CqlIdentifier.fromCql("personal_id"), DataTypes.TEXT)
                .withColumn(CqlIdentifier.fromCql("first_name"), DataTypes.TEXT)
                .withColumn(CqlIdentifier.fromCql("last_name"), DataTypes.TEXT)
                .withColumn(CqlIdentifier.fromCql("client_type"), DataTypes.TEXT)
                .withColumn(CqlIdentifier.fromCql("archived"), DataTypes.BOOLEAN)
                .withColumn(CqlIdentifier.fromCql("current_rents_number"), DataTypes.INT)
                .build();

        getSession().execute(createClients);
    }

    public void dropTable() {
        SimpleStatement dropClients = SchemaBuilder.dropTable(CqlIdentifier.fromCql("clients"))
                .ifExists()
                .build();

        getSession().execute(dropClients);
    }

    @Override
    public Client findById(Object id) {
        return clientDao.findById((String) id);
    }

    @Override
    public void add(Client obj) {
        clientDao.add(obj);
    }

    @Override
    public void update(Client obj) {
        clientDao.update(obj);
    }

    @Override
    public void delete(Client obj) {
        clientDao.delete(obj);
    }
}
