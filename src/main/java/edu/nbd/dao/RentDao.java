package edu.nbd.dao;

import com.datastax.oss.driver.api.mapper.annotations.*;
import edu.nbd.model.Rent;

@Dao
public interface RentDao {
    @Select
    Rent findRentById(long rentId);
    @Insert
    void add(Rent rent);
    @Update
    void update(Rent rent);
    @Delete
    void delete(Rent rent);
}
