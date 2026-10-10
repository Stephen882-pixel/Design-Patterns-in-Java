package com.example.ObjectPoolDesignPattern;

import java.sql.Connection;
import java.sql.SQLException;

public class PooledObject {
    private Connection connection;

    public PooledObject(Connection connection){
        this.connection=connection;
    }

    public Connection getConnection(){
        return connection;
    }

    public void close(){
        try{
            connection.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public boolean isClosed(){
        try{
            return  connection.isClosed();
        } catch(SQLException e){
            e.printStackTrace();
            return true;
        }
    }
}
