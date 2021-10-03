package asia.cmg.f8.notification.push;

import java.util.Locale;

/**
 * Created on 2/7/17.
 */
public interface UserInfoService {

    /**
     * A convenience method to get locale of given user's uuid
     *
     * @param uuid the user's uuid
     * @return user's locale or {@link Locale#ENGLISH} as default.
     */
    Locale getLocale(String uuid);

    /**
     * The method trigger for removing locale data from cache.
     */
    void reloadLocale(String uuid);
}
