package com.qlthuvien.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public abstract class DocumentDAO<T> {

    protected Connection connection;

    public DocumentDAO(Connection connection) {
        this.connection = connection;
    }

    // Phương thức trừu tượng để các lớp con triển khai
    public abstract void add(T document) throws SQLException;
    public abstract void update(T document) throws SQLException;
    public abstract void delete(int documentId) throws SQLException;
    public abstract List<T> getAll() throws SQLException;
    public abstract T getById(int documentId) throws SQLException;
}
