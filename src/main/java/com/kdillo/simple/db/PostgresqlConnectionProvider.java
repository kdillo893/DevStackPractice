package com.kdillo.simple.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class PostgresqlConnectionProvider {

    private final String dbUser;
    private final String dbPassword;
    private final String dbName;
    private final String dbSchema;
    private final String dbHost;
    private final int dbPort;

    public PostgresqlConnectionProvider(Properties props) {
        //load dbProps from the application properties (db.X) passed.

        this.dbUser = props.getProperty("db.user");
        this.dbPassword = props.getProperty("db.password");
        this.dbName = props.getProperty("db.name");
        this.dbSchema = props.getProperty("db.schema");
        this.dbHost = props.getProperty("db.host");
        this.dbPort = props.getProperty("db.port") != null ?
            Integer.parseInt(props.getProperty("db.port")) :
            5432;

    }

    public Connection getConnection() throws SQLException, ClassNotFoundException {
        
        //ensure the postgresql driver is here:
        Class.forName("org.postgresql.Driver");

        Properties dbProps = new Properties();
        dbProps.setProperty("user", dbUser);
        dbProps.setProperty("password", dbPassword);

        String url = "jdbc:postgresql://" + dbHost + ":" + dbPort + "/"
                + dbName + "?currentSchema=" + dbSchema;

        Class.forName("org.postgresql.Driver");
        //return connector, use the db properties
        return DriverManager.getConnection(url, dbProps);
    }
}
