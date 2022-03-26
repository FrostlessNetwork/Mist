package network.frostless.mist.config.model;

import lombok.Data;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import network.frostless.mist.services.autorole.model.EmojiModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@Data
@ConfigSerializable
public class SelectOptionModel {

    @NotNull
    private String label;

    @NotNull
    private String value;

    @Nullable
    private String description;

    @Nullable
    private EmojiModel emoji;

    @NotNull
    private String role;

    SelectOptionModel() {}
    public SelectOption to() {
        SelectOption selectOption = SelectOption.of(label, value);

        if (description != null) selectOption = selectOption.withDescription(description);
        if(emoji != null) selectOption = selectOption.withEmoji(emoji.to());

        return selectOption;
    }
}
