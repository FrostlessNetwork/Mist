package network.frostless.mist.config;

import network.frostless.frostcore.config.impl.YamlConfiguration;
import network.frostless.mist.config.model.MistConfigModel;

public class MistConfig extends YamlConfiguration<MistConfigModel> {

    public MistConfig() { }
    @Override
    protected Class<MistConfigModel> clazz() {
        return MistConfigModel.class;
    }
}
