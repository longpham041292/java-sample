package asia.cmg.f8.commerce.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import asia.cmg.f8.commerce.entity.BasicUserEntity;
import asia.cmg.f8.commerce.entity.credit.CreditTransactionStatus;
import asia.cmg.f8.commerce.entity.credit.CreditTransactionType;
import asia.cmg.f8.commerce.entity.credit.CreditWalletTransactionEntity;

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
	public TransactionReportDto() {
		super();
	}

	public TransactionReportDto(CreditWalletTransactionEntity transaction, BasicUserEntity client) {

		this.id = transaction.getId();
		this.creditWalletId = transaction.getCreditWalletId();
		this.partnerUuid = "";
		this.partnerName = transaction.getDescriptionParams();
		this.partnerUsername = "";
		this.ownerUuid = transaction.getOwnerUuid();
		this.clientUuid = client.getUuid();
		this.clientUsername = client.getUserName();
		this.clientName = client.getFullName();
		this.creditAmount = transaction.getCreditAmount();
		this.transactionType = transaction.getTransactionType().toString();
		this.transactionStatus = transaction.getTransactionStatus().toString();
		this.descriptionParams = this.parseDescriptionParams(transaction.getDescriptionParams());
		this.transactionDate = this.parserDateTime(transaction.getCreatedDate());
		this.bookingId = transaction.getBookingId();
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

}
