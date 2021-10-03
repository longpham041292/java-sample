package asia.cmg.f8.user.entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created on 11/28/16.
 */
@Entity
@Table(name = "session_users")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class UserInfoEntity extends AbstractEntity {

	
    @Column(name = "user_type", length = 5)
    private String userType;
    
    @Column(name = "on_board_completed")
    private String onBoardCompleted = "false";
    
    @Column(name = "picture")
    private String picture;
    
    @Column(name = "document_approval_status")
    private String documentApprovalStatus;
    
    @Column(name = "last_question_sequence", nullable = false)
    private String lastQuestionSequence = "0";
    
    @Column(name = "extend_user_type", length = 50)
    @JsonProperty("extend_user_type")
    private String extendUserType;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "language")
    private String language;
    
    @Column(name = "permissions")
    private String permissions;

    


	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String isOnBoardCompleted() {
		return onBoardCompleted;
	}

	public void setOnBoardCompleted(String onBoardCompleted) {
		this.onBoardCompleted = onBoardCompleted;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getDocumentApprovalStatus() {
		return documentApprovalStatus;
	}

	public void setDocumentApprovalStatus(String documentApprovalStatus) {
		this.documentApprovalStatus = documentApprovalStatus;
	}

	public String getLastQuestionSequence() {
		return lastQuestionSequence;
	}

	public void setLastQuestionSequence(String lastQuestionSequence) {
		this.lastQuestionSequence = lastQuestionSequence;
	}

	public String getExtendUserType() {
		return extendUserType;
	}

	public void setExtendUserType(String extendUserType) {
		this.extendUserType = extendUserType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getPermissions() {
		return permissions;
	}

	public void setPermissions(String permissions) {
		this.permissions = permissions;
	}
    
    
}
