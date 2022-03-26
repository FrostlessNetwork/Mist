package network.frostless.mist.services.autorole.model;

import lombok.Data;
import net.dv8tion.jda.api.entities.Emoji;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@Data
@ConfigSerializable
public class EmojiModel {

    private String icon;

    /**
     * Custom emojis
     */
    @Nullable
    private Long id;

    private boolean animated = false;

    private boolean custom = false;


    public Emoji to() {
        if (id != null) {
            return Emoji.fromEmote(icon, id, animated);
        } else {
            return Emoji.fromUnicode(icon);
        }
    }
}
