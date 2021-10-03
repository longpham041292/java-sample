package asia.cmg.f8.commerce.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "payment")
public class PaymentProperties {

    private final PaymentModule domestic = new PaymentModule();;
    private final PaymentModule international = new PaymentModule();
    private String paymentUser;
    private String paymentPassword;
    public String getPaymentSuccessHtml() {
		return paymentSuccessHtml;
	}

	public void setPaymentSuccessHtml(String paymentSuccessHtml) {
		this.paymentSuccessHtml = paymentSuccessHtml;
	}

	public String getPaymentFailedHtml() {
		return paymentFailedHtml;
	}

	public void setPaymentFailedHtml(String paymentFailedHtml) {
		this.paymentFailedHtml = paymentFailedHtml;
	}

	private String versionModule;
    private String queryVersionModule;
    private String title;
    private boolean testMode;
    private String returnHtml;
    private String paymentSuccessHtml;
    private String paymentFailedHtml;
    private String promotionCode;
    private String promotionPTUserName;
    private String groupId;
    private String onepayBaseUrl;
    private String onepayAccessKeyId;
    private String onepaySecretAccessKey;
    private String onepayRegion;
    private String onepayService;
    
    public PaymentModule getDomestic() {
        return domestic;
    }

    public PaymentModule getInternational() {
        return international;
    }

    public String getVersionModule() {
        return versionModule;
    }

    public void setVersionModule(final String versionModule) {
        this.versionModule = versionModule;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public boolean isTestMode() {
        return testMode;
    }

    public void setTestMode(final boolean testMode) {
        this.testMode = testMode;
    }

//    public String getReturnHtml() {
//        return returnHtml;
//    }

    public void setReturnHtml(final String returnHtml) {
        this.returnHtml = returnHtml;
    }

    public String getQueryVersionModule() {
        return queryVersionModule;
    }

    public void setQueryVersionModule(final String queryVersionModule) {
        this.queryVersionModule = queryVersionModule;
    }

    public String getPaymentPassword() {
        return paymentPassword;
    }

    public void setPaymentPassword(final String paymentPassword) {
        this.paymentPassword = paymentPassword;
    }

    public String getPaymentUser() {
        return paymentUser;
    }

    public void setPaymentUser(final String paymentUser) {
        this.paymentUser = paymentUser;
    }
    
    public static class PaymentModule {
        private String url;
        private String secretKey;
        private String merchantId;
        private String accessCode;
        private String returnUrl;
        private double testAmount;
        private String statusQueryUrl;

        public String getUrl() {
            return url;
        }

        public void setUrl(final String url) {
            this.url = url;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(final String secretKey) {
            this.secretKey = secretKey;
        }

        public String getMerchantId() {
            return merchantId;
        }

        public void setMerchantId(final String merchantId) {
            this.merchantId = merchantId;
        }

        public String getAccessCode() {
            return accessCode;
        }

        public void setAccessCode(final String accessCode) {
            this.accessCode = accessCode;
        }

        public String getReturnUrl() {
            return returnUrl;
        }

        public void setReturnUrl(final String returnUrl) {
            this.returnUrl = returnUrl;
        }

        public double getTestAmount() {
            return testAmount;
        }

        public void setTestAmount(final double testAmount) {
            this.testAmount = testAmount;
        }

		public String getStatusQueryUrl() {
			return statusQueryUrl;
		}

		public void setStatusQueryUrl(String statusQueryUrl) {
			this.statusQueryUrl = statusQueryUrl;
		}
    }

	public String getPromotionCode() {
		return promotionCode;
	}

	public void setPromotionCode(final String promotionCode) {
		this.promotionCode = promotionCode;
	}

	public String getPromotionPTUserName() {
		return promotionPTUserName;
	}

	public void setPromotionPTUserName(final String promotionPTUserName) {
		this.promotionPTUserName = promotionPTUserName;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getOnepayBaseUrl() {
		return onepayBaseUrl;
	}

	public void setOnepayBaseUrl(String onepayBaseUrl) {
		this.onepayBaseUrl = onepayBaseUrl;
	}

	public String getOnepayAccessKeyId() {
		return onepayAccessKeyId;
	}

	public void setOnepayAccessKeyId(String onepayAccessKeyId) {
		this.onepayAccessKeyId = onepayAccessKeyId;
	}

	public String getOnepaySecretAccessKey() {
		return onepaySecretAccessKey;
	}

	public void setOnepaySecretAccessKey(String onepaySecretAccessKey) {
		this.onepaySecretAccessKey = onepaySecretAccessKey;
	}

	public String getOnepayRegion() {
		return onepayRegion;
	}

	public void setOnepayRegion(String onepayRegion) {
		this.onepayRegion = onepayRegion;
	}

	public String getOnepayService() {
		return onepayService;
	}

	public void setOnepayService(String onepayService) {
		this.onepayService = onepayService;
	}
    
}
