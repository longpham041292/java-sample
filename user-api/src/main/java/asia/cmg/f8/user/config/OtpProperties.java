package asia.cmg.f8.user.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(value = "otp")
public class OtpProperties {
	private String saltSecret;
	private Integer timeStepSizeInSecond;
	private Integer windowSize;
	private String messageEn;
	private String messageVi;
	private CmcSmsProperties cmcSms;
	public String getSaltSecret() {
		return saltSecret;
	}
	public void setSaltSecret(String saltSecret) {
		this.saltSecret = saltSecret;
	}
	public Integer getTimeStepSizeInSecond() {
		return timeStepSizeInSecond;
	}
	public void setTimeStepSizeInSecond(Integer timeStepSizeInSecond) {
		this.timeStepSizeInSecond = timeStepSizeInSecond;
	}
	public Integer getWindowSize() {
		return windowSize;
	}
	public void setWindowSize(Integer windowSize) {
		this.windowSize = windowSize;
	}
	public String getMessageEn() {
		return messageEn;
	}
	public void setMessageEn(String messageEn) {
		this.messageEn = messageEn;
	}
	public String getMessageVi() {
		return messageVi;
	}
	public void setMessageVi(String messageVi) {
		this.messageVi = messageVi;
	}
	public CmcSmsProperties getCmcSms() {
		return cmcSms;
	}
	public void setCmcSms(CmcSmsProperties cmcSms) {
		this.cmcSms = cmcSms;
	}
	
	
}
