package com.microservicio.mesas.db_config;

import com.microservicio.mesas.db_config.DataSourceRouter;
import com.microservicio.mesas.db_config.DatabaseType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfiguration {
    @Bean(name = "writeDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.write")
    public DataSource writeDataSource(){
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "readDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.read")
    public DataSource readDataSource(){
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean
    public DataSource routingDataSource(
            @Qualifier("writeDataSource")
            DataSource writeDataSource,
            @Qualifier("readDataSource")
            DataSource readDataSource
    ){
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DatabaseType.WRITE, writeDataSource);
        targetDataSources.put(DatabaseType.READ, readDataSource);
        DataSourceRouter routingDataSource = new DataSourceRouter();
        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.setDefaultTargetDataSource(writeDataSource);
        return routingDataSource;
    }
}
