package asia.cmg.f8.notification.dto;

public class NotificationRequest {
	private String title;
	private String description;
	private NotificationGroupType groupType;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public NotificationGroupType getGroupType() {
		return groupType;
	}

	public void setGroupType(NotificationGroupType groupType) {
		this.groupType = groupType;
	}
}
