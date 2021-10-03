package asia.cmg.f8.session.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created on 11/21/16.
 */
@Entity
@Table(name = "reset_auto_follow")
public class ResetAutoFollowEntity extends AbstractEntity {

	private static final long serialVersionUID = -8511465642180271779L;
	
	@Column(name = "user_uuid", length = 36, nullable = false)
	private String userUuid;
	
	@Column(name = "number_of_following")
	private int numberOfFollowing;

    public int getNumberOfFollowing() {
		return numberOfFollowing;
	}

	public void setNumberOfFollowing(int numberOfFollowing) {
		this.numberOfFollowing = numberOfFollowing;
	}

	
    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(final String userUuid) {
        this.userUuid = userUuid;
    }
}
