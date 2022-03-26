package network.frostless.fragment.database;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;

public class DatabaseManager {

    @Getter
    private HikariDataSource dataSource;

    public DatabaseManager() {
        dataSource = new HikariDataSource(HikariConfigUtil.generateConfigFromEnvironment());
    }

    private void initTables() {}
}
