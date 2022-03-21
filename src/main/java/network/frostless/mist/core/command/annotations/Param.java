package network.frostless.mist.core.command.annotations;

import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Param {

    String name();

    String description() default "No description found";

    OptionType type() default OptionType.STRING;

    boolean required() default false;
}
