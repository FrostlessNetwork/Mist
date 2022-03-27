package network.frostless.mist;

import network.frostless.mist.config.MistConfig;
import org.spongepowered.configurate.ConfigurateException;

import javax.security.auth.login.LoginException;
import java.nio.file.Path;

public class Application {


    public static MistConfig config;

    public static Path configDirectory = Path.of("./");

    static {
        System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
    }

    public static void main(String[] args) throws LoginException, InterruptedException, ConfigurateException {
        if(!System.getenv().containsKey("DISCORD_TOKEN")) throw new IllegalArgumentException("DISCORD_TOKEN environment variable is not set");

        for (String arg : args) {
            if(arg.startsWith("--config=")) {
                configDirectory = Path.of(arg.substring(9));
            }
        }

        Application.config = new MistConfig();

        config.setFilePath(Path.of(configDirectory + "/config.yml"));
        config.load();

        Mist mist = new Mist();
        mist.start();
    }
}
