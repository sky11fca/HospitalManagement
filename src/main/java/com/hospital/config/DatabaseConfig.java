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

            //initializeDatabase();
        }
        catch (IOException e){
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
        try(Connection conn = getConnection();
        InputStream schemaStream = DatabaseConfig.class.getClassLoader().getResourceAsStream("db-schema.sql");
        InputStream dataStream = DatabaseConfig.class.getClassLoader().getResourceAsStream("db-populate.sql")){

            //execute the schema script
            if(schemaStream!=null){
                String schemaSql = new String(schemaStream.readAllBytes());
                conn.createStatement().execute(schemaSql);
            }

            //Execute population script

            if(dataStream!= null){
                String dataSql = new String(schemaStream.readAllBytes());
                conn.createStatement().execute(dataSql);
            }
        }
        catch(IOException e){
            throw new SQLException("Something went wrong while initializing the database", e);
        }
    }
}
