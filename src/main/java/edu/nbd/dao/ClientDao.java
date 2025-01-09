package edu.nbd.dao;

import com.datastax.oss.driver.api.mapper.annotations.*;
import edu.nbd.model.Client;

@Dao
public interface ClientDao {
    @StatementAttributes(consistencyLevel = "ONE")
    @Select
    Client findById(String personalID);

    @StatementAttributes(consistencyLevel = "QUORUM")
    @Insert
    void add(Client client);

    @StatementAttributes(consistencyLevel = "QUORUM")
    @Update
    void update(Client client);

    @StatementAttributes(consistencyLevel = "QUORUM")
    @Delete
    void delete(Client client);
}
