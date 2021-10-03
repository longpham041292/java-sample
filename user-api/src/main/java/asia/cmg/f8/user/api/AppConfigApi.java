package asia.cmg.f8.user.api;

import asia.cmg.f8.user.entity.AppConfig;
import asia.cmg.f8.user.service.AppConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * Created by nhieu on 8/4/17.
 */

@RestController
public class AppConfigApi {

    @Autowired
    private AppConfigService appConfigService;

    @RequestMapping(
            value = "/configs", method = RequestMethod.GET,
            produces = APPLICATION_JSON_UTF8_VALUE)
    public List<AppConfig> getListClub() {
        return appConfigService.getAllConfigs();
    }
}
