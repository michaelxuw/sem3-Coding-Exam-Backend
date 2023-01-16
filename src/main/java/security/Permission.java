package security;

public enum Permission {
    ADMIN(Types.ADMIN),
    USER(Types.USER);

    public class Types{
        public static final String ADMIN = "ADMIN";
        public static final String USER = "USER";
    }

    private final String value;

    Permission(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }
}
