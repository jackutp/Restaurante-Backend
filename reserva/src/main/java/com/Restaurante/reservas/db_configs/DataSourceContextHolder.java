package com.Restaurante.reservas.db_configs;

import org.springframework.util.Assert;

public class DataSourceContextHolder {
    private static final ThreadLocal<DatabaseType> CONTEXT = new ThreadLocal<>();

    public static void set(DatabaseType type){
        Assert.notNull(type, "Database type can't be null >:C");
        CONTEXT.set(type);
    }
    public static DatabaseType getDatabase(){
        return CONTEXT.get();
    }

    public static void clear(){
        CONTEXT.remove();
    }
}
