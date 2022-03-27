package network.frostless.mist.services.polling.config.model;

import lombok.Data;
import network.frostless.mist.config.model.common.EmojiModel;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@Data
@ConfigSerializable
public class PollOption {

    private String description;

    private EmojiModel emoji;

    PollOption() { }
}
