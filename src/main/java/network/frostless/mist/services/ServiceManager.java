package network.frostless.mist.services;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import network.frostless.mist.core.service.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ServiceManager {

    private final Map<Class<? extends Service>, Service> services = Maps.newConcurrentMap();
    private static ServiceManager instance;

    public ServiceManager() {
        instance = this;
    }

    public List<Service> getServices() {
        return Lists.newArrayList(services.values());
    }

    public void registerService(Service... registeringServices) {
        for (Service service : registeringServices) {
            services.put(service.getClass(), service);
        }
    }

    public <T extends Service> Optional<T> getService(Class<T> clazz) {
        return Optional.ofNullable(clazz.cast(services.get(clazz)));
    }

    public static <T extends Service> Optional<T> get(Class<T> clazz) {
        return instance.getService(clazz);
    }

    public static ServiceManager get() {
        if (instance == null) instance = new ServiceManager();

        return instance;
    }

}
