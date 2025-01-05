package edu.nbd.dao;

import com.datastax.oss.driver.api.mapper.annotations.*;
import edu.nbd.model.Client;

@Dao
public interface ClientDao {
    @Select
    Client findById(String personalID);

    @Insert
    void add(Client client);

    @Update
    void update(Client client);

    @Delete
    void delete(Client client);
}
