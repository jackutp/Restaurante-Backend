package com.microservicio.solicitudes.db_config;

import org.jspecify.annotations.Nullable;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DataSourceRouter extends AbstractRoutingDataSource {
    protected @Nullable Object determineCurrentLookupKey() {
        return DataSourceContextHolder.getDatabase();
    }
}
