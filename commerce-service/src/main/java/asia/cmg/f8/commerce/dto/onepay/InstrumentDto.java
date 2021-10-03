package asia.cmg.f8.commerce.dto.onepay;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InstrumentDto {

	@JsonProperty(value = "id")
	private String instrumentId;

	@JsonProperty(value = "user_id")
	private String userId;

	@JsonProperty(value = "type")
	private String type;

	@JsonProperty(value = "name")
	private String name;

	@JsonProperty(value = "number")
	private String number;

	@JsonProperty(value = "number_hash")
	private String numberHash;

	@JsonProperty(value = "month")
	private String month;

	@JsonProperty(value = "year")
	private String year;

	@JsonProperty(value = "state")
	private String state;

	@JsonProperty(value = "transaction")
	private TransactionDto transaction;

	@JsonProperty(value = "create_token")
	private Boolean createToken;

	@JsonProperty(value = "token")
	private TokenDto token;

	@JsonProperty(value = "ref_id")
	private String refId;

	@JsonProperty(value = "issuer")
	private IssuerDto issuer;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public TransactionDto getTransaction() {
		return transaction;
	}

	public void setTransaction(TransactionDto transaction) {
		this.transaction = transaction;
	}

	public Boolean getCreateToken() {
		return createToken;
	}

	public void setCreateToken(Boolean createToken) {
		this.createToken = createToken;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getNumberHash() {
		return numberHash;
	}

	public void setNumberHash(String numberHash) {
		this.numberHash = numberHash;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public TokenDto getToken() {
		return token;
	}

	public void setToken(TokenDto token) {
		this.token = token;
	}

	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}

	public IssuerDto getIssuer() {
		return issuer;
	}

	public void setIssuer(IssuerDto issuer) {
		this.issuer = issuer;
	}

	public String getInstrumentId() {
		return instrumentId;
	}

	public void setInstrumentId(String instrumentId) {
		this.instrumentId = instrumentId;
	}

}
