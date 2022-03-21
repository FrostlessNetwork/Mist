package network.frostless.mist.core.session;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.exceptions.AccountTypeException;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.utils.SessionController;
import net.dv8tion.jda.api.utils.SessionControllerAdapter;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.requests.Route;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;

/**
 * A default implementation of {@link SessionController} that can be used to
 * get the session limits for a {@link JDA} instance.
 * @author RiceCX
 */
public class DiscordSessionControllerImpl extends SessionControllerAdapter implements DiscordSessionController {


    @Override
    public SessionStartLimit getSessionStartLimit(@NotNull JDA api) {
        AccountTypeException.check(api.getAccountType(), AccountType.BOT);
        return new RestActionImpl<SessionStartLimit>(api, Route.Misc.GATEWAY_BOT.compile()) {
            @Override
            public void handleResponse(Response response, Request<SessionStartLimit> request) {
                if (response.isOk()) {
                    DataObject object = response.getObject();
                    long total = object.getObject("session_start_limit").getLong("total", 1);
                    long remaining = object.getObject("session_start_limit").getLong("remaining", 1);

                    request.onSuccess(new SessionStartLimit(total, remaining));
                }
                else if (response.code == 401) {
                    api.shutdownNow();
                    request.onFailure(new LoginException("The provided token is invalid!"));
                } else {
                    request.onFailure(response);
                }
            }
        }.priority().complete();
    }

    /**
     * A record that contains the total and remaining session limits for a {@link JDA} instance.
     */
    public static record SessionStartLimit(long total, long remaining) { }
}
