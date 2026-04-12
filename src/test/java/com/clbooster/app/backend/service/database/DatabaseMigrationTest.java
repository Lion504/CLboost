package com.clbooster.app.backend.service.database;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DatabaseMigrationTest {

    @Test
    void runMigration_executesStatementsWhenConnectionAvailable() throws SQLException {
        Connection connection = mock(Connection.class);
        Statement statement = mock(Statement.class);

        when(connection.createStatement()).thenReturn(statement);

        try (MockedStatic<DatabaseConnection> dbConnMock = Mockito.mockStatic(DatabaseConnection.class)) {
            dbConnMock.when(DatabaseConnection::getConnection).thenReturn(connection);

            assertDoesNotThrow(DatabaseMigration::runMigration);
        }

        verify(connection, atLeast(6)).createStatement();
        verify(statement, atLeast(6)).execute(anyString());
    }

    @Test
    void runMigration_handlesSqlExceptionFromConnection() {
        try (MockedStatic<DatabaseConnection> dbConnMock = Mockito.mockStatic(DatabaseConnection.class)) {
            dbConnMock.when(DatabaseConnection::getConnection).thenThrow(new SQLException("db unavailable"));

            assertDoesNotThrow(DatabaseMigration::runMigration);
        }
    }

    @Test
    void main_delegatesToRunMigration() {
        try (MockedStatic<DatabaseConnection> dbConnMock = Mockito.mockStatic(DatabaseConnection.class)) {
            dbConnMock.when(DatabaseConnection::getConnection).thenThrow(new SQLException("db unavailable"));

            assertDoesNotThrow(() -> DatabaseMigration.main(new String[0]));
        }
    }
}
