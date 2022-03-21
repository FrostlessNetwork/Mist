package network.frostless.mist;

import network.frostless.mist.config.MistConfig;
import org.spongepowered.configurate.ConfigurateException;

import javax.security.auth.login.LoginException;
import java.nio.file.Path;

public class Application {


    public static MistConfig config;

    static {
        System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
    }

    public static void main(String[] args) throws LoginException, InterruptedException, ConfigurateException {
        if(!System.getenv().containsKey("DISCORD_TOKEN")) throw new IllegalArgumentException("DISCORD_TOKEN environment variable is not set");

        Application.config = new MistConfig();

        config.setFilePath(Path.of("./config.yml"));
        config.load();

        Mist mist = new Mist();

        mist.start();
    }
}
