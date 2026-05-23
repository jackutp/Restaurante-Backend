package com.microservicio.reservas.db_configs;

import com.microservicio.reservas.db_configs.DataSourceContextHolder;
import org.jspecify.annotations.Nullable;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DataSourceRouter extends AbstractRoutingDataSource {
    @Override
    protected @Nullable Object determineCurrentLookupKey() {
        return DataSourceContextHolder.getDatabase();
    }
}
