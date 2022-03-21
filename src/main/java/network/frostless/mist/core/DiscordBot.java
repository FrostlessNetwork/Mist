package network.frostless.mist.core;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import network.frostless.mist.core.service.Service;
import network.frostless.mist.core.service.impl.EventableService;
import network.frostless.mist.core.session.DiscordSessionController;
import network.frostless.mist.core.session.DiscordSessionControllerImpl;
import network.frostless.mist.services.ServiceManager;

import javax.security.auth.login.LoginException;
import java.util.*;

/**
 * @author RiceCX
 */
@Getter
public abstract class DiscordBot implements AutoCloseable {

    @Setter
    private JDABuilder jdaBuilder = buildPrerequisites();

    @Getter
    private static JDA jda;

    private final DiscordSessionController sessionController;

    private final Queue<Object> queue = new ArrayDeque<>();

    public DiscordBot() {
        sessionController = new DiscordSessionControllerImpl();
    }

    protected void registerListener(Object listener) {
        if(jda != null) {
            jda.addEventListener(listener);
        } else {
            queue.offer(listener);
        }
    }

    protected void registerService(Service... services) {
        for (Service service : services) {
            ServiceManager.get().registerService(service);
            if (service instanceof EventableService) {
                if (jda != null) {
                    jda.addEventListener(service);
                } else {
                    queue.offer(service);
                }
            }
        }
    }

    /**
     * Starts the creation of the {@link JDA} instance.
     *
     * @throws LoginException       Thrown when the login fails.
     * @throws InterruptedException Thrown when the login is interrupted.
     */
    public void start() throws LoginException, InterruptedException {
        jda = jdaBuilder.build();

        jda.setRequiredScopes("applications.commands");

        while (!queue.isEmpty()) {
            jda.addEventListener(queue.poll());
        }

        jda.addEventListener(this);

        jda.awaitReady();
        onStart();
    }

    private JDABuilder buildPrerequisites() {
        return JDABuilder
                .createDefault(System.getenv("DISCORD_TOKEN"))
                .enableIntents(EnumSet.allOf(GatewayIntent.class))
                .setEventManager(new AnnotatedEventManager());
    }

    /**
     * This method is invoked when
     * the JDA instance has been called and
     * is ready to be used.
     */
    protected abstract void onStart();

    @Override
    public void close() {
        jda.shutdown();
    }
}
