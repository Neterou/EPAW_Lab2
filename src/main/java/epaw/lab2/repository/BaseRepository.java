package epaw.lab2.repository;

import epaw.lab2.util.DBManager;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Base class for all repositories.
 * Provides the shared helper to obtain the current DB connection.
 */
public abstract class BaseRepository {

    /** Convenience accessor so subclasses never call DBManager directly. */
    protected Connection conn() throws SQLException {
        return DBManager.getInstance().getConnection();
    }
}