package asia.cmg.f8.session.dto;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 
 * @author khoa.bui
 *
 */
@Value.Immutable
@Value.Style(typeImmutable = "*")
@JsonSerialize(as = asia.cmg.f8.session.dto.ContractUploadInfo.class)
@JsonDeserialize(builder = asia.cmg.f8.session.dto.ContractUploadInfo.Builder.class)
@SuppressWarnings({ "CheckReturnValue", "PMD" })
public abstract class AbstractContractUploadInfo {

	@JsonProperty("contract_number")
	public abstract String getContractNumber();
	
	@JsonProperty("member_barcode")
	public abstract String getMemberBarCode();
	
	@JsonProperty("trainer_code")
	public abstract String getTrainerCode();
	
	@JsonProperty("pt_uuid")
	@Nullable
	public abstract String getPtUuid();

	@JsonProperty("eu_uuid")
	@Nullable
	public abstract String getEuUuid();
}
