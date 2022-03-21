package network.frostless.mist;

import cc.ricecx.logsnag4j.Emoji;
import cc.ricecx.logsnag4j.LogSnagClient;
import cc.ricecx.logsnag4j.api.LogSnag;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import network.frostless.frostcore.messaging.redis.Redis;
import network.frostless.frostcore.messaging.redis.impl.DefaultRedisProvider;
import network.frostless.mist.commands.HelloWorldCommand;
import network.frostless.mist.commands.autorole.AutoRoleCommand;
import network.frostless.mist.commands.HelpCommand;
import network.frostless.mist.commands.server.PlayerCountCommand;
import network.frostless.mist.commands.server.ServerIPCommand;
import network.frostless.mist.commands.server.link.LinkCommand;
import network.frostless.mist.config.MistConfig;
import network.frostless.mist.core.DiscordBot;
import network.frostless.mist.core.session.DiscordSessionControllerImpl;
import network.frostless.mist.services.autorole.AutoRoleService;
import network.frostless.mist.services.command.CommandService;
import network.frostless.mist.services.invitetracking.InviteTrackingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The main Bot class
 */
public class Mist extends DiscordBot {

    private final Logger logger = LogManager.getLogger("Mist");

    private final MistConfig config = Application.config;
    private final Redis<String, String> redis;
    private final LogSnag logSnag;

    private final CommandService commandService;

    /**
     * Instantiates a {@link Mist} bot and
     * initializes all of its services and commands.
     */
    public Mist() {
        super();
        logSnag = new LogSnagClient(System.getenv("LOGSNAG_KEY"), "frostless-mist");
        redis = new DefaultRedisProvider(config.get().getRedis().get());

        commandService = new CommandService();
        registerService(
                commandService,
                new InviteTrackingService(),
                new AutoRoleService()
        );

        commandService.addCommand(
                new HelloWorldCommand(),
                new HelpCommand(),
                new LinkCommand(),
                new ServerIPCommand(),
                new PlayerCountCommand(),
                new AutoRoleCommand()
        );
    }

    @Override
    protected void onStart() {
        DiscordSessionControllerImpl.SessionStartLimit session = getSessionController().getSessionStartLimit(getJda());
        logSnag.log("start", "boot", String.format("Mist has started! %s out of %s sessions remaining", session.remaining(), session.total()), Emoji.of("🧊"));

        commandService.registerCommands();
    }

    @SubscribeEvent
    public void onReady(ReadyEvent evt) {
        final SelfUser user = evt.getJDA().getSelfUser();
        final DiscordSessionControllerImpl.SessionStartLimit sessionStartLimit = getSessionController().getSessionStartLimit(evt.getJDA());

        logger.info("Successfully logged into Discord as {}", user.getAsTag());
        logger.info("Bot ID: {}", user.getId());
        logger.info("Bot Owner: {}", user.getJDA().retrieveApplicationInfo().complete().getOwner().getId());
        logger.info("Available guilds: {} ({}/{} available)", evt.getGuildTotalCount(), evt.getGuildTotalCount() - evt.getGuildUnavailableCount(), evt.getGuildAvailableCount());
        logger.info("Connected to the Discord API (v{}) with {}/{} sessions remaining.", JDAInfo.DISCORD_GATEWAY_VERSION, sessionStartLimit.remaining(), sessionStartLimit.total());

        evt.getJDA().getPresence().setPresence(
                OnlineStatus.ONLINE,
                Activity.of(Activity.ActivityType.WATCHING, "for help!")
        );
    }
}
