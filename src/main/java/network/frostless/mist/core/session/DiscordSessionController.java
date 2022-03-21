package network.frostless.mist.core.session;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.utils.SessionController;
import org.jetbrains.annotations.NotNull;

/**
 * A helper interface that allows us to gather the
 * session limits for a JDA instance.
 */
public interface DiscordSessionController extends SessionController {

    /**
     * Gets the session start limits and returns them as a {@link DiscordSessionControllerImpl.SessionStartLimit} object.
     * This method is used to get the limits for the current session which are set by Discord. By default, we are allowed to
     * connect to the gateway 1,000 times a day.
     * @param api The JDA instance to get the limits from.
     * @return The session start limits.
     */
    DiscordSessionControllerImpl.SessionStartLimit getSessionStartLimit(@NotNull JDA api);
}
