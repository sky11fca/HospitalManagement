package com.hospital.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConfig {
    private static Connection connection;
    private static final Properties properties = new Properties();

    static{
        try(InputStream in = DatabaseConfig.class.getClassLoader().getResourceAsStream("config.properties")){
            if(in == null){
                throw new RuntimeException("Could not find config.properties");
            }
            properties.load(in);

            initializeDatabase();
        }
        catch (IOException| SQLException e){
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection() throws SQLException{
        if(connection == null || connection.isClosed()){
            connection = DriverManager.getConnection(
                    properties.getProperty("db.url"),
                    properties.getProperty("db.username"),
                    properties.getProperty("db.password")
            );
        }
        return connection;
    }

    private static void initializeDatabase() throws SQLException{
        //TODO: Split the database file, into 2 files and add functionality
    }
}
