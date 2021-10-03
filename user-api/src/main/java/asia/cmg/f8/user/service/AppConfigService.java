package asia.cmg.f8.user.service;

import asia.cmg.f8.user.client.AppConfigClient;
import asia.cmg.f8.user.entity.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by nhieu on 8/4/17.
 */
@Service
public class AppConfigService {

    @Autowired
    private AppConfigClient appConfigClient;

    public List<AppConfig> getAllConfigs() {
        return appConfigClient.geConfigByQuery().getEntities().stream().map(entity -> AppConfig
                .builder()
                .name(entity.getName())
                .value(entity.getValue())
                .build()).collect(Collectors.toList());
    }
}
