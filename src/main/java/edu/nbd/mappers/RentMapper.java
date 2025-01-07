package edu.nbd.mappers;

import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace;
import com.datastax.oss.driver.api.mapper.annotations.DaoTable;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;
import edu.nbd.dao.RentDao;

@Mapper
public interface RentMapper {
    @DaoFactory
    RentDao rentDao(@DaoKeyspace String keyspace, @DaoTable String table);
    @DaoFactory
    RentDao rentDao();
}
