package network.frostless.mist.config.model;

import lombok.Data;
import network.frostless.mist.services.autorole.model.ButtonModel;
import network.frostless.mist.config.model.common.EmojiModel;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@ConfigSerializable
public class AutoRoleModel {

    private List<String> allowedAdmins = new ArrayList<>();

    /**
     * Action group id -> action rows -> button models
     */
    private Map<String, Map<Integer, Map<Integer, ButtonModel>>> buttons = new HashMap<>();

    private Map<String, List<SelectOptionModel>> selectOptions = new HashMap<>();

    private Map<String, Map<String, EmojiModel>> selectReactions = new HashMap<>();
}
