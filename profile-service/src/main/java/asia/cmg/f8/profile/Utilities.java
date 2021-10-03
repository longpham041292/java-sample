package asia.cmg.f8.profile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import asia.cmg.f8.profile.config.UserProfileProperties;

public class Utilities {
	
	private UserProfileProperties userProfileProperties;
	
	private static List<String> LEVELS = Arrays.asList("GOLD", "PLATINUM", "DIAMOND");
	
	public Utilities(UserProfileProperties config) {
		userProfileProperties = config;
	}
	
	public int processUserSearchPagesize(int pageSize) {
		int size = 0;
		
        if(pageSize < 1) {
        	size = userProfileProperties.getUser().getSizeLoad();
        } else {
			size = pageSize > userProfileProperties.getUser().getMaxLoad() ? userProfileProperties.getUser().getMaxLoad() : pageSize;
		}
        
        return size;
	}
	
	public static List<String> getLowerOrEqualLevels(String level) {
		List<String> result = new ArrayList<String>();
		level = level.toUpperCase();
		int index = LEVELS.indexOf(level);
		
		if(index != -1) {
			for(int i = 0; i<= index; i++) {
				result.add(LEVELS.get(i));
			}
		}
		return result;
	}
}
