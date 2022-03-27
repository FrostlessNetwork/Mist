package network.frostless.mist.services.polling.config.model;

import lombok.Data;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@ConfigSerializable
public class PollModel {

    private String name;

    private String description;

    private int minVote = 0;

    private int maxVote = 100;

    private Map<String, PollOption> options = new HashMap<>();

    PollModel() {
    }


    public List<SelectOption> getSelectOptions() {
        return options.entrySet().stream().map(entry -> {
            SelectOption of = SelectOption.of(entry.getKey(), entry.getKey());
            of = entry.getValue().getEmoji() != null ? of.withEmoji(entry.getValue().getEmoji().to()) : of;
            of = entry.getValue().getDescription() != null ? of.withDescription(entry.getValue().getDescription()) : of;

            return of;
        }).toList();
    }
}
