package asia.cmg.f8.report.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import asia.cmg.f8.report.entity.database.BasicUserEntity;
import asia.cmg.f8.report.entity.database.CreditBookingEntity;
import asia.cmg.f8.report.entity.database.CreditClassBookingEntity;
import asia.cmg.f8.report.entity.database.CreditWalletTransactionEntity;

public class TransactionReportDto {
	private Long id;

	@JsonProperty("credit_wallet_id")
	private Long creditWalletId;

	@JsonProperty("owner_uuid")
	private String ownerUuid;

	@JsonProperty("client_uuid")
	private String clientUuid;

	@JsonProperty("client_username")
	private String clientUsername;

	@JsonProperty("client_name")
	private String clientName;

	@JsonProperty("partner_uuid")
	private String partnerUuid;

	@JsonProperty("partner_name")
	private String partnerName;

	@JsonProperty("partner_username")
	private String partnerUsername;

	@JsonProperty("credit_amount")
	private Integer creditAmount;

	@JsonProperty("transaction_type")
	private String transactionType;

	@JsonProperty("transaction_status")
	private String transactionStatus;

	@JsonProperty("description_params")
	private List<String> descriptionParams;

	@JsonProperty("transaction_date")
	private String transactionDate;

	@JsonProperty("booking_id")
	private Long bookingId;

	@JsonProperty("studio_name")
	private String studioName;

	@JsonProperty("studio_address")
	private String studioAddress;

	@JsonProperty("class_service_name")
	private String classServiceName;

	@JsonProperty("booking_date")
	private String bookingDate;

	public TransactionReportDto() {
		super();
	}

	public TransactionReportDto(CreditWalletTransactionEntity transaction, BasicUserEntity ownerUser,
			CreditBookingEntity creditBooking, BasicUserEntity clientUser) {

		this.id = transaction.getId();
		this.creditWalletId = transaction.getCreditWalletId();
		this.partnerUuid = "";
		this.partnerName = transaction.getDescriptionParams();
		this.partnerUsername = "";
		this.ownerUuid = transaction.getOwnerUuid();
		if (ownerUser != null) {
			this.clientUuid = ownerUser.getUuid();
			this.clientUsername = ownerUser.getUserName();
			this.clientName = ownerUser.getFullName();
		}
		this.creditAmount = transaction.getCreditAmount();
		this.transactionType = transaction.getTransactionType().toString();
		this.transactionStatus = transaction.getTransactionStatus().toString();
		this.descriptionParams = this.parseDescriptionParams(transaction.getDescriptionParams());
		this.transactionDate = this.parserDateTime(transaction.getCreatedDate());
		this.bookingId = transaction.getBookingId();

		if (creditBooking == null)
			return;
		this.bookingDate = this.parserDateTime(creditBooking.getCreatedDate());
		switch (creditBooking.getBookingType()) {
		case ETICKET:
			this.partnerName = creditBooking.getStudioName();
			this.studioName = creditBooking.getStudioName();
			this.studioAddress = creditBooking.getStudioAddress();
			this.descriptionParams = Arrays.asList(creditBooking.getStudioName());
			if (clientUser != null) {
				this.clientUuid = clientUser.getUuid();
				this.clientUsername = clientUser.getUserName();
				this.clientName = clientUser.getFullName();
			}
			break;
		case CLASS:
			this.partnerName = creditBooking.getStudioName();
			this.studioName = creditBooking.getStudioName();
			this.studioAddress = creditBooking.getStudioAddress();
			this.descriptionParams = Arrays.asList(creditBooking.getStudioName());
			if (clientUser != null) {
				this.clientUuid = clientUser.getUuid();
				this.clientUsername = clientUser.getUserName();
				this.clientName = clientUser.getFullName();
			}
			List<CreditClassBookingEntity> classBooking = creditBooking.getClasses();
			if (classBooking.size() > 0) {
				this.classServiceName = creditBooking.getClasses().get(0).getServiceName();
			}
			break;
		case SESSION:
			this.partnerName = transaction.getDescriptionParams();
			this.studioName = creditBooking.getStudioName();
			this.studioAddress = creditBooking.getStudioAddress();
			this.descriptionParams = Arrays.asList(transaction.getDescriptionParams());
			break;
		default:
			break;
		}
	}

	private String parserDateTime(LocalDateTime datetime) {
		final String DATE_PATTERN = "dd/MM/yyyy hh:mm:ss a";
		final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
		return datetime == null ? "" : DATE_FORMATTER.format(datetime);
	}

	private List<String> parseDescriptionParams(String textSplitBySemicolon) {
		List<String> result = new ArrayList<String>();

		if (!StringUtils.isEmpty(textSplitBySemicolon)) {
			String[] array = textSplitBySemicolon.split(";");
			result = Arrays.asList(array);
		}

		return result;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCreditWalletId() {
		return creditWalletId;
	}

	public void setCreditWalletId(Long creditWalletId) {
		this.creditWalletId = creditWalletId;
	}

	public String getOwnerUuid() {
		return ownerUuid;
	}

	public void setOwnerUuid(String ownerUuid) {
		this.ownerUuid = ownerUuid;
	}

	public String getClientUuid() {
		return clientUuid;
	}

	public void setClientUuid(String clientUuid) {
		this.clientUuid = clientUuid;
	}

	public String getClientUsername() {
		return clientUsername;
	}

	public void setClientUsername(String clientUsername) {
		this.clientUsername = clientUsername;
	}

	public String getPartnerUuid() {
		return partnerUuid;
	}

	public void setPartnerUuid(String partnerUuid) {
		this.partnerUuid = partnerUuid;
	}

	public String getPartnerName() {
		return partnerName;
	}

	public void setPartnerName(String partnerName) {
		this.partnerName = partnerName;
	}

	public String getPartnerUsername() {
		return partnerUsername;
	}

	public void setPartnerUsername(String partnerUsername) {
		this.partnerUsername = partnerUsername;
	}

	public Integer getCreditAmount() {
		return creditAmount;
	}

	public void setCreditAmount(Integer creditAmount) {
		this.creditAmount = creditAmount;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public List<String> getDescriptionParams() {
		return descriptionParams;
	}

	public void setDescriptionParams(List<String> descriptionParams) {
		this.descriptionParams = descriptionParams;
	}

	public String getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}

	public Long getBookingId() {
		return bookingId;
	}

	public void setBookingId(Long bookingId) {
		this.bookingId = bookingId;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getStudioName() {
		return studioName;
	}

	public void setStudioName(String studioName) {
		this.studioName = studioName;
	}

	public String getStudioAddress() {
		return studioAddress;
	}

	public void setStudioAddress(String studioAddress) {
		this.studioAddress = studioAddress;
	}

	public String getClassServiceName() {
		return classServiceName;
	}

	public void setClassServiceName(String classServiceName) {
		this.classServiceName = classServiceName;
	}

	public String getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(String bookingDate) {
		this.bookingDate = bookingDate;
	}

}
