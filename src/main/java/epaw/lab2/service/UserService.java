package epaw.lab2.service;

import epaw.lab2.model.User;
import epaw.lab2.repository.UserRepository;
import org.apache.commons.codec.digest.DigestUtils;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Singleton service.
 * ALL validation logic and registration flow lives here.
 * The controller (Register servlet) never talks to UserRepository directly.
 */
public class UserService {

    // ── Regex patterns (mirror HTML5 client constraints for defence-in-depth) ──

    private static final Pattern NAME_RE     = Pattern.compile("^[A-Za-zÀ-ÿ\\s\\-']{2,60}$");
    private static final Pattern EMAIL_RE    = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final Pattern PHONE_RE    = Pattern.compile("^\\d{7,15}$");
    private static final Pattern DNI_RE      = Pattern.compile("^\\d{8}[A-Z]$");
    private static final Pattern ZIP_RE      = Pattern.compile("^\\d{5}$");
    private static final Pattern USERNAME_RE = Pattern.compile("^[A-Za-z0-9_]{4,30}$");
    private static final Pattern PASSWORD_RE =
            Pattern.compile("^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$");

    /** Small deny-list – extend as needed */
    private static final Set<String> BAD_WORDS = Set.of("admin", "root");

    /** ISO 3166-1 alpha-2 country codes */
    private static final Set<String> VALID_COUNTRIES = Set.of(
        "AD", "AE", "AF", "AG", "AI", "AL", "AM", "AO", "AQ", "AR", "AS", "AT", "AU", "AW", "AX", "AZ",
        "BA", "BB", "BD", "BE", "BF", "BG", "BH", "BI", "BJ", "BL", "BM", "BN", "BO", "BQ", "BR", "BS", "BT", "BV", "BW", "BY", "BZ",
        "CA", "CC", "CD", "CF", "CG", "CH", "CI", "CK", "CL", "CM", "CN", "CO", "CR", "CU", "CV", "CW", "CX", "CY", "CZ",
        "DE", "DJ", "DK", "DM", "DO", "DZ",
        "EC", "EE", "EG", "EH", "ER", "ES", "ET",
        "FI", "FJ", "FK", "FM", "FO", "FR",
        "GA", "GB", "GD", "GE", "GF", "GG", "GH", "GI", "GL", "GM", "GN", "GP", "GQ", "GR", "GS", "GT", "GU", "GW", "GY",
        "HK", "HM", "HN", "HR", "HT", "HU",
        "ID", "IE", "IL", "IM", "IN", "IO", "IQ", "IR", "IS", "IT",
        "JE", "JM", "JO", "JP",
        "KE", "KG", "KH", "KI", "KM", "KN", "KP", "KR", "KW", "KY", "KZ",
        "LA", "LB", "LC", "LI", "LK", "LR", "LS", "LT", "LU", "LV", "LY",
        "MA", "MC", "MD", "ME", "MF", "MG", "MH", "MK", "ML", "MM", "MN", "MO", "MP", "MQ", "MR", "MS", "MT", "MU", "MV", "MW", "MX", "MY", "MZ",
        "NA", "NC", "NE", "NF", "NG", "NI", "NL", "NO", "NP", "NR", "NU", "NZ",
        "OM",
        "PA", "PE", "PF", "PG", "PH", "PK", "PL", "PM", "PN", "PR", "PS", "PT", "PW", "PY",
        "QA",
        "RE", "RO", "RS", "RU", "RW",
        "SA", "SB", "SC", "SD", "SE", "SG", "SH", "SI", "SJ", "SK", "SL", "SM", "SN", "SO", "SR", "SS", "ST", "SV", "SX", "SY", "SZ",
        "TC", "TD", "TF", "TG", "TH", "TJ", "TK", "TL", "TM", "TN", "TO", "TR", "TT", "TV", "TW", "TZ",
        "UA", "UG", "UM", "US", "UY", "UZ",
        "VA", "VC", "VE", "VG", "VI", "VN", "VU",
        "WF", "WS",
        "YE", "YT",
        "ZA", "ZM", "ZW"
    );

    // ── Singleton ────────────────────────────────────────────────────────

    private static UserService instance;
    private final UserRepository repo = UserRepository.getInstance();

    private UserService() {}

    public static synchronized UserService getInstance() {
        if (instance == null) instance = new UserService();
        return instance;
    }

    // ── Public API ───────────────────────────────────────────────────────

    /**
     * Runs all validation, bubble assignment, and persistence.
     *
     * @param user          populated model (password still plain-text)
     * @param passwordCheck the confirm-password field value
     * @return map of field errors; empty when registration succeeds
     */
    public Map<String, String> register(User user, String passwordCheck) {
        Map<String, String> errors = new LinkedHashMap<>();

        // ── Step 1: client-mirrored format checks ────────────────────────
        validateName(user.getName(), errors);
        validateEmail(user.getEmail(), errors);
        validatePhone(user.getPhone(), user.getPhoneCountry(), errors);
        validateDni(user.getDni(), errors);
        validateZip(user.getZip(), errors);
        validateCity(user.getCity(), errors);
        validateCountry(user.getCountry(), errors);
        validatePassword(user.getPassword(), passwordCheck, errors);
        validateUsername(user.getUsername(), errors);

        if (!errors.isEmpty()) return errors;

        // ── Step 2: DB uniqueness checks ────────────────────────────────
        try {
            if (repo.existsByEmail(user.getEmail()))
                errors.put("email", "This e-mail is already registered.");
            if (repo.existsByDni(user.getDni()))
                errors.put("dni", "This DNI is already registered.");
            if (repo.existsByUsername(user.getUsername()))
                errors.put("username", "This username is already taken.");
        } catch (SQLException e) {
            errors.put("_db", "Database error. Please try again.");
            return errors;
        }

        if (!errors.isEmpty()) return errors;

        // ── Step 3: Bubble assignment ────────────────────────────────────
        int bubbleId;
        try {
            bubbleId = repo.findBubbleId(user.getZip(), user.getCity(), user.getCountry());
        } catch (SQLException e) {
            errors.put("_db", "Database error during bubble lookup. Please try again.");
            return errors;
        }

        if (bubbleId == -1) {
            errors.put("zip", "No Bubble available for your location yet.");
            return errors;
        }

        user.setBubbleId(bubbleId);

        // ── Step 4: Organisation approval ───────────────────────────────
        boolean open;
        try {
            open = repo.isBubbleOpen(bubbleId);
        } catch (SQLException e) {
            errors.put("_db", "Database error during approval check. Please try again.");
            return errors;
        }

        user.setStatus(open ? "APPROVED" : "PENDING");

        // ── Step 5: Hash password & persist ─────────────────────────────
        user.setPassword(hashPassword(user.getPassword()));

        try {
            repo.insert(user);
        } catch (SQLException e) {
            errors.put("_db", "Could not save your registration. Please try again.");
            return errors;
        }

        return errors;
    }

    // ── Field validators ─────────────────────────────────────────────────

    private void validateName(String v, Map<String, String> e) {
        if (blank(v))                               { e.put("name", "Name is required."); return; }
        if (!NAME_RE.matcher(v.trim()).matches())     e.put("name", "Name must be 2–60 letters (no digits).");
    }

    private void validateEmail(String v, Map<String, String> e) {
        if (blank(v))                               { e.put("email", "Email is required."); return; }
        if (!EMAIL_RE.matcher(v.trim()).matches())    e.put("email", "Enter a valid email address.");
    }

    private void validatePhone(String phone, String prefix, Map<String, String> e) {
        if (!blank(phone)) {
            if (!PHONE_RE.matcher(phone.trim()).matches())
                e.put("phone", "Phone must be 7–15 digits when provided.");
            if (blank(prefix))
                e.put("phoneCountry", "Select a country prefix for the phone number.");
        }
    }

    private void validateDni(String v, Map<String, String> e) {
        if (blank(v))                               { e.put("dni", "DNI is required."); return; }
        if (!DNI_RE.matcher(v.trim().toUpperCase()).matches())
            e.put("dni", "DNI must be 8 digits + 1 uppercase letter (e.g. 12345678A).");
    }

    private void validateZip(String v, Map<String, String> e) {
        if (blank(v))                               { e.put("zip", "ZIP code is required."); return; }
        if (!ZIP_RE.matcher(v.trim()).matches())      e.put("zip", "ZIP code must be exactly 5 digits.");
    }

    private void validateCity(String v, Map<String, String> e) {
        if (blank(v)) e.put("city", "City is required.");
    }

    private void validateCountry(String v, Map<String, String> e) {
        if (blank(v))                                          { e.put("country", "Country is required."); return; }
        String code = v.trim().toUpperCase();
        if (!VALID_COUNTRIES.contains(code))
            e.put("country", "Invalid country code. Use ISO 3166-1 alpha-2 (e.g., ES, GB, US).");
    }

    private void validatePassword(String pw, String check, Map<String, String> e) {
        if (blank(pw))                              { e.put("password", "Password is required."); return; }
        if (!PASSWORD_RE.matcher(pw).matches())
            e.put("password", "Min 8 chars, 1 uppercase, 1 digit, 1 special character.");
        if (!pw.equals(check))
            e.put("passwordCheck", "Passwords do not match.");
    }

    private void validateUsername(String v, Map<String, String> e) {
        if (blank(v))                               { e.put("username", "Username is required."); return; }
        String val = v.trim();
        if (!USERNAME_RE.matcher(val).matches())
            e.put("username", "4–30 chars: letters, digits, underscores only.");
        if (BAD_WORDS.contains(val.toLowerCase()))
            e.put("username", "That username is not allowed.");
    }

    // ── Helpers ──────────────────────────────────────────────────────────

    private static boolean blank(String s)      { return s == null || s.isBlank(); }
    private static String  hashPassword(String p) { return DigestUtils.sha256Hex(p); }
}