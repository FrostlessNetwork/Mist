package network.frostless.mist.config.model;

import lombok.Data;
import network.frostless.mist.services.autorole.model.EmojiModel;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@Data
@ConfigSerializable
public class SelectReactionModel {

    private String role;
    private EmojiModel emojiModel;
}
