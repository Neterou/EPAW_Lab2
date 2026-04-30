package epaw.lab2.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Singleton that owns the single SQLite connection.
 * Path is set once at servlet init time via {@link #setDbPath(String)}.
 */
public class DBManager {

    private static DBManager instance;
    private Connection connection;

    private static String dbPath;

    private DBManager() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        initSchema();
    }

    /** Called once by Register.init() with the real WEB-INF path. */
    public static void setDbPath(String path) {
        dbPath = path;
    }

    public static synchronized DBManager getInstance() throws SQLException {
        if (instance == null || instance.connection.isClosed()) {
            instance = new DBManager();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    // ────────────────────────────────────────────────────────────────────

    private void initSchema() throws SQLException {
        String[] stmts = {
            """
            CREATE TABLE IF NOT EXISTS bubbles (
                id      INTEGER PRIMARY KEY AUTOINCREMENT,
                zip     VARCHAR(5)   NOT NULL,
                city    VARCHAR(100) NOT NULL,
                country VARCHAR(2)   NOT NULL,
                name    VARCHAR(100) NOT NULL,
                open    INTEGER      NOT NULL DEFAULT 1
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS users (
                id            INTEGER PRIMARY KEY AUTOINCREMENT,
                name          VARCHAR(60)  NOT NULL,
                email         VARCHAR(255) NOT NULL UNIQUE,
                phone         VARCHAR(15),
                phone_country VARCHAR(5),
                dni           VARCHAR(9)   NOT NULL UNIQUE,
                zip           VARCHAR(5)   NOT NULL,
                city          VARCHAR(100) NOT NULL,
                country       VARCHAR(2)   NOT NULL,
                gender        VARCHAR(20),
                password      VARCHAR(255) NOT NULL,
                username      VARCHAR(30)  NOT NULL UNIQUE,
                status        VARCHAR(10)  NOT NULL DEFAULT 'PENDING',
                bubble_id     INTEGER,
                created_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (bubble_id) REFERENCES bubbles(id)
            )
            """,
            """
            INSERT OR IGNORE INTO bubbles (zip, city, country, name, open)
            VALUES
                ('08001', 'Barcelona', 'ES', 'Bubble Barcelona', 1),
                ('10001', 'New York', 'US', 'Bubble New York', 1),
                ('75001', 'Paris', 'FR', 'Bubble Paris', 0)
            """
        };
        try (Statement st = connection.createStatement()) {
            for (String sql : stmts) {
                st.execute(sql.strip());
            }
        }
    }
}