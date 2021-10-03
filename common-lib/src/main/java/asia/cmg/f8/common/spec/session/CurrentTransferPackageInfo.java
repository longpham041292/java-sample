/**
 * 
 */
package asia.cmg.f8.common.spec.session;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author khoa.bui
 *
 */
public class CurrentTransferPackageInfo extends TransferSessionPackageInfo {

	@JsonProperty("modified_date")
    private Long modifiedDate;
    
    @JsonProperty("created_date")
    private Long createdDate;
    
    public Long getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(final Long modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public Long getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(final Long createdDate) {
		this.createdDate = createdDate;
	}
}
