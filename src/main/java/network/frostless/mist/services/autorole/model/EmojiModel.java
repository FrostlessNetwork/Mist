package network.frostless.mist.services.autorole.model;

import lombok.Data;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@Data
@ConfigSerializable
public class EmojiModel {

    private String icon;

    /**
     * Custom emojis
     */
    private Long id;

    private boolean animated = false;

    private boolean custom = false;
}
