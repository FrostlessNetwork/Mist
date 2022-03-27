package network.frostless.mist.config.model.polling;

import lombok.Data;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@ConfigSerializable
public class PollingConfigModel {

    private List<String> admins = new ArrayList<>();

    private Map<String, PollModel> list = new HashMap<>();

    public PollingConfigModel() { }

}
