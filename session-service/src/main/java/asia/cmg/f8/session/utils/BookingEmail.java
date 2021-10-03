package asia.cmg.f8.session.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "mail.bookingEmail")
public class BookingEmail {
	private String sender;
	private String recipients;
	private String logoUrl;
	private String appStore;
	private String googlePlay;
	private String decor;
	private String divider;
	private String contact;

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getRecipients() {
		return recipients;
	}

	public void setRecipients(String recipients) {
		this.recipients = recipients;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public String getAppStore() {
		return appStore;
	}

	public void setAppStore(String appStore) {
		this.appStore = appStore;
	}

	public String getGooglePlay() {
		return googlePlay;
	}

	public void setGooglePlay(String googlePlay) {
		this.googlePlay = googlePlay;
	}

	public String getDecor() {
		return decor;
	}

	public void setDecor(String decor) {
		this.decor = decor;
	}

	public String getDivider() {
		return divider;
	}

	public void setDivider(String divider) {
		this.divider = divider;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

}
