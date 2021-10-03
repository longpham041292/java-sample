package asia.cmg.f8.session.dto;

import javax.persistence.Column;

/**
 * Created on 11/21/16.
 */

public class UserConnectionDto  {

    private String userUuid;
    private String ptUuid;
  
    @Column(name = "user_uuid", length = 36, nullable = false)
    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(final String userUuid) {
        this.userUuid = userUuid;
    }

    @Column(name = "pt_uuid", length = 36, nullable = false)
    public String getPtUuid() {
        return ptUuid;
    }

    public void setPtUuid(final String ptUuid) {
        this.ptUuid = ptUuid;
    }
    
    public static UserConnectionDto convertFromSession(final Object[] item) {
    	UserConnectionDto dto = new UserConnectionDto();
    	dto.setUserUuid(item[0].toString());
    	dto.setPtUuid(item[1].toString());
		return dto;
    }
}
