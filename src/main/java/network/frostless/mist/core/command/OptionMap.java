package network.frostless.mist.core.command;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

@SuppressWarnings("unchecked")
public enum OptionMap {
    STRING(OptionType.STRING) {
        @Override
        public String get(OptionMapping map) {
            return map.getAsString();
        }
    },
    INTEGER(OptionType.INTEGER) {
        @Override
        public Long get(OptionMapping map) {
            return map.getAsLong();
        }
    },
    BOOLEAN(OptionType.BOOLEAN) {
        @Override
        public Boolean get(OptionMapping map) {
            return map.getAsBoolean();
        }
    },
    USER(OptionType.USER) {
        @Override
        public User get(OptionMapping map) {
            return map.getAsUser();
        }
    },
    CHANNEL(OptionType.CHANNEL) {
        @Override
        public GuildChannel get(OptionMapping map) {
            return map.getAsGuildChannel();
        }
    },
    ROLE(OptionType.ROLE) {
        @Override
        public Role get(OptionMapping map) {
            return map.getAsRole();
        }
    },
    MENTIONABLE(OptionType.MENTIONABLE) {
        @Override
        public IMentionable get(OptionMapping map) {
            return map.getAsMentionable();
        }
    },
    NUMBER(OptionType.NUMBER) {
        @Override
        public Number get(OptionMapping map) {
            return map.getAsLong();
        }
    }
    ;

    private final OptionType type;

    public abstract <T> T get(OptionMapping map);

    OptionMap(OptionType optionType) {
        this.type = optionType;
    }

    public static OptionMap as(OptionType type) {
        for (OptionMap map : values()) {
            if (map.type == type) return map;
        }

        return null;
    }
}
