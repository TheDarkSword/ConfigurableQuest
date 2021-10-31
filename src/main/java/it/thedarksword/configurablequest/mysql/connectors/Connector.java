package it.thedarksword.configurablequest.mysql.connectors;

import java.sql.Connection;
import java.sql.SQLException;

public interface Connector {

    Connection getConnection() throws SQLException;
    void shutdown();
}
