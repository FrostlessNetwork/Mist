package network.frostless.mist.services.polling.config;

import network.frostless.frostcore.config.impl.YamlConfiguration;
import network.frostless.mist.services.polling.config.model.PollingConfigModel;

public class PollingConfig extends YamlConfiguration<PollingConfigModel> {

    public PollingConfig() {
    }

    @Override
    protected Class<PollingConfigModel> clazz() {
        return PollingConfigModel.class;
    }
}
