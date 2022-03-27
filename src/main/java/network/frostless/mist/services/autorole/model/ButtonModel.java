package network.frostless.mist.services.autorole.model;

import lombok.Data;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;
import network.frostless.mist.config.model.common.EmojiModel;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@Data
@ConfigSerializable
public class ButtonModel {

    private ButtonStyle buttonStyle = ButtonStyle.PRIMARY;

    private String idOrUrl = "https://frostless.network";

    private String label = "Click me!";

    private String role;

    @Nullable
    private EmojiModel emoji = new EmojiModel();

}
