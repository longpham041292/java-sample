package asia.cmg.f8.session.dto;

import asia.cmg.f8.common.util.ZoneDateTimeUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.Locale;

import javax.annotation.Nullable;

/**
 * Created on 12/15/16.
 */
public class ClientSessionInfo {

    public static final String NOT_STARTED = "NOT_STARTED";
    @JsonProperty("uuid")
    private String uuid;

    @JsonProperty("name")
    private String name;

    @JsonProperty("avatar")
    private String avatar;

    @JsonProperty("session_burned")
    @Nullable
    private Integer sessionBurned;
    
    @JsonProperty("session_confirmed")
    @Nullable
    private Integer sessionConfirmed;

    @JsonProperty("session_number")
    private Integer sessionNumber;

    @JsonProperty("expired_date")
    @Nullable
    private Long expireDate;

    @JsonProperty("status")
    private String status;

    @JsonProperty("packagePrice")
    private String price;

    @JsonProperty("commission")
    private Double commission;

    @JsonProperty("package_session_uuid")
    private String packageSessionUuid;

    @JsonProperty("order_code")
    private String orderCode;

	@JsonProperty("contract_number")
	private String contractNumber;

	public String getContractNumber() {
		return contractNumber;
	}

	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}

	@JsonProperty("order_uuid")
	@Nullable
	private String orderUuid;

	private String userName;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(final String avatar) {
        this.avatar = avatar;
    }

    @Nullable
    public Integer getSessionBurned() {
        return sessionBurned;
    }

    public void setSessionBurned(@Nullable final Integer sessionBurned) {
        this.sessionBurned = sessionBurned;
    }
    
    @Nullable
    public Integer getSessionConfirmed() {
        return sessionConfirmed;
    }

    public void setSessionConfirmed(@Nullable final Integer sessionConfirmed) {
        this.sessionConfirmed = sessionConfirmed;
    }

    public Integer getSessionNumber() {
        return sessionNumber;
    }

    public void setSessionNumber(final Integer sessionNumber) {
        this.sessionNumber = sessionNumber;
    }

    @Nullable
    public Long getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(@Nullable final Long expireDate) {
        this.expireDate = expireDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(final String price) {
        this.price = price;
    }

    public Double getCommission() {
        return commission;
    }

    public void setCommission(final Double commission) {
        this.commission = commission;
    }

    public String getPackageSessionUuid() {
        return packageSessionUuid;
    }

    public void setPackageSessionUuid(final String packageSessionUuid) {
        this.packageSessionUuid = packageSessionUuid;
    }

    @JsonIgnore
    public String getUserName() {
        return userName;
    }

    public void setUserName(final String userName) {
        this.userName = userName;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(final String orderCode) {
        this.orderCode = orderCode;
    }

	public String getOrderUuid() {
		return orderUuid;
	}

	public void setOrderUuid(final String orderUuid) {
		this.orderUuid = orderUuid;
	}

    @JsonIgnore
    public ClientSessionInfoExport getExportResponse(final Locale locale) {
        final ClientSessionInfoExport clientSessionInfoExport = new ClientSessionInfoExport();
        clientSessionInfoExport.setFullName(this.getName());
        clientSessionInfoExport.setUserName(this.getUserName());
        clientSessionInfoExport.setNumberOfBurnedSessions(String.valueOf(this.getSessionBurned()));
        clientSessionInfoExport.setNumberOfSessions(String.valueOf(this.getSessionNumber()));
        clientSessionInfoExport.setPackagePrice(String.valueOf(this.getPrice()));
        clientSessionInfoExport.setStatus(this.getStatus());

        if (this.getExpireDate() != null) {
            final String datePattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(
                    FormatStyle.SHORT, null, IsoChronology.INSTANCE,
                    locale);
            final String orderExpiredDate = ZoneDateTimeUtils.convertFromUTCToLocalDateTime(this.getExpireDate())
                    .format(DateTimeFormatter.ofPattern(datePattern));
            clientSessionInfoExport.setExpirationDate(orderExpiredDate);
        } else {
            clientSessionInfoExport.setExpirationDate(NOT_STARTED);
        }

        return clientSessionInfoExport;
    }

    public class ClientSessionInfoExport {
        private String userName;
        private String fullName;
        private String numberOfBurnedSessions;
        private String numberOfSessions;
        private String packagePrice;
        private String status;
        private String expirationDate;

        public String getUserName() {
            return userName;
        }

        public void setUserName(final String userName) {
            this.userName = userName;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(final String fullName) {
            this.fullName = fullName;
        }

        public String getNumberOfBurnedSessions() {
            return numberOfBurnedSessions;
        }

        public void setNumberOfBurnedSessions(final String numberOfBurnedSessions) {
            this.numberOfBurnedSessions = numberOfBurnedSessions;
        }

        public String getNumberOfSessions() {
            return numberOfSessions;
        }

        public void setNumberOfSessions(final String numberOfSessions) {
            this.numberOfSessions = numberOfSessions;
        }

        public String getPackagePrice() {
            return packagePrice;
        }

        public void setPackagePrice(final String packagePrice) {
            this.packagePrice = packagePrice;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(final String status) {
            this.status = status;
        }

        public String getExpirationDate() {
            return expirationDate;
        }

        public void setExpirationDate(final String expirationDate) {
            this.expirationDate = expirationDate;
        }
    }

}
