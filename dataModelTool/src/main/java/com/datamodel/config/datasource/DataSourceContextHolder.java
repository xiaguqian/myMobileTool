package com.datamodel.config.datasource;

public class DataSourceContextHolder {

    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();

    public static final String PRIMARY_DATA_SOURCE = "primary";

    public static void setDataSource(String dataSourceName) {
        CONTEXT_HOLDER.set(dataSourceName);
    }

    public static String getDataSource() {
        return CONTEXT_HOLDER.get();
    }

    public static void clearDataSource() {
        CONTEXT_HOLDER.remove();
    }

    public static void switchToPrimary() {
        setDataSource(PRIMARY_DATA_SOURCE);
    }
}
