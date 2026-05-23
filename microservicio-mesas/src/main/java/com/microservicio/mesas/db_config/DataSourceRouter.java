package com.microservicio.mesas.db_config;

import com.microservicio.mesas.db_config.DataSourceContextHolder;
import org.jspecify.annotations.Nullable;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DataSourceRouter extends AbstractRoutingDataSource {
    protected @Nullable Object determineCurrentLookupKey() {
        return DataSourceContextHolder.getDatabase();
    }
}
