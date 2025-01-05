package edu.nbd.mappers;

import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace;
import com.datastax.oss.driver.api.mapper.annotations.DaoTable;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;
import edu.nbd.dao.VehicleDao;

@Mapper
public interface VehicleMapper {
    @DaoFactory
    VehicleDao vehicleDao(@DaoKeyspace String keyspace, @DaoTable String table);

    @DaoFactory
    VehicleDao vehicleDao();
}
