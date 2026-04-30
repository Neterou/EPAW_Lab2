package epaw.lab2.repository;

import epaw.lab2.model.User;

import java.sql.*;

/**
 * Singleton repository – only this class knows SQL.
 * No business logic; only stores and retrieves data.
 */
public class UserRepository extends BaseRepository {

    private static UserRepository instance;

    private UserRepository() {}

    public static synchronized UserRepository getInstance() {
        if (instance == null) instance = new UserRepository();
        return instance;
    }

    // ── Uniqueness checks ───────────────────────────────────────────────

    public boolean existsByEmail(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE LOWER(email) = LOWER(?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public boolean existsByDni(String dni) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE UPPER(dni) = UPPER(?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, dni);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public boolean existsByUsername(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE LOWER(username) = LOWER(?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    // ── Bubble lookup ───────────────────────────────────────────────────

    /** Returns bubble id matching zip + city + country, or -1 if none exists. */
    public int findBubbleId(String zip, String city, String country) throws SQLException {
        String sql = """
            SELECT id FROM bubbles
            WHERE zip = ? AND LOWER(city) = LOWER(?) AND UPPER(country) = UPPER(?)
            LIMIT 1
            """;
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, zip);
            ps.setString(2, city);
            ps.setString(3, country);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt("id") : -1;
        }
    }

    /** Returns true if the bubble is open (auto-approve). */
    public boolean isBubbleOpen(int bubbleId) throws SQLException {
        String sql = "SELECT open FROM bubbles WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, bubbleId);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt("open") == 1;
        }
    }

    // ── Insert ──────────────────────────────────────────────────────────

    /** Inserts a new user and returns the generated id. */
    public int insert(User user) throws SQLException {
        String sql = """
            INSERT INTO users
                (name, email, phone, phone_country, dni, zip, city, country,
                 gender, password, username, status, bubble_id)
            VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)
            """;
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1,  user.getName());
            ps.setString(2,  user.getEmail());
            ps.setString(3,  user.getPhone());
            ps.setString(4,  user.getPhoneCountry());
            ps.setString(5,  user.getDni());
            ps.setString(6,  user.getZip());
            ps.setString(7,  user.getCity());
            ps.setString(8,  user.getCountry());
            ps.setString(9,  user.getGender());
            ps.setString(10, user.getPassword());
            ps.setString(11, user.getUsername());
            ps.setString(12, user.getStatus());
            if (user.getBubbleId() != null) {
                ps.setInt(13, user.getBubbleId());
            } else {
                ps.setNull(13, Types.INTEGER);
            }
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            return keys.next() ? keys.getInt(1) : -1;
        }
    }
}