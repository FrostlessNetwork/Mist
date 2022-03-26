package network.frostless.fragment.database;

import com.zaxxer.hikari.HikariConfig;

public class HikariConfigUtil {

    public static HikariConfig generateConfigFromEnvironment() {
        HikariConfig config = new HikariConfig();

        config.setPassword(System.getenv("DB_PASSWORD"));
        config.setUsername(System.getenv("DB_USERNAME"));
        config.setJdbcUrl(System.getenv("DB_URL"));
        config.setDriverClassName(System.getenv("DB_DRIVER"));

        return config;
    }
}
